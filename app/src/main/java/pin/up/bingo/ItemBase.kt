package pin.up.bingo

import android.content.Context

class ItemBase (context: Context) {
    var item = Array(7){0}

    init{
        for (i in 0 until 7){
            item[i] = context.resources.getIdentifier("kx$i", "drawable", context.packageName)
        }
    }
}