package com.example.travelcompanionapp

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var editTextValue: EditText
    private lateinit var buttonConvert: Button
    private lateinit var textViewResult: TextView

    private val currencyUnits = listOf("USD", "AUD", "EUR", "JPY", "GBP")
    private val fuelDistanceUnits = listOf("MPG", "KM/L", "Gallon (US)", "Litre", "Nautical Mile", "Kilometre")
    private val temperatureUnits = listOf("Celsius", "Fahrenheit", "Kelvin")
    private val categories = listOf("Currency", "Fuel & Distance", "Temperature")

    // Currency rates relative to USD
    private val currencyToUsd = mapOf(
        "USD" to 1.0,
        "AUD" to 1.55,
        "EUR" to 0.92,
        "JPY" to 148.50,
        "GBP" to 0.78
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerCategory = findViewById(R.id.spinnerCategory)
        spinnerFrom = findViewById(R.id.spinnerFrom)
        spinnerTo = findViewById(R.id.spinnerTo)
        editTextValue = findViewById(R.id.editTextValue)
        buttonConvert = findViewById(R.id.buttonConvert)
        textViewResult = findViewById(R.id.textViewResult)

        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateUnitSpinners(categories[position])
                textViewResult.text = ""
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        buttonConvert.setOnClickListener {
            performConversion()
        }
    }

    private fun updateUnitSpinners(category: String) {
        val units = when (category) {
            "Currency" -> currencyUnits
            "Fuel & Distance" -> fuelDistanceUnits
            "Temperature" -> temperatureUnits
            else -> emptyList()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter

        if (units.size > 1) {
            spinnerTo.setSelection(1)
        }
    }

    private fun performConversion() {
        val inputText = editTextValue.text.toString().trim()

        if (inputText.isEmpty()) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            return
        }

        val value = inputText.toDoubleOrNull()
        if (value == null) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            return
        }

        val from = spinnerFrom.selectedItem?.toString() ?: return
        val to = spinnerTo.selectedItem?.toString() ?: return

        if (from == to) {
            Toast.makeText(this, "Please select different units", Toast.LENGTH_SHORT).show()
            textViewResult.text = String.format("%.4f %s", value, to)
            return
        }

        val category = spinnerCategory.selectedItem?.toString() ?: return

        if (category == "Fuel & Distance" && value < 0) {
            Toast.makeText(this, "Fuel and distance values cannot be negative", Toast.LENGTH_SHORT).show()
            return
        }

        val result = convert(from, to, value)
        textViewResult.text = String.format("%.4f %s", result, to)
    }

    private fun convert(from: String, to: String, value: Double): Double {
        val category = spinnerCategory.selectedItem?.toString() ?: return 0.0

        return when (category) {
            "Currency" -> convertCurrency(from, to, value)
            "Fuel & Distance" -> convertFuelDistance(from, to, value)
            "Temperature" -> convertTemperature(from, to, value)
            else -> 0.0
        }
    }

    private fun convertCurrency(from: String, to: String, value: Double): Double {
        val fromRate = currencyToUsd[from] ?: return 0.0
        val toRate = currencyToUsd[to] ?: return 0.0
        val usdValue = value / fromRate
        return usdValue * toRate
    }

    private fun convertFuelDistance(from: String, to: String, value: Double): Double {
        // Convert everything to a base unit first, then to target
        // Efficiency base: MPG
        // Volume base: Gallon (US)
        // Distance base: Nautical Mile

        // Group 1: Fuel efficiency (MPG, KM/L)
        // Group 2: Volume (Gallon (US), Litre)
        // Group 3: Distance (Nautical Mile, Kilometre)

        val efficiencyUnits = listOf("MPG", "KM/L")
        val volumeUnits = listOf("Gallon (US)", "Litre")
        val distanceUnits = listOf("Nautical Mile", "Kilometre")

        return when {
            from in efficiencyUnits && to in efficiencyUnits -> {
                convertEfficiency(from, to, value)
            }
            from in volumeUnits && to in volumeUnits -> {
                convertVolume(from, to, value)
            }
            from in distanceUnits && to in distanceUnits -> {
                convertDistance(from, to, value)
            }
            // Cross-group conversions
            else -> {
                // Convert to common intermediate values
                val baseValue = toFuelBase(from, value)
                fromFuelBase(to, baseValue, from)
            }
        }
    }

    private fun convertEfficiency(from: String, to: String, value: Double): Double {
        // 1 MPG = 0.425 KM/L
        return when {
            from == "MPG" && to == "KM/L" -> value * 0.425
            from == "KM/L" && to == "MPG" -> value / 0.425
            else -> value
        }
    }

    private fun convertVolume(from: String, to: String, value: Double): Double {
        // 1 Gallon = 3.785 Litres
        return when {
            from == "Gallon (US)" && to == "Litre" -> value * 3.785
            from == "Litre" && to == "Gallon (US)" -> value / 3.785
            else -> value
        }
    }

    private fun convertDistance(from: String, to: String, value: Double): Double {
        // 1 Nautical Mile = 1.852 Kilometres
        return when {
            from == "Nautical Mile" && to == "Kilometre" -> value * 1.852
            from == "Kilometre" && to == "Nautical Mile" -> value / 1.852
            else -> value
        }
    }

    private fun toFuelBase(unit: String, value: Double): Double {
        return when (unit) {
            "MPG" -> value                          // base efficiency
            "KM/L" -> value / 0.425                 // to MPG
            "Gallon (US)" -> value                   // base volume
            "Litre" -> value / 3.785                 // to Gallons
            "Nautical Mile" -> value                 // base distance
            "Kilometre" -> value / 1.852             // to Nautical Miles
            else -> value
        }
    }

    private fun fromFuelBase(unit: String, baseValue: Double, fromUnit: String): Double {
        // For cross-group, we just do direct base-to-target
        return when (unit) {
            "MPG" -> baseValue
            "KM/L" -> baseValue * 0.425
            "Gallon (US)" -> baseValue
            "Litre" -> baseValue * 3.785
            "Nautical Mile" -> baseValue
            "Kilometre" -> baseValue * 1.852
            else -> baseValue
        }
    }

    private fun convertTemperature(from: String, to: String, value: Double): Double {
        return when {
            // Celsius conversions
            from == "Celsius" && to == "Fahrenheit" -> (value * 1.8) + 32
            from == "Celsius" && to == "Kelvin" -> value + 273.15

            // Fahrenheit conversions
            from == "Fahrenheit" && to == "Celsius" -> (value - 32) / 1.8
            from == "Fahrenheit" && to == "Kelvin" -> ((value - 32) / 1.8) + 273.15

            // Kelvin conversions
            from == "Kelvin" && to == "Celsius" -> value - 273.15
            from == "Kelvin" && to == "Fahrenheit" -> ((value - 273.15) * 1.8) + 32

            else -> value
        }
    }
}