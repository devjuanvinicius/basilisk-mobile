package com.example.basilisk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.basilisk.R

class ProgressBarFragment : Fragment() {
    private var progressValue: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            progressValue = it.getInt(ARG_PROGRESS, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_progress_bar, container, false)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.progress = progressValue
        return view
    }

    companion object {
        private const val ARG_PROGRESS = "progress"

        fun newInstance(progress: Int): ProgressBarFragment {
            val fragment = ProgressBarFragment()
            val args = Bundle()
            args.putInt(ARG_PROGRESS, progress)
            fragment.arguments = args
            return fragment
        }
    }
}
