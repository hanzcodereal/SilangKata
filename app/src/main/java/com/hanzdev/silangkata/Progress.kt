package com.hanzdev.silangkata

import android.content.Context

/** Menyimpan progres pemain di perangkat (SharedPreferences). */
class Progress(context: Context) {
    private val prefs = context.getSharedPreferences("silang_kata", Context.MODE_PRIVATE)

    fun isDone(levelId: Int): Boolean = prefs.getBoolean("done_$levelId", false)

    fun markDone(levelId: Int) {
        prefs.edit().putBoolean("done_$levelId", true).apply()
    }

    fun savedGrid(levelId: Int): String? = prefs.getString("grid_$levelId", null)

    fun saveGrid(levelId: Int, value: String) {
        prefs.edit().putString("grid_$levelId", value).apply()
    }

    var hints: Int
        get() = prefs.getInt("hints", START_HINTS)
        set(value) {
            prefs.edit().putInt("hints", value).apply()
        }

    companion object {
        const val START_HINTS = 5
        const val REWARD_HINTS = 2
    }
}
