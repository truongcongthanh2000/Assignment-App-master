package com.orderapp.assignment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class RegisterActivity extends AppCompatActivity {
    EditText email,pass,name,phone;
    private FirebaseAuth mAuthentication;
    private DatabaseReference mData;
    FirebaseUser user;
    AlertDialog process;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);

        declare();
        mAuthentication = FirebaseAuth.getInstance();

        final Button register = (Button) findViewById(R.id.register_btn);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                process.show();
                register();
            }
        });
        final ImageView back_register = (ImageView) findViewById(R.id.back_btn);
        back_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, WelcomeActivity.class));
            }
        });
        mData = FirebaseDatabase.getInstance().getReference();
    }

    private void register() {
        String Email = email.getText().toString().trim();
        String Pass = pass.getText().toString().trim();
        final String Name = name.getText().toString().trim();
        String Phone = phone.getText().toString().trim();

        final User new_cus = new User(Email, Pass, Name, Phone, "customers");
        if (Email.isEmpty() || Pass.isEmpty() || Name.isEmpty() || Phone.isEmpty()) {
            process.dismiss();
            alertDisplayer("Warning","Enter missing places!");
        } else {
            mAuthentication.createUserWithEmailAndPassword(Email, Pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        user = mAuthentication.getCurrentUser();

                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task1) {
                                if (task1.isSuccessful()) {
                                    process.dismiss();
                                    alertDisplayer("Thank you!", "Please check your email to verification.");
                                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(Name)
                                            .setPhotoUri(null)
                                            .build();
                                    user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        }
                                    });

                                    //push data to realtime database
                                    String userID = user.getUid();
                                    mData.child("users").child(userID).setValue(new_cus);
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                } else {
                                    alertDisplayer("An error has occurred!", "Please try again.");
                                }

                            }
                        });
                    } else {
                        process.dismiss();
                        alertDisplayer("This email has been registered!", "Try different email.");
                    }
                }
            });
        }

    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        //startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void declare() {
        email = (EditText) findViewById(R.id.txtEmail);
        pass = (EditText) findViewById(R.id.txtPass);
        name = (EditText) findViewById(R.id.txtName);
        phone = (EditText) findViewById(R.id.txtPhone);

        process =  new SpotsDialog.Builder().setContext(this).setMessage("Almost done").setCancelable(false).build();
    }
}
