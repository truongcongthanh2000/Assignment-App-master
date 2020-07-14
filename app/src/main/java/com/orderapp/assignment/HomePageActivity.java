package com.orderapp.assignment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.andremion.counterfab.CounterFab;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orderapp.assignment.Adapter.FoodAdapter;
import com.orderapp.assignment.Adapter.UserViewHolder;
import com.orderapp.assignment.Model.Banner;
import com.orderapp.assignment.Model.Favorite;
import com.orderapp.assignment.Model.Food;
import com.orderapp.assignment.Model.Order;
import com.orderapp.assignment.Model.User;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomePageActivity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();
    DatabaseReference mDatabase;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    DatabaseReference database;
    ImageView homepage, cart, user_View;
    FirebaseRecyclerAdapter<User, UserViewHolder> adapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_homepage);
        homepage = findViewById(R.id.homepage_view);
        cart = findViewById(R.id.cart_view);
        user_View = findViewById(R.id.user_view);
        database = FirebaseDatabase.getInstance().getReference("users");

//        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(User.class,
//                R.layout.item_user,
//                UserViewHolder.class,
//                database)
//        {
            user_View.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){
                    startActivity(new Intent(HomePageActivity.this, InfoPersonActivity.class));
//                    Intent info = new Intent(HomePageActivity.this, InfoPersonActivity.class);
//                    info.putExtra("userID", userID); //gửi UserID đến activity mới
//                    startActivity(info);
                }
            });
       // }
    }
}
