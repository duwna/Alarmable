package com.duwna.alarmable.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.duwna.alarmable.R
import com.duwna.alarmable.api.WeatherResponse
import com.duwna.alarmable.database.recipe.Recipe
import com.duwna.alarmable.ui.custom.UnorderedListSpan
import com.duwna.alarmable.utils.*
import com.duwna.alarmable.viewmodels.InfoViewModel
import com.duwna.alarmable.viewmodels.Notify
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_info.*
import java.util.*


class InfoActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: InfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        viewModel = ViewModelProvider(this).get(InfoViewModel::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (isPermitted()) loadLocation()
        else request()

        viewModel.weather.observe(this, Observer { bindWeather(it) })
        viewModel.recipe.observe(this, Observer { bindRecipe(it) })
        viewModel.observeNotifications(this) { renderNotification(it) }

        btn_other_recipe.setOnClickListener { viewModel.loadRecipe() }
    }

    private fun bindRecipe(recipe: Recipe) {

        tv_recipe_name.text = recipe.name
        tv_recipe_description.text = recipe.description

        tv_recipe_ingridients.text = buildSpannedString {
            inSpans(UnorderedListSpan(dpToPx(8), dpToPx(4), attrValue(R.attr.colorAccent))) {
                append(recipe.ingredients)
            }
        }

        Glide.with(this).load(recipe.imgUrl)
            .apply(RequestOptions().transform(RoundedCorners(50)))
            .into(iv_recipe)

        iv_recipe.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(recipe.url)))
        }
    }

    private fun bindWeather(weather: WeatherResponse?) {

        progress_circular.isVisible = weather == null
        container.isVisible = weather != null

        weather ?: return

        tv_city.text = weather.name

        val celTmp = (weather.main.temp - 273.15).toInt()
        tv_temp.text = "${if (celTmp > 0) "+$celTmp" else celTmp}"

        val feelsCelTmp = (weather.main.feelsLike - 273.15).toInt()
        tv_feels_like.text = "${if (feelsCelTmp > 0) "+$feelsCelTmp" else feelsCelTmp}"

        tv_wind.text = "${weather.wind.speed} м/с"
        tv_pressure.text = "${(weather.main.pressure / 1.333).toInt()} мм рт. ст."
        tv_sunrise.text = Date(weather.sys.sunrise * 1000).format("HH:mm")
        tv_sunset.text = Date(weather.sys.sunset * 1000).format("HH:mm")
        tv_description.text = weather.weather[0].description.capitalize()

        setWeatherIcon(weather.weather[0].icon)
        setClothesText(celTmp)
    }

    private fun setClothesText(celTmp: Int) {
        tv_clothes.text = when {
            celTmp > 15 -> "Шорты, футболка, головной убор (панама или кепка) и легкая обувь (кроссовки, сланцы, летние туфли)"
            celTmp in 5..15 -> "Джинсы (брюки), кофта, легкая обувь"
            celTmp in 0..5 -> "Джинсы (брюки), кофта, куртка, кроссовки/туфли"
            celTmp in -5..0 -> "Джинсы (брюки), кофта, куртка, кроссовки/туфли, шапка, легкие перчатки;"
            celTmp in -10..-5 -> "Теплые штаны, кофта, куртка, зимняя обувь, шапка, перчатки;"
            else -> "Термобелье, теплые штаны, кофта, зимняя куртка, зимняя обувь, шапка, перчатки."
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                viewModel.loadWeather(location.latitude, location.longitude)
            } else {
                Snackbar.make(
                    container,
                    "Не удалось получить последнее местоположение",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setWeatherIcon(icon: String) {
        iv_weather_icon.setImageResource(
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

    private fun isPermitted(): Boolean = ActivityCompat
        .checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun request() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun renderNotification(notify: Notify) {
        Snackbar.make(container, notify.message, Snackbar.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    loadLocation()
                } else {
                    Snackbar.make(
                        container,
                        "Для просмотра погоды необходимо дать разрешение на определения города",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                return
            }
            else -> {
            }
        }
    }
}