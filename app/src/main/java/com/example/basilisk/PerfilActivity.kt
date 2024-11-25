package com.example.basilisk

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.basilisk.database.UsuarioDAO
import com.example.basilisk.model.Usuario
import com.example.basilisk.utils.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PerfilActivity : AppCompatActivity() {
  private val auth by lazy { FirebaseAuth.getInstance() }
  private val db by lazy { FirebaseFirestore.getInstance() }

  lateinit var usuario: Usuario
  lateinit var btnExcluirConta: Button
  lateinit var btnSalvarEdicoes: Button
  lateinit var buttonTesteDeLogof: Button

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContentView(R.layout.activity_perfil)
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
      insets
    }

    // Verificação do usuário atual e carregamento de dados
    if (!auth.currentUser?.uid.isNullOrEmpty()) {
      UsuarioDAO(db, auth).retornarUsuario(
        auth.currentUser!!.uid,
        onSuccess = { infoUser ->
          if (infoUser != null) {
            usuario = infoUser
            mostrarDadosEmTela()
          }
        },
        onFailure = { failure ->
          Log.d("erro grandao", failure.toString())
        }
      )
    }

    // Configuração dos botões
    btnExcluirConta = findViewById(R.id.button_excluir)
    btnExcluirConta.setOnClickListener { deletarUsuario(auth.currentUser!!.uid) }

    btnSalvarEdicoes = findViewById(R.id.button_editar)
    btnSalvarEdicoes.setOnClickListener { salvarEdicoesBd() }

    buttonTesteDeLogof = findViewById(R.id.buttonTesteDeLogof) // Certifique-se de ter o ID correto no layout
    buttonTesteDeLogof.setOnClickListener {
      FirebaseAuth.getInstance().signOut()
      val voltarTelaLogin = Intent(this, CadastroActivity::class.java)
      startActivity(voltarTelaLogin)
      finish()
    }

    // Configuração do campo de data
    val dataInput: EditText = findViewById(R.id.inputNascimentoUsuario)
    dataInput.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        // Não faz nada
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Não faz nada
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s != null && s.length <= 10) {
          val texto = s.toString()
          val formatted = formatarData(texto)
          if (texto != formatted) {
            dataInput.setText(formatted)
            dataInput.setSelection(formatted.length) // Mantém o cursor no final
          }
        }
      }
    })
  }

  private fun salvarEdicoesBd() {
    val nomeInput: EditText = findViewById(R.id.inputNomeUsuario)
    val emailInput: EditText = findViewById(R.id.inputEmailUsuario)
    val dataInput: EditText = findViewById(R.id.inputNascimentoUsuario)
    val telefoneInput: EditText = findViewById(R.id.inputTelefoneUsuario)

    val usuarioAtualizado = Usuario(
      auth.currentUser?.uid,
      nomeInput.text.toString(),
      emailInput.text.toString(),
      dataInput.text.toString(),
      telefoneInput.text.toString()
    )

    UsuarioDAO(db, auth).editarUsuario(
      usuarioAtualizado,
      onSuccess = {
        exibirMensagem("Editado com sucesso!")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
      },
      onFailure = { exception -> exibirMensagem(exception.toString()) },
    )
  }

  private fun deletarUsuario(idUsuario: String) {
    UsuarioDAO(db, auth).deletarUsuario(
      idUsuario,
      onSuccess = {
        btnExcluirConta = findViewById(R.id.button_excluir)

        btnExcluirConta.setOnClickListener {
          FirebaseAuth.getInstance().signOut()
          val intent = Intent(this, EntryActivity::class.java)
          startActivity(intent)
        }
      },
      onFailure = {
        exibirMensagem("Erro ao deletar conta!")
      }
    )
  }

  private fun formatarData(data: String): String {
    var dataFormatada = data.replace(Regex("[^0-9]"), "") // Remove tudo que não for número
    if (dataFormatada.length > 2) {
      dataFormatada = dataFormatada.substring(0, 2) + "/" + dataFormatada.substring(2)
    }
    if (dataFormatada.length > 5) {
      dataFormatada = dataFormatada.substring(0, 5) + "/" + dataFormatada.substring(5)
    }
    return dataFormatada
  }

  private fun mostrarDadosEmTela() {
    val nomeUsuario: TextView = findViewById(R.id.textNomeUsuario)
    val emailUsuario: TextView = findViewById(R.id.textEmailUsuario)

    val nomeInput: EditText = findViewById(R.id.inputNomeUsuario)
    val emailInput: EditText = findViewById(R.id.inputEmailUsuario)
    val dataInput: EditText = findViewById(R.id.inputNascimentoUsuario)
    val telefoneInput: EditText = findViewById(R.id.inputTelefoneUsuario)

    nomeUsuario.text = usuario.nome
    emailUsuario.text = usuario.email

    nomeInput.setText(usuario.nome)
    emailInput.setText(usuario.email)

    // Aplicar a máscara na data de nascimento
    dataInput.setText(formatarData(usuario.dataNascimento))

    telefoneInput.setText(usuario.telefone)
  }

  fun irParaHome(view: View) {
    val intent = Intent(view.context, MainActivity::class.java)
    view.context.startActivity(intent)
  }

  fun irParaInvest(view: View) {
    val intent = Intent(view.context, InvestimentoActivity::class.java)
    view.context.startActivity(intent)
  }

  fun irParaCofrinho(view: View) {
    val intent = Intent(view.context, CofrinhoActivity::class.java)
    view.context.startActivity(intent)
  }

  fun irParaCalendario(view: View) {
    val intent = Intent(view.context, CalendarioActivity::class.java)
    view.context.startActivity(intent)
  }
}