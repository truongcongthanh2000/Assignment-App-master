package com.orderapp.assignment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


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
import com.squareup.picasso.Picasso;
import com.orderapp.assignment.Model.Food;
import com.orderapp.assignment.Model.User;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class InfoPersonActivity extends AppCompatActivity {
    Button updateInfo, LogOutUser, ChangePassUser;
    CircleImageView image;
    ImageView change_image;
    TextView ten, tenTK, phone_number;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();
    DatabaseReference mDatabase;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://order-3bdf0.appspot.com");
    int REQUEST_CODE_FOLDER = 123;
    String link_image;
    AlertDialog waiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_info_person);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        declare();
        LoadData();

        updateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogUpdate();
            }

        });

        LogOutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });
        change_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_FOLDER);
            }
        });
        ChangePassUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InfoPersonActivity.this, ChangePassActivity.class));
            }
        });

    }

    private void LogOut(){
        final Dialog dialogLogOut = new Dialog(InfoPersonActivity.this,R.style.Theme_Dialog);
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
                startActivity(new Intent(InfoPersonActivity.this,WelcomeActivity.class));
            }
        });
    }
    // get image from folder
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_CODE_FOLDER && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                waiting.show();
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                image.setImageBitmap(bitmap);

                Calendar calendar = Calendar.getInstance();
                String image_name = "image" + calendar.getTimeInMillis();
                final StorageReference mountainsRef = storageRef.child(image_name + ".png");
                image.setDrawingCacheEnabled(true);
                image.buildDrawingCache();

                Bitmap bitmap1 = ((BitmapDrawable) image.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap1.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data1 = baos.toByteArray();

                final UploadTask uploadTask = mountainsRef.putBytes(data1);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // get downloadUrl
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                link_image = mountainsRef.getDownloadUrl().toString();
                                // Continue with the task to get the download URL
                                return mountainsRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    link_image = task.getResult().toString();
                                    mDatabase.child("image").setValue(link_image);
                                    Toast.makeText(InfoPersonActivity.this, "Thêm ảnh thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(InfoPersonActivity.this, "Thêm không thành công", Toast.LENGTH_SHORT).show();
                                }
                                waiting.dismiss();
                            }
                        });
                    }
                });


            } catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showDialogUpdate(){
        final Dialog dialog   = new Dialog(InfoPersonActivity.this,R.style.Theme_Dialog);
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
                }
            }
        });
    }

    private void declare() {
        updateInfo  = findViewById(R.id.btnUpdateInfo);
        LogOutUser = findViewById(R.id.btnLogOutUser);
        ChangePassUser = findViewById(R.id.btnChangePassUser);
        ten         = findViewById(R.id.tvtenKhachHang);
        tenTK       = findViewById(R.id.tvtentaikhoan);
        phone_number          = findViewById(R.id.tvsdtkhachhang);
        image       = findViewById(R.id.image_person);
        change_image = findViewById(R.id.change_image_person);
        waiting =  new SpotsDialog.Builder().setContext(this).setMessage("Đang upload...").setCancelable(false).build();
    }

    private void LoadData(){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User uInfo = dataSnapshot.getValue(User.class);
                ten.setText( uInfo.getName());
                tenTK.setText(uInfo.getEmail());
                phone_number.setText(uInfo.getPhone());
                if(uInfo.getImage() != null){
                    Picasso.get().load(uInfo.getImage()).into(image);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(InfoPersonActivity.this)
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
        AlertDialog.Builder builder = new AlertDialog.Builder(InfoPersonActivity.this)
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
