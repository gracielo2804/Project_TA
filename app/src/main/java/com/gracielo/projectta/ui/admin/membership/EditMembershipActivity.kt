package com.gracielo.projectta.ui.admin.membership

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.gracielo.projectta.data.model.membershipPackage.DataMembership
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityEditMembershipBinding
import com.gracielo.projectta.ui.admin.AdminHomeActivity
import java.text.NumberFormat
import java.util.*

class EditMembershipActivity : AppCompatActivity() {
    lateinit var binding:ActivityEditMembershipBinding

    var dataMembershipPlan = mutableListOf<DataMembership>()
    val apiServices = ApiServices()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMembershipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val formatter = NumberFormat.getCurrencyInstance(Locale.GERMANY)
        apiServices.getMembershipPackage {
            if(it?.code==1){
                dataMembershipPlan.addAll(it.dataMembership)
                binding.txtHargaBasic.text = "Rp. ${formatter.format(dataMembershipPlan[0].price).split(",")[0]}"
                binding.textBasicFeature.text="${dataMembershipPlan[0].description}"
                binding.txtHargaLowCarb.text = "Rp. ${formatter.format(dataMembershipPlan[2].price).split(",")[0]}"
                binding.textLowCarbFeatures.text="${dataMembershipPlan[2].description}"
                binding.txtVegetarianPrice.text = "Rp. ${formatter.format(dataMembershipPlan[1].price).split(",")[0]}"
                binding.textViewVegetarianFeatures.text="${dataMembershipPlan[1].description}"
                binding.txtAllInOnePrice.text = "Rp. ${formatter.format(dataMembershipPlan[3].price).split(",")[0]}"
                binding.textViewAllInOneFeatures.text="${dataMembershipPlan[3].description}"
            }
        }

