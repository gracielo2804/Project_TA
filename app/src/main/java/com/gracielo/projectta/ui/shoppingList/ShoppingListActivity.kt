package com.gracielo.projectta.ui.shoppingList

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gracielo.projectta.R
import com.gracielo.projectta.data.source.local.entity.ShoppingListEntity
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityShoppingListBinding
import com.gracielo.projectta.ui.history.HistoryHomeActivity
import com.gracielo.projectta.ui.homepage.HomeActivity
import com.gracielo.projectta.ui.ingredients.IngridientsList
import com.gracielo.projectta.ui.setting.SettingActivity
import com.gracielo.projectta.util.FunHelper.observeOnce
import com.gracielo.projectta.viewmodel.ShoppingListViewModel
import com.gracielo.projectta.viewmodel.UserViewModel
import com.gracielo.projectta.viewmodel.ViewModelFactory
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.Executors


class ShoppingListActivity : AppCompatActivity(),PDFUtilityShoppingList.OnDocumentClose,ShoppingListCallback {

    private lateinit var binding : ActivityShoppingListBinding
    lateinit var viewModel: UserViewModel
    lateinit var shoppingListViewModel: ShoppingListViewModel
    private var idUser = ""
    private var listRecipe = mutableListOf<ShoppingListEntity>()
    private var listIngredients = mutableMapOf<String,List<String>>()
    private var listIngredientsDetail = mutableMapOf<String,List<String>>()

    private var listRecipeSearch = mutableListOf<ShoppingListEntity>()
    private var listIngredientsSearch = mutableMapOf<String,List<String>>()
    private var listIngredientsDetailSearch = mutableMapOf<String,List<String>>()

    private val adapters = ShoppingListRecipeAdapter(this)
    private var apiServices = ApiServices()
    private var listRecipeName = ArrayList<String>()
    private var listImageRecipe = mutableListOf<ByteArray>()
    lateinit var rvShoppingList : RecyclerView
//    private var shoppingList = mutableMapOf<String,List<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        rvShoppingList = binding.rvShoppingList
        viewModel = obtainUserViewModel(this)
        shoppingListViewModel = obtainShoppingListViewModel(this)
        viewModel.getUser().observe(this){ it ->
            idUser = it.id
            shoppingListViewModel.getShoppingList(idUser).observeOnce(this){
                if(!it.isNullOrEmpty()){
                    Log.d("Entity Shopping List",it.toString())
                    rvShoppingList.visibility= View.VISIBLE
                    binding.txtShoppingEmpty.visibility=View.INVISIBLE
                    Log.d("ShoppingList",it[0].ingredients_list)
                    for(j in it.indices){
                        listRecipe.add(it[j])
                        var rawString = it[j].ingredients_list
                        val listIngredientsSplit = mutableListOf<String>()
                        val listIngredientsDetailSplit = mutableListOf<String>()
                        var ingredientsSplit = rawString.split(";-")
                        ingredientsSplit.forEach {its->
                            val ingredientsValue = its.split(" /? ")
                            listIngredientsSplit.add(ingredientsValue[0])
                            listIngredientsDetailSplit.add(ingredientsValue[1])
                        }
                        listIngredients.put(it[j].recipe_name,listIngredientsSplit)
                        listIngredientsDetail.put(it[j].recipe_name,listIngredientsDetailSplit)
                        Log.d("Link Image","https://spoonacular.com/recipeImages/${it[j].id_recipe}-556x370.jpg")

                        var bitmap:Bitmap?=null
                        Executors.newSingleThreadExecutor().execute {
                            bitmap = Glide
                                .with(this).asBitmap()
                                .load("https://spoonacular.com/recipeImages/${it[j].id_recipe}-556x370.jpg")
                                .submit(100, 100).get()
                            val baos = ByteArrayOutputStream()
                            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                            val imageInByte: ByteArray = baos.toByteArray()
                            listImageRecipe.add(imageInByte)
                        }
                    }
                }
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            adapters.setData(listRecipe,listRecipeName,listIngredients,listIngredientsDetail)
            rvShoppingList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = adapters
            }
        },1000)
        val fabAdd = binding.fabAdd
        fabAdd.setOnClickListener {
            intent = Intent(this, IngridientsList::class.java)
            startActivity(intent)
        }
        val bottomMenu = binding.bottomnavigationbar
        bottomMenu.selectedItemId= R.id.mShopList
        bottomMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mHome -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                R.id.mShopList -> {
                    //Do Nothing cz this is the activity currently active
//                        val intent = Intent(this, ShoppingListActivity::class.java)
//                        finish()
//                        startActivity(intent)
                }
                R.id.mHistory -> {
                    val intent = Intent(this, HistoryHomeActivity::class.java)
                    finish()
                    startActivity(intent)

                }
                R.id.mPerson -> {
                    val intent = Intent(this, SettingActivity::class.java)
                    finish()
                    startActivity(intent)
                }
            }
            true
        }
