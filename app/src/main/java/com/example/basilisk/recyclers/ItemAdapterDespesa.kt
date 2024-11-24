package com.example.basilisk.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.basilisk.R
import com.example.basilisk.model.Despesas
import java.text.NumberFormat
import java.util.Locale

class ItemAdapterDespesa(
    val lista: List<Despesas>
) : Adapter<ItemAdapterDespesa.ItemViewHolder>() {

    inner class ItemViewHolder(
        val itemView: View
    ) : ViewHolder(itemView){
        val tituloDespesa: TextView = itemView.findViewById(R.id.tituloItemLista)
        val dataDespesa: TextView = itemView.findViewById(R.id.subTituloItemLista)
        val valorDespesa: TextView = itemView.findViewById(R.id.valorItemLista)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val itemView = layoutInflater.inflate(
            R.layout.item_lista, parent, false
        )

        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemDaLista = lista[position]
        val formatador = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        holder.tituloDespesa.text = itemDaLista.nome
        holder.dataDespesa.text = itemDaLista.dataPagamento
        holder.valorDespesa.text = formatador.format(itemDaLista.valor)
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}