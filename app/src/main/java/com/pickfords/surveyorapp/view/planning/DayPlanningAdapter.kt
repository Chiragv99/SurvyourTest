package com.pickfords.surveyorapp.view.planning

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.model.planning.CodeDetails
import com.pickfords.surveyorapp.model.planning.TimeModel
import com.pickfords.surveyorapp.model.planning.getPlanningAddress
import com.pickfords.surveyorapp.model.surveyDetails.ActivityListPlanningModel
import com.pickfords.surveyorapp.model.surveyDetails.CodePlanningModel
import com.pickfords.surveyorapp.model.surveyDetails.SurveyPlanningListModel
import com.pickfords.surveyorapp.room.DeletePlanningData
import com.pickfords.surveyorapp.utils.AppConstants
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import com.pickfords.surveyorapp.viewModel.surveyDetails.PlanningViewModel
import kotlinx.android.synthetic.main.item_add_day_planing.view.*


class DayPlanningAdapter
    (
    private var timeLiveList: MutableLiveData<List<TimeModel>>,
    private var codeDetails: MutableList<CodeDetails>,
    address: MutableList<getPlanningAddress>,
    activity: MutableLiveData<List<ActivityListPlanningModel>>,
    code: MutableLiveData<List<CodePlanningModel?>>,
    private val mContext: Context,
    list: ArrayList<SurveyPlanningListModel.Option.Day>?,
    listener: OnItemClickListener,
    val viewModel: PlanningViewModel,
    var isAtFirstPos: Boolean,
    var isAtFirstTime: Boolean,
    var surveyPlanningListModel: SurveyPlanningListModel

) : RecyclerView.Adapter<DayPlanningAdapter.ViewHolder3>() {

    private var list: ArrayList<SurveyPlanningListModel.Option.Day> = list!!
    private var listener: OnItemClickListener = listener
    private var isStaticData = false
    private var addressList: MutableList<getPlanningAddress> = address
    private var activityMainList: MutableLiveData<List<ActivityListPlanningModel>> = activity
    private var activityList: MutableList<String?> = ArrayList()
    private var codeList: MutableLiveData<List<CodePlanningModel?>> = code
    private var tempCodeList: MutableList<String?> = ArrayList()
    private var tempSetCodeList: MutableList<String?> = ArrayList()
    private var timeList: MutableList<String?> = ArrayList()
    var spinnerTouched = false


    inner class ViewHolder3(view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("ResourceType")
        fun bind(
            timeList: MutableList<String?>,
            mContext: Context,
            list: ArrayList<SurveyPlanningListModel.Option.Day>,
            listener: OnItemClickListener,
            addressList: MutableList<getPlanningAddress>,
            activity: MutableList<String?>,
            code: MutableLiveData<List<CodePlanningModel?>>,
            tempCodeList: MutableList<String?>,
            tempSetCodeList: MutableList<String?>, //            position: Int,
            isAtFirstPos: Boolean) {


             isAtFirstTime = true

            //set day count
            val etAddDayCount: AppCompatEditText =
                itemView.etAddDayCount.findViewById(R.id.etAddDayCount) as AppCompatEditText

            itemView.etAddDayCount.setText(
                if (list[adapterPosition].optionDay != null) list[adapterPosition].optionDay.toString() else (adapterPosition + 1).toString()
            )

            //if option day is null than add value in model
            if (list[adapterPosition].optionDay == null || list[adapterPosition].optionDay == 0)
                list[adapterPosition].optionDay = adapterPosition + 1


            // add activity name in list
            if (activityMainList.value != null && activityMainList.value?.size!! > 0) {
                activity.clear()
                activity.add("Select Activity")
                for (i in 0 until activityMainList.value?.size!!) {
                    activity.add(activityMainList.value?.get(i)?.activity.toString())
                }
            }
            // add activity name in list
            if (timeLiveList.value != null && timeLiveList.value?.size!! > 0) {
                timeList.clear()
                for (i in 0 until timeLiveList.value?.size!!) {

                    if (timeLiveList.value?.get(i)?.activityCode == 0) {
                        timeList.add("Select Time")
                    } else {
                        timeList.add(timeLiveList.value?.get(i)?.activityDescription.toString())
                    }
                }
            }
            itemView.tvAddDay.setOnClickListener {
                itemView.etAddDayCount.requestFocus()

                val imm =
                    mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(itemView.etAddDayCount, InputMethodManager.SHOW_IMPLICIT)
                if (itemView.etAddDayCount.text?.isNotEmpty() == true)
                    itemView.etAddDayCount.setSelection(itemView.etAddDayCount.text?.length!!)
            }
            itemView.edTime?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    list[adapterPosition].time = itemView.edTime?.text.toString()
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            itemView.edNotes.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    list[adapterPosition].notes = itemView.edNotes.text.toString()
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            itemView.spnAddress.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        Log.e("TempAddress",(addressList[position].addressId) + " " + position)
                        if (addressList[position].addressId.contains("ADD")) {
                            list[adapterPosition].addressId = 0
                            list[adapterPosition].tempAddressId = addressList[position].addressId
                        } else {
                            list[adapterPosition].addressId =
                                addressList[position].addressId.toInt()
                            list[adapterPosition].tempAddressId = addressList[position].addressId
                        }
                        list[adapterPosition].address = addressList[position].address
                        Log.e("TempAddress",list[adapterPosition].tempAddressId)
                    }
                }


            // set Adapter
            setSpinnerAdapterAddress(mContext, addressList)
            // set Adapter
            setSpinnerAdapterActivity(mContext, activity)  // set Adapter
            setSpinnerTime(mContext, timeList)

            setSpinnerTitle(itemView)

            /**
             * select activity and set code
             *  */

            itemView.spnActivity.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (position > 0) {
                            list[adapterPosition].activityId =
                                activityMainList.value?.get(position - 1)?.activityId
                            list[adapterPosition].activity =
                                activityMainList.value?.get(position - 1)?.activity

                        } else {
                            list[adapterPosition].activityId =
                                activityMainList.value?.get(position)?.activityId
                            list[adapterPosition].activity =
                                activityMainList.value?.get(position)?.activity
                        }
                        var cList: ArrayList<String> = ArrayList()
                        cList.add("Select Code")
                        cList.addAll((mContext as BaseActivity).codeListDao.getCodeByActivityId(list[adapterPosition].activityId.toString()))
                        setSpinnerAdapterCode(mContext, cList)

                        if (getSelectedCode(cList, list) != -1) {
                            getSelectedCode(cList, list).let { itemView.spnCode.setSelection(it) }
                        }
                    }
                }


            itemView.edNotes.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Clear focus here from edittext
                    itemView.edNotes.clearFocus()
                }
                false
            }
            itemView.etAddDayCount.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Clear focus here from edittext
                    itemView.edNotes.clearFocus()
                }
                false
            }
            itemView.edNoDriver.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Clear focus here from edittext
                    itemView.edNoDriver.clearFocus()
                }
                false
            }
            itemView.edNoPackars.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Clear focus here from edittext
                    itemView.edNoPackars.clearFocus()
                }
                false
            }

            itemView.spnCode.setOnTouchListener(OnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                     spinnerTouched = true
                }
                false
            })

            itemView.spnCode.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (parent?.selectedItem.toString().isNotEmpty())
                            list[adapterPosition].code = getCodePosition(code, parent?.selectedItem.toString())?.discription

                         if (spinnerTouched){
                             itemView.edNotes.setText(getCodePosition(code, parent?.selectedItem.toString())?.discription)
                         }
                    }
                }

            itemView.spnTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (parent?.selectedItem.toString().isNotEmpty())
                        list[adapterPosition].time = parent?.selectedItem.toString()

                    list[adapterPosition].time =
                        timeLiveList.value?.get(position)?.activityCode.toString()


                }
            }

            itemView.edNotes.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    list[adapterPosition].notes = itemView.edNotes.text.toString()
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })
            itemView.etAddDayCount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    list[adapterPosition].optionDay = if (itemView.etAddDayCount.text.toString()
                            .isNotEmpty()
                    ) itemView.etAddDayCount.text.toString().toInt() else 0
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            itemView.edNoDriver.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    list[adapterPosition].aMPorter = itemView.edNoDriver.text.toString()
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            itemView.edNoPackars.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    list[adapterPosition].aMDriver = itemView.edNoPackars.text.toString()
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            itemView.isCheck?.setOnCheckedChangeListener { buttonView, isChecked ->
                list[adapterPosition].tracking = isChecked
            }


            itemView.edTime?.setText(list[adapterPosition].time)
            itemView.edNotes.setText(list[adapterPosition].notes)
            itemView.edNoDriver.setText(list[adapterPosition].aMPorter.toString())
            itemView.edNoPackars.setText(list[adapterPosition].aMDriver.toString())


            itemView.isCheck?.isChecked = list[adapterPosition].tracking == true


            val indexAddress = getAddressPosition(addressList, list)
            if (indexAddress != -1) {
                itemView.spnAddress.setSelection(indexAddress)
            }

            if (getActivityPosition(activity, list) != -1) {
                itemView.spnActivity.setSelection(getActivityPosition(activity, list))
            }
            /*For time*/
            if (getTimePosition(list, timeLiveList) != -1) {
                getTimePosition(list, timeLiveList)?.let { itemView.spnTime.setSelection(it) }
            }


            if (adapterPosition == list.size - 1)
                itemView.btnAddDay.visibility = View.VISIBLE
            else
                itemView.btnAddDay.visibility = View.GONE

            val btnAddDay: ImageView =
                itemView.findViewById(R.id.btnAddDay) as ImageView

            val btnRemoveDay: ImageView =
                itemView.findViewById(R.id.btnRemoveDay) as ImageView

            itemView.btnRemoveDay.setOnClickListener {
                if (!AppConstants.revisitPlanning)
                    if (list.size > 1) {
                        MessageDialog(
                            mContext,
                            mContext.getString(R.string.are_you_sure_delete_day)
                        )
                            .setListener(object : MessageDialog.OkButtonListener {
                                override fun onOkPressed(dialog: MessageDialog) {
                                    listener.onRemoveClick(adapterPosition)
                                    dialog.dismiss()
                                }

                            })
                            .setCancelButton(true)
                            .show()
                    } else if (list.size == 1 && isAtFirstPos) {
                        Toast.makeText(
                            mContext,
                            "Please keep single option and day",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (list.size == 1 && !isAtFirstPos) {
                        Toast.makeText(mContext, "Please remove whole planning", Toast.LENGTH_SHORT)
                            .show()
                    }
            }

            itemView.btnAddDay.setOnClickListener() {
                if (!AppConstants.revisitPlanning) {
                    saveData()
                }
            }


        }

        // For Save Data
        private fun saveData() {

            var optioncount = 0

            var daycount = 0

            var allValidData = true

            val data = viewModel.getPlanningAdapter()?.getData()
            for (item in data?.indices!!) {
                data[item].optionId = item + 1
                optioncount += 1
                daycount = 0
                var isBreak = false

                for (dayModel in data[item].days!!) {
                    daycount += 1
                    val dayCount =
                        if (dayModel.optionDay == null || dayModel.optionDay == 0) daycount else dayModel.optionDay
                    if (dayModel.optionDay == null || dayModel.optionDay == 0) {
                        MessageDialog(
                            itemView.context,
                            "Please enter Day Count in Day $dayCount"
                        ).show()
                        allValidData = false
                        break
                    } else if (dayModel.time!!.isEmpty()) {
                        MessageDialog(
                            itemView.context,
                            "Please enter the details in Time in Day $dayCount"
                        ).show()
                        allValidData = false
                        break
                    } else if (dayModel.address.toString().contains("Select")) {
                        MessageDialog(
                            itemView.context,
                            "Please select Address in Day $dayCount"
                        ).show()
                        allValidData = false
                        break
                    } else if (dayModel.activityId == 0) {
                        MessageDialog(
                            itemView.context,
                            "Please select Activity in Day $dayCount"
                        ).show()
                        allValidData = false
                        break
                    } else if (dayModel.code == null || dayModel.code?.isEmpty() == true) {
                        MessageDialog(
                            itemView.context,
                            "Please select Code in Day $dayCount"
                        ).show()
                        allValidData = false
                        break
                    } else if (dayModel.notes!! == "0") {
                        MessageDialog(
                            itemView.context,
                            "Please enter the Notes in Day $dayCount"
                        ).show()
                        allValidData = false
                        break
                    } else if (dayModel.aMDriver!!.isEmpty()) {
                        MessageDialog(
                            itemView.context,
                            "Please enter the details in No. of Driver in Day $dayCount"
                        ).show()
                        allValidData = false
                        break
                    } else if (dayModel.aMPorter!! == null || dayModel.aMPorter!!.isEmpty()) {
                        MessageDialog(
                            itemView.context,
                            "Please enter the details in No. of Porter in Day $dayCount"
                        ).show()
                        allValidData = false
                        break
                    } else {
                        Log.d("addressId", dayModel.addressId.toString())
                    }
                }
            }
            if (allValidData) {
                val modelData = SurveyPlanningListModel()
                modelData.options = data
                modelData.sequenceId = viewModel.surveySequenceId
                // modelData.surveySequence = viewModel.surveySequenceName
                modelData.sequenceNo = viewModel.surveySequenceName
                //val gson =
                val json = Gson().toJson(modelData)
                Log.e("request data", json.toString())
                listener.onItemClick(list[adapterPosition], adapterPosition)
                itemView.btnAddDay.visibility = View.GONE
            }

        }

        // For Address position
        private fun getAddressPosition(
            addressList: MutableList<getPlanningAddress>,
            list: ArrayList<SurveyPlanningListModel.Option.Day>
        ): Int {
            return if (list[adapterPosition].addressId.toString() == "0"){
                addressList.indexOfFirst { it.addressId == list[adapterPosition].tempAddressId.toString() }
            } else{
                addressList.indexOfFirst { it.addressId == list[adapterPosition].addressId.toString() }
            }
        }

        private fun getSelectedCode(tempCodeList: List<String?>, list: ArrayList<SurveyPlanningListModel.Option.Day>): Int {
            return tempCodeList.indexOfFirst { it == list[adapterPosition].code.toString() }
        }

        private fun getCodePosition(code: MutableLiveData<List<CodePlanningModel?>>, description: String): CodePlanningModel? {
            return code.value?.find { it?.discription.equals(description) }
        }

        /*  private fun getTimePosition(
              activityList: MutableList<String?>,
              list: ArrayList<SurveyPlanningListModel.Option.Day>
          ): Int {
              return if (adapterPosition < list.size)
                  activityList.indexOfFirst { it == list[adapterPosition].code.toString() }
              else
                  -1

          }*/

        private fun getActivityPosition(
            activityList: MutableList<String?>,
            list: ArrayList<SurveyPlanningListModel.Option.Day>
        ): Int {
            if (adapterPosition < list.size)
                return activityList.indexOfFirst { it == list[adapterPosition].activity.toString() }
            else
                return -1

        }

        private fun getTimePosition(
            list: ArrayList<SurveyPlanningListModel.Option.Day>,
            timeList: MutableLiveData<List<TimeModel>>
        ): Int? {
            // val timeList = mContext.resources.getStringArray(R.array.timeDetails)
            return timeList.value?.indexOfFirst { it.activityCode.toString() == list[adapterPosition].time }
        }

        private fun setSpinnerAdapterAddress(
            mContext: Context,
            addressList: MutableList<getPlanningAddress>
        ) {
            val address: ArrayList<String> = ArrayList()

            for (i in addressList) {
                address.add(i.address)
            }

            val sequenceGroupSpinnerAdapter =
                ArrayAdapter(mContext, android.R.layout.simple_spinner_item, address)
            sequenceGroupSpinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_item)

            itemView.spnAddress.adapter = sequenceGroupSpinnerAdapter;
        }

        private fun setSpinnerAdapterActivity(mContext: Context, activity: MutableList<String?>) {
            val sequenceGroupSpinnerAdapter =
                ArrayAdapter(mContext, android.R.layout.simple_spinner_item, activity)
            sequenceGroupSpinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_item)
            itemView.spnActivity.adapter = sequenceGroupSpinnerAdapter;
        }

        private fun setSpinnerTime(mContext: Context, timeDesc: MutableList<String?>) {
            val sequenceGroupSpinnerAdapter =
                ArrayAdapter(mContext, android.R.layout.simple_spinner_item, timeDesc)
            sequenceGroupSpinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_item)
            itemView.spnTime.adapter = sequenceGroupSpinnerAdapter
        }

        private fun setSpinnerAdapterCode(mContext: Context, code: List<String>) {
            if (code.isNotEmpty()) {
                val sequenceGroupSpinnerAdapter =
                    ArrayAdapter<String?>(mContext, android.R.layout.simple_spinner_item, code)
                sequenceGroupSpinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_item)
                itemView.spnCode.adapter = sequenceGroupSpinnerAdapter
            }
        }
    }

    // For Set Search Spinner Title
    private fun setSpinnerTitle(itemView: View) {
        itemView.spnTime.setTitle("Select Time")
        itemView.spnAddress.setTitle("")
        itemView.spnActivity.setTitle("Select Activity")
        itemView.spnCode.setTitle("Select Code")
    }


    interface OnItemClickListener {

        fun onItemClick(item: SurveyPlanningListModel.Option.Day, position: Int)
        fun onRemoveClick(position: Int)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder3 {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add_day_planing, parent, false)
        return ViewHolder3(view)

    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DayPlanningAdapter.ViewHolder3, position: Int) {
        holder.bind(
            timeList,
            mContext,
            list,
            listener,
            addressList,
            activityList,
            codeList,
            tempCodeList, tempSetCodeList,
            isAtFirstPos
        )

    }

    fun addDay() {

        list.add(
            SurveyPlanningListModel.Option.Day(
                0,
                0,
                list.size + 1,
                "1",
                "1",
                "1",
                "1",
                "1",
                "1",
                "",
                false,
                0,
                "",
                0,
                "",
                "",
                "",
                true
            )
        )
    }

    fun changeValue(isFirstOption: Boolean) {
        isAtFirstPos = isFirstOption
    }

    // For Remove Planning Day
    fun removeDay(
        position: Int,
        listOption: MutableList<SurveyPlanningListModel.Option>,
        positionOption: Int
    ) {
        if (this.list[position].isStaticData == true) {
            this.list.removeAt(position)
        } else {
            var deletePlanningData: DeletePlanningData = DeletePlanningData()
            deletePlanningData.surveyPlanningId = this.list[position].surveyPlanningDetailId
            deletePlanningData.IsDelete = true
            deletePlanningData.surveyId =   viewModel.selectedData!!.surveyId.toInt()

            (mContext as BaseActivity).deletePlanningDataDao.insert(deletePlanningData)
            this.list.removeAt(position)


//            if (!Utility.isNetworkConnected(mContext)) {
//                this.list.removeAt(position)
//            } else {
//                viewModel.deleteSurveyPlanDetail(
//                    mContext,
//                    this.list[position].surveyPlanningDetailId.toString(),
//                    position
//                )
//                this.list.removeAt(position)
//            }
        }
        notifyItemRemoved(position)
        // if (this.list.size > 0)
        notifyItemChanged(this.list.size - 1)
    }
}