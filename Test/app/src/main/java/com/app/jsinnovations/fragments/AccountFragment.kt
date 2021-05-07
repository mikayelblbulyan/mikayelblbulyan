package com.app.jsinnovations.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.jsinnovations.R
import com.app.jsinnovations.adapters.AccountAdapter
import com.app.jsinnovations.api.BackendApi
import com.app.jsinnovations.models.User
import com.app.jsinnovations.sharing.Constant
import com.app.jsinnovations.sharing.LoginHelper
import com.app.jsinnovations.ui.ActionSheet
import com.app.jsinnovations.ui.croperino.Croperino
import com.app.jsinnovations.utils.ItemOffsetDecorationVertical
import com.app.jsinnovations.utils.ProgressRequestBody
import com.app.jsinnovations.utils.Utils
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_account.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt


class AccountFragment : Fragment(), ActionSheet.ActionSheetListener,
    ProgressRequestBody.UploadCallbacks {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = activity?.findViewById<TextView>(R.id.title)
        title?.text = getString(R.string.account)
        Picasso.get()
            .load(Constant.BASE_URL + "/api/images")
            .into(profilePhoto, object : Callback {
                override fun onSuccess() {
                    println()
                }

                override fun onError(e: java.lang.Exception?) {
                    println()
                }

            })
        accountRecyclerView.layoutManager = LinearLayoutManager(context)
        accountRecyclerView.addItemDecoration(
            ItemOffsetDecorationVertical(
                Utils.convertDpToPixel(5).roundToInt()
            )
        )
        val accountAdapter = AccountAdapter(requireContext())
        if (Utils.isNetworkConnected(requireContext()))
            BackendApi.getInstance().getUser(LoginHelper.getAccessToken(requireContext())!!) {
                accountAdapter.user = it
                accountRecyclerView.adapter = accountAdapter
            }
        else {
            val realm = Realm.getDefaultInstance()
            val user = realm.where(User::class.java).findFirst()!!
            accountAdapter.user = user
            accountRecyclerView.adapter = accountAdapter
        }
        profilePhoto.setOnClickListener {
            openActionSheet()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_account, container, false)


    private fun openActionSheet() {
        val green = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
        val actionSheet = ActionSheet.Builder()
            //.setTitleTextSize(20)

            .setOtherBtn(
                arrayOf(getString(R.string.photo_library), getString(R.string.camera)),
                intArrayOf(green, green, green)
            )
            //.setOtherBtnSubTextSize(20)
            .setCancelBtn(
                getString(R.string.cancel),
                ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
            )
            //.setCancelBtnTextSize(30)
            .setCancelableOnTouchOutside(true)
            .setActionSheetListener(this).build()

        actionSheet.show(parentFragmentManager)
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )
            Croperino.prepareCamera(activity, this, requireContext(), "/profileImage.jpg")
        else
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ), 1
            )
    }

    private fun accessGallery() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val tempFile = File(activity?.externalCacheDir, "/profileImage.jpg")
            Croperino.prepareGallery(activity, tempFile)
        } else
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2)
    }


    override fun onButtonClicked(actionSheet: ActionSheet?, index: Int) {
        when (index) {
            0 -> {
                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), 1
                    )
                } else {
                    val tempFile = File(activity?.externalCacheDir, "/profileImage.png")
                    Croperino.prepareGallery(activity, tempFile)
                }
            }
            1 -> {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), 2
                    )
                } else
                    Croperino.prepareCamera(activity, this, requireContext(), "/profileImage.png")
            }
            2 -> {
                actionSheet!!.dismiss()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val tempImageFile = File(activity?.externalCacheDir, "/profileImage.png")
        when (requestCode) {
            0 -> {
                if (resultCode == -1)
                    Croperino.runCropImage(
                        tempImageFile, this, activity, true, 1, 1,
                        R.color.colorPrimaryDark,
                        R.color.colorPrimary
                    )
                else
                    tempImageFile.delete()
            }
            1 -> {
                if (data != null) {
                    try {
                        val uri = data.data
                        val inputStream = activity?.contentResolver?.openInputStream(uri!!)
                        val buffer = ByteArray(inputStream!!.available())
                        inputStream.read(buffer)
                        val outStream = FileOutputStream(tempImageFile)
                        outStream.write(buffer)
                        Croperino.runCropImage(
                            tempImageFile,
                            this,
                            activity,
                            true,
                            1,
                            1,
                            R.color.colorPrimaryDark,
                            R.color.colorPrimary
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
            3 -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    val uri = Uri.fromFile(tempImageFile)
                    try {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)
                        val baos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 1, baos)
//                        val b1 = baos.toByteArray()
//                        val encodedImage = Base64.encodeToString(b1, Base64.DEFAULT)
//                        val decodedString = Base64.decode(encodedImage, Base64.DEFAULT)
//                        val decodedByte =
//                            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
//                        val options = Utils.getImageSize(tempImageFile)
                        val fileBody = ProgressRequestBody(
                            tempImageFile,
                            "multipart/form-data",
                            this
                        )
                        val f = File(context?.cacheDir,"/profileImage1.png")
                        f.createNewFile()
                        val bitmapdata = baos.toByteArray();
                        val fos = FileOutputStream(f)
                        fos.write(bitmapdata)
                        fos.flush()
                        fos.close()
                        val filePath: String = f.path
                        val bitmap1 = BitmapFactory.decodeFile(filePath)
                        val file_size = java.lang.String.valueOf(f.length() / 1024).toInt()
                        BackendApi.getInstance().images(
                            f,
                            LoginHelper.getAccessToken(requireContext())!!,
                            fileBody
                        )
//                        val client =  OkHttpClient().newBuilder()
//                            .build()
//                        val mediaType = MediaType.parse("application/json")
//                        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
//                            .addFormDataPart("image","file", RequestBody.create(MediaType.parse("application/octet-stream"),
//                                tempImageFile))
//                            .build()
//                        val request = Request.Builder()
//                            .url("localhost:3000/api/images")
//                            .method("POST", body)
//                            .addHeader("Content-Type", "application/json")
//                            .addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVlMjJlOWZhNDgxYzBjNmY4YWE1YWY2MiIsInJvbGUiOiJwYXJ0bmVyIiwiaWF0IjoxNTc5MzU4NTkyLCJleHAiOjE2MTA5MTYxOTJ9.dIgC1ZinJxIBlwWqvo0xTClmzqttq5h6Ig0eLrLMItd1BbEVJ3iezjWihUCzSP3DA2Fok2XWC_LWes-ncHv1YLkg7f9r5keVCEcHDqi84CUpBkUkVG-gd2WYwPmOBmJfLMKIOq6__A47Aj0F2b1HIuUDhcLi2Im8WdE0LuFZ_MnsmL73_VKLA0coyoflMVZcnsEFY7E0W1dVFtR3AtXZGhZFAJzqmKIaQjh0AM0AMtoD_IiHdFKRH3eSgCU-gw_FFMobJdAOQxfUJoOnKj8uit4pZbVdQiFdayZcrwOllwCb1JtgtqcXNzOtsoT01GGkTRQlhZLcgLq0cTpOOBO0uw")
//                            .build()
//                        val response = client.newCall(request).execute()
//                        println()
//                        BackendApi.getInstance().createAttachment(
//                            LoginHelper.getAccessToken(context!!)!!,Utils.getMimeType(tempImageFile.toString()), options.outWidth, options.outHeight){
//                            BackendApi.getInstance().uploadImage(it?.contentType, it?.uploadLink, tempImageFile){
//                                contactImage.setImageBitmap(Utils.getCircularBitmap(decodedByte))
//                                if (tempImageFile.exists())
//                                    tempImageFile.delete()
//                                if(it != null)
//                                    attachmentModelId = it.uuId!!
//                            }
//                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            12 -> {

            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        /*when (requestCode) {
            1 -> accessGallery()
            2 -> openCamera()
        }*/
    }

    override fun onDismiss(actionSheet: ActionSheet?, isByBtn: Boolean) {

    }

    override fun onFinish() {

    }

    override fun onProgressUpdate(percentage: Int) {
        println("///////////$percentage")
    }

    override fun onError() {

    }
}