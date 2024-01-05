package com.pickfords.surveyorapp.view.surveyDetails

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pickfords.surveyorapp.DashboardActivity
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.FragmentInvantoryBinding
import com.pickfords.surveyorapp.interfaces.FragmentLifecycleInterface
import com.pickfords.surveyorapp.interfaces.InventorySelection
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.model.surveyDetails.InventoryTypeSelectionModel
import com.pickfords.surveyorapp.model.surveyDetails.SequenceDetailsModel
import com.pickfords.surveyorapp.model.surveyDetails.SurveyInventoryList
import com.pickfords.surveyorapp.utils.*
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.BaseFragment
import com.pickfords.surveyorapp.view.dialogs.*
import com.pickfords.surveyorapp.viewModel.surveyDetails.InventoryViewModel
import kotlinx.android.synthetic.main.fragment_invantory.*
import kotlinx.android.synthetic.main.fragment_survey_details.tabLayout
import kotlinx.android.synthetic.main.fragment_survey_details.viewPager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class FragmentInventory : BaseFragment(), FragmentLifecycleInterface {
    lateinit var inventoryBinding: FragmentInvantoryBinding
    private val viewModel by lazy { InventoryViewModel(requireActivity()) }
    private var data: ArrayList<SurveyInventoryList>? = ArrayList()
    private var selectedData: DashboardModel? = null
    private var model: SequenceDetailsModel? = null
    private var isOneOptionSelected = false
    private var isDismantleSelected = false
    private var isCrate = false
    private var isCardSelected = false
    private var isBubbleWrapSelected = false
    private var isRemainsSelected = false
    private var isFullExportWrapSelected = false
    private var crateLength: String = ""
    private var createMeasurementType: String = ""
    private var crateWidth: String = ""
    private var crateHeight: String = ""
    private var miscComment: String = ""
    private var damageComment: String = ""
    private var imageName: String = ""
    private var imgFile: File? = null
    private var imagePath: Uri? = null
    private var isChildItem: Boolean? = false
    private var childItemDescription: String = ""

    private val camera: Int = 0x50
    val gallery: Int = 0x51

    private var viewPagerAdapter: InventoryViewPagerAdapter? = null

    var isCustom: Boolean = false

    var countStr: String = ""
    var volumeStr: String = ""
    var itemValueStr: String = ""

    private var inventoryFloorSpinnerAdapter: ArrayAdapter<String?>? = null
    private var inventoryRoomSpinnerAdapter: ArrayAdapter<String?>? = null
    private var sequenceSpinnerAdapter: ArrayAdapter<String?>? = null
    private var sequenceLegSpinnerAdapter: ArrayAdapter<String?>? = null

    private var inventoryFloorList: ArrayList<String> = ArrayList<String>()
    private val inventoryRoomList: ArrayList<String> = ArrayList<String>()
    private val sequenceList: MutableList<String?> = mutableListOf()
    private val sequenceLegList: MutableList<String?> = mutableListOf()
    private var surveyInventoryID: String? = "0"


    private var currentSequenceDetail: SequenceDetailsModel? = null
    private var currentInventoryTypeSelection: InventoryTypeSelectionModel? = null

    var inventoryTypeSelectionPosition = -1
    private var selectedSequencePosition = 1

    // For Room Add
    var isRoomAdd: Boolean = false;
    var filterInventoryTypeSelection: FragmentInventoryTypeSelection? = null;
    var sequenceId = "0"

    private var crateTypeId: Int = 0
    private var crateTypeList: ArrayList<String> = ArrayList<String>()
    private var crateTypeIdList: ArrayList<Int> = ArrayList<Int>()

    companion object {
        fun newInstance(selectedData: DashboardModel?): FragmentInventory {
            val bundle = Bundle()
            bundle.putSerializable(Session.DATA, selectedData)
            val fragmentInventory = FragmentInventory()
            fragmentInventory.arguments = bundle
            return fragmentInventory
        }

        internal var selectedFloor: String = ""
        internal var selectedRoom: String = ""
        internal var selectedSequence: String = ""
        internal var selectedLeg: String = ""
        internal var selectedSurveyId: String = ""

        internal var selectedRoomId: Int = 0
        internal var selectedUserRoomId: Int = 0

        internal var selectedTypeId: Int = 0

        var isAddItem: Boolean = false
    }


    @SuppressLint("LongLogTag")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::inventoryBinding.isInitialized) {
            inventoryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_invantory, container, false)
        }
        selectedSurveyId = selectedData?.surveyId.toString()
        setObserver()
        setAction()
        setSpinnerTitle()
        return inventoryBinding.root
    }

    @SuppressLint("LongLogTag")
    private fun setObserver() {

        viewModel.selectedPosition.observe(requireActivity()) {
           // selectedPosition = it
            inventoryBinding.spnSequence.setSelection(it)
           // isFirstTime = false
        }

        viewModel.selectedInventoryFloorLiveData.observe(requireActivity()) {
            selectedFloor = if (it.floor.toString() != "null") it.floor.toString() else ""
            Log.e("SelectedFloor: ", selectedFloor)
            checkAndUpdateData()
        }

        viewModel.itemimage.observe(requireActivity()) {
            if (it != null && !it.equals("")) {
                inventoryBinding.imgCamera.setColorFilter(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.red
                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                );
            } else {
                inventoryBinding.imgCamera.setColorFilter(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.gray_323232
                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                );
            }
        }

        viewModel.addRoomLiveList.observe(requireActivity()) {
            Log.e("AddRoom", it)
            Log.e("SelectedRoom: ", selectedRoom)
            selectedRoom = ""
            viewModel.roomNameEnabled.set(false)
            var roomName = ""
            AddRoomDialog(requireContext(), roomName).setListener(object :
                AddRoomDialog.OkButtonListener {
                override fun onOkPressed(dialogDamage: AddRoomDialog, roomName: String?) {
                    // For Update and Add Room

                    selectedUserRoomId = 0
                    isRoomAdd = true;
                    viewModel.saveRoomName(
                        0,
                        Integer.parseInt(selectedData?.surveyId),
                        roomName.toString(),
                        0,
                        selectedUserRoomId
                    )
                    dialogDamage.dismiss()
                }
            }).setDamageTitle(true).show()
        }

        viewModel.selectedInventoryRoomLiveData.observe(requireActivity()) {
            selectedRoom = if (it.room.toString() != "null") it.room.toString() else ""
            Log.e("SelectedRoom: ", selectedRoom)

            if (it.room.toString() != "null") {
                selectedRoomId = it.roomId
                selectedUserRoomId = it.userRoomId
                viewModel.roomNameEnabled.set(true)
                inventoryBinding.edtItemRoomName.setText(selectedRoom)
                checkAndUpdateData()
            } else {
                viewModel.roomNameEnabled.set(false)
            }
        }

        viewModel.selectedSequenceLiveData.observe(requireActivity()) {

            selectedSequence =
                if (it.surveySequence == null) it.labelToUse ?: "" else it.surveySequence ?: ""
            selectedSequencePosition = it.position
            //            selectedSequence = if (!it.surveySequence.toString().equals("null")) it.surveySequence.toString() else ""
            Log.e("SelectedSequence: ", selectedSequence)
            checkAndUpdateData()
        }

        viewModel.selectedSequenceLegLiveData.observe(requireActivity()) {
            selectedLeg =
                if (it.surveySequenceLeg.toString() != "null") it.surveySequenceLeg.toString() else ""
            Log.e("SelectedLeg: ", selectedLeg)
            checkAndUpdateData()
        }

        viewModel.isReset.observe(requireActivity()) {
            imgFile = null
            imageName = ""
            if (model != null) model?.image = null


            if (isAddItem) {
                isAddItem = false
                resetValues()
                inventoryBinding.btnApproveAdmin.isChecked = false
                viewModel.itemEnabled.set(isAddItem)
                inventoryBinding.llActionButtons.visibility = View.GONE

            } else {
                Log.e("surveyInventoryId: ", surveyInventoryID.toString())
                Log.e(
                    "inventoryTypeSelectionPosition: $inventoryTypeSelectionPosition",
                    "surveyInventoryID: $surveyInventoryID"
                )
                if (inventoryTypeSelectionPosition != -1 && !surveyInventoryID.isNullOrEmpty()) {
                    viewPagerAdapter?.updateSavedData(
                        inventoryBinding.viewPager.currentItem,
                        inventoryTypeSelectionPosition,
                        surveyInventoryID!!,
                        false,
                        isCustom
                    )
                }
            }
            viewPagerAdapter?.setSearchData(inventoryBinding.viewPager.currentItem,0)

        }

        viewModel.isDelete.observe(requireActivity()) {
            if (inventoryTypeSelectionPosition != -1) {
                viewPagerAdapter?.updateSavedData(
                    inventoryBinding.viewPager.currentItem,
                    inventoryTypeSelectionPosition,
                    "",
                    true,
                    isCustom
                )
                if (viewModel.isUserAdded.value == true){
                    viewPagerAdapter?.removeParentItem( inventoryBinding.viewPager.currentItem,inventoryTypeSelectionPosition)
                }
             //
            }
            inventoryBinding.spnCount.setText("1")
            inventoryBinding.edtInputVolume.setText("0")
            resetValues()
        }

        viewModel.currentSequenceDetailModel.observe(requireActivity()) {
            Log.e("currentSequenceDetailModel: ", it.toString())
            Log.e("surveyInventoryId: ", it.surveyInventoryId)
            surveyInventoryID = it.surveyInventoryId
            if (inventoryTypeSelectionPosition != -1) {
                viewPagerAdapter?.updateSavedData(
                    inventoryBinding.viewPager.currentItem,
                    inventoryTypeSelectionPosition,
                    it.surveyInventoryId,
                    false
                )
            }
        }

        viewModel.newlyAddedInventoryType.observe(requireActivity()) {
            Log.e("newlyAddedInventoryType", it.toString())
            viewPagerAdapter?.addNewItemData(inventoryBinding.viewPager.currentItem, it)
            inventoryBinding.llActionButtons.visibility = View.GONE
            Toast.makeText(
                context,
                "Item Saved Successfully",
                Toast.LENGTH_SHORT
            ).show()
        }

        viewModel.totalVolume.observe(requireActivity()) {
            inventoryBinding.txtTotalVolume.text = "Total Volume : $it"
        }

        viewModel.selectedCount.observe(requireActivity()) {
            inventoryBinding.txtTotalVolume.text = "Total Volume : $it"
        }

        viewModel.selectedItemPositionSequence.observe(requireActivity()) {
            if (it > 0) {
                sequenceId = viewModel!!.getSelectSequenceModel().value?.get(viewModel.selectedItemPosition - 1)?.surveySequenceId.toString()
                viewModel.updateVolumeCount(selectedData!!.surveyId, sequenceId)
                //   viewPager.currentItem = 0
                //     checkAndUpdateData()
            } else {
                inventoryBinding.txtTotalVolume.text = "Total Volume : 0.0"
            }
        }

    }

    private fun setAction() {

        inventoryBinding.btnRenameRoom.setOnClickListener {
            if (inventoryBinding.edtItemRoomName.text.toString() != "null" && inventoryBinding.edtItemRoomName.text.toString() != ""
            ) {
                // For Update and Add Room
                viewModel.saveRoomName(
                    selectedRoomId,
                    Integer.parseInt(selectedData?.surveyId),
                    edtItemRoomName.text.toString(),
                    1,
                    selectedUserRoomId
                )
            } else {
                Toast.makeText(
                    context,
                    requireActivity().getString(R.string.please_select_add_room_label),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        inventoryFloorSpinnerAdapter = ArrayAdapter<String?>(
            mContext, android.R.layout.simple_spinner_item, inventoryFloorList as List<String>
        )
        inventoryFloorSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)
        viewModel.inventoryFloorLiveList.observe(requireActivity()) {
            if (it != null) {
                inventoryFloorList.clear()
                inventoryFloorList.add("Select Floor")
                for (data in it) {
                    inventoryFloorList.add(data.floor.toString())
                }
                inventoryFloorSpinnerAdapter?.notifyDataSetChanged()
                inventoryBinding.spnFloor.setSelection(inventoryFloorList.indexOfFirst { s -> s.lowercase() == "ground floor" })
            }
        }
        inventoryBinding.spnFloor.adapter = inventoryFloorSpinnerAdapter


        inventoryRoomSpinnerAdapter = ArrayAdapter<String?>(
            mContext, android.R.layout.simple_spinner_item, inventoryRoomList as List<String>
        )
        inventoryRoomSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)
        viewModel.inventoryRoomLiveList.observe(requireActivity()) {
            if (it != null) {
                inventoryRoomList.clear()
                inventoryRoomList.add("Select Room")
                inventoryRoomList.add("Add Room")
                for (data in it) {
                    inventoryRoomList.add(data.room.toString())
                }
                inventoryRoomSpinnerAdapter?.notifyDataSetChanged()
                if (isRoomAdd && inventoryRoomList != null && inventoryRoomList.size > 0) {
                    inventoryBinding.spnRoom.setSelection(inventoryRoomList.size - 1)
                }
                inventoryBinding.spnRoom.setSelection(inventoryRoomList.indexOfFirst { s -> s.lowercase() == "lounge" })
            }
        }
        inventoryBinding.spnRoom.adapter = inventoryRoomSpinnerAdapter


        // For Get Crate Type List
        viewModel.inventoryCrateTypeList.observe(requireActivity()) {
            if (it != null) {
                crateTypeList.clear()
                crateTypeIdList.clear()
                crateTypeList.add("Select Crate Type")
                for (data in it) {
                    crateTypeList.add(data.getCrateType().toString())
                    crateTypeIdList.add(data.getCrateTypeId()!!.toInt())
                }
            }
        }


        sequenceList.clear()
        sequenceList.add("Select Sequence")
        sequenceSpinnerAdapter = ArrayAdapter<String?>(
            mContext, android.R.layout.simple_spinner_item, sequenceList as List<String>
        )
        sequenceSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)
        inventoryBinding.spnSequence.adapter = sequenceSpinnerAdapter
        viewModel.sequenceLiveList.observe(requireActivity()) {
            if (it != null) {
                sequenceList.clear()
                sequenceList.add("Select Sequence")
                for (data in it) {
                    val name =
                        if (data.surveySequence == null) data.labelToUse else data.surveySequence
                    sequenceList.add(name)
                }
                if (sequenceList.size!! > 0) {
                    inventoryBinding.spnSequence.setSelection(1)
                } else {
                    inventoryBinding.spnSequence.setSelection(0)
                }
                sequenceSpinnerAdapter?.notifyDataSetChanged()


            }
        }


        sequenceLegSpinnerAdapter = ArrayAdapter<String?>(
            mContext, android.R.layout.simple_spinner_item, sequenceLegList as List<String>
        )
        sequenceLegSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)
        viewModel.sequenceLegLiveList.observe(requireActivity()) {
            if (it != null) {
                sequenceLegList.clear()
                sequenceLegList.add("Select Leg")
                for (data in it) {
                    sequenceLegList.add(data.surveySequenceLeg)
                }
            } else {
                sequenceLegList.clear()
                sequenceLegList.add("Select Leg")
            }
            sequenceLegSpinnerAdapter?.notifyDataSetChanged()
        }
        inventoryBinding.spnLegs.adapter = sequenceLegSpinnerAdapter


        countStr = inventoryBinding.spnCount.text.toString()
        volumeStr = inventoryBinding.edtInputVolume.text.toString()
        itemValueStr = inventoryBinding.edtItemValue.text.toString()

        inventoryBinding.edtItemRoomName.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    if (inventoryBinding.edtItemRoomName.text.toString() != "null" && inventoryBinding.edtItemRoomName.text.toString() != ""
                    ) {
                        // For Update and Add Room
                        viewModel.saveRoomName(
                            selectedRoomId,
                            Integer.parseInt(selectedData?.surveyId),
                            edtItemRoomName.text.toString(),
                            1,
                            selectedUserRoomId
                        )
                    } else {
                        Toast.makeText(
                            context,
                            requireActivity().getString(R.string.please_select_add_room_label),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }

        inventoryBinding.spnCount.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    manageSaveBtnClick(true,false,"","")
                    if (countStr != inventoryBinding.spnCount.text.toString())
                        manageSaveBtnClick(true,false,"","")
                    true
                }
                else -> {
                    false
                }
            }
        }

        inventoryBinding.edtInputVolume.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    if (countStr != inventoryBinding.edtInputVolume.text.toString())
                        manageSaveBtnClick(true,false,"","")
                    true
                }
                else -> {
                    false
                }
            }
        }

        inventoryBinding.edtItemValue.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    if (countStr != inventoryBinding.edtItemValue.text.toString())
                        manageSaveBtnClick(true,false,"","")
                    true
                }
                else -> {
                    false
                }
            }
        }
    }


    //  Set Tittle For Spinner
    private fun setSpinnerTitle() {
        inventoryBinding.spnFloor.setTitle("");
        inventoryBinding.spnRoom.setTitle("");
        inventoryBinding.spnSequence.setTitle("");
        inventoryBinding.spnLegs.setTitle("");
    }

    private fun checkAndUpdateData() {
        if (selectedData?.surveyId.toString().isNotBlank() && selectedFloor.isNotBlank() && selectedRoom.isNotBlank() && selectedSequence.isNotBlank()) {
            hideSoftKeyboard()
            onButtonDeselect(inventoryBinding.btnCrate)
            onButtonDeselect(inventoryBinding.btnMisc)
            onButtonDeselect(inventoryBinding.btnDamage)
            isCustom = false
            imgFile = null
            imageName = ""
            inventoryBinding.edtInputVolume.setText("0")

            inventoryBinding.cbDismantle.tag = false
            inventoryBinding.cbFullExportWrap.tag = false
            inventoryBinding.cbCard.tag = false
            inventoryBinding.cbBubbleWrap.tag = false
            inventoryBinding.cbRemains.tag = false


            inventoryBinding.cbDismantle.isChecked = false
            inventoryBinding.cbFullExportWrap.isChecked = false
            inventoryBinding.cbCard.isChecked = false
            inventoryBinding.cbBubbleWrap.isChecked = false
            inventoryBinding.cbRemains.isChecked = false
            viewModel.isEditable.set(false)

            if (this.model != null) this.model?.image = null

            inventoryBinding.imgCamera.setColorFilter(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.gray_323232
                ), android.graphics.PorterDuff.Mode.MULTIPLY
            );

            viewPagerAdapter?.updateSelectedData(
                inventoryBinding.viewPager.currentItem, selectedData?.surveyId.toString()
            )
        }


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = SequenceDetailsModel()

        viewModel.isRefreshList.observe(requireActivity()) { isRefresh ->
            if (isRefresh) {
                inventoryBinding.spnCount.setText("1")
                inventoryBinding.btnApproveAdmin.isChecked = false
                inventoryBinding.cbDismantle.isChecked = false
                inventoryBinding.cbCard.isChecked = false
                inventoryBinding.cbBubbleWrap.isChecked = false
                inventoryBinding.cbRemains.isChecked = false
                inventoryBinding.cbFullExportWrap.isChecked = false
                onButtonDeselect(inventoryBinding.btnCrate)
                onButtonDeselect(inventoryBinding.btnMisc)
                onButtonDeselect(inventoryBinding.btnDamage)
                crateLength = ""
                createMeasurementType = ""
                crateWidth = ""
                crateHeight = ""
                miscComment = ""
                damageComment = ""
                isCrate = false
                viewModel.isRefreshList.postValue(false)
            }
        }
        viewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }

        viewModel.getInventoryModel().observe(requireActivity()) { inventory ->
            if (inventory != null) {
                data = ArrayList()
                for (model in inventory) {
                    data?.add(model)
                }
                setStatePageAdapter()
            }
        }

        inventoryBinding.btnCrate.setOnClickListener {
            crateTypeId = 0
            try {
                crateTypeId = currentSequenceDetail!!.crateTypeId!!.toInt()
            }catch (e: Exception){
            }

            if (viewModel.isEditable.get() == false){
                return@setOnClickListener
            }
            DimensionDialog(
                requireContext(),
                crateLength,
                crateWidth,
                crateHeight,
                isCrate,
                session,
                createMeasurementType,
                crateTypeId,
                crateTypeList,
                crateTypeIdList
            )//todo Pranav
                .setListener(object : DimensionDialog.OkButtonListener {
                    override fun onOkPressed(
                        dialog: DimensionDialog,
                        length: String?,
                        width: String?,
                        height: String?,
                        crate: Boolean?,
                        measurementType: String?,
                        createTypeId: Int) {
                        crateLength = length ?: ""
                        crateWidth = width ?: ""
                        crateHeight = height ?: ""
                        isCrate = crate ?: false
                        createMeasurementType = measurementType ?: ""
                        crateTypeId  = createTypeId

                        if (isCrate){
                            onButtonChanged(inventoryBinding.btnCrate)
                        }else{
                            onButtonDeselect(inventoryBinding.btnCrate)
                        }


                        manageSaveBtnClick(false,false,"","") // todo viraj
                        hideSoftKeyboard()
                        dialog.dismiss()
                    }

                    override fun onCancelPressed(dialogDamage: DimensionDialog) {
                        hideSoftKeyboard()
                        dialogDamage.dismiss()
                    }

                }).show()
        }

        inventoryBinding.btnCrate.setOnLongClickListener {
            crateLength = ""
            crateWidth = ""
            crateHeight = ""
            createMeasurementType = ""
            isCrate = false
            onButtonDeselect(inventoryBinding.btnCrate)
            hideSoftKeyboard()
            manageSaveBtnClick(false,false,"","") // todo viraj

            true
        }

        inventoryBinding.btnMisc.setOnClickListener {
            if (viewModel.isEditable.get() == false){
                return@setOnClickListener
            }
            MiscCommentsDialog(requireContext(), miscComment).setListener(object :
                MiscCommentsDialog.OkButtonListener {
                override fun onOkPressed(dialogMisc: MiscCommentsDialog, comment: String?) {
                    onButtonChanged(inventoryBinding.btnMisc)
                    miscComment = comment!!
                    manageSaveBtnClick(true,false,"","") // todo viraj
                    hideSoftKeyboard()
                    dialogMisc.dismiss()
                }

                override fun onCancelPressed(dialogDamage: MiscCommentsDialog) {
                    hideSoftKeyboard()
                    dialogDamage.dismiss()
                }
            }).setMiscTitle(true).show()
        }

        inventoryBinding.btnMisc.setOnLongClickListener {
            if (miscComment.isNotEmpty()) {
                miscComment = ""
                onButtonDeselect(inventoryBinding.btnMisc)
                manageSaveBtnClick(true,false,"","") // todo viraj
                hideSoftKeyboard()
            }

            true
        }

        inventoryBinding.btnDamage.setOnClickListener {
            if (viewModel.isEditable.get() == false){
                return@setOnClickListener
            }
            DamageCommentsDialog(requireContext(), damageComment).setListener(object :
                DamageCommentsDialog.OkButtonListener {
                override fun onOkPressed(dialogDamage: DamageCommentsDialog, comment: String?) {
                    onButtonChanged(inventoryBinding.btnDamage)
                    damageComment = comment!!
                    manageSaveBtnClick(true,false,"","") // todo viraj
                    hideSoftKeyboard()
                    dialogDamage.dismiss()
                }

                override fun onCancelPressed(dialogDamage: DamageCommentsDialog) {
                    hideSoftKeyboard()
                    dialogDamage.dismiss()
                }
            }).setDamageTitle(true).show()
        }

        inventoryBinding.btnDamage.setOnLongClickListener {
            if (damageComment.isNotEmpty()) {
                damageComment = ""
                onButtonDeselect(inventoryBinding.btnDamage)
                manageSaveBtnClick(true,false,"","") // todo viraj
                hideSoftKeyboard()
            }

            true
        }

        inventoryBinding.cbDismantle.setOnCheckedChangeListener { _, isChecked ->
            if (inventoryBinding.cbDismantle.tag != false) {
                isDismantleSelected = isChecked
                manageSaveBtnClick(true,false,"","")
            }
        }

        inventoryBinding.cbCard.setOnCheckedChangeListener { _, isChecked ->
            if (inventoryBinding.cbCard.tag != false) {
                isCardSelected = isChecked
                manageSaveBtnClick(true,false,"","")
            }
        }
        inventoryBinding.cbBubbleWrap.setOnCheckedChangeListener { _, isChecked ->
            if (inventoryBinding.cbBubbleWrap.tag != false) {
                isBubbleWrapSelected = isChecked
                manageSaveBtnClick(true,false,"","")
            }
        }
        inventoryBinding.cbRemains.setOnCheckedChangeListener { _, isChecked ->
            if (inventoryBinding.cbRemains.tag != false) {
                isRemainsSelected = isChecked
                manageSaveBtnClick(true,false,"","")
            }
        }
        inventoryBinding.cbFullExportWrap.setOnCheckedChangeListener { _, isChecked ->
            if (inventoryBinding.cbFullExportWrap.tag != false) {
                isFullExportWrapSelected = isChecked
                manageSaveBtnClick(true,false,"","")
            }
        }

        inventoryBinding.btnSave.setOnClickListener {
            // API will call to add new Custom Item
            var listItem : ArrayList<InventoryTypeSelectionModel> = ArrayList()
            Log.e("ItemName",selectedSequence + " "+selectedRoom + " "+selectedFloor + " "+AppConstants.surveyID+ " "+edtItemName.text.toString())
            listItem =   (context as BaseActivity).inventoryItemListDao.checkInventoryItemIsThere(edtItemName.text.toString()) as ArrayList<InventoryTypeSelectionModel>
            if (listItem.isEmpty()){
                manageSaveBtnClick(viewPagerAdapter?.getInventoryItemId(inventoryBinding.viewPager.currentItem) ?: "", "",false,false,"","",true,"")
            }else{
                Toast.makeText(
                    context,
                    "Item Name already exist",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        inventoryBinding.btnCancel.setOnClickListener {
            resetValues()
            isAddItem = false
            inventoryBinding.btnApproveAdmin.isChecked = false
            viewModel.itemEnabled.set(isAddItem)
            inventoryBinding.llActionButtons.visibility = View.GONE
        }

        inventoryBinding.imgCamera.setOnClickListener {
            checkImagePickerPermission()
        }

    }

    fun hideSoftKeyboard(): Boolean {
        try {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            return imm.hideSoftInputFromWindow(requireActivity().currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            return false
        }
    }

    private fun getBaseStringURL(imgFile: File): String {
        val filePath: String = imgFile!!.path
        val bitmap = BitmapFactory.decodeFile(filePath)
        val stream = ByteArrayOutputStream()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        }
        // get the base 64 string
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
    }


    @SuppressLint("LongLogTag")
    private fun manageSaveBtnClick(isEditable : Boolean,isChildItem: Boolean,description: String,count: String) {
        if (surveyInventoryID != null && surveyInventoryID!!.isNotEmpty()) {
            val sequenceInventoryItem: SequenceDetailsModel? = viewModel.getSurveyDetailByInventoryItem(mContext, surveyInventoryID!!)
            if (sequenceInventoryItem != null) {
                // API will call to update Item
                viewModel.isEditable.set(isEditable)
                Log.e("Update",sequenceInventoryItem.inventoryNameId.toString() + " "+sequenceInventoryItem.isUserAdded.toString())
                manageSaveBtnClick(
                    sequenceInventoryItem.inventoryTypeId!!,
                    if (sequenceInventoryItem.isCustomize) "" else sequenceInventoryItem.inventoryNameId!!,isEditable,isChildItem,description,count,sequenceInventoryItem.isUserAdded!!,sequenceInventoryItem.inventoryNameId!!
                )
            } else {
                Log.e("SequenceDetailsModel is Null", "Unable to update Data")
            }
        } else {
            Log.e("surveyInventoryID is Null", "Unable to update Data")
        }
        hideSoftKeyboard()
    }

    private fun manageSaveBtnClick(inventoryTypeId: String = "", inventoryItemId: String = "",isEditable: Boolean,isChildItem: Boolean,description:String,count:String,isUserAdded: Boolean,inventoryNameId:String) {

        when {
            inventoryBinding.spnFloor.selectedItemPosition <= 0 -> {
                Toast.makeText(
                    context,
                    requireActivity().getString(R.string.please_select_floor),
                    Toast.LENGTH_SHORT
                ).show()
            }
            inventoryBinding.spnRoom.selectedItemPosition <= 0 -> {
                Toast.makeText(
                    context,
                    requireActivity().getString(R.string.please_select_room),
                    Toast.LENGTH_SHORT
                ).show()
            }
            inventoryBinding.spnSequence.selectedItemPosition <= 0 -> {
                Toast.makeText(
                    context,
                    requireActivity().getString(R.string.please_select_sequence),
                    Toast.LENGTH_SHORT
                ).show()
            }

            inventoryBinding.spnCount.text.toString().isEmpty() -> {
                Toast.makeText(
                    context,
                    requireActivity().getString(R.string.please_add_count),
                    Toast.LENGTH_SHORT
                ).show()
            }
            inventoryBinding.edtInputVolume.text.toString().isEmpty() -> {
                Toast.makeText(
                    context,
                    requireActivity().getString(R.string.please_add_volume),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                val surveyLegsId: String
                val surveyLeg: String

                viewPagerAdapter?.getItem(inventoryBinding.viewPager.currentItem) as FragmentInventoryTypeSelection?

                if (inventoryBinding.spnLegs.selectedItemPosition > 0) {
                    surveyLeg =
                        viewModel.getSelectedLegList().value?.get(inventoryBinding.spnLegs.selectedItemPosition - 1)?.surveySequenceLeg.toString()
                    surveyLegsId =
                        viewModel.getSelectedLegList().value?.get(inventoryBinding.spnLegs.selectedItemPosition - 1)?.surveySequenceLegId.toString()
                } else {
                    surveyLeg = ""
                    surveyLegsId = ""
                }


                var itemValue: String = if (inventoryBinding.edtItemValue.text.toString() == null || inventoryBinding.edtItemValue.text.toString()
                        .equals("")
                ) {
                    "0.0"
                } else {
                    inventoryBinding.edtItemValue.text.toString()
                }


                val partMap: MutableMap<String, RequestBody> = mutableMapOf(
                    "SurveyInventoryId" to surveyInventoryID.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    "SurveyId" to selectedData?.surveyId.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    "FloorId" to viewModel.getSelectFloorModel().value?.get(inventoryBinding.spnFloor.selectedItemPosition - 1)?.floorId.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    "RoomId" to viewModel.getSelectRoomModel().value?.get(inventoryBinding.spnRoom.selectedItemPosition - 2)?.roomId.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    "Room" to viewModel.getSelectRoomModel().value?.get(inventoryBinding.spnRoom.selectedItemPosition - 2)?.room.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    "SequenceId" to viewModel.getSelectSequenceModel().value?.get(inventoryBinding.spnSequence.selectedItemPosition - 1)?.surveySequenceId.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    "SurveySequenceLeg" to surveyLeg.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "SurveySequenceLegId" to surveyLegsId.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "CountId" to inventoryBinding.spnCount.text.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    "Volume" to inventoryBinding.edtInputVolume.text.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    "TotalVolume" to inventoryBinding.edtInputVolume.text.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    "IsDismantle" to isDismantleSelected.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    "IsCard" to isCardSelected.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    "IsBubbleWrap" to isBubbleWrapSelected.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    "IsRemain" to isRemainsSelected.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    "IsFullExportWrap" to isFullExportWrapSelected.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    "InventoryTypeId" to inventoryTypeId.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "InventoryItemName" to (currentInventoryTypeSelection?.name?.toRequestBody("text/plain".toMediaTypeOrNull())
                        ?: "".toRequestBody("text/plain".toMediaTypeOrNull())),
                    "Item" to (currentInventoryTypeSelection?.name?.toRequestBody("text/plain".toMediaTypeOrNull())
                        ?: "".toRequestBody("text/plain".toMediaTypeOrNull())),
                    "InventoryItemId" to inventoryItemId.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "CreatedBy" to selectedData?.userId.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    "CreateLength" to crateLength.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "MeasurementType" to "1".toRequestBody("text/plain".toMediaTypeOrNull()),
                    "CreateHeight" to crateHeight.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "CreateWidth" to crateWidth.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "MiscComment" to miscComment.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "DamageComment" to damageComment.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "Image" to imageName.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "InventoryValue" to (itemValue.toString()
                        ?.toRequestBody("text/plain".toMediaTypeOrNull())
                        ?: "".toRequestBody("text/plain".toMediaTypeOrNull())),
                    "IsCrate" to isCrate.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    "IsPackingType" to if (isAddItem) 0.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()) else currentInventoryTypeSelection?.packingType.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    "IsCustomize" to isCustom.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    "SurveySequence" to inventoryBinding.spnSequence.selectedItem.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                )

                if (inventoryItemId == "" && !isChildItem) {
                    if (!isCheckEditText()) {
                        return
                    }
                    Log.e("ChildItem",this.isChildItem.toString())
                    partMap["InventoryItemName"] = inventoryBinding.edtItemName.text.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull())
                    partMap["Item"] = inventoryBinding.edtItemName.text.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull())
                    partMap["IsApproveAdmin"] =
                        inventoryBinding.btnApproveAdmin.isChecked.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull())

                    val photos: MutableList<MultipartBody.Part> = ArrayList()
                    if (imgFile != null) {
                        val filePath: String = imgFile!!.path
                        val bitmap = BitmapFactory.decodeFile(filePath)

                        partMap["ImageData"] = getByteArrayFromImageURL(bitmap)!!.toRequestBody("text/plain".toMediaTypeOrNull())

                    }

                    if ((context as BaseActivity).addressListDao.getAddressList() != null && (context as BaseActivity).addressListDao.getAddressList().isNotEmpty()) {
                        val list = (context as BaseActivity).addressListDao.getAddressListBySurveyId(selectedData?.surveyId.toString())
                        val countryList = (context as BaseActivity).countryListDao.getCountryList()

                        var code = ""
                        var city = ""
                        var addressId = ""
                        for (i in list)
                            if (viewModel.getSelectSequenceModel().value?.get(inventoryBinding.spnSequence.selectedItemPosition - 1)?.originAddress.toString() == i.titleName) {
                                code = i.countryId.toString()
                                city = i.cityName.toString()
                                addressId = i.surveyAddressId
                            }

                        for (i in countryList)
                            if (i.countryId == code) {
                                code = i.countryName.toString()
                            }
                        model!!.SequenceFromAddressCity = "$city $code"
                        model!!.SequenceFromAddressId = addressId
                        Log.d("add", Gson().toJson((context as BaseActivity).addressListDao.getAddressList()))
                    }
                    if ((context as BaseActivity).addressListDao.getAddressList() != null && (context as BaseActivity).addressListDao.getAddressList()
                            .isNotEmpty()
                    ) {
                        val list = (context as BaseActivity).addressListDao.getAddressListBySurveyId(selectedData?.surveyId.toString())
                        val countryList = (context as BaseActivity).countryListDao.getCountryList()

                        var code = ""
                        var city = ""
                        var addressId = ""
                        for (i in list)
                            if (viewModel.getSelectSequenceModel().value?.get(inventoryBinding.spnSequence.selectedItemPosition - 1)?.destinationAddress.toString() == i.titleName) {
                                code = i.countryId.toString()
                                city = i.cityName.toString()
                                addressId = i.surveyAddressId
                            }

                        for (i in countryList)
                            if (i.countryId == code) {
                                code = i.countryName.toString()
                            }
                        model!!.SequenceToAddressCity = "$city $code"
                        model!!.SequenceToAddressId = addressId
                    }

                    model!!.surveyInventoryId = surveyInventoryID.toString()
                    model!!.surveyId = selectedData?.surveyId.toString()
                    model!!.floor = inventoryBinding.spnFloor.selectedItem.toString()
                    model!!.floorId =
                        viewModel.getSelectFloorModel().value?.get(inventoryBinding.spnFloor.selectedItemPosition - 1)?.floorId.toString()
                    model!!.room = inventoryBinding.spnRoom.selectedItem.toString()
                    model!!.roomId =
                        viewModel.getSelectRoomModel().value?.get(inventoryBinding.spnRoom.selectedItemPosition - 2)?.roomId.toString()
                    model!!.sequence = inventoryBinding.spnSequence.selectedItem.toString()
                    model!!.sequenceId = viewModel.getSelectSequenceModel().value?.get(inventoryBinding.spnSequence.selectedItemPosition - 1)?.surveySequenceId.toString()

                    if (inventoryBinding.spnLegs.selectedItemPosition > 0) {
                        model!!.surveySequenceLeg = inventoryBinding.spnLegs.selectedItem.toString()
                        model!!.surveySequenceLegId = viewModel.getSelectedLegList().value?.get(inventoryBinding.spnLegs.selectedItemPosition - 1)?.surveySequenceLegId.toString()
                    } else {
                        model!!.surveySequenceLeg = null
                        model!!.surveySequenceLegId = "0"
                    }
                    Log.e("SelectedLeg: ", model!!.surveySequenceLeg.toString())
                    Log.e("SelectedLegId: ", model!!.surveySequenceLegId.toString())
                    //                        if (selectedItemId == "") {
                    model!!.inventoryItemName = inventoryBinding.edtItemName.text.toString()
                    model!!.item = inventoryBinding.edtItemName.text.toString()
                    /*} else {
                        model!!.inventoryItemName = ""
                    }*/
                    model!!.count = inventoryBinding.spnCount.text.toString()
                    model!!.volume = inventoryBinding.edtInputVolume.text.toString()
                    model!!.totalVolume =
                        (model!!.volume!!.toFloat() * model!!.count!!.toFloat()).toString()
                    model!!.isDismantle = isDismantleSelected
                    model!!.isCrate = isCrate
                    model!!.packingType =
                        if (isAddItem) 0 else currentInventoryTypeSelection?.packingType
                    model!!.isCard = isCardSelected
                    model!!.isBubbleWrap = isBubbleWrapSelected
                    model!!.isRemain = isRemainsSelected
                    model!!.isFullExportWrap = isFullExportWrapSelected
                    model!!.inventoryTypeId = inventoryTypeId
                    model!!.inventoryNameId = inventoryNameId
                    model!!.createdBy = selectedData?.userId.toString()
                    model!!.createLength = crateLength
                    model!!.createHeight = crateHeight
                    model!!.createWidth = crateWidth
                  //  model!!.dimenstion = crateLength + " x " + crateWidth + " x " + crateHeight
                    model!!.dimenstion = crateHeight + " x " + crateLength + " x " + crateWidth
                    model!!.measurementType = "1"
                    /*Utility.getMeasurementType(createMeasurementType, requireActivity())
                        .toString() //todo Pranav*/
                    model!!.isPackage =
                        inventoryBinding.cbBubbleWrap.isChecked || inventoryBinding.cbFullExportWrap.isChecked
                    model!!.miscComment = miscComment
                    model!!.damageComment = damageComment
                    model!!.image = imgFile.toString()
                    model!!.isApproveAdmin = inventoryBinding.btnApproveAdmin.isChecked
                    model!!.isCustomize = isCustom
                    model!!.inventoryValue = itemValue
                    model!!.surveySequence = inventoryBinding.spnSequence.selectedItem.toString()
                    model!!.isSubItem = isChildItem
                    model!!.subItemName = ""
                    model!!.isUserAdded = isUserAdded
                    viewModel.postSaveInventoryDetails(
                        mContext,
                        partMap,
                        photos,
                        model,
                        surveyInventoryID,
                        imgFile
                    )
                    isOneOptionSelected = false
                    if (!isEditable){
                        viewPagerAdapter?.updateSearchData(inventoryBinding.viewPager.currentItem)
                    }

                } else if (!isChildItem) {
                    partMap["IsApproveAdmin"] =
                        inventoryBinding.btnApproveAdmin.isChecked.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull())
                    val photos: MutableList<MultipartBody.Part> = ArrayList()


                    if (imgFile != null) {

//                        partMap["ImageData"] =
//                            getBaseStringURL(imgFile!!).toRequestBody("text/plain".toMediaTypeOrNull())

                        val filePath: String = imgFile!!.path
                        val bitmap = BitmapFactory.decodeFile(filePath)

                        partMap["ImageData"] = getByteArrayFromImageURL(bitmap)!!.toRequestBody("text/plain".toMediaTypeOrNull())

                    }
                    if ((context as BaseActivity).addressListDao.getAddressList() != null && (context as BaseActivity).addressListDao.getAddressList()
                            .isNotEmpty()
                    ) {
                        val list =
                            (context as BaseActivity).addressListDao.getAddressListBySurveyId(
                                selectedData?.surveyId.toString()
                            )
                        val countryList = (context as BaseActivity).countryListDao.getCountryList()

                        var code = ""
                        var city = ""
                        var addressId = ""
                        for (i in list)
                            if (viewModel.getSelectSequenceModel().value?.get(inventoryBinding.spnSequence.selectedItemPosition - 1)?.originAddress.toString() == i.titleName) {
                                code = i.countryId.toString()
                                city = i.cityName.toString()
                                addressId = i.surveyAddressId
                            }

                        for (i in countryList)
                            if (i.countryId == code) {
                                code = i.countryName.toString()
                            }
                        model!!.SequenceFromAddressCity = "$city $code"
                        model!!.SequenceFromAddressId = addressId
                        Log.d("add", Gson().toJson((context as BaseActivity).addressListDao.getAddressList()))
                    }
                    if ((context as BaseActivity).addressListDao.getAddressList() != null && (context as BaseActivity).addressListDao.getAddressList()
                            .isNotEmpty()
                    ) {
                        val list = (context as BaseActivity).addressListDao.getAddressListBySurveyId(selectedData?.surveyId.toString())
                        val countryList = (context as BaseActivity).countryListDao.getCountryList()

                        var code = ""
                        var city = ""
                        var addressId = ""
                        for (i in list)
                            if (viewModel.getSelectSequenceModel().value?.get(inventoryBinding.spnSequence.selectedItemPosition - 1)?.destinationAddress.toString() == i.titleName) {
                                code = i.countryId.toString()
                                city = i.cityName.toString()
                                addressId = i.surveyAddressId
                            }

                        for (i in countryList)
                            if (i.countryId == code) {
                                code = i.countryName.toString()
                            }
                        model!!.SequenceToAddressCity = "$city $code"
                        model!!.SequenceToAddressId = addressId
                    }


                    model!!.surveyInventoryId = surveyInventoryID.toString()
                    model!!.surveyId = selectedData?.surveyId.toString()
                    model!!.floor = inventoryBinding.spnFloor.selectedItem.toString()
                    model!!.inventoryNameId = inventoryNameId
                    model!!.floorId =
                        viewModel.getSelectFloorModel().value?.get(inventoryBinding.spnFloor.selectedItemPosition - 1)?.floorId.toString()
                    model!!.room = inventoryBinding.spnRoom.selectedItem.toString()
                    model!!.roomId =
                        viewModel.getSelectRoomModel().value?.get(inventoryBinding.spnRoom.selectedItemPosition - 2)?.roomId.toString()
                    model!!.sequence = inventoryBinding.spnSequence.selectedItem.toString()
                    model!!.sequenceId =
                        viewModel.getSelectSequenceModel().value?.get(inventoryBinding.spnSequence.selectedItemPosition - 1)?.surveySequenceId.toString()

                    if (inventoryBinding.spnLegs.selectedItemPosition > 0) {
                        model!!.surveySequenceLeg = inventoryBinding.spnLegs.selectedItem.toString()
                        model!!.surveySequenceLegId =
                            viewModel.getSelectedLegList().value?.get(inventoryBinding.spnLegs.selectedItemPosition - 1)?.surveySequenceLegId.toString()
                        if ((context as BaseActivity).addressListDao.getAddressListByAddressId(
                                viewModel.getSelectedLegList().value?.get(inventoryBinding.spnLegs.selectedItemPosition - 1)?.originAddressId.toString()
                            ) != null
                        ) {

                            try {
                                var city = (context as BaseActivity).addressListDao.getAddressListByAddressId(viewModel.getSelectedLegList().value?.get(inventoryBinding.spnLegs.selectedItemPosition - 1)?.originAddressId.toString()).cityName
                                var counryId =
                                    (context as BaseActivity).addressListDao.getAddressListByAddressId(
                                        viewModel.getSelectedLegList().value?.get(inventoryBinding.spnLegs.selectedItemPosition - 1)?.originAddressId.toString()
                                    ).countryId!!.toInt()
                                var country =
                                    (context as BaseActivity).countryListDao.getCountryNameFromId(
                                        counryId
                                    ).countryName
                            } catch (e: Exception) {
                            }
                        }
                        if ((context as BaseActivity).addressListDao.getAddressListByAddressId(viewModel.getSelectedLegList().value?.get(inventoryBinding.spnLegs.selectedItemPosition - 1)?.destinationAddressId.toString()) != null
                        ) {

                            try {
                                var city =
                                    (context as BaseActivity).addressListDao.getAddressListByAddressId(
                                        viewModel.getSelectedLegList().value?.get(inventoryBinding.spnLegs.selectedItemPosition - 1)?.destinationAddressId.toString()
                                    ).cityName
                                var counryId =
                                    (context as BaseActivity).addressListDao.getAddressListByAddressId(
                                        viewModel.getSelectedLegList().value?.get(inventoryBinding.spnLegs.selectedItemPosition - 1)?.destinationAddressId.toString()
                                    ).countryId!!.toInt()
                                var country =
                                    (context as BaseActivity).countryListDao.getCountryNameFromId(
                                        counryId
                                    ).countryName
                            } catch (e: Exception) {
                            }
                        }

                    } else {
                        model!!.surveySequenceLeg = null
                        model!!.surveySequenceLegId = "0"
                    }

                    Log.e("SelectedLeg: ", model!!.surveySequenceLeg.toString())
                    Log.e("SelectedLegId: ", model!!.surveySequenceLegId.toString())
                    Log.e("DetailModel", model.toString())
                    //                        if (selectedItemId == "") {
                    Log.e("ChildItem", this.isChildItem.toString())
                    if (this.isChildItem == false){
                        model!!.inventoryItemName = currentInventoryTypeSelection?.name ?: ""
                    }

                    model!!.item = currentInventoryTypeSelection?.name ?: ""
                    /*} else {
                        model!!.inventoryItemName = ""
                    }*/
                    model!!.count = inventoryBinding.spnCount.text.toString()
                    model!!.volume = inventoryBinding.edtInputVolume.text.toString()
                    model!!.totalVolume =
                        (model!!.volume!!.toFloat() * model!!.count!!.toFloat()).toString()
                    model!!.isDismantle = isDismantleSelected
                    model!!.isCrate = isCrate
                    model!!.packingType =
                        if (isAddItem) 0 else currentInventoryTypeSelection?.packingType
                    model!!.isCard = isCardSelected
                    model!!.isBubbleWrap = isBubbleWrapSelected
                    model!!.isRemain = isRemainsSelected
                    model!!.isFullExportWrap = isFullExportWrapSelected
                    model!!.inventoryTypeId = inventoryTypeId
                    model!!.inventoryNameId = inventoryItemId
                    model!!.createdBy = selectedData?.userId.toString()
                    model!!.createLength = crateLength
                    model!!.createHeight = crateHeight
                    model!!.createWidth = crateWidth
                  //  model!!.dimenstion = crateLength + " x " + crateWidth + " x " + crateHeight
                    model!!.dimenstion = crateHeight + " x " + crateLength + " x " + crateWidth
                    model!!.measurementType = "1"
                    /*Utility.getMeasurementType(createMeasurementType, requireActivity())
                        .toString()*/ //todo Pranav
                    model!!.miscComment = miscComment
                    model!!.damageComment = damageComment
                    model!!.image = imgFile.toString()
                    model!!.isPackage =
                        inventoryBinding.cbBubbleWrap.isChecked || inventoryBinding.cbFullExportWrap.isChecked
                    model!!.isApproveAdmin = inventoryBinding.btnApproveAdmin.isChecked
                    model!!.isCustomize = isCustom
                    model!!.crateTypeId = crateTypeId
                    model!!.inventoryValue = itemValue
                    model!!.surveySequence = inventoryBinding.spnSequence.selectedItem.toString()
                    model!!.isSubItem = this.isChildItem
                    model!!.isUserAdded = isUserAdded
                    model!!.subItemName = this.childItemDescription
                    viewModel.postSaveInventoryDetails(
                        mContext,
                        partMap,
                        photos,
                        model,
                        surveyInventoryID,
                        imgFile
                    )
                    isOneOptionSelected = false
                    if (!isEditable){
                        viewPagerAdapter?.updateSearchData(inventoryBinding.viewPager.currentItem)
                    }
                }else{

                    // Add Child Item
                    partMap["InventoryItemName"] = inventoryBinding.edtItemName.text.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull())
                    partMap["Item"] = inventoryBinding.edtItemName.text.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull())
                    partMap["IsApproveAdmin"] =
                        inventoryBinding.btnApproveAdmin.isChecked.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull())

                    val photos: MutableList<MultipartBody.Part> = ArrayList()
                    if (imgFile != null) {
                        val filePath: String = imgFile!!.path
                        val bitmap = BitmapFactory.decodeFile(filePath)

                        partMap["ImageData"] = getByteArrayFromImageURL(bitmap)!!.toRequestBody("text/plain".toMediaTypeOrNull())

                    }

                    if ((context as BaseActivity).addressListDao.getAddressList() != null && (context as BaseActivity).addressListDao.getAddressList().isNotEmpty()
                    ) {
                        val list = (context as BaseActivity).addressListDao.getAddressListBySurveyId(selectedData?.surveyId.toString())
                        val countryList = (context as BaseActivity).countryListDao.getCountryList()

                        var code = ""
                        var city = ""
                        var addressId = ""
                        for (i in list)
                            if (viewModel.getSelectSequenceModel().value?.get(inventoryBinding.spnSequence.selectedItemPosition - 1)?.originAddress.toString() == i.titleName) {
                                code = i.countryId.toString()
                                city = i.cityName.toString()
                                addressId = i.surveyAddressId
                            }

                        for (i in countryList)
                            if (i.countryId == code) {
                                code = i.countryName.toString()
                            }
                        model!!.SequenceFromAddressCity = "$city $code"
                        model!!.SequenceFromAddressId = addressId
                        Log.d("add", Gson().toJson((context as BaseActivity).addressListDao.getAddressList()))
                    }
                    if ((context as BaseActivity).addressListDao.getAddressList() != null && (context as BaseActivity).addressListDao.getAddressList()
                            .isNotEmpty()
                    ) {
                        val list = (context as BaseActivity).addressListDao.getAddressListBySurveyId(selectedData?.surveyId.toString())
                        val countryList = (context as BaseActivity).countryListDao.getCountryList()

                        var code = ""
                        var city = ""
                        var addressId = ""
                        for (i in list)
                            if (viewModel.getSelectSequenceModel().value?.get(inventoryBinding.spnSequence.selectedItemPosition - 1)?.destinationAddress.toString() == i.titleName) {
                                code = i.countryId.toString()
                                city = i.cityName.toString()
                                addressId = i.surveyAddressId
                            }

                        for (i in countryList)
                            if (i.countryId == code) {
                                code = i.countryName.toString()
                            }
                        model!!.SequenceToAddressCity = "$city $code"
                        model!!.SequenceToAddressId = addressId
                    }

                    model!!.surveyInventoryId = "0"
                    model!!.surveyId = selectedData?.surveyId.toString()
                    model!!.floor = inventoryBinding.spnFloor.selectedItem.toString()
                    model!!.floorId =
                        viewModel.getSelectFloorModel().value?.get(inventoryBinding.spnFloor.selectedItemPosition - 1)?.floorId.toString()
                    model!!.room = inventoryBinding.spnRoom.selectedItem.toString()
                    model!!.roomId =
                        viewModel.getSelectRoomModel().value?.get(inventoryBinding.spnRoom.selectedItemPosition - 2)?.roomId.toString()
                    model!!.sequence = inventoryBinding.spnSequence.selectedItem.toString()
                    model!!.sequenceId = viewModel.getSelectSequenceModel().value?.get(inventoryBinding.spnSequence.selectedItemPosition - 1)?.surveySequenceId.toString()

                    if (inventoryBinding.spnLegs.selectedItemPosition > 0) {
                        model!!.surveySequenceLeg = inventoryBinding.spnLegs.selectedItem.toString()
                        model!!.surveySequenceLegId = viewModel.getSelectedLegList().value?.get(inventoryBinding.spnLegs.selectedItemPosition - 1)?.surveySequenceLegId.toString()
                    } else {
                        model!!.surveySequenceLeg = null
                        model!!.surveySequenceLegId = "0"
                    }
                    Log.e("SelectedLeg: ", model!!.surveySequenceLeg.toString())
                    Log.e("SelectedLegId: ", model!!.surveySequenceLegId.toString())
                    //                        if (selectedItemId == "") {
                    model!!.inventoryItemName = inventoryBinding.edtItemName.text.toString()
                    model!!.item = inventoryBinding.edtItemName.text.toString()
                    /*} else {
                        model!!.inventoryItemName = ""
                    }*/
                    model!!.count = count
                    model!!.volume = inventoryBinding.edtInputVolume.text.toString()
                    model!!.totalVolume =
                        (model!!.volume!!.toFloat() * model!!.count!!.toFloat()).toString()
                    model!!.isDismantle = isDismantleSelected
                    model!!.isCrate = isCrate
                    model!!.packingType =
                        if (isAddItem) 0 else currentInventoryTypeSelection?.packingType
                    model!!.isCard = isCardSelected
                    model!!.isBubbleWrap = isBubbleWrapSelected
                    model!!.isRemain = isRemainsSelected
                    model!!.isFullExportWrap = isFullExportWrapSelected
                    model!!.inventoryTypeId = inventoryTypeId
                    model!!.inventoryNameId = inventoryItemId
                    model!!.createdBy = selectedData?.userId.toString()
                    model!!.createLength = crateLength
                    model!!.createHeight = crateHeight
                    model!!.createWidth = crateWidth
                    //  model!!.dimenstion = crateLength + " x " + crateWidth + " x " + crateHeight
                    model!!.dimenstion = crateHeight + " x " + crateLength + " x " + crateWidth
                    model!!.measurementType = "1"
                    /*Utility.getMeasurementType(createMeasurementType, requireActivity())
                        .toString() //todo Pranav*/
                    model!!.isPackage =
                        inventoryBinding.cbBubbleWrap.isChecked || inventoryBinding.cbFullExportWrap.isChecked
                    model!!.miscComment = miscComment
                    model!!.damageComment = damageComment
                    model!!.image = imgFile.toString()
                    model!!.isApproveAdmin = inventoryBinding.btnApproveAdmin.isChecked
                    model!!.isCustomize = isCustom
                    model!!.inventoryValue = itemValue
                    model!!.surveySequence = inventoryBinding.spnSequence.selectedItem.toString()
                    model!!.isSubItem = isChildItem
                    model!!.subItemName = description
                    viewModel.postSaveInventoryDetails(
                        mContext,
                        partMap,
                        photos,
                        model,
                        "0",
                        imgFile
                    )
                    isOneOptionSelected = false
                    if (!isEditable){
                        viewPagerAdapter?.updateSearchData(inventoryBinding.viewPager.currentItem)
                    }
                }
            }

        }

        fun addItem(){

        }
    }


    private fun getByteArrayFromImageURL(bitmap: Bitmap): String? {
        val stream = ByteArrayOutputStream()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        }
        // get the base 64 string
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
    }

    private fun isCheckEditText(): Boolean {
        if (inventoryBinding.edtItemName.text.toString().isEmpty()) {
            Toast.makeText(
                context,
                requireActivity().getString(R.string.please_enter_item_name),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true

    }

    private var onInventorySelection = object : InventorySelection {
        override fun onSelectedItem(name: String?, model: InventoryTypeSelectionModel?) {
            inventoryTypeSelectionPosition = -1
            if (!name.isNullOrEmpty() && model != null) {
                manageOnSelectionCode(name, model, false,false,"","")
                //   showKeyboard()
               Log.e("Manage","Manage Selection")


            } else {
                Log.e("Name or Model Null", "Name or Model Null")
                if (AppConstants.revisitPlanning){
                    MessageDialog(mContext, mContext.getString(R.string.revisit_error)).show()
                }
                else{
                    resetValues()
                    isAddItem = true
                    isCustom = true
                    viewModel.itemEnabled.set(isAddItem)
                    inventoryBinding.btnApproveAdmin.isChecked = false
                    inventoryBinding.llActionButtons.visibility = View.VISIBLE

                    // inventoryBinding.edtItemName.setFocusableInTouchMode(true);
                    Handler(Looper.getMainLooper()).postDelayed({
                        openSoftKeyboard(mContext, inventoryBinding.edtItemName)
                    }, 500)

                }
            }
        }


        fun openSoftKeyboard(context: Context, view: View) {
            view.requestFocus()
            // open the soft keyboard
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }

        fun showKeyboard() {
            val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        override fun onSelectedItem(
            name: String?,
            model: InventoryTypeSelectionModel?, position: Int?, isCallSaveAPI: Boolean, isChidItem: Boolean,childDescription: String) {
            if (!name.isNullOrEmpty() && model != null) {
                inventoryTypeSelectionPosition = position ?: -1
                manageOnSelectionCode(name, model, isCallSaveAPI,isChidItem,childDescription,"")
                viewModel.updateVolumeCount(selectedData!!.surveyId, sequenceId)
            } else {
                Log.e("Name or Model Null", "Name or Model Null")
                inventoryTypeSelectionPosition = -1
            }
            isAddItem = false
            inventoryBinding.btnApproveAdmin.isChecked = false
            viewModel.itemEnabled.set(isAddItem)
            inventoryBinding.llActionButtons.visibility = View.GONE
        }

        @SuppressLint("LongLogTag")
        override fun onLongClickDeSelectedItem(name: String?, model: InventoryTypeSelectionModel?, position: Int?) {
            val sequenceInventoryItem: SequenceDetailsModel? = viewModel.getSurveyInventoryItem(mContext, selectedSequence, selectedData?.surveyId.toString(), selectedFloor, selectedRoom, model?.inventoryTypeId.toString(), model?.name.toString(), selectedLeg, if (model?.isCustomize == true) "" else model?.inventoryItemId.toString())
            inventoryTypeSelectionPosition = position ?: -1
            isCustom = model?.isCustomize == true

            if (sequenceInventoryItem != null) {
                Log.e("IsUserAdd",sequenceInventoryItem!!.isUserAdded.toString())
                Log.e("sequenceInventoryItemId: ", sequenceInventoryItem.surveyInventoryId)
                viewModel.deleteSequenceInventoryItem(
                    sequenceInventoryItem.surveyInventoryId,
                    mContext,
                    selectedData?.surveyId.toString(),
                    sequenceInventoryItem.inventoryItemName!!
                )

            } else {
                Toast.makeText(mContext, "Error in delete in data, try again!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        override fun onCountIncreaseSelectedItem(model: InventoryTypeSelectionModel?, position: Int?) {
            CoroutineScope(Dispatchers.Main).launch {
                var count : Int  =  inventoryBinding.spnCount.text.toString().toInt()
                count += 1
                inventoryBinding.spnCount.setText(count.toString())
                manageSaveBtnClick(true,false,"","")
            }

        }

        override fun onAddInventoryAddSubItem(name: String?, model: InventoryTypeSelectionModel?, position: Int?, description: String?, count: String?) {
            if (!name.isNullOrEmpty() && model != null) {
                inventoryTypeSelectionPosition = position ?: -1
                inventoryBinding.edtItemName.setText(name)
                manageOnSelectionCode(name, model, true,true,description!!,count!!)
            } else {
                Log.e("Name or Model Null", "Name or Model Null")
                inventoryTypeSelectionPosition = -1
            }
        }

        override fun onChildItemDelete() {
            resetValues()
            viewPagerAdapter?.updateSelectedInventoryPosition(inventoryBinding.viewPager.currentItem)
            viewModel.updateVolumeCount(selectedData!!.surveyId, sequenceId)
        }

        override fun onParentDelete(inventoryId: String?, position: Int?, name: String?) {
            inventoryTypeSelectionPosition = position ?: -1
            isCustom = model?.isCustomize == true
            viewModel.deleteSequenceInventoryItem(inventoryId.toString(), mContext, AppConstants.surveyID,name!!)
        }

        override fun onChildUpdate(name: String?, count: String?) {
             if (name.equals(edtItemName.text.toString())) {
                 inventoryBinding.spnCount.setText(count)
             }

        }
    }

    private fun manageOnSelectionCode(name: String?, model: InventoryTypeSelectionModel?, isCallSaveAPI: Boolean, isChildItem: Boolean, description: String, count:String) {
        // TODO Jaimit Manage Click event of Item here from Item List
        currentInventoryTypeSelection = model
        viewModel.itemEnabled.set(name!!.isEmpty())
        viewModel.isEditable.set(true)
        this.isChildItem = isChildItem;
        this.childItemDescription = description

        if (name.isNotEmpty()) inventoryBinding.btnApproveAdmin.isChecked = false
        viewModel.itemName.set(name)
        if (model?.packingType != null) {
            viewModel.isPackingType.set(model.packingType)
        } else {
            viewModel.isPackingType.set(0)
        }
        if (model?.volume != null) {
            viewModel.volume.set(model.volume.toString())
        } else {
            viewModel.volume.set(0.toString())
        }

        if (model?.inventoryValue != null) {
            viewModel.itemValue.set(model.inventoryValue.toString())
        } else {
            viewModel.itemValue.set(0.toString())
        }

        crateHeight = if (model?.height != null) {
            model.height.toString()
        } else {
            ""
        }
        crateWidth = if (model?.width != null) {
            model.width.toString()
        } else {
            ""
        }
        crateLength = if (model?.length != null) {
            model.length.toString()
        } else {
            ""
        }

        //todo Pranav
        createMeasurementType = if (model?.measurementType != null) {
            Utility.getMeasurementTypeName(model.measurementType ?: "", requireActivity())
        } else {
            ""
        }

        // For Get Selected Item Image
        viewModel.getSelectedItemImage(name, inventoryBinding.spnSequence.selectedItem.toString())


        imgFile = null
        imageName = ""
        if (this.model != null) this.model?.image = null

        miscComment = ""
        damageComment = ""

        onButtonDeselect(inventoryBinding.btnCrate)
        onButtonDeselect(inventoryBinding.btnMisc)
        onButtonDeselect(inventoryBinding.btnDamage)

        isCustom = model?.isCustomize == true

        crateTypeId = 0;

        if (!TextUtils.isEmpty(model?.surveyInventoryID)) {
            currentSequenceDetail = viewModel.getSurveyDetailByInventoryItem(
                mContext, model?.surveyInventoryID.toString()
            )

            viewModel.volume.set(currentSequenceDetail?.volume ?: "0.0")
            inventoryBinding.spnCount.setText(currentSequenceDetail?.count ?: "1")
            inventoryBinding.edtInputVolume.setText(currentSequenceDetail?.volume ?: "20.0")
            inventoryBinding.edtItemValue.setText(currentSequenceDetail?.inventoryValue ?: "0.0")
            val splitDimentation = (currentSequenceDetail?.dimenstion?.split("x"))?.toTypedArray()
            Log.e("splitDimentation: ", splitDimentation.toString())
            isCrate = currentSequenceDetail?.isCrate ?: false

            Log.e("IsCreate", isCrate.toString())
            if (splitDimentation != null && splitDimentation.size == 3) {
                if (crateLength == splitDimentation[0].trim() && crateWidth == splitDimentation[1].trim() && crateHeight == splitDimentation[2].trim()) {
                    Log.e("Default Dimentions", "Default Dimentions")
                } else {
                    crateLength = splitDimentation[1].trim()
                    crateWidth = splitDimentation[2].trim()
                    crateHeight = splitDimentation[0].trim()

                    onButtonChanged(inventoryBinding.btnCrate)
                }
            } else {
                onButtonDeselect(inventoryBinding.btnCrate)
            }

            if (isCrate){
                onButtonChanged(inventoryBinding.btnCrate)
            }
            else{
                onButtonDeselect(
                    inventoryBinding.btnCrate
                )
            }

            miscComment = currentSequenceDetail?.miscComment ?: ""
            if (miscComment.isNotEmpty()) onButtonChanged(inventoryBinding.btnMisc) else onButtonDeselect(
                inventoryBinding.btnMisc
            )


            damageComment = currentSequenceDetail?.damageComment ?: ""
            if (damageComment.isNotEmpty()) onButtonChanged(inventoryBinding.btnDamage) else onButtonDeselect(
                inventoryBinding.btnDamage
            )

        } else {
            currentSequenceDetail = null
            inventoryBinding.spnCount.setText("1")
            inventoryBinding.edtInputVolume.setText(model?.volume.toString())
            isCrate = false
        }

        if (currentSequenceDetail != null) {
            updateCheckBox(currentSequenceDetail)
            surveyInventoryID = model?.surveyInventoryID.toString()
        } else {
            surveyInventoryID = "0"
            deSelectAllCheckBoxes()
        }

        // API will call to Save Item
        if (isCallSaveAPI) {
            manageSaveBtnClick(
                model?.inventoryTypeId.toString(), model?.inventoryItemId.toString(),false,isChildItem,description,count,false,""
            )
        }

    }


    private fun updateCheckBox(sequenceInventoryItem: SequenceDetailsModel?) {
        isDismantleSelected = sequenceInventoryItem?.isDismantle ?: false
        isCardSelected = sequenceInventoryItem?.isCard ?: false
        isBubbleWrapSelected = sequenceInventoryItem?.isBubbleWrap ?: false
        isRemainsSelected = sequenceInventoryItem?.isRemain ?: false
        isFullExportWrapSelected = sequenceInventoryItem?.isFullExportWrap ?: false

        inventoryBinding.cbDismantle.tag = false
        inventoryBinding.cbDismantle.isChecked = sequenceInventoryItem?.isDismantle ?: false
        inventoryBinding.cbDismantle.tag = true

        inventoryBinding.cbCard.tag = false
        inventoryBinding.cbCard.isChecked = sequenceInventoryItem?.isCard ?: false
        inventoryBinding.cbCard.tag = true

        inventoryBinding.cbBubbleWrap.tag = false
        inventoryBinding.cbBubbleWrap.isChecked = sequenceInventoryItem?.isBubbleWrap ?: false
        inventoryBinding.cbBubbleWrap.tag = true

        inventoryBinding.cbRemains.tag = false
        inventoryBinding.cbRemains.isChecked = sequenceInventoryItem?.isRemain ?: false
        inventoryBinding.cbRemains.tag = true

        inventoryBinding.cbFullExportWrap.tag = false
        inventoryBinding.cbFullExportWrap.isChecked =
            sequenceInventoryItem?.isFullExportWrap ?: false
        inventoryBinding.cbFullExportWrap.tag = true
    }

    private fun deSelectAllCheckBoxes() {
        inventoryBinding.cbDismantle.tag = false
        inventoryBinding.cbDismantle.isChecked = false
        inventoryBinding.cbDismantle.tag = true

        inventoryBinding.cbCard.tag = false
        inventoryBinding.cbCard.isChecked = false
        inventoryBinding.cbCard.tag = true

        inventoryBinding.cbBubbleWrap.tag = false
        inventoryBinding.cbBubbleWrap.isChecked = false
        inventoryBinding.cbBubbleWrap.tag = true

        inventoryBinding.cbRemains.tag = false
        inventoryBinding.cbRemains.isChecked = false
        inventoryBinding.cbRemains.tag = true

        inventoryBinding.cbFullExportWrap.tag = false
        inventoryBinding.cbFullExportWrap.isChecked = false
        inventoryBinding.cbFullExportWrap.tag = true

        isDismantleSelected = false
        isCardSelected = false
        isBubbleWrapSelected = false
        isRemainsSelected = false
        isFullExportWrapSelected = false
    }

    private fun setStatePageAdapter() {
        viewPagerAdapter = InventoryViewPagerAdapter(childFragmentManager, 0)
        for (list in data!!) {
            filterInventoryTypeSelection = FragmentInventoryTypeSelection.newInstance(
                list.inventoryTypeId.toString(),
                selectedData?.surveyId.toString()
            )

            filterInventoryTypeSelection!!.setItemNameListener(onInventorySelection)
            viewPagerAdapter?.addFragment(filterInventoryTypeSelection!!, list.name ?: "")
        }
        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)
        viewPager.currentItem = 0
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            var currentPosition = 0
            override fun onPageSelected(newPosition: Int) {
                Log.e("Page", "Selected")
                //viewPager.reMeasureCurrentPage(wrapContentViewPager.getCurrentItem());
                data!![currentPosition].inventoryTypeId?.let { viewPagerAdapter?.setSearchData(currentPosition,it) }
                val fragmentToHide: FragmentLifecycleInterface =
                    viewPagerAdapter!!.getItem(currentPosition) as FragmentLifecycleInterface
                fragmentToHide.onPauseFragment()
                val fragmentToShow: FragmentLifecycleInterface =
                    viewPagerAdapter!!.getItem(newPosition) as FragmentLifecycleInterface
                fragmentToShow.onResumeFragment(null)
                currentPosition = newPosition
                selectedTypeId = newPosition
                viewModel.itemEnabled.set(false)
                viewModel.itemName.set("")
                viewModel.isPackingType.set(0)
                inventoryBinding.imgCamera.setColorFilter(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.gray_323232
                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                );
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
                Log.e("Page", "Scrolled")
            }

            override fun onPageScrollStateChanged(arg0: Int) {
                Log.e("Page", "Changed")
            }
        })

    }

    fun onButtonChanged(button: RadioButton) {
        isOneOptionSelected = true
        button.setBackgroundResource(R.drawable.bg_rect_orange)
        button.setTextColor(ContextCompat.getColor(requireActivity(), android.R.color.white))
    }

    private fun onButtonDeselect(button: RadioButton) {
        isOneOptionSelected = false
        button.setBackgroundResource(R.drawable.bg_rect_grey)
        button.setTextColor(ContextCompat.getColor(requireActivity(), android.R.color.black))
    }

    private fun checkImagePickerPermission() {
        Dexter.withActivity(requireActivity())
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        openImagePickerDialog()
                    } else {
                        Utility.showSettingsDialog(requireActivity())
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        Utility.showSettingsDialog(requireActivity())
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).withErrorListener { }.onSameThread().check()
    }

    private fun openImagePickerDialog() {
        ImagePickerDialog(requireActivity(), object : onItemClick {
            override fun onCameraClicked() {
                displayCamera()
            }

            override fun onGalleryClicked() {
                val pickPhoto =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, gallery)
            }
        }).show()

    }

    fun displayCamera() {
        val destPath: String? = Objects.requireNonNull(
            Objects.requireNonNull(requireActivity()).getExternalFilesDir(null)
        )?.absolutePath
        val imagesFolder = File(destPath, this.resources.getString(R.string.app_name))
        try {
            imagesFolder.mkdirs()
            imgFile = File(imagesFolder, Date().time.toString() + ".jpg")
            imagePath = FileProvider.getUriForFile(
                requireActivity(),
                com.pickfords.surveyorapp.BuildConfig.APPLICATION_ID  + ".fileProvider",
                imgFile!!
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath)
            startActivityForResult(intent, camera)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == camera && resultCode == Activity.RESULT_OK) {
            uploadImage()
        }
        if (requestCode == gallery && resultCode == Activity.RESULT_OK) {
            imagePath = Objects.requireNonNull(data)?.data
            imgFile = File(Objects.requireNonNull(imagePath?.let {
                Utility.getPath(
                    requireActivity(), it
                )
            }))
            uploadImage()
        }
    }

    private fun uploadImage() {
        if (imgFile != null) {
            val filePath: String = imgFile!!.path
            val bitmap = BitmapFactory.decodeFile(filePath)
            manageSaveBtnClick(true,false,"","")
        }

    }

    override fun onPauseFragment() {

    }

    @SuppressLint("LongLogTag")
    override fun onResumeFragment(s: String?) {

        Log.e("OnResumeSelectedFloor: ", selectedFloor)
        Log.e("OnResumeSelectedRoom: ", selectedRoom)
        Log.e("OnResumeSelectedSequence: ", selectedSequence)
        Log.e("OnResumeSelectedLeg: ", selectedLeg)


        isRoomAdd = false;
        viewModel.init(mContext, Integer.parseInt(session.user!!.userId))

        inventoryBinding.lifecycleOwner = this
        inventoryBinding.viewModel = viewModel


        inventoryBinding.viewPager.currentItem = 0


        inventoryBinding.imgCamera.setColorFilter(
            ContextCompat.getColor(
                requireActivity(),
                R.color.gray_323232
            ), android.graphics.PorterDuff.Mode.MULTIPLY
        );

        if (arguments != null && requireArguments().containsKey(Session.DATA) && requireArguments().get(
                Session.DATA
            ) != null
        ) {
            selectedData = requireArguments().getSerializable(Session.DATA) as DashboardModel?
            inventoryBinding.mainData = selectedData
        }
        viewModel.getSequenceList(selectedData?.surveyId.toString(), mContext)

        viewModel.updateVolumeCount(selectedData?.surveyId.toString(),sequenceId)

        hideSoftKeyboard()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        viewModel.getLegList()

        // For select sequence code
        if((context as DashboardActivity).setSelectedSequencePosition() > 0){
            var selectedPosition =  ((context as DashboardActivity).setSelectedSequencePosition())
            viewModel.selectedItemPositionSequence.postValue(selectedPosition)
            viewModel.selectedItemPosition = selectedPosition
            viewModel.selectedPosition.postValue(selectedPosition)
        }

    }

    fun resetValues() {

        inventoryBinding.edtItemName.text?.clear()
        inventoryBinding.spnCount.setText("1")
        inventoryBinding.edtInputVolume.setText("0.0")
        inventoryBinding.edtItemValue.setText("0.0")

        surveyInventoryID = "0"
        deSelectAllCheckBoxes()

        isCrate = false
        crateHeight = ""
        crateWidth = ""
        crateLength = ""
        miscComment = ""
        createMeasurementType = ""
        damageComment = ""

        onButtonDeselect(inventoryBinding.btnCrate)
        onButtonDeselect(inventoryBinding.btnMisc)
        onButtonDeselect(inventoryBinding.btnDamage)

        isCustom = false

        imgFile = null
        imageName = ""
        if (this.model != null) this.model?.image = null

        currentSequenceDetail = null
        currentInventoryTypeSelection = null
    }

}