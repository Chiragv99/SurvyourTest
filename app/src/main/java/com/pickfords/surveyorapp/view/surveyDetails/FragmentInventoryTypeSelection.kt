package com.pickfords.surveyorapp.view.surveyDetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.FragmentInvantoryBinding
import com.pickfords.surveyorapp.databinding.FragmentInventoryTypeSelectionBinding
import com.pickfords.surveyorapp.interfaces.FragmentLifecycleInterface
import com.pickfords.surveyorapp.interfaces.InventorySelection
import com.pickfords.surveyorapp.model.surveyDetails.InventoryTypeSelectionModel
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.BaseFragment
import com.pickfords.surveyorapp.view.surveyDetails.FragmentInventory.Companion.selectedFloor
import com.pickfords.surveyorapp.view.surveyDetails.FragmentInventory.Companion.selectedLeg
import com.pickfords.surveyorapp.view.surveyDetails.FragmentInventory.Companion.selectedRoom
import com.pickfords.surveyorapp.view.surveyDetails.FragmentInventory.Companion.selectedSequence
import com.pickfords.surveyorapp.viewModel.surveyDetails.InventoryTypeSelectionViewModel
import java.util.*

class FragmentInventoryTypeSelection : BaseFragment(), FragmentLifecycleInterface {
    lateinit var inventoryTypeSelectionBinding: FragmentInventoryTypeSelectionBinding
    private val viewModel by lazy { InventoryTypeSelectionViewModel(requireActivity()) }
    private var itemId: String = ""
    private var surveyId: String = ""

    companion object {

        const val SURVEYID: String = "surveyId"
        fun newInstance(
            itemId: String?,
            surveyId: String): FragmentInventoryTypeSelection {
            val bundle = Bundle()
            bundle.putString(Session.INVENTORY, itemId)
            bundle.putString(SURVEYID, surveyId)
            val fragmentInventoryTypeSelection = FragmentInventoryTypeSelection()
            fragmentInventoryTypeSelection.arguments = bundle

            return fragmentInventoryTypeSelection
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (!this::inventoryTypeSelectionBinding.isInitialized) {
            inventoryTypeSelectionBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_inventory_type_selection,
                container,
                false
            )

        }
        return inventoryTypeSelectionBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // if (savedInstanceState == null)
        viewModel.init(mContext)

        if (arguments != null && requireArguments().containsKey(Session.INVENTORY) && requireArguments().get(Session.INVENTORY) != null) {
            itemId = requireArguments().getString(Session.INVENTORY).toString()
            surveyId = requireArguments().getString(SURVEYID).toString()
            viewModel.getSurveyInventoryItemList(
                mContext,
                itemId,
                surveyId,
                selectedFloor,
                selectedRoom,
                selectedSequence,
                selectedLeg,
            )
        }

        inventoryTypeSelectionBinding.lifecycleOwner = this
        inventoryTypeSelectionBinding.viewModel = viewModel
        if (this::onInventorySelection.isInitialized) {
            viewModel.setInventorySelection(onInventorySelection)
        }

        searchView(itemId)
    }

