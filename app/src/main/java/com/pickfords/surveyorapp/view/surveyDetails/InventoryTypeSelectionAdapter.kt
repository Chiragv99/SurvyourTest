package com.pickfords.surveyorapp.view.surveyDetails

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.ItemInventoryTypeSelectionBinding
import com.pickfords.surveyorapp.interfaces.InventorySelection
import com.pickfords.surveyorapp.model.surveyDetails.InventoryTypeSelectionModel
import com.pickfords.surveyorapp.model.surveyDetails.SequenceDetailsModel
import com.pickfords.surveyorapp.room.Database
import com.pickfords.surveyorapp.utils.AppConstants
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.dialogs.InventoryAddSubItemDialog
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class InventoryTypeSelectionAdapter(
    private val context: Context,
    private var list: MutableList<InventoryTypeSelectionModel>,
    inventoryTypeListSearch: ArrayList<InventoryTypeSelectionModel>,
) :
    RecyclerView.Adapter<InventoryTypeSelectionViewHolder>() {

    lateinit var onInventorySelection: InventorySelection
    var selectedPosition: Int = -1
    var firstTimeSelectedPosition: Int = -1

    // For Selection Data
    var selectedLeg :String = ""
    var selectedFloor :String = ""
    var selectedSequence :String = ""
    var selectedRoom :String = ""
    var selectedTypeId :Int = 0
    var selectedSearchItem :String = ""


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryTypeSelectionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binder = DataBindingUtil.inflate<ItemInventoryTypeSelectionBinding>(layoutInflater, R.layout.item_inventory_type_selection, parent, false)
        setSelectedData();
        return InventoryTypeSelectionViewHolder(binder)
    }

    // method for filtering our recyclerview items.
    fun filterList(filterList: MutableList<InventoryTypeSelectionModel>) {
        list = filterList
        notifyDataSetChanged()
    }


    fun removeItem(postion: Int){
        if(list.isNotEmpty() && postion < list.size){
            list!!.removeAt(postion)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return list.size + 1
    }

    override fun onBindViewHolder(holder: InventoryTypeSelectionViewHolder, position: Int) {
        setSelectedData();
        if (position == 0) {
            if (selectedTypeId == 0 ){
                holder.binding.btnTextAdd.visibility = View.VISIBLE
                holder.binding.llItem.visibility = View.VISIBLE
                holder.binding.btnTextAdd.isClickable = false
                holder.binding.btnTextAdd.setBackgroundColor(holder.binding.btnTextAdd.context.resources.getColor(R.color.disable));

            }else{
                try {
                    holder.binding.btnTextAdd.visibility = View.VISIBLE
                    holder.binding.llItem.visibility = View.GONE
                    holder.binding.btnTextAdd.setOnClickListener {
                        if (this::onInventorySelection.isInitialized) {
                            var temp = 0
                            if (selectedPosition < list.size){
                                 temp = selectedPosition
                            }
                            selectedPosition = -1
                            onInventorySelection.onSelectedItem("", null)
                            if (temp != -1) {
                                list[temp].isCurrentItem = false
                                notifyItemChanged(temp.plus(1))
                            }
                        }
                    }
                }catch (e : Exception){
                }
            }
        } else {
            val pos = position - 1
            holder.binding.btnTextAdd.visibility = View.GONE
            holder.binding.llItem.visibility = View.VISIBLE
            holder.bind(list[pos])
            holder.binding.llItem.isSelected = list[pos].isSelected

            holder.binding.txtItemName.text = list[pos].name



            if (list[pos].isCurrentItem) {
                holder.binding.llMain.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_yellow_border_rounded_new))
            } else {
                holder.binding.llMain.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rb_style_custom))
            }

            getSubItemCount(holder, pos,list[pos].inventoryNameId,list[pos].name)
            holder.binding.llItem.setOnClickListener {
                try {
                    // For Add Count
                    if (!list.isNullOrEmpty()){
                        if (list[pos].isCurrentItem){
                            if (this::onInventorySelection.isInitialized) {
                                val temp = selectedPosition
                                selectedPosition = -1
                                onInventorySelection.onCountIncreaseSelectedItem(list[pos],pos)
                            }
                        }
                    }
                    if (AppConstants.revisitPlanning) {
                        MessageDialog(context, context.getString(R.string.revisit_error)).show()
                    } else {

                        if (!list[pos].isCurrentItem){
                            if (FragmentInventory.selectedFloor.isNotBlank() && FragmentInventory.selectedRoom.isNotBlank() && FragmentInventory.selectedSequence.isNotBlank()) {
                                selectedPosition = pos
                                //updateSavedData(position)

                                selectedSearchItem = list[pos].name!!

                                if (this::onInventorySelection.isInitialized) {
                                    if (list[pos].surveyInventoryID == null || list[pos].surveyInventoryID.equals("") || list[pos].surveyInventoryID.equals("0") ) {
                                        firstTimeSelectedPosition = pos

                                        onInventorySelection.onSelectedItem(
                                            list[pos].name,
                                            list[pos],
                                            pos,
                                            isCallSaveAPI = true, isChildItem = false, childDescription = ""
                                        )
                                    } else {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            for (i in 0 until list.size) {
                                                list[i].isCurrentItem = false
                                            }
                                            firstTimeSelectedPosition = -1
                                            list[pos].isCurrentItem = true
                                            notifyDataSetChanged()
                                            onInventorySelection.onSelectedItem(
                                                list[pos].name,
                                                list[pos],
                                                pos,
                                                isCallSaveAPI = false, isChildItem = false, childDescription = ""
                                            )
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, context.getString(R.string.select_floor_room_and_sequence), Toast.LENGTH_SHORT).show()
                                selectedPosition = -1
                            }
                        }

                    }
                }catch (e: Exception){
                   Log.e("Error",e.message.toString())
                }
            }


            holder.binding.llItem.setOnLongClickListener {
                hideSoftKeyboard()
                InventoryAddSubItemDialog(context, list[pos].inventoryItemId.toString(),list[pos].name)
                    .setListener(object : InventoryAddSubItemDialog.OkButtonListener {
                        override fun onOkPressed(itemDeSelectDialog: InventoryAddSubItemDialog, description: String?, count: String?) {
                            notifyDataSetChanged()
                            if (description!!.isNotEmpty()) {
                                if (onInventorySelection != null) {
                                    onInventorySelection.onAddInventoryAddSubItem(list[pos].name, list[pos], pos, description, count)
                                }
                            }
                        }

                        override fun onSubItemSelection(dialogDamage: InventoryAddSubItemDialog, inventoryId: String?) {
                            dialogDamage.dismiss()
                            var subItemData: SequenceDetailsModel = SequenceDetailsModel()
                            subItemData = (context as BaseActivity).sequenceDetailsDao.getSelectedCartoonSubItem(inventoryId.toString())
                            if (subItemData != null) {
                                var selectedModel: InventoryTypeSelectionModel = InventoryTypeSelectionModel();
                                setSelectedModel(selectedModel, subItemData, pos)
                            }
                        }

                        override fun onItemDelete(
                            dialogDamage: InventoryAddSubItemDialog,
                            isChild: Boolean,
                            inventoryId: String?,
                            size: Int
                        ) {
                            try {
                                getSubItemCount(holder, pos, list[pos].inventoryNameId , list[pos].name)
                                notifyDataSetChanged()
                                if (!isChild) {
                                    firstTimeSelectedPosition = -1
                                    list[pos].isSelected = false
                                    holder.binding.llItem.isSelected = false
                                    notifyDataSetChanged()
                                    onInventorySelection.onParentDelete(list[pos].surveyInventoryID, pos,list[pos].name)
                                    notifyDataSetChanged()
                                }
                                onInventorySelection.onChildItemDelete()
                                if (size <= 1){
                                    dialogDamage.dismiss()
                                }
                            } catch (e: Exception) {
                            }
                        }

                        override fun onItemUpdate(
                            dialogDamage: InventoryAddSubItemDialog,
                            description: String?,
                            count: String?,
                            inventoryId: String?
                        ) {
                            if (onInventorySelection != null) {
                                onInventorySelection.onChildUpdate(list[pos].name, count)
                            }
                            var subItemData: SequenceDetailsModel = SequenceDetailsModel()
                            subItemData = (context as BaseActivity).sequenceDetailsDao.getSelectedCartoonSubItem(inventoryId.toString())
                            if (subItemData != null) {
                                var selectedModel: InventoryTypeSelectionModel = InventoryTypeSelectionModel();
                                setSelectedModel(selectedModel, subItemData, pos)
                            }
                        }
                    })
                    .show()
                true
            }

        }
    }

    // Set Selection Data
    private fun setSelectedData() {
        selectedLeg = FragmentInventory.selectedLeg
        selectedRoom = FragmentInventory.selectedRoom
        selectedSequence = FragmentInventory.selectedSequence
        selectedFloor = FragmentInventory.selectedFloor
        selectedTypeId = FragmentInventory.selectedTypeId
    }


    private fun hideSoftKeyboard(): Boolean {
        return try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow((context as BaseActivity).currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            false
        }
    }
    private fun getSubItemCount(
        holder: InventoryTypeSelectionViewHolder,
        pos: Int,
        nameId: String?,
        name: String?
    ) {

        var listSubItem : ArrayList<SequenceDetailsModel> = ArrayList()
        listSubItem = if (selectedLeg.isNotEmpty()){
            (context as BaseActivity).sequenceDetailsDao.getAllCartoonSubItemCountByLeg(this.list[pos].inventoryItemId.toString(),selectedSequence,selectedRoom,selectedFloor,AppConstants.surveyID,selectedLeg) as ArrayList<SequenceDetailsModel>
        } else{
            (context as BaseActivity).sequenceDetailsDao.getAllCartoonSubItemCount(this.list[pos].inventoryItemId.toString(),selectedSequence,selectedRoom,selectedFloor,AppConstants.surveyID) as ArrayList<SequenceDetailsModel>
        }

        if (!nameId.isNullOrEmpty()){
            if (nameId!!.length > 5 && listSubItem.isEmpty()){
                listSubItem =  (context as BaseActivity).sequenceDetailsDao.getAllCartoonSubItemForCustomCountItem(selectedSequence, selectedRoom, selectedFloor, AppConstants.surveyID,name!!) as ArrayList<SequenceDetailsModel>
            }
        }

        if (!listSubItem.isNullOrEmpty()){
            holder.binding.llCountSubItem.visibility = View.VISIBLE
            holder.binding.txtSubItemCount.text = listSubItem.size.toString()
        }else{
            holder.binding.llCountSubItem.visibility = View.GONE
        }
      //  notifyDataSetChanged()
    }

    private fun setSelectedModel(selectedModel: InventoryTypeSelectionModel, subItemData: SequenceDetailsModel, pos: Int) {
        selectedModel.name =   subItemData.subItemName
        selectedModel.isSelected = true
        selectedModel.isCurrentItem = false
        selectedModel.surveyInventoryID =  subItemData.surveyInventoryId
        selectedModel.volume = 0.0
        selectedModel.inventoryValue = 0.0

        onInventorySelection.onSelectedItem(
            selectedModel.name,
            selectedModel,
            pos,
            isCallSaveAPI = false, isChildItem = true, childDescription = subItemData.subItemName!!
        )
    }

    fun updateSavedData(position: Int, surveyInventoryId: String, isDelete: Boolean, isCustom: Boolean) {
        try {
            if(isCustom && isDelete){
                val db = Room.databaseBuilder(context, Database::class.java, AppConstants.ROOM_DB).allowMainThreadQueries().build()
                db.inventoryItemListDao().getInventoryItemById(list[position]?.inventoryTypeId ?: -1, list[position]?.inventoryItemId ?: -1)
             //   db.inventoryItemListDao().deleteUserAddedItem(list[position]?.inventoryItemId ?: -1)
            //   list.removeAt(position)
            } else {
                list[position].surveyInventoryID = if (isDelete) "" else surveyInventoryId
                list[position].isSelected = !isDelete

                for (i in 0 until list.size) {
                    list[i].isCurrentItem = false
                }
                if (!isDelete) {
                    list[position].isCurrentItem = true
                    val db = Room.databaseBuilder(
                        context,
                        Database::class.java, AppConstants.ROOM_DB
                    ).allowMainThreadQueries().build()
                    val surveyDetail =
                        db.sequenceDetailsDao().getSurveyDetailByInventoryItem(surveyInventoryId)

                    list[position].volume =
                        if (surveyDetail.volume != null || surveyDetail.volume?.isNotEmpty() == true) {
                            surveyDetail.volume?.toDouble()
                        } else 20.0
                } else {
                    val db = Room.databaseBuilder(
                        context,
                        Database::class.java, AppConstants.ROOM_DB
                    ).allowMainThreadQueries().build()
                    val inventoryItem = db.inventoryItemListDao().getInventoryItemById(list[position]?.inventoryTypeId ?: -1, list[position]?.inventoryItemId ?: -1)
                    list[position].volume = inventoryItem.volume
                }
            }
            notifyDataSetChanged()
        }catch (e: Exception){

        }

    }

    fun addNewItemData(newInventory: InventoryTypeSelectionModel) {
        try {
            list.add(newInventory)
            var index = list.size - 1
            selectedPosition = index
            list[selectedPosition].isSelected = true
            notifyDataSetChanged()
        }catch (e: Exception){
        }
    }
}