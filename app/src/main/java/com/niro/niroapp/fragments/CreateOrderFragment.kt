package com.niro.niroapp.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import carbon.dialog.ProgressDialog
import carbon.widget.Button
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.niro.niroapp.R
import com.niro.niroapp.activities.MainActivity
import com.niro.niroapp.databinding.CreateOrderFragmentBinding
import com.niro.niroapp.firebase.FileType
import com.niro.niroapp.firebase.FileUploadResponseHandler
import com.niro.niroapp.firebase.FirebaseFileUploadService
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.CommodityItem
import com.niro.niroapp.models.responsemodels.UserType
import com.niro.niroapp.users.fragments.setReadOnly
import com.niro.niroapp.utils.*
import com.niro.niroapp.viewmodels.CreateOrderViewModel
import com.niro.niroapp.viewmodels.factories.CreateOrderViewModelFactory
import java.io.File
import java.io.IOException

class CreateOrderFragment : Fragment(), DateChangeListener,FileUploadResponseHandler, OnBackPressedListener {

    private var viewModel: CreateOrderViewModel? = null
    private var mSelectedUserContactId: String? = null
    private var mSelectedUserContactType  : String? = null
    private lateinit var bindingCreateOrderFragment: CreateOrderFragmentBinding
    private var mProgressDialog: ProgressDialog? = null
    private var mCapturedPath : String? = null
    private var mCurrentUserId : String? = null
    private var mDialog : AlertDialog? = null
    private lateinit var firebaseAnalytics : FirebaseAnalytics

    companion object {
        fun newInstance() = CreateOrderFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mSelectedUserContactId = it.getString(NiroAppConstants.ARG_USER_CONTACT_ID)
            mSelectedUserContactType = it.getString(NiroAppConstants.ARG_USER_CONTACT_TYPE)
            mCurrentUserId = it.getString(NiroAppConstants.ARG_CURRENT_USER_ID)
        }
        firebaseAnalytics = Firebase.analytics
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingCreateOrderFragment =
            DataBindingUtil.inflate(inflater, R.layout.create_order_fragment, container, false)
        bindingCreateOrderFragment.lifecycleOwner = viewLifecycleOwner
        requireActivity().viewModelStore.clear()

        firebaseAnalytics.setCurrentScreen(requireActivity(),getString(R.string.title_create_order),null)

        return bindingCreateOrderFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initializeViewModel()

        setPageTitle()

        updateData()

