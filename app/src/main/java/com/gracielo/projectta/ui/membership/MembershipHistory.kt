package com.gracielo.projectta.ui.membership

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityMembershipHistoryBinding
import com.gracielo.projectta.ui.history.nutrientHistory.NutrientHistoryAdapter
import com.gracielo.projectta.util.FunHelper
import com.gracielo.projectta.util.FunHelper.observeOnce
import com.gracielo.projectta.viewmodel.UserViewModel

class MembershipHistory : AppCompatActivity() {

    private lateinit var binding : ActivityMembershipHistoryBinding
    private val apiServices = ApiServices()
    lateinit var viewModel: UserViewModel
    private var helper= FunHelper
    val adapters = MembershipHistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembershipHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rvHistory=binding.rvMembershipHistory

        viewModel=helper.obtainUserViewModel(this)
        viewModel.getUser().observeOnce(this){
            apiServices.GetUserMembershipTransaction(it.id){transaction->
                if(transaction!=null){
                    adapters.setData(transaction.dataMembershipHistory)
                    rvHistory.apply {
                        layoutManager = LinearLayoutManager(this@MembershipHistory)
                        adapter = adapters
                    }
                }
            }
        }
        supportActionBar?.hide()

    }
}