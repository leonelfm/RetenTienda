package com.qnecesitas.retentienda.auxiliary

import java.util.Random

object IDCreater {

    fun generate(): String {
        val random = Random()
        val min = 100000
        val max = 999999
        val randomNumber = random.nextInt(max - min + 1) + min
        return randomNumber.toString()
    }
}