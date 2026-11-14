package com.hanzdev.silangkata

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MenuActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(48), dp(24), dp(24))
        }

        root.addView(tileRow("SILANG", 0), lp(WRAP, WRAP, 0f, 3))
        root.addView(tileRow("KATA", 3), lp(WRAP, WRAP, 0f, 3))

        root.addView(
            TextView(this).apply {
                text = "Teka-teki silang Bahasa Indonesia"
                textSize = 15f
                setTextColor(Palette.INK_SOFT)
                gravity = Gravity.CENTER
            },
            lp(MATCH, WRAP, 0f, 12)
        )

        root.addView(button("Mulai Main", Palette.TEAL, Color.WHITE) { startGame() }, lp(MATCH, dp(54), 0f, 6))
        root.addView(
            button("Pilih Level", Palette.CELL, Palette.INK) {
                startActivity(Intent(this, LevelsActivity::class.java))
            },
            lp(MATCH, dp(54), 0f, 6)
        )
        root.addView(button("Tentang & Dukung", Palette.CELL, Palette.INK) { showAbout() }, lp(MATCH, dp(54), 0f, 6))

        root.addView(
            TextView(this).apply {
                text = "Versi ${AppInfo.VERSION} · ${AppInfo.DEVELOPER}"
                textSize = 13f
                setTextColor(Palette.INK_SOFT)
                gravity = Gravity.CENTER
                setTypeface(Typeface.DEFAULT)
            },
            lp(MATCH, WRAP, 0f, 24)
        )

        val scroll = ScrollView(this).apply {
            setBackgroundColor(Palette.PAPER)
            isFillViewport = true
            addView(root)
        }
        setContentView(scroll)
    }

    private fun tileRow(word: String, offset: Int): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        for ((i, ch) in word.withIndex()) {
            val tile = TextView(this).apply {
                text = ch.toString()
                gravity = Gravity.CENTER
                textSize = 24f
                setTextColor(Color.WHITE)
                setTypeface(Typeface.DEFAULT_BOLD)
                background = roundedBg(Palette.TILES[(i + offset) % Palette.TILES.size], 10)
                rotation = if (i % 2 == 0) -4f else 4f
            }
            row.addView(tile, lp(dp(44), dp(48), 0f, 3))
        }
        return row
    }

    private fun startGame() {
        val levels = Level.loadAll(this)
        val progress = Progress(this)
        val target = levels.firstOrNull { !progress.isDone(it.id) } ?: levels.first()
        GameActivity.start(this, target.id)
    }

    private fun showAbout() {
        AlertDialog.Builder(this)
            .setTitle("Silang Kata")
            .setMessage(
                "Versi ${AppInfo.VERSION}\n" +
                    "Teka-teki silang Bahasa Indonesia.\n\n" +
                    "Developer: ${AppInfo.DEVELOPER}\n" +
                    "Kontak: ${AppInfo.CONTACT_EMAIL}"
            )
            .setPositiveButton("Dukung") { _, _ -> openUrl(AppInfo.SUPPORT_URL) }
            .setNeutralButton("Kirim Email") { _, _ -> openUrl("mailto:${AppInfo.CONTACT_EMAIL}") }
            .setNegativeButton("Tutup", null)
            .show()
    }

    private fun openUrl(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Tidak ada aplikasi untuk membuka tautan ini.", Toast.LENGTH_SHORT).show()
        }
    }
}
