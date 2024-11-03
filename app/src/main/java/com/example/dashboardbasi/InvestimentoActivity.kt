package com.example.dashboardbasi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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


  }
  private lateinit var searchView: SearchView

  fun irParaDash(view: View) {
    val intent = Intent(view.context, MainActivity::class.java)
    view.context.startActivity(intent)
  }
  fun irParaCoffin(view: View){
    val intent = Intent(view.context, CofrinhoActivity::class.java)
    view.context.startActivity(intent)
  }
}
