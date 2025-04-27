package com.example.jetpriviaapp.screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jetpriviaapp.component.Questions

@Composable
fun TriviaHome( viewModel: QuestionViewModel = hiltViewModel()){
    Questions(viewModel)
}