package com.pickfords.surveyorapp.interfaces

import com.pickfords.surveyorapp.model.surveyDetails.InventoryTypeSelectionModel
import com.pickfords.surveyorapp.model.surveyDetails.SequenceDetailsModel

interface DeleteFromSequence {
    fun onLongClickSelectedItem(model: SequenceDetailsModel?, position: Int?)
    fun onSelectedItem(name: String?, model: SequenceDetailsModel?)
    fun onEditSelectedItem(model: SequenceDetailsModel?, position: Int?)
    fun onChangeSelectedItem(model: SequenceDetailsModel?, position: Int?)
}