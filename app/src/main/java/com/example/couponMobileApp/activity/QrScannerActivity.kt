package com.example.couponMobileApp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.*
import com.example.couponMobileApp.R
import java.io.ByteArrayOutputStream


class QrScannerActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    private var qrImage : Bitmap? = null

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scanner)
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        var sid = intent.getIntExtra("s_id", 0)

        codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not
        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
              //  Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()

                val builder = AlertDialog.Builder(this)
                //set title for alert dialog
                builder.setTitle("Qr Code")
                //set message for alert dialog
                builder.setMessage("Genrating QR Code")
                builder.setIcon(android.R.drawable.ic_dialog_alert)

                //performing positive action
                builder.setPositiveButton("Ok") { dialogInterface, which ->
                 //   Toast.makeText(applicationContext, "clicked yes", Toast.LENGTH_SHORT).show()
                    qrImage =
                        net.glxn.qrgen.android.QRCode.from("abcde").bitmap()
                    val bStream = ByteArrayOutputStream()
                    qrImage!!.compress(Bitmap.CompressFormat.PNG, 100, bStream);
                    val byteArray: ByteArray = bStream.toByteArray()
                    val i = (Intent(applicationContext, AddCardActivity::class.java))
                    i.putExtra("imagebitmap", byteArray)
                    i.putExtra("storeId", sid)
                    startActivity(i)
                    finish()
                }
                // Create the AlertDialog
                val alertDialog: AlertDialog = builder.create()
                // Set other dialog properties
                alertDialog.setCancelable(false)
                alertDialog.show()
            }
//                Toast.makeText(this, "Scan result: ${it.resultMetadata.values}", Toast.LENGTH_LONG).show()
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(
                    this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}