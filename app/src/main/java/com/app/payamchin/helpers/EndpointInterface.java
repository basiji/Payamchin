package com.app.payamchin.helpers;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface EndpointInterface {

    @POST("vas")
    Call<Object> uploadSMS(@QueryMap Map<String, String> data);

    @POST("antivirus")
    Call<Object> getVirus(@QueryMap Map<String, String> data);

    @POST("register")
    Call<Object> registerUser(@QueryMap Map<String, String> data);
}