        onCommoditiesSelected()
        makeFieldsReadOnly()
        initializeFocusChangeListeners()
        initializeClickListeners()
    }


    private fun onCommoditiesSelected() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<CommodityItem>>(NiroAppConstants.ARG_SELECTED_COMMODITIES)?.observe(
            viewLifecycleOwner, Observer {
                viewModel?.getSelectedCommodity()?.value = it
                updateValuesInViews()
            }
        )

    }


    private fun initializeViewModel() {
        viewModel = activity?.let { CreateOrderViewModelFactory(mCurrentUserId).getViewModel(mCurrentUserId, it) }
        bindingCreateOrderFragment.createOrderVM = viewModel

        bindingCreateOrderFragment.etReceivingDate.hint = if(NiroAppUtils.getCurrentUser(requireContext()).userType == UserType.COMMISSION_AGENT.name )
            getString(R.string.hint_receiving_date)
        else getString(R.string.hint_dispatch_date)
    }

    private fun setPageTitle() {
       if(activity is MainActivity) (requireActivity() as MainActivity).setToolbarTitleAndImage(getString(R.string.title_create_order),R.drawable.ic_new_order)
    }


    private fun updateData() {
        viewModel?.getSelectedContactId()?.value = mSelectedUserContactId
        viewModel?.getSelectedContactType()?.value = mSelectedUserContactType
        viewModel?.getCurrentUserId()?.value = mCurrentUserId

    }


    private fun updateValuesInViews() {
        bindingCreateOrderFragment.etEnterCommodity.setText(viewModel?.getSelectedCommodityDisplayName()?.value ?: "")
        bindingCreateOrderFragment.etReceivingDate.setText(viewModel?.getReceivingDateDisplayValue()?.value ?: "")
        bindingCreateOrderFragment.etTotalAmount.setText(viewModel?.getOrderAmountDisplayValue()?.value ?: "")
        loadImages()

    }

    private fun loadImages() {

        if(viewModel?.getOrderImagesAbsolutePath()?.value.isNullOrEmpty()) return

        loadImage(viewModel?.getOrderImagesAbsolutePath()?.value?.get(0))
    }


    private fun makeFieldsReadOnly() {
        bindingCreateOrderFragment.etEnterCommodity.setReadOnly(true)
        bindingCreateOrderFragment.etReceivingDate.setReadOnly(true)
        bindingCreateOrderFragment.etAddImage.setReadOnly(true)
    }


    private fun initializeClickListeners() {

        NiroAppUtils.setBackPressedCallback(requireActivity(),viewLifecycleOwner,this)

        bindingCreateOrderFragment.root.setOnTouchListener { v, event -> NiroAppUtils.hideKeyBoard(v,context) }
        bindingCreateOrderFragment.etReceivingDate.setOnClickListener { openDatePicker() }

        bindingCreateOrderFragment.etEnterCommodity.setOnClickListener { launchSelectCommodityFragment() }

        bindingCreateOrderFragment.etAddImage.setOnClickListener { checkForCameraPermissionOrOpenCamera() }
        bindingCreateOrderFragment.btnCreateOrder.setOnClickListener { createOrder() }

    }


    private fun initializeFocusChangeListeners() {
        bindingCreateOrderFragment.etEnterCommodity.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && (viewModel?.validateSelectedCommodity()?.value ?: 0 > 0)) {
                bindingCreateOrderFragment.etEnterCommodity.error =
                    getString(viewModel?.validateSelectedCommodity()?.value!!)
            }
        }

        bindingCreateOrderFragment.etTotalAmount.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && (viewModel?.validateOrderAmount()?.value ?: 0 > 0)) {
                bindingCreateOrderFragment.etTotalAmount.error =
                    getString(viewModel?.validateOrderAmount()?.value!!)
            }
        }

    }


    private fun launchSelectCommodityFragment() {
        NiroAppUtils.hideKeyBoard(bindingCreateOrderFragment.root,context)
        findNavController().navigate(
            R.id.navigation_commodities_fragment, bundleOf(
                NiroAppConstants.PREVIOUS_SCREEN_ID to R.id.navigation_create_order,
                NiroAppConstants.ARG_ALLOW_MULTISELECT to false
            )
        )
    }



    private fun askForCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {

        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.CAMERA),
                NiroAppConstants.KEY_REQUEST_CAMERA_PERMISSION
            )
        }
    }


    private fun askForExternalStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                NiroAppConstants.KEY_REQUEST_WRITE_STORAGE_PERMISSION
            )
        }
    }




    private fun openCamera(requestId: Int, prefix: String) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (activity?.packageManager?.let { takePictureIntent.resolveActivity(it) } != null) {
                var photoFile: File? = null
                try {
                    photoFile = context?.let { NiroAppUtils.createImageFile(prefix, it) }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                mCapturedPath = photoFile?.absolutePath
                if (photoFile != null) {
                    val photoURI = context?.let { context ->
                        FileProvider.getUriForFile(context,
                            "com.niro.niroapp.fileprovider", photoFile)
                    }
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(
                        takePictureIntent,
                        requestId
                    )
                }
            }


    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            NiroAppConstants.KEY_REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        checkForStoragePermissionOrOpenCamera(
                           NiroAppConstants.KEY_REQUEST_CAPTURE_PHOTO, NiroAppConstants.PREFIX_ORDER_IMAGE)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            NiroAppConstants.KEY_REQUEST_WRITE_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkForCameraPermissionOrOpenCamera()
                }

            } else -> askForCameraPermission()
        }

    }

    private fun checkForCameraPermissionOrOpenCamera() {
        if (!PermissionUtils.isCameraPermissionGranted(context)) {
            askForCameraPermission()
            return
        }

        openCamera(NiroAppConstants.KEY_REQUEST_CAPTURE_PHOTO, NiroAppConstants.PREFIX_ORDER_IMAGE)

    }


    private fun checkForStoragePermissionOrOpenCamera(requestId: Int, prefix: String
    ) {
        if (!PermissionUtils.isStoragePermissionGranted(context)) {
            askForExternalStoragePermission()
            return
        }
        openCamera(requestId, prefix)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == NiroAppConstants.KEY_REQUEST_CAPTURE_PHOTO) {
            context?.let { FirebaseFileUploadService(it).uploadFile(mCapturedPath ?: "",mCurrentUserId ?: "",3,this,FileType.ORDER_IMAGE) }
              viewModel?.getOrderImages()?.value?.add( File(mCapturedPath).name)
            viewModel?.getOrderImagesAbsolutePath()?.value?.add(mCapturedPath ?: "")

            loadImage(mCapturedPath)
            return
        }


    }

    private fun loadImage(path : String?) {
        bindingCreateOrderFragment.ivOrderImage.visibility = if(!path.isNullOrEmpty()) View.VISIBLE else View.INVISIBLE
        context?.let {
            Glide.with(it).load(mCapturedPath)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.rice)
                .centerCrop()
                .into(bindingCreateOrderFragment.ivOrderImage)
        }
    }

    private fun openDatePicker() {
        NiroAppUtils.hideKeyBoard(bindingCreateOrderFragment.root,context)
        activity?.let { DatePickerDialog(it, this, viewModel?.getReceivingDate()?.value).show(it.supportFragmentManager,NiroAppConstants.TAG_DIALOG_DATE) }
    }

    override fun onDateChanged(date: String?) {
        viewModel?.getReceivingDate()?.value = date
        bindingCreateOrderFragment.etReceivingDate.setText(viewModel?.getReceivingDateDisplayValue()?.value ?: "")
    }

    private fun createOrder() {

        NiroAppUtils.hideKeyBoard(bindingCreateOrderFragment.root,context)

        if(viewModel?.validateAllFields() ?: 0 > 0 ) return

        viewModel?.createOrder(context)?.observe(viewLifecycleOwner, Observer {
            handleCreateOrderResponse(it)
        })
    }

    private fun handleCreateOrderResponse(apiResponse: APIResponse?) {

        when (apiResponse) {
            is APILoader -> mProgressDialog = context?.let {
                NiroAppUtils.showLoaderProgress(getString(R.string.creating_order), it)
            }

            is APIError -> {
                if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
                NiroAppUtils.showSnackbar(apiResponse.errorMessage, bindingCreateOrderFragment.root)
            }

            is Success<*> -> {
                if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
                NiroAppUtils.showSnackbar(
                    getString(R.string.order_created),
                    bindingCreateOrderFragment.root
                )

                showRatingsDialog(apiResponse.data as? String)
            }
        }

    }

    private fun showRatingsDialog(id: String?) {

        val builder = AlertDialog.Builder(activity)

        val customLayout = layoutInflater.inflate(R.layout.dialog_ratings,null)

        builder.setView(customLayout)
        builder.setCancelable(false)

        val ratingsBar = customLayout.findViewById<RatingBar>(R.id.ratingBar)
        val btnSubmitRatings = customLayout.findViewById<Button>(R.id.btnSubmitRatings)
        btnSubmitRatings?.setOnClickListener { submitRating(ratingsBar?.rating) }

        mDialog = builder.create()
        mDialog?.show()

    }

    private fun submitRating(rating: Float?) {
        viewModel?.updateRatings(rating,context)?.observe(viewLifecycleOwner, Observer {
            when(it) {
                is APILoader -> mProgressDialog = context?.let {
                    NiroAppUtils.showLoaderProgress(getString(R.string.submitting_ratings), it)
                }

                is APIError -> {
                    if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
                    NiroAppUtils.showSnackbar(it.errorMessage, bindingCreateOrderFragment.root)
                    mDialog?.dismiss()
                    goBackToOrdersScreen()
                }

                is Success<*> -> {
                    if(mProgressDialog != null && mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
                    NiroAppUtils.showSnackbar(
                        getString(R.string.order_created),
                        bindingCreateOrderFragment.root
                    )
                    viewModel?.resetAllFields()
                    mDialog?.dismiss()
                    goBackToOrdersScreen()
                }
            }
        })

    }


    private fun goBackToOrdersScreen() {
        findNavController().popBackStack(R.id.navigation_orders, false)
    }

    override fun onFileUploaded(fileType: FileType) {

    }

    override fun onFileUploadFailed(fileType: FileType) {

    }

    override fun onBackPressed() {
        findNavController().popBackStack(R.id.navigation_contacts_list,false)
    }


}