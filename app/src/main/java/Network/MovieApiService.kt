package com.example.moviemuse.Network

import com.example.moviemuse.model.Movie
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.moviemuse.model.MovieResponse
import com.example.moviemuse.model.ReviewResponse
import model.TrailerResponse
import retrofit2.http.Path

interface MovieApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): MovieResponse

    @GET("movie/{movie_id}/reviews")
    suspend fun getMovieReviews(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): ReviewResponse

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): TrailerResponse

    @GET("search/movie")
    suspend fun searchMovie(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int,
        @Query("query") query: String,
    ): MovieResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
    ): Movie
}