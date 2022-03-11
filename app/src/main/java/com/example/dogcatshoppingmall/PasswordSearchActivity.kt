package com.example.dogcatshoppingmall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.dogcatshoppingmall.databinding.ActivityPasswordSearchBinding
import com.google.firebase.auth.FirebaseAuth

class PasswordSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPasswordSearchBinding

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPasswordResetButton()
    }

    private fun initPasswordResetButton() = with(binding) {
        passwordResetButton.setOnClickListener {
            val email = passwordSearchWithEmailEditText.text.toString()

            if (!emailCheck(email)) return@setOnClickListener

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@PasswordSearchActivity,
                            "가입하신 이메일로 비밀번호 재설정 이메일을 보냈습니다.",
                            Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@PasswordSearchActivity,
                            "가입하신 이메일이 아닙니다.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun emailCheck(email: String): Boolean {
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS

        return when {
            email.isEmpty() -> {
                binding.passwordSearchWithEmailTextField.error = "이메일을 입력해 주세요."
                false
            }
            !emailPattern.matcher(email).matches() -> {
                binding.passwordSearchWithEmailTextField.error = "이메일 형식이 아닙니다."
                false
            }
            else -> {
                if (emailPattern.matcher(email).matches()) {
                    binding.passwordSearchWithEmailTextField.apply {
                        helperText = null
                        error = null
                        isErrorEnabled = false
                    }
                }

                binding.passwordSearchWithEmailTextField.apply {
                    helperText = null
                    error = null
                    isErrorEnabled = false
                }
                true
            }
        }
    }
}