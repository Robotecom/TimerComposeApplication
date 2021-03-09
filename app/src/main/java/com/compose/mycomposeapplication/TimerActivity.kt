package com.compose.mycomposeapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.compose.mycomposeapplication.ui.MyComposeApplicationTheme
import androidx.compose.runtime.getValue

import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment

class TimerActivity : AppCompatActivity() {

    private val timerViewModel by viewModels<TimerViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimerUI {
                Timer(timerViewModel)
            }
        }
    }

}

private val TabHeight = 56.dp

@Composable
fun TimerUI(content: @Composable () -> Unit) {
    MyComposeApplicationTheme {
        // A surface container using the 'background' color from the theme

        Column {


            Surface(
                color = Color.Blue,
                modifier = Modifier
                    .height(TabHeight)
                    .fillMaxWidth()
            ) {
                Row(modifier = Modifier.wrapContentSize(Alignment.Center)) {

                    Text(
                        text = "Timer",
                        fontSize = MaterialTheme.typography.h4.fontSize,
                        color = Color.White
                    )

                }
            }
            content()

        }
    }
}


enum class CircleState {
    START, END
}

@Composable
fun Timer(viewModel: TimerViewModel) {


    val seconds by viewModel.secondsLiveData.observeAsState()
    val minutes by viewModel.minutesLiveData.observeAsState()
    val startAnimation by viewModel.startAnimationLiveData.observeAsState()

    val timerCountInMilliSeconds: Int =
        toInteger(minutes!!) * 60 * 1000 + toInteger(seconds!!) * 1000
    Column(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize()
    ) {

        TextFields(minutes = minutes!!,
            onMinutesChange = { viewModel.onMinutesCountChange(it) },
            seconds = seconds!!,
            onSecondsChagne = { viewModel.onSecondsCountChange(it) })

        ActionButtons(onStartClick = {viewModel.onStartClick()}, onResetClick = {viewModel.onResetClick()} )

        val angleOffset = updateAngleTransition(
            newState = if (startAnimation!!) {
                CircleState.END
            } else {
                CircleState.START
            },
            timeCount = timerCountInMilliSeconds
        )


        TimerCircle(angleOffset = angleOffset)


    }


}

@Composable
fun ActionButtons(onStartClick:()->Unit, onResetClick:()-> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentSize(Alignment.Center)) {
        Button(
            onClick = { onStartClick() },
            modifier = Modifier
                .size(96.dp, 48.dp)
                .padding(4.dp)
        ) {
            Text(text = "Start",  color = Color.White)
        }

        Button(
            onClick = { onResetClick() },
            modifier = Modifier
                .size(96.dp, 48.dp)
                .padding(4.dp)
        ) {
            Text(text = "Reset",  color = Color.White)
        }
    }
}

@Composable
fun TimerCircle(angleOffset: AngleOffsetData) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val minWidth = size.minDimension

        val circleSize = Size(minWidth, minWidth) / 2F
        val customStroke = Stroke(5.dp.toPx(), cap = StrokeCap.Round)
        println("Angle:${angleOffset.angleOffset}")



        drawCircle(
            color = Color.Gray,
            style = Fill,
            radius = circleSize.width / 2f,
            center = Offset(minWidth / 2F, minWidth / 2F),
            alpha = 0.6f
        )

        drawArc(
            color = Color.Blue,
            startAngle = -90F,
            sweepAngle = angleOffset.angleOffset,
            useCenter = false,
            topLeft = Offset(minWidth / 4F, minWidth / 4F),
            size = circleSize,
            alpha = 1F,
            style = customStroke

        )
    }
}

class AngleOffsetData(
    angleOffset: State<Float>
) {
    val angleOffset by angleOffset
}

@Composable
fun updateAngleTransition(newState: CircleState, timeCount: Int): AngleOffsetData {
    val transition = updateTransition(newState)

    val angleOffset = transition.animateFloat(
        transitionSpec = {
            when {
                CircleState.START isTransitioningTo CircleState.END ->
                    tween(
                        durationMillis = 500,
                        delayMillis = 500,
                        easing = LinearOutSlowInEasing
                    )
                else -> tween(
                    timeCount,
                    500,
                    LinearEasing
                )
            }

        }) { progress ->
        println("Circle state:${progress}")
        if (progress == CircleState.START) {
            0f
        } else {
            360f
        }
    }

    return remember(transition) { AngleOffsetData(angleOffset) }
}

fun toInteger(stringToParse: String): Int {
    try {
        val integerValue = stringToParse.toInt()
        return integerValue

    } catch (e: NumberFormatException) {
        println(e)
        return 0
    }
}

@Composable
fun TextFields(
    minutes: String,
    onMinutesChange: (String) -> Unit,
    seconds: String,
    onSecondsChagne: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .padding(16.dp)
            .wrapContentSize(Alignment.Center)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
                .padding(8.dp)
        ) {

            OutlinedTextField(modifier = Modifier
                .size(96.dp, 72.dp)
                .padding(4.dp),
                value = "",
                onValueChange = { onSecondsChagne(it) },
                label = { Text("Hours") }
            )

            OutlinedTextField(
                modifier = Modifier
                    .size(width = 96.dp,height = 72.dp )
                    .padding(4.dp),
                value = minutes,
                onValueChange = { onMinutesChange(it) },
                label = { Text("Minutes") },

                )

            OutlinedTextField(modifier = Modifier
                .size(96.dp, 72.dp)
                .padding(4.dp),
                value = seconds,
                onValueChange = { onSecondsChagne(it) },
                label = { Text("Seconds") }
            )


        }

    }
}

