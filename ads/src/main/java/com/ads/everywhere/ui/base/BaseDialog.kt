package com.ads.everywhere.ui.base

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ads.everywhere.R
import com.ads.everywhere.data.di.CustomKoinComponent

abstract class BaseDialog(private val layoutRes:Int):DialogFragment(), CustomKoinComponent {
    companion object{
        const val DIALOG_SIZE_TO_SCREEN = 0.9
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.MyDialog)
    }


    override fun onResume() {
        super.onResume()
        dialog?.window?.let { window->

            val width =  (resources.displayMetrics.widthPixels * DIALOG_SIZE_TO_SCREEN).toInt()
            window.setGravity(Gravity.CENTER)

            window.attributes = window.attributes.apply {
                this.width = width
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =inflater.inflate(layoutRes,container,false )
}