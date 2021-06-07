package com.example.stocardapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.stocardapp.models.CardSer
import java.util.*
import kotlin.collections.ArrayList

class SerAdapter (var ctx:Context,var lst:ArrayList<CardSer>,var lisener:OnStartDragListener): RecyclerView.Adapter<SerAdapter.ViewHolder>(),IteamTouchHelperAdapter {

    inner class ViewHolder(v:View):RecyclerView.ViewHolder(v){
       var cn = v.findViewById<TextView>(R.id.storeImtit)
        var item = v.findViewById<CardView>(R.id.storeCv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(ctx).inflate(R.layout.store_details,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.cn.text = lst[position].cnm
            holder.item.setOnLongClickListener {
                lisener.onStartDrag(holder)
                false
            }
    }

    override fun getItemCount(): Int {
       return lst.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int):Boolean {
        Collections.swap(lst,fromPosition,toPosition)
        notifyItemMoved(fromPosition,toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
        lst.removeAt(position)
        notifyItemRemoved(position)
    }
}