package com.example.thehollowbar.Interface;
import com.example.thehollowbar.models.User;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface UserService {
    @GET("/users/")
    Call<List<User>> getUser();
}
