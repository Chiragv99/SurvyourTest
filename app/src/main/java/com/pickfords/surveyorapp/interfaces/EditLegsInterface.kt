package com.pickfords.surveyorapp.interfaces

import com.pickfords.surveyorapp.model.surveyDetails.ShowLegsModel

public interface EditLegsInterface {
    fun onEditLegs(position: Int, dataModel: ShowLegsModel)

}