package com.example.basilisk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import com.example.basilisk.database.UsuarioDAO
import com.example.basilisk.databinding.ActivityCadastroBinding
import com.example.basilisk.model.Usuario
import com.example.basilisk.utils.exibirMensagem
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : FragmentActivity() {
    public lateinit var binding: ActivityCadastroBinding

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttoncadastro.setOnClickListener {
            cadastrarUsuario()
        }

        binding.loginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    public fun cadastrarUsuario() {
        val email = binding.emailCad.text.toString()
        val senha = binding.senhaCad.text.toString()
        val nome = binding.NomeCompletoCad.text.toString()
        val telefone = binding.TelefoneCad.text.toString()
        val dataNascimento = binding.nascimentoCad.text.toString()

        if (!validarCampos(email, senha, nome, telefone, dataNascimento)) return

        val usuario = Usuario(null, nome, email, dataNascimento, telefone)

        UsuarioDAO(db, auth).criarUsuario(
            usuario,
            senha,
            onSuccess = {
                exibirMensagem("Bem-vindo, ${usuario.nome}! Cadastro realizado com sucesso.")
                limparCampos()
                startActivity(Intent(applicationContext, MainActivity::class.java))
            },
            onFailure = { exception ->
                val mensagemErro = getMensagemErro(exception)
                exibirMensagem(mensagemErro)
            }
        )
    }

    public fun getMensagemErro(exception: Exception): String {
        return when (exception) {
            is FirebaseAuthWeakPasswordException -> "Digite uma senha com no mínimo 6 caracteres"
            is FirebaseAuthInvalidCredentialsException -> "Digite um e-mail válido"
            is FirebaseAuthUserCollisionException -> "E-mail já cadastrado"
            is FirebaseNetworkException -> "Sem conexão com internet"
            else -> {
                Log.e("CadastroActivity", "Erro ao cadastrar: ${exception.message}", exception)
                "Erro ao cadastrar: ${exception.message}"
            }
        }
    }

    public fun validarCampos(email: String, senha: String, nome: String, telefone: String, dataNascimentoString: String): Boolean {
        if (email.isEmpty() || senha.isEmpty() || nome.isEmpty() || dataNascimentoString.isEmpty() || telefone.isEmpty()) {
            exibirMensagem("Preencha todos os campos!")
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            exibirMensagem("Digite um e-mail válido!")
            return false
        }

        return true
    }

    public fun limparCampos() {
        with(binding) {
            NomeCompletoCad.setText("")
            nascimentoCad.setText("")
            TelefoneCad.setText("")
            emailCad.setText("")
            senhaCad.setText("")
        }
    }
}
