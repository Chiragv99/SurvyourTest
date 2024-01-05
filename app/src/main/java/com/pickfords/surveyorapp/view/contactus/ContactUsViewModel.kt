package com.pickfords.surveyorapp.view.contactus

import android.content.Context
import androidx.lifecycle.ViewModel

class ContactUsViewModel : ViewModel() {
    private var addressList : ArrayList<ContactUsModel> = ArrayList()
    private lateinit var data : ContactUsModel
    private var contactUsItemAdapter: ContactUsItemAdapter? = null

    fun init(context: Context) {
        for (i in 0..10){
            data = ContactUsModel()
            data.address = "address$i"
            addressList.add(data)
        }
        contactUsItemAdapter =
            ContactUsItemAdapter(context, this, addressList)
        contactUsItemAdapter?.notifyDataSetChanged()
    }

    fun getAdapter(): ContactUsItemAdapter? = contactUsItemAdapter

}