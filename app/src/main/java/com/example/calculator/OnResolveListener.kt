package com.example.calculator

interface OnResolveListener {

    public abstract fun onShowResult(result: Double)

    public abstract fun onShowMessage(errorRes: Int)

}