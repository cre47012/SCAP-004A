package pin.up.bingo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity
import com.zeugmasolutions.localehelper.currentLocale
import kotlinx.coroutines.*
import java.util.*


class aab : LocaleAwareCompatActivity() {
    private lateinit var game : Game
    private lateinit var btnAudio : ImageView
    private lateinit var btnShare : ImageView
    private lateinit var btnLangUA : ImageView
    private lateinit var btnLangKZ : ImageView
    private lateinit var btnLangRU : ImageView
    private lateinit var btnMenu : ImageView
    private lateinit var menuLayout : ConstraintLayout
    private lateinit var mainLayout : ConstraintLayout

    private lateinit var txtCash : TextView
    private lateinit var txtBet : TextView
    private lateinit var txtWin : TextView
    private lateinit var btnMore : ImageView
    private lateinit var btnLess : ImageView
    private lateinit var btnStart : ImageView
    private lateinit var btnAuto : ImageView
    private var language = "en"
    private lateinit var images : Array<ImageView>
    private lateinit var itemBase : ItemBase
    private var infoJob: Job? = null
    private var isAuto = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        btnAudio = findViewById(R.id.btn_audio)
        btnShare = findViewById(R.id.btn_share)
        btnLangUA = findViewById(R.id.btn_lang_ua)
        btnLangKZ = findViewById(R.id.btn_lang_kz)
        btnLangRU = findViewById(R.id.btn_lang_ru)
        txtCash = findViewById(R.id.txt_money_value)
        btnMenu = findViewById(R.id.btnMenu)
        txtBet = findViewById(R.id.txt_bet_value)
        txtWin = findViewById(R.id.txt_last_value)
        btnMore = findViewById(R.id.btn_plus)
        btnLess = findViewById(R.id.btn_minus)
        btnStart = findViewById(R.id.btn_play)
        btnAuto = findViewById(R.id.btn_auto)
        menuLayout = findViewById(R.id.menu_frame)
        mainLayout = findViewById(R.id.main_layout)
        images = Array(12){t->findViewById(applicationContext.resources.getIdentifier("x$t", "id",packageName))}
        itemBase = ItemBase(this)
        setClickListeners()
        setDefaultLocale()
        startInfoJob()
        game = Game(images, itemBase, this)
        btnAudio.setImageResource(if (game.audio) R.drawable.btn_audio_on else R.drawable.btn_audio_off)
    }

    private fun setClickListeners(){
        btnLangUA.setOnClickListener {updateLanguage("uk")}
        btnLangKZ.setOnClickListener {updateLanguage("kk")}
        btnLangRU.setOnClickListener {updateLanguage("ru")}
        btnMore.setOnClickListener {game.moreBet()}
        btnLess.setOnClickListener {game.lessBet()}
        btnStart.setOnClickListener {game.spinOne()}
        btnAuto.setOnClickListener {
            if (!isAuto){
                isAuto = true
                game.spinCircle()
                val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.auto)
                btnAuto.clearAnimation()
                btnAuto.startAnimation(animation)
            }else{
                isAuto = false
                game.stopCircle()
                btnAuto.clearAnimation()
            }
        }

        btnMenu.setOnClickListener { menuLayout.visibility = if (menuLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE}
        mainLayout.setOnClickListener { menuLayout.visibility = View.GONE }
        btnAudio.setOnClickListener {
            game.changeAudio()
            btnAudio.setImageResource(if (game.audio) R.drawable.btn_audio_on else R.drawable.btn_audio_off)
        }
        btnShare.setOnClickListener {
            val intent = Intent()
            intent.action =Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT,"Check this app : ${resources.getString(R.string.app_name)}")
            intent.type="text/plain"
            startActivity(Intent.createChooser(intent,"Share To:"))
        }
    }

    private fun updateLanguage(){
        if (language in arrayOf("uk", "kk", "ru") && Locale(language) != currentLocale){
            updateLocale(Locale(language))
        }
    }

    private fun setDefaultLocale(){
        language = Locale.getDefault().language
        updateLanguage()
    }

    private fun updateLanguage(nextLang : String){
        language = nextLang
        updateLanguage()
    }

    private fun startInfoJob() {
        infoJob = CoroutineScope(Dispatchers.Main).async {
            while (true) {
                setInfo()
                delay(20)
            }
        }
    }

    private fun setInfo(){
        txtBet.text = game.bet.toString()
        txtCash.text = game.credits.toString()
        txtWin.text = game.lastWin.toString()
        isAuto = game.auto
    }

    override fun onDestroy() {
        super.onDestroy()
        infoJob?.cancel()
        game.cancel()
    }

    override fun onBackPressed() {
        if (App.state != 417){
            val intent = Intent(this, aaa::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }
    }
}