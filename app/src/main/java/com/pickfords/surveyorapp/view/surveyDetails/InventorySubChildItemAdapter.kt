package com.pickfords.surveyorapp.view.surveyDetails

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.ListAddInventorySubItemBinding
import com.pickfords.surveyorapp.extentions.disable
import com.pickfords.surveyorapp.extentions.enable
import com.pickfords.surveyorapp.extentions.hide
import com.pickfords.surveyorapp.extentions.visible
import com.pickfords.surveyorapp.model.surveyDetails.InventoryTypeSelectionModel
import com.pickfords.surveyorapp.model.surveyDetails.SequenceDetailsModel


class InventorySubChildItemAdapter(private val context: Context, private var list: ArrayList<SequenceDetailsModel>) :  RecyclerView.Adapter<InventoryAddInventorySubItemViewHolder>() {

    lateinit var mContext: Context
    private var listener: onDeleteSubItemListener? = null
    private var isEditableIndex = -1
    private var isAddNewItem: Boolean = false

    class ViewHolder(val binding: ListAddInventorySubItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryAddInventorySubItemViewHolder { val layoutInflater = LayoutInflater.from(parent.context)
        mContext = parent.context
        val binder = DataBindingUtil.inflate<ListAddInventorySubItemBinding>(
            layoutInflater,
            R.layout.list_add_inventory_sub_item,
            parent,
            false
        )
        return InventoryAddInventorySubItemViewHolder(binder)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: InventoryAddInventorySubItemViewHolder, position: Int) {
        holder.bind(list[position])
        val addInventorySubItem: SequenceDetailsModel = list[position]
        holder.binding.edName.setText(if(addInventorySubItem.isSubItem == true)  addInventorySubItem.subItemName.toString() else  addInventorySubItem.inventoryItemName.toString())
        holder.binding.edCount.setText(addInventorySubItem.count)
        holder.binding.ivDelete.setOnClickListener {
            if (listener != null) {
                if (!list.isNullOrEmpty()){
                    listener?.onDeleteIconPressed(addInventorySubItem.surveyInventoryId,addInventorySubItem.isSubItem!!,list.size)
                }
            }
        }

        if (isAddNewItem) {
            holder.binding.ivEdit.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.disable))
            holder.binding.ivDelete.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.disable))
            holder.binding.llMain.setBackgroundResource(R.drawable.bg_inventory_sub_item)
            holder.binding.ivEdit.disable()
            holder.binding.ivDelete.disable()
            holder.binding.ivClose.hide()
        } else {
            holder.binding.ivEdit.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccentLight))
            holder.binding.ivDelete.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccentLight))
            holder.binding.ivEdit.enable()
            holder.binding.ivDelete.enable()

            if (isEditableIndex != -1) {
                if (isEditableIndex == holder.adapterPosition) {
                    holder.binding.edName.enable()
                    holder.binding.edCount.enable()
                    holder.binding.edName.requestFocus()
                    holder.binding.ivEdit.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.navy_blue))
                    holder.binding.ivEdit.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_correct))
                    holder.binding.ivClose.visible()
                    holder.binding.ivDelete.hide()


                    holder.binding.llMain.setBackgroundResource(R.drawable.bg_add_inventory_sub_item)
                    if (addInventorySubItem.isSubItem == false) {
                        holder.binding.edName.disable()
                        holder.binding.edCount.requestFocus();
                    }

                    if (listener != null) {
                        listener?.onEditSelect()
                    }

                } else {
                    holder.binding.ivEdit.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.disable))
                    holder.binding.ivDelete.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.disable))
                    holder.binding.ivEdit.disable()
                    holder.binding.ivDelete.visible()
                    holder.binding.ivClose.hide()

                    if (listener != null) {
                        listener?.onEditDeSelect()
                    }
                    holder.binding.llMain.setBackgroundResource(R.drawable.bg_inventory_sub_item)
                }

            } else {
                holder.binding.edName.disable()
                holder.binding.edCount.disable()
                holder.binding.ivEdit.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccentLight))
                holder.binding.ivEdit.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_edit))
                holder.binding.llMain.setBackgroundResource(R.drawable.bg_inventory_sub_item)
                holder.binding.ivDelete.visible()
                holder.binding.ivClose.hide()
                if (listener != null) {
                    listener?.onEditDeSelect()
                }


                if (addInventorySubItem.isSubItem == false) {
                    holder.binding.edName.disable()
                }
            }
        }

        holder.binding.ivClose.setOnClickListener {
            isEditableIndex = -1
            notifyDataSetChanged()
        }


        holder.binding.ivEdit.setOnClickListener {
            if (isEditableIndex == holder.adapterPosition) {
                var description = holder.binding.edName.text.toString()
                val count = holder.binding.edCount.text.toString()
                var subItemName = if (addInventorySubItem.isSubItem == true){
                    addInventorySubItem.subItemName
                } else {
                    addInventorySubItem.inventoryItemName
                }

                Log.e("Description", description + " " + addInventorySubItem.subItemName)
                if (description.isNullOrEmpty()) {
                    listener?.onEditError(mContext.getString(R.string.enter_subitem_name))
                } else if (count.isNullOrEmpty()) {
                    listener?.onEditError(mContext.getString(R.string.enter_subitem_count))
                } else if (Integer.parseInt(count) < 1) {
                    listener?.onEditError(mContext.getString(R.string.enter_subitem_count_more_than_zero))
                } else if (description != subItemName ) {
                    if (list.any { it.subItemName == description || it.inventoryItemName == description }) {
                        listener?.onEditError(mContext.getString(R.string.this_sub_item_exists))
                    }else{
                        editItem(addInventorySubItem,description,count)
                    }
                }
                  else {
                    editItem(addInventorySubItem, description, count)
                }
            } else {
                isEditableIndex = holder.adapterPosition
                notifyDataSetChanged()
            }
        }

        holder.binding.llMain.setOnClickListener {
            val selectedModel = InventoryTypeSelectionModel();
            selectedModel.name = holder.binding.edName.text.toString()
            selectedModel.isSelected = true
            selectedModel.isCurrentItem = false
            selectedModel.surveyInventoryID = addInventorySubItem.surveyInventoryId
            selectedModel.volume = 0.0
            selectedModel.inventoryValue = 0.0
            if (listener != null) {
                listener?.onItemSelected(addInventorySubItem.surveyInventoryId,)
            }
        }
    }

    private fun editItem(
        addInventorySubItem: SequenceDetailsModel,
        description: String,
        count: String
    ) {
        if (listener != null) {
            listener?.onEditError("")
            addInventorySubItem.count = count
            addInventorySubItem.subItemName = description
            listener?.onSubItemUpdate(addInventorySubItem.surveyInventoryId, description, count)
            isEditableIndex = -1
            notifyDataSetChanged()
        }
    }

    interface onDeleteSubItemListener {
        fun onDeleteIconPressed(inventoryId: String?, isChild: Boolean, size: Int)
        fun  onSubItemUpdate(inventoryId: String?,description: String?, count: String?)

        fun  onItemSelected(inventoryId: String?)

        fun  onEditSelect()

        fun  onEditDeSelect()

        fun onEditError(message: String)
    }
    fun setListener(listener: onDeleteSubItemListener?): InventorySubChildItemAdapter {
        this.listener = listener
        return this
    }

    fun setIsAddNewItem(isAddNewItem: Boolean) {
        this.isAddNewItem = isAddNewItem
        notifyDataSetChanged()
    }

    fun setSequenceDetailsModelList(list: ArrayList<SequenceDetailsModel>) {
        this.list = list
        notifyDataSetChanged()
    }
}
