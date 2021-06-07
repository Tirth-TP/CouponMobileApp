package com.example.stocardapp

interface IteamTouchHelperAdapter {
    fun onItemMove(fromPosition:Int,toPosition:Int):Boolean
    fun onItemDismiss(position:Int)
}