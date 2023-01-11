package com.example.weather_forecast
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weather_forecast.models.WeatherModel
import com.example.weather_forecast.screens.MainScreens
import com.example.weather_forecast.ui.theme.Weather_forecastTheme
import org.json.JSONObject


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Weather_forecastTheme {
                // создаём переменную с отслеживанием состяния c прогноза погоды для списка
                val daysList=remember{
                    mutableStateOf(listOf<WeatherModel>())
                }

                // создаём переменную с отслеживанием состяния c прогноза погоды для карточки
                val currentDay=remember{
                    mutableStateOf(WeatherModel(
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                    ))
                }
                getResult("London", this, daysList, currentDay)


                MainScreens(daysList, currentDay)
            }
        }
    }
}


// получаем данные с сервера
private fun getResult(city:String, context: Context, daysList:MutableState<List<WeatherModel>>, currentDay:MutableState<WeatherModel>){
    val url = "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY" +
            "&q=$city" +
            "&days=" +
            "3" +
            "&aqi=no&alerts=no"

    // создаём очередь для запросов
    val queue=Volley.newRequestQueue(context)

    // запрос
    val stringRequest=StringRequest(
        Request.Method.GET,
        url,
        // результат
        {
            response->
            // приобразыем пришедшие данные
            val list= getWeatherByDays(response)
            // записываем 0 элемент из списка (сегодняшний день) в переменную для карточки
            currentDay.value = list[0]
            // записываем значения в переменную
            daysList.value=list
        },
        // ошибка
        {
            error->
        }
    )

    // выполняем запрос
    queue.add(stringRequest)
}


// функция которая будет получать список со всему днями
private fun getWeatherByDays(response:String):List<WeatherModel>{
    // если данные не пришли
    if(response.isEmpty()) return listOf()

    // список для заполнения
    val weatherList=ArrayList<WeatherModel>()

    // основной обьект со свсей информацией
    val mainObject=JSONObject(response)
    // прогноз погоды на день
    val days=mainObject.getJSONObject("forecast").getJSONArray("forecastday")

    val city=mainObject.getJSONObject("location").getString("name")

    // перебераем массив
    for(i in 0 until days.length()){
        val item=days[i] as JSONObject
        weatherList.add(
            WeatherModel(
                city,
                item.getString("date"),
                "",
                item.getJSONObject("day").getJSONObject("condition").getString("text"),
                item.getJSONObject("day").getJSONObject("condition").getString("icon"),
                item.getJSONObject("day").getString("maxtemp_c"),
                item.getJSONObject("day").getString("mintemp_c"),
                item.getJSONArray("hour").toString()
            )
        )
    }
    // перезаписываем на 0 значение копию для отоброжения информации на главной карточке
    weatherList[0] = weatherList[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        currentTemp = mainObject.getJSONObject("current").getString("temp_c"),
    )
    return weatherList
}