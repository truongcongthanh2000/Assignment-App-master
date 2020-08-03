package com.orderapp.assignment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.orderapp.assignment.Model.Common;
import com.orderapp.assignment.Model.Order;
import com.orderapp.assignment.Notifications.APIService;
import com.orderapp.assignment.Notifications.MyResponse;
import com.orderapp.assignment.Notifications.Notification;
import com.orderapp.assignment.Notifications.Sender;
import com.orderapp.assignment.Notifications.Token;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailOrderActivity extends AppCompatActivity {
    String foodID = "";
    String CustomerID = "";
    RadioGroup radioGroup;
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView tenmon, gia, tenKh, sdt, diachi, soluong, ngaydathang, tongtien;
    ImageView hinh;
    FButton xacnhan;
    RadioButton dagiao, danglam, hethang;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();
    Order order;
    long quantity;
    long gia1mon;
    String userid;

    DatabaseReference database;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail_order);

        declare();
        mService = Common.getFCMService();

        //Nhận THông tin Order từ Intent gửi đến
        Intent intent = getIntent();
        if (intent != null) {
            foodID = intent.getStringExtra("FoodID");
            CustomerID = intent.getStringExtra("CustomerID");
        }
        if (!foodID.isEmpty() && foodID != null && !CustomerID.isEmpty() && CustomerID != null) {
            getDataOrder(CustomerID, foodID);
        }


        xacnhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( dagiao.isChecked() || danglam.isChecked() || hethang.isChecked()){
                    String status="";
                    if (dagiao.isChecked()) {
                        status = "đã giao";
                        mDatabase.child("Orders").child(userID).child(CustomerID).child(foodID).child("check").setValue(1);
                    }
                    else if (danglam.isChecked()) {
                        status ="đang làm";
                        mDatabase.child("Orders").child(userID).child(CustomerID).child(foodID).child("check").setValue(2);
                    }
                    else if (hethang.isChecked()) {
                        status = "hết hàng";
                        mDatabase.child("Orders").child(userID).child(CustomerID).child(foodID).child("check").setValue(3);
                    }

                    //send notification
                    sendNotification(order.getfoodName(),order.getcusName(),status,order.getUserID());

                    Toast.makeText(DetailOrderActivity.this, "Đã xác nhận đơn hàng", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DetailOrderActivity.this, RestaurantActivity.class));
                }
                else {
                    Toast.makeText(DetailOrderActivity.this, "Vui lòng chọn tình trạng giao hàng", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void declare() {
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupShip);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingOrder);
        tenmon = (TextView) findViewById(R.id.tenmon);
        gia = (TextView) findViewById(R.id.tien);
        tenKh = (TextView) findViewById(R.id.ten);
        sdt = (TextView) findViewById(R.id.sdt);
        //diachi = (TextView) findViewById(R.id.diachi);
        soluong = (TextView) findViewById(R.id.soluong);
        ngaydathang = (TextView) findViewById(R.id.ngaydathang);
        tongtien = (TextView) findViewById(R.id.tongtien);
        hinh = (ImageView) findViewById(R.id.hinh);
        xacnhan = (FButton) findViewById(R.id.xacnhan);
        dagiao = (RadioButton) findViewById(R.id.dagiao);
        danglam = (RadioButton) findViewById(R.id.danglam);
        hethang = (RadioButton) findViewById(R.id.hethang);
    }

    private void getDataOrder(final String CustomerID, final String foodID) {

        mDatabase.child("Orders").child(userID).child(CustomerID).child(foodID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                // đối tượng order lấy dữ liệu từ database
                if(dataSnapshot.getValue() != null) {
                    order = dataSnapshot.getValue(Order.class);
                    //THiết lập ảnh
                    Picasso.get().load(order.getlinkPic()).into(hinh);
                    quantity = order.getnumber();
                    gia1mon = order.getprice();
                    gia.setText(gia1mon + " VNĐ");
                    tenmon.setText(order.getfoodName());
                    tenKh.setText("Tên: " + order.getcusName());
                    sdt.setText("Sđt: " + order.getcusPhone());
                   //diachi.setText("Địa chỉ giao hàng: " + order.get());
                    ngaydathang.setText("Ngày đặt hàng: " + order.getDateTime());
                    soluong.setText("Số lượng: " + order.getnumber());
                    tongtien.setText("Tổng tiền: " + gia1mon * quantity + " VNĐ");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendNotification(final String nameFood, final String nameCustomer, final String status, final String ID){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("checkToken").equalTo(1); // get all node isServerToken is false
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    if (ID.equals(ds.getKey())) {
                        Token customerToken = ds.getValue(Token.class);
                        Notification notification = new Notification(nameFood + " được xác nhận " + status, "Chào! " + nameCustomer);
                        Sender content = new Sender(customerToken.getToken(), notification);

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
                        break;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}