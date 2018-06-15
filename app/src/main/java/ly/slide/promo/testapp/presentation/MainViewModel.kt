package ly.slide.promo.testapp.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ly.slide.promo.testapp.domain.Media
import ly.slide.promo.testapp.platform.MediaLoader
import org.joda.time.LocalTime
import java.util.concurrent.TimeUnit

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val showSlide: LiveData<Media> get() = _showSlide
    val prepareSlide: LiveData<Media> get() = _prepareSlide
    val localTime: LiveData<LocalTime> get() = _localTime

    private val _showSlide = MutableLiveData<Media>()
    private val _prepareSlide = MutableLiveData<Media>()
    private val _localTime = MutableLiveData<LocalTime>()

    private var mediaList: List<Media>? = null
    private var currentSlidePosition = -1

    private val compositeDisposable = CompositeDisposable()
    private var slideShowDisposable: Disposable? = null

    init {
        Observable.interval(1, TimeUnit.SECONDS)
                .map(::LocalTime)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(_localTime::setValue)
                .addTo(compositeDisposable)
    }

    fun loadMediaList() {
        getApplication<Application>()
                .baseContext
                .let(MediaLoader::getImagesAndVideos)
                .subscribeOn(Schedulers.io())
                .map { it.sortedBy(Media::addedAt) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy { mediaList = it }
                .addTo(compositeDisposable)
    }

    fun startSlideshow() {
        Observable.interval(4, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    mediaList?.let { slides ->
                        currentSlidePosition = (currentSlidePosition + 1) % slides.count()
                        _showSlide.value = slides[currentSlidePosition]
                        val nextSlidePosition = (currentSlidePosition + 1) % slides.count()
                        _prepareSlide.value = slides[nextSlidePosition]
                    }
                }
                .subscribe()
                .addTo(compositeDisposable)
                .also { slideShowDisposable = it }
    }

    fun pauseSlideshow() {
        slideShowDisposable?.dispose()
        slideShowDisposable = null
    }

    fun stopSlideshow() {
        pauseSlideshow()
        currentSlidePosition = -1
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }
}