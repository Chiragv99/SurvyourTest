package com.pickfords.surveyorapp.viewModel.surveyDetails

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.base.BaseViewModel
import com.pickfords.surveyorapp.interfaces.InventorySelection
import com.pickfords.surveyorapp.model.address.AddressListModel
import com.pickfords.surveyorapp.model.surveyDetails.*
import com.pickfords.surveyorapp.network.BaseModel
import com.pickfords.surveyorapp.network.CallbackObserver
import com.pickfords.surveyorapp.network.Networking
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.utils.Utility
import com.pickfords.surveyorapp.view.BaseActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class LegsViewModel(val context: Context) : BaseViewModel() {

    private val sequenceLegOriginTypeList: MutableList<String?> = mutableListOf()
    private var sequenceLegTypeOriginLiveList: MutableLiveData<List<SequenceLegsTypeModel>> =
        MutableLiveData()
    private var sequenceLegTypeOriginSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getSequenceLegTypeOriginSpinnerAdapter(): ArrayAdapter<String?>? =
        sequenceLegTypeOriginSpinnerAdapter

    private val sequenceLegDestinationTypeList: MutableList<String?> = mutableListOf()
    private var sequenceLegTypeDestinationLiveList: MutableLiveData<List<SequenceLegsTypeModel>> =
        MutableLiveData()
    private var sequenceLegTypeDestinationSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getSequenceLegTypeDestinationSpinnerAdapter(): ArrayAdapter<String?>? =
        sequenceLegTypeDestinationSpinnerAdapter

    private val sequenceLegOriginDistanceTypeList: MutableList<String?> = mutableListOf()
    private var sequenceLegOriginDistanceTypeLiveList: MutableLiveData<List<DistanceUnitTypeModel>> =
        MutableLiveData()
    private var sequenceLegOriginDistanceTypeSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getSequenceLegOriginDistanceTypeSpinnerAdapter(): ArrayAdapter<String?>? =
        sequenceLegOriginDistanceTypeSpinnerAdapter

    private val sequenceLegDestinationDistanceTypeList: MutableList<String?> = mutableListOf()
    private var sequenceLegDestinationDistanceTypeLiveList: MutableLiveData<List<DistanceUnitTypeModel>> =
        MutableLiveData()
    private var sequenceLegDestinationDistanceTypeSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getSequenceLegDestinationDistanceTypeSpinnerAdapter(): ArrayAdapter<String?>? =
        sequenceLegDestinationDistanceTypeSpinnerAdapter

    private val sequenceLegOriginAccessList: MutableList<String?> = mutableListOf()
    private var sequenceLegAccessOriginLiveList: MutableLiveData<List<LegAccessModel>> =
        MutableLiveData()
    private var sequenceLegAccessOriginSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getSequenceLegAccessOriginSpinnerAdapter(): ArrayAdapter<String?>? =
        sequenceLegAccessOriginSpinnerAdapter

    private val sequenceLegDestinationAccessList: MutableList<String?> = mutableListOf()
    private var sequenceLegAccessDestinationLiveList: MutableLiveData<List<LegAccessModel>> =
        MutableLiveData()
    private var sequenceLegAccessDestinationSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getSequenceLegAccessDestinationSpinnerAdapter(): ArrayAdapter<String?>? =
        sequenceLegAccessDestinationSpinnerAdapter


    private val sequenceLegOriginPermitList: MutableList<String?> = mutableListOf()
    private var sequenceLegPermitOriginLiveList: MutableLiveData<List<LegPermitModel>> =
        MutableLiveData()
    private var sequenceLegPermitOriginSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getSequenceLegPermitOriginSpinnerAdapter(): ArrayAdapter<String?>? =
        sequenceLegPermitOriginSpinnerAdapter

    private val sequenceLegDestinationPermitList: MutableList<String?> = mutableListOf()
    private var sequenceLegPermitDestinationLiveList: MutableLiveData<List<LegPermitModel>> =
        MutableLiveData()
    private var sequenceLegPermitDestinationSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getSequenceLegPermitDestinationSpinnerAdapter(): ArrayAdapter<String?>? =
        sequenceLegPermitDestinationSpinnerAdapter

    private val addressOriginList: MutableList<String?> = mutableListOf()
    private var addressOriginLiveList: MutableLiveData<List<AddressListModel>> = MutableLiveData()
    private var addressOriginSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getAddressOriginSpinnerAdapter(): ArrayAdapter<String?>? = addressOriginSpinnerAdapter

    private val addressDestinationList: MutableList<String?> = mutableListOf()
    private var addressDestinationLiveList: MutableLiveData<List<AddressListModel>> =
        MutableLiveData()
    private var addressDestinationSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getAddressDestinationSpinnerAdapter(): ArrayAdapter<String?>? = addressOriginSpinnerAdapter

    private val allowanceTypeList: MutableList<String?> = mutableListOf()
    private var allowanceTypeLiveList: MutableLiveData<List<AllowanceTypeModel>> =
        MutableLiveData()
    private var allowanceTypeSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getAllowanceTypeSpinnerAdapter(): ArrayAdapter<String?>? = allowanceTypeSpinnerAdapter

    private val deliveryTypeList: MutableList<String?> = mutableListOf()
    private var deliveryTypeLiveList: MutableLiveData<List<DeliveryTypeModel>> =
        MutableLiveData()
    private var deliveryTypeSpinnerAdapter: ArrayAdapter<String?>? = null
    fun getDeliveryTypeSpinnerAdapter(): ArrayAdapter<String?>? = deliveryTypeSpinnerAdapter


    val date = ObservableField<String>().apply {
        addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                this@apply.get()?.let { Log.d("value", it) } //selected value
            }
        })
    }

    fun getSequenceLegTypeOriginLiveList(): MutableLiveData<List<SequenceLegsTypeModel>> {
        return sequenceLegTypeOriginLiveList
    }

    fun getSequenceDeliveryTypeLiveList(): MutableLiveData<List<DeliveryTypeModel>> {
        return deliveryTypeLiveList
    }

    fun getSequenceLegTypeDestinationLiveList(): MutableLiveData<List<SequenceLegsTypeModel>> {
        return sequenceLegTypeDestinationLiveList
    }

    fun getSequenceLegAllowanceTypeLiveList(): MutableLiveData<List<AllowanceTypeModel>> {
        return allowanceTypeLiveList
    }

    fun getSequenceLegOriginDistanceTypeLiveList(): MutableLiveData<List<DistanceUnitTypeModel>> {
        return sequenceLegOriginDistanceTypeLiveList
    }

    fun getSequenceLegDestinationDistanceTypeLiveList(): MutableLiveData<List<DistanceUnitTypeModel>> {
        return sequenceLegDestinationDistanceTypeLiveList
    }

    fun getSequenceLegAccessOriginLiveList(): MutableLiveData<List<LegAccessModel>> {
        return sequenceLegAccessOriginLiveList
    }

    fun getSequenceLegAccessDestinationLiveList(): MutableLiveData<List<LegAccessModel>> {
        return sequenceLegAccessDestinationLiveList
    }

    fun getSequenceLegAddressOriginLiveList(): MutableLiveData<List<AddressListModel>> {
        return addressOriginLiveList
    }

    fun getSequenceLegAddressDestinationLiveList(): MutableLiveData<List<AddressListModel>> {
        return addressDestinationLiveList
    }

    fun getSequenceLegPermitOriginLiveList(): MutableLiveData<List<LegPermitModel>> {
        return sequenceLegPermitOriginLiveList
    }

    fun getSequenceLegPermitDestinationLiveList(): MutableLiveData<List<LegPermitModel>> {
        return sequenceLegPermitDestinationLiveList
    }

    @SuppressLint("NotifyDataSetChanged")
    fun init(context: Context) {
        allowanceTypeSpinnerAdapter =
            ArrayAdapter<String?>(
                context,
                android.R.layout.simple_spinner_item,
                allowanceTypeList
            )
        allowanceTypeSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        deliveryTypeSpinnerAdapter =
            ArrayAdapter<String?>(
                context,
                android.R.layout.simple_spinner_item,
                deliveryTypeList
            )
        deliveryTypeSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        sequenceLegOriginDistanceTypeSpinnerAdapter = ArrayAdapter<String?>(
            context,
            android.R.layout.simple_spinner_item,
            sequenceLegOriginDistanceTypeList
        )
        sequenceLegOriginDistanceTypeSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        sequenceLegDestinationDistanceTypeSpinnerAdapter = ArrayAdapter<String?>(
            context,
            android.R.layout.simple_spinner_item,
            sequenceLegDestinationDistanceTypeList
        )
        sequenceLegDestinationDistanceTypeSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        sequenceLegTypeOriginSpinnerAdapter =
            ArrayAdapter<String?>(
                context,
                android.R.layout.simple_spinner_item,
                sequenceLegOriginTypeList
            )
        sequenceLegTypeOriginSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)
        sequenceLegTypeDestinationSpinnerAdapter =
            ArrayAdapter<String?>(
                context,
                android.R.layout.simple_spinner_item,
                sequenceLegDestinationTypeList
            )
        sequenceLegTypeDestinationSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        sequenceLegAccessOriginSpinnerAdapter =
            ArrayAdapter<String?>(
                context,
                android.R.layout.simple_spinner_item,
                sequenceLegOriginAccessList
            )
        sequenceLegAccessOriginSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        sequenceLegAccessDestinationSpinnerAdapter =
            ArrayAdapter<String?>(
                context,
                android.R.layout.simple_spinner_item,
                sequenceLegDestinationAccessList
            )
        sequenceLegAccessDestinationSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        //-------

        sequenceLegPermitOriginSpinnerAdapter =
            ArrayAdapter<String?>(
                context,
                android.R.layout.simple_spinner_item,
                sequenceLegOriginPermitList
            )
        sequenceLegPermitOriginSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        sequenceLegPermitDestinationSpinnerAdapter =
            ArrayAdapter<String?>(
                context,
                android.R.layout.simple_spinner_item,
                sequenceLegDestinationPermitList
            )
        sequenceLegPermitDestinationSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)


        sequenceLegTypeOriginLiveList.observeForever {
            if (it != null) {
                sequenceLegOriginTypeList.clear()
                for (data in it) {
                    sequenceLegOriginTypeList.add(data.name)
                }
                sequenceLegTypeOriginSpinnerAdapter?.notifyDataSetChanged()
            }
        }

        sequenceLegOriginDistanceTypeLiveList.observeForever {
            if (it != null) {
                sequenceLegOriginDistanceTypeList.clear()
                for (data in it) {
                    sequenceLegOriginDistanceTypeList.add(data.distanceUnit)
                }
                sequenceLegOriginDistanceTypeSpinnerAdapter?.notifyDataSetChanged()
            }
        }

        sequenceLegDestinationDistanceTypeLiveList.observeForever {
            if (it != null) {
                sequenceLegDestinationDistanceTypeList.clear()
                for (data in it) {
                    sequenceLegDestinationDistanceTypeList.add(data.distanceUnit)
                }
                sequenceLegDestinationDistanceTypeSpinnerAdapter?.notifyDataSetChanged()
            }
        }

        deliveryTypeLiveList.observeForever {
            if (it != null) {
                deliveryTypeList.clear()
                for (data in it) {
                    deliveryTypeList.add(data.deliveryType)
                }
                deliveryTypeSpinnerAdapter?.notifyDataSetChanged()
            }
        }
        sequenceLegTypeDestinationLiveList.observeForever {
            if (it != null) {
                sequenceLegDestinationTypeList.clear()
                for (data in it) {
                    sequenceLegDestinationTypeList.add(data.name)
                }
                sequenceLegTypeDestinationSpinnerAdapter?.notifyDataSetChanged()
            }
        }

        sequenceLegAccessOriginLiveList.observeForever {
            if (it != null) {
                sequenceLegOriginAccessList.clear()
                for (data in it) {
                    sequenceLegOriginAccessList.add(data.name)
                }
                sequenceLegAccessOriginSpinnerAdapter?.notifyDataSetChanged()
            }
        }

        sequenceLegAccessDestinationLiveList.observeForever {
            if (it != null) {
                sequenceLegDestinationAccessList.clear()
                for (data in it) {
                    sequenceLegDestinationAccessList.add(data.name)
                }
                sequenceLegAccessDestinationSpinnerAdapter?.notifyDataSetChanged()
            }
        }

        sequenceLegPermitOriginLiveList.observeForever {
            if (it != null) {
                sequenceLegOriginPermitList.clear()
                for (data in it) {
                    sequenceLegOriginPermitList.add(data.name)
                }
                sequenceLegPermitOriginSpinnerAdapter?.notifyDataSetChanged()
            }
        }

        sequenceLegPermitDestinationLiveList.observeForever {
            if (it != null) {
                sequenceLegDestinationPermitList.clear()
                for (data in it) {
                    sequenceLegDestinationPermitList.add(data.name)
                }
                sequenceLegPermitDestinationSpinnerAdapter?.notifyDataSetChanged()
            }
        }

        addressOriginSpinnerAdapter =
            ArrayAdapter<String?>(context, android.R.layout.simple_spinner_item, addressOriginList)
        addressOriginSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        addressDestinationSpinnerAdapter =
            ArrayAdapter<String?>(
                context,
                android.R.layout.simple_spinner_item,
                addressDestinationList
            )
        addressDestinationSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)

        addressOriginLiveList.observeForever {
            if (it != null) {
                addressOriginList.clear()
                for (data in it) {
                    addressOriginList.add(data.titleName)
                }
                addressOriginSpinnerAdapter?.notifyDataSetChanged()
            }
        }
        addressDestinationLiveList.observeForever {
            if (it != null) {
                addressDestinationList.clear()
                for (data in it) {
                    addressDestinationList.add(data.titleName)
                }
                addressDestinationSpinnerAdapter?.notifyDataSetChanged()
            }
        }

        allowanceTypeLiveList.observeForever {
            if (it != null) {
                allowanceTypeList.clear()
                for (data in it) {
                    allowanceTypeList.add(data.name)
                }
                allowanceTypeSpinnerAdapter?.notifyDataSetChanged()
            }
        }

        if ((context as BaseActivity).sequenceLegsTypeDao.getSequenceLegsTypeList() != null && (context as BaseActivity).sequenceLegsTypeDao.getSequenceLegsTypeList().size > 0) {
            sequenceLegTypeOriginLiveList.postValue((context as BaseActivity).sequenceLegsTypeDao.getSequenceLegsTypeList())
            sequenceLegTypeDestinationLiveList.postValue((context as BaseActivity).sequenceLegsTypeDao.getSequenceLegsTypeList())


            if ((context as BaseActivity).legAccessDao.getLegAccessList() != null && (context as BaseActivity).legAccessDao.getLegAccessList().size > 0) {
                sequenceLegAccessOriginLiveList.postValue((context as BaseActivity).legAccessDao.getLegAccessList())
                sequenceLegAccessDestinationLiveList.postValue((context as BaseActivity).legAccessDao.getLegAccessList())

                if ((context as BaseActivity).legPermitDao.getLegPermitList() != null && (context as BaseActivity).legPermitDao.getLegPermitList().size > 0) {
                    sequenceLegPermitOriginLiveList.postValue((context as BaseActivity).legPermitDao.getLegPermitList())
                    sequenceLegPermitDestinationLiveList.postValue((context as BaseActivity).legPermitDao.getLegPermitList())

                    if ((context as BaseActivity).allowanceTypeDao.getAllowanceTypeList() != null && (context as BaseActivity).allowanceTypeDao.getAllowanceTypeList().size > 0) {
                        allowanceTypeLiveList.postValue((context as BaseActivity).allowanceTypeDao.getAllowanceTypeList())


                        if ((context as BaseActivity).deliveryTypeDao.getDeliveryTypeList() != null && (context as BaseActivity).deliveryTypeDao.getDeliveryTypeList().size > 0) {
                            deliveryTypeLiveList.postValue((context as BaseActivity).deliveryTypeDao.getDeliveryTypeList())
                            if ((context as BaseActivity).distanceUnitTypeDao.getDistanceUnitList() != null && (context as BaseActivity).distanceUnitTypeDao.getDistanceUnitList().size > 0) {
                                sequenceLegOriginDistanceTypeLiveList.postValue((context as BaseActivity).distanceUnitTypeDao.getDistanceUnitList())
                                sequenceLegDestinationDistanceTypeLiveList.postValue((context as BaseActivity).distanceUnitTypeDao.getDistanceUnitList())
                            } else {
                                getDistanceTypeUnit(context)
                            }
                        } else {
                            getDeliveryTypeList(context)
                        }

                    } else {
                        getAllowanceTypeList(context)
                    }

                } else {
                    getSequenceOriginLegsPermitList(context)
                }


            } else {
                getSequenceLegsOriginAccessList(context)
            }


        } else {
            getSequenceLegsTypeList(context)
        }

    }
    //  For Distance Unit List API
    private fun getDistanceTypeUnit(context: Context) {
        Networking(context)
            .getServices()
            .getUnitTypeList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<DistanceUnitTypeModel>>>() {
                override fun onSuccess(response: BaseModel<List<DistanceUnitTypeModel>>) {

                    sequenceLegOriginDistanceTypeLiveList.postValue(response.data)
                    sequenceLegDestinationDistanceTypeLiveList.postValue(response.data)

                }

                override fun onFailed(code: Int, message: String) {

                    sequenceLegOriginDistanceTypeLiveList.postValue(null)
                    sequenceLegDestinationDistanceTypeLiveList.postValue(null)


                }
            })
    }
    //  For Allowance Type List API
    private fun getAllowanceTypeList(context: Context) {
        //isLoading.postValue(true)
        Networking(context)
            .getServices()
            .getallowanceTypeList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<AllowanceTypeModel>>>() {
                override fun onSuccess(response: BaseModel<List<AllowanceTypeModel>>) {
                    allowanceTypeLiveList.postValue(response.data)

                    if ((context as BaseActivity).deliveryTypeDao.getDeliveryTypeList() != null && (context as BaseActivity).deliveryTypeDao.getDeliveryTypeList().size > 0) {
                        deliveryTypeLiveList.postValue((context as BaseActivity).deliveryTypeDao.getDeliveryTypeList())
                    } else {
                        getDeliveryTypeList(context)
                    }
                }

                override fun onFailed(code: Int, message: String) {
                    //                    isLoading.postValue(false)
                    allowanceTypeLiveList.postValue(null)

                    if ((context as BaseActivity).deliveryTypeDao.getDeliveryTypeList() != null && (context as BaseActivity).deliveryTypeDao.getDeliveryTypeList().size > 0) {
                        deliveryTypeLiveList.postValue((context as BaseActivity).deliveryTypeDao.getDeliveryTypeList())
                    } else {
                        getDeliveryTypeList(context)
                    }
                }
            })
    }

    //  For Delivery Type List API
    private fun getDeliveryTypeList(context: Context) {
        //        isLoading.postValue(true)
        Networking(context)
            .getServices()
            .getDeliveryTypeList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<DeliveryTypeModel>>>() {
                override fun onSuccess(response: BaseModel<List<DeliveryTypeModel>>) {
                    isLoading.postValue(false)
                    deliveryTypeLiveList.postValue(response.data)
                }

                override fun onFailed(code: Int, message: String) {
                    isLoading.postValue(false)
                    deliveryTypeLiveList.postValue(null)
                }
            })
    }
    //  For Sequence Legs Type API
    private fun getSequenceLegsTypeList(context: Context) {
        isLoading.postValue(true)
        Networking(context)
            .getServices()
            .getSequenceLegtypeListForDDL()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<SequenceLegsTypeModel>>>() {
                override fun onSuccess(response: BaseModel<List<SequenceLegsTypeModel>>) {
                    // isLoading.postValue(false)
                    sequenceLegTypeOriginLiveList.postValue(response.data)
                    sequenceLegTypeDestinationLiveList.postValue(response.data)
                    //                    (context as BaseActivity).sequenceLegsTypeDao.insertAll(response.data)


                    if ((context as BaseActivity).legAccessDao.getLegAccessList() != null && (context as BaseActivity).legAccessDao.getLegAccessList().size > 0) {
                        sequenceLegAccessOriginLiveList.postValue((context as BaseActivity).legAccessDao.getLegAccessList())
                        sequenceLegAccessDestinationLiveList.postValue((context as BaseActivity).legAccessDao.getLegAccessList())
                    } else {
                        getSequenceLegsOriginAccessList(context)
                    }
                }

                override fun onFailed(code: Int, message: String) {
                    //                    isLoading.postValue(false)
                    sequenceLegTypeOriginLiveList.postValue(null)
                    sequenceLegTypeDestinationLiveList.postValue(null)


                    if ((context as BaseActivity).legAccessDao.getLegAccessList() != null && (context as BaseActivity).legAccessDao.getLegAccessList().size > 0) {
                        sequenceLegAccessOriginLiveList.postValue((context as BaseActivity).legAccessDao.getLegAccessList())
                        sequenceLegAccessDestinationLiveList.postValue((context as BaseActivity).legAccessDao.getLegAccessList())
                    } else {
                        getSequenceLegsOriginAccessList(context)
                    }

                }
            })
    }


    private fun getSequenceLegsOriginAccessList(context: Context) {
        //  isLoading.postValue(true)
        Networking(context)
            .getServices()
            .getSequenceLegAccessList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<LegAccessModel>>>() {
                override fun onSuccess(response: BaseModel<List<LegAccessModel>>) {

                    sequenceLegAccessOriginLiveList.postValue(response.data)
                    sequenceLegAccessDestinationLiveList.postValue(response.data)

                    if ((context as BaseActivity).legPermitDao.getLegPermitList() != null && (context as BaseActivity).legPermitDao.getLegPermitList().size > 0) {
                        sequenceLegPermitOriginLiveList.postValue((context as BaseActivity).legPermitDao.getLegPermitList())
                        sequenceLegPermitDestinationLiveList.postValue((context as BaseActivity).legPermitDao.getLegPermitList())
                    } else {
                        getSequenceOriginLegsPermitList(context)
                    }

                }

                override fun onFailed(code: Int, message: String) {
                    //                    isLoading.postValue(false)
                    sequenceLegAccessOriginLiveList.postValue(null)
                    sequenceLegAccessDestinationLiveList.postValue(null)


                    if ((context as BaseActivity).legPermitDao.getLegPermitList() != null && (context as BaseActivity).legPermitDao.getLegPermitList().size > 0) {
                        sequenceLegPermitOriginLiveList.postValue((context as BaseActivity).legPermitDao.getLegPermitList())
                        sequenceLegPermitDestinationLiveList.postValue((context as BaseActivity).legPermitDao.getLegPermitList())
                    } else {
                        getSequenceOriginLegsPermitList(context)
                    }

                }
            })
    }

    private fun getSequenceOriginLegsPermitList(context: Context) {
        //  isLoading.postValue(true)
        Networking(context)
            .getServices()
            .getSequenceLegPermitList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<LegPermitModel>>>() {
                override fun onSuccess(response: BaseModel<List<LegPermitModel>>) {
                    //                    isLoading.postValue(false)
                    sequenceLegPermitOriginLiveList.postValue(response.data)
                    sequenceLegPermitDestinationLiveList.postValue(response.data)
                    //                    (context as BaseActivity).legPermitDao.insertAll(response.data)


                    if ((context as BaseActivity).allowanceTypeDao.getAllowanceTypeList() != null && (context as BaseActivity).allowanceTypeDao.getAllowanceTypeList().size > 0) {
                        allowanceTypeLiveList.postValue((context as BaseActivity).allowanceTypeDao.getAllowanceTypeList())
                    } else {
                        getAllowanceTypeList(context)
                    }
                }

                override fun onFailed(code: Int, message: String) {
                    //                    isLoading.postValue(false)
                    sequenceLegPermitOriginLiveList.postValue(null)
                    sequenceLegPermitDestinationLiveList.postValue(null)


                    if ((context as BaseActivity).allowanceTypeDao.getAllowanceTypeList() != null && (context as BaseActivity).allowanceTypeDao.getAllowanceTypeList().size > 0) {
                        allowanceTypeLiveList.postValue((context as BaseActivity).allowanceTypeDao.getAllowanceTypeList())
                    } else {
                        getAllowanceTypeList(context)
                    }
                }
            })
    }
    // Get Address Origin List
     fun getAddressOriginList(context: Context, surveyId: String?) {
        if ((context as BaseActivity).addressListDao.getAddressList() != null && (context as BaseActivity).addressListDao.getAddressList().size > 0 && context.addressListDao.getAddressListBySurveyId(
                surveyId.toString()
            ) != null
        ) {
            addressOriginLiveList.postValue(
                (context as BaseActivity).addressListDao.getAddressListBySurveyId(
                    surveyId.toString()
                )
            )
            addressDestinationLiveList.postValue(
                (context as BaseActivity).addressListDao.getAddressListBySurveyId(
                    surveyId.toString()
                )
            )
        } else {
            val session = Session(context)
            isLoading.postValue(true)
            Networking(context)
                .getServices()
                .getSurveyAddressListBySurveyId(surveyId!!, session.user!!.userId!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<AddressListModel>>>() {
                    override fun onSuccess(response: BaseModel<List<AddressListModel>>) {
                        isLoading.postValue(false)
                        addressOriginLiveList.postValue(response.data)
                        addressDestinationLiveList.postValue(response.data)
                    }

                    override fun onFailed(code: Int, message: String) {
                        isLoading.postValue(false)
                        addressOriginLiveList.postValue(null)
                        addressDestinationLiveList.postValue(null)
                    }
                })
        }
    }

