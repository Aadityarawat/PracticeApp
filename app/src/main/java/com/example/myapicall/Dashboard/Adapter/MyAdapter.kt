package com.example.myapicall.Dashboard.Adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapicall.Dashboard.Model.GetFamilyListModel
import com.example.myapicall.R
import com.example.myapicall.UserAuth.Model.Constant
import com.google.android.material.imageview.ShapeableImageView


class MyAdapter(private var detailList: List<GetFamilyListModel.Data.Data?>?) : RecyclerView.Adapter<MyAdapter.MyViewHolder>(){

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ShapeableImageView>(com.example.myapicall.R.id.imageIV)
        val status = itemView.findViewById<TextView>(R.id.statusTV)
        val phone = itemView.findViewById<TextView>(R.id.phoneTV)
        val gender = itemView.findViewById<TextView>(R.id.genderTV)
        val role = itemView.findViewById<TextView>(R.id.roleTv)
        val genderCV = itemView.findViewById<CardView>(R.id.genderCV)
        val statusCV = itemView.findViewById<CardView>(R.id.statusCV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return detailList?.size ?: 1
    }

    @SuppressLint("ResourceAsColor", "ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = detailList?.get(position)
        //holder.image.setImageResource(currentItem.image)
        holder.status.text = currentItem?.staffingNeedTime
        holder.phone.text = currentItem?.contactPhone
        holder.gender.text = currentItem?.staffGender
        holder.role.text = currentItem?.requestTitle

        if (currentItem?.staffGender == "Male"){
            holder.genderCV.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.lightblue))
        }
        else{
            holder.genderCV.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.lightpink))
        }

        if (currentItem?.staffingNeedTime == "immediately"){
            holder.statusCV.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.lightgreen))
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
        }
        else if (currentItem?.staffingNeedTime == "within-a-week"){
            holder.statusCV.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.lightorange))
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.orangedark))
        }
        else {
            holder.statusCV.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.lightred))
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
        }
        val image_URL = Constant.BASE_URL+currentItem?.image
        Glide.with(holder.itemView.context).load(image_URL).placeholder(R.drawable.imageloader).into(holder.image)
    }
    fun filterList(filterList: ArrayList<GetFamilyListModel.Data.Data>) {
        detailList = filterList
        notifyDataSetChanged()
    }
}