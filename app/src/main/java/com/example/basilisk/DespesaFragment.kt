package com.example.basilisk

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class DespesaFragment : Fragment(R.layout.fragment_despesa) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.findViewById<View>(R.id.novaDespesa)?.setOnClickListener {
            irParaAddDespesa()
        }


    }

    private fun irParaAddDespesa() {
        val intent = Intent(requireContext(), novaDespesa::class.java)
        startActivity(intent)
    }


}
