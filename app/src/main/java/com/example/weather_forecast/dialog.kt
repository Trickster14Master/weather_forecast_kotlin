package com.example.weather_forecast

import android.content.Context
import androidx.appcompat.app.AlertDialog


// функция которая будет спрашивать у пользователя включить ли GPS
object DialogManager {
    fun locationSettingsDialog(context: Context, listener:Listener){
        val builder=AlertDialog.Builder(context)
        val dialog=builder.create()
        dialog.setTitle("Выключен GPS")
        dialog.setMessage("Хотите включить GPS")
        // если пользователь согласился
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok"){_,_->
            listener.onClick()
            dialog.dismiss()
        }
        // не согласился
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Ok"){_,_->
            dialog.dismiss()
        }
        // запуск
        dialog.show()

    }

    interface Listener{
        fun onClick()
    }
}