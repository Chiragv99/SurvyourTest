package com.pickfords.surveyorapp.viewModel.surveyDetails

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.base.BaseViewModel
import com.pickfords.surveyorapp.model.surveyDetails.CommentsDetailModel
import com.pickfords.surveyorapp.model.surveyDetails.SaveSequenceModel
import com.pickfords.surveyorapp.network.BaseModel
import com.pickfords.surveyorapp.network.CallbackObserver
import com.pickfords.surveyorapp.network.Networking
import com.pickfords.surveyorapp.utils.Utility
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.random.Random

class CommentsViewModel(val context: Context) : BaseViewModel() {
    @SuppressLint("NotifyDataSetChanged")
    private val sequenceList: MutableList<String?> = mutableListOf()
    var surveyId1: Int? = null
    private var sequenceSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getSequenceSpinnerAdapter(): ArrayAdapter<String?>? = sequenceSpinnerAdapter

    var selectedPosition: MutableLiveData<Int> = MutableLiveData()

    private var sequenceLiveList: MutableLiveData<List<SaveSequenceModel>> = MutableLiveData()

    private var selectedSequenceLiveData: MutableLiveData<SaveSequenceModel> = MutableLiveData()
    fun getSelectSequenceModel(): MutableLiveData<List<SaveSequenceModel>> {
        return sequenceLiveList
    }


    private var selectedCommentsDetailLiveData: MutableLiveData<CommentsDetailModel> =
        MutableLiveData()

    fun getSelectCommentsDetailModel(): MutableLiveData<CommentsDetailModel> {
        return selectedCommentsDetailLiveData
    }

    fun init(context: Context) {

        sequenceSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, sequenceList)
        sequenceSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        sequenceLiveList.observeForever {
            if (it != null) {
                sequenceList.clear()
                for (data in it) {
                    val name =
                        if (data.surveySequence == null) data.labelToUse else data.surveySequence
                    sequenceList.add(name)
                }
                sequenceSpinnerAdapter?.notifyDataSetChanged()
            }
        }

    }

    // For Save Survey Sequence List
    fun getSequenceList(surveyId: String?, context: Context) {
        if ((context as BaseActivity).saveSequenceDao.getSequenceList() != null && (context as BaseActivity).saveSequenceDao.getSequenceListBySurveyId(surveyId.toString(),false).isNotEmpty()) {
            sequenceLiveList.postValue((context as BaseActivity).saveSequenceDao.getSequenceListBySurveyId(surveyId.toString(),false))
        } else {
            if (Utility.isNetworkConnected(context)){
                isLoading.postValue(true)
                Networking(context)
                    .getServices()
                    .getSequenceList(surveyId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CallbackObserver<BaseModel<List<SaveSequenceModel>>>() {
                        override fun onSuccess(response: BaseModel<List<SaveSequenceModel>>) {
                            isLoading.postValue(false)
                            sequenceLiveList.postValue(response.data)
                            if (response.data?.size!! > 0) {
                                if (selectedPosition.value != 1) {
                                    selectedPosition.postValue(0)
                                }
                            }
                        }

                        override fun onFailed(code: Int, message: String) {
                            isLoading.postValue(false)
                            sequenceLiveList.postValue(null)
                        }
                    })
            }
        }
    }

    // For Save Survey Comment
    fun saveSurveyComment(data: CommentsDetailModel?) {
        saveUserComment(data)
    }

    private fun saveUserComment(data: CommentsDetailModel?) {
        var flag = false
        if ((context as BaseActivity).commentsDetailDao.getCommentsList() != null && (context as BaseActivity).commentsDetailDao.getCommentsList().size > 0) {
            for (i in 0 until (context as BaseActivity).commentsDetailDao.getCommentsList().size) {
                if (data?.sequenceId == (context as BaseActivity).commentsDetailDao.getCommentsList()[i].sequenceId) {
                    flag = true
                    break
                } else {
                    flag = false
                }

            }
        }
        if (flag) {
            data?.isChangedField = true
            (context as BaseActivity).commentsDetailDao.update(data)
            Utility.setOfflineTag(context, "Comments Saved Successfully")
        } else {
            data?.sequenceNo =
                sequenceLiveList.value?.get(selectedPosition.value!!)?.surveySequence
            data?.surveyCommentId =
                "ADD" + Random.nextInt()
            (context as BaseActivity).commentsDetailDao.insert(data)
            if (!Utility.isNetworkConnected(context)){
                Utility.setOfflineTag(context, "Comments Saved Successfully")
            }
        }
    }

    // Return Comment API Response
    private fun getCommentById(context: Context, surveyId: String) {

        if ((context as BaseActivity).commentsDetailDao.getCommentsList() != null && context.commentsDetailDao.getCommentsBySurveySequenceId(selectedSequenceLiveData.value!!.surveySequenceId, surveyId) != null && context.commentsDetailDao.getCommentsBySurveySequenceId(selectedSequenceLiveData.value!!.surveySequenceId, surveyId).isNotEmpty()) {
            selectedCommentsDetailLiveData.postValue(
                context.commentsDetailDao.getCommentsBySurveySequenceId(
                    selectedSequenceLiveData.value!!.surveySequenceId,
                    surveyId
                )[0]
            )
        } else {
            selectedCommentsDetailLiveData.postValue(null)
        }
    }

    fun setSurveyID(surveyId: Int) {
        surveyId1 = surveyId
    }

    // Survey Selection Listener
    val clicksListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (sequenceLiveList != null && sequenceLiveList.value != null && sequenceLiveList.value!!.size >= position) {// && position != 0
                selectedSequenceLiveData.value = sequenceLiveList.value!![position]
                selectedPosition.postValue(position)
                getCommentById(context, surveyId1.toString())
            } else {
                selectedSequenceLiveData.value = SaveSequenceModel()
              //  selectedPosition.postValue(0)
                selectedCommentsDetailLiveData.postValue(null)
            }

        }
    }
}