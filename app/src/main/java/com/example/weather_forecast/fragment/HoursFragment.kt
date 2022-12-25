package com.example.weather_forecast.fragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather_forecast.adapters.WeatherAdapter
import com.example.weather_forecast.databinding.FragmentHoursBinding
import com.example.weather_forecast.models.MainViewModel
import com.example.weather_forecast.models.WeatherModel
import org.json.JSONArray
import org.json.JSONObject


class HoursFragment : Fragment() {
    private lateinit var binding: FragmentHoursBinding
    private lateinit var adapter:WeatherAdapter
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentHoursBinding.inflate(inflater, container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        model.liveDataCurrent.observe(viewLifecycleOwner){
            adapter.submitList(getHoursList(it))
        }
    }

    private fun initRcView()= with(binding){
        rcView.layoutManager=LinearLayoutManager(activity)
        adapter= WeatherAdapter(null)
        rcView.adapter=adapter


    }

    // цикол который переберает пришедший json ответ
    private fun getHoursList(itemWeatherModel:WeatherModel):List<WeatherModel>{
        val hoursArray=JSONArray(itemWeatherModel.hours)
        val list=ArrayList<WeatherModel>()
        for (i in 0 until hoursArray.length()){
            val item=WeatherModel(
                "",
                (hoursArray[i] as JSONObject).getString("time"),
                (hoursArray[i] as JSONObject).getJSONObject("condition").getString("text"),
                (hoursArray[i] as JSONObject).getString("temp_c"),
                "",
                "",
                (hoursArray[i] as JSONObject).getJSONObject("condition").getString("icon"),
                ""
            )
            list.add(item)
        }
        return list
    }

    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()
    }
}