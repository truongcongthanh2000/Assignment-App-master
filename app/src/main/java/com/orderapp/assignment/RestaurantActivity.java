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
import android.widget.Toast;

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
import com.google.firebase.iid.FirebaseInstanceId;
import com.orderapp.assignment.Model.User;
//import com.orderapp.assignment.Notifications.Token;

import io.paperdb.Paper;

public class RestaurantActivity extends AppCompatActivity {
    Button themMon,xemDSMon, xemDonDatHang,doiMK,update;
    ImageView LogOut;
    TextView tenQuan;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_restaurant);

        ///updateToken(FirebaseInstanceId.getInstance().getToken());

//        // Paper init
//        Paper.init(this);


        AnhXa();
        tenQuan.setText(user.getDisplayName());
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        });
        doiMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantActivity.this,ChangePassActivity.class));
            }
        });
        themMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantActivity.this, AddFoodActivity.class));
            }
        });

//        xemDSMon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(RestaurantActivity.this, ViewListFoodActivity.class));
//            }
//        });

//        xemDonDatHang.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(RestaurantActivity.this, RestaurantViewOrderActivity.class));
//            }
//        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogUpdate();
            }
        });


    }


    private void AnhXa(){
        LogOut=(ImageView) findViewById(R.id.btnLogOutQuan);
        doiMK=(Button) findViewById(R.id.btnChangePassRestaurant);
        tenQuan=(TextView) findViewById(R.id.twTenQuan);
        themMon=(Button) findViewById(R.id.btnThemMon);
        xemDSMon=(Button) findViewById(R.id.btnXemMon);
        xemDonDatHang= (Button) findViewById(R.id.btnXemDonHang);
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
                //delete remember user and password
                //Paper.book().destroy();
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

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getName());
                phone.setText(user.getPhone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                alertDisplayer_only("Update Successfully");
                //alertDisplayer("An error has occurred!", "Please try again.");
                //Toast.makeText(InfoPersonActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                dialog.cancel();
                mDatabase.child("name").setValue(Name);
                mDatabase.child("phone").setValue(Phone);
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
                tenQuan.setText(user.getDisplayName());
            }
            }
        });
    }

//    private void updateToken(String token){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
//        Token token1 = new Token(token,2);
//        reference.child(user.getUid()).setValue(token1);
//    }
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
