package com.example.weather_forecast.models
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather_forecast.models.WeatherModel

// класс который будет обновлять данные в фрагментах
class MainViewModel : ViewModel() {
    // прогноз погоды по часам
    val liveDataCurrent=MutableLiveData<WeatherModel>()

    // прогноз погоды по дням
    val liveDataList=MutableLiveData<List<WeatherModel>>()
}