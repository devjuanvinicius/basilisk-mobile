package com.example.dashboardbasi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class InvestimentoFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Infla o layout para este fragmento
    return inflater.inflate(R.layout.fragment_investimento, container, false)
  }
}
