package com.pickfords.surveyorapp

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.CursorWindow
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.*
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.pickfords.surveyorapp.databinding.ActivityDashboardBinding
import com.pickfords.surveyorapp.extentions.goToActivityAndClearTask
import com.pickfords.surveyorapp.interfaces.InternetConnectionListener
import com.pickfords.surveyorapp.interfaces.SyncCallback
import com.pickfords.surveyorapp.model.DeletePartnerAndServiceContactModel
import com.pickfords.surveyorapp.model.MasterTableModel
import com.pickfords.surveyorapp.model.PartnerServiceModel
import com.pickfords.surveyorapp.model.address.AddressListModel
import com.pickfords.surveyorapp.model.address.AddressTypeListModel
import com.pickfords.surveyorapp.model.address.CountryListModel
import com.pickfords.surveyorapp.model.address.PropertyTypeModel
import com.pickfords.surveyorapp.model.auth.User
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.model.dashboard.FilterSurvey
import com.pickfords.surveyorapp.model.enquiry.EnquiryTypeModel
import com.pickfords.surveyorapp.model.enquiry.RentalTypeModel
import com.pickfords.surveyorapp.model.enquiry.SurveySizeListModel
import com.pickfords.surveyorapp.model.planning.TimeModel
import com.pickfords.surveyorapp.model.surveyDetails.*
import com.pickfords.surveyorapp.model.syncDatatoServer.*
import com.pickfords.surveyorapp.network.BaseModel
import com.pickfords.surveyorapp.network.CallbackObserver
import com.pickfords.surveyorapp.network.Networking
import com.pickfords.surveyorapp.utils.AppConstants
import com.pickfords.surveyorapp.utils.SyncManager
import com.pickfords.surveyorapp.utils.Utility
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.view.auth.SignInActivity
import com.pickfords.surveyorapp.view.dashboard.FragmentDashboard
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import com.pickfords.surveyorapp.view.dialogs.SyncDataDialog
import com.pickfords.surveyorapp.view.planning.CustomerListModel
import com.pickfords.surveyorapp.view.planning.ReferenceListModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.reflect.Field
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.set
import kotlin.random.Random


@Suppress("OVERRIDE_DEPRECATION")
class DashboardActivity : BaseActivity(), MessageDialog.OkButtonListener {

    private var mNetworkReceiver: BroadcastReceiver? = null
    private lateinit var binding: ActivityDashboardBinding
    lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var isFirstTime: Boolean = true
    private var isSync: Boolean = false
    private var isSyncProgress: Boolean = false
    var i = 0

    // For Repeat Call back
    private lateinit var jobRepeatCallBack: Job


    // For Sync data to server
    private val obj_syncAllData = SyncDataToServer()
    private val obj_syncPartnerAndCommentData = SyncPlanningCommentToServer()
    private var syncManager: SyncManager? = null


    var errorMessage = ""


    private val compositeDisposable = CompositeDisposable()
    private var syncDataObservable = PublishSubject.create<String>()

    // Set Selected Sequence Position
    private var selectedSequencePosition = 0


    // For check Survey Size
    var surveySizeFromServer: Int = 0
    var surveySizeFromDatabase: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentView(this, R.layout.activity_dashboard)

        try {
            val field: Field = CursorWindow::class.java.getDeclaredField("sCursorWindowSize")
            field.isAccessible = true
            field.set(null, 20 * 1024 * 1024) //the 100MB is the new size
        } catch (e: java.lang.Exception) {

        }

        FirebaseApp.initializeApp(this)
        session.user?.userId?.let { userId ->
            FirebaseCrashlytics.getInstance().setUserId(userId)
        }

        syncManager = SyncManager(this, db, session)
        syncDataObservable = PublishSubject.create<String>()

        val value = "1753-01-01T00:00:00"
        if (value != null) {
            val cal = Calendar.getInstance()
            try {
                val input = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.US)
                cal.time = input.parse(value)
                val output = SimpleDateFormat("hh:mm aa", Locale.US)
                Log.e("test time", output.format(cal.timeInMillis))

            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        val value1 = "1753-01-01T13:00:00"
        if (value1 != null) {
            val cal = Calendar.getInstance()
            try {
                val input = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
                cal.time = input.parse(value1)
                val output = SimpleDateFormat("hh:mm a")
                Log.e("test time", output.format(cal.timeInMillis))

            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }

        // Set up ActionBar
        setSupportActionBar(toolbarDashboard)
        val actionBar = supportActionBar
        if (actionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.title = getString(R.string.dashboard)
        }
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_nav_menup)

        navController = findNavController(R.id.navHostFragmentPickford)
        @Suppress("DEPRECATION")
        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.dashboardFragment,
            //            R.id.surveyDetailsFragment,
            R.id.settingFragment,
            R.id.contactUsFragment,
            //            R.id.termsAndConditionFragment,
            //            R.id.privacyPolicyFragment
        ).setDrawerLayout(drawer).build()


        setupActionBarWithNavController(
            navController,
            appBarConfiguration
        )
        setupNavControl() //To configure navController with drawer and bottom navigation

        mNetworkReceiver = CheckConnection(internetConnectionListener)
        registerNetworkBroadcastForNougat()
        /**
         * Set version name in navigation drawer
         * */

        val versionName: MenuItem = navigationView.menu.findItem(R.id.nav_log_version)
        versionName.title = "Version ${BuildConfig.VERSION_NAME}"

