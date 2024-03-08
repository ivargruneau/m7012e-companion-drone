package com.example.cdroneapp.utils

import androidx.lifecycle.MutableLiveData


/**
 * Description :djiToastLD的存储和提供工具类，用于向其他ViewModel提供djiToastLD
 *
 * @author: Byte.Cai
 * date : 2022/6/16
 *
 *
 * Copyright (c) 2022, DJI All Rights Reserved.
 */
object DJIToastUtil {
    var dJIToastLD: MutableLiveData<DJIToastResult>? = null
}