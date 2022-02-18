package com.example.couponMobileApp

interface IteamTouchHelperAdapter {
    fun onItemMove(fromPosition:Int,toPosition:Int):Boolean
    fun onItemDismiss(position:Int)
}