package com.example.weather_forecast.fragment
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

// функция которая определяет дал ли пользователь разрешение на использование место положения
fun Fragment.isPermissionGranted(p:String):Boolean {
    return ContextCompat.checkSelfPermission(activity as AppCompatActivity,p)==PackageManager.PERMISSION_GRANTED
}