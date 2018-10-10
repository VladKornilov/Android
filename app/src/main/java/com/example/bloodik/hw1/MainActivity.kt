package com.example.bloodik.hw1

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Build
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.inputmethod.InputMethodManager
import java.text.DecimalFormat
import java.text.NumberFormat


class MainActivity : AppCompatActivity() {
    var formulaText: String = ""
    var digits: Array<TextView>? = null
    var signs: Array<TextView>? = null
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
        for (digit in digits!!) {
            digit.setOnClickListener {
                val newF = formulaText.substring(0, selection) + digit.text + formulaText.substring(selection)
                if (parser.checkSyntax(newF)) {
                    formulaText = newF
                    selection++
                    recalcAnswer()
                }

                //answer.text = parser.parse(formulaText)
            }
        }

        for (sign in signs!!) {
            sign.setOnClickListener {
                val newF = formulaText.substring(0, selection) + sign.text + formulaText.substring(selection)
                if (parser.checkSyntax(newF)) {
                    formulaText = newF
                    selection++
                    recalcAnswer()
                }
            }
        }

        open.setOnClickListener {
            val newF = formulaText.substring(0, selection) + open.text + formulaText.substring(selection)
            if (parser.checkSyntax(newF)) {
                formulaText = newF
                selection++
                recalcAnswer()
            }
        }

        close.setOnClickListener {
            val newF = formulaText.substring(0, selection) + close.text + formulaText.substring(selection)
            if (parser.checkSyntax(newF)) {
                formulaText = newF
                selection++
                recalcAnswer()
            }

            //answer.text = parser.parse(formulaText)
        }

        del.setOnClickListener {
            if (selection != 0) {
                formulaText = formulaText.substring(0, selection - 1) + formulaText.substring(selection)
                selection--
                recalcAnswer()
            }
        }

        del.setOnLongClickListener {
            formulaText = ""
            selection = 0
            formula.setText("")
            answer.text = ""
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (vibrator.hasVibrator()) { vibrator.vibrate(100) }

            true
        }

        count.setOnClickListener {
            if (!parser.checkSyntax(formulaText))
                formulaText = "Error"
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
