package ly.slide.promo.testapp.presentation

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_main.*
import ly.slide.promo.testapp.R
import ly.slide.promo.testapp.platform.withPermissions
import ly.slide.promo.testapp.presentation.util.observeLiveData
import ly.slide.promo.testapp.presentation.widget.SliderView
import kotlin.properties.Delegates


class MainFragment : Fragment() {

    private var viewModel: MainViewModel by Delegates.notNull()
    private var sliderView: SliderView by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        sliderView = slideshow_container
        observeLiveData(viewModel.showSlide) { sliderView.showSlide(it) }
        observeLiveData(viewModel.prepareSlide) { sliderView.prepareSlide(it) }
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
}