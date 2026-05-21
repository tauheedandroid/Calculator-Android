package com.abasyn.calculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.abasyn.calculator.databinding.ActivityMainBinding
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var lastNumeric = false
    private var stateError = false
    private var lastDot = false

    private lateinit var expression: Expression

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }

    // =========================
    // ALL CLEAR
    // =========================
    fun onAllclearClick(view: View) {

        binding.dataTv.text = ""
        binding.resultTv.text = ""

        stateError = false
        lastDot = false
        lastNumeric = false

        binding.resultTv.visibility = View.GONE
    }

    // =========================
    // DIGIT CLICK
    // =========================
    fun onDigitClick(view: View) {

        val button = view as Button

        if (stateError) {

            binding.dataTv.text = button.text
            stateError = false

        } else {

            binding.dataTv.append(button.text)
        }

        lastNumeric = true

        onEqual()
    }

    // =========================
    // OPERATOR CLICK
    // =========================
    fun onOperatorClick(view: View) {

        if (lastNumeric && !stateError) {

            var operator = (view as Button).text.toString()

            // Replace multiplication symbol
            if (operator == "x" || operator == "×") {
                operator = "*"
            }

            binding.dataTv.append(operator)

            lastNumeric = false
            lastDot = false
        }
    }

    // =========================
    // DOT CLICK
    // =========================
    fun onDecimalPointClick(view: View) {

        if (lastNumeric && !stateError && !lastDot) {

            binding.dataTv.append(".")
            lastNumeric = false
            lastDot = true
        }
    }

    // =========================
    // EQUAL CLICK
    // =========================
    fun onEqualClick(view: View) {

        onEqual()

        val result = binding.resultTv.text.toString()

        if (result.startsWith("=")) {

            binding.dataTv.text = result.drop(1)

            binding.resultTv.visibility = View.GONE
        }
    }

    // =========================
    // BACKSPACE
    // =========================
    fun onBackClick(view: View) {

        val text = binding.dataTv.text.toString()

        if (text.isNotEmpty()) {

            binding.dataTv.text = text.dropLast(1)

            val newText = binding.dataTv.text.toString()

            if (newText.isNotEmpty() && newText.last().isDigit()) {

                lastNumeric = true
                onEqual()

            } else {

                binding.resultTv.text = ""
                binding.resultTv.visibility = View.GONE
            }
        }
    }

    // =========================
    // CLEAR
    // =========================
    fun onClearClick(view: View) {

        binding.dataTv.text = ""

        binding.resultTv.text = ""

        binding.resultTv.visibility = View.GONE

        lastNumeric = false
        lastDot = false
        stateError = false
    }

    // =========================
    // CALCULATE RESULT
    // =========================
    @SuppressLint("SetTextI18n")
    fun onEqual() {

        if (lastNumeric && !stateError) {

            try {

                var txt = binding.dataTv.text.toString()

                // Replace multiply symbols
                txt = txt.replace("x", "*")
                    .replace("×", "*")

                expression = ExpressionBuilder(txt).build()

                val result = expression.evaluate()

                binding.resultTv.visibility = View.VISIBLE

                binding.resultTv.text = if (result % 1 == 0.0) {

                    "=" + result.toInt().toString()

                } else {

                    "=" + result.toString()
                }

            } catch (e: Exception) {

                Log.e("CalculatorError", e.toString())

                binding.resultTv.visibility = View.VISIBLE
                binding.resultTv.text = "Error"

                stateError = true
                lastNumeric = false
            }
        }
    }
}