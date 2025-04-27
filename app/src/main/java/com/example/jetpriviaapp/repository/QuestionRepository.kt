package com.example.jetpriviaapp.repository

import com.example.jetpriviaapp.data.DataOrException
import com.example.jetpriviaapp.data.QuestionDataItem
import com.example.jetpriviaapp.network.QuestionApi
import javax.inject.Inject

class QuestionRepository @Inject constructor(private val api: QuestionApi) {
    private val dataOrException=
        DataOrException<ArrayList<QuestionDataItem>,Boolean,Exception>()

    suspend fun getAllQuestion():DataOrException<ArrayList<QuestionDataItem>,Boolean,Exception>{
        try{
            dataOrException.loading=true
            dataOrException.data=api.getQuestionItem()
            if(dataOrException.data.toString().isNotEmpty())dataOrException.loading=false
        }
        catch (e:Exception){
            dataOrException.exception=e
        }
        return dataOrException
    }

}