    public fun searchView(itemId: String) {
        inventoryTypeSelectionBinding.searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    filter(query,itemId)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    filter(newText, itemId)
                    return false
                }

            })
    }

    public fun hideKeyboard(){
        inventoryTypeSelectionBinding.searchView.clearFocus();
    }

    private fun hideSoftKeyboard(): Boolean {
        return try {
            val imm = (context as BaseActivity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow((context as BaseActivity).currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            false
        }
    }

    private fun filter(text: String?, itemId: String) {
        // creating a new array list to filter our data.
        val filteredList: ArrayList<InventoryTypeSelectionModel> =
            ArrayList<InventoryTypeSelectionModel>()

        if (itemId == "0" && text != ""){
            for (item in viewModel.inventoryTypeListSearch) {
                // checking if the entered string matched with any item of our recycler view.
                addInFilterData(text,item,filteredList)
            }
        }else{
            for (item in viewModel.inventoryTypeList) {
                item.isCurrentItem = false
                addInFilterData(text,item,filteredList)
            }
        }

        if (filteredList.isNotEmpty())
            viewModel.getInventoryTypeSelectionAdapter()?.filterList(filteredList)
        else{
            viewModel.getInventoryTypeSelectionAdapter()?.filterList(viewModel.inventoryTypeList)
        }
    }



    private fun addInFilterData(
        text: String?,
        item: InventoryTypeSelectionModel,
        filteredList: ArrayList<InventoryTypeSelectionModel>
    ) {
        if (text?.lowercase(Locale.getDefault())?.let { item.name?.lowercase(Locale.getDefault())?.contains(it) } == true) {
            // if the item is matched we are
            // adding it to our filtered list.
            filteredList.add(item)
        }
    }

    fun updateSelectedPosition(){
        viewModel.updateSelectedItemPosition()
    }

    fun updateData(
        surveyId: String,
        selectedFloor: String,
        selectedRoom: String,
        selectedSequence: String,
        selectedLeg: String) {
        this.surveyId = surveyId

        viewModel.getSurveyInventoryItemList(
            mContext,
            itemId,
            surveyId,
            selectedFloor,
            selectedRoom,
            selectedSequence,
            selectedLeg
        )

        hideKeyboard()


    }


    fun getSelectedId(): String? {
        return if (!isAdded || viewModel.getInventoryTypeSelectionAdapter()?.selectedPosition == -1) {
            ""
        } else {
            viewModel.getInventoryTypeSelectionAdapter()?.selectedPosition?.let {
                viewModel.getInventoryTypeSelectionList().value?.get(
                    it
                )?.inventoryItemId.toString()
            }!!
        }
    }

    override fun onResumeFragment(s: String?) {
        if (!isAdded)
            return
        viewModel.init(mContext)
        if (arguments != null && requireArguments().containsKey(Session.INVENTORY) && requireArguments().get(
                Session.INVENTORY
            ) != null
        ) {
            itemId = requireArguments().getString(Session.INVENTORY).toString()
            surveyId = requireArguments().getString(SURVEYID).toString()
            viewModel.getSurveyInventoryItemList(
                mContext,
                itemId,
                surveyId,
                selectedFloor,
                selectedRoom,
                selectedSequence,
                selectedLeg
            )
        }

        inventoryTypeSelectionBinding.lifecycleOwner = this
        inventoryTypeSelectionBinding.viewModel = viewModel
        viewModel.setInventorySelection(onInventorySelection)


    }

    override fun onPauseFragment() {}
    lateinit var onInventorySelection: InventorySelection

    fun setItemNameListener(onInventorySelection: InventorySelection) {
        this.onInventorySelection = onInventorySelection
    }

    fun removeParent(  position: Int){
    viewModel.removePos(position)
    }

    fun updateSavedData(
        position: Int,
        surveyInventoryId: String,
        isDelete: Boolean,
        isCustom: Boolean
    ) {

        viewModel.updateSavedData(position, surveyInventoryId, isDelete, isCustom)
        viewModel.getInventoryTypeSelectionAdapter()?.filterList(viewModel.inventoryTypeList)
    }

    fun getInventoryItemId(): String {
        return itemId
    }

    fun addNewItemData(newInventory: InventoryTypeSelectionModel) {
        viewModel.addNewItemData(newInventory)
    }

    fun refreshData() {
        viewModel.getSurveyInventoryItemList(
            mContext,
            itemId,
            surveyId,
            selectedFloor,
            selectedRoom,
            selectedSequence,
            selectedLeg
        )
    }

    fun onItemSelection(){
        if (surveyId.isNotBlank() && selectedFloor.isNotBlank() && selectedRoom.isNotBlank() && selectedSequence.isNotBlank()) {
            try {
                if (inventoryTypeSelectionBinding.searchView.query.toString() != ""){
                    viewModel.getInventoryTypeSelectionAdapter()!!.selectedPosition = -1;
                    setSelectedSearchPosition()
                }
                updateData(surveyId,selectedFloor, selectedRoom, selectedSequence, selectedLeg)
            }catch (e: Exception){

            }

        }
    }

    private fun setSelectedSearchPosition() {
        try {
            if (viewModel.inventoryTypeList != null){
                val index =  viewModel.inventoryTypeList.indexOfFirst{
                    it.name ==viewModel.getInventoryTypeSelectionAdapter()!!.selectedSearchItem
                }
                viewModel.getInventoryTypeSelectionAdapter()!!.selectedPosition = index
                inventoryTypeSelectionBinding.rvInventoryTypeSelection.scrollToPosition(index);
            }
        }catch (e: Exception){

        }
    }
}