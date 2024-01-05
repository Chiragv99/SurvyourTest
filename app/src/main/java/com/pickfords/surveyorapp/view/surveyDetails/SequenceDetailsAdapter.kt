package com.pickfords.surveyorapp.view.surveyDetails

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.ItemSequenceDetailsBinding
import com.pickfords.surveyorapp.interfaces.DeleteFromSequence
import com.pickfords.surveyorapp.model.surveyDetails.SectionHeaderSeqaunceDetails
import com.pickfords.surveyorapp.model.surveyDetails.SequenceDetailViewTypeModel
import com.pickfords.surveyorapp.model.surveyDetails.SequenceDetailsModel
import com.pickfords.surveyorapp.utils.AppConstants
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.dialogs.ItemDeSelectDialog
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import com.pickfords.surveyorapp.view.dialogs.ShowSequenceDetailImage
import kotlinx.android.synthetic.main.item_sequence_details.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SequenceDetailsAdapter(private val list: ArrayList<SequenceDetailViewTypeModel>, private val isPickford: Boolean, private var sequenceDetailsDataList: ArrayList<SequenceDetailsModel>, private var  onDeleteFromSequence: DeleteFromSequence?) :
    RecyclerView.Adapter<SequenceDetailsViewHolder>() {

    private var expandedItemIndex = 0
    private var oldPosition = 0


    // Header Type
    private val typeHeader = 0
    private val typeItem = 1


    lateinit var binder: ItemSequenceDetailsBinding

    lateinit var mContext: Context




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SequenceDetailsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        mContext = parent.context
        (mContext as BaseActivity).runOnUiThread {
            if (viewType == typeItem) {
                //inflate your layout and pass it to view holder
                binder = DataBindingUtil.inflate<ItemSequenceDetailsBinding>(
                    layoutInflater,
                    R.layout.item_sequence_details,
                    parent,
                    false
                )
                setVisibility(0)

            } else if (viewType == typeHeader) {
                //inflate your layout and pass it to view holder
                binder = DataBindingUtil.inflate<ItemSequenceDetailsBinding>(
                    layoutInflater, R.layout.item_sequence_details, parent,
                    false
                )
                setVisibility(1)
            }
        }

        return SequenceDetailsViewHolder(binder)
    }


    fun View.setAllEnabled(enabled: Boolean) {
        isEnabled = enabled

    }

    private fun setVisibility(visibilityType: Int) {

        if (visibilityType == 0) {
            binder.llItem.visibility = View.VISIBLE
            binder.llHeader.visibility = View.GONE
            binder.cardMain.visibility = View.VISIBLE
        } else {
            binder.llItem.visibility = View.GONE
            binder.llHeader.visibility = View.GONE
            binder.cardMain.visibility = View.GONE
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].getViewType() == 0) {
            typeHeader
        } else {
            typeItem
        }
    }


    override fun onBindViewHolder(holder: SequenceDetailsViewHolder, @SuppressLint("RecyclerView") position: Int) {
            CoroutineScope(Dispatchers.Main).launch {

                if (AppConstants.revisitPlanning) {
                    holder.itemView.ll_Item.forEach { child -> child.setAllEnabled(false) }
                } else {
                    holder.itemView.ll_Item.forEach { child -> child.setAllEnabled(true) }
                }
                if (list[position].getViewType() == 1) {

                    val detailViewTypeModel: SequenceDetailsModel =
                        list[position] as SequenceDetailsModel
                    holder.bind(detailViewTypeModel)

                    holder.itemView.card_main.setOnClickListener {

                    }


                    holder.itemView.iv_SequenceImage.setOnClickListener {
                        ShowSequenceDetailImage(
                            mContext,
                            detailViewTypeModel.itemImage,
                            detailViewTypeModel.itemImage,
                            detailViewTypeModel.inventoryItemName
                        )
                            .setListener(object : ShowSequenceDetailImage.OkButtonListener {
                                override fun onOkPressed(
                                    dialogDamage: ShowSequenceDetailImage,
                                    roomName: String?
                                ) {
                                    dialogDamage.dismiss()
                                }
                            })
                            .setDamageTitle(true)
                            .show()
                    }

                    holder.binding.cardMain.setOnLongClickListener {
                        if (AppConstants.revisitPlanning) {
                            MessageDialog(
                                mContext,
                                mContext.getString(R.string.revisit_error)
                            ).show()
                        } else {
                            ItemDeSelectDialog(
                                mContext,
                                mContext.resources.getString(R.string.itemdeletealert)
                            )
                                .setListener(object : ItemDeSelectDialog.OkButtonListener {
                                    override fun onOkPressed(

                                        itemDeSelectDialog: ItemDeSelectDialog, comment: String?
                                    ) {
                                        itemDeSelectDialog.dismiss()
                                        onDeleteFromSequence!!.onLongClickSelectedItem(
                                            detailViewTypeModel,
                                            position
                                        )
                                    }

                                    override fun onCancelPressed(
                                        dialogDamage: ItemDeSelectDialog,
                                        comment: String?
                                    ) {

                                    }
                                })
                                .show()
                        }

                        true
                    }

                    //   if (detailViewTypeModel.isCrate) {
                    if (detailViewTypeModel.dimenstion.toString() != "0 x 0 x 0" && detailViewTypeModel.dimenstion.toString() != " x  x ") {
                        holder.itemView.tvDimenstion.visibility = View.VISIBLE
                        holder.itemView.tvDimenstionTitle.visibility = View.VISIBLE
                    } else {
                        holder.itemView.tvDimenstion.visibility = View.GONE
                       // holder.itemView.tvDimenstionTitle.visibility = View.GONE
                    }

                    if (detailViewTypeModel.isSubItem == true) {
                        holder.itemView.tvItem.text =
                            detailViewTypeModel.inventoryItemName + "-" + detailViewTypeModel.subItemName
                    } else {
                        holder.itemView.tvItem.text = detailViewTypeModel.inventoryItemName
                    }

                    if (detailViewTypeModel.itemImage.toString()
                            .isNotEmpty() && !detailViewTypeModel.itemImage.isNullOrEmpty()
                    ) {
                        holder.binding.ivSequenceImage.visibility = View.VISIBLE
                    } else {
                        holder.binding.ivSequenceImage.visibility = View.GONE
                    }

                    holder.itemView.cbDismantle.isChecked = detailViewTypeModel.isDismantle!!

                    holder.itemView.cbDismantle.setOnCheckedChangeListener { buttonView, isChecked ->
                        changeInventoryData(buttonView.id, detailViewTypeModel, holder)
                    }

                    holder.itemView.cbCard.setOnCheckedChangeListener { buttonView, isChecked ->
                        changeInventoryData(buttonView.id, detailViewTypeModel, holder)
                    }

                    holder.itemView.cbBubbleWrap.setOnCheckedChangeListener { buttonView, isChecked ->
                        changeInventoryData(buttonView.id, detailViewTypeModel, holder)
                    }

                    holder.itemView.cbRemains.setOnCheckedChangeListener { buttonView, isChecked ->
                        changeInventoryData(buttonView.id, detailViewTypeModel, holder)
                    }

                    holder.itemView.cbFullExportWrap.setOnCheckedChangeListener { buttonView, isChecked ->
                        changeInventoryData(buttonView.id, detailViewTypeModel, holder)
                    }

                    holder.itemView.cbCrate.setOnCheckedChangeListener { buttonView, isChecked ->
                        changeInventoryData(buttonView.id, detailViewTypeModel, holder)
                    }

                    if (isPickford) {
                        if (position == expandedItemIndex) {
                            holder.showMore()
                        } else {
                            holder.showLess()
                        }
                    } else {
                        holder.showMore()
//                    invisibleView(holder.binding.tvCommentsTitle, holder.binding.tvComments)
                    }

                } else {
                    val detailViewTypeModel: SectionHeaderSeqaunceDetails =
                        list[position] as SectionHeaderSeqaunceDetails
                    Log.e("HeaderDetail", detailViewTypeModel.toString())
                    if (!detailViewTypeModel.headerName.isNullOrEmpty()) {
                        holder.binding.txtLeg.text = "Leg: ${detailViewTypeModel.headerName}"
                    }
                    if (position == 0 && detailViewTypeModel.fromCity.isNullOrEmpty()) {
                        holder.binding.txtLeg.visibility = View.GONE

                    } else {
                        if (!detailViewTypeModel.fromCity.isNullOrEmpty()) {
                            holder.binding.txtFromCity.text = detailViewTypeModel.fromCity
                            holder.binding.llHeader.visibility = View.VISIBLE
                        } else {
                            holder.binding.txtFrom.text = " "
                            holder.binding.llHeader.visibility = View.GONE
                        }
                    }
                    if (position == 0 && detailViewTypeModel.toCity.isNullOrEmpty()) {
                    } else {
                        if (!detailViewTypeModel.toCity.isNullOrEmpty()) {
                            holder.binding.txtToCity.text = detailViewTypeModel.toCity

                        } else {
                            holder.binding.txtTo.text = " "
                        }
                    }

                }

        }
    }

    //  Save Checkbox Saved Data
    private fun changeInventoryData(id: Int, detailViewTypeModel: SequenceDetailsModel, holder: SequenceDetailsViewHolder) {
        when (id) {
            R.id.cbDismantle -> {
                detailViewTypeModel.isDismantle = holder.itemView.cbDismantle.isChecked
                detailViewTypeModel.isChangedField = true
                onDeleteFromSequence!!.onChangeSelectedItem(detailViewTypeModel,0)
            }
            R.id.cbCard -> {
                detailViewTypeModel.isCard = holder.itemView.cbCard.isChecked
                detailViewTypeModel.isChangedField = true
                onDeleteFromSequence!!.onChangeSelectedItem(detailViewTypeModel,0)
            }
            R.id.cbBubbleWrap -> {
                detailViewTypeModel.isBubbleWrap = holder.itemView.cbBubbleWrap.isChecked
                detailViewTypeModel.isChangedField = true
                onDeleteFromSequence!!.onChangeSelectedItem(detailViewTypeModel,0)
            }
            R.id.cbRemains -> {
                detailViewTypeModel.isRemain = holder.itemView.cbRemains.isChecked
                detailViewTypeModel.isChangedField = true
                onDeleteFromSequence!!.onChangeSelectedItem(detailViewTypeModel,0)
            }
            R.id.cbFullExportWrap -> {
                detailViewTypeModel.isFullExportWrap = holder.itemView.cbFullExportWrap.isChecked
                detailViewTypeModel.isChangedField = true
                onDeleteFromSequence!!.onChangeSelectedItem(detailViewTypeModel,0)
            }
            else -> {
                detailViewTypeModel.isCrate = holder.itemView.cbCrate.isChecked
                detailViewTypeModel.isChangedField = true
                onDeleteFromSequence!!.onChangeSelectedItem(detailViewTypeModel,0)
            }
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

    // Delete Sequence Inventory Item
    fun deleteSequenceInventoryItem(sequenceDetailsModel: SequenceDetailsModel) {
        if (sequenceDetailsModel.surveyInventoryId.contains("ADD")) {
            (mContext as BaseActivity).sequenceDetailsDao.delete(sequenceDetailsModel.surveyInventoryId)
        } else {
            (mContext as BaseActivity).sequenceDetailsDao.updateIsDelete(
                sequenceDetailsModel.surveyInventoryId,
                true
            )
        }

    }
}