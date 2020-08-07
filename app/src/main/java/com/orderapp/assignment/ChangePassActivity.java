package com.orderapp.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.annotation.NonNull;
import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.view.Change;
import com.orderapp.assignment.Model.User;

public class ChangePassActivity extends AppCompatActivity {
    private EditText newPassword, retypeNewPassword , oldPassword;
    private Button submitChangePass, closeChangePass;
    DatabaseReference mDatabase;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();
    private String type, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_change_pass);
        declare();
        changePass();
    }

    private void declare(){
        newPassword = (EditText) findViewById(R.id.edtNewPassword);
        retypeNewPassword = (EditText) findViewById(R.id.edtRetypeNewPassword);
        oldPassword = (EditText) findViewById(R.id.edtOldPassword);
        submitChangePass = (Button) findViewById(R.id.btnSubmitChangePass);
        closeChangePass = (Button) findViewById(R.id.btnCloseChangePass);
    }


    private void changePass(){

        //get data of User ( typeUser and pass )
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User uInfo = dataSnapshot.getValue(User.class);
                type = uInfo.getUserType();
                pass = uInfo.getPass();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addListenerForSingleValueEvent(eventListener);

        submitChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String oldPass = oldPassword.getText().toString().trim();
                final String newPass = newPassword.getText().toString().trim();
                final String retypeNewPass = retypeNewPassword.getText().toString().trim();
                if(oldPass.isEmpty() || newPass.isEmpty() || retypeNewPass.isEmpty()){
                    alertDisplayer("Enter missing place", "Please fill out the form");
                }
                else {
                    if (oldPass.equals(pass) == false) {
                        alertDisplayer("Your old password is not correct", "Please try again");
                    }
                    else {
                        if (newPass.length() < 6) {
                            alertDisplayer("Your new password must contain at least 6 characters", "Please try again");
                        }
                        else {
                            if (newPass.equals(retypeNewPass)) {
                                user = FirebaseAuth.getInstance().getCurrentUser();
                                final String userID = user.getUid();
                                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                user.updatePassword(newPass)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // updata pass in database
                                                    mDatabase.child("users").child(userID).child("pass").setValue(newPass);
                                                    alertDisplayer_only("Password change successful");
                                                    if (type.equals("admin")) {
                                                        startActivity(new Intent(ChangePassActivity.this, AdminActivity.class));
                                                    }
                                                    if (type.equals("customers")) {
                                                        startActivity(new Intent(ChangePassActivity.this, CustomerActivity.class));
                                                    }
                                                    if (type.equals("staff")) {
                                                        startActivity(new Intent(ChangePassActivity.this, RestaurantActivity.class));
                                                    }
                                                } else {
                                                    alertDisplayer("Password change failed", "Please try again");
                                                }
                                            }
                                        });
                            } else {
                                alertDisplayer("Your new password does not match retype new password", "Please try again");
                            }
                        }
                    }
                }
            }
        });

        closeChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("staff")) startActivity(new Intent(ChangePassActivity.this, RestaurantActivity.class));

                if (type.equals("admin")) startActivity(new Intent(ChangePassActivity.this, AdminActivity.class));

                if (type.equals("customers")) startActivity(new Intent(ChangePassActivity.this, CustomerActivity.class));

            }
        });
    }
    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }
    private void alertDisplayer_only(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }
}
