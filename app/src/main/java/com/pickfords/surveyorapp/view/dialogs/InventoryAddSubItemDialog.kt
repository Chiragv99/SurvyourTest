package com.pickfords.surveyorapp.view.dialogs

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.DialogAddInventorySubitemBinding
import com.pickfords.surveyorapp.extentions.clear
import com.pickfords.surveyorapp.extentions.disable
import com.pickfords.surveyorapp.extentions.enable
import com.pickfords.surveyorapp.extentions.getValue
import com.pickfords.surveyorapp.extentions.hide
import com.pickfords.surveyorapp.extentions.visible
import com.pickfords.surveyorapp.model.surveyDetails.SequenceDetailsModel
import com.pickfords.surveyorapp.utils.AppConstants
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.surveyDetails.FragmentInventory
import com.pickfords.surveyorapp.view.surveyDetails.InventorySubChildItemAdapter


class InventoryAddSubItemDialog(var mContext: Context, inventoryNameId: String, name: String?) : Dialog(mContext, R.style.ThemeDialog) {

    private lateinit var binding: DialogAddInventorySubitemBinding
    private var listener: OkButtonListener? = null
    private var inventoryNameId: String = inventoryNameId
    private var inventoryName: String = name!!
    private var sequenceDetailsDataList: ArrayList<SequenceDetailsModel> = ArrayList()

    private val sequenceDetailsAdapter: InventorySubChildItemAdapter by lazy {
        InventorySubChildItemAdapter(mContext, sequenceDetailsDataList)
    }

