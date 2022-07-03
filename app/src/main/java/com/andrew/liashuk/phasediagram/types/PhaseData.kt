package com.andrew.liashuk.phasediagram.types

import android.os.Parcelable
import com.andrew.liashuk.phasediagram.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhaseData(
    var meltingTempFirst: Double? = null,
    var meltingTempSecond: Double? = null,
    var entropFirst: Double? = null,
    var entropSecond: Double? = null,
    var alphaLFirst: Double? = null,
    var alphaSFirst: Double? = null,
    var alphaLSecond: Double? = null,
    var alphaSSecond: Double? = null
) : Parcelable {

    /**
     * Check on correct data availability
     *
     * @param type  always check availability of temperature and entropy, they must be biggert then 0
     *              for regular calculation need to exist alphaLFirst and alphaSFirst,
     *              for subregular calculation must attend all variables.
     *              Variables with suffix 'str' need for dataBinding,
     *              no need to check them
     * @return      <code>null</code> if all data is correct
     *              <code>int</code> return error text id to show for user
     */
    fun checkData(type: SolutionType): Int? {
        return when {
            meltingTempFirst == null -> R.string.empty_first_temp
            meltingTempFirst ?: 0.0 < 0.0 -> R.string.small_first_temp

            meltingTempSecond == null -> R.string.empty_second_temp
            meltingTempSecond ?: 0.0 < 0.0 -> R.string.small_second_temp

            entropFirst == null -> R.string.empty_first_entrop
            entropFirst ?: 0.0 < 0.0 -> R.string.small_first_entrop

            entropSecond == null -> R.string.empty_second_entrop
            entropSecond ?: 0.0 < 0.0 -> R.string.small_second_entrop

            else -> {
                when(type) {
                    SolutionType.REGULAR -> {
                        when {
                            alphaLFirst == null -> R.string.empty_alpha_l
                            alphaSFirst == null -> R.string.empty_alpha_s
                            else -> null
                        }
                    }
                    SolutionType.SUBREGULAR -> {
                        when {
                            alphaLFirst == null -> R.string.empty_first_alpha_l
                            alphaSFirst == null -> R.string.empty_first_alpha_s
                            alphaLSecond == null -> R.string.empty_second_alpha_l
                            alphaSSecond == null -> R.string.empty_second_alpha_s
                            else -> null
                        }
                    }
                    SolutionType.IDEAL -> null
                }
            }
        }
    }

    /**
     * Set alpha as null for different solution types for prevent build with wrong data
     */
    fun changeType(type: SolutionType) {
        when(type) {
            SolutionType.IDEAL -> {
                alphaLFirst = null
                alphaSFirst = null
                alphaLSecond = null
                alphaSSecond = null
            }
            SolutionType.REGULAR -> {
                alphaLSecond = null
                alphaSSecond = null
            }
            SolutionType.SUBREGULAR -> Unit
        }
    }

    /**
     * Show double in normal format
     *
     * 20.00 show as 20,
     * 20.10 as 20.1
     * if null, show nothing
    */
    private fun Double?.toNormalString(): String {
        return when (this) {
            null -> ""

            // round double by converting to long and compare with original double, if same show rounded
            this.toLong().toDouble() -> String.format("%d", this.toLong()) // show 20.0 as 20

            else -> String.format("%s", this)
        }
    }
}