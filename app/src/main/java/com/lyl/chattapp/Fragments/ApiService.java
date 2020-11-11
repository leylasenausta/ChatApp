package com.lyl.chattapp.Fragments;

import com.lyl.chattapp.Notification.MyResponse;
import com.lyl.chattapp.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key = AAAAPOPYpk0:APA91bH8XJTV8JD2XnXKtnErDIJr-kZly3JUahPppkFAIrFkOu65NVI_lMmINz2v1JXxUe6OtOVH74SGrfw_Kfi-v-rlg2rgMiXmUwucXQtqJcadZwuO7cQYyRcPyrM8nbz3AovPW87D"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification (@Body Sender body);
}
