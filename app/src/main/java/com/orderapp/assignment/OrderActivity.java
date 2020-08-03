package com.orderapp.assignment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andremion.counterfab.CounterFab;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orderapp.assignment.Adapter.FoodAdapter;
import com.orderapp.assignment.Adapter.OrderAdapter;
import com.orderapp.assignment.Model.Banner;
import com.orderapp.assignment.Model.Common;
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

public class OrderActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    CoordinatorLayout coordinatorLayout;
    ImageView home;

    DatabaseReference database;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_order);

        home = findViewById(R.id.home_order);
        //set color status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderActivity.this, CustomerActivity.class));
            }
        });

        initRecyclerView();
    }
    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_order);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.layoutOrder);


        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        final ArrayList<Order> arrOrder = new ArrayList<>();


        database = FirebaseDatabase.getInstance().getReference().child("Orders");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get date-time
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy   hh:mm:ss aa");
                final String dateCurent = dateFormat.format(c.getTime());

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    for(DataSnapshot ds1: ds.getChildren()){
                        if(ds1.getKey().equals(userID)){
                            for (DataSnapshot ds2:ds1.getChildren()){
                                if(ds2.getValue() !=null) {
                                    Order order = ds2.getValue(Order.class);
                                    int dayOrder = getDayTime(order.getDateTime());
                                    int dayCurent = getDayTime(dateCurent);
                                    if( dayOrder == dayCurent){
                                        arrOrder.add(order);
                                    }
                                }
                            }
                        }
                    }
                }

                final OrderAdapter orderAdapter = new OrderAdapter(arrOrder,getApplicationContext());
                recyclerView.setAdapter(orderAdapter);

                //set anim
                Common.runAnimation(recyclerView);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private Integer getDayTime(String date){
        return Integer.parseInt(date.substring(0,2));
    }
}
