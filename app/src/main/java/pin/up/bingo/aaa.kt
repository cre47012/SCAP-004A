package pin.up.bingo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@SuppressLint("CustomSplashScreen")
class aaa : AppCompatActivity() {
    private lateinit var activity : Activity
    private lateinit var context : Context
    private var worker : Job? = null
    private lateinit var loaderImage : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.easyFlow.logActivity(this)
        setContentView(R.layout.activity_splash)
        activity = this
        context = applicationContext
        loaderImage = findViewById(R.id.imageViewLoader)
        logic()
        reloadAnimate()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun logic() {
        worker = GlobalScope.launch {
            App.noInternetConnection = false
            if (App.easyFlow.checkInternetConnection(context)) {
                if (App.state == 120) {
                    initServices()
                    App.state = 767
                }
                if (App.state == 767) {
                    val data: DataPref? = App.store.getData()
                    if (data == null) {
                        App.state = 248
                    } else {
                        App.data = data
                        App.state = if (data.user == null || data.user == false) 417 else 719
                        if (App.state == 417) {
                            App.easyFlow.disableOneSignal()
                        }
                    }
                }
                if (App.state == 248) {
                    if (getAllServices()) {
                        App.state = 641
                    } else {
                        gotoRestart()
                    }
                }
                if (App.state == 641) {
                    var res = getServerRequest(null)
                    if (res != null) {
                        var jRes = JSONObject(res)
                        var user : Boolean? = null
                        var visitorID : String? = null
                        try {
                            user = jRes.getBoolean("user") //todo check null user
                            visitorID = jRes.getString("visitor_id")
                        }catch (e : Exception){ }


                        if (user != null && user && visitorID != null) {
                            res = getServerRequest(visitorID)
                            if (res != null) {
                                val link = getLink(res!!)
                                saveData(true, link, visitorID)
                                App.firstLaunch = true
                                App.state = 719
                            } else gotoRestart()
                        } else {
                            App.easyFlow.logEvent("user_false", context)
                            App.data.user = false
                            App.store.setData(App.data)
                            App.easyFlow.disableOneSignal()
                            App.state = 417
                        }
                    } else gotoRestart()
                }
                if (App.state == 417) {
                    val intent = Intent(activity, aab::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent)
                    activity.overridePendingTransition(0,0)
                    activity.finish()
                }
                if (App.state == 719) {
                    val intent = Intent(activity, aac::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent)
                    activity.overridePendingTransition(0,0)
                    activity.finish()
                }
            } else {
                gotoRestart()
            }
        }
    }

    private fun gotoRestart(){
        App.noInternetConnection = true
        val intent = Intent(activity, aac::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent)
        activity.overridePendingTransition(0,0)
        activity.finish()
    }


    private suspend fun getAllServices() : Boolean{
        var res = true
            GlobalScope.launch {
                val advertisingData: Deferred<String?> = async { App.easyFlow.getUIDAdvertising(context) }
                val appsflyerData: Deferred<String?> = async { App.easyFlow.getUIDAppsFlyer(activity) }
                val advertisingID = advertisingData.await()
                val appsflyerID = appsflyerData.await()
                if (advertisingID == null || advertisingID == "")
                    res = false
                if (appsflyerID == null || appsflyerID == "")
                    res = false
                if (res) {
                    App.advertisingID = advertisingID
                    App.appsflyerID = appsflyerID
                }
            }.join()
        return res
    }

    private fun getServerRequest(visitorID : String?) : String? {
        var res: String? = null
        val site = "https://tb-int-site.pp.ua/"
        val json =
            if (visitorID == null)
                """{"bundle_id":"${getBundleID()}","advertising_id":"${App.advertisingID}","appsflyer_device_id":"${App.appsflyerID}"}"""
            else """{"bundle_id":"${getBundleID()}","visitor_id":"$visitorID"}"""
        val finalLink = "${site}api/user/${if (visitorID == null) "check" else "data"}/v2/"
        val  client = OkHttpClient()
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(finalLink)
            .post(body)
            .build()


        client.newCall(request).execute().use { response ->
            res = response.body!!.string()
        }

        return res
    }

    private fun getBundleID() : String{
        return BuildConfig.APPLICATION_ID
    }

    private fun initServices(){
        App.easyFlow.createAppsFlyer(this)
        App.easyFlow.createOneSignal(this)
        App.store = DataManager(this)
    }

    private fun getLink(json : String) : String? {
        var jObject = JSONObject(json.substring(1, json.length - 1))
        return jObject.getString("product_url")
    }

    private suspend fun saveData(user : Boolean?, url: String?, visitor: String?){
        var data = DataPref()
        data.user = user ?: false
        data.link = url
        data.visitor = visitor
        App.data = data
        App.store.setData(data)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (worker != null && worker!!.isActive){
            worker!!.cancel()
        }
    }

    private fun reloadAnimate(){
        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.rotation360infinite)
        loaderImage.startAnimation(animation)
    }
}