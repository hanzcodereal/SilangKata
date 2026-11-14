package com.hanzdev.silangkata

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

/** Daftar level per paket. Level berikutnya dalam paket terbuka setelah level sebelumnya selesai. */
class LevelsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val progress = Progress(this)
        val levels = Level.loadAll(this)

        val list = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(16), dp(20), dp(20))
        }
        list.addView(heading("Pilih Level", 26f), lp(MATCH, WRAP, 0f, 4))

        var currentPack = ""
        for ((index, lv) in levels.withIndex()) {
            if (lv.pack != currentPack) {
                currentPack = lv.pack
                list.addView(heading(packLabel(lv.pack), 18f), lp(MATCH, WRAP, 0f, 8))
            }

            val previous = levels.getOrNull(index - 1)?.takeIf { it.pack == lv.pack }
            val locked = previous != null && !progress.isDone(previous.id)
            val done = progress.isDone(lv.id)

            val label = when {
                done -> "✓ ${lv.id}. ${lv.title} · ${lv.difficulty}"
                locked -> "🔒 ${lv.id}. ${lv.title} · ${lv.difficulty}"
                else -> "${lv.id}. ${lv.title} · ${lv.difficulty}"
            }
            val fill = when {
                done -> Palette.TEAL
                locked -> Color.parseColor("#E4DED1")
                else -> Palette.CELL
            }
            val textColor = when {
                done -> Color.WHITE
                locked -> Palette.INK_SOFT
                else -> Palette.INK
            }
            list.addView(
                button(label, fill, textColor, 16f) {
                    if (locked) {
                        Toast.makeText(this, "Selesaikan level sebelumnya dulu.", Toast.LENGTH_SHORT).show()
                    } else {
                        GameActivity.start(this, lv.id)
                    }
                },
                lp(MATCH, dp(52), 0f, 5)
            )
        }

        val scroll = ScrollView(this).apply {
            setBackgroundColor(Palette.PAPER)
            isFillViewport = true
            addView(list)
        }
        setContentView(scroll)
    }

    private fun packLabel(pack: String): String =
        if (pack == "indonesia") "Paket Indonesia" else "Paket Internasional"

    private fun heading(text: String, sp: Float): TextView =
        TextView(this).apply {
            this.text = text
            textSize = sp
            setTextColor(Palette.INK)
            setTypeface(Typeface.DEFAULT_BOLD)
            gravity = Gravity.START
        }
}
