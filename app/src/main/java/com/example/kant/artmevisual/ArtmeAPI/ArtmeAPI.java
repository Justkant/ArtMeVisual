package com.example.kant.artmevisual.ArtmeAPI;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

/**
 * Created by Shaft on 22/02/2015.
 */
public interface ArtmeAPI {

    @GET(Constants.ME)
    public void userMe(@Header("TOKEN") String token, Callback<User> callback);

    @GET(Constants.EVENTS)
    public void getEvents(Callback<List<Event>> callback);

    @POST(Constants.LOGIN)
    void login(@Body User log, Callback<String> cb);

    @POST(Constants.USERS)
    void postUser(@Body User log, Callback<String> cb);

    @PUT("/users/{id}")
    void putUser(@Header("TOKEN") String token, @Path("id") int id, @Body User log, Callback<User> cb);

    @GET("/users/{id}")
    void getUserById(@Path("id") int id, Callback<User> cb);

    @DELETE("/users/{id}")
    void deleteUser(@Path("id") int id, @Header("TOKEN") String token, Callback<String> cb);

    @DELETE("/events/{id}")
    void deleteEvent(@Path("id") int id, @Header("TOKEN") String token, Callback<String> cb);

    @POST("/users/{id}/event")
    void userPostEvent(@Path("id") int id, @Header("TOKEN") String token, @Body Event event, Callback<Event> cb);

    @PUT("/events/{id}")
    void putEvent(@Path("id") int id, @Header("TOKEN") String token, @Body Event event, Callback<Event> cb);

    @POST("/events/{id}/user")
    void subEvent(@Path("id") int id, @Header("TOKEN") String token, Callback<Event> cb);

    @POST("/groups/{id}/event")
    void groupPostEvent(@Path("id") int id, @Header("TOKEN") String token, @Body Event event, Callback<Event> cb);

    @DELETE("/groups/{id_grp}/user/{id}")
    void unsubGroup(@Path("id_grp") int id_grp, @Path("id") int id, @Header("TOKEN") String token, Callback<ApiReturn> cb);

    @DELETE("/groups/{id_grp}")
    void deleteGroup(@Path("id_grp") int id_grp, @Header("TOKEN") String token, Callback<ApiReturn> cb);

    @POST(Constants.C_GROUP)
    void crtGroup(@Header("TOKEN") String token, @Body Group group, Callback<ApiReturn> cb);

    @PUT("/groups/{id}")
    void putGroup(@Header("TOKEN") String token, @Path("id") int id, @Body Group log, Callback<Group> cb);

    @GET("/groups/{id}")
    void getGroupById(@Path("id") int id, @Header("TOKEN") String token, Callback<Group> cb);

    @GET("/events/{id}")
    void getEventById(@Path("id") int id, @Header("TOKEN") String token, Callback<Event> cb);

    @Multipart
    @POST("/{type}/{id}/photo")
    void postPhoto(@Path("type") String type, @Path("id") int id, @Header("TOKEN") String token, @Part("photo") TypedFile photo, Callback<String> cb);
}
