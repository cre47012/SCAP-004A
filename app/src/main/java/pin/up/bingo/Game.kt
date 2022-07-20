package pin.up.bingo

import kotlin.random.Random

class Game {
    var credits : Int = 10000
    var bet : Int = 10
    var item = Array(9){0}

    init{
        for (i in 0..9){
            item[i] = Random.nextInt(7)
        }
    }

    fun spinOne(){

    }

    fun spinCircle(){

    }

    fun moreBet(){
        
    }
    
    fun lessBet(){
        
    }
}