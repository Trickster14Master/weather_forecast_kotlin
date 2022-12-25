package com.example.weather_forecast.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_forecast.R
import com.example.weather_forecast.databinding.ListItemBinding
import com.example.weather_forecast.models.WeatherModel
import com.squareup.picasso.Picasso

class WeatherAdapter(val listener:Listener?):ListAdapter<WeatherModel, WeatherAdapter.Holder>(Comparator()){

    // класс в нутри которого хронится элемент для заполнения, и ведётся взаимодействие с элементом
    class Holder(view:View, private val listener: Listener?):RecyclerView.ViewHolder(view){
        val binding = ListItemBinding.bind(view)

        // item который мы получем в дальнейшем из bind
        var itemTemp:WeatherModel?=null

        // функция которая будет срабатывать при нажатии
        init {
            // itemView нажатый элемент
            itemView.setOnClickListener{
                itemTemp?.let { it1 -> listener?.onClick(it1) }
            }
        }

        // заполняем элементы из разметки полученными данными
        fun bind(item:WeatherModel)= with(binding){
            itemTemp=item
            tvDate.text=item.time
            tvCondition.text=item.condition
            tvTemp2.text=item.currentTemp.ifEmpty { "${item.maxTemp} / ${item.minTemp}" }
            Picasso.get().load("https:"+item.imageUrl).into(im)
        }
    }

    // данный класс сравнивает старый список элементов и новый, и в зависимости от результата добавляет новые элементы или удаляет
    class Comparator:DiffUtil.ItemCallback<WeatherModel>(){
        // сравнимает старый список с новым, в реальном проекте надо стравнивать по id
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem==newItem
        }

    }

    // функция которая создаёт шаблон
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        // инициализируем шаблон
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return Holder(view, listener)
    }

    // функция которая выдаёт id при создании нового элента
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    // интерфейс благодоря которому можно будет выбирать день с прогнозом погоды
    interface Listener{
        fun onClick(item:WeatherModel)
    }
}