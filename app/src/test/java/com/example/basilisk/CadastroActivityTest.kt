package com.example.basilisk

import com.example.basilisk.databinding.ActivityCadastroBinding
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock

class CadastroActivityTest {

  // Mock da atividade com setup do binding
  private val activity = CadastroActivity().apply {
    setupTestBinding()
  }

  // Método para configurar o mock do binding
  private fun CadastroActivity.setupTestBinding() {
    binding = mock(ActivityCadastroBinding::class.java) // Mock do binding
  }

  // Testa o resultado de quando o cadastro é preenchido corretamente
  @Test
  fun cadastroPreenchidoCorretamente() {
    val email = "teste@exemplo.com"
    val senha = "123456"
    val nome = "teste"
    val telefone = "123456789"
    val dataNascimento = "01/01/2000"

    val resultado = activity.validarCampos(email, senha, nome, telefone, dataNascimento)
    assertTrue(resultado) // Campos válidos devem retornar true
  }

  // Testa o resultado de quando o cadastro é preenchido incorretamente
  @Test
  fun cadastroPreenchidoIncorretamente() {
    val email = ""
    val senha = ""
    val nome = ""
    val telefone = ""
    val dataNascimento = ""

    val resultado = activity.validarCampos(email, senha, nome, telefone, dataNascimento)
    assertFalse(resultado) // Campos vazios devem retornar false
  }
}
