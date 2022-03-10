package com.example.dogcatshoppingmall

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.dogcatshoppingmall.databinding.ActivitySignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignUpActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SignActivity"
    }

    private lateinit var binding: ActivitySignUpBinding

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
                        Log.d(LoginActivity.TAG, "accountId : ${it.id}")
                        firebaseAuthWithGoogle(it.idToken!!)
                    } ?: throw Exception()
                } catch (e: ApiException) {
                    Log.e(LoginActivity.TAG, "GoogleLogin failed")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initGoogleLoginButton()
        initSignInButton()
    }

    private fun initSignInButton() = with(binding) {
        signUpButton.setOnClickListener { view ->
            val name = signUpNameEditText.text.toString()
            val email = signUpEmailEditText.text.toString()
            val password = signUpPasswordEditText.text.toString()
            val passwordCheck = signUpPasswordCheckEditText.text.toString()

            if (!nameCheck(name)) return@setOnClickListener
            if (!emailCheck(email)) return@setOnClickListener
            if (!passwordCheck(password, passwordCheck)) return@setOnClickListener

            createUserWithEmailAndPassword(email, passwordCheck, view)
        }
    }

    @SuppressLint("ShowToast")
    private fun createUserWithEmailAndPassword(email: String, password: String, view: View) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(this, "회원가입 성공하셨습니다.\n로그인 해주세요.", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Log.d(TAG, "createUserWithEmail:failure")
                    Toast.makeText(this@SignUpActivity, "회원가입 실패하셨습니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun nameCheck(name: String): Boolean {
        return when {
            name.isEmpty() -> {
                binding.outlinedNameTextField.error = "이름을 입력해 주세요."
                false
            }
            else -> {
                binding.outlinedNameTextField.apply {
                    error = null
                    helperText = null
                    isErrorEnabled = false
                }
                true
            }
        }
    }

    private fun emailCheck(email: String): Boolean {
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS

        return when {
            email.isEmpty() -> {
                binding.outlinedEmailTextField.error = "이메일을 입력해주세요."
                false
            }
            !emailPattern.matcher(email).matches() -> {
                binding.outlinedEmailTextField.error = "이메일 형식이 맞지 않습니다."
                false
            }
            else -> {
                if (emailPattern.matcher(email).matches()) {
                    binding.outlinedEmailTextField.apply {
                        error = null
                        helperText = null
                        isErrorEnabled = false
                    }
                }

                binding.outlinedEmailTextField.apply {
                    error = null
                    helperText = null
                    isErrorEnabled = false
                }
                true
            }
        }
    }

    private fun passwordCheck(password: String, passwordCheck: String): Boolean {
        return when {
            password.isEmpty() -> {
                binding.outlinedPasswordTextField.error = "비밀번호를 입력해주세요."
                false
            }
            password.length < 6 -> {
                binding.outlinedPasswordTextField.error = "비밀번호는 6자리 이상 입력해주세요."
                false
            }
            password != passwordCheck -> {
                binding.outlinedPasswordTextField.error = "비밀번호가 일치하지 않습니다."
                binding.outlinedPasswordCheckTextField.error = "비밀번호가 일치하지 않습니다."
                false
            }
            else -> {
                binding.outlinedPasswordTextField.apply {
                    error = null
                    helperText = null
                    isErrorEnabled = false
                }
                binding.outlinedPasswordCheckTextField.apply {
                    error = null
                    helperText = null
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
                    Log.d(LoginActivity.TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.d(LoginActivity.TAG, "signInWithCredential:failure")
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signInGoogle() {
        val signInIntent = gsc.signInIntent
        loginLauncher.launch(signInIntent)
    }
}