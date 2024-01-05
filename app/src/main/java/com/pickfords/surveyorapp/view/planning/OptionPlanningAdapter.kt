package com.pickfords.surveyorapp.view.planning

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.Time
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.model.planning.CodeDetails
import com.pickfords.surveyorapp.model.planning.TimeModel
import com.pickfords.surveyorapp.model.planning.getPlanningAddress
import com.pickfords.surveyorapp.model.surveyDetails.ActivityListPlanningModel
import com.pickfords.surveyorapp.model.surveyDetails.CodePlanningModel
import com.pickfords.surveyorapp.model.surveyDetails.SurveyPlanningListModel
import com.pickfords.surveyorapp.utils.Utility
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import com.pickfords.surveyorapp.viewModel.surveyDetails.PlanningViewModel

class OptionPlanningAdapter(
    private val timeLiveList: MutableLiveData<List<TimeModel>>,
    codeDetails: MutableList<CodeDetails>,
    val mContext: Context,
    addressList: MutableList<getPlanningAddress>,
    activityList: MutableLiveData<List<ActivityListPlanningModel>>,
    private val codeList: MutableLiveData<List<CodePlanningModel?>>,
    private var listener: OnItemClickListener,
    private val newList: MutableList<SurveyPlanningListModel.Option>,
    val viewModel: PlanningViewModel,
    private var isAtFirstPosition: Boolean
) : RecyclerView.Adapter<OptionPlanningAdapter.ViewHolder1>() {
    private var surveyPlanningListModel = SurveyPlanningListModel()
    var list = ArrayList<AddOptionPlanningModel>()

    var codeDetails: MutableList<CodeDetails> = codeDetails
    var address: MutableList<getPlanningAddress> = addressList
    var activity: MutableLiveData<List<ActivityListPlanningModel>> = activityList

    interface OnItemClickListener {

        fun onItemClick(position: Int, )

        fun onRemoveClick(
            position: Int,
        )
    }

    class ViewHolder1(view: View) : RecyclerView.ViewHolder(view) {

        private val recyclerViewAddDay: RecyclerView =
            view.findViewById(R.id.recyclerViewAddDay) as RecyclerView
        val btnAddOption: Button =
            view.findViewById(R.id.btnAddOption) as Button
        val btnRemoveOption: Button =
            view.findViewById(R.id.btnRemoveOption) as Button
        private val tvAddOptionCount: TextView =
            view.findViewById(R.id.tvAddOptionCount) as TextView

        var adapter: DayPlanningAdapter? = null


        fun bind(
            timeLiveList: MutableLiveData<List<TimeModel>>,
            codeDetails: MutableList<CodeDetails>,
            context: Context,
            list: SurveyPlanningListModel.Option,
            position: Int,
            viewModel: PlanningViewModel,
            address: MutableList<getPlanningAddress>,
            activity: MutableLiveData<List<ActivityListPlanningModel>>,
            code: MutableLiveData<List<CodePlanningModel?>>,
            isAtFirstPosition: Boolean,
            surveyPlanningListModel: SurveyPlanningListModel
        ) {


            val position1: Int = position + 1
            tvAddOptionCount.text = position1.toString()
            recyclerViewAddDay.layoutManager = LinearLayoutManager(context)



            adapter = DayPlanningAdapter(timeLiveList,
                codeDetails,
                address,
                activity, code,
                context, list.days as ArrayList<SurveyPlanningListModel.Option.Day>?,
                object : DayPlanningAdapter.OnItemClickListener {

                    override fun onItemClick(
                        item: SurveyPlanningListModel.Option.Day,
                        position: Int
                    ) {
                    }

                    override fun onRemoveClick(position: Int) {
                    }
                }, viewModel, isAtFirstPosition, true ,surveyPlanningListModel
            )
            recyclerViewAddDay.adapter = adapter

        }

        fun bindAPI(timeLiveList: MutableLiveData<List<TimeModel>>, codeDetails: MutableList<CodeDetails>, context: Context, list: MutableList<SurveyPlanningListModel.Option>, position: Int, viewModel: PlanningViewModel, address: MutableList<getPlanningAddress>, activity: MutableLiveData<List<ActivityListPlanningModel>>, code: MutableLiveData<List<CodePlanningModel?>>, isAtFirstPosition: Boolean, surveyPlanningListModel: SurveyPlanningListModel) {
            val position1: Int = position + 1
            tvAddOptionCount.text = position1.toString()
            recyclerViewAddDay.layoutManager = LinearLayoutManager(context)

            adapter = DayPlanningAdapter(timeLiveList,
                codeDetails,
                address,
                activity, code,
                context,
                list[position].days as ArrayList<SurveyPlanningListModel.Option.Day>?,
                object : DayPlanningAdapter.OnItemClickListener {

                    override fun onItemClick(
                        item: SurveyPlanningListModel.Option.Day,
                        position: Int
                    ) {

                        adapter?.addDay()
                        //todo notifyDatasetChange()
                        adapter?.notifyItemChanged(position + 1)
                    }

                    override fun onRemoveClick(pos: Int) {
                        if (list.size == 1) {
                            adapter?.changeValue(true)
                        } else {
                            adapter?.changeValue(false)
                        }
                        adapter?.removeDay(pos, list, position)
                    }
                },
                viewModel,
                isAtFirstPosition,
                true,
                surveyPlanningListModel
            )
            recyclerViewAddDay.adapter = adapter

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder1 {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add_option_planning, parent, false)
        return ViewHolder1(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder1,
        @SuppressLint("RecyclerView") position: Int
    ) {
        if (newList.size != 0) {
            holder.bindAPI(
                timeLiveList,
                codeDetails,
                mContext,
                newList,
                position,
                viewModel,
                address,
                activity, codeList,
                isAtFirstPosition,
                surveyPlanningListModel
            )
        } else {
            holder.bind(
                timeLiveList,
                codeDetails,
                mContext,
                newList[position],
                position,
                viewModel,
                address,
                activity, codeList,
                isAtFirstPosition,
                surveyPlanningListModel
            )
        }
        if (position == newList.size - 1)
            holder.btnAddOption.visibility = View.GONE
        else
            holder.btnAddOption.visibility = View.GONE

        holder.btnAddOption.setOnClickListener {
            listener.onItemClick(position)
        }

        isAtFirstPosition = newList.size == 1

        holder.btnRemoveOption.setOnClickListener {
            if (newList.size > 1) {
                MessageDialog(mContext, mContext.getString(R.string.are_you_sure_delete_option))
                    .setListener(object : MessageDialog.OkButtonListener {
                        override fun onOkPressed(dialog: MessageDialog) {
                            listener.onRemoveClick(position)
                            dialog.dismiss()
                        }

                    })
                    .setCancelButton(true)
                    .show()
            } else if (newList.size == 1) {
                Toast.makeText(mContext, "Please keep single option and day", Toast.LENGTH_SHORT)
                    .show()
            }

        }

    }

    override fun getItemCount(): Int {
        return newList.size
    }

    fun addOption(position: Int) {
        val dayList = ArrayList<SurveyPlanningListModel.Option.Day>()
        dayList.add(
            SurveyPlanningListModel.Option.Day(0,
                0,
                1,
                "1",
                "1",
                "1",
                "1",
                "1",
                "1",
                " ",
                true,
                0,
                "",
                0,
                "",
                " ",
                " "
            )
        )
        newList.add(SurveyPlanningListModel.Option(position + 1, dayList))
        notifyDataSetChanged()
    }

    fun removeOption(position: Int) {
        /*      if (newList[position].optionStatic == true) {
                  newList.removeAt(position)
              } else {*/
        if (!Utility.isNetworkConnected(mContext)) {
            newList.removeAt(position)
            surveyPlanningListModel.options = newList

            if (surveyPlanningListModel.sequenceId != null) {
                val existPlanning =
                    (mContext as BaseActivity).surveyPlanningListDao.checkPlanningID(
                        surveyPlanningListModel.sequenceId!!
                    )
            }

            Utility.setOfflineTag(mContext, null)
            (mContext as BaseActivity).surveyPlanningListDao.updateRow(
                surveyPlanningListModel.options,
                surveyPlanningListModel.sequenceId!!
            )

            Toast.makeText(mContext, "Delete Successfully", Toast.LENGTH_SHORT).show()


        } else {
            newList.removeAt(position)
            surveyPlanningListModel.options = newList
            surveyPlanningListModel.isChangedField = true

            if (surveyPlanningListModel.sequenceId != null)
                (mContext as BaseActivity).surveyPlanningListDao.updateRow(
                    surveyPlanningListModel.options,
                    surveyPlanningListModel.sequenceId!!
                )
            /*viewModel.deleteSurveyPlanDetailOption(
                mContext,
                newList[position].optionId.toString()
            )*/

            notifyDataSetChanged()
        }
        notifyDataSetChanged()
    }

    fun getData(): MutableList<SurveyPlanningListModel.Option> {
        return newList
    }

    @JvmName("setSurveyPlanningListModel1")
    fun setSurveyPlanningListModel(surveyPlanningListModel: SurveyPlanningListModel) {
        this.surveyPlanningListModel = surveyPlanningListModel
    }

}