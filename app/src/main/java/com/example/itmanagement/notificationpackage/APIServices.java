package com.example.itmanagement.notificationpackage;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIServices {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAvBpz48A:APA91bFeZSp6Omw00uRbRa7Rt1ulm05fB79_s8l9mNHd0jAnhrNMTJVY-wEtTmj0OI2oX1yXvih9DHYC35mE514uFTjDo-gZqREjb6ogLZm0Lx7TW9oJ14k4OpxVqw9bmqEM9286-_PR"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
