package com.niro.niroapp.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.niro.niroapp.R
import com.niro.niroapp.databinding.OrderDetailsFragmentBinding
import com.niro.niroapp.firebase.FileDownloadResponseHandler
import com.niro.niroapp.firebase.FileType
import com.niro.niroapp.firebase.FirebaseFileUploadService
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.models.responsemodels.UserOrder
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.utils.NavigationBackPressedCallback
import com.niro.niroapp.utils.OnBackPressedListener
import com.niro.niroapp.viewmodels.OrderDetailsViewModel

class OrderDetailsFragment : AbstractBaseFragment(),FileDownloadResponseHandler {

    private lateinit var bindingOrderDetailsFragment: OrderDetailsFragmentBinding
    private  var viewModel: OrderDetailsViewModel? = null
    private var mSelectedOrder : UserOrder? = null
    private var mCurrentUser : User? = null

    companion object {
        fun newInstance() = OrderDetailsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            mSelectedOrder = getParcelable(NiroAppConstants.ARG_SELECTED_ORDER) as? UserOrder
            mCurrentUser = getParcelable(NiroAppConstants.ARG_CURRENT_USER) as? User
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingOrderDetailsFragment = DataBindingUtil.inflate(inflater,R.layout.order_details_fragment, container, false)
        bindingOrderDetailsFragment.lifecycleOwner = viewLifecycleOwner
        return bindingOrderDetailsFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,NavigationBackPressedCallback(this))

        viewModel = activity?.let { ViewModelProvider(it).get(OrderDetailsViewModel::class.java) }
        viewModel?.getAmountPrefix()?.value = getString(R.string.rupee_symbol)

        bindingOrderDetailsFragment.detailVM = viewModel
        bindingOrderDetailsFragment.cardOrderDetail.ordersVM = viewModel


        initializeData()
        loadOrderImage()
        loadCommodityImage()
    }

    private fun loadCommodityImage() {
        Glide.with(requireActivity()).load(viewModel?.getCommodityImage()?.value).centerCrop().thumbnail(0.2f).placeholder(R.drawable.rice).into(bindingOrderDetailsFragment.cardOrderDetail.ivCommodityImage)
    }

    private fun initializeData() {
        viewModel?.getCurrentUserData()?.value = mCurrentUser
        viewModel?.getSelectedOrder()?.value = mSelectedOrder
        mCurrentUser = null
        mSelectedOrder = null

        if((viewModel?.getAmountDifference() ?: 0.0) < 0.0) bindingOrderDetailsFragment.tvAmountPendingLabel.text = getString(R.string.text_amount_receive)
        else bindingOrderDetailsFragment.tvAmountPendingLabel.text = getString(R.string.text_amount_pending)

        super.setPageTitle(String.format(getString(R.string.title_order_details), viewModel?.getOrderNumber()?.value),-1)

        super.registerBackPressedCallback(R.id.navigation_orders)




    }

    private fun loadOrderImage() {

        context?.let { FirebaseFileUploadService(it).downloadFile(viewModel?.getOrderImageName() ?: "",viewModel?.getCurrentUserData()?.value?.id ?: "",3,this, FileType.ORDER_IMAGE) }
    }

    override fun onFileDownloaded(fileUri: Uri, fileType: FileType) {
        context?.let { Glide.with(it).load(fileUri).centerCrop().thumbnail(0.2f).placeholder(R.drawable.rice).into(bindingOrderDetailsFragment.ivOrderImage) }
    }

    override fun onFileDownloadFailed(fileType: FileType) {

    }




}