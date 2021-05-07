package com.app.jsinnovations.api

import com.app.jsinnovations.models.Board
import com.app.jsinnovations.models.Place
import com.app.jsinnovations.models.User
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

interface BackendApiInterface{

    @POST("/definition/login")
    fun login(@Header("Authorization") token: String, @Body hashMap: HashMap<String, String>): Call<ResponseBody>

    @POST("/api/user/addUser")
    fun addUser(@Header("Authorization") token: String, @Body hashMap: HashMap<String, String>): Call<ResponseBody>

    @POST("/api/auth/local")
    fun auth(@Body hashMap: HashMap<String, String>): Call<JsonObject>

    @GET("/api/user/allBoards")
    fun allBoards(@Query("limit") limit: String, @Query("current") current: String,
                  @Header("Authorization") token: String): Call<JsonObject>

    @GET("api/partners/places/boards/{placeId}")
    fun getBoards(@Path("placeId") placeId: String, @Header("Authorization") token: String): Call<ArrayList<Board>>

    @GET("api/partners/places/boards/report/{placeId}")
    fun getBoardsByDate(@Path("placeId") placeId: String,
                        @Query("startDate") startDate: String,
                        @Query("endDate")endDate: String,
                        @Query("utcOffset=+4")utcOff:TimeZone,
                        @Header("Authorization") token: String): Call<ArrayList<Board>>

    @GET("/api/user/board/{boardId}")
    fun getBoard(@Path("boardId") boardId: String, @Header("Authorization") token: String): Call<JsonObject>

    @GET("/api/admin/partners/board/{userId}")
    fun getBoard(@Query("userId") userId: String): Call<JsonObject>

    @POST("/api/admin/definitions/board/addBoard")
    fun addBoard(@Header("Authorization") token: String, @Body hashMap: HashMap<String, String>):Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @Multipart
    @POST("/api/images/{width}/{height}")
    fun images(@Part file: MultipartBody.Part, @Header("Authorization") token: String):Call<ResponseBody>

    @GET("/api/images")
    fun getImage(@Header("Authorization") token: String):Call<ResponseBody>

    @GET("/api/customers/account")
    fun getUser(@Header("Authorization") token: String): Call<User>

    @GET("/api/partners/places")
    fun getPlaces(@Header("Authorization") token: String): Call<ArrayList<Place>>

    @GET("/api/place/{placeId}")
    fun getPlace(@Path("placeId") placeId: String, @Header("Authorization") token: String): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("/api/place")
    fun addPlace(@Header("Authorization") token: String, @Body hashMap: HashMap<String, String>): Call<ResponseBody>

    @GET("/api/partners/boards/{boardId}")
    fun getModules(@Query("boardId") boardId: String, @Header("Authorization") token: String): Call<JsonObject>

    @GET("/api/partners/boards/modules/setting/{boardId}/{moduleId}")
    fun settings(@Query("boardId") boardId: String, @Query("moduleId") moduleId: String,
                 @Header("Authorization") token: String): Call<JsonObject>?

    @POST("/api/customers/account")
    fun register(@Body hashMap: HashMap<String, String>): Call<ResponseBody>

    @GET("/api/partners/boards/modules/setting/{boardId}")
    fun getModule(@Header("Authorization") token: String): Call<ResponseBody>
}