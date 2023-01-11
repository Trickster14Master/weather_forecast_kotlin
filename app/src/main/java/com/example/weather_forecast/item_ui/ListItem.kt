package com.example.weather_forecast.item_ui
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather_forecast.models.WeatherModel
import com.example.weather_forecast.ui.theme.BlueLight

@Composable
fun ListItem(item:WeatherModel, currentDay:MutableState<WeatherModel>){
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 3.dp). clickable {
            // отключаем возможность нажимать на карточку в прогнозе по часам
            if (item.hours.isEmpty()) return@clickable
            // меняем значение из которого берётся погода по часам
              currentDay.value=item
        },
        shape = RoundedCornerShape(6.dp),
        backgroundColor = BlueLight,
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(3.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = item.time, style = TextStyle(color = Color.White))
                Text(text = item.condition, style = TextStyle(color = Color.White))
            }

            Text(
                // если в currentTemp что то есть по это прогноз погоды для таблици погоды по часам, если пусто того прогноз погоды на целый день
                text = item.currentTemp.ifEmpty { "${item.minTemp }/${item.maxTemp}" }, style = TextStyle(color = Color.White, fontSize = 25.sp))

            AsyncImage(
                modifier = Modifier.size(30.dp),
                model = "https:${item.icon}",
                contentDescription = "im5",
            )
        }

    }
}