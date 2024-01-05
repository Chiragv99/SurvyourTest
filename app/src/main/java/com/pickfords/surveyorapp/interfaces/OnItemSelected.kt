package com.pickfords.surveyorapp.interfaces

interface OnItemSelected<T> {
    fun onItemSelected(t: T?, position: Int)
}