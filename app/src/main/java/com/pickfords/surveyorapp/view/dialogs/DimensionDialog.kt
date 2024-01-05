package com.pickfords.surveyorapp.view.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.DialogDimensionsBinding
import com.pickfords.surveyorapp.utils.Session
import kotlinx.android.synthetic.main.dialog_dimensions.*

class DimensionDialog(
    var mContext: Context,
    var length: String?,
    var width: String?,
    var height: String?,
    var isCrate: Boolean?,
    var session: Session,
    private var measurementType: String?,
    private var crateTypeId: Int,
    var crateTypeList: java.util.ArrayList<String>,
    var  crateTypeIdList: java.util.ArrayList<Int>
) : Dialog(mContext, R.style.ThemeDialog) {
    private lateinit var binding: DialogDimensionsBinding
    private var listener: OkButtonListener? = null

    // For Crate Type Dropdown
    private var inventoryCrateTypeSpinnerAdapter: ArrayAdapter<String?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(false)
        setCancelable(false)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.dialog_dimensions,
            null,
            false
        )

        setContentView(binding.root)

        binding.cbCrate.setChecked(true)

        binding.etLength.setText(length)
        binding.etHeight.setText(height)
        binding.etWidth.setText(width)
        Log.e("isCrate", isCrate.toString())

        binding.etLength.text?.let { binding.etLength.requestFocus(it.length) }
        this.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        binding.etLength.requestFocus()

     //


        // For Crate Type Adapter
        inventoryCrateTypeSpinnerAdapter = ArrayAdapter<String?>(mContext, android.R.layout.simple_spinner_item, crateTypeList as List<String>)
        inventoryCrateTypeSpinnerAdapter?.setDropDownViewResource(R.layout.custom_spinner_item)
        binding.spnType.adapter = inventoryCrateTypeSpinnerAdapter


          binding.spnType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.e("OnItemSelected",""+position.toString())
                if(position > 0){
                    crateTypeId = crateTypeIdList[position - 1].toInt()
                }
                if (position == 0){
                    crateTypeId = 0
                }
            }
        }

        setSelectedPosition(crateTypeId)


        //todo Pranav
        val measurementList: ArrayList<String> = arrayListOf()
        if (session.getDataByKey(
                Session.MeasurementPreference,
                mContext.getString(R.string.inches_feet)
            ) == mContext.getString(R.string.inches_feet)
        ) {
            measurementList.add(mContext.getString(R.string.inches))
            measurementList.add(mContext.getString(R.string.feet))
        } else {
            measurementList.add(mContext.getString(R.string.centimeters))
            measurementList.add(mContext.getString(R.string.metres))
        }

        val measurementAdapter = ArrayAdapter(
            mContext,
            android.R.layout.simple_spinner_item,
            measurementList
        )
        measurementAdapter.setDropDownViewResource(R.layout.custom_spinner_item)
        btnOk.setOnClickListener {
            if (listener != null) {
                if (binding.etLength.text.isNullOrEmpty()) {
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.enter_length_val),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (binding.etWidth.text.isNullOrEmpty()) {
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.enter_width_val),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (binding.etHeight.text.isNullOrEmpty()) {
                    Toast.makeText(
                        mContext,
                        mContext.getString(R.string.enter_height_val),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    length = binding.etLength.text.toString()
                    width = binding.etWidth.text.toString()
                    height = binding.etHeight.text.toString()
                    listener?.onOkPressed(
                        this,
                        binding.etLength.text.toString(),
                        binding.etWidth.text.toString(),
                        binding.etHeight.text.toString(),
                        binding.cbCrate.isChecked,
                        measurementType,
                        crateTypeId
                    )
                }
            } else {
                listener?.onCancelPressed(
                    this
                )
            }
        }
        btnCancel.setOnClickListener {
            listener?.onCancelPressed(
                this
            )
        }
    }

    private fun setSelectedPosition(crateTypeId: Int) {
        var selectedposition = crateTypeIdList!!.indexOfFirst { it == crateTypeId  }
        if(selectedposition >= 0){
            binding.spnType.setSelection(selectedposition + 1)
        }
        Log.e("SelectedPostion",selectedposition.toString())
    }

    fun setListener(listener: OkButtonListener?): DimensionDialog {
        this.listener = listener
        return this
    }


    interface OkButtonListener {
        fun onOkPressed(
            dialog: DimensionDialog,
            length: String?,
            width: String?,
            height: String?,
            isCrate: Boolean?,
            measurementType: String?,
            createTypeId: Int
        )

        fun onCancelPressed(dialogDamage: DimensionDialog)
    }

}