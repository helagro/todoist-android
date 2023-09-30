package se.helagro.postmessenger.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences


//NOT THREADING SAFE
class StorageHandler private constructor(context: Context) {

    companion object{
        private const val PREFERENCES_NAME = "main_preferences"
        private var instance: StorageHandler? = null

        fun init(context: Context){
            instance = StorageHandler(context)
        }

        fun getInstance(): StorageHandler {
            if(instance == null) throw Exception("StorageHandler has not been initialized")
            return instance!!
        }
    }

    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE)

    fun getString(id: SettingsID): String?{
        return sharedPreferences.getString(id.value, null)
    }

    @SuppressLint("ApplySharedPref")
    fun setString(id: SettingsID, value: String){
        val editor = sharedPreferences.edit()
        editor.putString(id.value, value)
        editor.commit()
    }
}