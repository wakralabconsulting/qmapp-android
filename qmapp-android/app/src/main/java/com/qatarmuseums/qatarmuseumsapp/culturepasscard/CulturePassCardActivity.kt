package com.qatarmuseums.qatarmuseumsapp.culturepasscard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.qatarmuseums.qatarmuseumsapp.LocaleManager
import com.qatarmuseums.qatarmuseumsapp.R
import kotlinx.android.synthetic.main.activity_culture_pass_card.*
import timber.log.Timber


class CulturePassCardActivity : AppCompatActivity() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.setLocale(base))
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_culture_pass_card)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        membershipTxtView.text = getString(R.string.membership_number) + " " + membershipNumber
        membershipTxt.text = membershipNumber
        nameTxt.text = userName

        flipButtonSecond.setOnClickListener {
            Timber.i("Second Flip Button clicked")
            flipView.flipTheView()
            val contentBundleParams = Bundle()
            contentBundleParams.putString(FirebaseAnalytics.Param.CONTENT_TYPE, flipButtonSecond.text.toString())
            contentBundleParams.putString(FirebaseAnalytics.Param.ITEM_ID, flipButtonSecond.text.toString())
            FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, contentBundleParams)
        }

        flipButtonFirst.setOnClickListener {
            Timber.i("First Flip Button clicked")
            flipView.flipTheView()
            val contentBundleParams = Bundle()
            contentBundleParams.putString(FirebaseAnalytics.Param.CONTENT_TYPE, flipButtonFirst.text.toString())
            contentBundleParams.putString(FirebaseAnalytics.Param.ITEM_ID, flipButtonFirst.text.toString())
            FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, contentBundleParams)
        }

        closeBtnFirst.setOnClickListener {
            Timber.i("First Close Button clicked")
            finish()
        }

        closeBtnSecond.setOnClickListener {
            Timber.i("Second Close Button clicked")
            finish()
        }

        prepareBarcode()

    }

    private fun prepareBarcode() {
        val multiFormatWriter = MultiFormatWriter()
        if (membershipNumber != "") {
            try {
                Timber.i("Preparing Barcode")
                val bitMatrix = multiFormatWriter.encode(membershipNumber, BarcodeFormat.CODABAR, 500, 250)
                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.createBitmap(bitMatrix)
                barcodeImg.setImageBitmap(bitmap)
            } catch (e: WriterException) {
                Timber.e("Preparing Barcode error: %s", e.message)
                e.printStackTrace()
            }
        }
    }

    companion object {
        var membershipNumber: String = ""
        var userName: String = ""
        var language: String = "en"
        fun newIntent(context: Context, memNumber: String, userName: String, language: String) {
            membershipNumber = memNumber
            this.userName = userName
            this.language = language
            val intent = Intent(context, CulturePassCardActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        FirebaseAnalytics.getInstance(this).setCurrentScreen(this,
                getString(R.string.culture_pass_card_page), null)

    }
}
