package com.orderapp.assignment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orderapp.assignment.Model.User;

import dmax.dialog.SpotsDialog;

public class RemoveRestaurantActivity extends AppCompatActivity {
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
                waiting.show();
                deleteRestaurant();
            }
        });

    }

    private void declare(){
        delRes =   (Button) findViewById(R.id.btnDel_res);
        emailDel =   (EditText) findViewById(R.id.edtEmailDel_res);
    }

    private void deleteRestaurant() {
        final String Email = emailDel.getText().toString().trim();

        // lay data của realtime bd từ email
        mData = FirebaseDatabase.getInstance().getReference().child("users");
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.getValue() != null){
                        User user = ds.getValue(User.class);
                        if(user.getEmail().equals(Email)){
                            userID = ds.getKey();
                            database = FirebaseDatabase.getInstance().getReference().child("restaurant");
                            database.child(userID).setValue(null);
                            waiting.dismiss();
                            alertDisplayer("Succesfully!");
                            startActivity(new Intent(RemoveRestaurantActivity.this,AdminActivity.class));
                            return;
                        }
                    }
                }
                waiting.dismiss();
                alertDisplayer("Restaurant doesn't exit");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void alertDisplayer(String message){
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
