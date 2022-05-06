package com.gracielo.projectta.data.source.remote.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gracielo.projectta.data.model.*
import com.gracielo.projectta.data.model.addEquipment.EquipmentToolsFromRecipeResponse
import com.gracielo.projectta.data.model.admin.allMembershipTransaction.AllMembershipResponse
import com.gracielo.projectta.data.model.admin.allUser.AllUserResponse
import com.gracielo.projectta.data.model.countIngredients.CountIngredientsResponse
import com.gracielo.projectta.data.model.favouriteRecipe.DataFavRecipe
import com.gracielo.projectta.data.model.favouriteRecipe.FavouriteRecipeResponse
import com.gracielo.projectta.data.model.listEquipment.AllEquipmentResponse
import com.gracielo.projectta.data.model.membershipTransactionHistory.DataMembershipHistoryResponse
import com.gracielo.projectta.data.model.nutrientsHistory.NutrientHistoryResponse
import com.gracielo.projectta.data.model.recipe.detail.RecipeDetailListResponse
import com.gracielo.projectta.data.model.recipe.detail.RecipeDetailResponse
import com.gracielo.projectta.data.model.recipe.detail.RecipeDetailResponseItem
import com.gracielo.projectta.data.model.recipe.searchWithNutrient.SearchRecipeNutrientResponse
import com.gracielo.projectta.data.model.recipe.similar.SimilarRecipeResponse
import com.gracielo.projectta.data.model.recipeCount.CountRecipeResponse
import com.gracielo.projectta.data.model.searchRecipeHistory.SearchRecipeHistoryResponse
import com.gracielo.projectta.data.model.shoppingList.ShoppingListResponse
import com.gracielo.projectta.data.model.userListEquipment.UserListEquipmentResponse
import com.gracielo.projectta.data.source.local.entity.FavouriteRecipeEntity
import com.gracielo.projectta.data.source.local.entity.ShoppingListEntity
import com.gracielo.projectta.data.source.local.entity.UserEntity
import com.gracielo.projectta.data.source.local.entity.UserNutrientsEntity
import com.gracielo.projectta.ui.membership.TransactionMembership
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


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
            activities = dataUserProfile.activities,
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

    fun searchRecipe(ingredientsParam:String , onResult: (RecipeDetailListResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.searchRecipe(
            function = "searchRecipe",
            ingredientsParam = ingredientsParam
        ).enqueue(
            object : Callback<RecipeDetailListResponse> {
                override fun onFailure(call: Call<RecipeDetailListResponse>, t: Throwable) {
                    Log.d("dataapi",t.message.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<RecipeDetailListResponse>, response: Response<RecipeDetailListResponse>) {
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
    fun insertUserSearchRecipe(id_user:String, id_recipe:String,ingredientsList:String,recipe_name:String,  onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.insertUserSearchRecipe(
            function = "insertUserSearchRecipe",
            id_users = id_user,
            ingredients_list = ingredientsList,
            id_Recipe = id_recipe,
            recipe_name = recipe_name

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
    fun InsertMembershipTransaction(transactionMembership: TransactionMembership,  onResult: (NutrientHistoryResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.InsertMembershipTransaction(
            function = "InsertMembershipTransaction",
            id_transaction = transactionMembership.id_transaction,
            id_users = transactionMembership.id_users,
            tanggal = transactionMembership.tanggal,
            nominal = transactionMembership.nominal,
            id_paket = transactionMembership.id_paket,
            status = transactionMembership.status,
            expired= transactionMembership.expired
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
    fun GetUserMembershipTransaction(id_user:String,  onResult: (DataMembershipHistoryResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.GetUserMembershipTransaction(
            function = "GetUserMembershipTransaction",
            id_user = id_user
        ).enqueue(
            object : Callback<DataMembershipHistoryResponse> {
                override fun onFailure(call: Call<DataMembershipHistoryResponse>, t: Throwable) {
                    Log.d("dataapi",t.message.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<DataMembershipHistoryResponse>, response: Response<DataMembershipHistoryResponse>) {
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }
    fun updateUser(dataUser:UserEntity,  onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.updateUser(
            function = "updateUser",
            id_user = dataUser.id,
            age = dataUser.age,
            height = dataUser.height,
            weight = dataUser.weight,
            kalori = dataUser.kalori,
            activities = dataUser.activities
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
    fun getEquipmentFromRecipe(id_Recipe:String,  onResult: (EquipmentToolsFromRecipeResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.getEquipmentFromRecipe(
            function = "getEquipmentFromRecipe",
            id_recipe = id_Recipe,
        ).enqueue(
            object : Callback<EquipmentToolsFromRecipeResponse> {
                override fun onFailure(call: Call<EquipmentToolsFromRecipeResponse>, t: Throwable) {
                    Log.d("dataapi",t.message.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<EquipmentToolsFromRecipeResponse>, response: Response<EquipmentToolsFromRecipeResponse>) {
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }

    fun insertEquipment(name:String,photo:String,  onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.insertEquipment(
            function = "insertEquipment",
            name= name,
            photo = photo,
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

    fun InsertUpdateuserEquipment(id_user:String,listEquipment:String,  onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.InsertUpdateuserEquipment(
            function = "InsertUpdateuserEquipment",
            id_user= id_user,
            list_equipment = listEquipment,
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

    fun getUserSelectedEquipment(id_user:String,  onResult: (UserListEquipmentResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.getUserSelectedEquipment(
            function = "getUserSelectedEquipment",
            id_user=id_user
        ).enqueue(
            object : Callback<UserListEquipmentResponse> {
                override fun onFailure(call: Call<UserListEquipmentResponse>, t: Throwable) {
                    Log.d("dataapi",t.message.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<UserListEquipmentResponse>, response: Response<UserListEquipmentResponse>) {
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }
    fun getAllEquipment(onResult: (AllEquipmentResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.getAllEquipment(
            function = "getAllEquipment",
        ).enqueue(
            object : Callback<AllEquipmentResponse> {
                override fun onFailure(call: Call<AllEquipmentResponse>, t: Throwable) {
                    Log.d("dataapi",t.message.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<AllEquipmentResponse>, response: Response<AllEquipmentResponse>) {
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }
    fun getAllMembershipTransaction(onResult: (AllMembershipResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.getAllMembershipTransaction(
            function = "getAllMembershipTransaction",
        ).enqueue(
            object : Callback<AllMembershipResponse> {
                override fun onFailure(call: Call<AllMembershipResponse>, t: Throwable) {
                    Log.d("dataapi",t.message.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<AllMembershipResponse>, response: Response<AllMembershipResponse>) {
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }
    fun getAllUsers(onResult: (AllUserResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.getAllUsers(
            function = "getAllUsers",
        ).enqueue(
            object : Callback<AllUserResponse> {
                override fun onFailure(call: Call<AllUserResponse>, t: Throwable) {
                    Log.d("dataapi",t.message.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<AllUserResponse>, response: Response<AllUserResponse>) {
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }
    fun tempFuncUpdateStatus(onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.tempFuncUpdateStatus(
            function = "tempFuncUpdateStatus",
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

    fun insertUpdateShoppingList(shoppingList: ShoppingListEntity,  onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.insertUpdateShoppingList(
            function = "insertUpdateShoppingList",
            id_user = shoppingList.id_user,
            id_recipe = shoppingList.id_recipe,
            ingredienst_list = shoppingList.ingredients_list,
            id_shopping_list= shoppingList.id_shopping_list,

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
    fun getShoppingListUser(id_user: String, onResult: (ShoppingListResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.getShoppingListUser(
            function = "getShoppingListUser",
            id_user=id_user
        ).enqueue(
            object : Callback<ShoppingListResponse> {
                override fun onFailure(call: Call<ShoppingListResponse>, t: Throwable) {
                    Log.d("dataapi",t.message.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<ShoppingListResponse>, response: Response<ShoppingListResponse>) {
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }

    fun InsertUpdateFavouriteRecipe(favRecipe: FavouriteRecipeEntity,  onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.InsertUpdateFavouriteRecipe(
            function = "InsertUpdateFavouriteRecipe",
            id_user = favRecipe.id_user,
            id_recipe = favRecipe.id_recipe,
            id_recipe_favourite = favRecipe.id_recipe_favourite

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
    fun getFavoriteRecipeUser(id_user: String):LiveData<ApiResponses<List<DataFavRecipe>>>{
        val resultData = MutableLiveData<ApiResponses<List<DataFavRecipe>>>()
        val retrofit = ApiConfig.provideApiService()
        retrofit.getFavoriteRecipeUser(
            function = "getFavoriteRecipeUser",
            id_user=id_user
        ).enqueue(
            object : Callback<FavouriteRecipeResponse> {
                override fun onFailure(call: Call<FavouriteRecipeResponse>, t: Throwable) {
                    resultData.value = ApiResponses.Error(t.message.toString())
                    Log.e("RemoteDataSource", t.message.toString())
                }
                override fun onResponse(call: Call<FavouriteRecipeResponse>, response: Response<FavouriteRecipeResponse>) {
                    Log.d("dataapi",response.body().toString())
                    val messageResponse = response.body()
                    val dataArray=messageResponse?.dataFavRecipe
                    resultData.value = if (dataArray != null) ApiResponses.Success(dataArray) else ApiResponses.Empty
                }
            }
        )
        return resultData
    }
    fun getRecipeName(id_recipe: String, onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.getRecipeName(
            function = "getRecipeName",
            id_recipe=id_recipe
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

    fun searchRecipeWithNutrients(ingredientsParam: String, maxCalories:Int, minCalories:Int,
                                  maxCarbohydrate:Int, minCarbohydrate:Int, maxProtein:Int,
                                  minProtein:Int, maxSugar:Int, minSugar:Int,
                                  maxFat:Int, minFat:Int, onResult: (SearchRecipeNutrientResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.searchRecipeWithNutrients(
            function = "searchRecipeWithNutrients",
            ingredientsParam = ingredientsParam,
            maxCalories = maxCalories,
            minCalories = minCalories,
            maxCarbohydrate = maxCarbohydrate,
            minCarbohydrate=minCarbohydrate,
            maxProtein = maxProtein,
            minProtein = minProtein,
            maxSugar=maxSugar,
            minSugar=minSugar,
            maxFat=maxFat,
            minFat=minFat
        ).enqueue(
            object : Callback<SearchRecipeNutrientResponse> {
                override fun onFailure(call: Call<SearchRecipeNutrientResponse>, t: Throwable) {
                    Log.d("dataapi",t.message.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<SearchRecipeNutrientResponse>, response: Response<SearchRecipeNutrientResponse>) {
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }
    fun getRecipeCount(onResult: (CountRecipeResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.getRecipeCount(
            function = "getRecipeCount",
        ).enqueue(
            object : Callback<CountRecipeResponse> {
                override fun onFailure(call: Call<CountRecipeResponse>, t: Throwable) {
                    Log.d("dataapi",t.message.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<CountRecipeResponse>, response: Response<CountRecipeResponse>) {
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }
    fun getIngredientsCount(onResult: (CountIngredientsResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.getIngredientsCount(
            function = "getCountBahanMakanan",
        ).enqueue(
            object : Callback<CountIngredientsResponse> {
                override fun onFailure(call: Call<CountIngredientsResponse>, t: Throwable) {
                    Log.d("dataapi",t.message.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<CountIngredientsResponse>, response: Response<CountIngredientsResponse>) {
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }

//    fun getUserSearchtxtFile(){
//        val retrofit = ApiConfig.provideApiService()
//        retrofit.getUserSearchtxtFile().enqueue(
//            object : Callback<ResponseBody> {
//                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                    Log.d("dataapi",t.message.toString())
//                    onResult(null)
//                }
//                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                    val data = response.body()
//                    onResult(data)
//                }
//            }
//        )
//    }



}