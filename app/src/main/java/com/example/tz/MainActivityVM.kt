package com.example.tz

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.File

class MainActivityVM : ViewModel() {

    //region values
    private val _brandView = MutableLiveData("")
    val brandView: LiveData<String> = _brandView

    private val _coordView = MutableLiveData("")
    val coordView: LiveData<String> = _coordView

    private val _countryView = MutableLiveData("")
    val countryView: LiveData<String> = _countryView

    private val _lengthView = MutableLiveData("")
    val lengthView: LiveData<String> = _lengthView

    private val _luhnView = MutableLiveData("")
    val luhnView: LiveData<String> = _luhnView

    private val _siteView = MutableLiveData("")
    val siteView: LiveData<String> = _siteView

    private val _namecityView = MutableLiveData("")
    val namecityView: LiveData<String> = _namecityView

    private val _phoneView = MutableLiveData("")
    val phoneView: LiveData<String> = _phoneView

    private val _prepaidView = MutableLiveData("")
    val prepaidView: LiveData<String> = _prepaidView

    private val _schemeView = MutableLiveData("")
    val schemeView: LiveData<String> = _schemeView

    private val _typeView = MutableLiveData("")
    val typeView: LiveData<String> = _typeView

    val BIN = MutableLiveData("")

    var longitude: String? = ""
    var latitude: String? = ""

    val storageFileName = "quarries.txt"
    var BINHistory: MutableLiveData<MutableList<String>>

    //endregion

    init {
        if (File(MyApplication.appContext?.filesDir, storageFileName).exists())
            BINHistory = MutableLiveData(readQuarriesFromFile(storageFileName).toMutableList())
        else {
            File(MyApplication.appContext?.filesDir, storageFileName).createNewFile()
            BINHistory = MutableLiveData()
        }
    }

    private fun requestBINData(BIN: String) {
        val url = "https://lookup.binlist.net/$BIN"

        val queue = Volley.newRequestQueue(MyApplication.appContext);
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                Log.d("MyLog", "BIN:$BIN Response: $response")
                writeData(response)
                writeBINToStorage(BIN, storageFileName)
            },
            {
                Log.d("MyLog", "BIN:$BIN Volley error: $it")
                Toast.makeText(MyApplication.appContext, "Данные не найдены", Toast.LENGTH_SHORT)
                    .show()
            }
        )
        queue.add(stringRequest)
    }


    private fun writeData(response: String) {
        val bankData = parseResponse(response)
        latitude = bankData.latitude
        longitude = bankData.longitude
        _brandView.value = bankData.brand
        _coordView.value = "(latitude:${bankData.latitude}, longitude: ${bankData.longitude}"
        _countryView.value = "${bankData.alpha2} ${bankData.countryName}"
        _lengthView.value = bankData.length
        _luhnView.value = bankData.luhn
        _siteView.value = bankData.url
        _namecityView.value = bankData.city
        _phoneView.value = bankData.phone
        _prepaidView.value = bankData.prepaid
        _schemeView.value = bankData.scheme
        _typeView.value = bankData.type
    }

    private fun readQuarriesFromFile(filename: String): List<String> {
        val file = File(MyApplication.appContext?.filesDir, filename)
        return file.readLines()
    }

    private fun writeBINToStorage(BIN: String, filename: String) {
        val file = File(MyApplication.appContext?.filesDir, filename)
        if (!file.toString().contains(BIN)) {
            file.appendText(BIN + "\n")
        }
    }

    private fun clearData() {
        val allValues = listOf(
            _brandView,
            _coordView,
            _lengthView,
            _luhnView,
            _siteView,
            _namecityView,
            _phoneView,
            _prepaidView,
            _schemeView,
            _typeView,
            _countryView
        )
        allValues.forEach { e -> e.value = "" }
    }

    fun onGetData() {
        clearData()
        requestBINData(BIN.value.toString())
        BINHistory.value?.add(BIN.value.toString())

    }

    fun goToMap() {
        if (_coordView.value != "") {
            // Create a Uri from an intent string. Use the result to create an Intent.
            val gmmIntentUri =
                Uri.parse("google.streetview:cbll=${latitude},${longitude}")

            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            // Make the Intent explicit by setting the Google Maps package
            mapIntent.setPackage("com.google.android.apps.maps")

            // Attempt to start an activity that can handle the Intent
            //startActivity(mapIntent)
            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(MyApplication.appContext!!, mapIntent, null)
        }
    }

    fun goToPhone() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${_phoneView.value}")
        //startActivity(intent)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(MyApplication.appContext!!, intent, null)
    }

    fun goToBrowser() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://${_siteView.value}"))
        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(MyApplication.appContext!!, browserIntent, null)
    }

}