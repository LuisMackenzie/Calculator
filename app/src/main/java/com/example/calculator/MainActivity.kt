package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.widget.addTextChangedListener
import com.example.calculator.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.lang.NumberFormatException
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvOperation.run {
            addTextChangedListener { charSequence ->
                if (Operations.canReplaceOperator(charSequence.toString())) {
                    val newStr = text.toString().substring(0, text.length-2) +
                            text.toString().substring(text.length-1)
                    text = newStr
                }
            }
        }
    }

    fun onClickButton(view: View) {
        // Testing
        // Log.i("CLick", (view as Button).text.toString())

        val valueStr = (view as Button).text.toString()
        val operation = binding.tvOperation.text.toString()

        when(view.id) {
            R.id.btn_delete -> {
                binding.tvOperation.run {
                    if (text.length > 0) text = operation.substring(0, text.length-1)
                }
            }
            R.id.btn_clear -> {
                binding.tvOperation.text = ""
                binding.tvResult.text = ""
            }
            R.id.btn_resolve -> checkOrResolve(operation, true)
            R.id.btn_mult,
            R.id.btn_div,
            R.id.btn_sum,
            R.id.btn_sub -> {
                checkOrResolve(operation, false)
                addOperator(valueStr, operation)
            }
            R.id.btn_point -> addPoint(valueStr, operation)
            else -> binding.tvOperation.append(valueStr)
        }

    }

    private fun addPoint(pointStr: String, operation: String) {
        if (!operation.contains(Constants.POINT)) {
            binding.tvOperation.append(pointStr)
        } else {
            val operator = Operations.getOperator(operation)
            var values = arrayOfNulls<String>(0)
            if (operator != Constants.OPERATOR_NULL) {
                if (operator == Constants.OPERATOR_SUB) {
                    val index= operation.lastIndexOf(Constants.OPERATOR_SUB)
                    if (index < operation.length-1) {
                        values = arrayOfNulls(2)
                        values[0] = operation.substring(0, index)
                        values[1] = operation.substring(index+1)
                    } else {
                        values = arrayOfNulls(1)
                        values[0] = operation.substring(0, index)
                    }
                } else {
                    values = operation.split(operator).toTypedArray()
                }
            }

            if (!values.isEmpty()) {
                val numOne = values[0]!!
                if (values.size > 1) {
                    val numTwo = values[1]!!
                    if (numOne.contains(Constants.POINT) && !numTwo.contains(Constants.POINT)) {
                        binding.tvOperation.append(pointStr)
                    }
                } else {
                    if (numOne.contains(Constants.POINT)) {
                        binding.tvOperation.append(pointStr)
                    }
                }
            }
        }
    }

    private fun addOperator(operator: String, operation: String) {
        val lastElement = if (operation.isEmpty()) "" else operation.substring(operation.length-1)
        if (operator == Constants.OPERATOR_SUB) {
            if (operation.isEmpty() || lastElement != Constants.OPERATOR_SUB && lastElement != Constants.POINT) {
                binding.tvOperation.append(operator)
            }

        } else {
            if (!operation.isEmpty() && lastElement != Constants.POINT) {
                binding.tvOperation.append(operator)
            }
        }
    }

    private fun checkOrResolve(operation: String, isFromResolve:Boolean) {

        Operations.tryResolve(operation, isFromResolve, object : OnResolveListener {
            override fun onShowResult(result: Double) {
                binding.tvResult.text = String.format(Locale.ROOT, "%.2f", result)

                if (binding.tvResult.text.isNotEmpty() && !isFromResolve) {
                    binding.tvOperation.text = binding.tvResult.text
                }
            }

            override fun onShowMessage(errorRes: Int) {
                showMessage(errorRes)
            }

        })

    }

    private fun showMessage(errorRes: Int) {
        Snackbar.make(binding.root, errorRes, Snackbar.LENGTH_LONG)
            .setAnchorView(binding.llTop).show()
    }

}