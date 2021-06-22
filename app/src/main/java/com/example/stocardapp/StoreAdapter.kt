 package com.example.stocardapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.example.stocardapp.models.FavoriteResponse
import com.example.stocardapp.models.StoreDetail
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import java.util.*

class StoreAdapter(
    var ctx: Context,
    var arr: ArrayList<StoreDetail>,
    var lisener: OnStartDragListener
) : RecyclerView.Adapter<StoreAdapter.ViewHolder>(), IteamTouchHelperAdapter {
    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var Simg = v.findViewById<ImageView>(R.id.storeRvImg)
        var Stit = v.findViewById<TextView>(R.id.storeImtit)
        var stcv = v.findViewById<CardView>(R.id.storeCv)
        var favBtn = v.findViewById<ImageView>(R.id.fav)
       /* var sid = 0
        var scon = ""
        var snm = ""
        var sloc = ""
        var isFav = ""

        init {

        }*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        var v = LayoutInflater.from(ctx).inflate(R.layout.store_details, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

//

//        if (position == arr.lastIndex){
//            val params = holder.itemView.layoutParams as FrameLayout.LayoutParams
//            params.bottomMargin = 100
//            holder.itemView.layoutParams = params
//        }else{
//            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
//            params.bottomMargin = 0
//            holder.itemView.layoutParams = params
//        }



        //  holder.Simg.setImageResource(list[position].imgSrc)
        holder.Stit.text = arr[position].stname

        var u: Uri = Uri.parse(arr[position].store_img)
        Log.d("img", u.toString())
//            holder.Simg.setImageURI(u)
        holder.Simg.load(u.toString())
        if (arr[position].is_favorite == "true") {
            holder.favBtn.setColorFilter(Color.parseColor("#F86459"));
        }




        holder.stcv.setOnClickListener {
            // Toast.makeText(v.context,i.toString(),Toast.LENGTH_LONG).show()
            val i = Intent(ctx, CardListActivity::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("storeId", arr[position].id)
            i.putExtra("storeNm", arr[position].stname)
            i.putExtra("storeCon", arr[position].stcontact)
            i.putExtra("storeLoc", arr[position].stlocation)

            SharedPrefManager.getInstance(ctx).saveStore(
                arr[position].id,
                arr[position].stname,
                arr[position].stlocation,
                arr[position].stcontact
            )
            ctx.startActivity(i)
        }

        holder.favBtn.setOnClickListener {
            var mAPIService: UserApi? = null
            mAPIService = ApiUtils.apiService
            val SHARED_PREF_NAME = "my_shared_preff"
            val sharedPreference = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val token = "Bearer " + sharedPreference.getString("token", "defaultName")
            val map: MutableMap<String, RequestBody> = HashMap()
            map["store_id"] = toPart(arr[position].id.toString()) as RequestBody
            if(arr[position].is_favorite == "false")
            {
                mAPIService.addFav(token!!, "AddFavorite", map).enqueue(object :
                    Callback<FavoriteResponse> {
                    override fun onResponse(
                        call: Call<FavoriteResponse>,
                        response: retrofit2.Response<FavoriteResponse>
                    ) {
                        arr[position].is_favorite = "true"
                        holder.favBtn.setColorFilter(Color.parseColor("#F86459"));
                        Log.d("resshh", response.toString())
                        //     Log.d("iddd",cid.toString())
                        Toast.makeText(ctx, response.body()?.message, Toast.LENGTH_LONG).show()

                        if(response.body()?.success == true) {
                            arr[position].is_favorite = "true"
                            holder.favBtn.setColorFilter(Color.parseColor("#F86459"));
                            Log.d("resshh", response.toString())
                            //     Log.d("iddd",cid.toString())
                            Toast.makeText(ctx, response.body()?.message, Toast.LENGTH_LONG).show()
                        }
                        else
                        {
                            Toast.makeText(ctx,"Something went wrong!",Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<FavoriteResponse>, t: Throwable) {
                        Toast.makeText(ctx, t.message, Toast.LENGTH_LONG).show()
                    }
                })
            }
            else
            {
                mAPIService.removeFav(token!!, "RemoveFavorite", map).enqueue(object :
                    Callback<FavoriteResponse> {
                    override fun onResponse(
                        call: Call<FavoriteResponse>,
                        response: retrofit2.Response<FavoriteResponse>
                    ) {
                        arr[position].is_favorite = "false"
                        holder.favBtn.setColorFilter(Color.parseColor("#342ea9"));
                        Log.d("resshh", response.toString())
                        //     Log.d("iddd",cid.toString())
                        Toast.makeText(ctx, response.body()?.message, Toast.LENGTH_LONG).show()
                        if(response.body()?.success == true) {
                            arr[position].is_favorite = "false"
                            holder.favBtn.setColorFilter(Color.parseColor("#342ea9"));
                            Log.d("resshh", response.toString())
                            //     Log.d("iddd",cid.toString())
                            Toast.makeText(ctx, response.body()?.message, Toast.LENGTH_LONG).show()
//                           ctx.startActivity(Intent(ctx,HomeActivity::class.java))
                        }
                        else
                        {
                            Toast.makeText(ctx,"Something went wrong!",Toast.LENGTH_LONG).show()
                        }

                    }

                    override fun onFailure(call: Call<FavoriteResponse>, t: Throwable) {
                        Toast.makeText(ctx, t.message, Toast.LENGTH_LONG).show()
                    }
                })
            }
        }

    }

    override fun getItemCount(): Int {
        return arr.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(arr, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
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
