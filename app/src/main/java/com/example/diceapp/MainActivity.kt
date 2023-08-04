package com.example.diceapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.diceapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val PREFS_NAME = "myPrefs"
    private val CUSTOM_VALUES_KEY = "customValues"
    private val SELECTED_RADIO_KEY = "selectedRadio"
    private val SCORES_KEY = "scores"
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
        val customValues = preferences.getString(CUSTOM_VALUES_KEY, "") ?: ""
        val selectedRadio = preferences.getInt(SELECTED_RADIO_KEY, R.id.radio6)
        val scores = preferences.getString(SCORES_KEY, "Result: ")
        binding.radioGroup.check(selectedRadio)
        binding.tvResult.text = scores
        binding.customSidesEditText.setText(customValues)

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            binding.customSidesEditText.isEnabled = checkedId == R.id.radioCustom
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
            R.id.radioCustom -> binding.customSidesEditText.text.toString().toIntOrNull() ?: 0
            else -> 0
        }
        val result = "Result: ${roll(maxValue)}"
        binding.tvResult.text = result
        val preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        preferences.edit().putString(SCORES_KEY, result).apply()
    }

    override fun onPause() {
        super.onPause()
        val preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val customValues = binding.customSidesEditText.text.toString()
        val selectedRadio = binding.radioGroup.checkedRadioButtonId

        preferences.edit().apply {
            putString(CUSTOM_VALUES_KEY, customValues)
            putInt(SELECTED_RADIO_KEY, selectedRadio)
            apply()
        }
    }

}