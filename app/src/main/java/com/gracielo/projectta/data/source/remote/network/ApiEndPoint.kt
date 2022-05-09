package com.gracielo.projectta.data.source.remote.network

import com.gracielo.projectta.data.model.*
import com.gracielo.projectta.data.model.listEquipment.AllEquipmentResponse
import com.gracielo.projectta.data.model.addEquipment.EquipmentToolsFromRecipeResponse
import com.gracielo.projectta.data.model.admin.allMembershipTransaction.AllMembershipResponse
import com.gracielo.projectta.data.model.admin.allUser.AllUserResponse
import com.gracielo.projectta.data.model.countIngredients.CountIngredientsResponse
import com.gracielo.projectta.data.model.favouriteRecipe.FavouriteRecipeResponse
import com.gracielo.projectta.data.model.membershipTransactionHistory.DataMembershipHistoryResponse
import com.gracielo.projectta.data.model.nutrientsHistory.NutrientHistoryResponse
import com.gracielo.projectta.data.model.recipe.detail.RecipeDetailListResponse
import com.gracielo.projectta.data.model.recipe.detail.RecipeDetailResponse
import com.gracielo.projectta.data.model.recipe.search.RecipeResponse
import com.gracielo.projectta.data.model.recipe.search.RecipeResponseItem
import com.gracielo.projectta.data.model.recipe.searchWithNutrient.SearchRecipeNutrientResponse
import com.gracielo.projectta.data.model.recipe.similar.SimilarRecipeResponse
import com.gracielo.projectta.data.model.recipeCount.CountRecipeResponse
import com.gracielo.projectta.data.model.searchRecipeHistory.SearchRecipeHistoryResponse
import com.gracielo.projectta.data.model.shoppingList.ShoppingListResponse
import com.gracielo.projectta.data.model.userListEquipment.UserListEquipmentResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiEndPoint {

    @POST("master.php")
    @FormUrlEncoded
    fun registerUser(
        @Field("function") function: String? = "adduser",
        @Field("email") email: String?,
        @Field("password") password: String? ,
        @Field("name") name: String?,
        @Field("username") username: String?
    ): Call<MessageResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun Login(
        @Field("function") function: String? = "login",
        @Field("username") username: String?,
        @Field("password") password: String?,
    ): Call<MessageResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun getDataUserProfile(
        @Field("function") function: String? = "login",
        @Field("id_users") id_users: String?,
    ): Call<MessageResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun getUserDataByID(
        @Field("function") function: String? = "getUserDataByID",
        @Field("id_users") id_users: String?,
    ): Call<MessageResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun addUserProfileData(
        @Field("function") function: String = "addUserProfileData",
        @Field("id_users") id_users: String,
        @Field("height") height : Int,
        @Field("weight") weight : Int,
        @Field("age") age : Int,
        @Field("activities") activities : Int,
        @Field("gender") gender : String? ,
        @Field("kalori") kalori: Double
    ): Call<MessageResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun sendVerificationEmail(
        @Field("function") function: String = "testSendEmail",
        @Field("id_users") id_users: String,
        @Field("email_recipient") email_recipient : String,
        @Field("recipient_name") recipient_name : String
    ): Call<MessageResponse>

    @Multipart
    @POST("master.php")
    fun uploadPicture(
        @Part("function") function: RequestBody,
        @Part imagePart: MultipartBody.Part
    ): Call<ResponseBody>

//    @Headers("Content-Type: application/json")
//    @POST("master.php")
//    fun registerUser(
//        @Body userData:AddUsers
//    ): Call<AddUsers>

    @POST("master.php")
    @FormUrlEncoded
    fun checkUsername(
        @Field("function") function: String = "checkUsername",
        @Field("username") username: String,
    ): Call<MessageResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun getAllIngredients(
        @Field("function") function: String = "getAllIngredients",
    ): Call<IngredientListResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun searchRecipe(
        @Field("function") function: String = "searchRecipe",
        @Field("ingredientsParam") ingredientsParam: String
    ): Call<RecipeDetailListResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun getRecipeDetail(
        @Field("function") function: String = "getRecipeDetail",
        @Field("id_recipe") id_recipe: String
    ): Call<RecipeDetailResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun getSimilarRecipe(
        @Field("function") function: String = "getSimilarRecipe",
        @Field("id_recipe") id_recipe: String
    ): Call<SimilarRecipeResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun insertRecipeDetail(
        @Field("function") function: String = "insertRecipeDetail",
        @Field("id_recipe") id_recipe: String,
        @Field("name") name : String,
        @Field("ingredients_list") ingredients_list : String,
        @Field("recipe_type") recipe_type : String,
        @Field("calories") calories : Double ,
        @Field("fat") fat: Double,
        @Field("carbohydrate") carbohydrate: Double,
        @Field("sugar") sugar: Double,
        @Field("protein") protein: Double
    ): Call<MessageResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun insertUserSearchRecipe(
        @Field("function") function: String = "insertUserSearchRecipe",
        @Field("id_user") id_users: String,
        @Field("id_recipe") id_Recipe: String,
        @Field("ingredients_list") ingredients_list : String,
        @Field("recipe_name") recipe_name : String,
    ): Call<MessageResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun saveUserNutrientHistory(
        @Field("function") function: String = "insertRecipeDetail",
        @Field("id_users") id_users: String,
        @Field("tanggal") tanggal : String,
        @Field("calories") calories : Double ,
        @Field("fat") fat: Double,
        @Field("carbohydrate") carbohydrate: Double,
        @Field("sugar") sugar: Double,
        @Field("protein") protein: Double
    ): Call<MessageResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun getUserSearchRecipeHistory(
        @Field("function") function: String = "getUserSearchRecipeHistory",
        @Field("id_users") id_users: String,
    ): Call<SearchRecipeHistoryResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun getUserNutrientHistory(
        @Field("function") function: String = "getUserNutrientHistory",
        @Field("id_users") id_users: String,
    ): Call<NutrientHistoryResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun InsertMembershipTransaction(
        @Field("function") function: String = "InsertMembershipTransaction",
        @Field("id_transaksi") id_transaction: String,
        @Field("id_users") id_users: String,
        @Field("tanggal") tanggal : String,
        @Field("nominal") nominal : Int ,
        @Field("id_paket") id_paket: Int,
        @Field("status") status: Int,
        @Field("expired") expired: String,
    ): Call<NutrientHistoryResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun GetUserMembershipTransaction(
        @Field("function") function: String = "GetUserMembershipTransaction",
        @Field("id_user") id_user: String,
    ): Call<DataMembershipHistoryResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun updateUser(
        @Field("function") function: String = "updateUser",
        @Field("id_user") id_user: String,
        @Field("age")age:Int,
        @Field("height")height:Int,
        @Field("weight")weight: Int,
        @Field("kalori")kalori:Double,
        @Field("activities")activities:Int,
    ): Call<MessageResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun getEquipmentFromRecipe(
        @Field("function") function: String = "getEquipmentFromRecipe",
        @Field("id_recipe") id_recipe: String,
    ): Call<EquipmentToolsFromRecipeResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun insertEquipment(
        @Field("function") function: String = "insertEquipment",
        @Field("equipment_name") name: String,
        @Field("equipment_photo")photo:String
    ): Call<MessageResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun InsertUpdateuserEquipment(
        @Field("function") function: String = "InsertUpdateuserEquipment",
        @Field("id_user") id_user: String,
        @Field("list_equipment")list_equipment:String
    ): Call<MessageResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun getUserSelectedEquipment(
        @Field("function") function: String = "getUserSelectedEquipment",
        @Field("id_user") id_user: String,
    ): Call<UserListEquipmentResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun getAllEquipment(
        @Field("function") function: String = "getAllEquipment",
    ): Call<AllEquipmentResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun getAllMembershipTransaction(
        @Field("function") function: String = "getAllMembershipTransaction",
    ): Call<AllMembershipResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun getAllUsers(
        @Field("function") function: String = "getAllUsers",
    ): Call<AllUserResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun tempFuncUpdateStatus(
        @Field("function") function: String = "tempFuncUpdateStatus",
    ): Call<MessageResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun insertUpdateShoppingList(
        @Field("function") function: String = "insertUpdateShoppingList",
        @Field("id_user")id_user:String,
        @Field("id_recipe")id_recipe:String,
        @Field("ingredienst_list")ingredienst_list:String,
        @Field("id_shopping_list")id_shopping_list:String,
    ): Call<MessageResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun getShoppingListUser(
        @Field("function") function: String = "getShoppingListUser",
        @Field("id_user")id_user:String,
    ): Call<ShoppingListResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun InsertUpdateFavouriteRecipe(
        @Field("function") function: String = "InsertUpdateFavouriteRecipe",
        @Field("id_user")id_user:String,
        @Field("id_recipe")id_recipe:String,
        @Field("id_recipe_favourite")id_recipe_favourite:String,
    ): Call<MessageResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun getFavoriteRecipeUser(
        @Field("function") function: String = "getFavoriteRecipeUser",
        @Field("id_user")id_user:String,
    ): Call<FavouriteRecipeResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun getRecipeName(
        @Field("function") function: String = "getRecipeName",
        @Field("id_recipe")id_recipe:String,
    ): Call<MessageResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun searchRecipeWithNutrients(
        @Field("function") function: String = "searchRecipeWithNutrients",
        @Field("ingredientsParam") ingredientsParam: String,
        @Field("maxCalories")maxCalories:Int,
        @Field("minCalories")minCalories:Int,
        @Field("maxCarbohydrate")maxCarbohydrate:Int,
        @Field("minCarbohydrate")minCarbohydrate:Int,
        @Field("maxProtein")maxProtein:Int,
        @Field("minProtein")minProtein:Int,
        @Field("maxSugar")maxSugar:Int,
        @Field("minSugar")minSugar:Int,
        @Field("maxFat")maxFat:Int,
        @Field("minFat")minFat:Int,
        
    ): Call<SearchRecipeNutrientResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun getRecipeCount(
        @Field("function") function: String = "getRecipeCount",
    ): Call<CountRecipeResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun getIngredientsCount(
        @Field("function") function: String = "getCountBahanMakanan",
    ): Call<CountIngredientsResponse>

    @Streaming
    @GET("dataIngredient.txt")
    fun getUserSearchtxtFile():Call<ResponseBody>

    @POST("master.php")
    @FormUrlEncoded
    fun sendOTPForgotPass(
        @Field("function") function: String = "sendOTPForgotPass",
        @Field("username") username: String ,
        @Field("email") email: String,

    ): Call<MessageResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun verifyResetOTP(
        @Field("function") function: String = "verifyResetOTP",
        @Field("username") username: String ,
        @Field("email") email: String,
        @Field("otp") otp: String,
    ): Call<MessageResponse>

    @POST("master.php")
    @FormUrlEncoded
    fun resetPassword(
        @Field("function") function: String = "resetPassword",
        @Field("username") username: String ,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<MessageResponse>


}