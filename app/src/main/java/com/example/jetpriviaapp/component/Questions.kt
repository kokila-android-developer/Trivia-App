package com.example.jetpriviaapp.component

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpriviaapp.data.QuestionDataItem
import com.example.jetpriviaapp.screen.QuestionViewModel
import com.example.jetpriviaapp.util.AppColors

@Composable
fun Questions(viewModel: QuestionViewModel){
    val questions=viewModel.data.value.data?.toMutableList()
    val questionIndex = remember {
        mutableIntStateOf(0)
    }

    val selectedAnswers = remember { mutableStateMapOf<Int, String>() }

    if(viewModel.data.value.loading!!){
        Log.d("TAG", "Questions Loading......")
        ShowCircularProgressIndicator()
    }else{
        Log.d("TAG", "Question: ${questions?.size}")
        if(questions!=null) {
            val question=try{
                questions[questionIndex.intValue]
            }catch (e:Exception){
                null
            }
            if (question != null) {
                Log.d("TAG", "Question: index changed ")
                QuestionDisplay(
                    questionDataItem = question, questionIndex = questionIndex,
                    viewModel = viewModel,
                    onPreviousClicked = {
                        questionIndex.value -= 1
                    },
                    onNextClicked = {
                        questionIndex.value += 1
                    },
                    selectedAnswers =selectedAnswers
                    )
            }
        }
    }
}


