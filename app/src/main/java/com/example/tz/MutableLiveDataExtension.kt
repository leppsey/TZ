package com.example.tz

import androidx.lifecycle.MutableLiveData

operator fun <T> MutableLiveData<MutableList<T>>.plusAssign(value: T) {
    val value = this.value ?: arrayListOf()
    value.addAll(value)
    this.value = value
}