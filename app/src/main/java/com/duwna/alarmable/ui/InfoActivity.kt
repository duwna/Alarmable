package com.duwna.alarmable.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.duwna.alarmable.R
import com.duwna.alarmable.data.api.WeatherResponse
import com.duwna.alarmable.data.database.alarm.Alarm
import com.duwna.alarmable.data.database.recipe.Recipe
import com.duwna.alarmable.databinding.ActivityInfoBinding
import com.duwna.alarmable.services.AlarmService
import com.duwna.alarmable.ui.custom.UnorderedListSpan
import com.duwna.alarmable.utils.*
import com.duwna.alarmable.viewmodels.InfoViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class InfoActivity : BaseActivity<InfoViewModel, ActivityInfoBinding>(R.layout.activity_info) {

    override val binding: ActivityInfoBinding by viewBinding()
    override val viewModel: InfoViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        showWhenLockedAndTurnScreenOn()
        super.onCreate(savedInstanceState)

        stopService(Intent(this, AlarmService::class.java))
        // update alarm after triggering
        val alarm = intent.getParcelableExtra<Alarm>(Alarm.KEY)
        if (alarm != null) viewModel.cancelAlarm(alarm)
        log(alarm)

        subscribeOnState()
        binding.btnOtherRecipe.setOnClickListener { viewModel.loadRecipe() }
    }

    private fun subscribeOnState() {
        viewModel.weather.collectOnLifecycle(this) { bindWeather(it) }
        viewModel.recipe.collectOnLifecycle(this) { bindRecipe(it) }
    }

    private fun bindRecipe(recipe: Recipe?) = with(binding) {
        recipe ?: return@with

        tvRecipeName.text = recipe.name
        tvRecipeDescription.text = recipe.description

        tvRecipeIngridients.text = buildSpannedString {
            inSpans(UnorderedListSpan(dpToPx(8), dpToPx(4), attrValue(R.attr.colorAccent))) {
                append(recipe.ingredients)
            }
        }

        Glide.with(this@InfoActivity).load(recipe.imgUrl)
            .apply(RequestOptions().transform(RoundedCorners(50)))
            .into(ivRecipe)

        ivRecipe.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(recipe.url)))
        }
    }

    private fun bindWeather(weather: WeatherResponse?) = with(binding) {

        progressCircular.isVisible = weather == null
        container.isVisible = weather != null

        weather ?: return

        tvCity.text = weather.name

        val celTmp = (weather.main.temp - 273.15).toInt()
        tvTemp.text = "${if (celTmp > 0) "+$celTmp" else celTmp}"

        val feelsCelTmp = (weather.main.feelsLike - 273.15).toInt()
        tvFeelsLike.text = "${if (feelsCelTmp > 0) "+$feelsCelTmp" else feelsCelTmp}"

        tvWind.text = "${weather.wind.speed} м/с"
        tvPressure.text = "${(weather.main.pressure / 1.333).toInt()} мм рт. ст."
        tvSunrise.text = Date(weather.sys.sunrise * 1000).format("HH:mm")
        tvSunset.text = Date(weather.sys.sunset * 1000).format("HH:mm")
        tvDescription.text = weather.weather[0].description.capitalize()

        setWeatherIcon(weather.weather[0].icon)
        setClothesText(celTmp)
    }

    private fun setClothesText(celTmp: Int) {
        binding.tvClothes.text = when {
            celTmp > 15 -> "Шорты, футболка, головной убор (панама или кепка) и легкая обувь (кроссовки, сланцы, летние туфли)"
            celTmp in 5..15 -> "Джинсы (брюки), кофта, легкая обувь"
            celTmp in 0..5 -> "Джинсы (брюки), кофта, куртка, кроссовки/туфли"
            celTmp in -5..0 -> "Джинсы (брюки), кофта, куртка, кроссовки/туфли, шапка, легкие перчатки;"
            celTmp in -10..-5 -> "Теплые штаны, кофта, куртка, зимняя обувь, шапка, перчатки;"
            else -> "Термобелье, теплые штаны, кофта, зимняя куртка, зимняя обувь, шапка, перчатки."
        }
    }


    private fun setWeatherIcon(icon: String) {
        binding.ivWeatherIcon.setImageResource(
            when (icon) {
                "01d" -> R.drawable.ic_weather_01d
                "02d" -> R.drawable.ic_weather_02d
                "02n" -> R.drawable.ic_weather_02n
                "03d", "03n" -> R.drawable.ic_weather_01d
                "04d", "04n" -> R.drawable.ic_weather_04d
                "09d", "09n" -> R.drawable.ic_weather_09d
                "10d" -> R.drawable.ic_weather_10d
                "10n" -> R.drawable.ic_weather_10n
                "11d", "11n" -> R.drawable.ic_weather_11d
                "13d", "13n" -> R.drawable.ic_weather_13d
                "50d", "50n" -> R.drawable.ic_weather_50d
                else -> R.drawable.ic_weather_03d
            }
        )
    }

    private fun showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }
}