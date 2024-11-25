package com.example.basilisk

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.hasErrorText
import org.junit.Test

class ComponenteTest {

  @Test
  fun camposDeLoginVazios() {
    // Lança a Activity em teste
    val activityScenario = ActivityScenario.launch(CadastroActivity::class.java)

    // Clica no botão "Cadastrar" sem preencher nenhum campo
    onView(withId(R.id.buttoncadastro)).perform(click())

    // Verifica se aparece a mensagem de erro para o campo Nome Completo
    onView(withId(R.id.NomeCompletoCad))
      .check(matches(hasErrorText("Campo obrigatório")))

    // Verifica se aparece a mensagem de erro para o campo Email
    onView(withId(R.id.emailCad))
      .check(matches(hasErrorText("Campo obrigatório")))

    // Finaliza a Activity
    activityScenario.close()
  }

  @Test
  fun navegacaoDeCadastroParaLogin() {
    // Lança a Activity em teste
    val activityScenario = ActivityScenario.launch(CadastroActivity::class.java)

    // Clica no TextView "Já possui uma conta? faça o login"
    onView(withId(R.id.loginLink)).perform(click())

    // Verifica se a Activity de login foi aberta
    intended(hasComponent(LoginActivity::class.java.name))

    // Finaliza a Activity
    activityScenario.close()
  }
}