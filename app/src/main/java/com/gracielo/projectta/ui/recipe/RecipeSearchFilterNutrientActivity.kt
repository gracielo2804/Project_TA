package com.gracielo.projectta.ui.recipe

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.gracielo.projectta.databinding.ActivityRecipeSearchFilterNutrientBinding

class RecipeSearchFilterNutrientActivity : AppCompatActivity() {

    var maxCalories=0;var minCalories=0
    var maxSugar=0;var minSugar=0
    var maxProtein=0;var minProtein=0
    var maxCarbohydrate=0;var minCarbohydrate=0
    var maxFat=0;var minFat=0
    private lateinit var binding: ActivityRecipeSearchFilterNutrientBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeSearchFilterNutrientBinding.inflate(layoutInflater)
        val minCaloriesText=binding.etMinCaloriesFilter;val maxCaloriesText=binding.etMaxCaloriesFilter
        val minSugarText=binding.etMinSugarFilter;val maxSugarText=binding.etMaxSugarFilter
        val minCarbohydrateText=binding.etMinCarbohydrateFilter;val maxCarbohydrateText=binding.etMaxCarbohydrateFilter
        val minFatText=binding.etMinFatFilter;val maxFatText=binding.etMaxFatFilter
        val minProteinText=binding.etMinProteinFilter;val maxProteinText=binding.etMaxProteinFilter
        setContentView(binding.root)

        maxCalories = intent.getIntExtra("maxCalories",0)
        minCalories = intent.getIntExtra("minCalories",0)

        maxFat = intent.getIntExtra("maxFat",0)
        minFat = intent.getIntExtra("minFat",0)

        maxCarbohydrate = intent.getIntExtra("maxCarbohydrate",0)
        minCarbohydrate = intent.getIntExtra("minCarbohydrate",0)

        maxProtein = intent.getIntExtra("maxProtein",0)
        minProtein = intent.getIntExtra("minProtein",0)

        maxSugar = intent.getIntExtra("maxSugar",0)
        minSugar = intent.getIntExtra("minSugar",0)

        minCaloriesText.text=Editable.Factory.getInstance().newEditable(minCalories.toString())
        maxCaloriesText.text=Editable.Factory.getInstance().newEditable(maxCalories.toString())

        minCarbohydrateText.text=Editable.Factory.getInstance().newEditable(minCarbohydrate.toString())
        maxCarbohydrateText.text=Editable.Factory.getInstance().newEditable(maxCarbohydrate.toString())

        minProteinText.text=Editable.Factory.getInstance().newEditable(minProtein.toString())
        maxProteinText.text=Editable.Factory.getInstance().newEditable(maxProtein.toString())

        minSugarText.text=Editable.Factory.getInstance().newEditable(minSugar.toString())
        maxSugarText.text=Editable.Factory.getInstance().newEditable(maxSugar.toString())

        minFatText.text=Editable.Factory.getInstance().newEditable(minFat.toString())
        maxFatText.text=Editable.Factory.getInstance().newEditable(maxFat.toString())
        
        
        minSugarText.setOnFocusChangeListener { view, b ->
            if(!b){
                if(minSugarText.text.isNullOrEmpty()){
                    minSugarText.text=Editable.Factory.getInstance().newEditable("0")
                    minSugar=0
                }

                if(minSugarText.text.toString()[0]=='0'){
                    var indexBukanNol = 1
                    for (i in minSugarText.text.toString().indices){
                        if(minSugarText.text.toString()[i]!='0'){
                            indexBukanNol=i
                            break
                        }
                    }
                    var stringtemp=""
                    for(i in minSugarText.text.toString().indices){
                        if(i>=indexBukanNol){
                            stringtemp+=minSugarText.text.toString()[i]
                        }
                    }
                    if(stringtemp!="")  minSugarText.text=Editable.Factory.getInstance().newEditable(stringtemp)
                }
                
                minSugar=minSugarText.text.toString().toInt()
                if(minSugar>0){
                    if(minSugar>maxSugar && maxSugar!=0){
                        maxSugar=minSugar
                        maxSugarText.text=Editable.Factory.getInstance().newEditable(minSugar.toString())
                    }
                }

            }
        }
        maxSugarText.setOnFocusChangeListener { view, b ->
            if(!b){
                if(maxSugarText.text.isNullOrEmpty()){
                    maxSugarText.text=Editable.Factory.getInstance().newEditable("0")
                    maxSugar=0
                }

                if(maxSugarText.text.toString()[0]=='0'){
                    var indexBukanNol = 1
                    for (i in maxSugarText.text.toString().indices){
                        if(maxSugarText.text.toString()[i]!='0'){
                            indexBukanNol=i
                            break
                        }
                    }
                    var stringtemp=""
                    for(i in maxSugarText.text.toString().indices){
                        if(i>=indexBukanNol){
                            stringtemp+=maxSugarText.text.toString()[i]
                        }
                    }

                    if(stringtemp!="") maxSugarText.text=Editable.Factory.getInstance().newEditable(stringtemp)
                }
                
                maxSugar=maxSugarText.text.toString().toInt()
                if(maxSugar<minSugar && minSugar!=0){
                    minSugar=maxSugar
                    minSugarText.text=Editable.Factory.getInstance().newEditable(maxSugar.toString())
                }
            }
        }
        
