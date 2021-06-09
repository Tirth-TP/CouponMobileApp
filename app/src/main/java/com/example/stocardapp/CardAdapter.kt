package com.example.stocardapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.BlurTransformation
import com.example.stocardapp.models.CardDetail
import com.example.stocardapp.models.DeleteResponse
import com.example.stocardapp.models.HideCard
import com.example.stocardapp.models.HideCardResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import java.util.*
import kotlin.collections.ArrayList

class CardAdapter(var ctx: Context, var arr:ArrayList<CardDetail>,var lisener:OnStartDragListener): RecyclerView.Adapter<CardAdapter.ViewHolder>(),IteamTouchHelperAdapter
{
    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v) {

        var Simg = v.findViewById<ImageView>(R.id.storeRvImg)
        var Ctit = v.findViewById<TextView>(R.id.storeImtit)
        var stcv =  v.findViewById<CardView>(R.id.storeCv)
        var hdBtn = v.findViewById<ImageView>(R.id.hide)
        var cid = 0
        var cnm = ""
        var cnum =""
        var crd=""
        var cdtl = ""
        var cdt = ""
        var st=""
        var act=""
        var u:Uri?=null
        var iu=""

        init {
            hdBtn.setOnClickListener {
                var mAPIService: UserApi? = null
                mAPIService = ApiUtils.apiService
                val SHARED_PREF_NAME = "my_shared_preff"
                val sharedPreference = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
                val token = "Bearer " + sharedPreference.getString("token", "defaultName")
                val map: MutableMap<String, RequestBody> = HashMap()
                map["card_id"] = toPart(cid.toString()) as RequestBody

                mAPIService.hideCard(token!!, "HideCard", map).enqueue(object :
                        Callback<HideCardResponse> {
                    override fun onResponse(
                            call: Call<HideCardResponse>,
                            response: retrofit2.Response<HideCardResponse>
                    ) {
                         Log.d("resshh",response.toString())
                   //     Log.d("iddd",cid.toString())
                        Toast.makeText(ctx,response.body()?.message,Toast.LENGTH_LONG).show()

                        Simg.setImageResource(R.drawable.ic_baseline_lock_24)
                        hdBtn.visibility = View.INVISIBLE
                        stcv.setOnClickListener{
                            val i :Intent
                            i = Intent(ctx, LockActivity::class.java)
                            i.putExtra("cardId",cid)
                            i.putExtra("cardNm",cnm)
                            i.putExtra("cardNum",cnum)
                            i.putExtra("cardRwd",crd)
                            i.putExtra("cardDetl",cdtl)
                            i.putExtra("cardDt",cdt)
                            i.putExtra("cardImg",u.toString())
                            v.context.startActivity(i)
                        }
                    }
                    override fun onFailure(call: Call<HideCardResponse>, t: Throwable) {
                        Toast.makeText(ctx, t.message, Toast.LENGTH_LONG).show()
                    }
                })
            }
            stcv.setOnClickListener {
                // Toast.makeText(v.context,i.toString(),Toast.LENGTH_LONG).show()
                val i :Intent
                if(st=="hide")
                {
                    i = Intent(ctx, LockActivity::class.java)
                }
                else
                {
                    i = Intent(ctx, CardDetailActivity::class.java)
                }
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("cardNm",cnm)
                i.putExtra("cardId",cid)
                i.putExtra("cardNum",cnum)
                i.putExtra("cardRwd",crd)
                i.putExtra("cardDetl",cdtl)
                i.putExtra("cardDt",cdt)
                i.putExtra("cardImg",u.toString())
                i.putExtra("status",st)
                Log.d("imgggggg",u.toString())
                v.context.startActivity(i)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(ctx).inflate(R.layout.card_details,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.Ctit.text=arr[position].cardname
        holder.cid = arr[position].id
        holder.cnm = arr[position].cardname
        holder.cnum = arr[position].cardno
        holder.crd = arr[position].rewardpercen
        holder.cdtl = arr[position].carddetail
        holder.cdt = arr[position].expdate
        holder.st = arr[position].status
        holder.act = arr[position].isActive
        holder.u = Uri.parse(arr[position].card_img)
        holder.iu = arr[position].is_Used
        if(holder.iu == "true")
        {
            holder.stcv.isEnabled = false
           // holder.stcv.setBackgroundColor(Color.LTGRAY)
           // holder.Simg.visibility = View.INVISIBLE
            holder.Ctit.setText("Coupon Expired")
            holder.Ctit.setTextColor(Color.RED)

        }
        if(arr.get(position).isActive == "false")
        {
            holder.stcv.isEnabled = false
           // holder.Simg.visibility = View.INVISIBLE
            holder.Ctit.setText("Coupon Expired")
            holder.Ctit.setTextColor(Color.RED)
            holder.Ctit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10F)
            holder.stcv.setBackgroundResource(R.drawable.btn_blank_corners)
            val layoutParams = holder.Ctit.layoutParams as LinearLayout.LayoutParams
            layoutParams.topMargin = 32
            holder.Simg.setColorFilter(Color.parseColor("#66000000"))
            holder.Ctit.layoutParams = layoutParams
            holder.hdBtn.visibility = View.INVISIBLE
        }
        if(holder.st == "hide")
        {
            holder.Simg.setImageResource(R.drawable.ic_baseline_lock_24)
            holder.hdBtn.visibility = View.INVISIBLE
        }
        else {

            Log.d("img", holder.u.toString())
//            holder.Simg.setImageURI(u)
            holder.Simg.load(holder.u.toString())
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(arr,fromPosition,toPosition)
        notifyItemMoved(fromPosition,toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
        arr.removeAt(position)
        notifyItemRemoved(position)
    }

    fun toPart(data: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
    }
}