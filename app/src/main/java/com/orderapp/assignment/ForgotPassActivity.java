package com.orderapp.assignment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import info.hoang8f.widget.FButton;

public class ForgotPassActivity extends AppCompatActivity {
    EditText email;
    FButton btnGetPass;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_forgot_pass);

        declare();
        firebaseAuth = FirebaseAuth.getInstance();
        btnGetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString().trim();
                if(Email.isEmpty()) {
                    alertDisplayer("Email cannot be empty", "Please try agian");
                }
                else {

                    firebaseAuth.sendPasswordResetEmail(Email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        alertDisplayer("Successful!", "Please access your email to confirm and change your password");
                                        startActivity(new Intent(ForgotPassActivity.this, LoginActivity.class));
                                    } else {
                                        alertDisplayer("Email doesn't exist", "Please try again");
                                    }
                                }
                            });
                }
            }
        });
    }

    private void declare(){
        email = (EditText) findViewById(R.id.edt_EmailForgotPass);
        btnGetPass = (FButton) findViewById(R.id.btn_RetrievePassword);
    }
    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassActivity.this)
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassActivity.this)
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
