package com.hanzdev.silangkata

import android.content.Context
import org.json.JSONObject

/** Satu petunjuk (mendatar atau menurun) beserta jawabannya. */
class Clue(
    val number: Int,
    val row: Int,
    val col: Int,
    val across: Boolean,
    val answer: String,
    val text: String
) {
    fun covers(r: Int, c: Int): Boolean =
        if (across) r == row && c >= col && c < col + answer.length
        else c == col && r >= row && r < row + answer.length

    fun indexOf(r: Int, c: Int): Int = if (across) c - col else r - row

    fun cellAt(index: Int): Pair<Int, Int> =
        if (across) Pair(row, col + index) else Pair(row + index, col)
}

/** Satu level teka-teki silang. Jawaban sudah diperiksa oleh tools/validate_levels.py. */
class Level(
    val id: Int,
    val title: String,
    val pack: String,
    val difficulty: String,
    val rows: Int,
    val cols: Int,
    val clues: List<Clue>
) {
    /** Huruf jawaban per kotak; BLACK untuk kotak hitam. */
    val solution: Array<CharArray> = Array(rows) { CharArray(cols) { BLACK } }

    /** Nomor petunjuk yang tampil di pojok kotak; 0 jika tidak ada. */
    val numbers: Array<IntArray> = Array(rows) { IntArray(cols) }

    init {
        for (clue in clues) {
            numbers[clue.row][clue.col] = clue.number
            for (i in clue.answer.indices) {
                val (r, c) = clue.cellAt(i)
                solution[r][c] = clue.answer[i]
            }
        }
    }

    fun isLetter(r: Int, c: Int): Boolean = solution[r][c] != BLACK

    fun clueOf(r: Int, c: Int, across: Boolean): Clue? =
        clues.firstOrNull { it.across == across && it.covers(r, c) }

    companion object {
        const val BLACK = '#'
        const val EMPTY = ' '

        fun loadAll(context: Context): List<Level> {
            val text = context.assets.open("levels.json").bufferedReader().use { it.readText() }
            val array = JSONObject(text).getJSONArray("levels")
            return List(array.length()) { parse(array.getJSONObject(it)) }
        }

        private fun parse(obj: JSONObject): Level {
            val rows = obj.getInt("rows")
            val cols = obj.getInt("cols")
            val words = obj.getJSONArray("words")
            val entries = List(words.length()) { words.getJSONObject(it) }

            // Nomor diberikan urut baris lalu kolom, untuk kotak awal kata mendatar atau menurun.
            val starts = HashSet<Int>()
            for (w in entries) starts.add(w.getInt("row") * 100 + w.getInt("col"))
            val numberAt = HashMap<Int, Int>()
            var next = 0
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val key = r * 100 + c
                    if (starts.contains(key)) {
                        next++
                        numberAt[key] = next
                    }
                }
            }

            val clues = entries.map { w ->
                val key = w.getInt("row") * 100 + w.getInt("col")
                Clue(
                    number = numberAt.getValue(key),
                    row = w.getInt("row"),
                    col = w.getInt("col"),
                    across = w.getBoolean("across"),
                    answer = w.getString("answer"),
                    text = w.getString("clue")
                )
            }
            return Level(
                id = obj.getInt("id"),
                title = obj.getString("title"),
                pack = obj.optString("pack", "indonesia"),
                difficulty = obj.optString("difficulty", "Mudah"),
                rows = rows,
                cols = cols,
                clues = clues
            )
        }
    }
}
