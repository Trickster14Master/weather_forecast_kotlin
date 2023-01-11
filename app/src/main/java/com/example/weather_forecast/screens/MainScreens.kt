package com.example.weather_forecast.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather_forecast.R
import com.example.weather_forecast.models.WeatherModel
import com.example.weather_forecast.ui.theme.BlueLight
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@Composable

fun MainScreens(daysList:MutableState<List<WeatherModel>>, currentDay:MutableState<WeatherModel>) {
    // картинка которая будет на заднем фоне
    Image(
        painter = painterResource(id = R.drawable.rrr),
        contentDescription = "im1",
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.7f),
        // адаптируем картинку по экран
        contentScale = ContentScale.FillBounds
    )

    // основной контейнер 
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        MainCard(currentDay)
        TabLayout(daysList, currentDay)
    }
}


@Composable
fun MainCard(currentDay:MutableState<WeatherModel>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = BlueLight,
        // приподнятие карточки
        elevation = 0.dp,
        // закругление
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = currentDay.value.time, color = Color.White)
                // картинка из интернета
                AsyncImage(
                    modifier = Modifier.size(30.dp),
                    model = "https:${currentDay.value.icon}",
                    contentDescription = "im5",
                )
            }

            Text(text = currentDay.value.city, color = Color.White, style = TextStyle(fontSize = 40.sp))

            Text(modifier = Modifier.padding(4.dp),
                text = if(currentDay.value.currentTemp.isNotEmpty())currentDay.value.currentTemp
                else "${currentDay.value.maxTemp}",
                color = Color.White, style = TextStyle(fontSize = 65.sp))

            Text(modifier = Modifier.padding(2.dp),text = currentDay.value.condition, color = Color.White, style = TextStyle(fontSize = 20.sp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { }) {
                    Icon(painter = painterResource(id = R.drawable.search_icon), contentDescription = "icon1", tint = Color.White)
                }

                Text(modifier = Modifier.padding(top = 6.dp),text = "${currentDay.value.maxTemp} / ${currentDay.value.minTemp}", color = Color.White, style = TextStyle(fontSize = 16.sp))

                IconButton(onClick = { }) {
                    Icon(painter = painterResource(id = R.drawable.sync_icon), contentDescription = "icon2", tint = Color.White)
                }

            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabLayout(daysList:MutableState<List<WeatherModel>>, currentDay: MutableState<WeatherModel>){

    // переменная с спискои имён для tabRow
    val tabList= listOf("HOURS","DAYS")
    //переменная которая отвечает за переключение страниц
    val pagerState = rememberPagerState()
    // переменная отвечает за переключение элементов в TabRow
    val tabIndex=pagerState.currentPage
    // переменная для запуска анимаций
    val coroutineScope= rememberCoroutineScope()


    Column(modifier = Modifier
        .padding(top = 5.dp)
        .clip(RoundedCornerShape(10.dp))) {
        TabRow(
            selectedTabIndex = tabIndex,
            indicator = {pos->
                        TabRowDefaults.Indicator(Modifier.pagerTabIndicatorOffset(pagerState, pos))
            },
            backgroundColor = BlueLight,
            contentColor = Color.White) {
            // перебираем список с именами
            tabList.forEachIndexed { index, text ->
                Tab(
                    selected =false,
                    // привязываем анимацию
                    onClick ={
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    //текст который будет отображаться в элементах
                    text={ Text(text = text, color=Color.White)}
                )
            }
        }
        // тут будет осуществляться перехож между экранами
        HorizontalPager(count = tabList.size, state = pagerState, modifier = Modifier.weight(1.0f))
        {// index указывает какая страница открыта
                index->
                    // присваиваем значение в зависимости от выбранного tab
                    val list=when(index){
                        // 0 и 1 это выбранные tab
                        // если выбран прогноз по часам
                        0-> getWeatherByHours(currentDay.value.hours)
                        // если выбран прогноз по дням
                        1->daysList.value
                        else->daysList.value
                    }
            MainList(list,  currentDay)
            
        }
    }
}

@Composable
fun MainList(list: List<WeatherModel>, currentDay: MutableState<WeatherModel>){
    LazyColumn(modifier = Modifier.fillMaxSize()){
        itemsIndexed(
            list
        )

        {
                index, item->
            com.example.weather_forecast.item_ui.ListItem(item, currentDay)
        }

    }
}

// функция которая будет выводить прогноз погоды по часам
private fun getWeatherByHours(hours:String):List<WeatherModel>{
    if (hours.isEmpty()) return listOf()

    val hoursArray = JSONArray(hours)
    val list = ArrayList<WeatherModel>()
    for(i in 0 until hoursArray.length()){
        val item=hoursArray[i] as JSONObject
        list.add(
            WeatherModel(
                "",
                item.getString("time"),
                item.getString("temp_c").toFloat().toInt().toString()+"C",
                item.getJSONObject("condition").getString("text"),
                item.getJSONObject("condition").getString("icon"),
                "",
                "",
                ""
            )
        )
    }
    return list
}


