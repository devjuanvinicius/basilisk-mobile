package com.example.basilisk.utils

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.example.basilisk.CofrinhoActivity
import com.example.basilisk.InvestimentoActivity
import com.example.basilisk.PerfilActivity
import com.example.basilisk.novaRendaActivity

fun Activity.exibirMensagem(mensagem: String){
    Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
}
fun Activity.irParaInvestimento(view: View) {
    val intent = Intent(view.context, InvestimentoActivity::class.java)
    view.context.startActivity(intent)
}
fun Activity.irParaCoffin(view: View) {
    val intent = Intent(view.context, CofrinhoActivity::class.java)
    view.context.startActivity(intent)
}
fun Activity.irParaAddRenda(view: View) {
    val intent = Intent(view.context, novaRendaActivity::class.java)
    view.context.startActivity(intent)
}
fun Activity.irParaPerfil(view: View) {
    val intent = Intent(view.context, PerfilActivity::class.java)
    view.context.startActivity(intent)
}