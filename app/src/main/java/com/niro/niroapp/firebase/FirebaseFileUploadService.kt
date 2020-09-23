package com.niro.niroapp.firebase

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.google.firebase.storage.FirebaseStorage
import com.niro.niroapp.BuildConfig
import com.niro.niroapp.network.NiroAPI
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


enum class FileType {
    ORDER_IMAGE,PAYMENTS_IMAGE,SELL_COMMODITY_IMAGE
}

interface FileUploadResponseHandler {
    fun onFileUploaded(fileType : FileType)
    fun onFileUploadFailed(fileType: FileType)

}

interface FileDownloadResponseHandler {
    fun onFileDownloaded(fileUri : Uri, fileType: FileType)
    fun onFileDownloadFailed(fileType: FileType)
}


class FirebaseFileUploadService(private val context : Context) {


    private var firebaseStorage: FirebaseStorage =
        FirebaseStorage.getInstance(BuildConfig.FILE_UPLOAD_URL)


    fun uploadFile(
        filePath: String,
        userId: String,
        retryCount: Int,
        fileUploadResponseHandler: FileUploadResponseHandler,
        fileType: FileType
    ) {
        val storageReference = firebaseStorage.reference
        val uploadFile = File(filePath);
        val fileReference = storageReference.child("user_docs/$userId/${uploadFile.name}")

        var fileUri = Uri.fromFile(uploadFile)

        var imageBitmap: Bitmap? = null
        try {
            imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, fileUri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val outputByteArrayStream = ByteArrayOutputStream()

        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 15, outputByteArrayStream)
        val fileInBytes: ByteArray = outputByteArrayStream.toByteArray()
        val uploadTask = fileReference.putBytes(fileInBytes)

        uploadTask.addOnFailureListener {
            if (retryCount > 0) uploadFile(
                filePath,
                userId,
                retryCount - 1,
                fileUploadResponseHandler,
                fileType
            ) else fileUploadResponseHandler.onFileUploadFailed(fileType)
        }.addOnSuccessListener {
            fileUploadResponseHandler.onFileUploaded(fileType)
        }

    }


    fun downloadFile(
        fileName: String,
        userId: String,
        retryCount: Int,
        fileDownloadResponseHandler: FileDownloadResponseHandler,
        fileType: FileType
    ) {
        val storageReference = firebaseStorage.reference
        val fileReference = storageReference.child("user_docs/$userId/$fileName")
        fileReference.downloadUrl.addOnCanceledListener {
            if (retryCount > 0) downloadFile(
                fileName,
                userId,
                retryCount - 1,
                fileDownloadResponseHandler,
                fileType
            ) else fileDownloadResponseHandler.onFileDownloadFailed(fileType = fileType)
        }.addOnSuccessListener { uri ->
            fileDownloadResponseHandler.onFileDownloaded(
                fileUri = uri,
                fileType = fileType
            )
        }
    }
}