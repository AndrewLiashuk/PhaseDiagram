package com.andrew.liashuk.phasediagram.logic

import androidx.annotation.WorkerThread
import com.andrew.liashuk.phasediagram.ext.step
import com.andrew.liashuk.phasediagram.types.PhaseData
import kotlin.math.*

fun DiagramCalculator(phaseData: PhaseData): DiagramCalculator {
    return DiagramCalculator(
        phaseData.meltingTempFirst ?: throw IllegalArgumentException("First melting temperature not set!"),
        phaseData.meltingTempSecond ?: throw IllegalArgumentException("Second melting temperature not set!"),
        phaseData.entropFirst ?: throw IllegalArgumentException("First entropy not set!"),
        phaseData.entropSecond ?: throw IllegalArgumentException("Second entropy not set!"),
        phaseData.alphaLFirst ?: 0.0, // if not set 0 for ideal formula
        phaseData.alphaSFirst ?: 0.0,
        phaseData.alphaLSecond ?: -1.0, // if not set -1 for regular formula
        phaseData.alphaSSecond ?: -1.0
    )
}

class DiagramCalculator(
    private val meltingTempFirst: Double, //melting Points (temperature of fusion), K
    private val meltingTempSecond: Double,
    private val entropFirst: Double, //entropies of fusion, J/K/mol
    private val entropSecond: Double,
    private val alphaLiquidFirst: Double = 0.0, //interaction parameter in Liquid phase
    private val alphaSolidFirst: Double = 0.0, // if not set 0 for ideal formula
    private val alphaLiquidSecond: Double = -1.0, // if -1 use regular formula
    private val alphaSolidSecond: Double = -1.0
) {

    private var temperature = 0.0

    /**
     * Function that calculate PhaseDiagram.
     *
     * @return  collection of PhasePoints that store value of solid and liquid phase
     *          depending on temperature.
     */
    @WorkerThread
    fun build(): List<PhasePoint> {
        // calculate total number of points
        val pointsCount = (FINISH_VALUE - START_VALUE) / CALCULATION_STEP + 2 // plus first and last points
        val points = ArrayList<PhasePoint>(pointsCount.toInt())
        points.add(PhasePoint(0.0, 0.0, meltingTempFirst)) // add first point

        for (i in START_VALUE..FINISH_VALUE step CALCULATION_STEP) {
            bisection(i)?.let {
                points.add(
                    PhasePoint(
                        solid = it * 100, //transfer into percentages
                        liquid = i * 100,
                        temperature
                    )
                )
            }
        }

        points.add(PhasePoint(100.0, 100.0, meltingTempSecond)) // add end point
        return points
    }

    // solution of equation temperatureFunction(x) = 0
    private fun bisection(i: Double): Double? {
        var x: Double
        var xMin = START_VALUE
        var xMax = FINISH_VALUE

        val fa = temperatureFunction(xMin, i)
        val fb = temperatureFunction(xMax, i)

        if (fa.sign == fb.sign) return null

        do {
            x = xMin + (xMax - xMin) / 2
            val fx = temperatureFunction(x, i)

            if (fa.sign == fx.sign) xMin = x else xMax = x

        } while (abs(xMax - xMin) > ACCURACY)

        return x
    }

    private fun temperatureFunction(x: Double, i: Double): Double {
        val alphaLFirst: Double; val alphaLSecond: Double
        val alphaSFirst: Double; val alphaSSecond: Double

        if (alphaLiquidSecond != -1.0 && alphaSolidSecond != -1.0) { // use subregular formula
            alphaLFirst = alphaLiquidSecond + 2 * (alphaLiquidFirst - alphaLiquidSecond) * (1 - i)
            alphaLSecond = alphaLiquidFirst + 2 * (alphaLiquidSecond - alphaLiquidFirst) * i
            alphaSFirst = alphaSolidSecond + 2 * (alphaSolidFirst - alphaSolidSecond) * (1 - x)
            alphaSSecond = alphaSolidFirst + 2 * (alphaSolidSecond - alphaSolidFirst) * x
        } else {
            alphaLFirst = alphaLiquidFirst
            alphaLSecond = alphaLiquidFirst
            alphaSFirst = alphaSolidFirst
            alphaSSecond = alphaSolidFirst
        }

        val ta1 = entropFirst * meltingTempFirst + alphaLFirst * i.pow(2) - alphaSFirst * x.pow(2)
        val ta2 = entropFirst + GAS_CONSTANTE * ln((1 - x) / (1 - i))
        val tb1 = entropSecond * meltingTempSecond + alphaLSecond * (1 - i).pow(2) - alphaSSecond * (1 - x).pow(2)
        val tb2 = entropSecond + GAS_CONSTANTE * ln(x / i)

        temperature = (ta1 / ta2 + tb1 / tb2) / 2
        return ta1 * tb2 - tb1 * ta2
    }

    /**
     * Store value of solid and liquid phase depending on temperature.
     *
     * Value of solid and liquid stored in into percentages.
     * Temperature stored in kelvins.
     */
    data class PhasePoint(val solid: Double, val liquid: Double, val temperature: Double)

    companion object {
        /**
         * Step with which the algorithm find phase points.
         * If value lower than the accuracy is greater.
         *
         * Change very carefully it very fast increases calculation time
         */
        private const val CALCULATION_STEP = 0.001

        private const val START_VALUE = CALCULATION_STEP
        private const val FINISH_VALUE = 1 - CALCULATION_STEP // not 1 because it greatly increases the calculation time

        private const val GAS_CONSTANTE = 8.314 //universal gas constante
        private const val ACCURACY = 1e-13
    }
}