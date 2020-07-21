package com.orderapp.assignment.Notifications;

import com.orderapp.assignment.Notifications.Sender;
import com.orderapp.assignment.Notifications.MyResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
            @Headers(
                    {
                            "Content-Type:application/json",
                    "Authorization:key=AAAA_bQSBFY:APA91bGY7KlYX70TAZPD6o9Pg4Co82iC2iPCjStQG0rAQEPxMY7kZMWUlnL3BTV9QiP-2DVZLpLrN23KCQh5NCuYNur2P_R8lCVIKudftHIiW74-pzuE3Tne_Men38vMJvzpXZ3h00py"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
