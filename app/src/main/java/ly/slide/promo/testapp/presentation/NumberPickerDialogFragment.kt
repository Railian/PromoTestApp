package ly.slide.promo.testapp.presentation

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import ly.slide.promo.testapp.R
import kotlin.properties.Delegates

class NumberPickerDialogFragment : DialogFragment() {

    companion object {

        private const val ARG_TITLE = "arg:title"
        private const val ARG_MIN_VALUE = "arg:minValue"
        private const val ARG_MAX_VALUE = "arg:maxValue"
        private const val ARG_INITIAL_VALUE = "arg:initialValue"
        private const val ARG_VALUE_PLURALS_RES = "arg:valuePluralsRes"

        private const val POSITIVE_BUTTON_RES = R.string.apply
        private const val NEGATIVE_BUTTON_RES = R.string.cancel
    }

    private val title: String? by lazy { arguments?.getString(ARG_TITLE) }
    private val minValue: Int? by lazy { arguments?.getInt(ARG_MIN_VALUE) }
    private val maxValue: Int? by lazy { arguments?.getInt(ARG_MAX_VALUE) }
    private val initialValue: Int? by lazy { arguments?.getInt(ARG_INITIAL_VALUE) }
    private val valuePluralsRes: Int?  by lazy { arguments?.getInt(ARG_VALUE_PLURALS_RES) }

    private var seekBar: SeekBar by Delegates.notNull()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog
                .Builder(requireContext())
                .setTitle(title)
                .setView(createDialogView())
                .setPositiveButton(getString(POSITIVE_BUTTON_RES)) { _, _ ->
                    val listener = targetFragment as? OnNumberPickedListener
                    listener?.onNumberPicked(targetRequestCode, seekBar.progress)
                }
                .setNegativeButton(getString(NEGATIVE_BUTTON_RES)) { _, _ -> }
                .create()
    }

    private fun createDialogView(): View {
        val layoutInflater = LayoutInflater.from(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_number_picker, null, false)
        val textView: TextView = view.findViewById(R.id.text_view)
        seekBar = view.findViewById(R.id.seek_bar)
        minValue?.let(seekBar::setMin)
        maxValue?.let(seekBar::setMax)
        initialValue?.let(seekBar::setProgress)
        textView.text = valuePluralsRes
                ?.let { resources.getQuantityString(it, seekBar.progress, seekBar.progress) }
                ?: seekBar.progress.toString()
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, p2: Boolean) {
                textView.text = valuePluralsRes
                        ?.let { resources.getQuantityString(it, progress, progress) }
                        ?: progress.toString()
            }
        })
        return view
    }

    class Builder(
            private val context: Context,
            private val targetFragment: Fragment? = null,
            private val requestCode: Int = 0
    ) {

        private var title: String? = null
        private var minValue: Int? = null
        private var maxValue: Int? = null
        private var initialValue: Int? = null
        private var valuePluralsRes: Int? = null

        fun setTitle(@StringRes titleRes: Int): Builder {
            title = context.resources.getString(titleRes)
            return this
        }

        fun setMinValue(minValue: Int): Builder {
            this.minValue = minValue
            return this
        }

        fun setMaxValue(maxValue: Int): Builder {
            this.maxValue = maxValue
            return this
        }

        fun setInitialValue(value: Int): Builder {
            this.initialValue = value
            return this
        }

        fun setValuePlurals(@PluralsRes valuePluralsRes: Int): Builder {
            this.valuePluralsRes = valuePluralsRes
            return this
        }

        fun create(): NumberPickerDialogFragment {
            return NumberPickerDialogFragment().also {
                it.setTargetFragment(targetFragment, requestCode)
                it.arguments = bundleOf(
                        ARG_TITLE to title,
                        ARG_VALUE_PLURALS_RES to valuePluralsRes,
                        ARG_MIN_VALUE to minValue,
                        ARG_MAX_VALUE to maxValue,
                        ARG_INITIAL_VALUE to initialValue
                )
            }
        }
    }

    interface OnNumberPickedListener {
        fun onNumberPicked(requestCode: Int, value: Int)
    }
}