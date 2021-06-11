package com.example.stocardapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.example.stocardapp.models.StoreDetail
import com.example.stocardapp.models.StoreDetailResponse
import kotlinx.android.synthetic.main.fragment_home_screen.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeScreen.newInstance] factory method to
 * create an instance of this fragment.
 */

class HomeScreen : Fragment() {
    lateinit var spinAnim: Animation;
    lateinit var btANim: Animation;

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var mAPIService: UserApi? = null
    var dispLst = ArrayList<StoreDetail>()
    var stList = ArrayList<StoreDetail>()
    var temp = ArrayList<String>()
    var itemTouchHelper: ItemTouchHelper? = null
    val map: MutableMap<String, RequestBody> = HashMap()
    lateinit var f: String

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
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_home_screen, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val btnAdd = requireView().findViewById(R.id.btn_addStore) as Button
        val stRc = requireView().findViewById(R.id.storeRv) as RecyclerView

        (context as AppCompatActivity).supportActionBar!!.title = "Stocard App"
        //  var cRv = requireView().findViewById<RecyclerView>(R.id.cdRv)
        //   var itemsLayout:  = requireView().findViewById(R.id.nav)
        spinAnim = AnimationUtils.loadAnimation(context, R.anim.spin_animation)
        btANim = AnimationUtils.loadAnimation(context, R.anim.bottom_animation)

        f = requireActivity().intent.getStringExtra("filter").toString()
        val SHARED_PREF_NAME1 = "my_shared_preff"
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(SHARED_PREF_NAME1, Context.MODE_PRIVATE)
        var s = sharedPreferences.getString("filterid", "defaultName")
        Log.d("fiiff", s!!)

        //filter selection
//        btnflt.setOnClickListener {
////            startActivity(Intent(context?.applicationContext,AddStoreActivity::class.java))
//            val intent = Intent(this@HomeScreen.context, FilterActivity::class.java)
//            startActivity(intent)
//        }

        //checkNetwork()
        mAPIService = ApiUtils.apiService

        val SHARED_PREF_NAME = "my_shared_preff"
        val sharedPreference =
                this.activity?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val token = "Bearer " + (sharedPreference?.getString("token", "defaultName"))

//        switch1.isClickable = false

