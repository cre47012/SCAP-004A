package pin.up.bingo

import android.content.Context
import android.media.MediaPlayer

class Effects (var context: Context) {
    private var mediaWin : MediaPlayer = MediaPlayer.create(context, R.raw.win)
    private var mediaStart : MediaPlayer = MediaPlayer.create(context, R.raw.start)
    private var mediaEnd : MediaPlayer = MediaPlayer.create(context, R.raw.tip)

    fun playWin(){
        mediaWin.seekTo(0)
        mediaWin.start()
    }

    fun playStart(){
        mediaStart.seekTo(0)
        mediaStart.start()
    }

    fun playEnd(){
        mediaEnd.seekTo(0)
        mediaEnd.start()
    }
}