        minProteinText.setOnFocusChangeListener { view, b ->
            if(!b){
                if(minProteinText.text.isNullOrEmpty()){
                    minProteinText.text=Editable.Factory.getInstance().newEditable("0")
                    minProtein=0
                }

                if(minProteinText.text.toString()[0]=='0'){
                    var indexBukanNol = 1
                    for (i in minProteinText.text.toString().indices){
                        if(minProteinText.text.toString()[i]!='0'){
                            indexBukanNol=i
                            break
                        }
                    }
                    var stringtemp=""
                    for(i in minProteinText.text.toString().indices){
                        if(i>=indexBukanNol){
                            stringtemp+=minProteinText.text.toString()[i]
                        }
                    }
                    if(stringtemp!="") minProteinText.text=Editable.Factory.getInstance().newEditable(stringtemp)
                }
                
                minProtein=minProteinText.text.toString().toInt()
                if(minProtein>0){
                    if(minProtein>maxProtein && maxProtein!=0){
                        maxProtein=minProtein
                        maxProteinText.text=Editable.Factory.getInstance().newEditable(minProtein.toString())
                    }
                }

            }
        }
        maxProteinText.setOnFocusChangeListener { view, b ->
            if(!b){
                if(maxProteinText.text.isNullOrEmpty()){
                    maxProteinText.text=Editable.Factory.getInstance().newEditable("0")
                    maxProtein=0
                }

                if(maxProteinText.text.toString()[0]=='0'){
                    var indexBukanNol = 1
                    for (i in maxProteinText.text.toString().indices){
                        if(maxProteinText.text.toString()[i]!='0'){
                            indexBukanNol=i
                            break
                        }
                    }
                    var stringtemp=""
                    for(i in maxProteinText.text.toString().indices){
                        if(i>=indexBukanNol){
                            stringtemp+=maxProteinText.text.toString()[i]
                        }
                    }
                    if(stringtemp!="")  maxProteinText.text=Editable.Factory.getInstance().newEditable(stringtemp)
                }
                
                maxProtein=maxProteinText.text.toString().toInt()
                if(maxProtein<minProtein && minProtein!=0){
                    minProtein=maxProtein
                    minProteinText.text=Editable.Factory.getInstance().newEditable(maxProtein.toString())
                }
            }
        }
        
