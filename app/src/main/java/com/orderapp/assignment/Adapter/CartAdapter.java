package com.orderapp.assignment.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orderapp.assignment.Model.Cart;
import com.orderapp.assignment.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    ArrayList<Cart> arrCart;
    Context context;

    public CartAdapter(ArrayList<Cart> arrCart, Context context) {
        this.arrCart = arrCart;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_cart,viewGroup,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Cart cart = arrCart.get(i);
        viewHolder.nameOfFood.setText(cart.getNameFood());
        viewHolder.price.setText(String.valueOf(cart.getPrice())+" VNĐ");
        viewHolder.nameOfRes.setText(cart.getNameRes());
        viewHolder.quantity.setText(String.valueOf(cart.getAmount()));
        Picasso.get().load(cart.getLinkPics()).into(viewHolder.image);

    }

    @Override
    public int getItemCount() {
        return arrCart.size();
    }


    public class ViewHolder extends  RecyclerView.ViewHolder{
        TextView nameOfFood,price,nameOfRes,quantity;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameOfFood = (TextView) itemView.findViewById(R.id.cart_item_name);
            price = (TextView) itemView.findViewById(R.id.cart_item_price);
            nameOfRes = (TextView) itemView.findViewById(R.id.cart_item_name_res);
            quantity = (TextView) itemView.findViewById(R.id.cart_item_count);
            image = (ImageView) itemView.findViewById(R.id.cart_item_image);
        }
    }


}