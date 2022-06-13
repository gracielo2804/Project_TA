package com.gracielo.projectta.ui.membership

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.membershipTransactionHistory.DataMembershipHistory
import com.gracielo.projectta.databinding.MembershipHistoryItemBinding
import java.text.DecimalFormat
import java.text.NumberFormat

class MembershipHistoryAdapter : RecyclerView.Adapter<MembershipHistoryAdapter.ViewHolder>() {

    private var listData =  ArrayList<DataMembershipHistory>()
    var onItemClick: ((DataMembershipHistory) -> Unit)? = null

    fun setData(newListData : List<DataMembershipHistory>?){
        if(newListData == null) return
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = MembershipHistoryItemBinding.bind(view)
        private val txtTanggal = binding.textTanggalOrder
        private val txtOrderID = binding.textOrderID
        private val txtPacakge = binding.textPackageName
        private val txtStatus = binding.statusTransaction
        private val txtPrice = binding.textNominalTransaction
        private val imageicon=binding.memberHistoryIcons

        fun bindRecipe(data: DataMembershipHistory) {
            txtTanggal.text = data.tanggal
            txtOrderID.text = data.idTransaksi
            txtPacakge.text = data.namaPaket
            if(data.namaPaket=="Basic Plan"){
                imageicon.setImageResource(R.drawable.basic_plan)
            }
            else if(data.namaPaket=="Low Carb Plan"){
                imageicon.setImageResource(R.drawable.carb)
            }
            else if(data.namaPaket=="Vegetarian Plan"){
                imageicon.setImageResource(R.drawable.vege)
            }
            else if(data.namaPaket=="All in One Plan"){
                imageicon.setImageResource(R.drawable.all_in_one)
            }
            if(data.status=="0"){
                txtStatus.text="Failed"
                txtStatus.setTextColor(Color.RED)
            }
            else if(data.status=="1"){
                txtStatus.text="Success"
                txtStatus.setTextColor(Color.GREEN)
            }
            else if(data.status=="2"){
                txtStatus.text="Pending"
                txtStatus.setTextColor(Color.YELLOW)
            }
            val formatter: NumberFormat = DecimalFormat("#.###")
            val myNumber = data.nominal.toInt()
            val formattedNumber: String = formatter.format(myNumber)
//formattedNumber is equal to 1,000,000
            txtPrice.text = "Rp. ${formattedNumber}"

        }
        init{
            binding.root.setOnClickListener{onItemClick?.invoke(listData[bindingAdapterPosition])}
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder=
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.membership_history_item, parent, false)
        )

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listData[position]
        holder.bindRecipe(data)
    }

}