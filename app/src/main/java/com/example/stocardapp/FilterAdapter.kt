package com.example.stocardapp

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.stocardapp.models.Filter
import com.google.android.material.chip.Chip

class FilterAdapter(var ctx: Context, var list:ArrayList<Filter>):RecyclerView.Adapter<FilterAdapter.ViewHolder>() {
    var i = ""
    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        var chip = v.findViewById<Chip>(R.id.chip_1)
        var fid = 0

        init {
            chip.setOnClickListener {
                if (chip.isChecked)
                {
                    Log.d("ffiiddd", i)
                    if(i == "") {
                        i = fid.toString()
                    }
                    else
                    {
                        i = i + "," + fid
                        Log.d("ffiiddd", i)
                    }
                }
                val SHARED_PREF_NAME1 = "my_shared_preff"
                val sharedPreferences: SharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME1,Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor =  sharedPreferences.edit()
                editor.putString("filterid",i)
                editor.apply()
                editor.commit()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(ctx).inflate(R.layout.filter_layout,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.chip.text = list[position].name
        Log.d("listsize","" + list.size)
        holder.fid = list[position].id
//        if(holder.chip.isChecked)
//        {
//
//          i = i + list[position].id
//            Log.d("ffiiddd",i)
//        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun getFilterId(): String {
        return i
    }
}
