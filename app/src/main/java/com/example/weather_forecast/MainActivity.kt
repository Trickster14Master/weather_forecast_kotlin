package com.example.weather_forecast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weather_forecast.databinding.ActivityMainBinding
import com.example.weather_forecast.fragment.MainFragment
import org.json.JSONObject

const val API_KEY = "b02180cad667471fb1445520221312"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            // устанавливаем новый фрагмент с заменой предыдущего
            .replace(
                // где установить
                R.id.main_activity_constraint_layout,
                // что установить
                MainFragment.newInstance())
            // применить
            .commit()
    }

}

