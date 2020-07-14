package com.orderapp.assignment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import io.paperdb.Paper;

public class AdminActivity extends AppCompatActivity {
    private Button addRes,delRes;
    ImageView logOut,changePass;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_admin);

        addRes      =(Button) findViewById(R.id.add_res_admin);
        delRes      =(Button) findViewById(R.id.del_res_admin);
        logOut      =(ImageView) findViewById(R.id.btnLogOutAdmin);
        changePass  =(ImageView) findViewById(R.id.btnChangePassAdmin);

        addRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent screenAddRestaurant = new Intent(AdminActivity.this, AddRestaurantActivity.class);
                startActivity(screenAddRestaurant);
            }
        });
        delRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent screenDeleteRestaurant = new Intent(AdminActivity.this, RemoveRestaurantActivity.class);
                startActivity(screenDeleteRestaurant);
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout();
            }
        });
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this,ChangePassActivity.class));
            }
        });
    }


    private void Logout(){
        final Dialog dialogLogOut = new Dialog(AdminActivity.this,R.style.Theme_Dialog);
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
                startActivity(new Intent(AdminActivity.this,WelcomeActivity.class));
            }
        });
    }

}
