package com.pickfords.surveyorapp.view

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.room.Room
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.room.*
import com.pickfords.surveyorapp.utils.AppConstants
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.view.dialogs.ProgressDialog
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_dashboard.tvTitle
import kotlinx.android.synthetic.main.toolbar.*

open class BaseActivity : AppCompatActivity() {
    private var shouldPerformDispatchTouch = true
    lateinit var session: Session
    lateinit var db: Database
    lateinit var dashboardDao: DashboardDao
    lateinit var languageDao: LanguageDao
    lateinit var syncDao: SyncDao
    lateinit var submitDao: SubmitDao
    lateinit var addressListDao: AddressListDao
    lateinit var countryListDao: CountryListDao
    lateinit var saveSequenceDao: SaveSequenceDao
    lateinit var sequenceTypeDao: SequenceTypeDao
    lateinit var sequenceGroupDao: SequenceGroupDao
    lateinit var sequenceModeDao: SequenceModeDao
    lateinit var insuranceRequirementDao: InsuranceRequirementDao
    lateinit var shippingMethodDao: ShippingMethodDao
    lateinit var packingMethodDao: PackingMethodDao
    lateinit var showLegsDao: ShowLegsDao
    lateinit var sequenceLegsTypeDao: SequenceLegsTypeDao
    lateinit var legAccessDao: LegAccessDao
    lateinit var legPermitDao: LegPermitDao
    lateinit var allowanceTypeDao: AllowanceTypeDao
    lateinit var deliveryTypeDao: DeliveryTypeDao
    lateinit var distanceUnitTypeDao: DistanceUnitDao
    lateinit var inventoryFloorDao: InventoryFloorDao
    lateinit var inventoryRoomDao: InventoryRoomDao
    lateinit var inventoryCountDao: InventoryCountDao
    lateinit var customerListDao: CustomerListDao
    lateinit var referenceListDao: ReferenceListDao
    lateinit var inventoryListDao: InventoryListDao
    lateinit var inventoryItemListDao: InventoryItemListDao
    lateinit var surveyPlanningListDao: SurveyPlanningListDao
    lateinit var commentsDetailDao: CommentsDetailDao
    lateinit var picturesDao: PicturesDao
    lateinit var sequenceDetailsDao: SequenceDetailsDao
    private lateinit var legListDao: LegListDao
    lateinit var partnerAndServiceDao: PartnerAndServiceDao
    lateinit var partnerTempDao: PartnerAndServiceTempDao
    lateinit var additionalInfo: AdditionalInfoPartnerDao
    lateinit var activityListDao: ActivityListDao
    lateinit var codeListDao: CodeListDao
    lateinit var timeListDao: TimeListDao
    lateinit var enquiryListDao: EnquiryListDao
    lateinit var permitTypeListModelListDao: PermitTypeListModelDao
    lateinit var addressTypeListModelDao: AddressTypeListModelDao
    lateinit var filterSurveyDao: FilterSurveyDao
    lateinit var deletePartnerAndServiceContactDao: DeletePartnerAndServiceContactDao
    lateinit var sequenceDetailSignatureDao: SequenceDetailSignatureDao
    lateinit var rentalDao: RentalTypeDao
    lateinit var surveySizeTypeDao: SurveySizeDao
    lateinit var propertyTypeDao: PropertyTypeDao
    lateinit var surveyTypeDao: SurveyTypeDao
    lateinit var deletePlanningDataDao: DeletePlanningDataDao
    lateinit var crateTypeDataDao: CrateTypeDao


    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = Session(this)
        db = Room.databaseBuilder(
            applicationContext,
            Database::class.java, AppConstants.ROOM_DB
        ).allowMainThreadQueries().build()
        dashboardDao = db.dashboardDao()
        languageDao = db.languageDao()
        syncDao = db.getSyncDao()
        submitDao = db.getSubmitDao()
        addressListDao = db.addressListDao()
        countryListDao = db.countryListDao()
        saveSequenceDao = db.saveSequenceDao()
        sequenceTypeDao = db.sequenceTypeDao()
        sequenceGroupDao = db.sequenceGroupDao()
        sequenceModeDao = db.sequenceModeDao()
        insuranceRequirementDao = db.insuranceRequirementDao()
        shippingMethodDao = db.shippingMethodDao()
        packingMethodDao = db.packingMethodDao()
        showLegsDao = db.showLegsDao()
        sequenceLegsTypeDao = db.sequenceLegsTypeDao()
        legAccessDao = db.legAccessDao()
        legPermitDao = db.legPermitDao()
        allowanceTypeDao = db.allowanceTypeDao()
        deliveryTypeDao = db.deliveryTypeDao()
        distanceUnitTypeDao = db.distanceUnitTypeDao()
        inventoryFloorDao = db.inventoryFloorDao()
        inventoryRoomDao = db.inventoryRoomDao()
        inventoryCountDao = db.inventoryCountDao()
        customerListDao = db.customerListDao()
        referenceListDao = db.referenceListDao()
        inventoryListDao = db.inventoryListDao()
        inventoryItemListDao = db.inventoryItemListDao()
        surveyPlanningListDao = db.surveyPlanningListDao()
        commentsDetailDao = db.commentsDetailsDao()
        picturesDao = db.picturesDao()
        sequenceDetailsDao = db.sequenceDetailsDao()
        legListDao = db.legListDao()
        partnerAndServiceDao = db.partnerAndServiceDao()
        partnerTempDao = db.partnerTempDao()
        additionalInfo = db.additionalInfo()
        activityListDao = db.activityListDao()
        codeListDao = db.codeListDao()
        timeListDao = db.timeListDao()
        enquiryListDao = db.enquiryListDao()
        permitTypeListModelListDao = db.permitTypeListModelListDao()
        filterSurveyDao = db.filterSurveyDao()
        addressTypeListModelDao = db.addressTypeListModelDao()
        deletePartnerAndServiceContactDao = db.deletePartnerContact()
        deletePartnerAndServiceContactDao = db.deletePartnerContact()
        sequenceDetailSignatureDao = db.sequencedetailSignature()
        rentalDao = db.rentalType()
        surveySizeTypeDao = db.surveySize()
        propertyTypeDao = db.propertyType()
        surveyTypeDao = db.surveyType()
        deletePlanningDataDao = db.deletePlanningData()
        crateTypeDataDao = db.cratetTypeData()
        disableAutoFill()
    }

    open fun clearDatabase() {
        db.clearAllTables()
    }

    fun setUpToolbar(strTitle: String? = null) {
        setUpToolbarWithBackArrow(strTitle, false)
    }

    @JvmOverloads
    fun setUpToolbarWithBackArrow(strTitle: String? = null, isBackArrow: Boolean = true) {
        val title = toolbar?.findViewById<TextView>(R.id.tvTitle)

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(isBackArrow)
            actionBar.setHomeAsUpIndicator(R.drawable.v_ic_back_arrow)
            if (strTitle != null) title?.text = strTitle
        }
    }

    fun setupToolbarWithMenu(title: String? = null) {
        setSupportActionBar(toolbarDashboard)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_nav_menup)
            if (title != null) tvTitle.text = title
        }

    }

    fun showSoftKeyboard(view: EditText) {
        view.requestFocus(view.text.length)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun hideSoftKeyboard(): Boolean {
        return try {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            false
        }
    }

    fun showProgressbar(message: String? = getString(R.string.please_wait)) {
        hideProgressbar()
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this, message)
        }
        progressDialog?.show()
    }

    fun hideProgressbar() {
       if (progressDialog != null && progressDialog?.isShowing!!) progressDialog!!.dismiss()
    }

    fun hideProgress() {
           if (progressDialog != null && progressDialog?.isShowing!!) progressDialog!!.dismiss()
    }


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        var ret = false
        try {
            val view = currentFocus
            ret = super.dispatchTouchEvent(event)
            if (shouldPerformDispatchTouch) {
                if (view is EditText) {
                    val w = currentFocus
                    val scrCords = IntArray(2)
                    if (w != null) {
                        w.getLocationOnScreen(scrCords)
                        val x = event.rawX + w.left - scrCords[0]
                        val y = event.rawY + w.top - scrCords[1]

                        if (event.action == MotionEvent.ACTION_UP && (x < w.left || x >= w.right || y < w.top || y > w.bottom)) {
                            val imm =
                                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                        }
                    }
                }
            }
            return ret
        } catch (e: Exception) {
            e.printStackTrace()
            return ret
        }

    }

    private fun disableAutoFill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.importantForAutofill =
                View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

}
