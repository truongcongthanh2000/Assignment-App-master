package com.orderapp.assignment.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orderapp.assignment.Model.Food;
import com.orderapp.assignment.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Food> ListFood;

    TextView name,nameRestaurant;
    ImageView picture;

    public FoodAdapter(Context context, int layout, List<Food> listFood) {
        this.context = context;
        this.layout = layout;
        ListFood = listFood;
    }

    @Override
    public int getCount() {
        return ListFood.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(layout, null);

        name =(TextView) view.findViewById(R.id.nameFood);
        nameRestaurant = (TextView) view.findViewById(R.id.nameRestaurant);
        picture = (ImageView) view.findViewById(R.id.picture);

        final Food food = ListFood.get(i);

        name.setText(food.getName());
        nameRestaurant.setText(food.getNameRestaurant());

        Picasso.get().load(food.getLinkPicture()).into(picture);

        // set anim
        Animation animation = AnimationUtils.loadAnimation(context,R.anim.item_animation_from_left);

        animation.setDuration(1500);
        view.startAnimation(animation);
        return view;
    }
}