//@Preview
@Composable
fun QuestionDisplay(
    questionDataItem: QuestionDataItem,
    questionIndex: MutableState<Int>,
    viewModel: QuestionViewModel,
    onNextClicked:(Int)->Unit,
    onPreviousClicked:(Int)->Unit,
    selectedAnswers: SnapshotStateMap<Int, String>
){
    Log.d("TAG", "QuestionDisplay:${selectedAnswers.contains(questionIndex.value)} ")
    val questions=viewModel.data.value.data?.toMutableList()
    val correctCount by remember {
        derivedStateOf {
            selectedAnswers.entries.count { (index, answer) ->
                questions?.getOrNull(index)?.answer == answer
            }
        }
    }
    val choicesState = remember(questionDataItem) {
        questionDataItem.choices.toMutableStateList()
    }
    var isChoiceSelected by remember {
        mutableStateOf(false)
    }
    var answerState by remember(questionDataItem){
        mutableStateOf<Int?>(null)
    }

    var correctAnswerState by remember(questionDataItem) {
        mutableStateOf<Boolean?>(null)
    }

    val updateAnswer:(Int) -> Unit= remember(questionDataItem) {
        {
            selectedAnswers[questionIndex.value]=choicesState[it]
            answerState=it
            correctAnswerState=questionDataItem.answer==choicesState[it]
            isChoiceSelected=true
        }
    }
    var showDialog by remember {
        mutableStateOf(false)
    }

    if(selectedAnswers.contains(questionIndex.value)){
        correctAnswerState=questionDataItem.answer== selectedAnswers[questionIndex.value]
        answerState=questionDataItem.choices.indexOf(selectedAnswers[questionIndex.value])
        isChoiceSelected=true
    }

    if(showDialog){
        CustomAlertDialog(
            onDismissRequest = {showDialog=false},
            dialogTitle = "Alert",
            dialogText = "Kindly select answer"
        )
    }
    val pathEffect=PathEffect.dashPathEffect(floatArrayOf(10f,10f),0f)
    Surface(modifier = Modifier.fillMaxSize(), color = AppColors.mDarkPurple) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Top) {
            if(questionIndex.value>=3)ShowLinearProgress(score =correctCount , questionCount = questionIndex.value, totalQuestion = viewModel.getTotalCount())
            QuestionTracker(counter = questionIndex.value, outOf = viewModel.getTotalCount())
            DashedLines(pathEffect)
            Column(modifier = Modifier.padding(top = 20.dp)) {
                Text(text = questionDataItem.question, color = AppColors.mOffWhite,
                    modifier = Modifier
                        .fillMaxHeight(0.4f)
                        .align(Alignment.Start),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp)

                choicesState.forEachIndexed { index, answerText ->
                    Row(modifier = Modifier
                        .padding(3.dp)
                        .fillMaxWidth()
                        .border(
                            width = 4.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    AppColors.mOffDarkPurple,
                                    AppColors.mOffDarkPurple
                                )
                            ),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clip(
                            RoundedCornerShape(
                                topStartPercent = 50,
                                topEndPercent = 50,
                                bottomEndPercent = 50,
                                bottomStartPercent = 50
                            )
                        )
                        .background(Color.Transparent), verticalAlignment = Alignment.CenterVertically)
                    {
                        RadioButton(
                            selected =answerState==index,
                            onClick = { updateAnswer(index) },
                            modifier = Modifier.padding(1.dp),
                            colors = RadioButtonDefaults.colors(selectedColor=
                                if(correctAnswerState==true&&answerState==index){
                                    Color.Green.copy(alpha = 0.3f)
                                }else{
                                    Color.Red.copy(alpha = 0.3f)
                                }
                            )
                        )
                        val annotatedString= buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Light,
                                color = if(answerState==index&&correctAnswerState==true){
                                    Color.Green
                                }
                                else if(answerState==index&&correctAnswerState!=true){
                                    Color.Red
                                }
                            else{
                                AppColors.mOffWhite
                            },
                                fontSize = 17.sp)){
                                append(answerText)
                            }
                        }
                        Text(text = annotatedString,Modifier.padding(top=3.dp, bottom = 3.dp))
                    }
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    if(questionIndex.value>0) {
                        ShowButton(
                            onClickButton = {
                                onPreviousClicked(questionIndex.value)
                            },
                            modifier = Modifier
                                .width(130.dp)
                                .padding(3.dp),
                            text = "Previous"
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    ShowButton(
                        onClickButton = {
                            if(isChoiceSelected){
                                isChoiceSelected=false
                                onNextClicked(questionIndex.value)
                            }
                            else{
                               showDialog=true
                            }
                        },
                        modifier = Modifier
                            .width(130.dp)
                            .padding(3.dp),
                        text = "Next"
                    )

                }
            }

        }
    }
}
@Composable
fun QuestionTracker(counter:Int=10,outOf:Int=1000){
    Text(text= buildAnnotatedString {
        withStyle(style = ParagraphStyle(textIndent = TextIndent.None)){
            withStyle(style = SpanStyle(color = AppColors.mLightGray, fontSize = 24.sp, fontWeight = FontWeight.Bold) ){
                append("Question $counter/")
                withStyle(style = SpanStyle(color = AppColors.mLightGray, fontSize = 15.sp, fontWeight = FontWeight.Light) ){
                    append("$outOf")
                }
            }
        }
    }
    )
}
@Composable
fun DashedLines(pathEffect: PathEffect){
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .padding(top = 10.dp)) {
        drawLine(color = AppColors.mLightGray, start = Offset(0f,0f),
            end = Offset(size.width,0f),
            pathEffect = pathEffect
        )
    }
}
@Preview
@Composable
fun ShowLinearProgress(score:Int=0,questionCount:Int=12,totalQuestion:Int=100){
//    val progressFactor by remember(score) {
//        androidx.compose.runtime.mutableFloatStateOf(questionCount*0.005f)
//    }
    val progressFactor by remember(questionCount, totalQuestion) {
        mutableFloatStateOf(questionCount.toFloat() / totalQuestion)
    }
    val gradient =Brush.linearGradient(listOf(Color(0xFFF95075),Color(0xFFBE6BE5)))
    Box(modifier = Modifier
        .height(45.dp)
        .fillMaxWidth()
        .border(
            width = 4.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    AppColors.mLightPurple,
                    AppColors.mLightPurple
                )
            ),
            shape = RoundedCornerShape(34.dp)
        )
        .clip(
            shape = RoundedCornerShape(
                topStartPercent = 50, topEndPercent = 50,
                bottomStartPercent = 50, bottomEndPercent = 50
            )
        )
       ){
        Box(
            modifier = Modifier
                .fillMaxWidth(progressFactor)
                .fillMaxHeight()
                .background(brush = gradient),
        )
        Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.Center) {
            Text(
                text = "Score - ${(score * 10)}",
                color = AppColors.mOffWhite,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }

    }
    Spacer(modifier = Modifier
        .fillMaxWidth()
        .height(10.dp))
}
@Composable
fun ShowCircularProgressIndicator(){
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(25.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 1.dp
        )
    }
}