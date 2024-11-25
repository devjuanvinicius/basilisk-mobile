package com.example.basilisk.recyclers

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.basilisk.EditInvestActivity
import com.example.basilisk.R
import com.example.basilisk.database.InvestimentoDAO
import com.example.basilisk.model.Investimento
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class ItemAdapterInvestimento(
    val lista: List<Investimento>
) : RecyclerView.Adapter<ItemAdapterInvestimento.ItemViewHolder>() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.tituloItemLista)
        val codigo: TextView = itemView.findViewById(R.id.subTituloItemLista)
        val valor: TextView = itemView.findViewById(R.id.valorItemLista)
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
        val formatador = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))


        holder.titulo.text = itemDaLista.nomeAcao
        holder.codigo.text = formatador.format(itemDaLista.valor)
        holder.valor.text = ""

        holder.editButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, EditInvestActivity::class.java)
            intent.putExtra("codigoInvestimento", itemDaLista.codigoAcao)
            intent.putExtra("valor", itemDaLista.valor)
            intent.putExtra("nomeAcao", itemDaLista.nomeAcao)
            intent.putExtra("dataCompra", itemDaLista.dataCompra)
            intent.putExtra("qtdAcoes", itemDaLista.qtdAcoes)

            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener{
            InvestimentoDAO(db).deletarInvestimento(
                auth.currentUser!!.uid,
                codigoAcao = itemDaLista.codigoAcao,
                onSuccess = {

                },
                onFailure = {}
            )
        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}
