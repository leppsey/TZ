package com.example.tz

import org.json.JSONObject

public fun parseResponse(response: String): BankData {
    val obj = JSONObject(response)
    var temp = obj.getJSONObject("number")
    var bankData = BankData()
    bankData.length = temp.optString("length")
    bankData.luhn = YESNOData(temp, "luhn")
    bankData.scheme = obj.optString("scheme")
    bankData.brand = obj.optString("brand")
    bankData.type = obj.optString("type")

    bankData.prepaid = YESNOData(obj, "prepaid")
    if (obj.has("country")) {
        temp = obj.getJSONObject("country")
        bankData.alpha2 = temp.optString("alpha2")
        bankData.countryName = temp.optString("name")
        bankData.latitude = temp.optString("latitude")
        bankData.longitude = temp.optString("longitude")
    }

    if (obj.getString("bank") != "null") {
        temp = obj.getJSONObject("bank")
        bankData.city = temp.optString("city")
        bankData.bankName = temp.optString("name")
        bankData.url = temp.optString("url")
        bankData.phone = temp.optString("phone")
    }
    return bankData
}

fun YESNOData(temp: JSONObject, name: String): String {
    val str = temp.opt("$name")
    if (str == "") {
        return ""
    } else {
        if (str == "true") {
            return "YES"
        } else {
            return "NO"
        }
    }
}
