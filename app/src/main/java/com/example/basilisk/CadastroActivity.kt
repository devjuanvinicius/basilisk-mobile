package com.example.basilisk

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import com.example.dashboardbasi.databinding.ActivityCadastroBinding // Ajuste o pacote conforme necessário
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : FragmentActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inicia binding
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuração de ajustes de layout com insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.buttoncadastro.setOnClickListener {
            val email = binding.emailCad.text.toString()
            val senha = binding.senhaCad.text.toString()
            val nome = binding.NomeCompletoCad.text.toString()
            val dataNascimento = binding.nascimentoCad.text.toString()
            val telefone = binding.TelefoneCad.text.toString()

            if (email.isEmpty() || senha.isEmpty() || nome.isEmpty() || dataNascimento.isEmpty() || telefone.isEmpty()) {
                // mostra uma msm de erro
                val snackbar =
                    Snackbar.make(binding.root, "Preencha todos os campos!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            } else {
                auth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener { cadastro ->
                        if(cadastro.isSuccessful) {

                            val userId = auth.currentUser?.uid

                            val userData = hashMapOf(
                                "nome" to nome,
                                "dataNascimento" to dataNascimento,
                                "telefone" to telefone,
                                "email" to email
                            )
                            if(userId !=null) {
                                db.collection("users").document(userId)
                                    .set(userData)
                                    .addOnSuccessListener {
                                        Snackbar.make(
                                            binding.root,
                                            "Usuário cadastrado com sucesso!",
                                            Snackbar.LENGTH_SHORT
                                        ).setBackgroundTint(Color.GREEN).show()

                                        //vai tirar os nomes dos campos
                                        binding.NomeCompletoCad.setText("")
                                        binding.nascimentoCad.setText("")
                                        binding.TelefoneCad.setText("")
                                        binding.emailCad.setText("")
                                        binding.senhaCad.setText("")
                                    }
                            }
                        }
                    }.addOnFailureListener{ expition ->
                        val mensgenErro = when(expition){
                            is FirebaseAuthWeakPasswordException -> "Digite uma senha com no minimo 6 caracteres"
                            is FirebaseAuthInvalidCredentialsException -> "digite um e-mail valido"
                            is FirebaseAuthUserCollisionException -> "esta conta já foi cadastrada"
                            is FirebaseNetworkException -> "Sem conexão com internet "
                            else -> "erro ao cadastrar"
                        }

                    }

            }
        }
        binding.loginLink.setOnClickListener{
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}

