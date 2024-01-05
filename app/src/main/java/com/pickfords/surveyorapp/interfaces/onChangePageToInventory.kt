package com.pickfords.surveyorapp.interfaces

import com.pickfords.surveyorapp.model.surveyDetails.SequenceDetailsModel

interface onChangePageToInventory {
    fun onPageChange(model: SequenceDetailsModel?, position: Int?)
}