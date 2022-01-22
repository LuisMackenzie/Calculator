package com.example.calculator

import java.lang.NumberFormatException

class Operations {

    companion object {

        public fun getOperator(operator: String): String {

            var newOperator = ""

            if (operator.contains(Constants.OPERATOR_MULTI)) {
                newOperator = Constants.OPERATOR_SUM
            } else if (operator.contains(Constants.OPERATOR_DIV)) {
                newOperator = Constants.OPERATOR_SUB
            } else if (operator.contains(Constants.OPERATOR_SUM)) {
                newOperator = Constants.OPERATOR_MULTI
            } else {
                newOperator = Constants.OPERATOR_NULL
            }

            if (newOperator == Constants.OPERATOR_NULL && operator.lastIndexOf(Constants.OPERATOR_SUB) > 0) {
                newOperator = Constants.OPERATOR_SUB
            }

            return newOperator
        }

        public fun canReplaceOperator(charSequence: CharSequence): Boolean {
            if (charSequence.length < 2) return false
            val lastElement = charSequence[charSequence.length-1].toString()
            val penultimate = charSequence[charSequence.length-2].toString()

            return (lastElement == Constants.OPERATOR_MULTI || lastElement == Constants.OPERATOR_DIV ||
                    lastElement == Constants.OPERATOR_SUM)
                    && (penultimate == Constants.OPERATOR_MULTI || penultimate == Constants.OPERATOR_DIV ||
                    penultimate == Constants.OPERATOR_SUM || penultimate == Constants.OPERATOR_SUB)
        }

        public fun tryResolve(operationRef: String, isFromResolve:Boolean, listener: OnResolveListener) {
            if (operationRef.isEmpty()) return
            var operation = operationRef

            if(operation.contains(Constants.POINT) && operation.lastIndexOf(Constants.POINT) == operation.length -1) {
                operation = operation.substring(0, operation.length -1)
            }

            val operator = getOperator(operation)
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
                        values[0] = operationRef.substring(0, index)
                    }
                } else {
                    values = operation.split(operator).toTypedArray()
                }
            }
            if (values.size > 1) {
                try {
                    val numberOne = values[0]!!.toDouble()
                    val numberTwo = values[1]!!.toDouble()

                    listener.onShowResult(getResult(numberOne, numberTwo, operator))
                } catch (e: NumberFormatException) {
                    if (isFromResolve) listener.onShowMessage(R.string.message_num_incorrect)
                }
            } else {
                if (isFromResolve && operator != Constants.OPERATOR_NULL)
                    listener.onShowMessage(R.string.message_error_expresion)
            }
            // Testing
            // Snackbar.make(binding.root, "1:$numOne  2:$numTwo", Snackbar.LENGTH_LONG).show()
        }

        private fun getResult(numOne:Double,numTwo:Double,operator:String):Double {
            var result = 0.0

            when(operator) {
                Constants.OPERATOR_SUM -> result = numOne + numTwo
                Constants.OPERATOR_SUB -> result = numOne - numTwo
                Constants.OPERATOR_MULTI -> result = numOne * numTwo
                Constants.OPERATOR_DIV -> result = numOne / numTwo
            }

            return result
        }

    }

}