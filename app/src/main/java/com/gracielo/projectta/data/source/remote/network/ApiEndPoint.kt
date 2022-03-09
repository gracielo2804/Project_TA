package com.gracielo.projectta.data.source.remote.network

import com.gracielo.expertsubmission1.core.data.source.remote.response.ListTVResponse
import com.gracielo.projectta.data.model.*
import com.gracielo.projectta.data.model.recipe.detail.RecipeDetailResponse
import com.gracielo.projectta.data.model.recipe.search.RecipeResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiEndPoint {

    @POST(" ")
    @FormUrlEncoded
    fun registerUser(
        @Field("function") function: String? = "adduser",
        @Field("email") email: String?,
        @Field("password") password: String? ,
        @Field("name") name: String?,
        @Field("username") username: String?
    ): Call<MessageResponse>

    @POST(" ")
    @FormUrlEncoded
    fun Login(
        @Field("function") function: String? = "login",
        @Field("username") username: String?,
        @Field("password") password: String?,
    ): Call<MessageResponse>

    @POST(" ")
    @FormUrlEncoded
    fun getDataUserProfile(
        @Field("function") function: String? = "login",
        @Field("id_users") id_users: String?,
    ): Call<MessageResponse>

    @POST(" ")
    @FormUrlEncoded
    fun getUserDataByID(
        @Field("function") function: String? = "getUserDataByID",
        @Field("id_users") id_users: String?,
    ): Call<MessageResponse>

    @POST(" ")
    @FormUrlEncoded
    fun addUserProfileData(
        @Field("function") function: String = "addUserProfileData",
        @Field("id_users") id_users: String,
        @Field("height") height : Int,
        @Field("weight") weight : Int,
        @Field("age") age : Int,
        @Field("gender") gender : String? ,
        @Field("kalori") kalori: Double
    ): Call<MessageResponse>

    @POST(" ")
    @FormUrlEncoded
    fun sendVerificationEmail(
        @Field("function") function: String = "testSendEmail",
        @Field("id_users") id_users: String,
        @Field("email_recipient") email_recipient : String,
        @Field("recipient_name") recipient_name : String
    ): Call<MessageResponse>

    @Multipart
    @POST(" ")
    fun uploadPicture(
        @Part("function") function: RequestBody,
        @Part imagePart: MultipartBody.Part
    ): Call<ResponseBody>

//    @Headers("Content-Type: application/json")
//    @POST(" ")
//    fun registerUser(
//        @Body userData:AddUsers
//    ): Call<AddUsers>

    @POST(" ")
    @FormUrlEncoded
    fun checkUsername(
        @Field("function") function: String = "checkUsername",
        @Field("username") username: String,
    ): Call<MessageResponse>

    @POST(" ")
    @FormUrlEncoded
    fun getAllIngredients(
        @Field("function") function: String = "getAllIngredients",
    ): Call<IngredientListResponse>

    @POST(" ")
    @FormUrlEncoded
    fun searchRecipe(
        @Field("function") function: String = "searchRecipe",
        @Field("ingredientsParam") ingredientsParam: String
    ): Call<RecipeResponse>

    @POST(" ")
    @FormUrlEncoded
    fun getRecipeDetail(
        @Field("function") function: String = "getRecipeDetail",
        @Field("id_recipe") id_recipe: String
    ): Call<RecipeDetailResponse>

    @GET("3/discover/tv")
    fun getTV(
        @Query("api_key") api_key: String = "d41df99ac455e611fbf59daae71c5bf3",
        @Query("language") language: String = "en-US",
        @Query("sort_by") sort_by: String = "popularity.desc",
        @Query("include_adult") include_adult: String = "false",
        @Query("vote_average.gte") param: Int = 8,
        @Query("release_date.gte") parameter: String = "2018-01-01",

        ): Call<ListTVResponse>
}