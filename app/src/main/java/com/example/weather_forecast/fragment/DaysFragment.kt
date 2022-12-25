package com.example.weather_forecast.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather_forecast.R
import com.example.weather_forecast.adapters.WeatherAdapter
import com.example.weather_forecast.databinding.FragmentDaysBinding
import com.example.weather_forecast.models.MainViewModel
import com.example.weather_forecast.models.WeatherModel


class DaysFragment : Fragment(), WeatherAdapter.Listener {
    private lateinit var adapter:WeatherAdapter
    private lateinit var binding:FragmentDaysBinding
    private val model:MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentDaysBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        model.liveDataList.observe(viewLifecycleOwner){
            // subList обрезает список вместо этого (0 по N) получем (1 по N)
            adapter.submitList(it.subList(1, it.size))
        }
    }

    private fun init()= with(binding){
        adapter=WeatherAdapter(this@DaysFragment)
        rv.layoutManager=LinearLayoutManager(activity)
        rv.adapter=adapter
    }

    companion object {
        @JvmStatic
        fun newInstance() = DaysFragment()
    }

    // передаём новое значение из которого будут браться данные для главной карточки
    override fun onClick(item: WeatherModel) {
        model.liveDataCurrent.value=item
    }
}