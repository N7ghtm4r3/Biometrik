package com.tecknobit.biometrik

import com.sun.jna.Library
import com.sun.jna.WString

internal interface NativeEngine : Library {

    fun requestAuth(
        reason: WString,
    ): Int

}