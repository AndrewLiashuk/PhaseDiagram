package com.andrew.liashuk.phasediagram.types

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.andrew.liashuk.phasediagram.BR
import kotlinx.android.parcel.Parcelize


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
) : Parcelable, BaseObservable() {
    // str varibable need for data binding
    // if use double variable for binding compiler didn't create MainFragmentBindingImpl file
    var meltingTempFirstStr: String
        @Bindable get() = meltingTempFirst.toNormalString()

        set(value) {
            value.toDoubleOrNull()?.let {
                if (it != meltingTempFirst) {
                    meltingTempFirst = it
                    notifyPropertyChanged(BR.meltingTempFirstStr)
                }
            }
        }


    var meltingTempSecondStr: String
        @Bindable get() = meltingTempSecond.toNormalString()

        set(value) {
            value.toDoubleOrNull()?.let {
                if (it != meltingTempSecond) {
                    meltingTempSecond = it
                    notifyPropertyChanged(BR.meltingTempSecondStr)
                }
            }
        }


    var entropFirstStr: String
        @Bindable get() = entropFirst.toNormalString()

        set(value) {
            value.toDoubleOrNull()?.let {
                if (it != entropFirst) {
                    entropFirst = it
                    notifyPropertyChanged(BR.entropFirstStr)
                }
            }
        }


    var entropSecondStr: String
        @Bindable get() = entropSecond.toNormalString()

        set(value) {
            value.toDoubleOrNull()?.let {
                if (it != entropSecond) {
                    entropSecond = it
                    notifyPropertyChanged(BR.entropSecondStr)
                }
            }
        }


    var alphaLFirstStr: String
        @Bindable get() = alphaLFirst.toNormalString()

        set(value) {
            value.toDoubleOrNull()?.let {
                if (it != alphaLFirst) {
                    alphaLFirst = it
                    notifyPropertyChanged(BR.alphaLFirstStr)
                }
            }
        }


    var alphaSFirstStr: String
        @Bindable get() = alphaSFirst.toNormalString()

        set(value) {
            value.toDoubleOrNull()?.let {
                if (it != alphaSFirst) {
                    alphaSFirst = it
                    notifyPropertyChanged(BR.alphaSFirstStr)
                }
            }
        }


    var alphaLSecondStr: String
        @Bindable get() = alphaLSecond.toNormalString()

        set(value) {
            value.toDoubleOrNull()?.let {
                if (it != alphaLSecond) {
                    alphaLSecond = it
                    notifyPropertyChanged(BR.alphaLSecondStr)
                }
            }
        }


    var alphaSSecondStr: String
        @Bindable get() = alphaSSecond.toNormalString()

        set(value) {
            value.toDoubleOrNull()?.let {
                if (it != alphaSSecond) {
                    alphaSSecond = it
                    notifyPropertyChanged(BR.alphaSSecondStr)
                }
            }
        }

    /**
     * Check on correct data availability
     *
     * @param type  always check availability of temperature and entropy, they must be biggert then 0
     *              for regular calculation need to exist alphaLFirst and alphaSFirst,
     *              for subregular calculation must attend all variables.
     *              Variables with suffix 'str' need for dataBinding,
     *              no need to check them
     * @return      <code>null</code> if all data is correct
     *              <code>string</code> return error text to show for user
     */
    fun checkData(type: SolutionType): String? {
        return when {
            meltingTempFirst == null -> "Enter temp first"
            meltingTempFirst ?: 0.0 < 0.0 -> "First temp must be bigger then 0"

            meltingTempSecond == null -> "Enter temp second"
            meltingTempSecond ?: 0.0 < 0.0 -> "Secong temp must be bigger then 0"

            entropFirst == null -> "Enter entrop first"
            entropFirst ?: 0.0 < 0.0 -> "First entrop must be bigger then 0"

            entropSecond == null -> "Enter entrop second"
            entropSecond ?: 0.0 < 0.0 -> "Secong entrop must be bigger then 0"


            else -> {
                when(type) {
                    SolutionType.REGULAR -> {
                        when {
                            alphaLFirst == null -> "Enter alphaL"
                            alphaSFirst == null -> "Enter alphaS"
                            else -> null
                        }
                    }
                    SolutionType.SUBREGULAR -> {
                        when {
                            alphaLFirst == null -> "Enter alphaL first"
                            alphaSFirst == null -> "Enter alphaS first"
                            alphaLSecond == null -> "Enter alphaL second"
                            alphaSSecond == null -> "Enter alphaS second"
                            else -> null
                        }
                    }
                    SolutionType.IDEAL -> null
                }
            }
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