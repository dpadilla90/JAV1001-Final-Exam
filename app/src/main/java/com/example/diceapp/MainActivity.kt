package com.example.diceapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.diceapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val PREFS_NAME = "myPrefs"
    private val RADIO_SELECTED_KEY = "radioSelected"
    private val SPINNER_SELECTED_KEY = "spinnerSelected"
    private val RESULT_TEXT_KEY = "resultText"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Setup the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Dice Rolling App"

        val preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val radioSelected = preferences.getInt(RADIO_SELECTED_KEY, R.id.radio4)
        binding.radioGroup.check(radioSelected)
        binding.spinner.setSelection(preferences.getInt(SPINNER_SELECTED_KEY, 0))
        binding.tvResult.text = preferences.getString(RESULT_TEXT_KEY, "")

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            binding.spinner.isEnabled = checkedId == R.id.radioCustom
        }

        binding.btnRoll.setOnClickListener {
            rollDice { maxValue -> ((Math.random() * maxValue).toInt() + 1).toString() }
        }

        binding.btnRollTwice.setOnClickListener {
            rollDice { maxValue ->
                val randomVal1 = (Math.random() * maxValue).toInt() + 1
                val randomVal2 = (Math.random() * maxValue).toInt() + 1
                "$randomVal1, $randomVal2"
            }
        }
    }

    private fun rollDice(roll: (Int) -> String) {
        val selectedId = binding.radioGroup.checkedRadioButtonId
        val maxValue = when (selectedId) {
            R.id.radio4 -> 4
            R.id.radio6 -> 6
            R.id.radio8 -> 8
            R.id.radio10 -> 10
            R.id.radio12 -> 12
            R.id.radio20 -> 20
            R.id.radioCustom -> binding.spinner.selectedItem.toString().toInt()
            else -> 0
        }
        binding.tvResult.text = "Result: ${roll(maxValue)}"
    }

    override fun onPause() {
        super.onPause()
        val preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        preferences.edit()
            .putInt(RADIO_SELECTED_KEY, binding.radioGroup.checkedRadioButtonId)
            .putInt(SPINNER_SELECTED_KEY, binding.spinner.selectedItemPosition)
            .putString(RESULT_TEXT_KEY, binding.tvResult.text.toString())
            .apply()
    }
}
