package com.orderapp.assignment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orderapp.assignment.Model.Common;
import com.orderapp.assignment.Notifications.MyResponse;
import com.orderapp.assignment.Notifications.Token;
import com.orderapp.assignment.Model.User;
import com.orderapp.assignment.Notifications.APIService;
import com.orderapp.assignment.Notifications.Notification;
import com.orderapp.assignment.Notifications.Sender;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    EditText email,pass,name,phone;
    private FirebaseAuth mAuthentication;
    private DatabaseReference mData;
    FirebaseUser user;
    AlertDialog process;
    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);

        mService = Common.getFCMService();

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

                                    sendNotification(Name);
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

    private void sendNotification(final String name) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("checkToken").equalTo(3); // get node isServerToken is 3
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Notification notification = new Notification(name+" vừa tạo tài khoản trên ứng dụng", "Có tài khoản mới");
                    Sender content = new Sender(token.getToken(), notification);

                    mService.sendNotification(content).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200) {
                                if (response.body().success == 1) {
                                }
                                else {
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            Log.e("Error", t.getMessage());
                        }
                    });
                    break;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
