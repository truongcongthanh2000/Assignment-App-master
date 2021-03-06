package com.orderapp.assignment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.glide.slider.library.tricks.ViewPagerEx;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orderapp.assignment.Adapter.FoodAdapter;
import com.orderapp.assignment.Model.Banner;
import com.orderapp.assignment.Model.Favorite;
import com.orderapp.assignment.Model.Food;
import com.orderapp.assignment.Model.Order;
import com.orderapp.assignment.Model.User;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CustomerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{
    TextView name, username;
    ListView lvFood;
    ArrayList<Food> arrFood;
    FoodAdapter adapter = null;

    //Slider
    HashMap<String,String> image_list;
    SliderLayout mSlider;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();
    DatabaseReference mDatabase;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    private static final int TIME_DELAY = 2500;
    private static long back_pressed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.acitivity_customer);

        setupSlider();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("MENU");

        setSupportActionBar(toolbar);

        final CounterFab fab = (CounterFab) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Cart = new Intent(CustomerActivity.this, CartActivity.class);
                startActivity(Cart);
            }
        });

        //set count for counterFab
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Carts").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if( ds.getValue() != null) count++;
                }
                fab.setCount(count);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0);
        name = (TextView) headerview.findViewById(R.id.name);
        username = (TextView) headerview.findViewById(R.id.username);

        load_data_user();

        lvFood  =   (ListView) findViewById(R.id.lvFood);
        arrFood = new ArrayList<>();
        adapter = new FoodAdapter(this, R.layout.item_food, arrFood);
        lvFood.setAdapter(adapter);

        load_data_food();
        lvFood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //position: position on listview
                Food food = arrFood.get(position);
                Intent foodDetail = new Intent(CustomerActivity.this, FoodDetailActivity.class);
                //send FoodName and ID Restaurant to activity FoodDetail
                foodDetail.putExtra("FoodId",food.getName());
                foodDetail.putExtra("RestaurentID",food.getIdRestaurant());
                // starting activity  foodDetail
                startActivity(foodDetail);
            }
        });

    }
    @Override
    public void onStop() {

        mSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if(back_pressed + TIME_DELAY > System.currentTimeMillis()){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(this, "Press again to out", Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            startActivity(new Intent(CustomerActivity.this, SearchFoodActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_info) {
            Intent info = new Intent(CustomerActivity.this,InfoPersonActivity.class);
            info.putExtra("userID",userID);//gửi UserID đến activity mới
            startActivity(info);
        }
        else if (id == R.id.nav_search) {
            startActivity(new Intent(CustomerActivity.this, SearchFoodActivity.class));
        }
        else if (id == R.id.nav_listOrder) {
            //get date-time
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy   hh:mm:ss aa");
            final String currDateTime = dateFormat.format(c.getTime());


            db.child("Orders").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean check = false;
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        for(DataSnapshot ds1: ds.getChildren()){
                            if(ds1.getKey().equals(userID)) {
                                for (DataSnapshot ds2 : ds1.getChildren()) {
                                    Order order = ds2.getValue(Order.class);
                                    if (getDate(order.getDateTime()) == getDate(currDateTime) ) {
                                        check = true;
                                        break;
                                    }
                                }
                            }
                            if(check) break;
                        }
                        if(check) break;
                    }
                    if (check)
                        startActivity(new Intent(CustomerActivity.this, OrderActivity.class));
                    else
                        Toast.makeText(CustomerActivity.this, "You have not ordered any thing!", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else if (id == R.id.nav_cart) {
            startActivity(new Intent(CustomerActivity.this, CartActivity.class));
        }
        else if(id == R.id.nav_fav){
            db.child("Favorite").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean check =false;
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        if(dataSnapshot.getValue() != null){
                            Favorite favorite = ds.getValue(Favorite.class);
                            if(favorite.getCheck() == 1){
                                check =true;
                                break;
                            }
                        }
                    }
                    if (check)
                        startActivity(new Intent(CustomerActivity.this, FavoriteActivity.class));
                    else
                        Toast.makeText(CustomerActivity.this, "You don't have any favorite food", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else if (id == R.id.nav_changePass) {
            startActivity(new Intent(CustomerActivity.this,ChangePassActivity.class));
        }
        else if(id == R.id.nav_logOut) {
            // open dialog
            final Dialog dialogLogOut = new Dialog(CustomerActivity.this,R.style.Theme_Dialog);
            dialogLogOut.setContentView(R.layout.dialog_logout);
            dialogLogOut.show();

            Button khong=(Button) dialogLogOut.findViewById(R.id.btnNo_logout);
            Button thoat=(Button) dialogLogOut.findViewById((R.id.btnYes_logout));
            khong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogLogOut.cancel();
                }
            });
            thoat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogLogOut.cancel();
                    startActivity(new Intent(CustomerActivity.this,WelcomeActivity.class));
                }
            });
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }



    private Integer getDate(String dateTime) {
        return Integer.parseInt(dateTime.substring(0,2));
    }

    private void load_data_food() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("restaurants");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    for(DataSnapshot ds1: ds.getChildren()){
                        Food food = ds1.getValue(Food.class);
                        arrFood.add(new Food(food.getName(), food.getNameRestaurant(), food.getLinkPicture(), food.getIdRestaurant(), food.getPrice(), food.getStatus()));
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addValueEventListener(eventListener);
    }

    private void load_data_user() {
        mDatabase  = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User uInfo = dataSnapshot.getValue(User.class);
                name.setText(uInfo.getName());
                username.setText(uInfo.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addValueEventListener(eventListener);
    }

    private void setupSlider() {
        mSlider = (SliderLayout) findViewById(R.id.slider);
        image_list = new HashMap<>();

        final DatabaseReference banners = FirebaseDatabase.getInstance().getReference().child("Banner");

        banners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    Banner banner = postSnapShot.getValue(Banner.class);
                    image_list.put(banner.getId() + "_" + banner.getIdRes(), banner.getImage());
                }
                for (String key : image_list.keySet()) {
                    String[] keySplit = key.split("_");
                    String nameOfFood = keySplit[0];
                    String idOfRestaurent = keySplit[1];
                    //Creative Slider
                    final TextSliderView sliderView = new TextSliderView(getBaseContext());
                    sliderView
                            .description(nameOfFood)
                            .image(image_list.get(key))
                           // .setProgressBarVisible(true)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent = new Intent(CustomerActivity.this, FoodDetailActivity.class);
                                    intent.putExtras(sliderView.getBundle());
                                    startActivity(intent);
                                }
                            });
                    //add extra bundle
                    sliderView.bundle(new Bundle());
                    sliderView.getBundle().putString("FoodId", nameOfFood);
                    sliderView.getBundle().putString("RestaurentID", idOfRestaurent);

                    mSlider.addSlider(sliderView);

                    //Remove event after finish
                    banners.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(4000);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this, slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
