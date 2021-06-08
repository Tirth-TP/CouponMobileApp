package com.example.stocardapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.stocardapp.models.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import java.util.*


class CardListActivity : AppCompatActivity() {

    //var cards = arrayListOf<CardSer>()
    var dispLst = ArrayList<CardDetail>()
    var itemTouchHelper: ItemTouchHelper? = null
    var stList = ArrayList<CardDetail>()

    private lateinit var layoutManager: RecyclerView.LayoutManager

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_list)

        supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.header_black)

        val SHARED_PREF_NAME = "my_shared_preff"
        val sharedPreference = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val txtTit = findViewById<TextView>(R.id.txtTitle)
        txtTit.setText("Your Coupons")
        val ibk = findViewById<ImageView>(R.id.imgBack)
        ibk.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        //img = findViewById<>()ViewById(R.id.nav_profilePic)

        var sid = sharedPreference.getInt("sid", 0)
        var snm = sharedPreference.getString("stname", "defaultName")
        var scon = sharedPreference.getString("contact", "defaultName")
        var sloc = sharedPreference.getString("stlocation", "defaultName")

        var getNm = findViewById<EditText>(R.id.getStNm)
        var getCn = findViewById<EditText>(R.id.getStCn)
        var getLc = findViewById<EditText>(R.id.getStLc)
        var addCd = findViewById<Button>(R.id.btn_addCard)
        var del_st = findViewById<Button>(R.id.btnSdel)
        var cRv = findViewById<RecyclerView>(R.id.cdRv)
        var update_st = findViewById<Button>(R.id.btnSUpdate)

        // var hdBtn = findViewById<ImageView>(R.id.hide)

        var mAPIService: UserApi? = null
        mAPIService = ApiUtils.apiService

        getNm.setText(snm)
        getCn.setText(scon)
        getLc.setText(sloc)

        addCd.setOnClickListener {
            val intent = Intent(this, AddCardActivity::class.java)
            intent.putExtra("storeId", sid)
            startActivity(intent)
        }


//        cards.add(CardSer("Card 1"))
//        cards.add(CardSer("Offer 1"))
//        cards.add(CardSer("Card 2"))
//        cards.add(CardSer("Offer 2"))
//        cards.add(CardSer("Discount"))

//     layoutManager = LinearLayoutManager(this)
//        cRv.layoutManager = layoutManager
//        dispLst.addAll(cards)
//    var adapter = SerAdapter(this,dispLst,object:OnStartDragListener{
//        override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {
//                itemTouchHelper?.startDrag(viewHolder!!)
//        }
//    })
//    cRv.adapter = adapter
//    val callBack = MyItemTouchHelperCallBack(adapter)
//    itemTouchHelper = ItemTouchHelper(callBack)
//    itemTouchHelper?.attachToRecyclerView(cRv)


        val token = "Bearer " + sharedPreference.getString("token", "defaultName")
        val map: MutableMap<String, RequestBody> = HashMap()
        map["st_id"] = toPart(sid.toString()) as RequestBody

        val s = intent.getStringExtra("s_id")


        //card list
        Log.d("msg", sid.toString())
        mAPIService.cardList(token!!, "CardDetail", map).enqueue(object :
                Callback<CardDetailResponse> {
            override fun onResponse(
                    call: Call<CardDetailResponse>,
                    response: retrofit2.Response<CardDetailResponse>
            ) {
                //var ad = CardAdapter(this@CardListActivity, stList)
                var dt = response.body()?.data
                if (dt != null) {
                    for (d in dt) {
                        var v = d
                        stList.add(v)
                    }
                    // cRv.adapter = ad
                    dispLst.addAll(stList)
                    var adapter =
                            CardAdapter(this@CardListActivity, dispLst, object : OnStartDragListener {
                                override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {
                                    itemTouchHelper?.startDrag(viewHolder!!)
                                }
                            })
                    cRv.adapter = adapter
                    val callBack = MyItemTouchHelperCallBack(adapter)
                    itemTouchHelper = ItemTouchHelper(callBack)
                    itemTouchHelper?.attachToRecyclerView(cRv)
                    //  Toast.makeText(this@CardListActivity,response.body()?.message,Toast.LENGTH_LONG).show()
                    //  var i = response.body()?.data
                }
            }

            override fun onFailure(call: Call<CardDetailResponse>, t: Throwable) {
                Toast.makeText(this@CardListActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })


        del_st.setOnClickListener {

            //store update

            update_st.setOnClickListener {
                map["id"] = toPart(sid.toString()) as RequestBody
                map["contact"] = toPart(getCn.text.toString())
                map["location"] = toPart(getLc.text.toString())
                mAPIService.storeUpdate(token!!, "StoreUpdate", map).enqueue(object :
                        Callback<StoreUpdateResponse> {
                    override fun onResponse(
                            call: Call<StoreUpdateResponse>,
                            response: retrofit2.Response<StoreUpdateResponse>
                    ) {
                        Toast.makeText(this@CardListActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                        val i = (Intent(applicationContext, HomeActivity::class.java))
                        startActivity(i)
                    }

                    override fun onFailure(call: Call<StoreUpdateResponse>, t: Throwable) {
                        Toast.makeText(this@CardListActivity, t.message, Toast.LENGTH_LONG).show()
                    }
                })
            }

            //store delete
            del_st.setOnClickListener {

                map["id"] = toPart(sid.toString()) as RequestBody
                mAPIService.delStore(token!!, "StoreDelete", map).enqueue(object :
                        Callback<DeleteResponse> {
                    override fun onResponse(
                            call: Call<DeleteResponse>,
                            response: retrofit2.Response<DeleteResponse>
                    ) {
                        Toast.makeText(
                                this@CardListActivity,
                                response.body()?.message,
                                Toast.LENGTH_LONG
                        ).show()
                        val i = (Intent(applicationContext, HomeActivity::class.java))
                        startActivity(i)
                    }

                    override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                        Toast.makeText(this@CardListActivity, t.message, Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }

        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.menu_ser, menu)
            var item: MenuItem = menu!!.findItem(R.id.ser)
            var cRv = findViewById<RecyclerView>(R.id.cdRv)

            if (item != null) {
                var sec: androidx.appcompat.widget.SearchView =
                        item.actionView as androidx.appcompat.widget.SearchView
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
                                if (it.cardname.toLowerCase(Locale.getDefault()).contains(se)) {
                                    dispLst.add(it)
                                }
                            }
                            cRv.adapter!!.notifyDataSetChanged()
                        } else {
                            dispLst.clear()
                            dispLst.addAll(stList)
                            cRv.adapter!!.notifyDataSetChanged()
                        }
                        return true
                    }
                })
            }
            return super.onCreateOptionsMenu(menu)
        }

        fun toPart(data: String): RequestBody {
            return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
        }

    }