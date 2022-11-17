package com.group.libraryapp.calculator

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.Test

class CalculatorTest {

    @Test
    fun add() {
        //given
        val calculator = Calculator(5)
        //when
        calculator.add(3)
        //then
        assertThat(calculator).isEqualTo(Calculator(8))
    }

    @Test
    fun minus() {
        //given
        val calculator = Calculator(5)
        //when
        calculator.minus(3)
        //then
        assertThat(calculator).isEqualTo(Calculator(2))
    }

    @Test
    fun multiply() {
        //given
        val calculator = Calculator(5)
        //when
        calculator.multiply(3)
        //then
        assertThat(calculator).isEqualTo(Calculator(15))
    }

    @Test
    fun divide() {
        //given
        val calculator = Calculator(5)
        //when
        calculator.divide(2)
        //then
        assertThat(calculator).isEqualTo(Calculator(2))
    }

    @Test
    fun divide_byZero_thrownIllegalArgumentException() {
        //given
        val calculator = Calculator(6)
        //when, then
        assertThatIllegalArgumentException().isThrownBy {
            calculator.divide(0)
        }.withMessageContaining("can not divide by zero")
    }
}
