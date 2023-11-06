package com.drobe.othello

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import kotlinx.serialization.Serializable
import com.drobe.othello.databinding.ActivityMainBinding

@Parcelize
@Serializable

data class Model(var name:String) : Parcelable

class Tile(var bt: Button){
    var amountCapped = 0
    var button = bt
    var valid = false
    var Directions = mutableListOf<IntArray>()
    var color = Color.LTGRAY
}

class Player(val _color:Int){
    var Ai = false
    var color = _color
    var qty = 1
    var dead = false
    var DefeatedBy : Int = -1
}


class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    var model = Model("")
    var grid : Array<Array<Tile?>>? = null
    val players = arrayOf(Player(Color.RED), Player(Color.GREEN), Player(Color.YELLOW), Player(Color.BLUE))
    val directions = listOf<IntArray>(intArrayOf(0,1), intArrayOf(1,1),intArrayOf(1,0), intArrayOf(1,-1), intArrayOf(0,-1), intArrayOf(-1,-1), intArrayOf(-1,0), intArrayOf(-1,1))
    var currentTurn = 0


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        grid  = Array(8){Array(8){Tile(Button(this@MainActivity))}}

        grid?.get(3)?.get(3)?.color = players[0].color
        grid?.get(3)?.get(4)?.color = players[1].color
        grid?.get(4)?.get(3)?.color = players[2].color
        grid?.get(4)?.get(4)?.color = players[3].color

        populateGrid()

        binding.turnIMG.setImageResource(R.drawable.red)
        binding.nb1.text = players[0].qty.toString()
        binding.nb2.text = players[1].qty.toString()
        binding.nb3.text = players[2].qty.toString()
        binding.nb4.text = players[3].qty.toString()
        CheckMoves()
    }

    fun BTVisuals(){
        players[0].Ai = binding.check1.isChecked
        players[1].Ai = binding.check2.isChecked
        players[2].Ai = binding.check3.isChecked
        players[3].Ai = binding.check4.isChecked

        when (currentTurn){
            0->{
                if (players[0].Ai){
                    binding.pass1.text = getString(R.string.AiPlay)

                }
                else{
                    binding.pass1.text = getString(R.string.pass)
                    binding.pass1.setOnClickListener { incrementTurn() }
                }
                binding.pass1.isEnabled = true
                binding.pass2.isEnabled = false
                binding.pass3.isEnabled = false
                binding.pass4.isEnabled = false
            }
            1->{
                if (players[1].Ai){
                    binding.pass2.text = getString(R.string.AiPlay)
                }
                else{
                    binding.pass2.text = getString(R.string.pass)
                    binding.pass1.setOnClickListener { incrementTurn() }
                }
                binding.pass1.isEnabled = false
                binding.pass2.isEnabled = true
                binding.pass3.isEnabled = false
                binding.pass4.isEnabled = false
            }
            2->{
                if (players[2].Ai){
                    binding.pass3.text = getString(R.string.AiPlay)
                }
                else{
                    binding.pass3.text = getString(R.string.pass)
                    binding.pass1.setOnClickListener { incrementTurn() }
                }
                binding.pass1.isEnabled = false
                binding.pass2.isEnabled = false
                binding.pass3.isEnabled = true
                binding.pass4.isEnabled = false
            }
            3->{
                if (players[3].Ai){
                    binding.pass4.text = getString(R.string.AiPlay)
                }
                else{
                    binding.pass4.text = getString(R.string.pass)
                    binding.pass1.setOnClickListener { incrementTurn() }
                }
                binding.pass2.isEnabled = false
                binding.pass3.isEnabled = false
                binding.pass4.isEnabled = true
                binding.pass1.isEnabled = false
            }
        }
    }
    private fun CheckMoves(){

        BTVisuals()
        //Parse through every index if they're gray
        for (i in 0..7){
            for (j in 0..7){
                if (grid?.get(i)?.get(j)?.color == Color.LTGRAY){
                    for (dir in directions){
                        if (i+(dir[0]) > 0 && i+(dir[0]) < grid!!.size && j+(dir[1]) > 0 && j+(dir[1]) < grid!![i].size){
                            if (grid!![i + dir[0]][j+dir[1]]!!.color != Color.LTGRAY && grid!![i + dir[0]][j+dir[1]]!!.color != Color.DKGRAY){
                                grid!![i][j]!!.valid = true;
                            }
                        }
                    }
                    //Loop through each direction
                    for (dir in directions){
                        var ind = 1
                        while (true){
                            grid!![i][j]!!.amountCapped = ind
                            //Are we getting out of the array
                            if (i+(dir[0]*ind) < grid!!.size-1 && j+(dir[1]*ind) < grid!![0].size-1 && i+(dir[0]*ind) > 0 && j+(dir[1]*ind) > 0){
                                if (grid!![i+(dir[0]*ind)][j+(dir[1]*ind)]!!.color == Color.LTGRAY || grid!![i+(dir[0]*ind)][j+(dir[1]*ind)]!!.color == Color.DKGRAY){
                                    break
                                }
                                //Is the player dead
                                if (players[currentTurn].dead){
                                    if (grid!![i + dir[0]][j+dir[1]]!!.color == players[players[currentTurn].DefeatedBy].color && ind == 1){
                                        grid!![i][j]!!.valid = false;
                                        break;
                                    }
                                    //if the next tile in that direction is the same color as the player's
                                    if (grid?.get(i+(dir[0]*ind))?.get(j+(dir[1] * ind))?.color == players[players[currentTurn].DefeatedBy].color && grid!![i][j]!!.valid&& ind >1){
                                        //stop the loop and save the direction
                                        grid?.get(i)?.get(j)?.Directions!! += dir
                                        break;
                                    }
                                }
                                else{
                                    //if the next tile in that direction is the same color as the player's
                                    if (grid?.get(i+(dir[0]*ind))?.get(j+(dir[1] * ind))?.color == players[currentTurn].color && grid!![i][j]!!.valid && ind >1){
                                        //stop the loop and save the direction
                                        grid!![i][j]!!.Directions!! += dir
                                        break;
                                    }
                                }
                                //if not, increment the current index and go again
                                ind++
                            }
                            else{
                                break
                            }

                        }
                    }
                }

                if (grid!![i][j]!!.Directions.size > 0 ){
                    grid!![i][j]!!.color = Color.DKGRAY
                    grid!![i][j]!!.button.background.setTint(grid!![i][j]!!.color)
                    grid!![i][j]!!.button.setOnClickListener { ApplyMove(i, j) }
                }
                else{
                    grid!![i][j]!!.button.setOnClickListener { null }
                    grid!![i][j]!!.button.background.setTint(grid!![i][j]!!.color)
                }
            }
        }


        if (players[currentTurn].Ai){
            val possibleMoves = mutableListOf<IntArray>()
            for (i in 0 until 7){
                for (j in 0 until 7){
                    possibleMoves += intArrayOf(i,j,grid!![i][j]!!.Directions.size)
                }
            }
            if(possibleMoves.size > 0){
                possibleMoves.sortByDescending { it[2] }
                Log.v("DEBUG", possibleMoves[0][2].toString())
                ApplyMove(possibleMoves[0][0], possibleMoves[0][1])
            }

        }

    }

    fun ResetGrays(){
        for (i in 0..7){
            for (j in 0..7){
                grid!![i][j]!!.button.setOnClickListener { null }
                if (grid!![i][j]!!.color == Color.DKGRAY){
                    grid!![i][j]!!.color = Color.LTGRAY
                    grid!![i][j]!!.button.background.setTint(Color.LTGRAY)
                }

                grid!![i][j]!!.Directions.clear()
            }
        }
    }

    private fun ApplyMove(i: Int, j: Int){

        grid!![i][j]!!.amountCapped = 0
        grid!![i][j]!!.color = players[currentTurn].color
        grid!![i][j]!!.button.background.setTint(players[currentTurn].color)
        for(dir in grid!![i][j]!!.Directions){
            var ind = 1
            while (true){
                if (players[currentTurn].dead){
                    if (grid!![i+(dir[0]*ind)][j+(dir[1] * ind)]!!.color != players[players[currentTurn].DefeatedBy].color){
                        grid!![i+(dir[0]*ind)][j+(dir[1] * ind)]!!.color = players[currentTurn].color
                        grid!![i+(dir[0]*ind)][j+(dir[1] * ind)]!!.button.background.setTint(players[currentTurn].color)
                    }
                    else{
                        grid!![i+(dir[0]*ind)][j+(dir[1]*ind)]!!.color = players[currentTurn].color
                        grid!![i+(dir[0]*ind)][j+(dir[1]*ind)]!!.button.background.setTint(players[currentTurn].color)
                        break
                    }
                }
                else{
                    if (grid!![i+(dir[0]*ind)][j+(dir[1] * ind)]!!.color != players[currentTurn].color){
                        grid!![i+(dir[0]*ind)][j+(dir[1]*ind)]!!.color = players[currentTurn].color
                        grid!![i+(dir[0]*ind)][j+(dir[1]*ind)]!!.button.background.setTint(players[currentTurn].color)
                    }
                    else{
                        break
                    }
                }
                ind++
            }
        }

        ResetGrays()

        //Check if you killed a player
        var ind = 0
        for (p in players){
            p.qty = 0
            for (i in 0..7){
                for (j in 0..7){
                    if (grid!![i][j]!!.color == p.color){
                        p.qty++
                    }
                }
            }
            if (p.qty == 0){
                p.dead = true
                p.DefeatedBy = currentTurn
            }
            else{
                p.dead = false
                p.DefeatedBy = -1
            }
        }
        incrementTurn()
    }
    fun incrementTurn(){
        if (currentTurn == 3){
            currentTurn = 0
        }
        else {
            currentTurn++

        }
        binding.turnIMG.setColorFilter(players[currentTurn].color)
        ResetGrays()
        CheckMoves()
    }


    private fun populateGrid(){
        grid?.let { tiles->
            for (i in tiles){
                for (j in i){
                    var button = j?.bt
                    button?.layoutParams = ViewGroup.LayoutParams(100,100)
                    button?.background?.setTint(j?.color!!)
                    binding.grid.addView(button)

                }
            }
        }
    }

}