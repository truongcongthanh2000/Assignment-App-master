package com.orderapp.assignment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orderapp.assignment.Adapter.CartAdapter;
import com.orderapp.assignment.Model.Cart;
import com.orderapp.assignment.Model.Common;
import com.orderapp.assignment.Model.Order;
import com.orderapp.assignment.Model.User;
import com.orderapp.assignment.Notifications.APIService;
import com.orderapp.assignment.Notifications.MyResponse;
import com.orderapp.assignment.Notifications.Notification;
import com.orderapp.assignment.Notifications.Sender;
import com.orderapp.assignment.Notifications.Token;
import com.stepstone.apprating.C;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {
    ArrayList<Cart> arrCart;
    ArrayList<Order> arrOrder;
    CartAdapter adapter = null;
    TextView total;
    FButton btnConfirm;
    long totalPrice = 0;
    String phoneCus ="", nameCus ="";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();
    User uInfo;
    DatabaseReference mDatabase, mDatabase1;

    RecyclerView recyclerView;
    RelativeLayout relativeLayout;

    APIService mService;

    ArrayList<String> arrID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_cart);

        mService = Common.getFCMService();

        total       = (TextView) findViewById(R.id.total);
        btnConfirm = (FButton) findViewById(R.id.btnPlaceOrder);
        arrCart = new ArrayList<>();
        arrOrder = new ArrayList<>();


        initRecyclerView();


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrCart.size() > 0) {

                    //open dialog_confirmCart
                    final Dialog dialogConfirm = new Dialog(CartActivity.this, R.style.Theme_Dialog);
                    dialogConfirm.setContentView(R.layout.dialog_confirmcart);
                    //anh xa
                    TextView cancel = (TextView) dialogConfirm.findViewById(R.id.cancelCart);
                    TextView confirm = (TextView) dialogConfirm.findViewById(R.id.confirmCart);
                    dialogConfirm.show();
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogConfirm.dismiss();
                        }
                    });

                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //get date-time
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy   hh:mm:ss aa");
                            final String dateTime = dateFormat.format(c.getTime());

                            // get address
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
                            ValueEventListener eventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    uInfo = dataSnapshot.getValue(User.class);
                                    // get sdt + ten khach hang
                                    phoneCus = uInfo.getPhone();
                                    nameCus = uInfo.getName();
                                    // them vào mảng Order
                                    for (int i = 0; i < arrCart.size(); i++) {
                                        arrOrder.add(new Order(dateTime, phoneCus, userID,
                                                nameCus, arrCart.get(i).getNameRes(), arrCart.get(i).getIDRes(),
                                                arrCart.get(i).getNameFood(), arrCart.get(i).getPrice(),
                                                arrCart.get(i).getAmount(), arrCart.get(i).getLinkPics(), 0));
                                        if (!arrID.contains(arrCart.get(i).getIDRes())) {
                                            arrID.add(arrCart.get(i).getIDRes());
                                        }
                                    }

                                    //Send notification
                                    sendNotification(nameCus, arrID);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                            mDatabase.addListenerForSingleValueEvent(eventListener);


                            // ghi data vào db Orders
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Orders");
                            ValueEventListener eventListener1 = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (int i = 0; i < arrOrder.size(); i++) {
                                        mDatabase.child(arrOrder.get(i).getresID()).child(userID).child(arrOrder.get(i).getfoodName()).setValue(arrOrder.get(i));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                            mDatabase.addListenerForSingleValueEvent(eventListener1);


                            // đóng dialog
                            dialogConfirm.dismiss();
                            Toast.makeText(CartActivity.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();

                            // xóa db Cart của user sau khi đặt hàng thành công
                            mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Carts").child(userID);
                            ValueEventListener eventListener2 = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    dataSnapshot.getRef().setValue(null);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                            mDatabase1.addListenerForSingleValueEvent(eventListener2);


                            startActivity(new Intent(CartActivity.this, CustomerActivity.class));

                        }
                    });

                } else {
                    Toast.makeText(CartActivity.this, "Không có món ăn trong Giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }

        });

        }

    private void sendNotification(final String nameCustomer, final ArrayList<String> arrId) {

            DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
            Query data = tokens.orderByChild("checkToken").equalTo(2); // get all node isServerToken is 2
            data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        Token serverToken = ds.getValue(Token.class);
                        for(int i=0;i<arrId.size();i++){
                            if(arrId.get(i).equals(ds.getKey())) {
                                Notification notification = new Notification(nameCustomer + " vừa đặt món ăn từ quán của bạn","Có đơn hàng mới");
                                Sender content = new Sender(serverToken.getToken(), notification);

                                mService.sendNotification(content).enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        if (response.code() == 200) {
                                            if (response.body().success == 1) {
                                                //Toast.makeText(CartActivity.this, "thành công", Toast.LENGTH_SHORT).show();
                                            } else {
                                                //Toast.makeText(CartActivity.this, "Thất bại", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {
                                        Log.e("Error", t.getMessage());
                                    }
                                });
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_cart);
        relativeLayout = (RelativeLayout) findViewById(R.id.layoutCart);


        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Carts").child(userID);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Cart cart = ds.getValue(Cart.class);
                    arrCart.add(cart);
                    totalPrice = totalPrice + cart.getPay();
                }
                total.setText(String.valueOf(totalPrice)+ "đ");

                final CartAdapter cartAdapter = new CartAdapter(arrCart,getApplicationContext());
                cartAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(cartAdapter);


                // set animation
                Common.runAnimation(recyclerView);



                //swipe to delete food or UNDO
                ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {

                    Drawable background;
                    Drawable xMark;
                    int xMarkMargin;
                    boolean initiated;

                    private void init() {
                        background = new ColorDrawable(Color.RED);
                        xMark = ContextCompat.getDrawable(CartActivity.this, R.drawable.ic_delete_white_36);
                        xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                        initiated = true;
                    }

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                        Toast.makeText(CartActivity.this, "on Move", Toast.LENGTH_SHORT).show();
                        return false;
                    }


                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                        //Remove swiped item from list and notify the RecyclerView
                        int position = viewHolder.getAdapterPosition();
                        final Cart mRecentlyDeletedItem = arrCart.get(position);
                        final int mRecentlyDeletedItemPosition = position;
                        arrCart.remove(position);

                        cartAdapter.notifyDataSetChanged();
                        totalPrice = totalPrice - mRecentlyDeletedItem.getPay();
                        total.setText(String.valueOf(totalPrice)+ "đ");

                        ValueEventListener eventListener1 = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                mDatabase.child(mRecentlyDeletedItem.getNameFood()).setValue(null);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        };
                        mDatabase.addListenerForSingleValueEvent(eventListener1);
                        Snackbar snackbar = Snackbar
                                .make(relativeLayout, "Đã xóa "+mRecentlyDeletedItem.getNameFood(), Snackbar.LENGTH_LONG)
                                .setAction("Quay lại", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Snackbar snackbar1 = Snackbar.make(relativeLayout, "Đã khôi phục "+mRecentlyDeletedItem.getNameFood(), Snackbar.LENGTH_LONG);
                                        arrCart.add(mRecentlyDeletedItemPosition,mRecentlyDeletedItem);
                                        cartAdapter.notifyDataSetChanged();
                                        totalPrice = totalPrice + mRecentlyDeletedItem.getPay();
                                        total.setText(String.valueOf(totalPrice)+ "đ");
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
                        }
                        else {
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
