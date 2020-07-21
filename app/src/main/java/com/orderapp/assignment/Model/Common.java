package com.orderapp.assignment.Model;

import androidx.recyclerview.widget.RecyclerView;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.orderapp.assignment.Notifications.APIService;
import com.orderapp.assignment.Notifications.RetrofitClient;

import com.orderapp.assignment.R;

public class Common {

    private static final String FCM_URL = "https://fcm.googleapis.com/";
    public static APIService getFCMService(){
        return RetrofitClient.getClient(FCM_URL).create(APIService.class);
    }


    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    public static void runAnimation(RecyclerView recyclerView) {
        LayoutAnimationController controller = null;

        controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(), R.anim.layout_slide_from_left);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

}
