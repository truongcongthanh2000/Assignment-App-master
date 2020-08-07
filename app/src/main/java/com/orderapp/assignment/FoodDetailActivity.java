package com.orderapp.assignment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orderapp.assignment.Adapter.RatingAdapter;
import com.orderapp.assignment.Model.Cart;
import com.orderapp.assignment.Model.Favorite;
import com.orderapp.assignment.Model.Food;
import com.orderapp.assignment.Model.Rating;
import com.orderapp.assignment.Model.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class FoodDetailActivity extends AppCompatActivity implements RatingDialogListener {
    User restaurant;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String name,phone;
    String foodId = "";
    String RestaurentID = "";
    FloatingActionButton btnCart,btnRating,btnShare;
    ElegantNumberButton number;
    ImageView pic,fav;
    TextView nameFood, priceFood, descriptionFood, reviewsFood;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference database, databaseReference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();
    Food food;
    long quantity = 1 , price = 0;
    String image="";
    RatingBar ratingBar;
    CounterFab call;


    ArrayList<Rating> arrRating;
    RatingAdapter adapter = null;
    ListView listView;

    DatabaseReference ratingTbl = FirebaseDatabase.getInstance().getReference();


    Favorite favorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.layout_food_detail);


        btnCart = (FloatingActionButton)findViewById(R.id.btnCart);
        number = (ElegantNumberButton) findViewById(R.id.number_button);
        pic = (ImageView) findViewById(R.id.image_food);
        nameFood = (TextView) findViewById(R.id.food_name);
        priceFood = (TextView) findViewById(R.id.food_price);
        descriptionFood =(TextView) findViewById(R.id.food_description);
        reviewsFood = (TextView) findViewById(R.id.cacdanhgia);
        fav = (ImageView) findViewById(R.id.fav);
        call = (CounterFab) findViewById(R.id.call);
        collapsingToolbarLayout =(CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        btnRating = (FloatingActionButton) findViewById(R.id.btnRating);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        //Nhận THông tin Food từ Intent gửi đến
        Intent intent = getIntent();
        if(intent != null){
            foodId       = intent.getStringExtra("FoodId");
            RestaurentID = intent.getStringExtra("RestaurentID");
        }
        if(!foodId.isEmpty() && foodId !=null && !RestaurentID.isEmpty() && RestaurentID!= null){
            getDetailFood(RestaurentID,foodId);
            getRatingFood(foodId);
            initRecyclerView(foodId,RestaurentID);
        }



        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(food.getStatus() == 1) {
                    Cart cart = new Cart(food.getName(),food.getNameRestaurant(),food.getIdRestaurant(),food.getLinkPicture(),food.getPrice(),quantity,price);
                    //them vao database
                    mDatabase.child("Carts").child(userID).child(food.getName()).setValue(cart);
                    Toast.makeText(FoodDetailActivity.this, "Having added to cart", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(FoodDetailActivity.this, "This food is out of order", Toast.LENGTH_SHORT).show();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                startActivity(intentCall);
            }
        });



        //favorite

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Favorite").child(userID).child(foodId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() == null){
                    fav.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }
                else {
                    Favorite favor = dataSnapshot.getValue(Favorite.class);
                    if (favor.getCheck() == 0) {
                        fav.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    } else {
                        fav.setImageResource(R.drawable.ic_favorite_black);
                    }
                }

                fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dataSnapshot.getValue() == null){
                            fav.setImageResource(R.drawable.ic_favorite_black);
                            Toast.makeText(FoodDetailActivity.this, foodId+" has been added to favorite foods", Toast.LENGTH_SHORT).show();
                            favorite = new Favorite(foodId,userID,RestaurentID,price,image,1);
                        }
                        else{
                            favorite = dataSnapshot.getValue(Favorite.class);
                            if(favorite.getCheck()==1){
                                fav.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                                Toast.makeText(FoodDetailActivity.this, foodId+" has been removed of favorite foods", Toast.LENGTH_SHORT).show();
                                favorite.setCheck(0);
                            }
                            else{
                                fav.setImageResource(R.drawable.ic_favorite_black);
                                Toast.makeText(FoodDetailActivity.this, foodId+" has been added to favorite foods", Toast.LENGTH_SHORT).show();
                                favorite.setCheck(1);
                            }
                        }
                        databaseReference.setValue(favorite);
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Send")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite OK","Very Good","Exellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("Pick one")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Your review ...")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(R.color.colorWhite)
                .setCommentBackgroundColor(R.color.colorPrimary_Dark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetailActivity.this)
                .show();


    }

    private void initRecyclerView(final String foodID, final String restaurentID) {
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewRating);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        final ArrayList<Rating> arrRating = new ArrayList<>();

        // divider item
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        database = FirebaseDatabase.getInstance().getReference().child("Rating").child(foodID);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Rating rating = ds.getValue(Rating.class);
                    if(rating.getFoodID().equals(foodID) && rating.getRestaurentID().equals(RestaurentID))
                        arrRating.add(rating);
                }
                if(arrRating.size() == 0){
                    reviewsFood.setText("This food hasn't reviewed yet");
                    reviewsFood.setTextColor(getResources().getColor(R.color.colorGray));
                }
                else{
                    reviewsFood.setText("Reviews:");
                    reviewsFood.setTextColor(Color.parseColor("#1A554E"));
                }
                //sort dateTime
                for(int i=0;i<arrRating.size()-1;i++){
                    int day_i   =  Integer.parseInt(arrRating.get(i).getDateTime().substring(0,2));
                    int month_i =  Integer.parseInt(arrRating.get(i).getDateTime().substring(3,5));
                    int year_i  =  Integer.parseInt(arrRating.get(i).getDateTime().substring(6,10));

                    for(int j= i+1 ;j<arrRating.size(); j++){
                        int day_j   =  Integer.parseInt(arrRating.get(j).getDateTime().substring(0,2));
                        int month_j =  Integer.parseInt(arrRating.get(j).getDateTime().substring(3,5));
                        int year_j  =  Integer.parseInt(arrRating.get(j).getDateTime().substring(6,10));
                        if(year_i == year_j){
                            if(month_i == month_j){
                                if(day_i < day_j) swap(arrRating,i,j);
                            }
                            else if( month_i < month_j) swap(arrRating,i,j);
                        }
                        else if(year_i < year_j) swap(arrRating,i,j);
                    }

                }
                RatingAdapter ratingAdapter = new RatingAdapter(arrRating,getApplicationContext());
                recyclerView.setAdapter(ratingAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    static void swap( ArrayList<Rating> arr , int i ,int j){
        Rating temp = arr.get(i);
        arr.set(i,arr.get(j));
        arr.set(j,temp);
    }

    private void getRatingFood(final String foodId) {
        ratingTbl.child("Rating").child(foodId).addValueEventListener(new ValueEventListener() {
            float count = 0,sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Rating item = ds.getValue(Rating.class);
                    if(item.getFoodID().equals(foodId)) {
                        sum += Float.parseFloat(item.getRateValue());
                        count++;
                    }
                }
                if( count != 0) {
                    float average = sum / count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDetailFood(final String restaurentID, final String foodId) {
        mDatabase.child("users").child(restaurentID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                restaurant = dataSnapshot.getValue(User.class);
                phone = restaurant.getPhone();
                //set data
                mDatabase.child("restaurants").child(restaurentID).child(foodId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // đối tượng food lấy dữ liệu từ database
                        food = dataSnapshot.getValue(Food.class);
                        //THiết lập ảnh
                        Picasso.get().load(food.getLinkPicture()).into(pic);
                        collapsingToolbarLayout.setTitle(food.getName());

                        price = food.getPrice();
                        image = food.getLinkPicture();
                        priceFood.setText(price +" VNĐ");
                        number.setOnClickListener(new ElegantNumberButton.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                quantity = Long.parseLong(number.getNumber());
                                price = quantity * food.getPrice();
                                priceFood.setText(price+" VNĐ");
                            }
                        });

                        nameFood.setText(food.getName());
                        if (food.getStatus() == 1) {
                            descriptionFood.setText("Status: Available\nRestaurant: "+food.getNameRestaurant()+"\nContact: "+restaurant.getPhone());
                        } else {
                            descriptionFood.setText("Status: Unavailable\nRestaurant: "+food.getNameRestaurant()+"\nContact: "+restaurant.getPhone());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(final int value, @NotNull final String comment) {

        //get date-time
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy  hh:mm aa");
        final String dateTime = dateFormat.format(c.getTime());
        //get name
        mDatabase.child("users").child(userID).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.getValue(String.class);
                //get Rating and upload firebase
                final Rating rating = new Rating(name,RestaurentID,foodId,String.valueOf(value),comment,dateTime);
                ratingTbl.child("Rating").child(foodId).child(userID).setValue(rating);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toast.makeText(FoodDetailActivity.this, "Thank you for your review", Toast.LENGTH_SHORT).show();

        finish();
        startActivity(getIntent());
    }
}
