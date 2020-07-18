package com.orderapp.assignment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orderapp.assignment.Model.User;

import dmax.dialog.SpotsDialog;

public class RemoveRestaurantActivity extends AppCompatActivity {
    public String TAG = "RemoveRestaurantAcitivity";
    DatabaseReference mData, database;
    Button delRes;
    EditText emailDel;
    String passDel;
    String userID = "";
    AlertDialog waiting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_remove_restaurant);
        declare();
        waiting =  new SpotsDialog.Builder().setContext(this).setMessage("Waiting...").setCancelable(false).build();
        delRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRestaurant();
            }
        });

    }

    private void declare(){
        //Log.d(TAG, "Test Declare");
        emailDel =   (EditText) findViewById(R.id.edtEmailDel_restaurant);
        delRes =   (Button) findViewById(R.id.btnDel_restaurant);
        //Log.d(TAG, emailDel.getText().toString().trim());
    }

    private void deleteRestaurant() {
        waiting.show();
        final String Email = emailDel.getText().toString().trim();
        if (Email.isEmpty()) {
            waiting.dismiss();
            alertDisplayer("Email cannot be empty", "Please try agian");
        }
        else {
            Log.d("Test Email", Email);
            // lay data của realtime bd từ email
            //waiting.dismiss();
            mData = FirebaseDatabase.getInstance().getReference().child("users");
            Log.d("Test DataChange", "First");
            ValueEventListener RemoveRestaurantListerner = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //waiting.dismiss();
                    int time = 0;
                    Log.d("Test DataChange", Integer.toString(time));
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.getValue() != null) {
                            //Log.d("Test DataChange", Integer.toString(time));
                            User user = ds.getValue(User.class);
                            if (user.getEmail().equals(Email)) {
                                userID = ds.getKey();
                                Log.d("Test ID", userID);
                                database = FirebaseDatabase.getInstance().getReference().child("restaurants");
                                database.child(userID).removeValue();
                                waiting.dismiss();
                                alertDisplayer_only("Delete restaurant succesful");
                                startActivity(new Intent(RemoveRestaurantActivity.this, AdminActivity.class));
                                return;
                            }
                        }
                    }
                    waiting.dismiss();
                    alertDisplayer("Restaurant doesn't exist", "Please try again");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "Remove Restaurant", databaseError.toException());
                }
            };
            mData.addValueEventListener(RemoveRestaurantListerner);
            Log.d("Test DataChange", "End");
        }
    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(RemoveRestaurantActivity.this)
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
    private void alertDisplayer_only(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(RemoveRestaurantActivity.this)
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
