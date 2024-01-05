package com.pickfords.surveyorapp.interfaces

import com.pickfords.surveyorapp.model.surveyDetails.InventoryTypeSelectionModel

interface InventorySelection {
    fun onSelectedItem(name: String?, model: InventoryTypeSelectionModel?)
    fun onSelectedItem(name: String?, model: InventoryTypeSelectionModel?, position: Int?, isCallSaveAPI: Boolean,isChildItem : Boolean,childDescription: String)
    fun onLongClickDeSelectedItem(name: String?, model: InventoryTypeSelectionModel?, position: Int?)

    fun onCountIncreaseSelectedItem(model: InventoryTypeSelectionModel?, position: Int?)

    fun onAddInventoryAddSubItem(name: String?, model: InventoryTypeSelectionModel?, position: Int?,description: String?,count: String?)

    fun onChildItemDelete()

    fun onParentDelete(inventoryId: String?, position: Int?, name: String?)

    fun onChildUpdate(name: String?,count: String?)
}