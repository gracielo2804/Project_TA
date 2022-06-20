package com.gracielo.projectta.ui.homepage

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.gracielo.projectta.R
import com.gracielo.projectta.data.source.local.entity.UserEntity
import com.gracielo.projectta.data.source.local.entity.UserNutrientsEntity
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityAddNutrientsBinding
import com.gracielo.projectta.ui.recipe.RecipeDetailActivity
import com.gracielo.projectta.util.FunHelper
import com.gracielo.projectta.util.FunHelper.observeOnce
import com.gracielo.projectta.viewmodel.UserViewModel
import com.jakewharton.threetenabp.AndroidThreeTen

class AddNutrientsActivity : AppCompatActivity() {

    private lateinit var binding:ActivityAddNutrientsBinding
    lateinit var dataUser : UserEntity
    lateinit var viewModel: UserViewModel
    val apiServices = ApiServices()
    private val helper = FunHelper
    private var dataUserNutrients: UserNutrientsEntity? = null

    companion object
    {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "TugasAkhir channel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNutrientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AndroidThreeTen.init(this)
        dataUser = UserEntity("1","1","1","1","0","",1,1,"1",1.1,1,1)
        viewModel = helper.obtainUserViewModel(this)
        viewModel.getUser().observeOnce(this) {
            dataUser=it
        }
        viewModel.getUserNutrients().observe(this){
            dataUserNutrients=it
        }
        binding.btnSaveNutrientAdd.setOnClickListener{
            AlertDialog.Builder(this)
                .setTitle("Add Nutrients Consumption ?")
                .setMessage("Are you sure you want to add your nutrition consumption with the data you have entered ?") // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Yes"
                ) { _, _ ->
                    val pass = addNutrient()
                    Log.d("pass",pass.toString())
                    if(pass){
                        val intentt = Intent(this,HomeActivity::class.java)
                        finish()
                        startActivity(intentt)
                    }
                } // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
//            .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }

    override fun onBackPressed() {
        val pass = addNutrient()
        Log.d("pass",pass.toString())
        if(pass){
            val intentt = Intent(this,HomeActivity::class.java)
            finish()
            startActivity(intentt)
        }
    }

    fun addNutrient():Boolean {
        var returnpass=false
        var fatTemp = 0.0;
        var carbohydrateTemp = 0.0;
        var sugarTemp = 0.0
        var caloriesTemp = 0.0;
        var proteintemp = 0.0
        var fat = binding.etAddFatAdd.text.toString().toInt()
        var carbohydrate = binding.etAddCarbohydrateAdd.text.toString().toInt()
        var sugar = binding.etAddSugarAdd.text.toString().toInt()
        var calories = binding.etAddCaloriesAdd.text.toString().toInt()
        var protein = binding.etAddProteinAdd.text.toString().toInt()

        var showDialogBool = false
        var overFat = false;
        var overCarbohydrate = false;
        var overCalories = false
        var overProtein = false;
        var overSugar = false
        if (dataUserNutrients != null) {
            fatTemp = dataUserNutrients!!.lemak_consumed
            if(fat>0){
                fatTemp += + fat
                if (fatTemp > dataUserNutrients!!.maxLemak) {
                    overFat = true
                    showDialogBool = true
                }
            }

            carbohydrateTemp = dataUserNutrients!!.karbo_consumed
            if(carbohydrate>0){
                carbohydrateTemp +=  + carbohydrate
                if (carbohydrateTemp > dataUserNutrients!!.maxKarbo) {
                    overCarbohydrate = true
                    showDialogBool = true
                }
            }

            sugarTemp = dataUserNutrients!!.gula_consumed
            if(sugar>0){
                sugarTemp += sugar
                if (sugarTemp > dataUserNutrients!!.maxGula) {
                    overSugar = true
                    showDialogBool = true
                }
            }

            proteintemp = dataUserNutrients!!.protein_consumed
            if(protein>0){
                proteintemp += protein
                if (proteintemp > dataUserNutrients!!.maxProtein) {
                    overProtein = true
                    showDialogBool = true
                }
            }

            caloriesTemp = dataUserNutrients!!.kalori_consumed
            if(calories>0){
                 caloriesTemp+= calories
                if (caloriesTemp > dataUserNutrients!!.maxKalori) {
                    overCalories = true
                    showDialogBool = true
                }
            }

            var message = ""
            if (showDialogBool) {
                var messageTemp = "There Will Be Excess "
                if (overCalories) messageTemp += "Calories, "
                if (overFat) messageTemp += "Fat, "
                if (overCarbohydrate) messageTemp += "Carbohydrate, "
                if (overSugar) messageTemp += "Sugar, "
                if (overProtein) messageTemp += "Protein, "
                var messageLength = messageTemp.length
                for (i in messageTemp.indices) {
                    if (i < messageLength - 2) {
                        message += messageTemp[i]
                    }
                }
                message += ". Still Continue To eat ?"
                AlertDialog.Builder(this)
                    .setTitle("Excess Nutrients to be Consumed ")
                    .setMessage(message) // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("Yes") { _, _ ->
                        dataUserNutrients!!.lemak_consumed =
                            String.format("%.2f", fatTemp).toDouble()
                        dataUserNutrients!!.karbo_consumed =
                            String.format("%.2f", carbohydrateTemp).toDouble()
                        dataUserNutrients!!.gula_consumed =
                            String.format("%.2f", sugarTemp).toDouble()
                        dataUserNutrients!!.kalori_consumed =
                            String.format("%.2f", caloriesTemp).toDouble()
                        dataUserNutrients!!.protein_consumed =
                            String.format("%.2f", proteintemp).toDouble()
                        viewModel.updateUserNutrients(dataUserNutrients!!)
                        if (overCalories) sendNotification("Calories", 20001)
                        if (overFat) sendNotification("Fat", 20002)
                        if (overCarbohydrate) sendNotification("Carbohydrate", 20003)
                        if (overSugar) sendNotification("Sugar", 20004)
                        if (overProtein) sendNotification("Protein", 20005)
                        val intentt = Intent(this,HomeActivity::class.java)
                        finish()
                        startActivity(intentt)
                    } // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no){ _, _ ->
                    }
//            .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }
            else{
                dataUserNutrients!!.lemak_consumed = String.format("%.2f", fatTemp).toDouble()
                dataUserNutrients!!.karbo_consumed = String.format("%.2f", carbohydrateTemp).toDouble()
                dataUserNutrients!!.gula_consumed = String.format("%.2f", sugarTemp).toDouble()
                dataUserNutrients!!.kalori_consumed = String.format("%.2f", caloriesTemp).toDouble()
                dataUserNutrients!!.protein_consumed = String.format("%.2f", proteintemp).toDouble()
                viewModel.updateUserNutrients(dataUserNutrients!!)
                returnpass = true
            }
        }
        return returnpass
    }
    fun sendNotification(title:String,notificationID:Int){
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(this, RecipeDetailActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_alert)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.notification_alert))
            .setContentTitle("Daily Nutrition")
            .setContentText("Your Daily ${title} Consumption Exceeds Your Daily Limit")
            .setVibrate( longArrayOf(0, 250, 500))
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /* Create or update. */
            val channel = NotificationChannel(RecipeDetailActivity.CHANNEL_ID, RecipeDetailActivity.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = RecipeDetailActivity.CHANNEL_NAME
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(0, 250, 500)
            mBuilder.setChannelId(RecipeDetailActivity.CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }

        val notification = mBuilder.build()
        mNotificationManager.notify(notificationID, notification)
    }
}