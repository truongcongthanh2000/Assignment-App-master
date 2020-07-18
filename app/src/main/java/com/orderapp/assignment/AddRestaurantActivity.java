package com.orderapp.assignment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orderapp.assignment.Model.User;

import dmax.dialog.SpotsDialog;

public class AddRestaurantActivity extends AppCompatActivity {
    EditText email,pass,name,phone,address;
    Button btnAdd;
    FirebaseAuth mAuthencation;
    DatabaseReference mData;
    FirebaseUser user;
    AlertDialog waiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_restaurant);
        declare();
        waiting =  new SpotsDialog.Builder().setContext(this).setMessage("Waiting...").setCancelable(false).build();
        mAuthencation = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddingRestaurant();
            }
        });
    }

    private void AddingRestaurant() {
        waiting.show();
        String Email = email.getText().toString().trim();
        String Pass = pass.getText().toString().trim();
        final String Name = name.getText().toString().trim();
        String Phone = phone.getText().toString().trim();
        String Address = address.getText().toString().trim();

        final User restaurant  = new User(Email,Pass,Name,Phone,"staff");

        if (Email.isEmpty() || Pass.isEmpty() || Name.isEmpty() || Phone.isEmpty()) {
            waiting.dismiss();
            alertDisplayer("Enter missing place.");
        }
        else {
            mAuthencation.createUserWithEmailAndPassword(Email, Pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        waiting.dismiss();
                        alertDisplayer("Successfully!");
                        user    =   mAuthencation.getCurrentUser();
                        //set Name for user
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(Name)
                                .setPhotoUri(null)
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("AddRestaurantActivity", "User Profile Updated");
                                        }
                                    }
                                });
                        //push data to realtime database
                        String userID   =   user.getUid();
                        mData.child("users").child(userID).setValue(restaurant);
                        //return Admin
                        startActivity(new Intent(AddRestaurantActivity.this, AdminActivity.class));
                    } else {
                        waiting.dismiss();
                       alertDisplayer("Restaurant has been registered!");
                    }
                }
            });
        }
    }
    private void alertDisplayer(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddRestaurantActivity.this)
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

    private void declare() {
        email   = (EditText) findViewById(R.id.edtEmail_restaurant);
        pass    = (EditText) findViewById(R.id.edtPass_restaurant);
        name    = (EditText) findViewById(R.id.edtName_restaurant);
        phone   = (EditText) findViewById(R.id.edtPhone_restaurant);
        address = (EditText) findViewById(R.id.edtLocation_restaurant);
        btnAdd = ( Button) findViewById(R.id.btnAdd_restaurant);
    }
}