// Save Data Leg
    fun saveData(model: ShowLegsModel, inventorySelection: InventorySelection?) {
        model.pakingDate = date.get()

        if (!Utility.isNetworkConnected(context)) {
            Utility.setOfflineTag(context, "Legs Saved Successfully")
            model.surveySequenceLegId = "ADD" + Random.nextInt()
            (context as BaseActivity).showLegsDao.insert(model)
            return
        } else {
            isLoading.postValue(true)
            Networking(context)
                .getServices()
                .saveSequenceLeg(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<ShowLegsModel>>>() {
                    override fun onSuccess(response: BaseModel<List<ShowLegsModel>>) {
                        isLoading.postValue(false)
                  //      inventorySelection?.onSelectedItem("", InventoryTypeSelectionModel())
                    }

                    override fun onFailed(code: Int, message: String) {
                        isLoading.postValue(false)
                //        inventorySelection?.onSelectedItem("", InventoryTypeSelectionModel())
                    }
                })
        }
    }

    // Update Sequence Leg
    fun updateSequenceLeg(model: ShowLegsModel, inventorySelection: InventorySelection?) {
        model.pakingDate = date.get()
        if (!Utility.isNetworkConnected(context)) {
            Utility.setOfflineTag(context, "Deatils Updated Successfully")
            model.isChangedField = true
            (context as BaseActivity).showLegsDao.update(model)
            return
        } else {
            isLoading.postValue(true)
            Networking(context)
                .getServices()
                .updateSequenceLeg(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<ShowLegsModel>>>() {
                    override fun onSuccess(response: BaseModel<List<ShowLegsModel>>) {
                        isLoading.postValue(false)
                   //     inventorySelection?.onSelectedItem("", InventoryTypeSelectionModel())
                    }

                    override fun onFailed(code: Int, message: String) {
                        isLoading.postValue(false)
                  //      inventorySelection?.onSelectedItem("", InventoryTypeSelectionModel())
                    }
                })
        }
    }

    fun onUsernameTextChanged(text: CharSequence?) {
        val calendar = Calendar.getInstance()
        calendar?.time = SimpleDateFormat("dd/MMM/yyyy").parse(text.toString())
        date.set(SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").format(calendar.time))
    }

}