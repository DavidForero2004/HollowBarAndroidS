package com.example.thehollowbar.Interface;

import com.example.thehollowbar.models.Order;
import com.example.thehollowbar.models.OrderResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface OrderService {
    @GET("orders/")
    Call<List<Order>> getOrder();

    @POST("orders/orderclient")
    Call<OrderResponse> getOrderClient(@Body Order order);
}
