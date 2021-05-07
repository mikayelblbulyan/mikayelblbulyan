package com.app.jsinnovations.activities

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.jsinnovations.R
import com.app.jsinnovations.utils.qrscannerutils.BarcodeScanningProcessor
import com.app.jsinnovations.utils.qrscannerutils.CameraSource
import kotlinx.android.synthetic.main.activity_qrscanner.*
import java.io.IOException

class QRScannerActivity : AppCompatActivity(), BarcodeScanningProcessor.BarcodeInterface {


    private var cameraSource: CameraSource? = null

    private val requiredPermissions: Array<String?>
        get() {
            return try {
                val info = this.packageManager
                    .getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS)
                val ps = info.requestedPermissions
                if (ps != null && ps.isNotEmpty()) {
                    ps
                } else {
                    arrayOfNulls(0)
                }
            } catch (e: Exception) {
                arrayOfNulls(0)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_qrscanner)
        if (allPermissionsGranted()) {
            createCameraSource()
        } else {
            getRuntimePermissions()
        }
        startCameraSource()
        cameraSource?.setFacing(CameraSource.CAMERA_FACING_BACK)
    }

    override fun barcode(url: String) {
        barcodeTextView.text = url
    }

    private fun createCameraSource() {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = CameraSource(this, fireFaceOverlay)
        }
        val barcodeScanningProcessor = BarcodeScanningProcessor()
        barcodeScanningProcessor.barcodeInterface = this
        cameraSource?.setMachineLearningFrameProcessor(barcodeScanningProcessor)
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private fun startCameraSource() {
        cameraSource?.let {
            try {
                firePreview?.start(cameraSource, fireFaceOverlay)
            } catch (e: IOException) {
                cameraSource?.release()
                cameraSource = null
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    /** Stops the camera.  */
    override fun onPause() {
        super.onPause()
        firePreview?.stop()
    }

    public override fun onDestroy() {
        super.onDestroy()
        cameraSource?.release()
    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in requiredPermissions) {
            if (!isPermissionGranted(this, permission!!)) {
                return false
            }
        }
        return true
    }

    private fun getRuntimePermissions() {
        val allNeededPermissions = arrayListOf<String>()
        for (permission in requiredPermissions) {
            if (!isPermissionGranted(this, permission!!)) {
                allNeededPermissions.add(permission)
            }
        }

        if (allNeededPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this, allNeededPermissions.toTypedArray(), PERMISSION_REQUESTS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (allPermissionsGranted()) {
            createCameraSource()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val PERMISSION_REQUESTS = 1

        private fun isPermissionGranted(context: Context, permission: String): Boolean {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
            return false
        }
    }
}
