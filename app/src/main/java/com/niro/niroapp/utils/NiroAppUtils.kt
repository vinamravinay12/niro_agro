package com.niro.niroapp.utils

import android.content.Context
import androidx.lifecycle.ViewModelProvider

object NiroAppUtils {


    @JvmStatic
    fun getPxFromDP(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }


}