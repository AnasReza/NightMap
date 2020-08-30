package com.nightmap.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.nightmap.R
import com.nightmap.ui.activity.user.UserHomeActivity
import com.nightmap.utility.Preferences
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity(), View.OnClickListener {
    private var sign_in: Button? = null
    private var resend_code: Button? = null
    private var resend_text: TextView? = null
    private var edit1: EditText? = null
    private var edit2: EditText? = null
    private var edit3: EditText? = null
    private var edit4: EditText? = null
    private var edit5: EditText? = null
    private var edit6: EditText? = null
    private var str: String = ""
    private var phone: String = ""
    private var cdt: CountDownTimer? = null
    private var pref: Preferences? = null
    private var db: FirebaseFirestore? = null
    private var auth: FirebaseAuth? = null
    private var verification: String = ""
    private var smsCode: String = ""
    private var newCredentials: PhoneAuthCredential? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        init()
    }

    private var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credentials: PhoneAuthCredential) {
            Log.i("Anas", "VERIFICATION IS COMPLETE")
            smsCode = credentials.smsCode!!
            newCredentials = credentials
            Log.d("Anas","$smsCode   sms code")
           // signInWithPhoneAuthCredentials(credentials)

        }

        override fun onVerificationFailed(exception: FirebaseException) {
            Log.e("Anas", "VERIFICATION IS FAILED")
            exception.printStackTrace()
        }

        override fun onCodeSent(
            verficationCode: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            super.onCodeSent(verficationCode, token)
            Log.i("Anas", "$verficationCode  this verification code")
            verification = verficationCode

        }

        override fun onCodeAutoRetrievalTimeOut(p0: String) {
            super.onCodeAutoRetrievalTimeOut(p0)

        }
    }
    private fun signInWithPhoneAuthCredentials(credentials: PhoneAuthCredential) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credentials).addOnCompleteListener(this) { task ->

            if (task.isSuccessful) {
                val user = task.result?.user

                registerUserInFirebase(user!!.uid)

            }
        }
    }

    private fun init() {
        sign_in = findViewById(R.id.sign_in)
        resend_code = findViewById(R.id.resend)
        resend_text = findViewById(R.id.resend_text)
        edit1 = findViewById(R.id.edit1)
        edit2 = findViewById(R.id.edit2)
        edit3 = findViewById(R.id.edit3)
        edit4 = findViewById(R.id.edit4)
        edit5 = findViewById(R.id.edit5)
        edit6 = findViewById(R.id.edit6)
        pref = Preferences(this)
        db = Firebase.firestore

        cdt = object : CountDownTimer(60000, 1000) {
            override fun onFinish() {
                resend_code!!.visibility = View.VISIBLE
                resend_text!!.visibility = View.GONE
            }

            override fun onTick(milliSecond: Long) {
                var seconds = (milliSecond / 1000) % 60
                val formater = SimpleDateFormat("ss")
                var text = "Resend Code in <b>00:$seconds</b>"
                resend_text!!.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
        }
        cdt!!.start()

        str = intent.getStringExtra("category")
        phone = intent.getStringExtra("phone")

        PhoneAuthProvider.getInstance()
            .verifyPhoneNumber(phone, 60, TimeUnit.SECONDS, this, callbacks)

        sign_in!!.setOnClickListener(this)
        resend_code!!.setOnClickListener(this)
    }


    private fun registerUserInFirebase(uid:String) {
        val user: String = intent.getStringExtra("name")
        val phone: String = intent.getStringExtra("phone")
        val latlng: LatLng = intent.getParcelableExtra("latlng")
        var geoPoint: GeoPoint = GeoPoint(latlng.latitude, latlng.longitude)
        var id: String

        val userHash =
            hashMapOf(
                "about" to "",
                "addedOn" to FieldValue.serverTimestamp(),
                "currentLocation" to geoPoint,
                "friendCount" to 0,
                "fullName" to user,
                "phoneNumber" to phone,
                "profileImage" to "",
                "uid" to uid,
                "showLocation" to false
            )
        db!!.collection("User").document(uid).set(userHash)
            .addOnSuccessListener { documentReference ->

            val newHash = hashMapOf("uid" to uid, "userType" to "User")
            db!!.collection("Users").document(uid).set(newHash)
                .addOnSuccessListener { _ ->
                    FirebaseInstanceId.getInstance().instanceId
                        .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            task.exception!!.printStackTrace()
                            return@addOnCompleteListener
                        }

                        val token = task.result?.token
                        val tokenMap = hashMapOf("token" to token)
                        db!!.collection("User").document(uid)
                            .update(tokenMap as Map<String, Any>).addOnSuccessListener {
                                pref!!.setFirstCheck(true)
                                pref!!.setUserID(uid)
                                pref!!.setLogin(true)
                                pref!!.setUserType("user")
                                pref!!.setUserName(user)
                                cdt!!.cancel()
                                startActivity(
                                    Intent(
                                        this,
                                        UserHomeActivity::class.java
                                    )
                                )
                                finish()
                            }
                    }

                }

        }
            .addOnFailureListener { e ->
                Log.w("Anas", "Error adding document", e)
            }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.sign_in -> {
                if (smsCode != "" && newCredentials != null) {
                    val string1 = edit1!!.text.toString()
                    val string2 = edit2!!.text.toString()
                    val string3 = edit3!!.text.toString()
                    val string4 = edit4!!.text.toString()
                    val string5 = edit5!!.text.toString()
                    val string6 = edit6!!.text.toString()
                    if (string1 != "" && string2 != "" && string3 != "" && string4 != "" && string5 != "" && string6 != "") {
                        val smsCodeString = "$string1$string2$string3$string4$string5$string6"
                      //  val credentials=PhoneAuthProvider.getCredential(verification,testVerificationCode)
                        if (smsCodeString == smsCode) {
                            signInWithPhoneAuthCredentials(newCredentials!!)
                        }else{
                            Log.e("Anas","OTP not equal")
                        }
                        Log.e("Anas", "$smsCode code sent by firebase")
                    }

                }
            }
            R.id.resend -> {
                PhoneAuthProvider.getInstance()
                    .verifyPhoneNumber(phone, 60, TimeUnit.SECONDS, this, callbacks)
                cdt!!.start()
                resend_code!!.visibility = View.GONE
                resend_text!!.visibility = View.VISIBLE
            }
        }
    }

}