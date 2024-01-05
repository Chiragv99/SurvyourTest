package com.pickfords.surveyorapp.interfaces

interface SyncCallback {
    fun onSyncFailed()
    fun onSyncComplete()
}