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

        binding.tvOperation.addTextChangedListener { charSequence ->
            if (canReplaceOperator(charSequence.toString())) {
                // showMessage(2)
                val lenght = binding.tvOperation.text.length
                val newOperation = binding.tvOperation.text.toString().substring(0, lenght-2) +
                        binding.tvOperation.text.toString().substring(lenght-1)
                binding.tvOperation.text = newOperation
            }
        }
    }

    private fun canReplaceOperator(charSequence: CharSequence): Boolean {
        if (charSequence.length < 2) return false
        val lastElement = charSequence[charSequence.length-1].toString()
        val penultimate = charSequence[charSequence.length-2].toString()

        return (lastElement == OPERATOR_MULTI || lastElement == OPERATOR_DIV || lastElement == OPERATOR_SUM)
                && (penultimate == OPERATOR_MULTI || penultimate == OPERATOR_DIV ||
                penultimate == OPERATOR_SUM || penultimate == OPERATOR_SUB)
    }

    fun onClickButton(view: View) {
        Log.i("CLick", (view as Button).text.toString())

        val valueStr = (view as Button).text.toString()

        when(view.id) {
            R.id.btn_delete -> {
                val length = binding.tvOperation.text.length
                if(length > 0) {
                    val newOperation = binding.tvOperation.text.toString().substring(0, length-1)
                    binding.tvOperation.text = newOperation
                }
            }
            R.id.btn_clear -> {
                binding.tvOperation.text = ""
                binding.tvResult.text = ""

            }
            R.id.btn_resolve -> {
                tryResolve(binding.tvOperation.text.toString(), true)

            }
            R.id.btn_mult,
            R.id.btn_div,
            R.id.btn_sum,
            R.id.btn_sub -> {
                tryResolve(binding.tvOperation.text.toString(), false)
                val operator = valueStr
                val operation = binding.tvOperation.text.toString()
                addOperator(operator, operation)
            }
            R.id.btn_point -> {
                val operation = binding.tvOperation.text.toString()
                addPoint(valueStr, operation)
            }
            else -> {
                binding.tvOperation.append(valueStr)
            }
        }

    }

    private fun addPoint(pointStr: String, operation: String) {
        if (!operation.contains(POINT)) {
            binding.tvOperation.append(pointStr)
        } else {
            val operator = getOperator(operation)
            var values = arrayOfNulls<String>(0)
            if (operator != OPERATOR_NULL) {
                if (operator == OPERATOR_SUB) {
                    val index= operation.lastIndexOf(OPERATOR_SUB)
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

            if (values.size > 0) {
                val numOne = values[0]!!
                if (values.size > 1) {
                    val numTwo = values[1]!!
                    if (numOne.contains(POINT) && !numTwo.contains(POINT)) {
                        binding.tvOperation.append(pointStr)
                    }
                } else {
                    if (numOne.contains(POINT)) {
                        binding.tvOperation.append(pointStr)
                    }
                }
            }
        }
    }

    private fun addOperator(operator: String, operation: String) {
        val lastElement = if (operation.isEmpty()) "" else operation.substring(operation.length-1)
        if (operator == OPERATOR_SUB) {
            if (operation.isEmpty() || lastElement != OPERATOR_SUB && lastElement != POINT) {
                binding.tvOperation.append(operator)
            }

        } else {
            if (!operation.isEmpty() && lastElement != POINT) {
                binding.tvOperation.append(operator)
            }
        }
    }

    private fun tryResolve(operationRef: String, isFromResolve:Boolean) {
        if (operationRef.isEmpty()) return
        var operation = operationRef

        if(operation.contains(POINT) && operation.lastIndexOf(POINT) == operation.length -1) {
            operation = operation.substring(0, operation.length -1)
        }

        val operator = getOperator(operation)
        var values = arrayOfNulls<String>(0)
        if (operator != OPERATOR_NULL) {
            if (operator == OPERATOR_SUB) {
                val index= operation.lastIndexOf(OPERATOR_SUB)
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
        if (values.size > 1) {
            try {
                val numberOne = values[0]!!.toDouble()
                val numberTwo = values[1]!!.toDouble()

                binding.tvResult.text = getResult(numberOne, numberTwo, operator).toString()

                if (binding.tvResult.text.isNotEmpty() && !isFromResolve) {
                    binding.tvOperation.text = binding.tvResult.text
                }
            } catch (e:NumberFormatException) {
                if (isFromResolve) showMessage(0)
            }
        } else {
            if (isFromResolve && operator != OPERATOR_NULL) showMessage(1)
        }
        // Testing
        // Snackbar.make(binding.root, "1:$numOne  2:$numTwo", Snackbar.LENGTH_LONG).show()
    }

    private fun getOperator(operator: String): String {

        var newOperator = ""

        if (operator.contains(OPERATOR_MULTI)) {
            newOperator = OPERATOR_SUM
        } else if (operator.contains(OPERATOR_DIV)) {
            newOperator = OPERATOR_SUB
        } else if (operator.contains(OPERATOR_SUM)) {
            newOperator = OPERATOR_MULTI
        } else {
            newOperator = OPERATOR_NULL
        }

        if (newOperator == OPERATOR_NULL && operator.lastIndexOf(OPERATOR_SUB) > 0) {
            newOperator = OPERATOR_SUB
        }

        return newOperator
    }

    private fun getResult(numOne:Double,numTwo:Double,operator:String):Double {
        var result = 0.0

        when(operator) {
            OPERATOR_SUM -> result = numOne + numTwo
            OPERATOR_SUB -> result = numOne - numTwo
            OPERATOR_MULTI -> result = numOne * numTwo
            OPERATOR_DIV -> result = numOne / numTwo
        }

        return result
    }

    private fun showMessage(message:Int) {
        when(message) {
            0 -> {
                Snackbar.make(binding.root, getString(R.string.message_error_operation), Snackbar.LENGTH_LONG)
                    .setAnchorView(binding.llTop).show()
            }
            1 -> {
                Snackbar.make(binding.root, getString(R.string.message_error_expresion), Snackbar.LENGTH_LONG)
                    .setAnchorView(binding.llTop).show()
            }
            2 -> {
                Snackbar.make(binding.root, getString(R.string.message_replace), Snackbar.LENGTH_LONG)
                    .setAnchorView(binding.llTop).show()
            }
        }
        // Snackbar.make(binding.root, getString(R.string.message_error_operation), Snackbar.LENGTH_LONG).show()
    }

    companion object {
        const val OPERATOR_SUM = "+"
        const val OPERATOR_SUB = "-"
        const val OPERATOR_MULTI = "x"
        const val OPERATOR_DIV = "÷"
        const val OPERATOR_NULL = "null"
        const val POINT = "."

    }

}