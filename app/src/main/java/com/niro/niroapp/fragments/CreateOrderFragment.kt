package com.niro.niroapp.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import carbon.dialog.ProgressDialog
import com.niro.niroapp.R
import com.niro.niroapp.databinding.CreateOrderFragmentBinding
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.UserContact
import com.niro.niroapp.users.fragments.setReadOnly
import com.niro.niroapp.utils.DateChangeListener
import com.niro.niroapp.utils.NIroAppConstants
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.utils.PermissionUtils
import com.niro.niroapp.viewmodels.CreateOrderViewModel
import com.niro.niroapp.viewmodels.factories.CreateOrderViewModelFactory
import java.io.File
import java.io.IOException

class CreateOrderFragment : Fragment(), DateChangeListener {

    private var viewModel: CreateOrderViewModel? = null
    private var mSelectedUserContact: UserContact? = null
    private lateinit var bindingCreateOrderFragment: CreateOrderFragmentBinding
    private var mProgressDialog: ProgressDialog? = null
    private var mCapturedPath : String? = null

    companion object {
        fun newInstance() = CreateOrderFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mSelectedUserContact = it.getParcelable(NIroAppConstants.ARG_USER_CONTACT)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingCreateOrderFragment =
            DataBindingUtil.inflate(inflater, R.layout.create_order_fragment, container, false)
        bindingCreateOrderFragment.lifecycleOwner = viewLifecycleOwner
        return bindingCreateOrderFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity?.let { CreateOrderViewModelFactory().getViewModel(null, it) }
        bindingCreateOrderFragment.createOrderVM = viewModel

        makeFieldsReadOnly()
        initializeFocusChangeListeners()
        initializeClickListeners()
    }

    private fun makeFieldsReadOnly() {
        bindingCreateOrderFragment.etEnterCommodity.setReadOnly(true)
        bindingCreateOrderFragment.etReceivingDate.setReadOnly(true)
        bindingCreateOrderFragment.etAddImage.setReadOnly(true)
    }


    private fun initializeClickListeners() {

        bindingCreateOrderFragment.etReceivingDate.setOnClickListener { openDatePicker() }

        bindingCreateOrderFragment.etEnterCommodity.setOnClickListener { launchSelectCommodityFragment() }

        bindingCreateOrderFragment.etAddImage.setOnClickListener { launchCamera() }
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

    private fun launchCamera() {
        val permissionArray = ArrayList<String>()
        if (!PermissionUtils.isCameraPermissionGranted(context)) {
            permissionArray.add(NIroAppConstants.CODE_CAMERA_PERMISSION)
        } else if (!PermissionUtils.isStoragePermissionGranted(context)) {
            permissionArray.add(NIroAppConstants.CODE_STORAGE_PERMISSION)
        }

        if(permissionArray.size > 0) {
            PermissionUtils.askForPermissions(activity, permissionArray.toArray() as Array<String?>?)
            return
        }

        openCamera(NIroAppConstants.KEY_REQUEST_CAPTURE_PHOTO, NIroAppConstants.PREFIX_ORDER_IMAGE)
    }


    private fun launchSelectCommodityFragment() {
        findNavController().navigate(
            R.id.navigation_commodities_fragment, bundleOf(
                NIroAppConstants.PREVIOUS_SCREEN_ID to R.id.navigation_create_order,
                NIroAppConstants.ARG_ALLOW_MULTISELECT to false
            )
        )
    }


    private fun openCamera(requestId: Int, prefix: String) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (activity?.packageManager?.let { takePictureIntent.resolveActivity(it) } != null) {
                //Create a file to store the image
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

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
               openCamera(NIroAppConstants.KEY_REQUEST_CAPTURE_PHOTO,NIroAppConstants.PREFIX_ORDER_IMAGE)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun openDatePicker() {
        context?.let { DatePickerDialog(it, this, viewModel?.getReceivingDate()?.value) }
    }

    override fun onDateChanged(date: String?) {
        viewModel?.getReceivingDate()?.value = date
    }

    private fun createOrder() {
        viewModel?.createOrder(context)?.observe(viewLifecycleOwner, Observer {
            handleCreateOrderResponse(it)
        })
    }

    private fun handleCreateOrderResponse(apiResponse: APIResponse?) {

        when (apiResponse) {
            is APILoader -> mProgressDialog = context?.let {
                NiroAppUtils.showLoaderProgress(getString(R.string.creating_contact), it)
            }

            is APIError -> {
                mProgressDialog?.dismiss()
                NiroAppUtils.showSnackbar(apiResponse.errorMessage, bindingCreateOrderFragment.root)
            }

            is Success<*> -> {
                mProgressDialog?.dismiss()
                NiroAppUtils.showSnackbar(
                    getString(R.string.order_created),
                    bindingCreateOrderFragment.root
                )
                viewModel?.resetAllFields()
                goBackToOrdersScreen()
            }
        }

    }

    private fun goBackToOrdersScreen() {
        findNavController().popBackStack(R.id.navigation_orders, false)
    }


}