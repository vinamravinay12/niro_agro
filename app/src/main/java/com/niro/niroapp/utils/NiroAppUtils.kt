package com.niro.niroapp.utils

import android.content.Context

class NiroAppUtils {

    companion object {
        @JvmStatic
        fun getPxFromDP(context: Context, dp : Float) : Float {
            return dp * context.resources.displayMetrics.density
        }
    }
}