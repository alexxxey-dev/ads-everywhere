package com.ads.everywhere.base

import androidx.appcompat.app.AppCompatActivity
import com.ads.everywhere.data.di.CustomKoinComponent

abstract class BaseActivity:AppCompatActivity(), CustomKoinComponent {
}