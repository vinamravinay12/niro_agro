package com.niro.niroapp.adapters.viewholders

import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.niro.niroapp.R
import com.niro.niroapp.databinding.CardBuyCommodityBinding
import com.niro.niroapp.firebase.FileDownloadResponseHandler
import com.niro.niroapp.firebase.FileType
import com.niro.niroapp.firebase.FirebaseFileUploadService
import com.niro.niroapp.models.responsemodels.BuyCommodity

class BuyCommoditiesListViewHolder(private val viewBinding : CardBuyCommodityBinding, private val variables : HashMap<Int,Any?>)
    : GenericViewHolder<BuyCommodity>(viewBinding), FileDownloadResponseHandler{

    override fun bind(item: BuyCommodity, position: Int) {

        viewBinding.position = position

        setVariables(variablesMap = variables)
        if(item.images?.size == 1 && !item.images[0].isNullOrEmpty())  {
            FirebaseFileUploadService(viewBinding.root.context).downloadFile(item.images[0] ,item.userDetails?.id ?: "",3,this, FileType.SELL_COMMODITY_IMAGE)
        }


        viewBinding.tvCallNow.setOnClickListener { viewBinding.callUserListener?.callUser(item.userDetails?.phoneNumber ?: "")}

    }

    override fun onFileDownloaded(fileUri: Uri, fileType: FileType) {
        Glide.with(viewBinding.ivCommodityImage).load(fileUri).thumbnail(0.2f)
            .diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().placeholder(
                R.drawable.rice
            ).into(viewBinding.ivCommodityImage)
    }

    override fun onFileDownloadFailed(fileType: FileType) {

    }
}