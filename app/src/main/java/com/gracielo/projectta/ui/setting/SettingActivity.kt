package com.gracielo.projectta.ui.setting

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gracielo.projectta.R
import com.gracielo.projectta.data.source.local.entity.UserEntity
import com.gracielo.projectta.databinding.ActivitySettingBinding
import com.gracielo.projectta.ui.history.HistoryHomeActivity
import com.gracielo.projectta.ui.homepage.HomeActivity
import com.gracielo.projectta.ui.ingredients.IngridientsList
import com.gracielo.projectta.ui.login.TestLoginActivity
import com.gracielo.projectta.ui.shoppingList.ShoppingListActivity
import com.gracielo.projectta.util.FunHelper
import com.gracielo.projectta.viewmodel.IngredientsViewModel
import com.gracielo.projectta.viewmodel.UserViewModel

class SettingActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySettingBinding
    private lateinit var rv_setting: RecyclerView
    private lateinit var userViewModel: UserViewModel
    private lateinit var ingredientsViewModel: IngredientsViewModel
    private var settingList = ArrayList<String>()
    val adapters = SettingListAdapter()
    val helper = FunHelper
    private lateinit var dataUser:UserEntity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ingredientsViewModel = helper.obtainIngredientsViewModel(this)
        userViewModel = helper.obtainUserViewModel(this)

        dataUser = UserEntity("1","1","1","1",1,1,"1",1.1,1)

        userViewModel.getUser()?.observeOnce(this){
            dataUser=it
        }

        rv_setting = binding.rvSettingList
        settingList.add("Change Account Information")
        settingList.add("Logout")
        adapters.setData(settingList)

        rv_setting.apply {
            layoutManager= LinearLayoutManager(context)
            adapter = adapters
        }
        adapters.onItemClick = {
            when (it){
                "Logout" ->{
                    AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout from this user ?")
                        .setPositiveButton("Yes"
                        ) { _, _ ->
                            userViewModel.delete(dataUser)
                            helper.deleteAllSelectedIngredients(this)
                            val intent = Intent(this,TestLoginActivity::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        }
                        .setNegativeButton(android.R.string.no, null)
                        .show()
                }
                "Change Account Information" ->{

                }
            }
        }

        val fabAdd = binding.fabAdd
        fabAdd.setOnClickListener {
            intent = Intent(this, IngridientsList::class.java)
            startActivity(intent)
        }

        val bottomMenu = binding.bottomnavigationbar
        bottomMenu.selectedItemId= R.id.mPerson
        bottomMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mHome -> {

                    val intent = Intent(this,HomeActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                R.id.mShopList -> {
                    val intent = Intent(this,ShoppingListActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                R.id.mHistory -> {
                    val intent = Intent(this, HistoryHomeActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                R.id.mPerson -> {
                    //Do Nothing cz this is the activity currently active
//                    val intent = Intent(this,SettingActivity::class.java)
//                    finish()
//                    startActivity(intent)
                }
            }
            true
        }
        supportActionBar?.hide()
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

    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }


}