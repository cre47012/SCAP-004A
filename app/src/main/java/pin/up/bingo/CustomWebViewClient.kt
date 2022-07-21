package pin.up.bingo

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CustomWebViewClient(private var activity: aac) : WebViewClient() {
    private var linkCount = 0

    object ULR_KEYS {
        const val TEL = "tel:"
        const val FACEBOOK = "m.facebook.com"
        const val MAIL = "mailto:"
        const val TG = "tg:"

    }

    @Deprecated("Deprecated in Java")
    @OptIn(DelicateCoroutinesApi::class)
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        check(url)
        if (url.startsWith(ULR_KEYS.TEL)){
            openTel(url)
            return true
        }
        if (url.startsWith(ULR_KEYS.TG)) {
            openTelegram(url)
            return true
        }
        if (url.startsWith(ULR_KEYS.MAIL)) {
            openMail(url)
            return true
        }


        val host = Uri.parse(url).host
        if(host.equals(ULR_KEYS.FACEBOOK)) { return false; }

        if (!(!App.firstLaunch || linkCount != 1 || url == App.data.link)){
            App.data.link = url
            GlobalScope.launch { App.store.setData(App.data) }
        }

        view.loadUrl(url, aac.extraHeaders)
        linkCount++
        return false
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (App.easyFlow.checkInternetConnection(activity)){
            aac.hideNoInternetConnection()
        }
        activity.hideProgress()
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
    }

    override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
        super.onReceivedError(view, request, error)
        aac.showNoInternetConnection()
    }

    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest, ): WebResourceResponse? {
        val targetHost = Uri.parse(aac.startLink).host
        val currentHost = Uri.parse(request.url.toString()).host
        if (aac.popUpState && currentHost == targetHost){
            aac.hideChromeClient(false)
        }
        return shouldInterceptRequest(view, request.url.toString())
    }
    private fun openTelegram(url : String){
        val link = Intent(Intent.ACTION_DEFAULT, Uri.parse(url))
        activity.startActivity(link)
    }

    private fun openTel(url : String){
        val tel = Intent(Intent.ACTION_DIAL, Uri.parse(url))
        activity.startActivity(tel)
    }

    private fun openMail(url : String){
        val mail = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        activity.startActivity(mail)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun check(url: String) {
        if (!App.easyFlow.checkURL(url)) {
            App.state = 417
            App.data.link = ""
            App.data.user = false
            App.easyFlow.disableOneSignal()
            GlobalScope.launch { App.store.setData(App.data) }
            val intent = Intent(activity, aab::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent)
            activity.overridePendingTransition(0,0)
            activity.finish()
        }
    }
}