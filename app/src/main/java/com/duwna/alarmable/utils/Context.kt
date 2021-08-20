package com.duwna.alarmable.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.text.Layout
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect


fun Context.dpToPx(dp: Int): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        this.resources.displayMetrics
    )
}

fun Context.dpToIntPx(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        this.resources.displayMetrics
    ).toInt()
}

fun Context.attrValue(@AttrRes res: Int): Int {
    val tv = TypedValue()
    return if (theme.resolveAttribute(res, tv, true)) tv.data
    else throw Resources.NotFoundException("Resource with id $res not found")
}

fun Context.hideKeyBoard(view: View) {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.toast(msg: Any?, tag: String = this::class.java.simpleName) {
    Toast.makeText(this, msg.toString(), Toast.LENGTH_SHORT).show()
}

fun Layout.getLineBottomWithoutPadding(line: Int): Int {
    var lineBottom = getLineBottomWithoutSpacing(line)
    if (line == lineCount - 1) lineBottom -= bottomPadding
    return lineBottom
}

fun Layout.getLineBottomWithoutSpacing(line: Int): Int {
    val lineBottom = getLineBottom(line)
    val isLastLine = line == lineCount - 1
    val hasLineSpacing = spacingAdd != 0f
    return if (!hasLineSpacing || isLastLine) {
        lineBottom + spacingAdd.toInt()
    } else {
        lineBottom - spacingAdd.toInt()
    }
}

fun <T> Flow<T>.collectOnLifecycle(
    lifecycleOwner: LifecycleOwner,
    onChange: (T) -> Unit
) {
    lifecycleOwner.lifecycleScope.launchWhenStarted { collect { onChange(it) } }
}