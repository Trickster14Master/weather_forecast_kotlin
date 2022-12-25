package com.example.weather_forecast.adapters
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


// класс который поможет манять фрагменты, private  val list: List<Fragment> - список фрагментов которые надо переключать
class VpAdapter (fa:FragmentActivity, private  val list: List<Fragment>): FragmentStateAdapter(fa){
    // количество элементов для переключения
    override fun getItemCount(): Int {
        return list.size
    }

    // само переключение между фрагментами
    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}