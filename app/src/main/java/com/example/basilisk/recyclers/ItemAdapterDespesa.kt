package com.example.basilisk.recyclers

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.basilisk.EditarDespesa
import com.example.basilisk.R
import com.example.basilisk.model.Despesas
import java.text.NumberFormat
import java.util.Locale

class ItemAdapterDespesa(
    val lista: List<Despesas>,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<ItemAdapterDespesa.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tituloDespesa: TextView = itemView.findViewById(R.id.tituloItemLista)
        val dataDespesa: TextView = itemView.findViewById(R.id.subTituloItemLista)
        val valorDespesa: TextView = itemView.findViewById(R.id.valorItemLista)
        val deleteButton: Button = itemView.findViewById(R.id.delete_despesa)
        val editButton: Button = itemView.findViewById(R.id.edit_despesa)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.item_lista, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemDaLista = lista[position]

        // Formatar o valor como moeda
        val valorFormatado = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(itemDaLista.valor)

        holder.tituloDespesa.text = itemDaLista.nome
        holder.dataDespesa.text = itemDaLista.dataPagamento
        holder.valorDespesa.text = valorFormatado

        // Configuração do botão de deletar
        holder.deleteButton.setOnClickListener {
            onDeleteClick(itemDaLista.id)
        }

        // Configuração do botão de editar
        holder.editButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, EditarDespesa::class.java)

            // Passando dados para a activity EditarDespesa
            intent.putExtra("idDespesa", itemDaLista.id)
            intent.putExtra("titulo", itemDaLista.nome)
            intent.putExtra("valor", itemDaLista.valor)
            intent.putExtra("dataPagamento", itemDaLista.dataPagamento)

            // Iniciando a Activity para editar
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}