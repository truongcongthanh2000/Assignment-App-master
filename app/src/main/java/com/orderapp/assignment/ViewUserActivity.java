package com.orderapp.assignment;
import android.content.Intent;
import android.os.Build;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orderapp.assignment.Adapter.UserViewHolder;
import com.orderapp.assignment.Interface.ItemClickListener;
import com.orderapp.assignment.Model.Common;
import com.orderapp.assignment.Model.User;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class ViewUserActivity extends AppCompatActivity {

    DatabaseReference database;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<User, UserViewHolder> adapter;
    ImageView home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_view_user);
        //set color status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        recyclerView = findViewById(R.id.recycler_view_user);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        database = FirebaseDatabase.getInstance().getReference("Users");
        home = findViewById(R.id.home_view_user);

        loadListUser();

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewUserActivity.this, AdminActivity.class));
            }
        });
    }


    //Hiển thị danh sách user
    private void loadListUser() {
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(database, User.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_user, parent, false);
                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(UserViewHolder viewHolder, int i, User model) {
                viewHolder.name.setText(model.getName());
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent info = new Intent(ViewUserActivity.this, InfoPersonActivity.class);
                        info.putExtra("userID", adapter.getRef(position).getKey());//gửi UserID đến activity mới
                        startActivity(info);
                    }
                });
            }

        };
        // Thiết lập adapter
        recyclerView.setAdapter(adapter);

        Common.runAnimation(recyclerView);

    }
}
