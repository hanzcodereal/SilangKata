package com.hanzdev.silangkata

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

/** Menggambar papan teka-teki silang dan menerjemahkan sentuhan menjadi kotak. */
class CrosswordView(context: Context) : View(context) {
    private var level: Level? = null
    private var grid: Array<CharArray> = arrayOf()
    private var wrong: Array<BooleanArray> = arrayOf()
    private var selR = -1
    private var selC = -1
    private var activeClue: Clue? = null
    private var cell = 0f
    private var originX = 0f
    private var originY = 0f

    var onCellTap: ((Int, Int) -> Unit)? = null

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val letterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }
    private val numberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Palette.INK_SOFT
        typeface = Typeface.DEFAULT_BOLD
    }

    fun show(
        level: Level,
        grid: Array<CharArray>,
        wrong: Array<BooleanArray>,
        selR: Int,
        selC: Int,
        active: Clue?
    ) {
        this.level = level
        this.grid = grid
        this.wrong = wrong
        this.selR = selR
        this.selC = selC
        this.activeClue = active
        layoutBoard()
        invalidate()
    }

    private fun layoutBoard() {
        val lv = level ?: return
        if (width == 0 || height == 0) return
        cell = min(width / lv.cols.toFloat(), height / lv.rows.toFloat())
        originX = (width - cell * lv.cols) / 2f
        originY = (height - cell * lv.rows) / 2f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        layoutBoard()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val lv = level ?: return
        if (cell <= 0f) return

        val gap = cell * 0.05f
        val radius = cell * 0.14f
        for (r in 0 until lv.rows) {
            for (c in 0 until lv.cols) {
                val left = originX + c * cell
                val top = originY + r * cell
                val rect = RectF(left + gap, top + gap, left + cell - gap, top + cell - gap)

                if (!lv.isLetter(r, c)) {
                    fillPaint.color = Palette.INK
                    canvas.drawRoundRect(rect, radius, radius, fillPaint)
                    continue
                }

                fillPaint.color = when {
                    r == selR && c == selC -> Palette.SELECT
                    activeClue?.covers(r, c) == true -> Palette.HIGHLIGHT
                    else -> Palette.CELL
                }
                canvas.drawRoundRect(rect, radius, radius, fillPaint)

                val ch = grid[r][c]
                if (ch != Level.EMPTY) {
                    letterPaint.textSize = cell * 0.52f
                    letterPaint.color = when {
                        wrong[r][c] -> Palette.WRONG
                        ch.isLowerCase() -> Palette.TEAL
                        else -> Palette.INK
                    }
                    val baseline = top + cell / 2f - (letterPaint.descent() + letterPaint.ascent()) / 2f
                    canvas.drawText(ch.uppercaseChar().toString(), left + cell / 2f, baseline, letterPaint)
                }

                val num = lv.numbers[r][c]
                if (num > 0) {
                    numberPaint.textSize = cell * 0.24f
                    canvas.drawText(num.toString(), left + gap * 3f, top + gap * 2f + numberPaint.textSize, numberPaint)
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val lv = level
        if (event.actionMasked == MotionEvent.ACTION_UP && lv != null && cell > 0f) {
            if (event.x >= originX && event.y >= originY) {
                val c = ((event.x - originX) / cell).toInt()
                val r = ((event.y - originY) / cell).toInt()
                if (r < lv.rows && c < lv.cols && lv.isLetter(r, c)) {
                    performClick()
                    onCellTap?.invoke(r, c)
                }
            }
        }
        return true
    }
}
