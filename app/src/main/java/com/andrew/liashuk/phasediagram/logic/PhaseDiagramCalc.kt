package com.andrew.liashuk.phasediagram.logic

import kotlin.math.*

class PhaseDiagramCalc(
    meltingTempFirst: Double,
    meltingTempSecond: Double,
    entropFirst: Double,
    entropSecond: Double,
    alphaLFirst: Double = 0.0,
    alphaSFirst: Double = 0.0,
    alphaLSecond: Double = -1.0, // if -1 use regular formula
    alphaSSecond: Double = -1.0
) {

    /**
     * Step with which the algorithm find phase points.
     * If value lower than the accuracy is greater.
     *
     * Change very carefully it very fast increases calculation time
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val calculationStep = 0.0001

    private val startVal = calculationStep
    private val finishVal = 0.99995 // not 1 because it greatly increases the calculation time

    private val GAS_CONSTANTE = 8.314 //universal gas constante
    private val ACCURACY = 1e-13

    private var mMeltingTempFirst = meltingTempFirst //melting Points (temperature of fusion), K
    private var mMeltingTempSecond = meltingTempSecond
    private var mEntropFirst = entropFirst //entropies of fusion, J/K/mol
    private var mEntropSecond = entropSecond
    private var mAlphaLFirst = alphaLFirst //interaction parameter in Liquid phase
    private var mAlphaSFirst = alphaSFirst //interaction parameter in Solid phase
    private var mAlphaLSecond = alphaLSecond
    private var mAlphaSSecond = alphaSSecond

    private var mTemperature = 0.0
    private var mPoints = mutableListOf<PhasePoint>()


    /**
     * Function that calculate PhaseDiagram.
     *
     * @return  collection of PhasePoints that store value of solid and liquid phase
     *          depending on temperature.
     */
    fun calculatePhaseDiagram() : MutableCollection<PhasePoint> {
        mPoints.add(PhasePoint(0.0, 0.0, mMeltingTempFirst)) // add first point

        for (i in startVal..finishVal step calculationStep) {
            bisection(i)?.let {
                mPoints.add(PhasePoint(i, it, mTemperature))
            }
        }

        mPoints.add(PhasePoint(1.0, 1.0, mMeltingTempSecond)) // add end point
        return mPoints
    }


    /**
     * Store value of solid and liquid phase depending on temperature.
     *
     * Input value of solid and liquid transfer into percentages by multiply by 100.
     * Temperature store in kelvins.
     */
    class PhasePoint(var solid: Double, var liquid: Double, var temperature: Double) {
        init {
            solid *= 100 //transfer into percentages
            liquid *= 100
        }
    }


    //solution of equation tempFunc(x)=0
    private fun bisection(i: Double): Double?
    {
        var x: Double
        var xMin = 0.0000001
        var xMax = 0.9999999

        val fa = tempFunc(xMin, i)
        val fb = tempFunc(xMax, i)

        if (fa.sign == fb.sign) { return null }

        do
        {
            x = xMin + (xMax - xMin) / 2
            val fx = tempFunc(x, i)

            if (fa.sign == fx.sign) { xMin = x; } else { xMax = x; }

        } while (abs(xMax - xMin)  > ACCURACY)

        return x
    }


    //Temperature function
    private fun tempFunc(x: Double, i: Double): Double
    {
        var alphaL1 = mAlphaLFirst
        var alphaL2 = mAlphaLFirst
        var alphaS1 = mAlphaSFirst
        var alphaS2 = mAlphaSFirst

        if (mAlphaLSecond != -1.0 && mAlphaSSecond != -1.0) { // use subregular formula
            alphaL1 = mAlphaLSecond + 2 * (mAlphaLFirst - mAlphaLSecond) * (1 - i)
            alphaL2 = mAlphaLFirst + 2 * (mAlphaLSecond - mAlphaLFirst) * i
            alphaS1 = mAlphaSSecond + 2 * (mAlphaSFirst - mAlphaSSecond) * (1 - x)
            alphaS2 = mAlphaSFirst + 2 * (mAlphaSSecond - mAlphaSFirst) * x
        }

        val ta1 = mEntropFirst * mMeltingTempFirst + alphaL1 * i.pow(2) - alphaS1 * x.pow(2)
        val ta2 = mEntropFirst + GAS_CONSTANTE * log10(((1 - x) / (1 - i)))
        val tb1 = mEntropSecond * mMeltingTempSecond + alphaL2 * (1 - i).pow(2) - alphaS2 * (1 - x).pow(2)
        val tb2 = mEntropSecond + GAS_CONSTANTE * log10(x / i)

        mTemperature = (ta1 / ta2 + tb1 / tb2) / 2
        return ta1 * tb2 - tb1 * ta2
    }


    // add step in for loop for double
    private infix fun ClosedRange<Double>.step(step: Double): Iterable<Double> {
        require(start.isFinite())
        require(endInclusive.isFinite())
        require(step > 0.0) { "Step must be positive, was: $step." }
        val sequence = generateSequence(start) { previous ->
            if (previous == Double.POSITIVE_INFINITY) return@generateSequence null
            val next = previous + step
            if (next > endInclusive) null else next
        }
        return sequence.asIterable()
    }
}

