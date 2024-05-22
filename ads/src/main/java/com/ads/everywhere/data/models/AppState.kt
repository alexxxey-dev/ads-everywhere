package com.ads.everywhere.data.models

enum class AppState {
    OPEN, SHOW_AD, CLOSE;

    fun toInt():Int{
        return when(this){
            OPEN->0
            SHOW_AD->1
            CLOSE->2
        }
    }

    companion object{
        fun fromInt(value:Int):AppState{
            return when(value){
                0->OPEN
                1->SHOW_AD
                else ->CLOSE
            }
        }
    }

}