        when (AppConstants.baseURL) {
            AppConstants.LiveURL -> {
                versionName.title = "Version Live${BuildConfig.VERSION_NAME}"
            }
            AppConstants.TestLiveURL -> {
                versionName.title = "Version Test ${BuildConfig.VERSION_NAME}"
            }
            else -> {
                versionName.title = "Version Staging ${BuildConfig.VERSION_NAME}"
            }
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            }
            when (menuItem.itemId) {
                R.id.dashboardFragment -> {
                    navController.navigate(R.id.dashboardFragment)
                    toolbarDashboard.setTitle(R.string.dashboard)

                }

                R.id.surveyHistoryFragment -> {
                    navController.navigate(R.id.surveyHistoryFragment)
                    toolbarDashboard.setTitle(R.string.survey_history)
                }

                R.id.preferenceFragment -> {
                    navController.navigate(R.id.measurementPreferenceFragment)
                    toolbarDashboard.setTitle(R.string.measurement_preference)
                }

                R.id.settingFragment -> {
                    navController.navigate(R.id.settingFragment)
                    toolbarDashboard.setTitle(R.string.change_password)
                }

                R.id.contactUsFragment -> {
                    navController.navigate(R.id.contactUsFragment)
                    toolbarDashboard.setTitle(R.string.contact_us)
                }

                R.id.termsAndConditionFragment -> {
                    navController.navigate(R.id.termsAndConditionFragment)
                    toolbarDashboard.setTitle(R.string.terms_amp_conditions)

                }

                R.id.privacyPolicyFragment -> {
                    navController.navigate(R.id.privacyPolicyFragment)
                    toolbarDashboard.setTitle(R.string.privacy_policy)
                }

                R.id.actionSignOut -> {
                    MessageDialog(
                        this,
                        getString(R.string.are_you_sure_you_want_to_logout)
                    ).setListener(this).setCancelButton(true).show()
                }
            }
            true
        }
        setupToolbarWithMenu(getString(R.string.dashboard))
        checkUserIsFirstTime()
        //   getSurveyDetailMasterApiResponse()
    }

    private fun getSurveyDetailMasterApiResponse() {
//        showProgressbar()
        Networking(this@DashboardActivity).getServices()
            .getSurveyDetailMasterApi(session.user!!.userId!!)
            .flatMapCompletable {
                if (it.success && it.data != null) {
                    Completable.fromCallable {
                        //   db.clearAllTables()
                        checkErrorMessage(it.data!!)
                        if (it.data?.getSurveyList().isNullOrEmpty()
                                .not() && it.data?.getSurveyAddressList().isNullOrEmpty()
                                .not() && it.data?.getSurveySequenceList().isNullOrEmpty().not()
                        ) {
                            checkforNewsurvey(it.data?.getSurveyList())
                            db.clearAllTables()
                            Log.e("Clear All Tables", "Clear All Tables")
                            insertAllSequenceData(it.data)
                        }
                    }
                } else {
                    Completable.error(Throwable("data not found"))
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                compareSurveySize()
                //  hideProgressbar()
                hideProgress()
                reloadDashboard()
                getMasterTableList()
            }, { error ->
                Log.d("MyTag", "Error:" + error.message)
                //    hideProgressbar()
                hideProgress()
                errorMessage = "There are no surveys to sync.  Please try again later."
                SyncDataDialog(
                    this,
                    errorMessage
                ).setTitleData(getString(R.string.dialog_sync_data_error_title))
                    .setListener(object : SyncDataDialog.OkButtonListener {
                        override fun onOkPressed(dialog: SyncDataDialog) {
                            dialog.dismiss()
                            hideProgress()
                            reloadDashboard()
                            getMasterTableList()
                        }
                    })
                    .setCancelButton(true)
                    .show()
            }).let {}
    }

    private fun compareSurveySize() {
        if (surveySizeFromServer <= surveySizeFromDatabase) {
            SyncDataDialog(
                this,
                resources.getString(R.string.dialog_sync_data_error_message)
            ).setTitleData(getString(R.string.dialog_sync_data_title))
                .setListener(object : SyncDataDialog.OkButtonListener {
                    override fun onOkPressed(dialog: SyncDataDialog) {
                        dialog.dismiss()
                        reloadDashboard()
                        //   getMasterTableList()
                    }
                })
                .setCancelButton(false)
                .show()
        }
    }

    private fun checkforNewsurvey(surveyList: List<DashboardModel?>?) {
        surveySizeFromDatabase = 0
        surveySizeFromServer = 0
        if (surveyList.isNullOrEmpty().not()) {
            if (dashboardDao.getDashboardList() != null && dashboardDao.getDashboardList()
                    .isNotEmpty()
            ) {
                surveySizeFromServer = surveyList!!.size
                surveySizeFromDatabase = dashboardDao.getDashboardList().size
            } else {
                surveySizeFromServer = surveyList!!.size
                surveySizeFromDatabase = 0
            }
        }
    }

    private fun reloadDashboard() {
        if (getForegroundFragment() is FragmentDashboard) {
            var fragmentDashboard: FragmentDashboard = getForegroundFragment() as FragmentDashboard
            fragmentDashboard.setDefaultValueDashboard()
        }
    }

    private fun checkErrorMessage(data: GetSurveyDetailMasterAPIModel) {
        if (data != null) {
            if (data?.getSurveyList().isNullOrEmpty()) {
                //    errorMessage = "Survey List are blank. Please try again later!"
            }
            if (data?.getSurveyAddressList().isNullOrEmpty()) {
                //     errorMessage = "Address List are blank. Please try again later!"
            }
            if (data?.getSurveySequenceList().isNullOrEmpty()) {
                //     errorMessage = "Sequence List are blank. Please try again later!"
            }

            if (data?.getSurveySequenceList().isNullOrEmpty()) {
                //    errorMessage = "Inventory Item List are blank. Please try again later!"
            }
        }
    }

    // For Add All Sequence Related Data
    @Throws(Throwable::class)
    private fun insertAllSequenceData(baseData: GetSurveyDetailMasterAPIModel?) {

        // Get Survey List
        if (baseData?.getSurveyList().isNullOrEmpty().not()) {
            dashboardDao.insertAll(baseData?.getSurveyList())
        } else {
            throw Throwable("SurveryList data not found")
        }
        // Get Survey Address List
        if (baseData?.getSurveyAddressList().isNullOrEmpty().not()) {
            addressListDao.insertAll(baseData?.getSurveyAddressList())
        } else {
            throw Throwable("SurveyAddressList data not found")
        }

        // Get Survey Sequence List
        if (baseData?.getSurveySequenceList().isNullOrEmpty().not()) {
            saveSequenceDao.insertAll(baseData?.getSurveySequenceList())
        } else {
            throw Throwable("SurveySequenceList data not found")
        }

        // Get Survey Leg List
        if (baseData?.getSurveySequenceLegList().isNullOrEmpty().not()) {
            showLegsDao.insertAll(baseData?.getSurveySequenceLegList())
        }
        // Get Survey Inventory List
        if (baseData?.getSurveyInventoryList().isNullOrEmpty().not()) {
            for (i in baseData?.getSurveyInventoryList()!!) {
                i?.miscComment = i?.comments
                i?.damageComment = i?.damage
                i?.surveySequence = i?.sequence
                i?.inventoryItemName = i?.item
             //    Log.e("ItemName", i?.inventoryItemName.toString() + i?.isUserAdded.toString())
                if (i?.isUserAdded == true) {
                    Log.e("ItemName", i?.inventoryItemName.toString() + i?.isUserAdded.toString())
                    addUserAddedItems(i)
                }
            }
            sequenceDetailsDao.insertAll(baseData.getSurveyInventoryList())
        }

        // Get Survey Planning List
        if (baseData?.getSurveyPlanningList().isNullOrEmpty().not()) {
            if (baseData?.getSurveyPlanningList() != null && baseData?.getSurveyPlanningList()?.size!! > 0) {
                surveyPlanningListDao.deleteOption()
                for (data in baseData.getSurveyPlanningList()!!) {
                    if (data?.options != null && data?.options!!.size > 0) {
                        data?.surveyId = data?.surveyId!!
                        data?.surveyPlanningId = Random.nextInt()
                        //todo change planing id
                        surveyPlanningListDao.insert(data)
                    }
                }
            }
        }

        // Get Survey Comment List
        if (baseData?.getSurveyCommentList().isNullOrEmpty().not()) {
            commentsDetailDao.insertAll(baseData?.getSurveyCommentList())
        }

        // Get Survey Picture List
        if (baseData?.getSurveyPictureList().isNullOrEmpty().not()) {
            picturesDao.insertAll(baseData?.getSurveyPictureList())
        }

        // Get Survey Room  List
        if (baseData?.getRoomList().isNullOrEmpty().not()) {
            inventoryRoomDao.insertAll(baseData?.getRoomList())
        } else {
            throw Throwable("RoomList data not found")
        }

        if (baseData?.getAdditionalInfoList().isNullOrEmpty().not()) {
            baseData?.getAdditionalInfoList()?.forEach { aInfoModel ->
                if (aInfoModel?.getAdditionPersonalList().isNullOrEmpty().not()) {
                    aInfoModel?.getAdditionPersonalList()?.forEach { aModel ->
                        aModel?.surveyId = aInfoModel?.getSurveyId()?.toString()
                        aModel?.newRecord = false
                        additionalInfo.insert(aModel)
                    }
                }
                if (aInfoModel?.getpartnerList().isNullOrEmpty().not()) {
                    partnerAndServiceDao.insertAllPartnerList(aInfoModel?.getpartnerList())
                }
            }
        } else {
            //throw Throwable("AdditionalInfoList data not found")
        }

        // Get Survey Filter Date
        if (baseData?.getFilterSurveyDateList().isNullOrEmpty().not()) {
            filterSurveyDao.insertAll(baseData?.getFilterSurveyDateList())
        } else {
            // throw Throwable("Filter Date data not found")
        }

        // Get signature list
        if (baseData?.getSignatureList().isNullOrEmpty().not()) {
            sequenceDetailSignatureDao.insertAll(baseData?.getSignatureList())
        } else {
            //throw Throwable("signature data not found")
        }
    }

    // For User Addeded Items
    private fun addUserAddedItems(data: SequenceDetailsModel?) {
        var intLastId: Long = 0;
        var inventoryItemId =   System.currentTimeMillis().toInt()
        if(!data!!.inventoryNameId.isNullOrEmpty()){
            inventoryItemId =  data!!.inventoryNameId!!.toInt()
        }
        intLastId = inventoryItemListDao.getInventoryListLastId()
        intLastId += 1;
        val inventorytype = InventoryTypeSelectionModel(
            intLastId,
            inventoryItemId,
            data?.inventoryTypeId?.toInt(),
            data?.inventoryItemName,
            0,
            data?.volume?.toDouble(),
            0,
            0,
            0,
            "",
            data?.surveyId?.toInt(),
            data?.packingType?.toInt(),
             "",
            true,
            isCurrentItem = false,
            isCustomize = true,
            inventoryValue = null,
            data?.inventoryNameId
        )
        //  newlyAddedInventoryType.value = inventorytype
        inventoryItemListDao.insert(inventorytype).also {
            Log.e("Inserted Id:", it.toString())
        }
    }


    public fun navigateToDashboard() {
        if (navController != null) {
            navController.navigate(R.id.dashboardFragment)
        }
    }

    /**
     * Check User Login first time for sync process
     */
    private fun checkUserIsFirstTime() {
        if (session.getIsFirstTimeKey()) {
            if (Utility.isNetworkConnected(this)) {
                syncandSendDataToServer()
            }
        }
    }

    class CheckConnection(private val internetConnectionListener: InternetConnectionListener) :
        BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            try {
                if (Utility.isNetworkConnected(context)) {
                    internetConnectionListener.onConnected()
                    Log.e("Connection", "Connected to Internet ")
                } else {
                    internetConnectionListener.onDisConnected()
                    Log.e("Connection", "No Internet Connection!!! ")
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }

    }

    private val internetConnectionListener = object : InternetConnectionListener {
        override fun onConnected() {
            Log.e("netConnected", "true")
        }

        override fun onDisConnected() {
            Log.e("netConnected", "false")
        }
    }

    private fun registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(
                mNetworkReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
    }

    private fun unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun setupNavControl() {
        navigationView.setupWithNavController(navController) //Setup Drawer navigation with navController
    }

    override fun onSupportNavigateUp(): Boolean { //Setup appBarConfiguration for back arrow
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    override fun onBackPressed() {
        when {
            drawer.isDrawerOpen(GravityCompat.START) -> {
                drawer.closeDrawer(GravityCompat.START)
            }

            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onOkPressed(dialog: MessageDialog) {
        //  db.clearAllTables()
        session.clearSession()
        dialog.dismiss()
        goToActivityAndClearTask<SignInActivity>()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START)
                } else {
                    drawer.openDrawer(GravityCompat.START)
                }
                true
            }

            R.id.actionSync -> {
                if (Utility.isNetworkConnected(this))
                //  syncDataFromServer(true, false)
                    syncandSendDataToServer()
                else
                    Toast.makeText(
                        this,
                        resources.getString(R.string.nointernetconnection),
                        Toast.LENGTH_SHORT
                    ).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun Disposable.collect() {
        compositeDisposable.add(this)
    }


    // For Upload Sync data in Single AP

    private fun callSavePartnerApi(saveDetails: String) {
        Networking(this).getServices()
            .savePartnerServiceList(JsonParser().parse(saveDetails.toString()) as JsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<Boolean>>() {
                override fun onSuccess(response: BaseModel<Boolean>) {
                    Log.d("PartnerServiceList", response.message)
                    partnerTempDao.deletePartnerTable()
                }


                override fun onFailed(code: Int, message: String) {
                    Log.d("PartnerServiceList", message)
                }
            })
    }

    private fun updateInventoryData(
        partMap: MutableMap<String, RequestBody>,
        photos: MutableList<MultipartBody.Part>,
        id: String
    ) {
        Networking(this)
            .getServices()
            .postUpdateSequenceInventoryItemDetails(partMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<SavedSequenceDetailsModel>() {
                override fun onSuccess(response: SavedSequenceDetailsModel) {
                    // checkSequenceDetailsList()
                    Log.d("Inventory", "onSuccess--$response")

                    sequenceDetailsDao.updateIsChangedField(id, false)
                }

                override fun onFailed(code: Int, message: String) {
                    Log.d("Inventory", "onFailure--$message")
                }
            })
    }

    private fun deleteSurveySequence(surveySequenceId: String, surveyId: String?) {
        Networking(this)
            .getServices()
            .deleteSurveySequence(surveySequenceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<SurveyPlanningListModel>>>() {
                override fun onSuccess(response: BaseModel<List<SurveyPlanningListModel>>) {
                    getSequenceList(surveyId)
                }

                override fun onFailed(code: Int, message: String) {
                }
            })

    }

    private fun deleteLegBySurveySequence(surveySequenceLegId: String, surveySequenceId: String?) {

        Networking(this).getServices().deleteSequenceLegById(surveySequenceLegId)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BasicModel>() {
                override fun onSuccess(response: BasicModel) {

                    if (response.success.toString() == "true") {
                        getSequenceLegList(surveySequenceId.toString())
                    }
                }

                override fun onFailed(code: Int, message: String) {

                }
            })
    }


    private fun saveSequenceData(data: SaveSequenceModel) {
        Networking(this)
            .getServices()
            .saveSequenceData(data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<SaveSequenceModel>>>() {
                override fun onSuccess(response: BaseModel<List<SaveSequenceModel>>) {
                }

                override fun onFailed(code: Int, message: String) {
                }
            })

    }

    private fun checkLegsData() {
        val showLegsList = showLegsDao.getShowLegsListAll()
        val legListSync: ArrayList<ShowLegsModel> = ArrayList()
        for (data: ShowLegsModel in showLegsList) {
            val id = data.surveySequenceLegId
            if (data.surveySequenceLegId!!.contains("ADD") || data.isChangedField == true) {
                if (data.originAddressId?.contains("ADD") == true) {
                    data.originAddressId = "0"
                }
                if (data.destinationAddressId?.contains("ADD") == true) {
                    data.destinationAddressId = "0"
                }
                if (data.surveySequenceId?.contains("ADD") == true) {
                    data.surveySequenceId = "0"
                }

                if (data.surveySequenceLegId!!.contains("ADD")) {
                    data.surveySequenceLegId = "0"
                    saveLegsData(data)
                } else {
                    saveLegsData(data)
                }

                legListSync.add(data)
                showLegsDao.delete(id!!)
            } else if (data.isDelete == true) {
                deleteLegBySurveySequence(data.surveySequenceLegId!!, data.surveySequenceId!!)
                showLegsDao.delete(id!!)
            }
        }
        obj_syncAllData.setSequenceLegList(legListSync)
        val json = Gson().toJson(obj_syncAllData)
        Log.e("SyncAddress", json.toString())
    }


    private fun saveLegsData(data: ShowLegsModel) {
        Networking(this)
            .getServices()
            .saveSequenceLeg(data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<ShowLegsModel>>>() {
                override fun onSuccess(response: BaseModel<List<ShowLegsModel>>) {
                    //    getSequenceLegList(data.surveySequenceId)
                }

                override fun onFailed(code: Int, message: String) {
                    Log.e("saveLegsData", "onFailed$message")
                }
            })
    }

    // Upload Newly Added Room
    private fun uploadNewlyRoom() {
        try {
            val getNewlyAddedRoomList = inventoryRoomDao.getNewlyAddedRoom(true)
            val roomListSync: ArrayList<SaveUserRoomSync> = ArrayList()
            if (getNewlyAddedRoomList != null && getNewlyAddedRoomList.isNotEmpty()) {
                for (i in getNewlyAddedRoomList) {
                    val params = HashMap<String, Any>()
                    params["roomId"] = i.roomId
                    params["SurveyId"] = i.surveyId!!
                    params["roomName"] = i.room!!
                    params["UserRoomId"] = i.userRoomId

                    var userRoom: SaveUserRoomSync = SaveUserRoomSync()
                    userRoom.setRoomId(i.roomId)
                    userRoom.setSurveyId(i.surveyId!!)
                    userRoom.setRoomName(i.room!!)
                    userRoom.setUserRoomId(i.userRoomId)

                    roomListSync.add(userRoom)

                    Networking(this)
                        .getServices()
                        .saveRoomName(Networking.wrapParams(params))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : CallbackObserver<BaseModel<Boolean>>() {
                            override fun onSuccess(response: BaseModel<Boolean>) {
                                Log.e("Room", "Success")
                                if (i.id != null) {
                                    inventoryRoomDao.updateNewRoomName(false, i.id.toString())
                                }

                            }

                            override fun onFailed(code: Int, message: String) {
                                Log.e("Room", "Fail")
                            }
                        })
                }

                obj_syncAllData.setUserRoom(roomListSync)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun saveSignature(jsonObject: JsonObject, surveyId: Int) {
        if (Utility.isNetworkConnected(this)) {
            Networking(this)
                .getServices()
                .saveSignature(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<Any>>() {
                    override fun onSuccess(response: BaseModel<Any>) {
                        sequenceDetailSignatureDao.delete(surveyId.toString())
                    }

                    override fun onFailed(code: Int, message: String) {

                    }
                })
        }
    }




    private fun deleteAddress(surveyAddressId: String?, surveyId: String?) {
        Networking(this)
            .getServices()
            .deleteSurveyAddress(
                surveyAddressId.toString(),
                session.user!!.userId!!
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<AddressListModel>>>() {
                override fun onSuccess(response: BaseModel<List<AddressListModel>>) {
                    checkAddressList(surveyId.toString())
                }

                override fun onFailed(code: Int, message: String) {

                }


            })
    }
    private fun deletePicture(surveyPicId: String?, surveyId: String?) {
        Networking(this)
            .getServices()
            .deleteSurveyPicture(surveyPicId!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<PicturesModel>>>() {
                override fun onSuccess(response: BaseModel<List<PicturesModel>>) {
                }

                override fun onFailed(code: Int, message: String) {
                    Log.e("deletePicture", "onFailed$message")
                }
            })

    }

    private fun savePicture(data: PicturesModel) {
        val imgFile = File(data.pictureName!!)
        val reqFile = imgFile.asRequestBody("image/*".toMediaTypeOrNull())
        val multipartBody =
            MultipartBody.Part.createFormData("ImageData", imgFile.name, reqFile)


        val filePath: String = imgFile!!.path
        val bitmap = BitmapFactory.decodeFile(filePath)
        val stream = ByteArrayOutputStream()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        }
        // get the base 64 string
        val imgString = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)

        val partMap: Map<String, RequestBody> = mapOf(
            "SurveyPicId" to "0".toRequestBody("text/plain".toMediaTypeOrNull()),
            "SurveyId" to data.surveyId!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            "PictureName" to data.pictureName!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            "CreatedBy" to data.createdBy!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            "ImageData" to imgString!!.toRequestBody("text/plain".toMediaTypeOrNull())
        )

        Networking(this)
            .getServices()
            .saveSurveyPictureBase(partMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<PicturesModel>>>() {
                override fun onSuccess(response: BaseModel<List<PicturesModel>>) {
                    Log.e("savePicture", "onSuccess" + response.success)
                    //  checkPicturesList(data.surveyId!!)
                }

                override fun onFailed(code: Int, message: String) {
                    Log.e("savePicture", "onFailed$message")
                }
            })
    }

    private fun saveAddress(data: AddressListModel) {
        Networking(this)
            .getServices()
            .saveSurveyAddress(data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<AddressListModel>>>() {
                override fun onSuccess(response: BaseModel<List<AddressListModel>>) {
                    Log.e("saveAddress", "onSuccess" + response.success)
                    checkAddressList(data.surveyId!!)
                }

                override fun onFailed(code: Int, message: String) {
                    Log.e("saveAddress", "onFailed$message")
                }
            })

    }


    private fun setSyncWorkerMethod() {

        checkDashboard()
        checkDashboard2()

        checkInsuranceRequirementList()
        Thread.sleep(1000)

        checkSequenceLegsTypeList()
        Thread.sleep(1000)

        checkLegAccessList()
        Thread.sleep(1000)

        checkDeliveryTypeList()
        Thread.sleep(1000)

        checkCustomerList()
        Thread.sleep(1000)

        checkReferenceList()
        Thread.sleep(1000)


    }

    // On Submit Click
    public fun onSubmitClick(surveyId: String) {
        submitSurveyAllData(surveyId)
    }

    public fun setSelectedSequencePosition(): Int {
        return selectedSequencePosition
    }

    public fun changeSelectedSequencePosition(position: Int) {
        selectedSequencePosition = position
    }


    // To Submit All Data Related Survey
    fun submitSurveyAllData(surveyId: String) {
        if (Utility.isNetworkConnected(this@DashboardActivity)) {
            showProgressbar()
            syncManager?.checkSyncData(surveyId, object : SyncCallback {
                override fun onSyncFailed() {
                    session.storeIsFirstTimeKey(false)
                    isSyncProgress = false
                    hideProgress()
                    FragmentDashboard().onSubmitButtonClick(
                        surveyId,
                        this@DashboardActivity
                    )
                }

                override fun onSyncComplete() {
                    session.storeIsFirstTimeKey(false)
                    isSyncProgress = false
                    hideProgress()
                    FragmentDashboard().onSubmitButtonClick(
                        surveyId,
                        this@DashboardActivity
                    )
                }
            })
        } else {
            Toast.makeText(
                this@DashboardActivity,
                "No Internet Connection",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    public fun onRefreshDashboard(context: BaseActivity) {

    }

    // To Submit All Data Related Survey
    fun syncandSendDataToServer() {
        if (Utility.isNetworkConnected(this@DashboardActivity)) {

            showProgressbar()
            syncManager?.checkSyncData("", object : SyncCallback {
                override fun onSyncFailed() {
                    session.storeIsFirstTimeKey(false)
                    if (Utility.isNetworkConnected(this@DashboardActivity)) {
                        class SaveToDataBase : AsyncTask<Void, Void, Void?>() {
                            override fun onPreExecute() {
                                isSyncProgress = true
                                //   showProgressbar()
                            }

                            override fun doInBackground(vararg p0: Void?): Void? {
                                setSyncWorkerMethod()
                                return null
                            }

                            override fun onPostExecute(result: Void?) {
                                super.onPostExecute(result)
                                isSyncProgress = false
                                hideProgress()
                            }

                        }
                        SaveToDataBase().execute()
                    } else {
                        isSyncProgress = true
                        Toast.makeText(
                            this@DashboardActivity,
                            "No Internet Connection",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onSyncComplete() {
                    session.storeIsFirstTimeKey(false)
                    if (Utility.isNetworkConnected(this@DashboardActivity)) {
                        class SaveToDataBase : AsyncTask<Void, Void, Void?>() {
                            override fun onPreExecute() {
                                isSyncProgress = true
                                //   showProgressbar()
                            }

                            override fun doInBackground(vararg p0: Void?): Void? {
                                setSyncWorkerMethod()
                                return null
                            }

                            override fun onPostExecute(result: Void?) {
                                super.onPostExecute(result)
//                                isSyncProgress = false
//                                hideProgress()
                            }

                        }
                        SaveToDataBase().execute()
                    } else {
                        isSyncProgress = true
                        Toast.makeText(
                            this@DashboardActivity,
                            "No Internet Connection",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })

        } else {
            Toast.makeText(
                this@DashboardActivity,
                "No Internet Connection",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun checkSurveyPlanningList(surveyId: String) {
        try {
            getPlanningDetailList(surveyId)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("checkSurveyPlanningList", "Exception" + e.localizedMessage)
            // hideProgressbar()
        }
    }

    private fun getPlanningDetailList(surveyId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getSurveyPlanningListBySurveyId(surveyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<SurveyPlanningListModel>>>() {
                    override fun onSuccess(response: BaseModel<List<SurveyPlanningListModel>>) {

                        //todo

                        if (response.data != null && response.data!!.size > 0) {

                            for (data: SurveyPlanningListModel in response.data!!) {
                                if (data.options != null && data.options!!.size > 0) {
                                    data.surveyId = data.surveyId
                                    data.surveyPlanningId = Random.nextInt()
                                    //todo change planing id
                                    surveyPlanningListDao.insert(data)
                                }
                            }
                            //surveyPlanningListDao.insertAll(response.data!!)
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("getPlanningDetailList", "onFailed$message")

                    }
                })
        }

    }

    private fun checkDashboard() {
        try {
            val jsonObject = JsonObject()
            jsonObject.addProperty("UserId", session.user?.userId)
            jsonObject.addProperty("PageSize", 100)
            jsonObject.addProperty("PageIndex", 1)
            jsonObject.addProperty("NextDay", 0)
            jsonObject.addProperty("LastDay", 0)
            jsonObject.addProperty("IsSubmitted", false)
            jsonObject.addProperty("SortBy", "")
            jsonObject.addProperty("DateFilter", "")
            jsonObject.addProperty(AppConstants.oldSubmitted, false)

            getSurveyDetailLists(jsonObject, true)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("checkDashboard", "Exception" + e.localizedMessage)
            //            hideProgressbar()

        }
    }

    private fun checkDashboard2() {
        try {
            val jsonObject = JsonObject()
            jsonObject.addProperty("UserId", session.user?.userId)
            jsonObject.addProperty("PageSize", 100)
            jsonObject.addProperty("PageIndex", 1)
            jsonObject.addProperty("NextDay", 0)
            jsonObject.addProperty("LastDay", 0)
            jsonObject.addProperty("IsSubmitted", false)
            jsonObject.addProperty("SortBy", "")
            jsonObject.addProperty("DateFilter", "")
            jsonObject.addProperty(AppConstants.oldSubmitted, true)

            getSurveyDetailLists(jsonObject, false)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("checkDashboard", "Exception" + e.localizedMessage)
            //            hideProgressbar()

        }
    }

    private fun checkAddressList(surveyId: String) {
        try {
            getAddressList(surveyId)
            getPermitTypeList()
            getAddressTypeList()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("checkAddressList", "Exception" + e.localizedMessage)
            //            hideProgressbar()

        }
    }

    private fun getSurveyDetailLists(jsonObject: JsonObject, isCall: Boolean) {
        if (isCall) {
            getSurveyDetailMasterApiResponse()
        }
    }

    private fun checkInventoryItemFromSurveyId(surveyId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getSurveyInventoryBySurveyId(surveyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object :
                    CallbackObserver<BaseModel<List<GetSurveyInventoryBySurveyId>>>() {
                    override fun onSuccess(response: BaseModel<List<GetSurveyInventoryBySurveyId>>) {

                        if (response.success.toString() == "true") {
                            if (response.data != null && response.data?.size!! > 0) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    for (i in response.data!!) {

                                        val inventorytype = InventoryTypeSelectionModel(
                                            i.SurveyInventoryId!!.toLong(),
                                            null,
                                            i.InventoryTypeId,
                                            i.InventoryItemName,
                                            0,
                                            i.Volume?.toDouble(),
                                            0,
                                            0,
                                            0,
                                            "",
                                            i.SurveyId,
                                            0,
                                            i.SurveyInventoryId!!.toString(),
                                            true,
                                            false,
                                            true,
                                        )

                                        Log.e("InventoryData", inventorytype.toString())

//                                        inventoryItemListDao.insert(inventorytype).also {
//                                            Log.e("Inserted Id:", it.toString())
//                                        }
                                    }
                                }
                            }

                        } else if (response.success.toString() == "false" && response.message.isNotEmpty()) {
                            Log.d("test", "test")
                        } else {
                            Log.d("test", "else")
                        }
                    }


                    override fun onFailed(code: Int, message: String) {
                        Log.e("SurveyInventoryItemList", "onFailed$message")
                    }
                })
        }
    }

    private fun getSurveyDetailList(surveyId: String) {
        Networking(this)
            .getServices()
            .getSequenceDetails(surveyId, AppConstants.defaultSequenceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<SequenceDetailsModel>>>() {
                override fun onSuccess(response: BaseModel<List<SequenceDetailsModel>>) {
                    val gson = Gson()
                    Log.e(
                        "ResponseSequence",
                        "SuveyId:" + surveyId + " " + gson.toJson(response.data)
                    )

                    try {
                        if (response.data != null)
                            if (response.data != null) {
                                for (i in response!!.data!!) {
                                    i.miscComment = i.comments
                                    i.damageComment = i.damage
                                    i.surveySequence = i.sequence
                                }
                            }
                        sequenceDetailsDao.insertAll(response.data)

                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailed(code: Int, message: String) {
                    Log.e("getSurveyDetailList", "onFailed$message")
                }
            })
    }

    private fun getEnquiryList() {

        Networking(this@DashboardActivity)
            .getServices()
            .getenquiryType()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<EnquiryTypeModel>>>() {
                override fun onSuccess(response: BaseModel<List<EnquiryTypeModel>>) {
                    enquiryListDao.insertAll(response.data)
                }

                override fun onFailed(code: Int, message: String) {
                }
            })

    }

    private fun getPropertyTypeList() {

        Networking(this@DashboardActivity)
            .getServices()
            .getPropertyType()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<PropertyTypeModel>>>() {
                override fun onSuccess(response: BaseModel<List<PropertyTypeModel>>) {

                    propertyTypeDao.insertAll(response.data)
                }

                override fun onFailed(code: Int, message: String) {

                }
            })
    }

    private fun getSurveySize() {

        Networking(this@DashboardActivity)
            .getServices()
            .getSurveySize()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<SurveySizeListModel>>>() {
                override fun onSuccess(response: BaseModel<List<SurveySizeListModel>>) {
                    surveySizeTypeDao.insertAll(response.data)
                }

                override fun onFailed(code: Int, message: String) {
                }
            })

    }

    private fun getRentalList() {

        Networking(this@DashboardActivity)
            .getServices()
            .getRentalList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<RentalTypeModel>>>() {
                override fun onSuccess(response: BaseModel<List<RentalTypeModel>>) {
                    rentalDao.insertAll(response.data)
                }

                override fun onFailed(code: Int, message: String) {
                }
            })

    }


    private fun checkCommentsList(surveyId: String, surveySequenceId: String) {
        try {
            getCommentById(surveyId, surveySequenceId)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("checkCommentsList", "Exception" + e.localizedMessage)
            //            hideProgressbar()

        }
    }

    private fun getCommentById(surveyId: String, surveySequenceId: String) {
        Networking(this)
            .getServices()
            .getSurveyCommentBySurveyId(surveyId, surveySequenceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<CommentsDetailModel>>() {
                override fun onSuccess(response: BaseModel<CommentsDetailModel>) {
                    if (response.data != null)
                        commentsDetailDao.insert(response.data!!)
                }

                override fun onFailed(code: Int, message: String) {
                    Log.e("getCommentById", "onFailed$message")
                }
            })
    }

    private fun getLanguageList() {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getLanguageList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<LanguageModel>>>() {
                    override fun onSuccess(response: BaseModel<List<LanguageModel>>) {
                        try {
                            if (response.data != null)
                                languageDao.insertAll(response.data)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("getLanguageList", "onFailed$message")
                    }
                })
        }

    }


    /**
     * Use to call multiple api related to master table
     * */
    private fun getMasterTableList() {
        try {
            getMasterList()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("checkMasterList", "Exception" + e.localizedMessage)
            //            hideProgressbar()

        }
    }

    private fun getMasterList() {

        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getMasterTableApi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<MasterTableModel>() {
                    override fun onSuccess(response: MasterTableModel) {
                        try {
                            if (response.data != null) {
                                when {
                                    response.data.activityCodeList != null && response.data.activityCodeList.isNotEmpty() -> {
                                        codeListDao.insertAll(response.data.activityCodeList)
                                    }
                                }
                                if (response.data.surveyActivityList != null && response.data.surveyActivityList.isNotEmpty()) {
                                    activityListDao.insertAll(response.data.surveyActivityList)
                                }
                                if (response.data.timeList != null && response.data.timeList.isNotEmpty()) {
                                    timeListDao.insertAll(response.data.timeList)
                                }
                                if (response.data.countryList != null && response.data.countryList.isNotEmpty()) {
                                    countryListDao.insertAll(response.data.countryList)
                                }
                                if (response.data.addressTypeList != null && response.data.addressTypeList.isNotEmpty()) {
                                    addressTypeListModelDao.insertAll(response.data.addressTypeList)
                                }
                                if (response.data.allowanceTypeList != null && response.data.allowanceTypeList.isNotEmpty()) {
                                    allowanceTypeDao.insertAll(response.data.allowanceTypeList)
                                }
                                if (response.data.enquiryTypeList != null && response.data.enquiryTypeList.isNotEmpty()) {
                                    enquiryListDao.insertAll(response.data.enquiryTypeList)
                                }
                                if (response.data.distanceUnitList != null && response.data.distanceUnitList.isNotEmpty()) {
                                    distanceUnitTypeDao.insertAll(response.data.distanceUnitList)
                                }
                                if (response.data.floorList != null && response.data.floorList.isNotEmpty()) {
                                    inventoryFloorDao.insertAll(response.data.floorList)
                                }

                                if (response.data.inventoryTypeList != null && response.data.inventoryTypeList.isNotEmpty()) {
                                    inventoryListDao.insertAll(response.data.inventoryTypeList)
                                }
                                if (response.data.languageList != null && response.data.languageList.isNotEmpty()) {
                                    languageDao.insertAll(response.data.languageList)
                                }
                                if (response.data.packingMethodList != null && response.data.packingMethodList.isNotEmpty()) {
                                    packingMethodDao.insertAll(response.data.packingMethodList)
                                }
                                if (response.data.sequenceTypeList != null && response.data.sequenceTypeList.isNotEmpty()) {
                                    sequenceTypeDao.insertAll(response.data.sequenceTypeList)
                                }
                                if (response.data.shipmentMethodList != null && response.data.shipmentMethodList.isNotEmpty()) {
                                    shippingMethodDao.insertAll(response.data.shipmentMethodList)
                                }
                                if (response.data.rentalPropertyList != null && response.data.rentalPropertyList.isNotEmpty()) {
                                    rentalDao.insertAll(response.data.rentalPropertyList)
                                }
                                if (response.data.propertyTypeList != null && response.data.propertyTypeList.isNotEmpty()) {
                                    propertyTypeDao.insertAll(response.data.propertyTypeList)
                                }
                                if (response.data.surveyTypeList != null && response.data.surveyTypeList.isNotEmpty()) {
                                    surveyTypeDao.insertAll(response.data.surveyTypeList)
                                }
                                if (response.data.surveySizeList != null && response.data.surveySizeList.isNotEmpty()) {
                                    surveySizeTypeDao.insertAll(response.data.surveySizeList)
                                }

                                if (response.data.inventoryItemList != null && response.data.inventoryItemList.isNotEmpty()) {
                                    inventoryItemListDao.insertAll(response.data.inventoryItemList)
                                }

                                if (response.data.crateTypeList != null && response.data.crateTypeList.isNotEmpty()) {
                                    crateTypeDataDao.insertAll(response.data.crateTypeList)
                                }

                                if (response.data.permitList != null && response.data.permitList.isNotEmpty()) {
                                    legPermitDao.insertAll(response.data.permitList)
                                }

                            }

                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("getCountryList", "onFailed$message")
                    }
                })
        }
    }

    private fun getCountryList() {

        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getCountryList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<CountryListModel>>>() {
                    override fun onSuccess(response: BaseModel<List<CountryListModel>>) {
                        try {
                            if (response.data != null)
                                countryListDao.insertAll(response.data)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("getCountryList", "onFailed$message")
                    }
                })
        }
    }

    private fun checkSequenceTypeList() {
        try {
            getSequenceTypeList()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("checkSequenceTypeList", "Exception" + e.localizedMessage)
            //            hideProgressbar()

        }
    }

    private fun getSequenceTypeList() {

        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getSequenceTypeList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<SequenceTypeModel>>>() {
                    override fun onSuccess(response: BaseModel<List<SequenceTypeModel>>) {
                        try {
                            if (response.data != null)
                                sequenceTypeDao.insertAll(response.data)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("getSequenceTypeList", "onFailed$message")
                    }
                })
        }


    }

    private fun checkInsuranceRequirementList() {
        try {
            getInsuranceRequiredGroupList()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("InsuranceRequirement", "Exception" + e.localizedMessage)
            //            hideProgressbar()

        }
    }

    private fun getInsuranceRequiredGroupList() {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getInsuranceRequirementList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<InsuranceRequirementModel>>>() {
                    override fun onSuccess(response: BaseModel<List<InsuranceRequirementModel>>) {
                        try {
                            if (response.data != null)
                                insuranceRequirementDao.insertAll(response.data)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("InsuranceRequiredGroup", "onFailed$message")
                    }
                })
        }

    }

    private fun getPackingMethodList() {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getPackingMethodListForDDL()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<PackingMethodModel>>>() {
                    override fun onSuccess(response: BaseModel<List<PackingMethodModel>>) {
                        try {
                            if (response.data != null)
                                packingMethodDao.insertAll(response.data)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("PackingMethodList", "onFailed$message")
                    }
                })
        }

    }

    private fun checkSequenceLegsTypeList() {
        try {
            getSequenceLegsTypeList()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("checkSequenceLegsType", "Exception" + e.localizedMessage)
            //  hideProgressbar()
        }
    }

    private fun getSequenceLegsTypeList() {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getSequenceLegtypeListForDDL()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<SequenceLegsTypeModel>>>() {
                    override fun onSuccess(response: BaseModel<List<SequenceLegsTypeModel>>) {
                        try {
                            if (response.data != null)
                                sequenceLegsTypeDao.insertAll(response.data)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("getSequenceLegsTypeList", "onFailed$message")
                    }
                })
        }
    }

    private fun checkLegAccessList() {
        try {
            getSequenceLegsOriginAccessList()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("checkLegAccessList", "Exception" + e.localizedMessage)
        }
    }

    private fun getSequenceLegsOriginAccessList() {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getSequenceLegAccessList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<LegAccessModel>>>() {
                    override fun onSuccess(response: BaseModel<List<LegAccessModel>>) {
                        try {
                            if (response.data != null)
                                legAccessDao.insertAll(response.data)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("SequenceLegsAccess", "onFailed$message")
                    }
                })
        }

    }

    private fun getSequenceOriginLegsPermitList() {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getSequenceLegPermitList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<LegPermitModel>>>() {
                    override fun onSuccess(response: BaseModel<List<LegPermitModel>>) {
                        try {
                            if (response.data != null)
                                legPermitDao.insertAll(response.data)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("OriginLegsPermit", "onFailed$message")
                    }
                })
        }

    }


    /** For Get Distance Unit Type List  API Response */
    private fun getDistanceUnitTypeList() {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getUnitTypeList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<DistanceUnitTypeModel>>>() {
                    override fun onSuccess(response: BaseModel<List<DistanceUnitTypeModel>>) {
                        try {
                            if (response.data != null)
                                distanceUnitTypeDao.insertAll(response.data)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("getDistanceUnitTypeList", "onFailed$message")
                    }
                })
        }

    }

    private fun checkAllowanceTypeList() {
        try {
            getAllowanceTypeList()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("checkAllowanceTypeList", "Exception" + e.localizedMessage)
            //            hideProgressbar()

        }
    }

    /** For Get Allowance Type  API Response */
    private fun getAllowanceTypeList() {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getallowanceTypeList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<AllowanceTypeModel>>>() {
                    override fun onSuccess(response: BaseModel<List<AllowanceTypeModel>>) {
                        try {
                            if (response.data != null)
                                allowanceTypeDao.insertAll(response.data)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("getAllowanceTypeList", "onFailed$message")
                    }
                })
        }

    }

    /** For Get Check Delivery Type List  API Response */
    private fun checkDeliveryTypeList() {
        try {
            getDeliveryTypeList()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("checkDeliveryTypeList", "Exception" + e.localizedMessage)
            //            hideProgressbar()

        }
    }

    /** For Get Delivery Type List  API Response */
    private fun getDeliveryTypeList() {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getDeliveryTypeList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<DeliveryTypeModel>>>() {
                    override fun onSuccess(response: BaseModel<List<DeliveryTypeModel>>) {
                        try {
                            if (response.data != null)
                                deliveryTypeDao.insertAll(response.data)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("getDeliveryTypeList", "onFailed$message")
                    }
                })
        }

    }


    /** For Get Inventory Floor List  API Response */
    private fun getInventoryFloorList() {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getInventoryFloorListForDDL()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<InventoryFloorModel>>>() {
                    override fun onSuccess(response: BaseModel<List<InventoryFloorModel>>) {
                        try {
                            if (response.data != null)
                                inventoryFloorDao.insertAll(response.data)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("getInventoryFloorList", "onFailed$message")
                    }
                })
        }

    }

    /** For Get Inventory Room List  API Response */
    private fun getInventoryRoomList(surveyId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getInventoryRoomListForDDL(Integer.parseInt(surveyId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<InventoryRoomModel>>>() {
                    override fun onSuccess(response: BaseModel<List<InventoryRoomModel>>) {
                        try {
                            if (response.data != null) {
                                inventoryRoomDao.insertAll(response.data)
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("getInventoryRoomList", "onFailed$message")
                    }
                })
        }

    }

    private fun checkCustomerList() {
        try {
            getCustomerList()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("checkCustomerList", "Exception" + e.localizedMessage)
            //  hideProgressbar()

        }
    }

    /** For Get Customer List  API Response */
    private fun getCustomerList() {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getCustomerList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<CustomerListModel>>>() {
                    override fun onSuccess(response: BaseModel<List<CustomerListModel>>) {
                        try {
                            if (response.data != null)
                                customerListDao.insertAll(response.data)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("getCustomerList", "onFailed$message")
                    }
                })
        }

    }

    /** For Get Partner List API Response */
    private fun getPartnerList(surveyId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getPartnerServiceList(surveyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<PartnerServiceModel>() {
                    override fun onSuccess(response: PartnerServiceModel) {
                        if (response.message == "Success") {
                            if (response.data != null && !response.data.additionList.isNullOrEmpty()) {
                                for (i in response.data.additionList) {
                                    i.surveyId = surveyId
                                    i.newRecord = false
                                    additionalInfo.insert(i)
                                }
                            }
                        }
                        partnerAndServiceDao.insertAllPartnerList(response.data?.partnerList)
                    }

                    override fun onFailed(code: Int, message: String) {
                    }
                })
        }
    }

    private fun checkReferenceList() {
        try {
            getReferenceList()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("checkReferenceList", "Exception" + e.localizedMessage)
            // hideProgressbar()

        }
    }

    /** For Get Reference List API Response */
    private fun getReferenceList() {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getReferenceList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<ReferenceListModel>>>() {
                    override fun onSuccess(response: BaseModel<List<ReferenceListModel>>) {
                        try {
                            if (response.data != null)
                                referenceListDao.insertAll(response.data)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("getReferenceList", "onFailed$message")
                    }
                })
        }

    }

    /** For Get Inventory List API Response */
    private fun getInventoryList() {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getInventoryTypeList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<SurveyInventoryList>>>() {
                    override fun onSuccess(response: BaseModel<List<SurveyInventoryList>>) {
                        try {
                            if (response.data != null) {
                                inventoryListDao.insertAll(response.data)
                                Log.e("getInventoryItemList", "1 Size: " + response.data?.size)
                            }

                            CoroutineScope(Dispatchers.IO).launch {
                                async {
                                    for (data: SurveyInventoryList in response.data!!) {
                                        try {
                                            getSurveyInventoryItemList(
                                                data.inventoryTypeId.toString(),
                                                ""
                                            )
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        withContext(Dispatchers.IO) {
                                            Thread.sleep(1000)
                                        }
                                    }
                                }
                            }

                        } catch (e: java.lang.Exception) {
                            Log.e("getInventoryList", "3 issue")
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("getInventoryList", "onFailed$message")
                    }
                })
        }

    }

    // Call api for code list based on activity
    private fun getCodeList() {
        Networking(this@DashboardActivity)
            .getServices()
            .getCodePlanning()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<CodePlanningModel>>>() {
                override fun onSuccess(response: BaseModel<List<CodePlanningModel>>) {
                    if (response.data != null) {
                        codeListDao.insertAll(response.data)
                    }
                }

                override fun onFailed(code: Int, message: String) {

                }
            })
    }

    private fun getTimeList() {
        Networking(this@DashboardActivity)
            .getServices()
            .getTime()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<TimeModel>>>() {
                override fun onSuccess(response: BaseModel<List<TimeModel>>) {
                    if (response.data != null) {
                        timeListDao.insertAll(response.data)
                    }
                }

                override fun onFailed(code: Int, message: String) {

                }
            })
    }

    /** For Get Activity List
     *  API Response */
    private fun getActivityList() {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getAcitivityListPlanning()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<ActivityListPlanningModel>>>() {
                    override fun onSuccess(response: BaseModel<List<ActivityListPlanningModel>>) {
                        if (response.data != null) {
                            activityListDao.insertAll(response.data)
                            Log.e("getActivityItemList", "1 Size: " + response.data?.size)
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("getActivityItemList", "onFailed$message")
                    }
                })
        }
    }

    /** For Survey Inventory Item List */
    private fun getSurveyInventoryItemList(itemId: String, surveyId: String) {
        Networking(this)
            .getServices()
            .getInventoryItemList(itemId, surveyId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CallbackObserver<BaseModel<List<InventoryTypeSelectionModel>>>() {
                override fun onSuccess(response: BaseModel<List<InventoryTypeSelectionModel>>) {


                    if (response.data != null) {
                        inventoryItemListDao.insertAll(response.data)
                    }

                }

                override fun onFailed(code: Int, message: String) {
                    Log.e("SurveyInventoryItemList", "onFailed$message")
                }
            })
    }

    /** For Sequence List API */
    private fun getSequenceList(surveyId: String?) {

        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getSequenceList(surveyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<SaveSequenceModel>>>() {
                    override fun onSuccess(response: BaseModel<List<SaveSequenceModel>>) {
                        try {
                            if (response.data != null)
                                saveSequenceDao.insertAll(response.data)

                            for (data: SaveSequenceModel in response.data!!) {
                                checkShowLegsList(data.surveySequenceId)
                                //   Thread.sleep(1000)
                                checkCommentsList(surveyId!!, data.surveySequenceId)
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("getSequenceList", "onFailed$message")
                    }
                })
        }

    }

    /** For Check Show Legs List */
    private fun checkShowLegsList(surveySequenceId: String) {
        try {
            getSequenceLegList(surveySequenceId)
            // getLegListBySequence(surveySequenceId)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("checkShowLegsList", "Exception" + e.localizedMessage)
            //            hideProgressbar()

        }
    }

    private fun getSequenceLegList(surveySequenceId: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getSequenceLegList(surveySequenceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<ShowLegsModel>>>() {
                    override fun onSuccess(response: BaseModel<List<ShowLegsModel>>) {
                        try {
                            if (response.data != null)
                                showLegsDao.insertAll(response.data)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("getSequenceLegList", "onFailed$message")
                    }
                })
        }

    }

    /** Used in Address module*/
    private fun getPermitTypeList() {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getPermitType()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<PermitTypeListModel>>>() {
                    override fun onSuccess(response: BaseModel<List<PermitTypeListModel>>) {

                        permitTypeListModelListDao.insertAll(response.data)
                    }

                    override fun onFailed(code: Int, message: String) {

                    }
                })
        }
    }

    /** Used in Address module*/
    private fun getAddressTypeList() {
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getAddressTypeList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<AddressTypeListModel>>>() {
                    override fun onSuccess(response: BaseModel<List<AddressTypeListModel>>) {
                        addressTypeListModelDao.insertAll(response.data)
                    }

                    override fun onFailed(code: Int, message: String) {
                    }
                })
        }
    }

    /** Used in Address module*/
    private fun getAddressList(surveyID: String) {
        Log.e("UserId", session.user!!.userId!!)
        CoroutineScope(Dispatchers.IO).launch {
            Networking(this@DashboardActivity)
                .getServices()
                .getSurveyAddressListBySurveyId(surveyID, session.user!!.userId!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<BaseModel<List<AddressListModel>>>() {
                    override fun onSuccess(response: BaseModel<List<AddressListModel>>) {
                        try {
                            if (response.data != null)
                                addressListDao.insertAll(response.data)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(code: Int, message: String) {
                        Log.e("getAddressList", "onFailed$message")
                    }
                })
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkChanges()
        if (::jobRepeatCallBack.isInitialized && jobRepeatCallBack.isActive) {
            jobRepeatCallBack.cancel()
        }
    }

    fun getForegroundFragment(): Fragment? {
        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.navHostFragmentPickford)
        return navHostFragment?.childFragmentManager?.fragments?.firstOrNull()
    }
}