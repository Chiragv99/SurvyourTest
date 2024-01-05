package com.pickfords.surveyorapp.view.surveyDetails

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.pickfords.surveyorapp.model.surveyDetails.InventoryTypeSelectionModel
import com.pickfords.surveyorapp.view.surveyDetails.FragmentInventory.Companion.selectedFloor
import com.pickfords.surveyorapp.view.surveyDetails.FragmentInventory.Companion.selectedLeg
import com.pickfords.surveyorapp.view.surveyDetails.FragmentInventory.Companion.selectedRoom
import com.pickfords.surveyorapp.view.surveyDetails.FragmentInventory.Companion.selectedSequence

class InventoryViewPagerAdapter(fragmentManager: FragmentManager, behavior: Int) :
    FragmentStatePagerAdapter(fragmentManager, behavior) {

    private val fragmentList: MutableList<Fragment> = ArrayList()
    private val fragmentTitleList: MutableList<String> = ArrayList()

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getPageTitle(position: Int): CharSequence {
        return fragmentTitleList[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        fragmentTitleList.add(title)
    }

    fun updateSelectedData(position: Int, surveyId: String){
        val fragmentInventoryTypeSelection: FragmentInventoryTypeSelection = fragmentList[position] as FragmentInventoryTypeSelection
        fragmentInventoryTypeSelection.updateData(surveyId, selectedFloor, selectedRoom, selectedSequence, selectedLeg)
        fragmentInventoryTypeSelection.updateSelectedPosition()
    }

    fun updateSelectedInventoryPosition(position: Int){
        val fragmentInventoryTypeSelection: FragmentInventoryTypeSelection = fragmentList[position] as FragmentInventoryTypeSelection
    }
    fun updateSavedData(position: Int, itemPosition: Int, surveyInventoryId: String, isDelete: Boolean) {
        updateSavedData(position, itemPosition, surveyInventoryId, isDelete, false)
    }

    fun updateSavedData(position: Int, itemPosition: Int, surveyInventoryId: String, isDelete: Boolean, isCustom: Boolean) {
        val fragmentInventoryTypeSelection: FragmentInventoryTypeSelection = fragmentList[position] as FragmentInventoryTypeSelection
        fragmentInventoryTypeSelection.updateSavedData(itemPosition, surveyInventoryId, isDelete, isCustom)
    }

    fun removeParentItem(currentPosition:Int, position: Int) {
        val fragmentInventoryTypeSelection: FragmentInventoryTypeSelection = fragmentList[currentPosition] as FragmentInventoryTypeSelection
        fragmentInventoryTypeSelection.removeParent(position)
    }


    fun getInventoryItemId(position: Int) :String{
        val fragmentInventoryTypeSelection: FragmentInventoryTypeSelection = fragmentList[position] as FragmentInventoryTypeSelection
        return fragmentInventoryTypeSelection.getInventoryItemId()
    }

    fun addNewItemData(position: Int, newInventory: InventoryTypeSelectionModel) {
        val fragmentInventoryTypeSelection: FragmentInventoryTypeSelection = fragmentList[position] as FragmentInventoryTypeSelection
        fragmentInventoryTypeSelection.addNewItemData(newInventory)
    }

    fun refreshData(position: Int){
        val fragmentInventoryTypeSelection: FragmentInventoryTypeSelection = fragmentList[position] as FragmentInventoryTypeSelection
        fragmentInventoryTypeSelection.refreshData()
    }

    fun updateSearchData(position: Int) {
        val fragmentInventoryTypeSelection: FragmentInventoryTypeSelection = fragmentList[position] as FragmentInventoryTypeSelection
        fragmentInventoryTypeSelection.onItemSelection()
    }

    fun setSearchData(position: Int,inventorytypeId: Int) {
        val fragmentInventoryTypeSelection: FragmentInventoryTypeSelection = fragmentList[position] as FragmentInventoryTypeSelection
        fragmentInventoryTypeSelection.searchView(inventorytypeId.toString())
    }

    fun hideSoftKeyboard(position: Int){
        val fragmentInventoryTypeSelection: FragmentInventoryTypeSelection = fragmentList[position] as FragmentInventoryTypeSelection
        fragmentInventoryTypeSelection.hideKeyboard()
    }
}