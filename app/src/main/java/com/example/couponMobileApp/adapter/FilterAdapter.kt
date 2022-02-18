package com.example.couponMobileApp.adapter

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.couponMobileApp.R
import com.example.couponMobileApp.models.Filter
import com.google.android.material.chip.Chip

class FilterAdapter(var ctx: Context, var list:ArrayList<Filter>):RecyclerView.Adapter<FilterAdapter.ViewHolder>() {
    var i = ""
    var F_list= ArrayList<String>()
    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        var chip = v.findViewById<Chip>(R.id.chip_1)
        var fid = 0

        init {
//            chip.setOnClickListener {
//                val joined: String
//
//                if (chip.isChecked) {
//
//                    F_list.add(fid.toString())
//                    joined = TextUtils.join(", ", F_list)
//                    Log.d("FILTER_ADD",joined)
//                    val SHARED_PREF_NAME1 = "my_shared_preff"
//                    val sharedPreferences: SharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME1,Context.MODE_PRIVATE)
//                    val editor: SharedPreferences.Editor =  sharedPreferences.edit()
//                    editor.putString("filterid",joined)
//                    editor.apply()
//                    editor.commit()
//
//
//                } else {
//                    F_list.remove(fid.toString())
//                    joined = TextUtils.join(", ", F_list)
//                    Log.d("FILTER_REMOVE",joined)
//                    val SHARED_PREF_NAME1 = "my_shared_preff"
//                    val sharedPreferences: SharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME1,Context.MODE_PRIVATE)
//                    val editor: SharedPreferences.Editor =  sharedPreferences.edit()
//                    editor.putString("filterid",joined)
//                    editor.apply()
//                    editor.commit()
//                }
//
//            }
//                val SHARED_PREF_NAME1 = "my_shared_preff"
//                val sharedPreferences: SharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME1,Context.MODE_PRIVATE)
//                val editor: SharedPreferences.Editor =  sharedPreferences.edit()
//                editor.putString("filterid",i)
//                editor.apply()
//                editor.commit()
//            }

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
        holder.chip.setOnClickListener {
            var joined: String

            if (holder.chip.isChecked) {

                F_list.add(list[position].id.toString())
                joined = TextUtils.join(", ", F_list)
                Log.d("FILTER_ADD",joined)



            } else {
                F_list.remove(list[position].id.toString())
                joined = TextUtils.join(", ", F_list)
                Log.d("FILTER_REMOVE",joined)

            }
            if(F_list.size == 0){
                joined = ""
            }
            val SHARED_PREF_NAME1 = "my_shared_preff"
            val sharedPreferences: SharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME1,Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor =  sharedPreferences.edit()
            editor.putString("filterid",joined)
            editor.putString("filtersize", F_list.size.toString())
            editor.apply()
            editor.commit()
            Log.d("getjoined",joined)
            Log.d("getjoinedlist","" + F_list.size)

        }
        Log.d("listsize","" + list.size)

//        holder.chip.setOnClickListener {
//            if (holder.chip.isChecked)
//            {
//                Log.d("ffiiddd", i)
//                if(i == "") {
//                    i = list[position].id.toString()
//                }
//                else
//                {
//                    i = i + "," +  list[position].id.toString()
//                    Log.d("ffiiddd", i)
//                }
//            }
//            val SHARED_PREF_NAME1 = "my_shared_preff"
//            val sharedPreferences: SharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME1,Context.MODE_PRIVATE)
//            val editor: SharedPreferences.Editor =  sharedPreferences.edit()
//            editor.putString("filterid",i)
//            editor.apply()
//            editor.commit()
//        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun getFilterId(): String {
        return i
    }
}
