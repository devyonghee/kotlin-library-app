package com.group.libraryapp.calculator

data class Calculator(
    private var number: Int
) {

    fun add(operand: Int) {
        this.number += operand
    }

    fun minus(operand: Int) {
        this.number -= operand
    }

    fun multiply(operand: Int) {
        this.number *= operand
    }

    fun divide(operand: Int) {
        if (operand == 0) {
            throw IllegalArgumentException("can not divide by zero")
        }
        this.number /= operand
    }
}
