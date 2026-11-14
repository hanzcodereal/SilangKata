package com.hanzdev.silangkata

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class GameActivity : Activity() {

    private lateinit var progress: Progress
    private lateinit var levels: List<Level>
    private lateinit var level: Level
    private lateinit var board: CrosswordView
    private lateinit var clueBar: TextView
    private lateinit var hintButton: TextView
    private lateinit var grid: Array<CharArray>
    private lateinit var wrong: Array<BooleanArray>
    private var selR = 0
    private var selC = 0
    private var across = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progress = Progress(this)
        levels = Level.loadAll(this)
        val id = intent.getIntExtra(EXTRA_LEVEL_ID, levels.first().id)
        level = levels.firstOrNull { it.id == id } ?: levels.first()
        grid = restoreGrid()
        wrong = Array(level.rows) { BooleanArray(level.cols) }
        setContentView(buildUi())

        val start = firstLetterCell()
        selR = start.first
        selC = start.second
        across = level.clueOf(selR, selC, true) != null
        refresh()
    }

    override fun onPause() {
        super.onPause()
        progress.saveGrid(level.id, gridText())
    }

    private fun restoreGrid(): Array<CharArray> {
        val saved = progress.savedGrid(level.id)
        val valid = saved != null && saved.length == level.rows * level.cols
        return Array(level.rows) { r ->
            CharArray(level.cols) { c ->
                when {
                    !level.isLetter(r, c) -> Level.BLACK
                    valid -> saved!![r * level.cols + c]
                    else -> Level.EMPTY
                }
            }
        }
    }

    private fun gridText(): String = grid.joinToString("") { it.concatToString() }

    private fun firstLetterCell(): Pair<Int, Int> {
        for (r in 0 until level.rows) {
            for (c in 0 until level.cols) {
                if (level.isLetter(r, c)) return Pair(r, c)
            }
        }
        return Pair(0, 0)
    }

    private fun buildUi(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Palette.PAPER)
            setPadding(dp(12), dp(8), dp(12), dp(12))
        }

        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        top.addView(button("←", Palette.CELL, Palette.INK, 20f) { finish() }, lp(dp(48), dp(48)))
        top.addView(
            TextView(this).apply {
                text = "${level.title} · ${level.difficulty}"
                textSize = 18f
                setTextColor(Palette.INK)
                setTypeface(Typeface.DEFAULT_BOLD)
                setPadding(dp(10), 0, 0, 0)
            },
            LinearLayout.LayoutParams(0, WRAP, 1f)
        )
        root.addView(top, lp(MATCH, WRAP))

        board = CrosswordView(this)
        board.onCellTap = { r, c -> tapCell(r, c) }
        root.addView(
            board,
            LinearLayout.LayoutParams(MATCH, 0, 1f).apply { setMargins(0, dp(8), 0, dp(8)) }
        )

        clueBar = TextView(this).apply {
            textSize = 16f
            setTextColor(Palette.INK)
            background = roundedBg(Palette.HIGHLIGHT, 12)
            setPadding(dp(14), dp(10), dp(14), dp(10))
            minimumHeight = dp(44)
            gravity = Gravity.CENTER_VERTICAL
        }
        root.addView(clueBar, lp(MATCH, WRAP))

        val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        hintButton = button("", Palette.TEAL, Color.WHITE) { useHint() }
        actions.addView(hintButton, lp(0, dp(48), 1f, 4))
        actions.addView(button("Periksa", Palette.INK, Color.WHITE) { checkAnswers() }, lp(0, dp(48), 1f, 4))
        root.addView(actions, lp(MATCH, WRAP, 0f, 0))

        val keyboard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(8), 0, 0)
        }
        keyboard.addView(keyRow("QWERTYUIOP", false))
        keyboard.addView(keyRow("ASDFGHJKL", false))
        keyboard.addView(keyRow("ZXCVBNM", true))
        root.addView(keyboard, lp(MATCH, WRAP))

        return root
    }

    private fun keyRow(letters: String, withDelete: Boolean): LinearLayout {
        val line = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        for (ch in letters) {
            line.addView(
                button(ch.toString(), Palette.CELL, Palette.INK, 18f) { typeLetter(ch) },
                lp(0, dp(44), 1f, 3)
            )
        }
        if (withDelete) {
            line.addView(
                button("⌫", Palette.INK, Color.WHITE, 18f) { deleteLetter() },
                lp(0, dp(44), 1.5f, 3)
            )
        }
        return line
    }

    private fun activeClue(): Clue? =
        level.clueOf(selR, selC, across) ?: level.clueOf(selR, selC, !across)

    private fun tapCell(r: Int, c: Int) {
        if (r == selR && c == selC) {
            if (level.clueOf(r, c, !across) != null) across = !across
        } else {
            selR = r
            selC = c
            if (level.clueOf(r, c, across) == null) across = !across
        }
        refresh()
    }

    private fun move(step: Int) {
        val clue = activeClue() ?: return
        val index = clue.indexOf(selR, selC) + step
        if (index < 0 || index >= clue.answer.length) return
        val (r, c) = clue.cellAt(index)
        selR = r
        selC = c
    }

    private fun isRevealed(r: Int, c: Int): Boolean = grid[r][c].isLowerCase()

    private fun isCorrect(r: Int, c: Int): Boolean = grid[r][c].uppercaseChar() == level.solution[r][c]

    private fun clearCell(r: Int, c: Int) {
        grid[r][c] = Level.EMPTY
        wrong[r][c] = false
    }

    private fun typeLetter(ch: Char) {
        if (!isRevealed(selR, selC)) {
            grid[selR][selC] = ch
            wrong[selR][selC] = false
        }
        move(1)
        afterChange()
    }

    private fun deleteLetter() {
        if (!isRevealed(selR, selC) && grid[selR][selC] != Level.EMPTY) {
            clearCell(selR, selC)
        } else {
            move(-1)
            if (!isRevealed(selR, selC)) clearCell(selR, selC)
        }
        afterChange()
    }

    private fun hintTarget(): Pair<Int, Int>? {
        if (level.isLetter(selR, selC) && !isCorrect(selR, selC)) return Pair(selR, selC)
        val clue = activeClue() ?: return null
        for (i in clue.answer.indices) {
            val (r, c) = clue.cellAt(i)
            if (!isCorrect(r, c)) return Pair(r, c)
        }
        return null
    }

    private fun useHint() {
        if (progress.hints <= 0) {
            toast("Bantuan kamu habis. Selesaikan level untuk dapat bantuan lagi.")
            return
        }
        val target = hintTarget()
        if (target == null) {
            toast("Semua huruf di petunjuk ini sudah benar.")
            return
        }
        val (r, c) = target
        grid[r][c] = level.solution[r][c].lowercaseChar()
        wrong[r][c] = false
        selR = r
        selC = c
        progress.hints = progress.hints - 1
        afterChange()
    }

    private fun checkAnswers() {
        var bad = 0
        for (r in 0 until level.rows) {
            for (c in 0 until level.cols) {
                if (!level.isLetter(r, c)) continue
                wrong[r][c] = grid[r][c] != Level.EMPTY && !isCorrect(r, c)
                if (wrong[r][c]) bad++
            }
        }
        refresh()
        toast(
            if (bad == 0) "Sejauh ini semua huruf sudah benar."
            else "$bad huruf belum tepat. Cek yang berwarna merah."
        )
    }

    private fun afterChange() {
        progress.saveGrid(level.id, gridText())
        checkCompletion()
        refresh()
    }

    private fun checkCompletion() {
        for (r in 0 until level.rows) {
            for (c in 0 until level.cols) {
                if (level.isLetter(r, c) && !isCorrect(r, c)) return
            }
        }
        if (progress.isDone(level.id)) return
        progress.markDone(level.id)
        progress.hints = progress.hints + Progress.REWARD_HINTS
        showFinished()
    }

    private fun showFinished() {
        val index = levels.indexOfFirst { it.id == level.id }
        val nextId = levels.getOrNull(index + 1)?.takeIf { it.pack == level.pack }?.id
        val builder = AlertDialog.Builder(this)
            .setTitle("Level selesai!")
            .setMessage("Kerja bagus! Kamu mendapat ${Progress.REWARD_HINTS} bantuan.")
            .setCancelable(false)
            .setNegativeButton("Kembali") { _, _ -> finish() }
        if (nextId != null) {
            builder.setPositiveButton("Level berikutnya") { _, _ ->
                start(this, nextId)
                finish()
            }
        } else {
            builder.setPositiveButton("Selesai") { _, _ -> finish() }
        }
        builder.show()
    }

    private fun refresh() {
        val clue = activeClue()
        board.show(level, grid, wrong, selR, selC, clue)
        val direction = if (clue?.across == true) "Mendatar" else "Menurun"
        clueBar.text = if (clue == null) "" else "${clue.number} $direction: ${clue.text}"
        hintButton.text = "Bantuan (${progress.hints})"
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_LEVEL_ID = "level_id"

        fun start(context: Context, levelId: Int) {
            context.startActivity(
                Intent(context, GameActivity::class.java).putExtra(EXTRA_LEVEL_ID, levelId)
            )
        }
    }
}
