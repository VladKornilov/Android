package com.example.bloodik.hw1

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.inputmethod.InputMethodManager
import java.text.DecimalFormat
import java.text.NumberFormat


class MainActivity : AppCompatActivity() {
    var formulaText: String = ""
    lateinit var digits: Array<TextView>
    lateinit var signs: Array<TextView>
    lateinit var input: Array<TextView>
    val parser: ExpressionParser = ExpressionParser()
    val signChars: String = "+-×÷"
    val LOG_TAG: String = "Main-Activity"
    var selection: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        digits = arrayOf(n0, n1, n2, n3, n4, n5, n6, n7, n8, n9, point)
        signs = arrayOf(add, sub, mul, div)
        input = digits.clone() + signs.clone() + arrayOf(open, close)

        hideKeyboard()
        setButtonsListener()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("formula", formulaText)
        outState?.putInt("selection", selection)
        outState?.putCharSequence("answer", answer.text)
        outState?.putBoolean("hasAnswer", answer.text.isNotEmpty())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        formulaText = savedInstanceState?.getString("formula") ?: ""
        selection = savedInstanceState?.getInt("selection") ?: 0
        if (savedInstanceState?.getBoolean("hasAnswer")!!)
            answer.text = savedInstanceState.getCharSequence("answer")
    }

    private fun setButtonsListener() {
        formula.setOnClickListener {
            selection = formula.selectionStart
            Log.d(LOG_TAG, "" + selection)
        }

        for (button in input) {
            button.setOnClickListener {
                val newF = formulaText.substring(0, selection) + button.text + formulaText.substring(selection)
                if (parser.checkSyntax(newF)) {
                    formulaText = newF
                    selection++
                    recalcAnswer()
                }
            }
        }

        del.setOnClickListener {
            if (selection != 0) {
                formulaText = formulaText.substring(0, selection - 1) + formulaText.substring(selection)
                selection--
                recalcAnswer()
            }
        }

        del.setOnLongClickListener {
            //while (selection != 0) //doesn't work
            if (selection != 0) {
                formulaText = formulaText.substring(0, selection - 1) + formulaText.substring(selection)
                selection--
                recalcAnswer()
                Thread.sleep(100)
            }
            true
        }

        clear.setOnClickListener {
            formulaText = ""
            selection = 0
            formula.setText("")
            answer.text = ""
            vibrate(100)
        }

        count.setOnClickListener {
            formulaText = parser.parse(formulaText)
            selection = formulaText.length
            recalcAnswer()
            answer.text = ""
        }
    }

    private fun recalcAnswer() {
        formula.setText(formulaText)
        formula.setSelection(selection)
        if (parser.checkSyntax(formulaText))
            answer.text = parser.parse(formulaText)
    }

    private fun vibrate(millis: Long) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
            }
            else { vibrator.vibrate(millis) }
        }
    }

    private fun hideKeyboard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            formula.showSoftInputOnFocus = false
        }
        else {
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(formula.windowToken, 0)
        }
    }
}
