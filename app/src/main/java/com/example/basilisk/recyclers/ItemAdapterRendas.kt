package com.example.basilisk.recyclers

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.basilisk.EditarRenda
import com.example.basilisk.R
import com.example.basilisk.model.Renda
import java.text.NumberFormat
import java.util.Locale

class ItemAdapterRendas(
    val lista: MutableList<Renda>,
    private val onDeleteClick: (String) -> Unit
) : Adapter<ItemAdapterRendas.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : ViewHolder(itemView) {
        val tituloRenda: TextView = itemView.findViewById(R.id.tituloDespesa)
        val dataRenda: TextView = itemView.findViewById(R.id.dataDespesa)
        val valorRenda: TextView = itemView.findViewById(R.id.valorDespesa)
        val deleteButton: View = itemView.findViewById(R.id.delete_despesa)
        val editButton: View = itemView.findViewById(R.id.edit_despesa)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.item_lista, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemDaLista = lista[position]

        // Formatar o valor para exibição com "R$" (sem alterar o valor real)
        val valorFormatado = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(itemDaLista.valor)

        // Acessando corretamente os componentes do ViewHolder
        holder.tituloRenda.text = itemDaLista.nome
        holder.dataRenda.text = itemDaLista.dataRecebimento
        holder.valorRenda.text = valorFormatado // Exibe o valor com "R$"

        holder.deleteButton.setOnClickListener {
            onDeleteClick(itemDaLista.id)
        }

        holder.editButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, EditarRenda::class.java)
            intent.putExtra("idRenda", itemDaLista.id)
            intent.putExtra("nome", itemDaLista.nome)

            // Enviar o valor numérico real (sem "R$") para o Intent
            intent.putExtra("valor", itemDaLista.valor)  // Aqui você envia o valor real

            intent.putExtra("dataRecebimento", itemDaLista.dataRecebimento)
            holder.itemView.context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return lista.size
    }

    fun updateList(novaLista: List<Renda>) {
        lista.clear()
        lista.addAll(novaLista)
        notifyDataSetChanged()
    }
}
