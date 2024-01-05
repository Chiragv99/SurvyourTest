package com.pickfords.surveyorapp.interfaces

import com.pickfords.surveyorapp.model.surveyDetails.ShowLegsModel
import com.pickfords.surveyorapp.model.surveyDetails.SurveySequenceDetailsModel
import java.io.Serializable

public interface OnLegsSaved : Serializable {
    fun onSaved(dataModel: ShowLegsModel)

}