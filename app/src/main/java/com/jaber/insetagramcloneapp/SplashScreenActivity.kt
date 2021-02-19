package com.jaber.insetagramcloneapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jaber.insetagramcloneapp.databinding.ActivitySplashScreenBinding
import com.jaber.insetagramcloneapp.model.UserInfoModel

class SplashScreenActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN = 7171
    }

    private lateinit var binding: ActivitySplashScreenBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var providers: List<AuthUI.IdpConfig>
    private lateinit var listener: FirebaseAuth.AuthStateListener

    private lateinit var database: FirebaseDatabase
    private lateinit var userInfoRef: DatabaseReference

    override fun onStart() {
        super.onStart()
        delaySplashScreen()
    }

    private fun delaySplashScreen() {
        Handler(Looper.getMainLooper()).postDelayed({ auth.addAuthStateListener(listener) }, 3000)
    }

    override fun onStop() {
        if (auth != null && listener != null) auth.removeAuthStateListener(listener)
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database
        userInfoRef = database.getReference(Common.USER_INFO_REFERENCE)

        init()

    }

    private fun init() {
        providers = arrayListOf(
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build(),
        )

        listener = FirebaseAuth.AuthStateListener { myAuth ->
            val user = myAuth.currentUser
            if (user != null) {
                Log.i("AuthStateListener" , user.uid)

                checkUserFromFirebase()

            } else {
                showLoginLayout()
            }
        }
    }

    private fun checkUserFromFirebase() {
        userInfoRef.child(auth.currentUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
//                        Toast.makeText(this@SplashScreenActivity, "user already register!!", Toast.LENGTH_LONG).show()

                        val user = snapshot.getValue(UserInfoModel::class.java)
                        Log.i("driver" , user?.firstName.toString())
                        Toast.makeText(this@SplashScreenActivity, "welcome: ${user?.firstName}", Toast.LENGTH_LONG).show()
                        goToHomeActivity(user)

                        binding.progressBar.visibility = View.GONE
                    } else {
                        showRegisterLayout()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("DatabaseError", "loadPost:onCancelled", error.toException())
                    Toast.makeText(this@SplashScreenActivity, error.message, Toast.LENGTH_LONG).show()
                }

            })
    }

    private fun showLoginLayout() {
        val authMethodPickerLayout = AuthMethodPickerLayout.Builder(R.layout.layout_sign_in)
            .setPhoneButtonId(R.id.btnPhoneSignIn)
            .setGoogleButtonId(R.id.btnGoogleSignIn)
            .setEmailButtonId(R.id.btnEmailSignIn)
            .build()

        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setAuthMethodPickerLayout(authMethodPickerLayout)
                .setAvailableProviders(providers)
                .setTheme(com.jaber.insetagramcloneapp.R.style.loginTheme).setIsSmartLockEnabled(false).build(),
            RC_SIGN_IN
        )
    }

    private fun goToHomeActivity(user: UserInfoModel?) {
        Common.currentUser = user // to send user Info to Common Object <== Very Important
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    private fun showRegisterLayout() {
        val builder = AlertDialog.Builder(this, R.style.dialogTheme)
        val itemView = LayoutInflater.from(this).inflate(R.layout.layout_register, null)

        val etFirstName = itemView.findViewById<EditText>(com.jaber.insetagramcloneapp.R.id.etFirstName)
        val etLastName = itemView.findViewById<EditText>(com.jaber.insetagramcloneapp.R.id.etLastName)
        val etPhone = itemView.findViewById<EditText>(com.jaber.insetagramcloneapp.R.id.etPhone)

        val btnContinue = itemView.findViewById<Button>(R.id.btnRegister)

        //Set Data
        if (FirebaseAuth.getInstance().currentUser?.phoneNumber != null && !TextUtils.isDigitsOnly(
                FirebaseAuth.getInstance().currentUser?.phoneNumber)
        ) {
            etPhone.setText(FirebaseAuth.getInstance().currentUser?.phoneNumber)
        }

        //Set View
        builder.setView(itemView)
        val dialog = builder.create()
        dialog.show()

        //Event
        btnContinue.setOnClickListener {
            when {
                etFirstName.text.toString().isEmpty() -> {
                    Toast.makeText(this, "Please enter First Name", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                etLastName.text.toString().isEmpty() -> {
                    Toast.makeText(this, "Please enter Last Name", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                etPhone.text.toString().isEmpty() -> {
                    Toast.makeText(this, "Please enter Phone Number", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                else -> {
                    val user = UserInfoModel()

                    user.firstName = etFirstName.text.toString()
                    user.lastName = etLastName.text.toString()
                    user.phoneNumber = etPhone.text.toString()

                    //Upload to firebase database
                    userInfoRef.child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .setValue(user)
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                            dialog.dismiss()
                            binding.progressBar.visibility = View.GONE
                        }.addOnSuccessListener {
                            Toast.makeText(this, "Register Success", Toast.LENGTH_LONG).show()
                            dialog.dismiss()

                            goToHomeActivity(user)
                            binding.progressBar.visibility = View.GONE
                        }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = auth.currentUser
            } else {
                Toast.makeText(this, response?.error?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

}