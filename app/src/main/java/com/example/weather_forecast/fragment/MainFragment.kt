package com.example.weather_forecast.fragment
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weather_forecast.DialogManager
import com.example.weather_forecast.adapters.VpAdapter
import com.example.weather_forecast.databinding.FragmentMainBinding
import com.example.weather_forecast.models.MainViewModel
import com.example.weather_forecast.models.WeatherModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject


class MainFragment : Fragment() {
    // переменная которая будет определять место положение
    private lateinit var fLocationClient:FusedLocationProviderClient
    private val API_KEY = "b02180cad667471fb1445520221312"
    private lateinit var binding: FragmentMainBinding
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private val listFragment = listOf(HoursFragment.newInstance(), DaysFragment.newInstance())
    private val nameTabLayoutMediator = listOf("Hours", "Days")

    // инициализируем переменную для хранения данных которые будут храниться в главной карточке
    private val model:MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        init()
        updateCurrentCard()
    }

    // функция которая будет подписана на обновление данных для главной карточки
    private fun updateCurrentCard()= with(binding){
        // подписываемся на обновление
        model.liveDataCurrent.observe(viewLifecycleOwner){item->
            tvData.text=item.time
            textView2.text=item.city
            tvTemp.text=item.currentTemp.ifEmpty { "${item.maxTemp}" }
            textView4.text=item.condition
            tvMacMin.text=if(item.currentTemp.isEmpty())"" else "${item.maxTemp}C/${item.minTemp}"

            // загружаем картинку из интернета
            Picasso.get().load("https:"+item.imageUrl).into(imWeather)
        }
    }

    // функция которая вызывает диалоговое окно где определяется можноли использовать место положение
    private fun permissionListener() {
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }

    // проверяем далли пользователь разрешение ранее
    private fun checkPermission() {
        // если не дал
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun init() = with(binding) {
        // инициализируем переменную для место положения
        fLocationClient=LocationServices.getFusedLocationProviderClient(requireContext())
        // инициализируем адаптер и передаём список фрагментов которые будем переключать
        val adapter = VpAdapter(activity as FragmentActivity, listFragment)
        vp.adapter = adapter

        // присваиваем имена элементам в tabLayout
        TabLayoutMediator(tabLayout, vp) { tab, pos ->
            tab.text = nameTabLayoutMediator[pos]
        }.attach()
        ibSync.setOnClickListener{
            checkLocation()
        }
    }

    // запускаем функцию поиска место положения каждый раз после временного выхода из приложения
    override fun onResume() {
        super.onResume()
        checkLocation()
    }

    // функция которая определяет включен ли gps
    private fun isLocationEnabled():Boolean{
        val lm=activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun checkLocation(){
        if(isLocationEnabled()){
            getLocation()
        }else{
            DialogManager.locationSettingsDialog(requireContext(), object:DialogManager.Listener{
                override fun onClick() {
                    // вызываем интерфейс в котором спрашиваем пользователя можноли использовать GPS
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                }
            })
        }
    }

    // функция определяет место положение
    private fun getLocation(){
        val ct=CancellationTokenSource()
        // определяет доноли разрешение на использование геолокации
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        // если дано определяем координаты
        fLocationClient
            .getCurrentLocation(100, ct.token)
            .addOnCompleteListener{
                // передаём каардинаты в фунции для запросов
                requestWeatherData("${it.result.latitude},${it.result.longitude}")
            }
    }


    // обращение к API
    private fun requestWeatherData(cityName: String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                API_KEY +
                "&q=" +
                cityName +
                "&days=" +
                "3" +
                "&aqi=no&alerts=no"

        // создаём переменную с очередью для запросов
        val queue = Volley.newRequestQueue(context)


        // переменная с запросом
        val stringRequest = StringRequest(
            // метод для получения данных
            Request.Method.GET,
            // url по которому делать запрос
            url,
            // пришедшие данные
            { response ->parserWeatherDate(response)
            },
            // ошибка
            { error ->
                Log.d("MyLog", "Volley error: $error")
            }
        )

        // добавляем запрос в очередь
        queue.add(stringRequest)
    }

    private fun parserWeatherDate(result: String) {
        val mainObject = JSONObject(result)
        val list=parsDays(mainObject)
        parseCurrentDate(mainObject, list[0])
    }

    // данная функция будет отвечать за отоброжение верхней карточки, получает только 1 день
    private fun parseCurrentDate(mainObject:JSONObject, weatherItem:WeatherModel, ){
        val item = WeatherModel(
            // получаем название города из пришедшего json
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            // полдучем json обьект из другого json
            mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getString("temp_c"),
            weatherItem.maxTemp,
            weatherItem.minTemp,
            mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
            weatherItem.hours
        )

        // передаём полученные данные в переменную которая будет доступна везде
        model.liveDataCurrent.value=item
    }

    // функция для парсинга списка, получает прогноз погода сразу на несколько дней
    private fun parsDays(mainObject:JSONObject):List<WeatherModel>{
        val list=ArrayList<WeatherModel>()
        val name=mainObject.getJSONObject("location").getString("name")
        // список пришедшего json обьекта
        val daysArray=mainObject.getJSONObject("forecast").getJSONArray("forecastday")


        for (i in 0 until daysArray.length()){
            val day = daysArray[i] as JSONObject
            val item = WeatherModel(
                name,
                day.getString("date"),
                day.getJSONObject("day").getJSONObject("condition")
                    .getString("text"),
                "",
                day.getJSONObject("day").getString("maxtemp_c"),
                day.getJSONObject("day").getString("mintemp_c"),
                day.getJSONObject("day").getJSONObject("condition")
                    .getString("icon"),
                day.getJSONArray("hour").toString()
            )
            list.add(item)
        }
        model.liveDataList.value=list
        return list
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}