        minCarbohydrateText.setOnFocusChangeListener { view, b ->
            if(!b){
                if(minCarbohydrateText.text.isNullOrEmpty()){
                    minCarbohydrateText.text=Editable.Factory.getInstance().newEditable("0")
                    minCarbohydrate=0
                }

                if(minCarbohydrateText.text.toString()[0]=='0'){
                    var indexBukanNol = 1
                    for (i in minCarbohydrateText.text.toString().indices){
                        if(minCarbohydrateText.text.toString()[i]!='0'){
                            indexBukanNol=i
                            break
                        }
                    }
                    var stringtemp=""
                    for(i in minCarbohydrateText.text.toString().indices){
                        if(i>=indexBukanNol){
                            stringtemp+=minCarbohydrateText.text.toString()[i]
                        }
                    }
                    if(stringtemp!="") minCarbohydrateText.text=Editable.Factory.getInstance().newEditable(stringtemp)
                }
                
                minCarbohydrate=minCarbohydrateText.text.toString().toInt()
                if(minCarbohydrate>0){
                    if(minCarbohydrate>maxCarbohydrate && maxCarbohydrate!=0){
                        maxCarbohydrate=minCarbohydrate
                        maxCarbohydrateText.text=Editable.Factory.getInstance().newEditable(minCarbohydrate.toString())
                    }
                }

            }
        }
        maxCarbohydrateText.setOnFocusChangeListener { view, b ->
            if(!b){
                if(maxCarbohydrateText.text.isNullOrEmpty()){
                    maxCarbohydrateText.text=Editable.Factory.getInstance().newEditable("0")
                    maxCarbohydrate=0
                }

                if(maxCarbohydrateText.text.toString()[0]=='0'){
                    var indexBukanNol = 1
                    for (i in maxCarbohydrateText.text.toString().indices){
                        if(maxCarbohydrateText.text.toString()[i]!='0'){
                            indexBukanNol=i
                            break
                        }
                    }
                    var stringtemp=""
                    for(i in maxCarbohydrateText.text.toString().indices){
                        if(i>=indexBukanNol){
                            stringtemp+=maxCarbohydrateText.text.toString()[i]
                        }
                    }
                    if(stringtemp!="") maxCarbohydrateText.text=Editable.Factory.getInstance().newEditable(stringtemp)
                }
                
                maxCarbohydrate=maxCarbohydrateText.text.toString().toInt()
                if(maxCarbohydrate<minCarbohydrate && minCarbohydrate!=0){
                    minCarbohydrate=maxCarbohydrate
                    minCarbohydrateText.text=Editable.Factory.getInstance().newEditable(maxCarbohydrate.toString())
                }
            }
        }
        
        minFatText.setOnFocusChangeListener { view, b ->
            if(!b){
                if(minFatText.text.isNullOrEmpty()){
                    minFatText.text=Editable.Factory.getInstance().newEditable("0")
                    minFat=0
                }

                if(minFatText.text.toString()[0]=='0'){
                    var indexBukanNol = 1
                    for (i in minFatText.text.toString().indices){
                        if(minFatText.text.toString()[i]!='0'){
                            indexBukanNol=i
                            break
                        }
                    }
                    var stringtemp=""
                    for(i in minFatText.text.toString().indices){
                        if(i>=indexBukanNol){
                            stringtemp+=minFatText.text.toString()[i]
                        }
                    }
                    if(stringtemp!="") minFatText.text=Editable.Factory.getInstance().newEditable(stringtemp)
                }
                
                minFat=minFatText.text.toString().toInt()
                if(minFat>0){
                    if(minFat>maxFat && maxFat!=0){
                        maxFat=minFat
                        maxFatText.text=Editable.Factory.getInstance().newEditable(minFat.toString())
                    }
                }

            }
        }
        maxFatText.setOnFocusChangeListener { view, b ->
            if(!b){
                if(maxFatText.text.isNullOrEmpty()){
                    maxFatText.text=Editable.Factory.getInstance().newEditable("0")
                    maxFat=0
                }

                if(maxFatText.text.toString()[0]=='0'){
                    var indexBukanNol = 1
                    for (i in maxFatText.text.toString().indices){
                        if(maxFatText.text.toString()[i]!='0'){
                            indexBukanNol=i
                            break
                        }
                    }
                    var stringtemp=""
                    for(i in maxFatText.text.toString().indices){
                        if(i>=indexBukanNol){
                            stringtemp+=maxFatText.text.toString()[i]
                        }
                    }
                    if(stringtemp!="") maxFatText.text=Editable.Factory.getInstance().newEditable(stringtemp)
                }
                
                maxFat=maxFatText.text.toString().toInt()
                if(maxFat<minFat && minFat!=0){
                    minFat=maxFat
                    minFatText.text=Editable.Factory.getInstance().newEditable(maxFat.toString())
                }
            }
        }
        
