package com.example.jetpriviaapp.network

import com.example.jetpriviaapp.data.QuestionData
import retrofit2.http.GET
import javax.inject.Singleton

@Singleton
interface QuestionApi {

//    https://raw.githubusercontent.com/itmmckernan/triviaJSON/refs/heads/master/world.json
    @GET("world.json")
    suspend fun getQuestionItem():QuestionData

}