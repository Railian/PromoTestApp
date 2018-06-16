package ly.slide.promo.testapp.presentation

import android.Manifest
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_main.*
import ly.slide.promo.testapp.R
import ly.slide.promo.testapp.platform.withPermissions
import ly.slide.promo.testapp.presentation.util.observeLiveData
import ly.slide.promo.testapp.presentation.widget.SliderView
import kotlin.properties.Delegates


class MainFragment : Fragment(), NumberPickerDialogFragment.OnNumberPickedListener {

    companion object {
        private const val PICK_SLIDESHOW_INTERVAL = 1
        private const val PICK_ANIMATION_DURATION = 2
    }

    private var sliderView: SliderView by Delegates.notNull()
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
        sliderView = slider_view
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
                        .setInitialValue(sliderView.animationDuration.toInt())
                        .setValuePlurals(R.plurals.animation_duration_value)
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
            PICK_ANIMATION_DURATION -> sliderView.animationDuration = value.toLong()
        }
    }
}