//            binding.imageView6.setOnClickListener {
//                finish()
//            }
        withDelay(1000){

        }


        val items = listOf("Recipe", "Ingredients")
        val adapter = ArrayAdapter(this, R.layout.list_item_dropdown_search_shopping_list, items)
        binding.dropDownAutoTextSearchByShoppingList.setAdapter(adapter)



        binding.btnSearchShoppingList.setOnClickListener {
            listIngredientsSearch.clear()
            listIngredientsDetailSearch.clear()
            listRecipeSearch.clear()
            var dropdownvalue = binding.dropDownAutoTextSearchByShoppingList.text.toString()
            var searchtext = binding.fieldSearchShoppingList.text.toString()
            olahDataSearch(dropdownvalue,searchtext)

//            if(dropdownvalue=="Recipe"){
//                if(searchtext!=""){
//                    listRecipe.forEach {
//                        if(it.recipe_name.contains(searchtext,true))listRecipeSearch.add(it)
//                    }
//                    if(listRecipeSearch.size==0){
//                        Log.d("SearchShoppingList","Masuk Empty")
//                        binding.txtShoppingEmpty.text = "No Recipe Data Found"
//                        binding.txtShoppingEmpty.visibility = View.VISIBLE
//                        binding.rvShoppingList.visibility = View.INVISIBLE
//                    }
//                    else{
//                        binding.txtShoppingEmpty.visibility = View.INVISIBLE
//                        binding.rvShoppingList.visibility = View.VISIBLE
//                        Log.d("SearchShoppingList","Masuk 2")
//                        for(j in listRecipeSearch.indices){
//                            listRecipe.add(listRecipeSearch[j])
//                            var rawString = listRecipeSearch[j].ingredients_list
//                            val listIngredientsSplit = mutableListOf<String>()
//                            val listIngredientsDetailSplit = mutableListOf<String>()
//                            var ingredientsSplit = rawString.split(";-")
//                            ingredientsSplit.forEach {its->
//                                val ingredientsValue = its.split(" /? ")
//                                listIngredientsSplit.add(ingredientsValue[0])
//                                listIngredientsDetailSplit.add(ingredientsValue[1])
//                            }
//                            listIngredientsSearch.put(listRecipeSearch[j].recipe_name,listIngredientsSplit)
//                            listIngredientsDetailSearch.put(listRecipeSearch[j].recipe_name,listIngredientsDetailSplit)
//                            Log.d("Link Image","https://spoonacular.com/recipeImages/${listRecipeSearch[j].id_recipe}-556x370.jpg")
//
//                            var bitmap:Bitmap?=null
//                            Executors.newSingleThreadExecutor().execute {
//                                bitmap = Glide
//                                    .with(this).asBitmap()
//                                    .load("https://spoonacular.com/recipeImages/${listRecipeSearch[j].id_recipe}-556x370.jpg")
//                                    .submit(100, 100).get()
//                                val baos = ByteArrayOutputStream()
//                                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//                                val imageInByte: ByteArray = baos.toByteArray()
//                                listImageRecipe.add(imageInByte)
//                            }
//                        }
//                        adapters.setData(listRecipeSearch,listRecipeName,listIngredientsSearch,listIngredientsDetailSearch)
//                    }
//                }
//                else{
//                    binding.txtShoppingEmpty.visibility = View.INVISIBLE
//                    binding.rvShoppingList.visibility = View.VISIBLE
//                    adapters.setData(listRecipe,listRecipeName,listIngredientsSearch,listIngredientsDetailSearch)
//                }
//            }
//            else if(dropdownvalue=="Ingredients"){
//                if(searchtext!=""){
//                    listRecipe.forEach {
//                        if(it.ingredients_list.contains(searchtext,true))listRecipeSearch.add(it)
//                    }
//                    if(listRecipeSearch.size==0){
//                        Log.d("SearchShoppingList","Masuk Empty")
//                        binding.txtShoppingEmpty.text = "No Recipe Data Found"
//                        binding.txtShoppingEmpty.visibility = View.VISIBLE
//                        binding.rvShoppingList.visibility = View.INVISIBLE
//                    }
//                    else{
//                        binding.txtShoppingEmpty.visibility = View.INVISIBLE
//                        binding.rvShoppingList.visibility = View.VISIBLE
//                        Log.d("SearchShoppingList","Masuk 2")
//                        for(j in listRecipeSearch.indices){
//                            listRecipe.add(listRecipeSearch[j])
//                            var rawString = listRecipeSearch[j].ingredients_list
//                            val listIngredientsSplit = mutableListOf<String>()
//                            val listIngredientsDetailSplit = mutableListOf<String>()
//                            var ingredientsSplit = rawString.split(";-")
//                            ingredientsSplit.forEach {its->
//                                val ingredientsValue = its.split(" /? ")
//                                listIngredientsSplit.add(ingredientsValue[0])
//                                listIngredientsDetailSplit.add(ingredientsValue[1])
//                            }
//                            listIngredientsSearch.put(listRecipeSearch[j].recipe_name,listIngredientsSplit)
//                            listIngredientsDetailSearch.put(listRecipeSearch[j].recipe_name,listIngredientsDetailSplit)
//                            Log.d("Link Image","https://spoonacular.com/recipeImages/${listRecipeSearch[j].id_recipe}-556x370.jpg")
//
//                            var bitmap:Bitmap?=null
//                            Executors.newSingleThreadExecutor().execute {
//                                bitmap = Glide
//                                    .with(this).asBitmap()
//                                    .load("https://spoonacular.com/recipeImages/${listRecipeSearch[j].id_recipe}-556x370.jpg")
//                                    .submit(100, 100).get()
//                                val baos = ByteArrayOutputStream()
//                                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//                                val imageInByte: ByteArray = baos.toByteArray()
//                                listImageRecipe.add(imageInByte)
//                            }
//                        }
//                        adapters.setData(listRecipeSearch,listRecipeName,listIngredientsSearch,listIngredientsDetailSearch)
//                    }
//                }
//                else{
//                    binding.txtShoppingEmpty.visibility = View.INVISIBLE
//                    binding.rvShoppingList.visibility = View.VISIBLE
//                    adapters.setData(listRecipe,listRecipeName,listIngredientsSearch,listIngredientsDetailSearch)
//                }
//            }
        }

        binding.openShoppingListMenu.setOnClickListener {
            showPopup(it)
        }

        supportActionBar?.hide()
    }

    fun olahDataSearch(tipe:String,searchtext:String){
        if(searchtext!=""){
            if(tipe=="Recipe"){
                listRecipe.forEach {
                    if(it.recipe_name.contains(searchtext,true))listRecipeSearch.add(it)
                }
            }
            else if(tipe=="Ingredients"){
                listRecipe.forEach {
                    if(it.ingredients_list.contains(searchtext,true))listRecipeSearch.add(it)
                }
            }

            if(listRecipeSearch.size==0){
                Log.d("SearchShoppingList","Masuk Empty")
                binding.txtShoppingEmpty.text = "No Recipe Data Found"
                binding.txtShoppingEmpty.visibility = View.VISIBLE
                binding.rvShoppingList.visibility = View.INVISIBLE
            }
            else{
                binding.txtShoppingEmpty.visibility = View.INVISIBLE
                binding.rvShoppingList.visibility = View.VISIBLE
                Log.d("SearchShoppingList","Masuk 2")
                for(j in listRecipeSearch.indices){
                    listRecipe.add(listRecipeSearch[j])
                    var rawString = listRecipeSearch[j].ingredients_list
                    val listIngredientsSplit = mutableListOf<String>()
                    val listIngredientsDetailSplit = mutableListOf<String>()
                    var ingredientsSplit = rawString.split(";-")
                    ingredientsSplit.forEach {its->
                        val ingredientsValue = its.split(" /? ")
                        listIngredientsSplit.add(ingredientsValue[0])
                        listIngredientsDetailSplit.add(ingredientsValue[1])
                    }
                    listIngredientsSearch.put(listRecipeSearch[j].recipe_name,listIngredientsSplit)
                    listIngredientsDetailSearch.put(listRecipeSearch[j].recipe_name,listIngredientsDetailSplit)
                    Log.d("Link Image","https://spoonacular.com/recipeImages/${listRecipeSearch[j].id_recipe}-556x370.jpg")

                    var bitmap:Bitmap?=null
                    Executors.newSingleThreadExecutor().execute {
                        bitmap = Glide
                            .with(this).asBitmap()
                            .load("https://spoonacular.com/recipeImages/${listRecipeSearch[j].id_recipe}-556x370.jpg")
                            .submit(100, 100).get()
                        val baos = ByteArrayOutputStream()
                        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val imageInByte: ByteArray = baos.toByteArray()
                        listImageRecipe.add(imageInByte)
                    }
                }
                adapters.setData(listRecipeSearch,listRecipeName,listIngredientsSearch,listIngredientsDetailSearch)
            }
        }
        else{
            binding.txtShoppingEmpty.visibility = View.INVISIBLE
            binding.rvShoppingList.visibility = View.VISIBLE
            adapters.setData(listRecipe,listRecipeName,listIngredientsSearch,listIngredientsDetailSearch)
        }
    }

    private fun obtainUserViewModel(activity: AppCompatActivity): UserViewModel {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(UserViewModel::class.java)
    }

    private fun obtainShoppingListViewModel(activity: AppCompatActivity): ShoppingListViewModel {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(ShoppingListViewModel::class.java)
    }

    fun withDelay(delay : Long, block : () -> Unit) {
        Handler().postDelayed(Runnable(block), delay)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit the application ?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton("Yes"
            ) { _, _ ->
//                Toast.makeText(this,"Successfully Logout",Toast.LENGTH_SHORT).show()
                finishAffinity()
//                viewModel.getUser()?.removeObserver(){}

            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no, null)
//            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun showPopup(v: View) {
        PopupMenu(this, v).apply {
            setOnMenuItemClickListener(object: PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    return when (item?.itemId) {
                        R.id.item_ingredients_recipe_export -> {
                            val pdfMaker = PDFUtilityShoppingList
                            val current = LocalDateTime.now()
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                            val formatted = current.format(formatter)
                            println("Current Date and Time is: $formatted")
                            val path =
                                "${getExternalFilesDir(null)}${File.separator} Shopping List Report.pdf"
                            try {
                                pdfMaker.setImage(listImageRecipe)
                                pdfMaker.setCreatedDate(formatted)
                                pdfMaker.createPdf(
                                    v.context,
                                    this@ShoppingListActivity,
                                    getData(),
                                    path,
                                    true
                                )

                            } catch (e: Exception) {
                                e.printStackTrace()
                                Log.e("PDFNEW", "Error Creating Pdf")
                                Toast.makeText(v.context, "Error Creating Pdf", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            true
                        }
                        else -> false
                    }
                }

            })
            inflate(R.menu.menu_admin_ingredients_recipe)
            show()
        }
    }
    private fun getData(): List<Array<String>> {
        var count = listRecipe.size
        val temp: MutableList<Array<String>> = java.util.ArrayList()
        for (i in 0 until count) {
            val data = listRecipe[i]
            Log.d("ListIngrediets","${data.recipe_name} - ${listIngredients[data.recipe_name].toString()}")
            val dataIngredients = listIngredients[data.recipe_name]
            val dataIngredientsDetil = listIngredientsDetail[data.recipe_name]
            var ingredientsString = ""
            for (i in dataIngredients!!.indices){
                ingredientsString += "-${dataIngredients[i]}\n${dataIngredientsDetil!![i]}\n"
            }
            temp.add(arrayOf(data.recipe_name,ingredientsString))
        }
        return temp

    }

    private fun renderPdf(context: Context, filePath: File) {
        val uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            filePath
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, "application/pdf")

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {

        }
    }

    override fun onPDFDocumentClose(file: File?) {
        Toast.makeText(this,"Pdf Created",Toast.LENGTH_SHORT).show()
        renderPdf(this,file!!)
    }

    override fun onButtonDeleteClicked(recipeData: ShoppingListEntity) {
        apiServices.deleteShoppingListUser(recipeData.id_shopping_list){
            if(it?.code==1){
                shoppingListViewModel.delete(recipeData)
                Toast.makeText(this,"Recipe Deleted From Shopping List",Toast.LENGTH_SHORT).show()
                recreate()
                Handler(Looper.getMainLooper()).postDelayed({
                    shoppingListViewModel.getShoppingList(idUser).observeOnce(this){
                        if(!it.isNullOrEmpty()){
                            Log.d("Entity Shopping List",it.toString())
                            rvShoppingList.visibility= View.VISIBLE
                            binding.txtShoppingEmpty.visibility=View.INVISIBLE
                            Log.d("ShoppingList",it[0].ingredients_list)
                            for(j in it.indices){
                                listRecipe.add(it[j])
                                var rawString = it[j].ingredients_list
                                val listIngredientsSplit = mutableListOf<String>()
                                val listIngredientsDetailSplit = mutableListOf<String>()
                                var ingredientsSplit = rawString.split(";-")
                                ingredientsSplit.forEach {its->
                                    val ingredientsValue = its.split(" /? ")
                                    listIngredientsSplit.add(ingredientsValue[0])
                                    listIngredientsDetailSplit.add(ingredientsValue[1])
                                }
                                listIngredients.put(it[j].recipe_name,listIngredientsSplit)
                                listIngredientsDetail.put(it[j].recipe_name,listIngredientsDetailSplit)
                                Log.d("Link Image","https://spoonacular.com/recipeImages/${it[j].id_recipe}-556x370.jpg")

                                var bitmap:Bitmap?=null
                                Executors.newSingleThreadExecutor().execute {
                                    bitmap = Glide
                                        .with(this).asBitmap()
                                        .load("https://spoonacular.com/recipeImages/${it[j].id_recipe}-556x370.jpg")
                                        .submit(100, 100).get()
                                    val baos = ByteArrayOutputStream()
                                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                                    val imageInByte: ByteArray = baos.toByteArray()
                                    listImageRecipe.add(imageInByte)
                                }
                            }
                        }
                    }
                },500)
            }
        }


    }
}

