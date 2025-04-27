package com.example.jetpriviaapp.data

data class QuestionDataItem(
    val answer: String,
    val category: String,
    val choices: List<String>,
    val question: String
)