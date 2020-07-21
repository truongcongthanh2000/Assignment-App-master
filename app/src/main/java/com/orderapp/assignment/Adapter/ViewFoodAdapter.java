package com.orderapp.assignment.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;
import android.view.ContextMenu;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

import dmax.dialog.SpotsDialog;

import com.orderapp.assignment.R;
import java.util.ArrayList;

public class ViewFoodAdapter extends RecyclerView.Adapter<ViewFoodAdapter.ViewHolder> {
    ArrayList<Food> arrMonAn;
    Context context;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mDatabase;


    public ViewFoodAdapter(ArrayList<Food> arrMonAn, Context context) {
        this.arrMonAn = arrMonAn;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_view_food,viewGroup,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Food monAn = arrMonAn.get(i);
        viewHolder.name.setText(monAn.getName());
        viewHolder.price.setText(monAn.getPrice()+"VNĐ");
        if(monAn.getStatus() == 0){
            viewHolder.status.setText("Hết hàng");
            viewHolder.status.setTextColor(Color.GRAY);
        }
        else {
            viewHolder.status.setText("Còn hàng");
        }
        Picasso.get().load(monAn.getLinkPicture()).into(viewHolder.image);

    }


    @Override
    public int getItemCount() {
        return arrMonAn.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView name , price,status;
        ImageView image;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.item_food_name_view);
            price = (TextView) itemView.findViewById(R.id.item_food_price_view);
            image = (ImageView) itemView.findViewById(R.id.item_food_image_view);
            status = (TextView) itemView.findViewById(R.id.item_food_status_view);
            cardView = (CardView) itemView.findViewById(R.id.cardView_food);
            cardView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Lựa chọn Option");
            menu.add(this.getAdapterPosition(),121,0,"Xóa món ăn");
            menu.add(this.getAdapterPosition(),122,1,"Cập nhật món ăn");
            menu.add(this.getAdapterPosition(),123,2,"Đặt làm Hot Food");
        }
    }

    public void removeItem(int position, String name){
        arrMonAn.remove(position);
        notifyDataSetChanged();
        // remove in database
        mDatabase = FirebaseDatabase.getInstance().getReference("QuanAn").child(user.getUid()).child(name);
        mDatabase.setValue(null);
    }

}
