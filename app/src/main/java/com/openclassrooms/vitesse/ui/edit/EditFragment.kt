package com.openclassrooms.vitesse.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import com.openclassrooms.vitesse.R
import com.openclassrooms.vitesse.databinding.AddScreenBinding
import com.openclassrooms.vitesse.databinding.EditScreenBinding
import com.openclassrooms.vitesse.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class EditFragment : Fragment() {

    private lateinit var binding: EditScreenBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSave()
        setupToolbar()
    }

    private fun setupSave() {
        //
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

}

