package com.randos.doctorsapp.utils

import android.os.Build
import javax.inject.Inject

class SdkVersionProvider @Inject constructor() {
    fun get() = Build.VERSION.SDK_INT
}