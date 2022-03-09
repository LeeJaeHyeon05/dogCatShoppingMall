package com.example.dogcatshoppingmall

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.dogcatshoppingmall.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlin.Exception

class LoginActivity : AppCompatActivity() {

    companion object {
        const val TAG = "LoginActivity"
    }

    private lateinit var binding: ActivityLoginBinding

    private val gso: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val gsc by lazy { GoogleSignIn.getClient(this, gso) }

    private val loginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    task.getResult(ApiException::class.java)?.let {
                        Log.d(TAG, "accountId : ${it.id}")
                        firebaseAuthWithGoogle(it.idToken!!)
                    } ?: throw Exception()
                } catch (e: ApiException) {
                    Log.e(TAG, "GoogleLogin failed")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initGoogleLoginButton()
        initLoginButton()
    }

    private fun initLoginButton() = with(binding) {
        loginButton.setOnClickListener {
            val email = outlinedEmailTextField.editText?.text.toString()
            val password = outlinedPasswordTextField.editText?.text.toString()

            if (!emailCheck(email)) return@setOnClickListener
            if (!passwordCheck(password)) return@setOnClickListener

            Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
        }
    }

    private fun emailCheck(email: String): Boolean {
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS

        return when {
            email.isEmpty() -> {
                binding.outlinedEmailTextField.error = "이메일을 입력해 주세요."
                false
            }
            !emailPattern.matcher(email).matches() -> {
                binding.outlinedEmailTextField.error = "이메일 형식이 아닙니다."
                false
            }
            else -> {
                if (emailPattern.matcher(email).matches()) {
                    binding.outlinedEmailTextField.apply {
                        helperText = null
                        error = null
                        isErrorEnabled = false
                    }
                }

                binding.outlinedEmailTextField.apply {
                    helperText = null
                    error = null
                    isErrorEnabled = false
                }
                true
            }
        }
    }

    private fun passwordCheck(password: String): Boolean {
        return when {
            password.isEmpty() -> {
                binding.outlinedPasswordTextField.error = "비밀번호를 입력해 주세요."
                false
            }
            password.length < 6 -> {
                binding.outlinedPasswordTextField.error = "비밀번호가 일치하지 않습니다."
                false
            }
            else -> {
                binding.outlinedPasswordTextField.apply {
                    helperText = null
                    error = null
                    isErrorEnabled = false
                }
                true
            }
        }
    }

    private fun initGoogleLoginButton() = with(binding) {
        googleLoginButton.setOnClickListener {
            signInGoogle()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                } else {
                    Log.d(TAG, "signInWithCredential:failure")
                }
            }
    }

    private fun signInGoogle() {
        val signInIntent = gsc.signInIntent
        loginLauncher.launch(signInIntent)
    }
}