    // For Selection Data
    var selectedLeg :String = ""
    var selectedFloor :String = ""
    var selectedSequence :String = ""
    var selectedRoom :String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        setCanceledOnTouchOutside(false)
        setCancelable(false)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_add_inventory_subitem,
            null,
            false
        )

        setContentView(binding.root)
        setSelectedData()

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        if (!inventoryName.isNullOrEmpty()) {
            binding.txtHeader.text = inventoryName
        }

        binding.addSubitemlistRecyclerView.layoutManager =
            LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.addSubitemlistRecyclerView.adapter = sequenceDetailsAdapter
        var listSubItem: ArrayList<SequenceDetailsModel> = ArrayList()
        listSubItem = if (selectedLeg.isNotEmpty()) {
            (mContext as BaseActivity).sequenceDetailsDao.getAllCartoonSubItemByLeg(
                inventoryNameId,
                selectedSequence,
                selectedRoom,
                selectedFloor,
                AppConstants.surveyID,
                selectedLeg
            ) as ArrayList<SequenceDetailsModel>
        } else {
            (mContext as BaseActivity).sequenceDetailsDao.getAllCartoonSubItem(
                inventoryNameId,
                selectedSequence,
                selectedRoom,
                selectedFloor,
                AppConstants.surveyID
            ) as ArrayList<SequenceDetailsModel>
        }

        if (inventoryNameId.length > 5 && listSubItem.isEmpty()){
            listSubItem =  (mContext as BaseActivity).sequenceDetailsDao.getAllCartoonSubItemForCustomItem(selectedSequence, selectedRoom, selectedFloor, AppConstants.surveyID,inventoryName) as ArrayList<SequenceDetailsModel>
        }

        if (listSubItem.isNotEmpty()) {

            onUpdateAdapterData()
            sequenceDetailsAdapter.setListener(object :
                InventorySubChildItemAdapter.onDeleteSubItemListener {
                override fun onDeleteIconPressed(inventoryId: String?, isChild: Boolean, size: Int) {
                    ItemDeSelectDialog(
                        context,
                        context.resources.getString(R.string.itemdeletealert)
                    )
                        .setListener(object : ItemDeSelectDialog.OkButtonListener {
                            override fun onOkPressed(
                                itemDeSelectDialog: ItemDeSelectDialog,
                                comment: String?
                            ) {
                                itemDeSelectDialog.dismiss()

                                if (isChild) {
                                    if (inventoryId!!.contains("ADD")) {
                                        (mContext as BaseActivity).sequenceDetailsDao.delete(inventoryId.toString())
                                    }
                                    else{
                                        (mContext as BaseActivity).sequenceDetailsDao.updateIsDelete(
                                            inventoryId,
                                        true
                                    )
                                    }
                                }

                                if (listener != null) {
                                    listener?.onItemDelete(
                                        this@InventoryAddSubItemDialog,
                                        isChild,
                                        inventoryId.toString(),
                                        size
                                    )
                                    onUpdateAdapterData()
                                }
                            }

                            override fun onCancelPressed(
                                dialogDamage: ItemDeSelectDialog,
                                comment: String?
                            ) {

                            }
                        })
                        .show()
                }

                override fun onSubItemUpdate(
                    inventoryId: String?,
                    description: String?,
                    count: String?
                ) {
                    val subItemData: SequenceDetailsModel =
                        (mContext as BaseActivity).sequenceDetailsDao.getSelectedCartoonSubItem(
                            inventoryId.toString()
                        )
                    subItemData.count = count
                    subItemData.subItemName = description
                    subItemData.isChangedField = true
                    (mContext as BaseActivity).sequenceDetailsDao.update(subItemData)

                    if (listener != null) {
                        listener?.onItemUpdate(
                            this@InventoryAddSubItemDialog,
                            description!!,
                            count!!,
                            inventoryId!!
                        )
                    }
                }

                override fun onItemSelected(inventoryId: String?) {
                    if (listener != null) {
                        listener?.onSubItemSelection(this@InventoryAddSubItemDialog, inventoryId!!)
                    }
                }

                override fun onEditSelect() {
                    binding.btnAddNew.disable()
                    binding.btnAddNew.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.disable))
                }

                override fun onEditDeSelect() {
                    binding.btnAddNew.enable()
                    binding.btnAddNew.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent))
                }

                override fun onEditError(message: String) {
                    if (message.isNotEmpty()) {
                        binding.txtErrorMessage.text = message
                        binding.txtErrorMessage.visible()
                    } else {
                        binding.txtErrorMessage.hide()
                    }
                }
            })
        } else {
            binding.txtNoData.visibility = View.VISIBLE
            binding.addSubitemlistRecyclerView.visibility = View.GONE
        }


        binding.btnAddNew.setOnClickListener {
            binding.txtNoData.visibility = View.GONE
            binding.addSubitemlistRecyclerView.visibility = View.VISIBLE
            binding.addNewItemView.visible()
            sequenceDetailsAdapter.setIsAddNewItem(true)
            val inputMethodManager =
                mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        binding.ivSave.setOnClickListener {
            if (listener != null) {
                if (binding.etDescription.text.isNullOrEmpty()) {
                    binding.txtErrorMessage.text = mContext.getString(R.string.enter_subitem_name)
                    binding.txtErrorMessage.visible()
                } else if (binding.etCount.text.isNullOrEmpty()) {
                    binding.txtErrorMessage.text = mContext.getString(R.string.enter_subitem_count)
                    binding.txtErrorMessage.visible()
                } else if (Integer.parseInt(binding.etCount.getValue()) < 1) {
                    binding.txtErrorMessage.text =
                        mContext.getString(R.string.enter_subitem_count_more_than_zero)
                    binding.txtErrorMessage.visible()
                } else if (sequenceDetailsDataList.any {
                        it.subItemName == binding.etDescription.getValue() || it.inventoryItemName == binding.etDescription.getValue()
                }) {
                    binding.txtErrorMessage.text = mContext.getString(R.string.this_sub_item_exists)
                    binding.txtErrorMessage.visible()
                } else {
                    binding.btnAddNew.enable()
                    binding.btnAddNew.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent))
                    binding.ivClose.enable()
                    binding.ivClose.colorFilter = PorterDuffColorFilter(
                        ContextCompat.getColor(context, R.color.red),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    sequenceDetailsAdapter.setIsAddNewItem(false)
                    binding.txtErrorMessage.hide()
                    listener?.onOkPressed(
                        this,
                        binding.etDescription.getValue(),
                        binding.etCount.getValue()
                    )
                    sequenceDetailsAdapter.setIsAddNewItem(false)
                    binding.etDescription.clear()
                    binding.etCount.clear()
                    binding.addNewItemView.hide()
                    val inputMethodManager =
                        mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                    onUpdateAdapterData()
                }
            } else {
                dismiss()
            }
        }

        binding.ivDelete.setOnClickListener {
            sequenceDetailsAdapter.setIsAddNewItem(false)
            binding.etDescription.clear()
            binding.etCount.clear()
            binding.addNewItemView.hide()
            binding.txtErrorMessage.hide()
        }

    }
    private fun onUpdateAdapterData() {
        binding.txtNoData.visibility = View.GONE
        binding.addSubitemlistRecyclerView.visibility = View.VISIBLE

        sequenceDetailsDataList = ArrayList()
        sequenceDetailsDataList = if (selectedLeg.isNotEmpty()){
            (mContext as BaseActivity).sequenceDetailsDao.getAllCartoonSubItemByLeg(inventoryNameId,selectedSequence,selectedRoom,selectedFloor,AppConstants.surveyID,selectedLeg) as ArrayList<SequenceDetailsModel>
        } else{
            (mContext as BaseActivity).sequenceDetailsDao.getAllCartoonSubItem(inventoryNameId,selectedSequence,selectedRoom,selectedFloor,AppConstants.surveyID) as ArrayList<SequenceDetailsModel>
        }

        if (inventoryNameId.length > 5 && sequenceDetailsDataList.isEmpty()){
            sequenceDetailsDataList =  (mContext as BaseActivity).sequenceDetailsDao.getAllCartoonSubItemForCustomItem(selectedSequence, selectedRoom, selectedFloor, AppConstants.surveyID,inventoryName) as ArrayList<SequenceDetailsModel>
        }

        sequenceDetailsAdapter.setSequenceDetailsModelList(sequenceDetailsDataList)

        if (!sequenceDetailsDataList.isNullOrEmpty()) {
            if (sequenceDetailsDataList.size > 4){
                val params: ViewGroup.LayoutParams =  binding.addSubitemlistRecyclerView.layoutParams
                params.height = 500
                binding.addSubitemlistRecyclerView.layoutParams = params
            }
        }
    }

    private fun setSelectedData() {
        selectedLeg = FragmentInventory.selectedLeg
        selectedRoom = FragmentInventory.selectedRoom
        selectedSequence = FragmentInventory.selectedSequence
        selectedFloor = FragmentInventory.selectedFloor
    }

    fun setListener(listener: OkButtonListener?): InventoryAddSubItemDialog {
        this.listener = listener
        return this
    }

    interface OkButtonListener {
        fun onOkPressed(dialogDamage: InventoryAddSubItemDialog, description: String?, count: String?)
        fun onSubItemSelection(dialogDamage: InventoryAddSubItemDialog,inventoryId: String?)

        fun onItemDelete(
            dialogDamage: InventoryAddSubItemDialog,
            isParent: Boolean,
            inventoryId: String?,
            size: Int
        )

        fun onItemUpdate(dialogDamage: InventoryAddSubItemDialog,description: String?, count: String?,inventoryId: String?)
    }
}