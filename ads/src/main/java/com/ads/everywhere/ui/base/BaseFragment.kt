package com.ads.everywhere.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ads.everywhere.data.di.CustomKoinComponent

abstract class BaseFragment(private val layoutRes:Int):Fragment(), CustomKoinComponent {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(layoutRes,container,false)
}