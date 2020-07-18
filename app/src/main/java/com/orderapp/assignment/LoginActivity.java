package com.orderapp.assignment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orderapp.assignment.Model.User;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
   DatabaseReference mData;
   FirebaseAuth mAuthentication;
   Button btnLog;
   EditText email;
   EditText pass;

    TextView forgotPass;

    AlertDialog waiting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_login);

        declare();

        waiting =  new SpotsDialog.Builder().setContext(this).setMessage("Logging in").setCancelable(false).build();
        mAuthentication = FirebaseAuth.getInstance();

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPassActivity.class));
            }
        });

    }

    private void login() {
        final String Email = email.getText().toString().trim();
        final String Pass = pass.getText().toString().trim();
        if (Email.isEmpty() || Pass.isEmpty()) {
            alertDisplayer("Enter missing place");
        }
        else {
            if (isNetworkAvailable()) {
                waiting.show();

                mAuthentication.signInWithEmailAndPassword(Email, Pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
                            String userID = USER.getUid();

                            mData = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
                            mData.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    waiting.dismiss();
                                    User user = snapshot.getValue(User.class);

                                    if (user.getUserType().equals("admin")) {
                                        startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                    } else if (user.getUserType().equals("staff")) {
                                        startActivity(new Intent(LoginActivity.this, StaffActivity.class));
                                    } else if (user.getUserType().equals("customers")){
                                        if (USER.isEmailVerified()) {
                                            startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
                                        }
                                        else {
                                            alertDisplayer("Verify email to log in");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
                            ValueEventListener eventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    dataSnapshot.child("pass").getRef().setValue(Pass);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                            mDatabase.addListenerForSingleValueEvent(eventListener);

                        } else {
                            waiting.dismiss();
                            alertDisplayer("Wrong email or password");
                        }
                    }
                });
            }
            else {
                alertDisplayer("Network is not available");
            }
        }
    }
    private void alertDisplayer(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void declare() {
        btnLog=(Button) findViewById(R.id.login_btn);
        email= (EditText) findViewById(R.id.email_log);
        pass=(EditText) findViewById((R.id.password_log));
        forgotPass = (TextView) findViewById(R.id.forgotPass);
    }

}
