package com.orderapp.assignment
;
import android.app.AlertDialog;
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

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.orderapp.assignment.Model.Food;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

import dmax.dialog.SpotsDialog;

public class AddFoodActivity extends AppCompatActivity {
    private Button addFood,folder;
    private EditText nameFood, priceFood;
    private ImageView image;
    private int REQUEST_CODE_FOLDER = 123;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private String link ;
    AlertDialog waiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_food);
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://order-3bdf0.appspot.com");
        declare();
        waiting =  new SpotsDialog.Builder().setContext(this).setMessage("Waiting...").setCancelable(false).build();

        folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE_FOLDER);
            }
        });

        addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waiting.show();
                final String nameFood = AddFoodActivity.this.nameFood.getText().toString().trim();
                final String str_foodPrice = priceFood.getText().toString().trim();
                if(nameFood.isEmpty() || str_foodPrice.isEmpty()){
                    waiting.dismiss();
                    alertDisplayer("Enter empty place", "Please try agian");
//                    Toast.makeText(AddFoodActivity.this, "Vui lòng nhập đầy đủ thông tin ", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (!isNumeric(str_foodPrice)) {
                        alertDisplayer("Wrong format numeric", "Please try agian");
                    }
                    else {
                        final long foodPrice = Long.parseLong(str_foodPrice);
                        Calendar calendar = Calendar.getInstance();
                        final String name_image = "image" + calendar.getTimeInMillis();
                        final StorageReference mountainsRef = storageRef.child(name_image + ".png");
                        image.setDrawingCacheEnabled(true);
                        image.buildDrawingCache();

                        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] data = baos.toByteArray();

                        final UploadTask uploadTask = mountainsRef.putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Log.d("AddFoodActivity", "unsuccessful uploads");
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
                                        link = mountainsRef.getDownloadUrl().toString();
                                        // Continue with the task to get the download URL
                                        return mountainsRef.getDownloadUrl();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            waiting.dismiss();
                                            link = task.getResult().toString();
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            String IDQuan = user.getUid();
                                            Food food = new Food(nameFood, user.getDisplayName(), link, IDQuan, foodPrice, 1);
                                            DatabaseReference refData = FirebaseDatabase.getInstance().getReference();
                                            refData.child("restaurants").child(IDQuan).child(nameFood).setValue(food);
                                            alertDisplayer_only("Add food succesfull");
                                            //Toast.makeText(AddFoodActivity.this, "Thêm món ăn thành công", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(AddFoodActivity.this, RestaurantActivity.class));

                                        } else {
                                            alertDisplayer("Add food unsuccesful", "Please try agian");
                                            //Toast.makeText(AddFoodActivity.this, "Thêm không thành công", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }

            }
        });


    }


    // chọn ảnh từ file
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_CODE_FOLDER && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                image.setImageBitmap(bitmap);
            } catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            long d = Long.parseLong(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    private void declare(){
        addFood = findViewById(R.id.btnAddFood);
        folder  = findViewById(R.id.btnfolder);
        nameFood = findViewById(R.id.edtFoodName);
        priceFood = findViewById(R.id.edtFoodPrice);
        //image   = findViewById(R.id.ivImage);
    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddFoodActivity.this)
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
        AlertDialog.Builder builder = new AlertDialog.Builder(AddFoodActivity.this)
                .setTitle(message)
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
