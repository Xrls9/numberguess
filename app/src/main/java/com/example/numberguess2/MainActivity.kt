package com.example.numberguess2

import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.provider.SyncStateContract.*
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.numberguess2.R.string
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import com.example.numberguess2.statistics.Statistics




class MainActivity : AppCompatActivity() {
    var started = false
    var number = 0
    var tries = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
//      fetchSavedInstanceData(savedInstanceState)
        doGuess.setEnabled(started)
    }


    override
    fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable("statistics.data",
                Statistics.data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId)
        {
            R.id.statistics ->
            {
                openStatistics()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override
    fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_options, menu)
        return true
    }
    private fun openStatistics() {
        val intent: Intent = Intent(this,
                StatisticsActivity::class.java)
        startActivity(intent)
    }
    fun start(v: View) {
        log("Game started")
        num.setText("")
        started = true
        doGuess.setEnabled(true)
        status.text = getString(string.guess_hint,
            Constants.LOWER_BOUND,
            Constants.UPPER_BOUND)
        val span = Constants.UPPER_BOUND -
                Constants.LOWER_BOUND + 1
        number = Constants.LOWER_BOUND +
                Math.floor(Math.random()*span).toInt()
        tries = 0


    }

    fun guess(v:View) {
        if(num.text.toString() == "") return
        tries++
        log("Guessed ${num.text} (tries:${tries})")
        val g = num.text.toString().toInt()
        if(g < number) {
            status.setText(R.string.status_too_low)
            num.setText("")
        } else if(g > number){
            status.setText(R.string.status_too_high)
            num.setText("")
        } else {
            Statistics.register(number, tries)
            status.text = getString(R.string.status_hit,
                tries)
            started = false
            doGuess.setEnabled(false)
        }
    }
    private fun putInstanceData(outState: Bundle?) {
        if (outState != null) with(outState) {
            putBoolean("started", started)
            putInt("number", number)
            putInt("tries", tries)
            putString("statusMsg", status.text.toString())
            putStringArrayList("logs",
                ArrayList(console.text.split("\n")))
        }
    }

    private fun fetchSavedInstanceData(
        savedInstanceState: Bundle?) {
        if (savedInstanceState != null)
            with(savedInstanceState) {
                started = getBoolean("started")
                number = getInt("number")
                tries = getInt("tries")
                status.text = getString("statusMsg")
                console.text = getStringArrayList("logs")!!.
                joinToString("\n")
            }
    }

    private fun log(msg:String) {
        Log.d("LOG", msg)
        console.log(msg)
    }

}
class Console(ctx:Context, aset:AttributeSet? = null)
    : ScrollView(ctx, aset) {
    companion object {
        val BACKGROUND_COLOR = 0x40FFFF00
        val MAX_LINES = 100
    }
    val tv = TextView(ctx)
    var text:String
        get() = tv.text.toString()
        set(value) { tv.setText(value) }
    init {
        setBackgroundColor(BACKGROUND_COLOR)
        addView(tv)
    }
    fun log(msg:String) {
        val l = tv.text.let {
            if(it == "") listOf() else it.split("\n") }.
        takeLast(MAX_LINES) + msg
        tv.text = l.joinToString("\n")
        post(object : Runnable {
            override fun run() {
                fullScroll(ScrollView.FOCUS_DOWN)
            }
        })
    }

    class GameUser(val firstName:String,
                   val lastName:String,
                   val userName:String,
                   val registrationNumber:Int,
                   val birthday:String = "",
                   val userRank:Double = 0.0) {
        val fullName:String
        val initials:String
        init {
            fullName = firstName + " " + lastName
            initials = firstName.toUpperCase() +
                    lastName.toUpperCase()
        }
    }
    // somewhere inside a function in class MainActivity
    val user = GameUser("Peter", "Smith", "psmith",
        123, "1988-10-03", 0.79)


}