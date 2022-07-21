package pin.up.bingo

import android.annotation.SuppressLint
import android.app.Application

class App : Application() {
    companion object{
        var money = 1000
        var advertisingID : String? = null
        var data : DataPref = DataPref()
        var firstLaunch = false
        var easyFlow = EasyFlow()
        var state : Int = 120
        var noInternetConnection = false
        var appsflyerID : String? = null
        var audio = true
        @SuppressLint("StaticFieldLeak") lateinit var store : DataManager
    }
}