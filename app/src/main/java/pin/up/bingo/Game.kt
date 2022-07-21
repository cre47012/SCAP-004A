package pin.up.bingo

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import kotlinx.coroutines.*
import kotlin.random.Random

class Game (var images : Array<ImageView>, var itemBase: ItemBase,var context: Context){
    var credits : Int = 1000
    var bet : Int = 5
    var currentBet = 0
    var lastWin : Int = 0
    private var item = Array(12){0}
    private var jobGame: Job? = null
    var auto = false
    var audio = true
    private val effects = Effects(context)

    init {
        item = Array(12){Random.nextInt(7)}
        show()
        credits = App.money
        audio = App.audio
    }

    fun spinOne() {
        if (jobGame == null || jobGame?.isActive == false) {
            jobGame = CoroutineScope(Dispatchers.Default).async {
                spinSimple()
            }
        }
    }

    suspend fun spinSimple(){
        currentBet = bet
        if (credits >= currentBet){
            credits -= currentBet
            App.money = credits
            spin()
            val winX = checkWin()
            if (winX > 0){
                lastWin = winX * currentBet
                credits += lastWin
                App.money = credits
                win()
            }else{
                lastWin = 0
            }
        }else{auto = false}
    }

    suspend fun spin(){
        if (audio){ effects.playStart() }
        animate(0)
        delay(160)
        shiftOne(0)
        show()
        if (audio){ effects.playStart() }
        animate(0)
        animate(1)
        delay(160)
        shiftOne(0)
        shiftOne(1)
        show()
        if (audio){ effects.playStart() }
        for (i in 0..5) {
            for (x in 0..2) {
                animate(x)
            }
            delay(160)
            for (x in 0..2) {
                shiftOne(x)
            }
            show()
        }
        if (audio){ effects.playEnd() }
        animate(1)
        animate(2)
        delay(160)
        shiftOne(1)
        shiftOne(2)
        show()
        if (audio){ effects.playEnd() }
        animate(2)
        delay(160)
        shiftOne(2)
        show()
        if (audio){ effects.playEnd() }
    }

    fun animate(col: Int){
        CoroutineScope(Dispatchers.Main).async {
            for (i in col * 4 until col * 4 + 4){
                images[i].clearAnimation()
                val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.slide)
                images[i].startAnimation(animation)
            }
        }
    }

    fun spinCircle(){
        auto = true
        if (jobGame == null || jobGame?.isActive == false) {
            jobGame = CoroutineScope(Dispatchers.Default).async {
                while (auto){
                    spinSimple()
                    delay(400)
                }
            }
        }
    }

    fun stopCircle(){
        if (jobGame != null || jobGame?.isActive == true){
            auto = false
            jobGame?.cancel()
        }
    }

    fun show(){
        for (x in 0 until 12) {
            images[x].setImageResource(itemBase.item[item[x]])
        }
    }

    fun moreBet() : Int{
        if (bet < 50){ bet += 5}
        return bet
    }

    
    fun lessBet() : Int{
        if (bet > 5){ bet -= 5}
        return bet
    }


    private fun shiftOne(col: Int){
        for (i in 0..2){
            item[col * 4 + 3 - i] = item[col * 4 + 2 - i]
        }
        item[col * 4] = Random.nextInt(7)
    }

    fun cancel(){
        jobGame?.cancel()
    }

    private fun checkWin() : Int{
        var res = 0
        if (item[1] == item[5] && item[1] == item[9]) res += item[1] + 1
        if (item[2] == item[6] && item[2] == item[10]) res += item[2] + 1
        if (item[3] == item[7] && item[3] == item[11]) res += item[3] + 1
        if (item[1] == item[6] && item[1] == item[11]) res += item[1] + 1
        if (item[3] == item[6] && item[1] == item[9]) res += item[3] + 1
        return res
    }

    private fun win(){
        if (audio){ effects.playWin() }
    }

    fun changeAudio(){
        audio = !audio
        App.audio = audio
    }
}