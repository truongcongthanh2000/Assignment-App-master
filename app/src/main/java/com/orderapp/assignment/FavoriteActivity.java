package com.orderapp.assignment;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orderapp.assignment.Adapter.FavoriteAdapter;
import com.orderapp.assignment.Model.Favorite;
import com.squareup.picasso.Picasso;
import com.orderapp.assignment.Model.Common;
import com.orderapp.assignment.Model.Order;
import com.orderapp.assignment.Notifications.APIService;
import com.orderapp.assignment.Notifications.MyResponse;
import com.orderapp.assignment.Notifications.Notification;
import com.orderapp.assignment.Notifications.Sender;
import com.orderapp.assignment.Notifications.Token;

import java.util.ArrayList;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteActivity extends AppCompatActivity {
    DatabaseReference database, databaseReference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();

    CoordinatorLayout coordinatorLayout;
    ImageView home;
    RecyclerView recyclerView;
    Favorite favorite;
    ArrayList<Favorite> arrFavorite = new ArrayList<>();
    FavoriteAdapter favoriteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_favorite);

        //set color status bar
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
//        }

        home = findViewById(R.id.home_favorite);
        initRecyclerView();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Favorite").child(userID);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FavoriteActivity.this, CustomerActivity.class));
            }
        });



    }




    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_fav_food);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.layout_favorite);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());



        database = FirebaseDatabase.getInstance().getReference().child("Favorite").child(userID);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrFavorite.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (dataSnapshot.getValue() != null) {
                        favorite = ds.getValue(Favorite.class);
                        if (favorite.getCheck() == 1)
                            arrFavorite.add(favorite);
                    }
                }
                favoriteAdapter = new FavoriteAdapter(arrFavorite, getApplicationContext());
                recyclerView.setAdapter(favoriteAdapter);

                //set animation
                Common.runAnimation(recyclerView);


                //delete vs Undo Favorite food
                ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                    Drawable background;
                    Drawable xMark;
                    int xMarkMargin;
                    boolean initiated;

                    private void init() {
                        background = new ColorDrawable(Color.RED);
                        xMark = ContextCompat.getDrawable(FavoriteActivity.this, R.drawable.ic_delete_white_36);
                        xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                        initiated = true;
                    }

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        Toast.makeText(FavoriteActivity.this, "on Move", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                        //Remove swiped item from list and notify the RecyclerView
                        final int position = viewHolder.getAdapterPosition();
                        final Favorite mRecentlyDeletedItem = arrFavorite.get(position);

                        //databaseReference.child(mRecentlyDeletedItem.getFoodID()).child("check").setValue(0);
                        final int mRecentlyDeletedItemPosition = position;
                        //arrFavorite.remove(position);
                        databaseReference.child(mRecentlyDeletedItem.getFoodID()).setValue(null);
                        favoriteAdapter.notifyDataSetChanged();

                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Remove " + mRecentlyDeletedItem.getFoodID(), Snackbar.LENGTH_LONG)
                                .setAction("Redo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "Add " + mRecentlyDeletedItem.getFoodID() + " again", Snackbar.LENGTH_LONG);
                                        //databaseReference.child(mRecentlyDeletedItem.getFoodID()).child("check").setValue(1);
                                        databaseReference.child(mRecentlyDeletedItem.getFoodID()).setValue(mRecentlyDeletedItem);
                                        ///arrFavorite.add(mRecentlyDeletedItemPosition,mRecentlyDeletedItem);
                                        favoriteAdapter.notifyDataSetChanged();
                                        snackbar1.show();
                                    }
                                });

                        snackbar.show();

                    }

                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        View itemView = viewHolder.itemView;

                        // not sure why, but this method get's called for viewholder that are already swiped away
                        if (viewHolder.getAdapterPosition() == -1) {
                            // not interested in those
                            return;
                        }

                        if (!initiated) {
                            init();
                        }

                        // draw red background
                        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                        background.draw(c);

                        int xMarkLeft = 0;
                        int xMarkRight = 0;
                        int xMarkTop = itemView.getTop() + (itemView.getHeight() - xMark.getIntrinsicHeight()) / 2;
                        int xMarkBottom = xMarkTop + xMark.getIntrinsicHeight();
                        if (dX < 0) {
                            xMarkLeft = itemView.getRight() - xMarkMargin - xMark.getIntrinsicWidth();
                            xMarkRight = itemView.getRight() - xMarkMargin;
                        } else {
                            xMarkLeft = itemView.getLeft() + xMarkMargin;
                            xMarkRight = itemView.getLeft() + xMarkMargin + xMark.getIntrinsicWidth();
                        }
                        xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
                        xMark.draw(c);

                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }

                };
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                itemTouchHelper.attachToRecyclerView(recyclerView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

}