        binding.btnEditBasic.setOnClickListener {
            if(binding.btnEditBasic.text=="Edit Price"){
                binding.btnEditBasic.text="Save Edit"
                binding.txtFieldEditBasic.visibility = View.VISIBLE
                binding.txtFieldEditDetailBasic.visibility = View.VISIBLE
                binding.etEditDetailBasicPlan.setText(binding.textBasicFeature.text)
            }
            else if(binding.btnEditBasic.text=="Save Edit"){
                if(!binding.etEditBasicPlan.text.toString().isNullOrEmpty()){
                    if(!binding.etEditDetailBasicPlan.text.toString().isNullOrEmpty()){
                        val newprice = binding.etEditBasicPlan.text.toString().toInt()
                        if(newprice>dataMembershipPlan[1].price||newprice>dataMembershipPlan[2].price||newprice>dataMembershipPlan[3].price){
                            Toast.makeText(this,"Basic Plan price cannot be more than the price of other plan !",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            var textDetailBasic = binding.etEditDetailBasicPlan.text.toString()
                            binding.textBasicFeature.text = textDetailBasic
                            if(!binding.etEditBasicPlan.text.isNullOrEmpty()){
                                apiServices.UpdateMembershipPlan("1",newprice,textDetailBasic){
                                    if(it?.code==1){
                                        Toast.makeText(this,"Basic Plan price changed succesfully",Toast.LENGTH_SHORT).show()
                                        binding.btnEditBasic.text="Edit Price"
                                        binding.txtFieldEditBasic.visibility = View.GONE
                                        binding.txtFieldEditDetailBasic.visibility = View.GONE
                                        dataMembershipPlan[0].price = newprice
                                        binding.txtHargaBasic.text = "Rp. ${formatter.format(dataMembershipPlan[0].price).split(",")[0]}"
                                    }
                                }
                            }
                        }
                    }
                    else{
                        Toast.makeText(this,"Membership plan description cannot be empty!",Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this,"Input Price cannot be empty!",Toast.LENGTH_SHORT).show()
                }

            }

        }
        binding.btnEditLowCarb.setOnClickListener {
            if(binding.btnEditLowCarb.text=="Edit Price"){
                binding.btnEditLowCarb.text="Save Edit"
                binding.txtFieldEditLowCarb.visibility = View.VISIBLE
                binding.txtFieldEditDetailLowCarb.visibility = View.VISIBLE
                binding.etEditDetailLowCarbPlan.setText(binding.textLowCarbFeatures.text.toString())
            }
            else if(binding.btnEditLowCarb.text=="Save Edit"){
                if(!binding.etEditLowPlan.text.toString().isNullOrEmpty()){
                    if(!binding.etEditDetailLowCarbPlan.text.toString().isNullOrEmpty()){
                        val newprice = binding.etEditLowPlan.text.toString().toInt()
                        if(newprice<dataMembershipPlan[0].price||newprice>dataMembershipPlan[3].price){
                            Toast.makeText(this,"Low Carb Plan price cannot less than the price of basic plan and cannot be more than all in one plan !"
                                ,Toast.LENGTH_SHORT).show()
                        }
                        else{
                            var textDetailLowcarb = binding.etEditDetailLowCarbPlan.text.toString()
                            binding.textLowCarbFeatures.text = textDetailLowcarb
                            if(!binding.etEditLowPlan.text.isNullOrEmpty()){
                                apiServices.UpdateMembershipPlan("3",newprice,textDetailLowcarb){
                                    if(it?.code==1){
                                        Toast.makeText(this,"Low Carb Plan price changed succesfully",Toast.LENGTH_SHORT).show()
                                        binding.btnEditLowCarb.text="Edit Price"
                                        binding.txtFieldEditLowCarb.visibility = View.GONE
                                        binding.txtFieldEditDetailLowCarb.visibility = View.GONE
                                        binding.textLowCarbFeatures.text = textDetailLowcarb
                                        dataMembershipPlan[2].price=newprice
                                        binding.txtHargaLowCarb.text = "Rp. ${formatter.format(dataMembershipPlan[2].price).split(",")[0]}"
                                    }
                                }
                            }
                        }
                    }
                    else{
                        Toast.makeText(this,"Membership plan description cannot be empty!",Toast.LENGTH_SHORT).show()
                    }

                }
                else{
                    Toast.makeText(this,"Input Price cannot be more empty!",Toast.LENGTH_SHORT).show()
                }

            }
        }
        binding.btnEditVegetarian.setOnClickListener {
            if(binding.btnEditVegetarian.text=="Edit Price"){
                binding.btnEditVegetarian.text="Save Edit"
                binding.txtFieldEditVegetarian.visibility = View.VISIBLE
                binding.txtFieldEditDetailVegetarian.visibility = View.VISIBLE
                binding.etEditDetailVegetarianPlan.setText(binding.textViewVegetarianFeatures.text.toString())
            }
            else if(binding.btnEditVegetarian.text=="Save Edit"){
                if(!binding.etEditVegetarianPlan.text.isNullOrEmpty()){
                    if(!binding.etEditDetailVegetarianPlan.text.isNullOrEmpty()){
                        val newprice = binding.etEditVegetarianPlan.text.toString().toInt()
                        if(newprice<dataMembershipPlan[0].price||newprice>dataMembershipPlan[3].price){
                            Toast.makeText(this,"Vegetarian Plan price cannot less than the price of basic plan and cannot be more than all in one plan !"
                                ,Toast.LENGTH_SHORT).show()
                        }
                        else{
                            var textDetailVege = binding.etEditDetailVegetarianPlan.text.toString()
                            binding.textViewVegetarianFeatures.text = textDetailVege
                            apiServices.UpdateMembershipPlan("2",newprice,textDetailVege){
                                if(it?.code==1){
                                    Toast.makeText(this,"Vegetarian Plan price changed succesfully",Toast.LENGTH_SHORT).show()
                                    binding.btnEditVegetarian.text="Edit Price"
                                    binding.txtFieldEditVegetarian.visibility = View.GONE
                                    binding.txtFieldEditDetailVegetarian.visibility=View.GONE
                                    binding.textViewVegetarianFeatures.text = textDetailVege
                                    dataMembershipPlan[1].price=newprice

                                    binding.txtVegetarianPrice.text = "Rp. ${formatter.format(dataMembershipPlan[1].price).split(",")[0]}"
                                }
                            }
                        }
                    }
                    else{
                        Toast.makeText(this,"Membership plan description cannot be empty!",Toast.LENGTH_SHORT).show()
                    }

                }
                else{
                    Toast.makeText(this,"Input Price cannot be more empty!",Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.btnEditAllInOne.setOnClickListener {

            if(binding.btnEditAllInOne.text=="Edit Price"){
                binding.btnEditAllInOne.text="Save Edit"
                binding.txtFieldEditAllInOne.visibility = View.VISIBLE
                binding.txtFieldEditDetailAllInOne.visibility = View.VISIBLE
                binding.etEditDetailAllInOnePlan.setText(binding.textViewAllInOneFeatures.text.toString())
            }
            else if(binding.btnEditAllInOne.text=="Save Edit"){
                if(!binding.etEditAllInOnePlan.text.isNullOrEmpty()){
                    if(!binding.etEditDetailAllInOnePlan.text.isNullOrEmpty()){
                        val newprice = binding.etEditAllInOnePlan.text.toString().toInt()
                        if(newprice<dataMembershipPlan[0].price||newprice<dataMembershipPlan[1].price||newprice<dataMembershipPlan[2].price){
                            Toast.makeText(this,"All in One Plan price cannot less than the price of other plan !",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            var textDetailAllInOne = binding.etEditDetailAllInOnePlan.text.toString()
                            binding.textViewAllInOneFeatures.text = textDetailAllInOne
                            apiServices.UpdateMembershipPlan("4",newprice,textDetailAllInOne){
                                if(it?.code==1){
                                    Toast.makeText(this,"All in One Plan price changed succesfully",Toast.LENGTH_SHORT).show()
                                    binding.btnEditAllInOne.text="Edit Price"
                                    binding.txtFieldEditAllInOne.visibility = View.GONE
                                    binding.txtFieldEditDetailAllInOne.visibility = View.GONE
                                    binding.textViewAllInOneFeatures.text = textDetailAllInOne
                                    dataMembershipPlan[3].price=newprice
                                    binding.txtAllInOnePrice.text = "Rp. ${formatter.format(dataMembershipPlan[3].price).split(",")[0]}"
                                }
                            }
                        }
                    }
                    else{
                        Toast.makeText(this,"Membership plan description cannot be empty!",Toast.LENGTH_SHORT).show()
                    }

                }
                else{
                    Toast.makeText(this,"Input Price cannot be more empty!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onBackPressed() {
        val intentt= Intent(this,AdminHomeActivity::class.java)
        finish()
        startActivity(intentt)
    }
}