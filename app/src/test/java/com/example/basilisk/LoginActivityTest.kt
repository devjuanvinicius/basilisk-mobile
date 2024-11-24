package com.example.basilisk

import android.content.Intent
import com.example.basilisk.databinding.ActivityCadastroBinding
import com.example.basilisk.databinding.ActivityLoginBinding
import com.example.basilisk.utils.exibirMensagem
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class LoginActivityTest {

  // Mock da atividade com setup do binding
  private val activity = CadastroActivity().apply {
    setupTestBinding()
  }

  // Método para configurar o mock do binding
  private fun CadastroActivity.setupTestBinding() {
    binding = mock(ActivityCadastroBinding::class.java) // Mock do binding
  }

  @Test
  fun loginPreenchidoIncorretamente() {
    // Mocking exibirMensagem
    val mockActivity = mock(LoginActivity::class.java)
    val mockBinding = mock(ActivityLoginBinding::class.java)
    `when`(mockActivity.binding).thenReturn(mockBinding)

    val email = ""
    val senha = ""

    // Simulando a ação de clicar no botão
    mockActivity.binding.btnEntrar.performClick()

    verify(mockActivity).exibirMensagem("Preencha todos os campos")
  }

  // Quando o login é bem-sucedido, o usuário deve ser encaminhado para o MainActivity
  @Test
  fun navegacaoParaMainActivityAcontece() {
    // Mockando FirebaseAuth
    val mockAuth = mock(FirebaseAuth::class.java)
    val mockActivity = mock(LoginActivity::class.java)
    val mockBinding = mock(ActivityLoginBinding::class.java)
    `when`(mockActivity.binding).thenReturn(mockBinding)
    `when`(mockActivity.auth).thenReturn(mockAuth)

    // Criando o mock do AuthResult
    val mockAuthResult = mock(AuthResult::class.java)

    // Criando o mock do Task<AuthResult>
    val mockTask = mock(Task::class.java) as Task<AuthResult>
    `when`(mockAuth.signInWithEmailAndPassword(anyString(), anyString()))
      .thenReturn(mockTask)

    // Simulando uma resposta de login bem-sucedido
    `when`(mockTask.isSuccessful).thenReturn(true)
    `when`(mockTask.result).thenReturn(mockAuthResult)

    // Simulando a ação de clicar no botão
    mockActivity.binding.btnEntrar.performClick()

    // Verificando se a MainActivity é iniciada
    verify(mockActivity).startActivity(any(Intent::class.java))
    verify(mockActivity).finish()
  }

  //Caso o usuário clique para se cadastrar ao invés de preencher o campo de login, ele deve ser redirecionado para o cadastro.
  @Test
  fun navegacaoParaCadastroAcontece() {
    // Mockando a Activity
    val mockActivity = mock(LoginActivity::class.java)
    val mockBinding = mock(ActivityLoginBinding::class.java)
    `when`(mockActivity.binding).thenReturn(mockBinding)

    // Simulando a ação de clicar no link de registro
    mockActivity.binding.registerLink.performClick()

    // Verificando se a CadastroActivity é iniciada
    verify(mockActivity).startActivity(any(Intent::class.java))
  }
}