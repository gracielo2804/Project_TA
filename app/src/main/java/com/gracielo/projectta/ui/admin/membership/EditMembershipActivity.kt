package com.gracielo.projectta.ui.admin.membership

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.gracielo.projectta.data.model.membershipPackage.DataMembership
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityEditMembershipBinding
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
                binding.txtHargaLowCarb.text = "Rp. ${formatter.format(dataMembershipPlan[2].price).split(",")[0]}"
                binding.txtVegetarianPrice.text = "Rp. ${formatter.format(dataMembershipPlan[1].price).split(",")[0]}"
                binding.txtAllInOnePrice.text = "Rp. ${formatter.format(dataMembershipPlan[3].price).split(",")[0]}"
            }
        }

        binding.btnEditBasic.setOnClickListener {
            if(binding.btnEditBasic.text=="Edit Price"){
                binding.btnEditBasic.text="Save Edit"
                binding.txtFieldEditBasic.visibility = View.VISIBLE
            }
            else if(binding.btnEditBasic.text=="Save Edit"){
                val newprice = binding.etEditBasicPlan.text.toString().toInt()
                if(newprice>dataMembershipPlan[1].price||newprice>dataMembershipPlan[2].price||newprice>dataMembershipPlan[3].price){
                    Toast.makeText(this,"Basic Plan price cannot be more than the price of other plan !",Toast.LENGTH_SHORT).show()
                }
                else{
                    if(!binding.etEditBasicPlan.text.isNullOrEmpty()){
                        apiServices.UpdateMembershipPlan("1",newprice){
                            if(it?.code==1){
                                Toast.makeText(this,"Basic Plan price changed succesfully",Toast.LENGTH_SHORT).show()
                                binding.btnEditBasic.text="Edit Price"
                                binding.txtFieldEditBasic.visibility = View.GONE
                                dataMembershipPlan[0].price = newprice
                                binding.txtHargaBasic.text = "Rp. ${formatter.format(dataMembershipPlan[0].price).split(",")[0]}"
                            }
                        }
                    }
                }
            }
        }
        binding.btnEditLowCarb.setOnClickListener {
            if(binding.btnEditLowCarb.text=="Edit Price"){
                binding.btnEditLowCarb.text="Save Edit"
                binding.txtFieldEditLowCarb.visibility = View.VISIBLE
            }
            else if(binding.btnEditLowCarb.text=="Save Edit"){
                val newprice = binding.etEditBasicPlan.text.toString().toInt()
                if(newprice<dataMembershipPlan[0].price||newprice>dataMembershipPlan[3].price){
                    Toast.makeText(this,"Low Carb Plan price cannot less than the price of basic plan and cannot be more than all in one plan !"
                        ,Toast.LENGTH_SHORT).show()
                }
                else{
                    if(!binding.etEditLowPlan.text.isNullOrEmpty()){
                        apiServices.UpdateMembershipPlan("3",newprice){
                            if(it?.code==1){
                                Toast.makeText(this,"Low Carb Plan price changed succesfully",Toast.LENGTH_SHORT).show()
                                binding.btnEditLowCarb.text="Edit Price"
                                binding.txtFieldEditLowCarb.visibility = View.GONE
                                dataMembershipPlan[2].price=newprice
                                binding.txtHargaLowCarb.text = "Rp. ${formatter.format(dataMembershipPlan[2].price).split(",")[0]}"

                            }
                        }
                    }
                }
            }
        }
        binding.btnEditVegetarian.setOnClickListener {
            if(binding.btnEditVegetarian.text=="Edit Price"){
                binding.btnEditVegetarian.text="Save Edit"
                binding.txtFieldEditVegetarian.visibility = View.VISIBLE
            }
            else if(binding.btnEditVegetarian.text=="Save Edit"){
                val newprice = binding.etEditVegetarianPlan.text.toString().toInt()
                if(newprice<dataMembershipPlan[0].price||newprice>dataMembershipPlan[3].price){
                    Toast.makeText(this,"Vegetarian Plan price cannot less than the price of basic plan and cannot be more than all in one plan !"
                        ,Toast.LENGTH_SHORT).show()
                }
                else{
                    if(!binding.etEditVegetarianPlan.text.isNullOrEmpty()){
                        apiServices.UpdateMembershipPlan("2",newprice){
                            if(it?.code==1){
                                Toast.makeText(this,"Vegetarian Plan price changed succesfully",Toast.LENGTH_SHORT).show()
                                binding.btnEditVegetarian.text="Edit Price"
                                binding.txtFieldEditVegetarian.visibility = View.GONE
                                dataMembershipPlan[1].price=newprice
                                binding.txtVegetarianPrice.text = "Rp. ${formatter.format(dataMembershipPlan[1].price).split(",")[0]}"

                            }
                        }
                    }
                }
            }
        }
        binding.btnEditAllInOne.setOnClickListener {
            if(binding.btnEditAllInOne.text=="Edit Price"){
                binding.btnEditAllInOne.text="Save Edit"
                binding.txtFieldEditAllInOne.visibility = View.VISIBLE
            }
            else if(binding.btnEditAllInOne.text=="Save Edit"){
                val newprice = binding.etEditAllInOnePlan.text.toString().toInt()
                if(newprice<dataMembershipPlan[0].price||newprice<dataMembershipPlan[1].price||newprice<dataMembershipPlan[2].price){
                    Toast.makeText(this,"All in One Plan price cannot less than the price of other plan !",Toast.LENGTH_SHORT).show()
                }
                else{
                    if(!binding.etEditAllInOnePlan.text.isNullOrEmpty()){
                        apiServices.UpdateMembershipPlan("4",newprice){
                            if(it?.code==1){
                                Toast.makeText(this,"All in One Plan price changed succesfully",Toast.LENGTH_SHORT).show()
                                binding.btnEditAllInOne.text="Edit Price"
                                binding.txtFieldEditAllInOne.visibility = View.GONE
                                dataMembershipPlan[3].price=newprice
                                binding.txtAllInOnePrice.text = "Rp. ${formatter.format(dataMembershipPlan[3].price).split(",")[0]}"
                            }
                        }
                    }
                }
            }
        }
    }
}