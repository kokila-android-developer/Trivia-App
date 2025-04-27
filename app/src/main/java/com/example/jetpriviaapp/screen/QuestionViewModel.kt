package com.example.jetpriviaapp.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpriviaapp.data.DataOrException
import com.example.jetpriviaapp.data.QuestionDataItem
import com.example.jetpriviaapp.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(private val repository: QuestionRepository):ViewModel(){
    val data:MutableState<DataOrException<ArrayList<QuestionDataItem>
    ,Boolean,Exception>> = mutableStateOf(DataOrException(null,true,Exception("")))

    init {
        getAllQuestion()
    }
    fun getAllQuestion(){
        viewModelScope.launch {
            data.value.loading=true
            data.value=repository.getAllQuestion()
            if(data.value.data.toString().isNotEmpty())data.value.loading=false

        }
    }

    fun getTotalCount():Int{
        return data.value.data?.toMutableList()?.size!!
    }

}