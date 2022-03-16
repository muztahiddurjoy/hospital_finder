package com.enconiya.hospitalapp.Interfaces;

import com.enconiya.hospitalapp.Datasets.Result;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("maps/api/directions/json")
    Single<Result> getDirection(@Query("mode") String mode,
                                @Query("transit_routing_preferance") String preferance,
                                @Query("origin") String origin,
                                @Query("destinaiton") String destinaiton,
                                @Query("key") String key);
}
