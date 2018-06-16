package ly.slide.promo.testapp.presentation

import android.Manifest
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_main.*
import ly.slide.promo.testapp.R
import ly.slide.promo.testapp.platform.dp2px
import ly.slide.promo.testapp.platform.px2dp
import ly.slide.promo.testapp.platform.withPermissions
import ly.slide.promo.testapp.presentation.util.hide
import ly.slide.promo.testapp.presentation.util.observeLiveData
import ly.slide.promo.testapp.presentation.util.show
import kotlin.properties.Delegates


class MainFragment : Fragment(), NumberPickerDialogFragment.OnNumberPickedListener {

    companion object {
        private const val PICK_SLIDESHOW_INTERVAL = 1
        private const val PICK_ANIMATION_DURATION = 2
        private const val PICK_CLOCK_ELEVATION = 3
        private const val PICK_CLOCK_OPACITY = 4
    }

    private var viewModel: MainViewModel by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this)[MainViewModel::class.java]
        withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                request = true
        ) { granted ->
            if (granted) viewModel.loadMediaList()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData(viewModel.showSlide) { slider_view.showSlide(it) }
        observeLiveData(viewModel.prepareSlide) { slider_view.prepareSlide(it) }
        observeLiveData(viewModel.localTime) { it?.let { clock_view.time = it } }
    }

    override fun onStart() {
        super.onStart()
        viewModel.startSlideshow()
    }

    override fun onPause() {
        viewModel.pauseSlideshow()
        super.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                request = false
        ) { granted ->
            if (granted) viewModel.loadMediaList()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        when (clock_view.isShown) {
            true -> {
                menu.findItem(R.id.action_clock_display).isChecked = true
                menu.findItem(R.id.action_clock_elevation).isEnabled = true
                menu.findItem(R.id.action_clock_opacity).isEnabled = true
            }
            else -> {
                menu.findItem(R.id.action_clock_display).isChecked = false
                menu.findItem(R.id.action_clock_elevation).isEnabled = false
                menu.findItem(R.id.action_clock_opacity).isEnabled = false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_slideshow_interval -> {
                NumberPickerDialogFragment
                        .Builder(requireContext(), targetFragment = this, requestCode = PICK_SLIDESHOW_INTERVAL)
                        .setTitle(R.string.action_slideshow_interval)
                        .setMinValue(UiConstants.SLIDESHOW_INTERVAL_MIN.toInt())
                        .setMaxValue(UiConstants.SLIDESHOW_INTERVAL_MAX.toInt())
                        .setInitialValue(viewModel.slideshowInterval.toInt())
                        .setValuePlurals(R.plurals.slideshow_interval_value)
                        .create()
                        .show(fragmentManager, null)
                true
            }
            R.id.action_animation_duration -> {
                NumberPickerDialogFragment
                        .Builder(requireContext(), targetFragment = this, requestCode = PICK_ANIMATION_DURATION)
                        .setTitle(R.string.action_animation_duration)
                        .setMinValue(UiConstants.ANIMATION_DURATION_MIN.toInt())
                        .setMaxValue(UiConstants.ANIMATION_DURATION_MAX.toInt())
                        .setInitialValue(slider_view.animationDuration.toInt())
                        .setValuePlurals(R.plurals.animation_duration_value)
                        .create()
                        .show(fragmentManager, null)
                true
            }
            R.id.action_clock_display -> {
                when (item.isChecked) {
                    true -> clock_view.hide()
                    false -> clock_view.show()
                }
                true
            }
            R.id.action_clock_elevation -> {
                NumberPickerDialogFragment
                        .Builder(requireContext(), targetFragment = this, requestCode = PICK_CLOCK_ELEVATION)
                        .setTitle(R.string.action_clock_elevation)
                        .setMinValue(UiConstants.CLOCK_ELEVATION_MIN.toInt())
                        .setMaxValue(UiConstants.CLOCK_ELEVATION_MAX.toInt())
                        .setInitialValue(px2dp(clock_view.elevation).toInt())
                        .setValuePlurals(R.plurals.clock_elevation_value)
                        .create()
                        .show(fragmentManager, null)
                true
            }
            R.id.action_clock_opacity -> {
                NumberPickerDialogFragment
                        .Builder(requireContext(), targetFragment = this, requestCode = PICK_CLOCK_OPACITY)
                        .setTitle(R.string.action_clock_opacity)
                        .setMinValue(UiConstants.CLOCK_OPACITY_MIN)
                        .setMaxValue(UiConstants.CLOCK_OPACITY_MAX)
                        .setInitialValue((clock_view.alpha * 100).toInt())
                        .setValuePlurals(R.plurals.clock_opacity_value)
                        .create()
                        .show(fragmentManager, null)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNumberPicked(requestCode: Int, value: Int) {
        when (requestCode) {
            PICK_SLIDESHOW_INTERVAL -> viewModel.slideshowInterval = value.toLong()
            PICK_ANIMATION_DURATION -> slider_view.animationDuration = value.toLong()
            PICK_CLOCK_ELEVATION -> clock_view.elevation = dp2px(value.toFloat())
            PICK_CLOCK_OPACITY -> clock_view.alpha = value / 100f
        }
    }
}