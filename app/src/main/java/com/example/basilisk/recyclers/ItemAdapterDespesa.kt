package com.example.basilisk.recyclers

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.basilisk.EditarDespesa
import com.example.basilisk.R
import com.example.basilisk.model.Despesas
import java.text.NumberFormat
import java.util.Locale

class ItemAdapterDespesa(
    val lista: MutableList<Despesas>,
    private val onDeleteClick: (String) -> Unit
) : Adapter<ItemAdapterDespesa.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : ViewHolder(itemView) {
        val tituloDespesa: TextView = itemView.findViewById(R.id.tituloDespesa) // Título da despesa
        val dataDespesa: TextView = itemView.findViewById(R.id.dataDespesa)     // Data da despesa
        val valorDespesa: TextView = itemView.findViewById(R.id.valorDespesa)   // Valor da despesa
        val deleteButton: View = itemView.findViewById(R.id.delete_despesa)     // Botão de deletar
        val editButton: View = itemView.findViewById(R.id.edit_despesa)         // Botão de editar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.item_lista, parent, false) // Certifique-se de que o layout esteja correto
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemDaLista = lista[position]

        // Formatar o valor para exibição com "R$" (sem alterar o valor real)
        val valorFormatado = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(itemDaLista.valor)

        // Acessando corretamente os componentes do ViewHolder
        holder.tituloDespesa.text = itemDaLista.nome
        holder.dataDespesa.text = itemDaLista.dataPagamento
        holder.valorDespesa.text = valorFormatado // Exibe o valor com "R$"

        // Configurar o botão de deletar
        holder.deleteButton.setOnClickListener {
            onDeleteClick(itemDaLista.id) // Passa o ID da despesa para a função de callback
        }

        // Configurar o botão de editar
        holder.editButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, EditarDespesa::class.java)
            intent.putExtra("idDespesa", itemDaLista.id)
            intent.putExtra("titulo", itemDaLista.nome)
            intent.putExtra("valor", itemDaLista.valor) // Passa o valor real da despesa
            intent.putExtra("dataPagamento", itemDaLista.dataPagamento)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    fun updateList(novaLista: List<Despesas>) {
        lista.clear()
        lista.addAll(novaLista)
        notifyDataSetChanged()
    }
}
