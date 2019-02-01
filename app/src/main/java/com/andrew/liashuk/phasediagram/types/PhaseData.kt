package com.andrew.liashuk.phasediagram.types

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.andrew.liashuk.phasediagram.BR
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize


@Parcelize
data class PhaseData(
    var meltingTempFirst: Double,
    var meltingTempSecond: Double,
    var entropFirst: Double,
    var entropSecond: Double,
    var alphaLFirst: Double = 0.0,
    var alphaSFirst: Double = 0.0,
    var alphaLSecond: Double = -1.0, // if -1 use regular formula
    var alphaSSecond: Double = -1.0
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


    // show double in normal format
    // 20.00 show as 20,
    // 20.10 as 20.1
    // if 0.0 or -1.0 (default value), show nothing
    private fun Double.toNormalString(): String {
        return when (this) {
            0.0, -1.0 -> ""

            // round double by converting to long and compare with original double, if same show rounded
            this.toLong().toDouble() -> String.format("%d", this.toLong()) // show 20.0 as 20

            else -> String.format("%s", this)
        }
    }
}