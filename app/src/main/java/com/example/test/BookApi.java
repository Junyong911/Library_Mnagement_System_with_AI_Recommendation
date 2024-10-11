package com.example.test;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BookApi {
    @GET("/api/books")
    Call<Map<String, BookResponse>> getBookDetails(
            @Query("bibkeys") String isbn,
            @Query("format") String format,
            @Query("jscmd") String cmd
    );
}



