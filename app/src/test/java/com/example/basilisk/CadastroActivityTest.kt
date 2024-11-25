package com.example.basilisk

import android.content.Context
import android.view.LayoutInflater
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.basilisk.databinding.ActivityCadastroBinding
import com.example.basilisk.model.Usuario
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(manifest = "AndroidManifest.xml")
@RunWith(AndroidJUnit4::class)
class CadastroActivityTest {

  private lateinit var activity: CadastroActivity
  private lateinit var context: Context

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    activity = CadastroActivity()
    activity.binding = ActivityCadastroBinding.inflate(LayoutInflater.from(context))
  }

  @Test
  fun testValidarCamposCorretamentePreenchidos() {
    val email = "teste@exemplo.com"
    val senha = "123456"
    val nome = "Teste"
    val telefone = "123456789"
    val dataNascimento = "01/01/2000"

    val resultado = activity.validarCampos(email, senha, nome, telefone, dataNascimento)
    assertTrue(resultado) // Espera-se que campos válidos retornem true
  }

  @Test
  fun testValidarCamposVazios() {
    val email = ""
    val senha = ""
    val nome = ""
    val telefone = ""
    val dataNascimento = ""

    val resultado = activity.validarCampos(email, senha, nome, telefone, dataNascimento)
    assertFalse(resultado) // Espera-se que campos vazios retornem false
  }

  @Test
  fun testValidarEmailInvalido() {
    val email = "email_invalido"
    val senha = "123456"
    val nome = "Teste"
    val telefone = "123456789"
    val dataNascimento = "01/01/2000"

    val resultado = activity.validarCampos(email, senha, nome, telefone, dataNascimento)
    assertFalse(resultado) // Espera-se que um e-mail inválido retorne false
  }

  @Test
  fun testLimparCampos() {
    with(activity.binding) {
      NomeCompletoCad.setText("Teste")
      nascimentoCad.setText("01/01/2000")
      TelefoneCad.setText("123456789")
      emailCad.setText("teste@exemplo.com")
      senhaCad.setText("123456")
    }

    activity.limparCampos()

    with(activity.binding) {
      assertTrue(NomeCompletoCad.text.isEmpty())
      assertTrue(nascimentoCad.text.isEmpty())
      assertTrue(TelefoneCad.text.isEmpty())
      assertTrue(emailCad.text.isEmpty())
      assertTrue(senhaCad.text.isEmpty())
    }
  }
}
