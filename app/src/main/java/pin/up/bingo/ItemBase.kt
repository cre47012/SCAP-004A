package pin.up.bingo

import android.content.Context

class ItemBase {
    var item = Array(7){0}

    fun init(context: Context){
        for (i in 0..7){
            item[i] = context.resources.getIdentifier("item_a$i", "drawable", context.packageName)
        }
    }

    fun isEmpty() = item[0] == 0
}