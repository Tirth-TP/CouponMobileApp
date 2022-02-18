package com.example.couponMobileApp.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.couponMobileApp.*
import com.example.couponMobileApp.activity.HomeActivity
import com.example.couponMobileApp.adapter.StoreAdapter
import com.example.couponMobileApp.models.StoreDetail
import com.example.couponMobileApp.models.StoreDetailResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import java.util.ArrayList
import java.util.HashMap


class FavoritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        var dispLst = ArrayList<StoreDetail>()
        var stList = ArrayList<StoreDetail>()
        var itemTouchHelper:ItemTouchHelper?=null

        (context as AppCompatActivity).supportActionBar!!.title = "FavoritesFragment"

        var mAPIService: UserApi? = null
        mAPIService = ApiUtils.apiService

        val stRc = requireView().findViewById(R.id.storefRv) as RecyclerView

        val SHARED_PREF_NAME = "my_shared_preff"
        val sharedPreference =
            this.activity?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val token = "Bearer " + (sharedPreference?.getString("token", "defaultName"))

        val map: MutableMap<String, RequestBody> = HashMap()
        map[""] = toPart("") as RequestBody

        mAPIService.storeDetails(token!!, "FetchFavorite", map).enqueue(object :
            Callback<StoreDetailResponse> {
            override fun onResponse(
                call: Call<StoreDetailResponse>,
                response: retrofit2.Response<StoreDetailResponse>
            ) {

                if(response.body()?.success == true) {
                    var dt = response.body()?.data
                    if (dt != null) {
                        for (d in dt) {
                            var v = d
                            stList.add(v)
                        }
                        // cRv.adapter = ad
                        dispLst.addAll(stList)

                        var adapter = StoreAdapter(
                                requireContext(),
                                dispLst,
                                object : OnStartDragListener {
                                    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {
                                        itemTouchHelper?.startDrag(viewHolder!!)
                                    }
                                })
                        stRc.adapter = adapter
                        val callBack = MyItemTouchHelperCallBack(adapter)
                        itemTouchHelper = ItemTouchHelper(callBack)
                        itemTouchHelper?.attachToRecyclerView(stRc)
                        stRc?.isVisible = true
                        var i = response.body()?.data
                    }
                }
                else
                {
                    Toast.makeText(context,"Something went wrong!",Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<StoreDetailResponse>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
            }
        })

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(context, HomeActivity::class.java))
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

    }

    fun toPart(data: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
    }


}