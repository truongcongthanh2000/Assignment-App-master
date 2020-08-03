package com.orderapp.assignment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.mancj.materialsearchbar.MaterialSearchBar;
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

import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchFoodActivity extends AppCompatActivity {
    MaterialSearchBar materialSearchBar;
    ListView lvFood;
    ArrayList<Food> arrFood;
    ArrayList<Food> arrFoodSearch;
    FoodAdapter adapter = null;
    FoodAdapter adapterSearch = null;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();
    DatabaseReference mDatabase;
    ArrayList<String> suggestList = new ArrayList<>();

//    @Override
////    protected void attachBaseContext(Context newBase){
////        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
////    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Note  add this code before setcontentView
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Rubik.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.layout_search_food);
        //Tìm kiếm
        materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.setHint("Nhập tên món ăn");
        // end tiem kiem
        lvFood  =   (ListView) findViewById(R.id.listview_food);
        arrFood = new ArrayList<>();
        adapter = new FoodAdapter(this, R.layout.item_food, arrFood);
        arrFoodSearch = new ArrayList<>();
        adapterSearch = new FoodAdapter(this, R.layout.item_food, arrFoodSearch);

        loadDataAllFood();
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //Khi người dùng nhập vào SearchBar danh sách gợi ý sẽ thay đổi theo.
                ArrayList<String> suggest = new ArrayList<String>();
                for(String search:suggestList){ // Vòng lặp suggetList
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //Khi Search Bar bị đóng
                //Khôi phục adapter ban đầu
                if(!enabled) {
                    //lvFood.setAdapter(adapter);
                    lvFood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            //position là vi tri tren listview
                            Food foodSearch = arrFood.get(position);
                            Intent foodDetail = new Intent(SearchFoodActivity.this,FoodDetailActivity.class);
                            //gửi FoodId (ten của Food) và id quán đến activity FoodDetail
                            foodDetail.putExtra("FoodId",foodSearch.getName());
                            foodDetail.putExtra("RestaurentID",foodSearch.getIdRestaurant());
                            // mở activity  foodDetail
                            startActivity(foodDetail);
                        }
                    });
                    lvFood.setAdapter(adapter);

                    arrFoodSearch.clear();
                }
            }

            @Override
            public void onSearchConfirmed(final CharSequence text) {
                //Khi Search hoàn tất
                //hiển thị kết quả của searchAdapter
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });


    }

    private void startSearch(final CharSequence text) {

        mDatabase = FirebaseDatabase.getInstance().getReference().child("restaurants");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrFoodSearch.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    for(DataSnapshot ds1: ds.getChildren()){
                        Food food = ds1.getValue(Food.class);
                        if(food.getName().contains(text.toString())){
                            arrFoodSearch.add(new Food(food.getName(), food.getNameRestaurant(), food.getLinkPicture(), food.getIdRestaurant(), food.getPrice(), food.getStatus()));
                            adapterSearch.notifyDataSetChanged();
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addListenerForSingleValueEvent(eventListener);


        lvFood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //position là vi tri tren listview
                Food foodSearch = arrFoodSearch.get(position);
                Intent foodDetail = new Intent(SearchFoodActivity.this,FoodDetailActivity.class);
                //gửi FoodId (ten của Food) và id quán đến activity FoodDetail
                foodDetail.putExtra("FoodId",foodSearch.getName());
                foodDetail.putExtra("RestaurentID",foodSearch.getIdRestaurant());
                // mở activity  foodDetail
                startActivity(foodDetail);
            }
        });
        lvFood.setAdapter(adapterSearch);


    }

    private  void loadDataAllFood(){

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


                lvFood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        //position là vi tri tren listview
                        Food food = arrFood.get(position);
                        Intent foodDetail = new Intent(SearchFoodActivity.this,FoodDetailActivity.class);
                        //gửi FoodId (ten của Food) và id quán đến activity FoodDetail
                        foodDetail.putExtra("FoodId",food.getName());
                        foodDetail.putExtra("RestaurentID",food.getIdRestaurant());
                        // mở activity  foodDetail
                        startActivity(foodDetail);
                    }
                });
                lvFood.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addListenerForSingleValueEvent(eventListener);




    }

    private void loadSuggest() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("restaurants");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds :dataSnapshot.getChildren())
                {
                    for(DataSnapshot ds1: ds.getChildren()) {
                        Food food = ds1.getValue(Food.class);
                        suggestList.add(food.getName()); //Thêm tên món ăn vào danh sách gợi ý
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
