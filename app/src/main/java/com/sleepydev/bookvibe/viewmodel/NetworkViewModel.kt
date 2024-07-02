package com.sleepydev.bookvibe.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.sleepydev.bookvibe.utils.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class NetworkViewModel @Inject constructor (networkMonitor : NetworkMonitor):ViewModel() {
        @RequiresApi(Build.VERSION_CODES.N)
        val isOnline = networkMonitor.isConnected.asLiveData()
}