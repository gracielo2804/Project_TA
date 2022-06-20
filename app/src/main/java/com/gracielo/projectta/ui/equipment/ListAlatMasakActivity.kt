package com.gracielo.projectta.ui.equipment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gracielo.projectta.data.model.listEquipment.DataEquipment
import com.gracielo.projectta.data.model.userListEquipment.DataEquipmentUser
import com.gracielo.projectta.data.source.local.entity.UserEntity
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityListAlatMasakBinding
import com.gracielo.projectta.ui.ingredients.DataEquipmentAdapter
import com.gracielo.projectta.ui.setting.SettingActivity
import com.gracielo.projectta.util.FunHelper
import com.gracielo.projectta.util.FunHelper.observeOnce
import com.gracielo.projectta.viewmodel.UserViewModel

class ListAlatMasakActivity : AppCompatActivity() {

    lateinit var binding:ActivityListAlatMasakBinding
    var helper=FunHelper
    private lateinit var rv_list_equipment: RecyclerView
    private lateinit var userViewModel: UserViewModel
    var apiServices = ApiServices()
    var listAllEquipment = ArrayList<DataEquipment>()
    var dataEquipmentUser = DataEquipmentUser("","")
    var listAllEquipmentAdapters = ArrayList<DataEquipmentAdapter>()
    private lateinit var dataUser: UserEntity
    private var  adapters = EquipmentToolsAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListAlatMasakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rv_list_equipment=binding.rvListEquipment
        rv_list_equipment.apply {
            layoutManager = LinearLayoutManager(this@ListAlatMasakActivity)
            adapter = adapters
        }

        userViewModel = helper.obtainUserViewModel(this)
        dataUser = UserEntity("1","1","1","1","0","",1,1,"1",1.1,1,1)
        userViewModel.getUser().observeOnce(this){
            dataUser=it
            apiServices.getAllEquipment {equipment->
                if(equipment?.code==1){
                    listAllEquipment.addAll(equipment.dataEquipment)
                    for (i in listAllEquipment.indices){
                        val data = DataEquipmentAdapter(listAllEquipment[i].id,listAllEquipment[i].image,listAllEquipment[i].name,false)
                        listAllEquipmentAdapters.add(data)
                    }
                    setAdapters()
//                    Handler(Looper.getMainLooper()).postDelayed({
//                        adapters.notifyDataSetChanged()
//                    },500)

                }
            }
        }


        adapters.onItemClick = {
            it.isSelected=!it.isSelected
            for (i in listAllEquipmentAdapters.indices){
                if(listAllEquipmentAdapters[i].id==it.id){listAllEquipmentAdapters[i].isSelected = it.isSelected}
            }
            adapters.notifyDataSetChanged()
        }
        binding.btnSaveEditCookingList.setOnClickListener {
            var listId= mutableListOf<String>()
            for(i in listAllEquipmentAdapters.indices){
                if(listAllEquipmentAdapters[i].isSelected){listId.add(listAllEquipmentAdapters[i].id)}
            }
            var stringListID = ""
            for (i in listId.indices){
                if(i < listId.size-1){
                    stringListID+="${listId[i]}, "
                }
                else stringListID+= listId[i]
            }
            apiServices.InsertUpdateuserEquipment(dataUser.id,stringListID){}
            val intent = Intent(this, SettingActivity::class.java)
            finish()
            startActivity(intent)
        }
        binding.imgBackEquipmentList.setOnClickListener {
            var listId= mutableListOf<String>()
            for(i in listAllEquipmentAdapters.indices){
                if(listAllEquipmentAdapters[i].isSelected){listId.add(listAllEquipmentAdapters[i].id)}
            }
            var stringListID = ""
            for (i in listId.indices){
                if(i < listId.size-1){
                    stringListID+="${listId[i]}, "
                }
                else stringListID+= listId[i]
            }
            apiServices.InsertUpdateuserEquipment(dataUser.id,stringListID){}
            val intent = Intent(this, SettingActivity::class.java)
            finish()
            startActivity(intent)
        }
    }
    override fun onBackPressed() {
        val intent = Intent(this, SettingActivity::class.java)
        finish()
        startActivity(intent)
    }

    fun setAdapters(){
        apiServices.getUserSelectedEquipment(dataUser.id){
            if(it?.code==1){
                dataEquipmentUser = it.dataEquipment
                var listIdEquipmentUser = dataEquipmentUser.ListIDEquipment.split(", ")
                for (i in listIdEquipmentUser.indices){
                    for (j in listAllEquipmentAdapters.indices){
                        if(listIdEquipmentUser[i]==listAllEquipmentAdapters[j].id)listAllEquipmentAdapters[j].isSelected=true
                    }
                }
            }
            adapters.setData(listAllEquipmentAdapters)
            adapters.notifyDataSetChanged()
        }

    }
}