package pin.up.bingo

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity
import com.zeugmasolutions.localehelper.currentLocale
import java.util.*

class DemoActivity : LocaleAwareCompatActivity() {
    private var game : Game = Game()
    private lateinit var btnAudio : ImageView
    private lateinit var btnShare : ImageView
    private lateinit var btnLangUA : ImageView
    private lateinit var btnLangKZ : ImageView
    private lateinit var btnLangRU : ImageView
    private lateinit var txtCash : TextView
    private lateinit var txtBet : TextView
    private lateinit var txtWin : TextView
    private lateinit var btnMore : ImageView
    private lateinit var btnLess : ImageView
    private lateinit var btnStart : ImageView
    private lateinit var btnAuto : ImageView
    private var language = "en"

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
        txtBet = findViewById(R.id.txt_bet_value)
        txtWin = findViewById(R.id.txt_last_value)
        btnMore = findViewById(R.id.btn_plus)
        btnLess = findViewById(R.id.btn_minus)
        btnStart = findViewById(R.id.btn_play)
        btnAuto = findViewById(R.id.btn_auto)

        setClickListeners()
        setDefaultLocale()
    }

    private fun setClickListeners(){
        btnLangUA.setOnClickListener {updateLanguage("ua")}
        btnLangKZ.setOnClickListener {updateLanguage("kz")}
        btnLangRU.setOnClickListener {updateLanguage("ru")}
        btnMore.setOnClickListener {game.moreBet()}
        btnLess.setOnClickListener {game.lessBet()}
        btnStart.setOnClickListener {game.spinOne()}
        btnAuto.setOnClickListener {game.spinCircle()}
    }

    private fun updateLanguage(){
        if (language in arrayOf("ua", "kz", "ru") && Locale(language) != currentLocale){
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
}