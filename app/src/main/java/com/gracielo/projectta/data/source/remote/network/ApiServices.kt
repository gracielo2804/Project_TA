package com.gracielo.projectta.data.source.remote.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gracielo.projectta.data.model.*
import com.gracielo.projectta.data.model.nutrientsHistory.NutrientHistoryResponse
import com.gracielo.projectta.data.model.recipe.detail.RecipeDetailResponse
import com.gracielo.projectta.data.model.recipe.detail.RecipeDetailResponseItem
import com.gracielo.projectta.data.model.recipe.search.RecipeResponse
import com.gracielo.projectta.data.model.recipe.similar.SimilarRecipeResponse
import com.gracielo.projectta.data.model.searchRecipeHistory.SearchRecipeHistoryResponse
import com.gracielo.projectta.data.source.local.entity.UserNutrientsEntity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiServices {

    companion object {
        private var INSTANCE: ApiServices? = null
        fun getInstance(): ApiServices? {
            if (INSTANCE == null) INSTANCE =
                ApiServices()
            return INSTANCE
        }
    }

    fun addUser(userData: AddUsers, onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.registerUser(
            username = userData.username,
            function = "adduser",
            email=userData.email,
            password = userData.password,
            name=userData.nama
        ).enqueue(
            object : Callback<MessageResponse> {
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    Log.d("dataapi",response.body().toString())
                    val addedUser = response.body()
                    onResult(addedUser)
                }
            }
        )
    }

    fun Login(username: String,password:String , onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.Login(
            function = "login",
            username = username,
            password = password,
        ).enqueue(
            object : Callback<MessageResponse> {
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    Log.d("dataapi",response.body().toString())
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }

    fun getUserProfileData(id:String , onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.getDataUserProfile(
            function = "getUserProfileData",
            id_users= id
        ).enqueue(
            object : Callback<MessageResponse> {
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    Log.d("dataapi",response.body().toString())
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }

    fun getUserDataByID(id:String , onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.getUserDataByID(
            function = "getUserDataByID",
            id_users= id
        ).enqueue(
            object : Callback<MessageResponse> {
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    Log.d("dataapi",response.body().toString())
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }

    fun addUserProfileData(dataUserProfile: DataUserProfile, onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.addUserProfileData(
            function = "addUserProfileData",
            id_users = dataUserProfile.id_users,
            height = dataUserProfile.height,
            weight = dataUserProfile.weight,
            gender = dataUserProfile.gender,
            age = dataUserProfile.age,
            kalori = dataUserProfile.kalori
        ).enqueue(
            object : Callback<MessageResponse> {
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    Log.d("dataapi",response.body().toString())
                    val addedUser = response.body()
                    onResult(addedUser)
                }
            }
        )
    }
    fun checkUsername(username: String, onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.checkUsername(
            function = "checkUsername",
            username = username
        ).enqueue(
            object : Callback<MessageResponse> {
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    Log.d("dataapi",response.body().toString())
                    val addedUser = response.body()
                    onResult(addedUser)
                }
            }
        )
    }

    fun uploadPicture(function : RequestBody , body: MultipartBody.Part, onResult: (ResponseBody?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.uploadPicture(
            function = function,
            imagePart = body
        ).enqueue(
            object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                   t.printStackTrace()
                    onResult(null)
                }
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("data",response.body()!!.string())
                    val addedUser = response.body()
                    onResult(addedUser)

                }
            }
        )
    }

    fun sendVerificationEmail(id: String,email:String,name:String, onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.sendVerificationEmail(
            function = "testSendEmail",
            id_users = id,
            email_recipient = email,
            recipient_name = name
        ).enqueue(
            object : Callback<MessageResponse> {
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    Log.d("dataapi",response.body().toString())
                    val messageResponse = response.body()
                    onResult(messageResponse)
                }
            }
        )
    }
    fun getAllIngredients() : LiveData<ApiResponses<List<DataIngrdient>>>{
        val resultData = MutableLiveData<ApiResponses<List<DataIngrdient>>>()
        val retrofit = ApiConfig.provideApiService()
        retrofit.getAllIngredients(
            function = "getAllIngredients",
        ).enqueue(
            object : Callback<IngredientListResponse> {
                override fun onFailure(call: Call<IngredientListResponse>, t: Throwable) {
                    resultData.value = ApiResponses.Error(t.message.toString())
                    Log.e("RemoteDataSource", t.message.toString())
                }
                override fun onResponse(call: Call<IngredientListResponse>, response: Response<IngredientListResponse>) {
                    Log.d("dataapi",response.body().toString())
                    val messageResponse = response.body()
                    val dataArray=messageResponse?.dataIngrdients
                    resultData.value = if (dataArray != null) ApiResponses.Success(dataArray) as ApiResponses<List<DataIngrdient>> else ApiResponses.Empty
                }
            }
        )
        return resultData
    }

    fun searchRecipe(ingredientsParam:String , onResult: (RecipeResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.searchRecipe(
            function = "searchRecipe",
            ingredientsParam = ingredientsParam
        ).enqueue(
            object : Callback<RecipeResponse> {
                override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                    Log.d("dataapi",t.message.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
                    val data = response.body()
                    Log.d("dataapi",data.toString())
                    onResult(data)
                }
            }
        )
    }

    fun getRecipeDetail(idRecipe:String , onResult: (RecipeDetailResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.getRecipeDetail(
            function = "getRecipeDetail",
            id_recipe = idRecipe
        ).enqueue(
            object : Callback<RecipeDetailResponse> {
                override fun onFailure(call: Call<RecipeDetailResponse>, t: Throwable) {
                    Log.d("dataapi",t.message.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<RecipeDetailResponse>, response: Response<RecipeDetailResponse>) {
                    val data = response.body()
                    Log.d("dataapi",data.toString())
                    onResult(data)
                }
            }
        )
    }
    fun getSimilarRecipe(idRecipe:String , onResult: (SimilarRecipeResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.getSimilarRecipe(
            function = "getSimilarRecipe",
            id_recipe = idRecipe
        ).enqueue(
            object : Callback<SimilarRecipeResponse> {
                override fun onFailure(call: Call<SimilarRecipeResponse>, t: Throwable) {
                    Log.d("dataapi",t.message.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<SimilarRecipeResponse>, response: Response<SimilarRecipeResponse>) {
                    val data = response.body()
                    Log.d("dataapi",data.toString())
                    onResult(data)
                }
            }
        )
    }
    fun insertRecipeDetail(recipeDetail : RecipeDetailResponseItem,  onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        var listIngredient = ""
        for (i in recipeDetail.extendedIngredients.indices){
            if(i != recipeDetail.extendedIngredients.size-1){
                listIngredient += "${recipeDetail.extendedIngredients[i].name} /? ${recipeDetail.extendedIngredients[i].original} ;-"
            }
            else listIngredient += "${recipeDetail.extendedIngredients[i].name} /? ${recipeDetail.extendedIngredients[i].original}"
        }
        var additionalInfo =""
        if(recipeDetail.diets.isEmpty()){
            additionalInfo =" - "
        }
        else {
            for(i in recipeDetail.diets.indices){
                if(i< recipeDetail.diets.size-1){
                    additionalInfo += "${recipeDetail.diets[i]} - "
                }
                else additionalInfo += recipeDetail.diets[i]
            }
        }
        var calories =0.0
        var fat =0.0
        var carbohydrate =0.0
        var sugar =0.0
        var protein=0.0
        for(i in recipeDetail.nutrition.nutrients.indices){
            val nutrients = recipeDetail.nutrition.nutrients[i]
            if(nutrients.name=="Calories") calories = nutrients.amount
            else if(nutrients.name=="Fat") fat = nutrients.amount
            else if(nutrients.name=="Carbohydrates") carbohydrate = nutrients.amount
            else if(nutrients.name=="Sugar")sugar= nutrients.amount
            else if(nutrients.name=="Protein")protein= nutrients.amount
        }
        retrofit.insertRecipeDetail(
            function = "insertRecipeDetail",
            id_recipe = recipeDetail.id.toString(),
            name = recipeDetail.title,
            ingredients_list = listIngredient,
            recipe_type = additionalInfo,
            calories = calories,
            fat = fat,
            carbohydrate = carbohydrate,
            sugar = sugar,
            protein = protein
        ).enqueue(
            object : Callback<MessageResponse> {
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    Log.d("dataapi",t.message.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }
    fun insertUserSearchRecipe(id_user:String, id_recipe:String,ingredientsList:String,  onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.insertUserSearchRecipe(
            function = "insertUserSearchRecipe",
            id_users = id_user,
            ingredients_list = ingredientsList,
            id_Recipe = id_recipe,

        ).enqueue(
            object : Callback<MessageResponse> {
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    Log.d("dataapi",t.message.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }

    fun saveUserNutrientHistory(userNutrients: UserNutrientsEntity ,  onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        var listIngredient = ""
        var id_user= userNutrients.id
        var calories =userNutrients.kalori_consumed
        var fat =userNutrients.lemak_consumed
        var carbohydrate =userNutrients.karbo_consumed
        var sugar =userNutrients.gula_consumed
        var protein=userNutrients.protein_consumed
        var tanggal =userNutrients.tanggal
        retrofit.saveUserNutrientHistory(
            function = "saveUserNutrientHistory",
            id_users=id_user,
            tanggal = tanggal,
            calories = calories,
            fat = fat,
            carbohydrate = carbohydrate,
            sugar = sugar,
            protein = protein
        ).enqueue(
            object : Callback<MessageResponse> {
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    Log.d("dataapi",t.message.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }

    fun getUserSearchRecipeHistory(id_user:String,  onResult: (SearchRecipeHistoryResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.getUserSearchRecipeHistory(
            function = "getUserSearchRecipeHistory",
            id_users = id_user
        ).enqueue(
            object : Callback<SearchRecipeHistoryResponse> {
                override fun onFailure(call: Call<SearchRecipeHistoryResponse>, t: Throwable) {
                    Log.d("dataapi",t.message.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<SearchRecipeHistoryResponse>, response: Response<SearchRecipeHistoryResponse>) {
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }

    fun getUserNutrientHistory(id_user:String,  onResult: (NutrientHistoryResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.getUserNutrientHistory(
            function = "getUserNutrientHistory",
            id_users = id_user
        ).enqueue(
            object : Callback<NutrientHistoryResponse> {
                override fun onFailure(call: Call<NutrientHistoryResponse>, t: Throwable) {
                    Log.d("dataapi",t.message.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<NutrientHistoryResponse>, response: Response<NutrientHistoryResponse>) {
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }
}