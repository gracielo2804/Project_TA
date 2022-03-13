package com.gracielo.projectta.ui.homepage

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.DataUserProfile
import com.gracielo.projectta.data.source.local.entity.UserEntity
import com.gracielo.projectta.databinding.ActivityHomeBinding
import com.gracielo.projectta.viewmodel.UserViewModel
import com.gracielo.projectta.viewmodel.ViewModelFactory
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.gracielo.projectta.data.source.local.entity.Ingredients
import com.gracielo.projectta.ui.ingredients.IngridientsList
import com.gracielo.projectta.ui.setting.SettingActivity
import com.gracielo.projectta.ui.shoppingList.ShoppingListActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
//    var dataUser:DataUser? = null
    var dataUserProfile:DataUserProfile?=null
    var selectedIngredients = mutableListOf<Ingredients>()
    lateinit var dataUser :UserEntity
    lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        binding.progressBar.visibility= View.INVISIBLE
        setContentView(binding.root)
        dataUser = UserEntity("1","1","1","1",1,1,"1",1.1,1)
        viewModel = obtainViewModel(this@HomeActivity)
        viewModel.getUser()?.observeOnce(this){
            dataUser=it
            binding.textUserLoginHome.text= dataUser.name
            binding.txtDashboardKalori.text = "0/${dataUser?.kalori}"

        }

        val fabAdd = binding.fabAdd
        fabAdd.setOnClickListener {
            intent = Intent(this, IngridientsList::class.java)
            startActivity(intent)
        }

        val bottomMenu = binding.bottomnavigationbar
        bottomMenu.selectedItemId=R.id.mHome
        bottomMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mHome -> {
                    //Do Nothing cz this is the activity currently active
                }
                R.id.mShopList -> {
                    val intent = Intent(this,ShoppingListActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                R.id.mSetting -> {
                    val intent = Intent(this,SettingActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                R.id.mPerson -> {

                }
            }
           true
        }

        Log.d("DATABASE",dataUser.toString())
    }

    private fun obtainViewModel(activity: AppCompatActivity): UserViewModel {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(UserViewModel::class.java)
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

//    private val usersObserver = Observer<UserEntity> { users ->
//        dataUser=users
//        binding.textUserLoginHome.text= dataUser?.name
//    }
}