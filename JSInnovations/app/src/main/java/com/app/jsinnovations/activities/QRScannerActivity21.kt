package com.app.jsinnovations.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import com.app.jsinnovations.R
import com.app.jsinnovations.utils.qrscannerutils.AutoFitPreviewBuilder
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import kotlinx.android.synthetic.main.activity_qrscanner21.*
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class QRScannerActivity21 : AppCompatActivity() {

    private val executor = Executors.newSingleThreadExecutor()
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null

    companion object{
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscanner21)
        viewFinder.post {
            startCamera()
        }
        capture.setOnClickListener {
            imageCapture?.takePicture(executor, object : ImageCapture.OnImageCapturedListener() {

                private fun degreesToFirebaseRotation(degrees: Int): Int = when (degrees) {
                    0 -> FirebaseVisionImageMetadata.ROTATION_0
                    90 -> FirebaseVisionImageMetadata.ROTATION_90
                    180 -> FirebaseVisionImageMetadata.ROTATION_180
                    270 -> FirebaseVisionImageMetadata.ROTATION_270
                    else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
                }

                override fun onCaptureSuccess(imageProxy: ImageProxy, rotationDegrees: Int) {
                    val mediaImage = imageProxy.image
                    val imageRotation = degreesToFirebaseRotation(rotationDegrees)
                    if (mediaImage != null) {
                        val image = FirebaseVisionImage.fromMediaImage(mediaImage, imageRotation)
                        val detector = FirebaseVision.getInstance().visionBarcodeDetector
                        detector.detectInImage(image)
                            .addOnCompleteListener {
                                val barcodes = it.result
                                if(barcodes != null && barcodes.isNotEmpty()){
                                    val url = barcodes[0].url
                                    println()
                                }

                            }
                    }

                }

                /** Callback for when an error occurred during image capture.  */
                override fun onError(
                    imageCaptureError: ImageCapture.ImageCaptureError, message: String,
                    cause: Throwable?
                ) {
                }

            })
        }
    }

    private fun startCamera() {
        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        val viewFinderConfig = PreviewConfig.Builder().apply {
            setLensFacing(CameraX.LensFacing.BACK)
            setTargetAspectRatio(screenAspectRatio)
            setTargetRotation(viewFinder.display.rotation)
        }.build()
        preview = AutoFitPreviewBuilder.build(viewFinderConfig, viewFinder)
        val imageCaptureConfig = ImageCaptureConfig.Builder().apply {
            setLensFacing(CameraX.LensFacing.BACK)
            setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            setTargetAspectRatio(screenAspectRatio)
            setTargetRotation(viewFinder.display.rotation)
        }.build()
        imageCapture = ImageCapture(imageCaptureConfig)
        CameraX.bindToLifecycle(this, preview, imageCapture)
    }

    private fun aspectRatio(width: Int, height: Int): AspectRatio {
        val previewRatio = max(width, height).toDouble() / min(width, height)

        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }
}
