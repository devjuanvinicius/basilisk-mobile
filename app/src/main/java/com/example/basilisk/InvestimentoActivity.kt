package com.example.basilisk

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.basilisk.utils.EditInvestActivity

class InvestimentoActivity : AppCompatActivity() {

  lateinit var homeButton: ImageButton

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContentView(R.layout.activity_investimento)
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
      insets
    }

    homeButton = findViewById(R.id.homeButton)
    homeButton.setOnClickListener{
      val intent = Intent(this, MainActivity::class.java)

      startActivity(intent)
    }
  }
  fun irParaAddInvest(view: View) {
    val intent = Intent(view.context, AddInvestActivity::class.java)
    view.context.startActivity(intent)
  }
  fun irParaHome(view: View) {
    val intent = Intent(view.context, MainActivity::class.java)
    view.context.startActivity(intent)
  }
  fun irParaCofrinho(view: View) {
    val intent = Intent(view.context, CofrinhoActivity::class.java)
    view.context.startActivity(intent)
  }
  fun irParaPerfil(view: View) {
    val intent = Intent(view.context, PerfilActivity::class.java)
    view.context.startActivity(intent)
  }
  fun irParaEditInvest(view: View) {
    val intent = Intent(view.context, EditInvestActivity::class.java)
    view.context.startActivity(intent)
  }
  fun irParaCalendario(view: View) {
    val intent = Intent(view.context, CalendarioActivity::class.java)
    view.context.startActivity(intent)
  }
}