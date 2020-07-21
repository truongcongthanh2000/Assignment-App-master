package com.orderapp.assignment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.orderapp.assignment.Adapter.ViewFoodAdapter;
import com.orderapp.assignment.Model.Food;
import com.orderapp.assignment.Model.Banner;
import com.orderapp.assignment.Model.Common;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

import dmax.dialog.SpotsDialog;


import java.util.ArrayList;

public class ViewListFoodActivity extends AppCompatActivity {
    ImageView back;
    TextView tenquan;
    RecyclerView recyclerViewFood;
    ArrayList<Food> arrFood = new ArrayList<>();
    ViewFoodAdapter viewFoodAdapter;
    Food food;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_view_food);
        declare();
        initRecyclerView();
        tenquan.setText(user.getDisplayName());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewListFoodActivity.this, RestaurantActivity.class));
            }
        });
    }

    private void declare(){
        back = (ImageView) findViewById(R.id.btnback);
        tenquan =(TextView) findViewById(R.id.tvtenQuanLayoutXemDSMon);
    }




    private void initRecyclerView(){
        recyclerViewFood = (RecyclerView) findViewById(R.id.recycler_view_food);

        recyclerViewFood.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerViewFood.setLayoutManager(layoutManager);
        recyclerViewFood.setItemAnimator(new DefaultItemAnimator());

        mDatabase = FirebaseDatabase.getInstance().getReference("restaurants").child(userID);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(dataSnapshot.getValue() != null) {
                        food = ds.getValue(Food.class);
                        arrFood.add(food);
                    }
                }
                viewFoodAdapter = new ViewFoodAdapter(arrFood,getApplicationContext());
                recyclerViewFood.setAdapter(viewFoodAdapter);
                //set animation
                Common.runAnimation(recyclerViewFood);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final int pos = item.getGroupId();
        final String name = arrFood.get(pos).getName();
        switch (item.getItemId()){
            case 121:
                new AlertDialog.Builder(this)
                        .setTitle("Xóa món ăn")
                        .setMessage("Bạn muốn xóa "+name+" ?")
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                displayMessage("Đã xóa thành công " + name);
                                viewFoodAdapter.removeItem(pos,name);
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("Hủy", null)
                        .setIcon(R.drawable.ic_delete_red_24dp)
                        .show();
                return true;
            case 122:
                DialogUpdate(name);
                return true;
            case 123:
                Banner banner = new Banner(name,userID,arrFood.get(pos).getLinkPicture());
                mDatabase = FirebaseDatabase.getInstance().getReference("Banner").child(userID);
                mDatabase.setValue(banner);
                displayMessage("Đã đặt "+ name+" làm Hot Food");
                return true;

                default:
                    return super.onContextItemSelected(item);

        }


    }

    private void displayMessage(String msg){
        Snackbar snackbar = Snackbar.make(findViewById(R.id.view_food), msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void DialogUpdate(String name){
        final Dialog dialog   = new Dialog(ViewListFoodActivity.this,R.style.Theme_Dialog);
        dialog.setContentView(R.layout.dialog_update_food);
        dialog.show();
        final EditText nameFood = (EditText) dialog.findViewById(R.id.updateNameFood);
        final EditText priceFood = (EditText) dialog.findViewById(R.id.updatePriceFood);
        final RadioButton conhang = (RadioButton) dialog.findViewById(R.id.conhang);
        final RadioButton hethang = (RadioButton) dialog.findViewById(R.id.hethang);
        Button update = (Button) dialog.findViewById(R.id.btnUpdateFood);
        Button cancel = (Button) dialog.findViewById(R.id.btnCancelFood);

        mDatabase = FirebaseDatabase.getInstance().getReference("restaurants").child(user.getUid()).child(name);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Food food = dataSnapshot.getValue(Food.class);
                nameFood.setText(food.getName());
                priceFood.setText(food.getPrice()+"");
                if(food.getStatus() == 1){
                    conhang.setChecked(true);
                }
                else{
                    hethang.setChecked(true);
                }
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
                final String Name = nameFood.getText().toString();
                final String Price = priceFood.getText().toString();
                if(Name.isEmpty() || Price.isEmpty() ){
                    Toast.makeText(ViewListFoodActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
                else{
                    dialog.cancel();
                    mDatabase.child("name").setValue(Name);
                    mDatabase.child("price").setValue(Long.parseLong(Price));
                    if(conhang.isChecked()) mDatabase.child("status").setValue(1);
                    else if(hethang.isChecked()) mDatabase.child("status").setValue(0);
                    // Reload Activity
                    finish();
                    startActivity(getIntent());
                }
            }
        });
    }
}



