package com.example.stocardapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.stocardapp.models.CardDetailResponse
import com.example.stocardapp.models.StoreDetail
import com.example.stocardapp.models.StoreDetailResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import java.util.ArrayList
import java.util.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Favorites.newInstance] factory method to
 * create an instance of this fragment.
 */
class Favorites : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var dispLst = ArrayList<StoreDetail>()
        var stList = ArrayList<StoreDetail>()
        var itemTouchHelper:ItemTouchHelper?=null

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

            override fun onFailure(call: Call<StoreDetailResponse>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
            }
        })

    }

    fun toPart(data: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
    }


}