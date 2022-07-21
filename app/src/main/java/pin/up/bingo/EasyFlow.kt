package pin.up.bingo

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import com.appsflyer.AppsFlyerLib
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.onesignal.OneSignal

class EasyFlow {
    fun logEvent(eventName: String, context : Context){
        val eventValues = HashMap<String, Any>()
        eventValues[context.getString(R.string.vis)]  = App.data.visitor?: context.getString(R.string.no_vis)
        AppsFlyerLib.getInstance().logEvent(context , eventName, eventValues)
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, eventName)
        FirebaseAnalytics.getInstance(context).logEvent(eventName, bundle)
    }

    fun getUIDAdvertising(context : Context) : String?{
        return AdvertisingIdClient.getAdvertisingIdInfo(context).id
    }

    fun createAppsFlyer(context: Context){
        AppsFlyerLib.getInstance().enableFacebookDeferredApplinks(false)
        AppsFlyerLib.getInstance().init(context.getString(R.string.appsflyer_key), null, context)
        AppsFlyerLib.getInstance().start(context)
    }

    fun getUIDAppsFlyer(activity : Activity) : String?{
        return AppsFlyerLib.getInstance().getAppsFlyerUID(activity)!!
    }


    fun createOneSignal(context: Context){
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.startInit ( context).inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init();
    }

    fun logActivity(activity : Activity){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, activity.javaClass.simpleName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, activity.javaClass.simpleName)
        FirebaseAnalytics.getInstance(activity).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun disableOneSignal(){
        OneSignal.setSubscription(false)
    }

    fun checkInternetConnection(context: Context) : Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) return true
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return true
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return true
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (!(activeNetworkInfo == null || !activeNetworkInfo.isConnected)) {
                return true
            }
        }
        return false
    }

    fun checkURL(url: String): Boolean{
        var res = true
        arrayListOf("flycricket","https://localhost/","wixsite").map { item-> if (item in url) res = false}
        return res
    }
}