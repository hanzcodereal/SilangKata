package com.hanzdev.silangkata

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

const val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
const val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT

/** Warna dasar: kertas krem, tinta biru tua, aksen kuning dan teal. */
object Palette {
    val PAPER: Int = Color.parseColor("#F5EEDF")
    val CELL: Int = Color.parseColor("#FFFCF5")
    val INK: Int = Color.parseColor("#1E2A38")
    val INK_SOFT: Int = Color.parseColor("#6B7585")
    val HIGHLIGHT: Int = Color.parseColor("#FBE7B5")
    val SELECT: Int = Color.parseColor("#F4B23A")
    val TEAL: Int = Color.parseColor("#1F8A7A")
    val WRONG: Int = Color.parseColor("#D6453D")
    val TILES: IntArray = intArrayOf(
        Color.parseColor("#F4B23A"),
        Color.parseColor("#1F8A7A"),
        Color.parseColor("#D6453D"),
        Color.parseColor("#2F5D8C"),
        Color.parseColor("#8A5CB8"),
        Color.parseColor("#E67E3B")
    )
}

/** Informasi developer yang tampil di aplikasi. */
object AppInfo {
    const val VERSION = "1.0.0"
    const val DEVELOPER = "Hanz Dev Official"
    const val CONTACT_EMAIL = "hanzzcodee@gmail.com"
    const val SUPPORT_URL = "https://saweria.co/hanzreally"
}

fun Context.dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

fun Context.roundedBg(color: Int, radiusDp: Int): GradientDrawable =
    GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dp(radiusDp).toFloat()
        setColor(color)
    }

fun Context.lp(width: Int, height: Int, weight: Float = 0f, margin: Int = 0): LinearLayout.LayoutParams {
    val m = dp(margin)
    return LinearLayout.LayoutParams(width, height, weight).apply { setMargins(m, m, m, m) }
}

/** Tombol berbentuk kartu dengan area sentuh minimal 44dp. */
fun Context.button(label: String, fill: Int, textColor: Int, textSp: Float = 16f, onClick: () -> Unit): TextView =
    TextView(this).apply {
        text = label
        gravity = Gravity.CENTER
        setTextColor(textColor)
        textSize = textSp
        setTypeface(Typeface.DEFAULT_BOLD)
        background = roundedBg(fill, 14)
        minimumHeight = dp(44)
        isClickable = true
        isFocusable = true
        setOnClickListener { onClick() }
    }
