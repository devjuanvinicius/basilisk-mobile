package com.example.basilisk.utils

import android.app.Activity
import android.widget.Toast

fun Activity.exibirMensagem(mensagem: String){
    Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
}