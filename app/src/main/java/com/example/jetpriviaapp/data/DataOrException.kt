package com.example.jetpriviaapp.data

data class DataOrException<T,Boolean,e:Exception>(
    var data:T?=null,
    var loading:Boolean?=null,
    var exception: e?=null
)
