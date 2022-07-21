package pin.up.bingo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout


class aac : AppCompatActivity() {
    private lateinit var progress : ProgressBar
    private lateinit var demoTextView : TextView
    private lateinit var retryImage : ImageView
    private lateinit var context : Context

    companion object {
        var extraHeaders: HashMap<String, String> = HashMap()
        const val REQUEST_SELECT_FILE = 100
        var reloadAgain = false
        var popUpState = false

        //web
        @SuppressLint("StaticFieldLeak")
        lateinit var chromeClient : CustomChromeClient
        @SuppressLint("StaticFieldLeak")
        lateinit var chromeClientView : WebView
        @SuppressLint("StaticFieldLeak")
        lateinit var webView : WebView
        //
        private var builder : AlertDialog? = null
        var startLink  = ""
        private lateinit var errorLayout : ConstraintLayout
        var uploadMessage: ValueCallback<Array<Uri>>? = null

        fun showNoInternetConnection(){
            errorLayout.visibility = VISIBLE
        }

        fun hideNoInternetConnection(){
            errorLayout.visibility = GONE
        }

        fun hideChromeClient(hardReset: Boolean){
            chromeClient.hideChrome(hardReset)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = applicationContext
        setContentView(R.layout.activity_web)
        extraHeaders["X-Requested-With"] = "app-view"
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        errorLayout = findViewById(R.id.internet_error)
        App.easyFlow.logEvent("open_web", this)
        webView = initWebView()
        App.easyFlow.logActivity(this)
        demoTextView = findViewById(R.id.textViewDemo)
        progress = findViewById(R.id.progressBar)
        retryImage = findViewById(R.id.imageViewRetry)

        initButtonListeners()

        if (App.state == 719){
            webView.loadUrl(App.data.link!!)
        }else{
            showNoInternetConnection()
        }

        if (reloadAgain) {
            reloadAgain = false
            reloadAnimate()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_FILE) {
            if (uploadMessage == null) return
            uploadMessage!!.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            uploadMessage = null;
        }
    }

    override fun onBackPressed() {
        if (popUpState){
            hideChromeClient(true)
            webView.reload()
        }else if (webView.canGoBack()){ webView.goBack()}
    }

    fun initWebView() : WebView{
        var newWebView = findViewById<WebView>(R.id.webView)// webView
        chromeClient = CustomChromeClient(this, context)
        newWebView.webChromeClient = chromeClient
        newWebView.webViewClient = CustomWebViewClient(this)
        newWebView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        setCookies(newWebView)  //Cookies
        setWebSettings(newWebView)
        return newWebView
    }

    private fun setCookies(web: WebView){
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(web,true)
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setWebSettings(web: WebView){ //todo random
        with(web.settings){
            mixedContentMode = 0
            useWideViewPort = true
            setEnableSmoothTransition(true)
            savePassword = true
            allowContentAccess = true
            javaScriptCanOpenWindowsAutomatically = true
            javaScriptEnabled = true
            allowFileAccess = true
            allowFileAccessFromFileURLs = true
            setSupportZoom(false)
            saveFormData = true
            cacheMode = WebSettings.LOAD_DEFAULT
            userAgentString = userAgentString.replaceAfter(")", "")
            loadsImagesAutomatically = true
            setRenderPriority(WebSettings.RenderPriority.HIGH)
            setSupportMultipleWindows(true)
            domStorageEnabled = true
            loadWithOverviewMode = true
            setAppCacheEnabled(true)
            allowUniversalAccessFromFileURLs = true
            databaseEnabled = true
        }
    }

    fun hideProgress(){
        progress.visibility = GONE
    }

    private fun initButtonListeners(){
        demoTextView.setOnClickListener{
            val intent = Intent(this, aab::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }
        errorLayout.setOnClickListener {
            if (App.state != 719){
                val intent = Intent(this, aaa::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent)
                overridePendingTransition(0,0)
                finish()
                reloadAgain = true
            }
            else{
                webView.reload()
                reloadAnimate()
            }
        }
    }

    private fun reloadAnimate(){
        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.rotation360)
        retryImage.startAnimation(animation)
    }




    ///
    class CustomChromeClient (var activity: aac, var context : Context) : WebChromeClient() {
        private var currentView : WebView? = null
        @SuppressLint("SetJavaScriptEnabled")
        override fun onCreateWindow(
            view: WebView?,
            isDialog: Boolean,
            isUserGesture: Boolean,
            resultMsg: Message?,
        ): Boolean {
            //Log.i("LOGXXXXXX", "chrome new ${resultMsg}")
            currentView = WebView(context)
            currentView!!.isVerticalScrollBarEnabled = false
            currentView!!.isHorizontalScrollBarEnabled = false
            currentView!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            with(currentView!!.settings){
                setSupportZoom(false)
                userAgentString = userAgentString.replaceAfter(")", "")
                databaseEnabled = true
                setEnableSmoothTransition(true)
                loadsImagesAutomatically = true
                cacheMode = WebSettings.LOAD_DEFAULT
                allowContentAccess = true
                mixedContentMode = 0
                allowFileAccess = true
                setRenderPriority(WebSettings.RenderPriority.HIGH)
                savePassword = true
                setAppCacheEnabled(true)
                domStorageEnabled = true
                loadWithOverviewMode = true
                saveFormData = true
                allowUniversalAccessFromFileURLs = true
                javaScriptEnabled = true
                useWideViewPort = true
                allowFileAccessFromFileURLs = true
            }

            //builder
            builder = AlertDialog.Builder(activity).create()
            builder!!.setTitle(" ")
            builder!!.setView(currentView)
            builder!!.setButton(context.getString(R.string.ccas)) { dialog, id ->
                hideChrome(true)
            }
            builder!!.show()
            builder!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            builder!!.setOnCancelListener {
                hideChrome(true)
            }

            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setAcceptThirdPartyCookies(currentView, true)
            cookieManager.setAcceptThirdPartyCookies(view, true)

            currentView!!.webChromeClient = CustomChromeClient(activity, context)

            val transport = resultMsg!!.obj as WebView.WebViewTransport
            transport.webView = currentView
            resultMsg.sendToTarget()

            currentView!!.webViewClient = object : WebViewClient(){
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    popUpState = true
                    if (startLink == ""){
                        startLink = url!!
                    }
                    if (url!!.startsWith(CustomWebViewClient.ULR_KEYS.TG)) {
                        openTelegram(url)
                        return true
                    }
                    return super.shouldOverrideUrlLoading(view, url)
                }

                private fun openTelegram(url : String){
                    val link = Intent(Intent.ACTION_DEFAULT, Uri.parse(url))
                    activity.startActivity(link)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    popUpState = true
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                }
            }
            currentView!!.requestFocus()
            chromeClientView = currentView!!
            return true
        }

        override fun onCloseWindow(window: WebView?) {
            super.onCloseWindow(window)
            hideChrome(false)
        }

        override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: FileChooserParams): Boolean {
            if (uploadMessage != null) {
                uploadMessage!!.onReceiveValue(null)
                uploadMessage = null
            }
            uploadMessage = filePathCallback
            val intent = fileChooserParams.createIntent()
            try {
                activity.startActivityForResult(intent, REQUEST_SELECT_FILE)
            } catch (e: ActivityNotFoundException) {
                uploadMessage = null
                return false
            }
            return true
        }

        fun hideChrome(hardReset : Boolean) {
            activity.runOnUiThread {
                if (currentView != null) {
                    try {
                        currentView!!.destroy()
                    } catch (e: Error) {
                    }
                }
                if (builder != null) {
                    try {
                        builder!!.dismiss()
                    } catch (e: Error) {
                    }
                }
                if (hardReset && popUpState) webView.reload()
                startLink = ""
                popUpState = false
            }
        }
    }
}