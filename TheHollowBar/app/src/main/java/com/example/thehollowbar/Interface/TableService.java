package com.example.thehollowbar.Interface;

import com.example.thehollowbar.models.ResponseWeb;
import com.example.thehollowbar.models.Table;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TableService {
    @GET("tables")
    Call<ResponseWeb> getTables();
}
