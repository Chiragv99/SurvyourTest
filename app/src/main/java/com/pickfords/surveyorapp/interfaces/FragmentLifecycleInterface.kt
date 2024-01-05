package com.pickfords.surveyorapp.interfaces

interface FragmentLifecycleInterface {
    fun onPauseFragment()
    fun onResumeFragment(s: String?)
}