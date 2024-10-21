package com.example.thehollowbar.Interface;
import com.example.thehollowbar.models.Order;
import com.example.thehollowbar.models.User;
import com.example.thehollowbar.models.UserResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserService {
    @GET("users/")
    Call<List<User>> getUser();

    @POST("users/login")
    Call<UserResponse> loginUser(@Body User user);
}
