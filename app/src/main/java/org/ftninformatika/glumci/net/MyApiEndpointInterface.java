package org.ftninformatika.glumci.net;


import org.ftninformatika.glumci.net.model.Detalji;
import org.ftninformatika.glumci.net.model2.Example;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface MyApiEndpointInterface {

    @GET("/")
    Call<Example> getMovieByName(@QueryMap Map<String, String> options);

    @GET("/")
    Call<Detalji> getMovieData(@QueryMap Map<String, String> options);

}
