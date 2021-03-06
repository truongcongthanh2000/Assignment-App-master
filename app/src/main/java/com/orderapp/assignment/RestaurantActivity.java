package com.orderapp.assignment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orderapp.assignment.Model.User;

public class RestaurantActivity extends AppCompatActivity {
    Button addFood, showListFood, showTableOfOrder, changePassword,update;
    ImageView LogOut;
    TextView restaurantName;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_restaurant);


        declare();

        restaurantName.setText(user.getDisplayName());
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantActivity.this,ChangePassActivity.class));
            }
        });
        addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantActivity.this, AddFoodActivity.class));
            }
        });

        showListFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantActivity.this, ViewListFoodActivity.class));
            }
        });

        showTableOfOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantActivity.this, RestaurantViewOrderActivity.class));
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogUpdate();
            }
        });


    }


    private void declare(){
        LogOut=(ImageView) findViewById(R.id.btnLogOutRestaurant);
        changePassword =(Button) findViewById(R.id.btnChangePassRestaurant);
        restaurantName =(TextView) findViewById(R.id.edt_restaurantName);
        addFood =(Button) findViewById(R.id.btnAddFood);
        showListFood =(Button) findViewById(R.id.btnListFood);
        showTableOfOrder = (Button) findViewById(R.id.btnTableOfOrder);
        update= findViewById(R.id.btnUpdateInfoRestaurant);
    }

    private void LogOut(){
        final Dialog dialogLogOut = new Dialog(RestaurantActivity.this,R.style.Theme_Dialog);
        dialogLogOut.setContentView(R.layout.dialog_logout);
        dialogLogOut.show();
        Button no =(Button) dialogLogOut.findViewById(R.id.btnNo_logout);
        Button yes =(Button) dialogLogOut.findViewById((R.id.btnYes_logout));
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLogOut.cancel();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLogOut.cancel();
                startActivity(new Intent(RestaurantActivity.this, WelcomeActivity.class));
            }
        });
    }


    private void showDialogUpdate(){
        final Dialog dialog   = new Dialog(RestaurantActivity.this,R.style.Theme_Dialog);
        dialog.setContentView(R.layout.dialog_update_info);
        dialog.show();
        final EditText name = (EditText) dialog.findViewById(R.id.updateName);
        final EditText phone = (EditText) dialog.findViewById(R.id.updatePhone);
        Button update = (Button) dialog.findViewById(R.id.btnUpdate);
        Button cancel = (Button) dialog.findViewById(R.id.btnCancel);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User uuser = dataSnapshot.getValue(User.class);
                name.setText(uuser.getName());
                phone.setText(uuser.getPhone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Name = name.getText().toString();
                final String Phone = phone.getText().toString();
                if(Name.isEmpty() || Phone.isEmpty()){
                    alertDisplayer("Name or Phone cannot be empty", "Please try again.");
                    //Toast.makeText(InfoPersonActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
                else{
                    dialog.cancel();
                    alertDisplayer_only("Update Successfully");
                    //alertDisplayer("An error has occurred!", "Please try again.");
                    //Toast.makeText(InfoPersonActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(Name)
                            .setPhotoUri(null)
                            .build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        restaurantName.setText(user.getDisplayName());
                                        mDatabase.child("name").setValue(Name);
                                        mDatabase.child("phone").setValue(Phone);
                                    }
                                }
                            });
                }
            }
        });
    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(RestaurantActivity.this)
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
        AlertDialog.Builder builder = new AlertDialog.Builder(RestaurantActivity.this)
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
