package com.vangood.atm

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.vangood.atm.databinding.FragmentBmiBinding

class BmiFragment:Fragment() {
    lateinit var binding:FragmentBmiBinding
    //val viewModel by viewModels<> {  }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBmiBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bBmi.setOnClickListener {
            val weight = binding.edWeight.toString().toInt()
            val height = binding.edHeight.toString().toInt()
            var bmi = weight/(height*height)
            binding.tvBmi.setText("your bmi is $bmi")

        }


    }
}