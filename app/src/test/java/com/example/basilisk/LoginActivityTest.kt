package com.example.basilisk

import android.widget.EditText
import androidx.test.core.app.ActivityScenario
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.junit.Assert.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class) // Usando MockitoJUnitRunner ao invés de RobolectricTestRunner
class LoginActivityTest {

  private lateinit var scenario: ActivityScenario<LoginActivity>

  @Mock
  lateinit var mockAuth: FirebaseAuth
  @Mock
  lateinit var mockEmailEditText: EditText
  @Mock
  lateinit var mockSenhaEditText: EditText

  @Before
  fun setUp() {
    // Inicializa o Mockito
    MockitoAnnotations.openMocks(this)

    // Criação do Activity e inicia ela
    scenario = ActivityScenario.launch(LoginActivity::class.java)
    scenario.onActivity { activity: LoginActivity ->
      activity.auth = mockAuth // Definindo o auth para o mockado
    }
  }

  @Test
  fun testUsuarioNaoVazio() {
    scenario.onActivity { activity: LoginActivity ->
      val emailEditText = activity.findViewById<EditText>(R.id.emailCad)
      val email = emailEditText.text.toString().trim()
      assertTrue("Email não pode ser vazio", email.isNotEmpty())
    }
  }

  @Test
  fun testSenhaNaoVazia() {
    scenario.onActivity { activity: LoginActivity ->
      val senhaEditText = activity.findViewById<EditText>(R.id.senhaCad)
      val senha = senhaEditText.text.toString().trim()
      assertTrue("Senha não pode ser vazia", senha.isNotEmpty())
    }
  }

  @Test
  fun testLoginComCredenciaisValidas() {
    `when`(mockAuth.signInWithEmailAndPassword("usuario_teste", "senha_teste"))
      .thenReturn(mock()) // Mockar a resposta de sucesso ou falha do login

    scenario.onActivity { activity: LoginActivity ->
      val emailEditText = activity.findViewById<EditText>(R.id.emailCad)
      val senhaEditText = activity.findViewById<EditText>(R.id.senhaCad)

      // Simular entrada de dados
      emailEditText.setText("usuario_teste")
      senhaEditText.setText("senha_teste")

      activity.binding.btnEntrar.performClick() // Simular clique no botão "Entrar"

      // Verificar se o método de login foi chamado
      verify(mockAuth).signInWithEmailAndPassword("usuario_teste", "senha_teste")
    }
  }
}