        if (f == "all") {
//            switch1.isClickable = true
//            switch1.isChecked = true
            map["filter_id"] = toPart(s!!) as RequestBody

            mAPIService!!.storeDetails(token!!, "Filter", map).enqueue(object :
                    Callback<StoreDetailResponse> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                        call: Call<StoreDetailResponse>,
                        response: Response<StoreDetailResponse>
                ) {

                    if(response.body()?.success == true) {
                        //  var ad = StoreAdapter(context!!.applicationContext, stList)
                        var dt = response.body()?.data
                        if (dt != null) {
                            for (d in dt) {
                                var v = d
                                stList.add(v)
                            }
                            //Log.d("arrayleng1", stList.size.toString())
                            if (stList.size == 0) {
                                var fr = view?.findViewById<FrameLayout>(R.id.frame)
                                Log.d("visible", stRc?.visibility.toString())
                                stRc?.isVisible = false
                                Log.d("visiblea", stRc?.visibility.toString())
                                var img: ImageView? = ImageView(context)
                                var msg: TextView? = TextView(context)
                                img?.load(R.drawable.empty)
                                val title = SpannableString("Add Your First Store!!")
                                title.setSpan(
                                        ForegroundColorSpan(Color.parseColor("#342ea9")),
                                        0,
                                        title.length,
                                        0
                                )
                                msg?.setText(title)
                                msg?.setX(200.00F)
                                msg?.setY(500.00F)
                                msg?.textSize = 28F
                                img?.maxHeight = 100
                                img?.maxWidth = 100
                                img?.minimumHeight = 100
                                img?.minimumWidth = 100
                                img?.animation = spinAnim
                                fr?.layoutParams = fr?.getLayoutParams()
                                fr?.addView(img)
                                fr?.addView(msg)
                            } else {
                                // cRv.adapter = adapter
                                checkNetwork()
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
                                adapter.notifyDataSetChanged()
                                stRc.adapter = adapter
                                var i = response.body()?.data
                            }
                            // stRc.layoutManager= GridLayoutManager(context, 2)
                        }
                    }
                    else
                    {
                        Toast.makeText(context,"Something went wrong!",Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<StoreDetailResponse>, t: Throwable) {
                    //checkNetwork()
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }
            })
        } else {
            allstore()
        }
        //add store activity
        btnAdd.setOnClickListener {
//            startActivity(Intent(context?.applicationContext,AddStoreActivity::class.java))
            val intent = Intent(this@HomeScreen.context, AddStoreActivity::class.java)
            startActivity(intent)
        }

//        switch1.setOnCheckedChangeListener({ _ , isChecked ->
//            if(isChecked) "Switch1:ON" else {
//                //allstore()
//               requireContext().startActivity(Intent(context,HomeActivity::class.java))
//                switch1.isClickable = false
//            }
//        })
    }

    private fun allstore() {
        val SHARED_PREF_NAME = "my_shared_preff"
        val sharedPreference =
                this.activity?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val token = "Bearer " + (sharedPreference?.getString("token", "defaultName"))
        val stRc = requireView().findViewById(R.id.storeRv) as RecyclerView

        map[""] = toPart("") as RequestBody
        mAPIService!!.storeDetails(token!!, "StoreList", map).enqueue(object :
                Callback<StoreDetailResponse> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                    call: Call<StoreDetailResponse>,
                    response: Response<StoreDetailResponse>
            ) {
                //  var ad = StoreAdapter(context!!.applicationContext, stList)
                if(response.body()?.success==true) {
                    var dt = response.body()?.data
                    if (dt != null) {
                        for (d in dt) {
                            var v = d
                            stList.add(v)
                        }
                        //Log.d("arrayleng1", stList.size.toString())
                        if (dt == null) {
                          allstore()
                        } else {
                            // cRv.adapter = adapter
                            //checkNetwork()
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
                        // stRc.layoutManager= GridLayoutManager(context, 2)
                    }
                }
                else
                {
                    allstore()

                    Toast.makeText(context,"Something went wrong!",Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<StoreDetailResponse>, t: Throwable) {
                //checkNetwork()
                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    fun toPart(data: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
    }

    fun checkNetwork() {
        //Check Network
        val stRc = requireView().findViewById(R.id.storeRv) as RecyclerView
        var cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cm.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    var fr = view?.findViewById<FrameLayout>(R.id.frame)

                    activity?.runOnUiThread(Runnable {
                        // Stuff that updates the UI
                        stRc?.isVisible = false
                        Toast.makeText(requireContext(), "hhhhhh", Toast.LENGTH_LONG).show()
                        var img: ImageView? = ImageView(context)
                        var msg: TextView? = TextView(context)
                        img?.load(R.drawable.net)
                        val title = SpannableString("Network Connection Lost.")
                        title.setSpan(
                                ForegroundColorSpan(Color.parseColor("#342ea9")),
                                0,
                                title.length,
                                0
                        )
                        msg?.setText(title)
                        msg?.setX(300.00F)
                        msg?.setY(500.00F)
                        msg?.textSize = 28F
                        img?.maxHeight = 80
                        img?.maxWidth = 80
                        img?.minimumHeight = 80
                        img?.minimumWidth = 80
                        fr?.layoutParams = fr?.getLayoutParams()
                        fr?.addView(img)
                    })
                }
            }
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.menu_filterser, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.ser -> {
                val stRc = requireView().findViewById(R.id.storeRv) as RecyclerView
                var sec: androidx.appcompat.widget.SearchView = item.actionView as androidx.appcompat.widget.SearchView
                val searchEditText: EditText =
                        sec.findViewById(androidx.appcompat.R.id.search_src_text)
                searchEditText.setTextColor(resources.getColor(R.color.white))

                if (item != null) {
                    sec.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                            androidx.appcompat.widget.SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            if (newText!!.isNotEmpty()) {
                                dispLst.clear()
                                val se = newText.toLowerCase(Locale.getDefault())
                                stList.forEach {
                                    if (it.stname.toLowerCase(Locale.getDefault()).contains(se)) {
                                        dispLst.add(it)
                                    }
                                }

                                stRc?.adapter!!.notifyDataSetChanged()
                            } else {
                                dispLst.clear()
                                dispLst.addAll(stList)
                                stRc?.adapter!!.notifyDataSetChanged()
                            }
                            return true
                        }
                    })
                }
                true
            }
            R.id.btnFilter -> {
                if (f != "all") {
                    f = "filtered"
                    //Toast.makeText(context,"" + item.title,Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@HomeScreen.context, FilterActivity::class.java)
                    startActivity(intent)
                }
                else{
                    startActivity(Intent(this@HomeScreen.context, HomeActivity::class.java))
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeScreen.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                HomeScreen().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
