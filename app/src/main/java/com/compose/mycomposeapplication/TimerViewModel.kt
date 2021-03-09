package com.compose.mycomposeapplication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

public class TimerViewModel: ViewModel() {

    val TAG = "TimerViewModel"
    val secondsCountDownTime = MutableLiveData("")
    val secondsLiveData: LiveData<String> = secondsCountDownTime
    val minutesCountDownTime = MutableLiveData("")
    val minutesLiveData: LiveData<String> = minutesCountDownTime
    val startAnimation = MutableLiveData(true)
    val startAnimationLiveData:LiveData<Boolean> = startAnimation

    fun onSecondsCountChange(newSecondsCountDownTime:String?){
        secondsCountDownTime.postValue(newSecondsCountDownTime)
    }
    fun onMinutesCountChange(newMinutesCountDownTime:String?){
        minutesCountDownTime.postValue(newMinutesCountDownTime)
    }

    fun onStartClick() {
        Log.d(TAG, "onStartClick: ")
        startAnimation.postValue(false)
    }

    fun onResetClick(){
        Log.d(TAG, "onResetClick: ")
        secondsCountDownTime.postValue("")
        minutesCountDownTime.postValue("")
        startAnimation.postValue(true)
    }
}