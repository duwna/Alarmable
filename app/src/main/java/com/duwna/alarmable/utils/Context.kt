package com.duwna.alarmable.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

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

fun Fragment.requestReadStoragePermission() {
    if (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        != PackageManager.PERMISSION_GRANTED
    ) {
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }
}

const val PERMISSION_REQUEST_CODE = 200
const val PICK_MELODY_CODE = 100