        minCaloriesText.setOnFocusChangeListener { view, b ->
            if(!b){
                if(minCaloriesText.text.isNullOrEmpty()){
                    minCaloriesText.text=Editable.Factory.getInstance().newEditable("0")
                    minCalories=0
                }
                if(minCaloriesText.text.toString()[0]=='0'){
                    var indexBukanNol = 1
                    for (i in minCaloriesText.text.toString().indices){
                        if(minCaloriesText.text.toString()[i]!='0'){
                            indexBukanNol=i
                            break
                        }
                    }
                    var stringtemp=""
                    for(i in minCaloriesText.text.toString().indices){
                        if(i>=indexBukanNol){
                            stringtemp+=minCaloriesText.text.toString()[i]
                        }
                    }
                    if(stringtemp!="")  minCaloriesText.text=Editable.Factory.getInstance().newEditable(stringtemp)
                }
                minCalories=minCaloriesText.text.toString().toInt()
                if(minCalories>0){
                    if(minCalories>maxCalories && maxCalories!=0){
                        maxCalories=minCalories
                        maxCaloriesText.text=Editable.Factory.getInstance().newEditable(minCalories.toString())
                    }
                }

            }
        }
        maxCaloriesText.setOnFocusChangeListener { view, b ->
            if(!b){
                if(maxCaloriesText.text.isNullOrEmpty()){
                    maxCaloriesText.text=Editable.Factory.getInstance().newEditable("0")
                    maxCalories=0
                }

                if(maxCaloriesText.text.toString()[0]=='0'){
                    var indexBukanNol = 1
                    for (i in maxCaloriesText.text.toString().indices){
                        if(maxCaloriesText.text.toString()[i]!='0'){
                            indexBukanNol=i
                            break
                        }
                    }
                    var stringtemp=""
                    for(i in maxCaloriesText.text.toString().indices){
                        if(i>=indexBukanNol){
                            stringtemp+=maxCaloriesText.text.toString()[i]
                        }
                    }
                    if(stringtemp!="")  maxCaloriesText.text=Editable.Factory.getInstance().newEditable(stringtemp)
                }
                
                maxCalories=maxCaloriesText.text.toString().toInt()
                if(maxCalories<minCalories && minCalories!=0){
                    minCalories=maxCalories
                    minCaloriesText.text=Editable.Factory.getInstance().newEditable(maxCalories.toString())
                }
            }
        }
        binding.btnSaveNutrientFilter.setOnClickListener {
            minSugarText.clearFocus()
            maxSugarText.clearFocus()

            minFatText.clearFocus()
            maxFatText.clearFocus()

            minCarbohydrateText.clearFocus()
            maxCarbohydrateText.clearFocus()

            minProteinText.clearFocus()
            maxProteinText.clearFocus()

            minCaloriesText.clearFocus()
            maxCaloriesText.clearFocus()
            val intent = Intent()
            
            intent.putExtra("minCalories",minCalories)
            intent.putExtra("maxCalories",maxCalories)

            intent.putExtra("minSugar",minSugar)
            intent.putExtra("maxSugar",maxSugar)

            intent.putExtra("minProtein",minProtein)
            intent.putExtra("maxProtein",maxProtein)

            intent.putExtra("minCarbohydrate",minCarbohydrate)
            intent.putExtra("maxCarbohydrate",maxCarbohydrate)

            intent.putExtra("minFat",minFat)
            intent.putExtra("maxFat",maxFat)

            setResult(Activity.RESULT_OK,intent)
            finish()
        }
        
    }

    override fun onBackPressed() {
        val intent = Intent()

        intent.putExtra("minCalories",minCalories)
        intent.putExtra("maxCalories",maxCalories)

        intent.putExtra("minSugar",minSugar)
        intent.putExtra("maxSugar",maxSugar)

        intent.putExtra("minProtein",minProtein)
        intent.putExtra("maxProtein",maxProtein)

        intent.putExtra("minCarbohydrate",minCarbohydrate)
        intent.putExtra("maxCarbohydrate",maxCarbohydrate)

        intent.putExtra("minFat",minFat)
        intent.putExtra("maxFat",maxFat)

        setResult(Activity.RESULT_OK,intent)
        finish()
    }
}