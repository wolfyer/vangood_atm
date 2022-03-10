package com.vangood.atm

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.vangood.atm.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    var remember = false
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = requireContext().getSharedPreferences("atm", Context.MODE_PRIVATE)
        val checked = pref.getBoolean("rem_username",false)
        binding.cbRemember.isChecked = checked
        binding.cbRemember.setOnCheckedChangeListener { compoundButton, checked ->
            //當打勾要記得 沒勾的要刪除
            remember = checked
            pref.edit().putBoolean("rem_username", remember).apply()
            if(!checked){
                pref.edit().putString("USER","").apply()
            }
        val preUser = pref.getString("USER","")
        if (preUser !=""){
            binding.edUsername.setText(preUser)
        }

        }

        binding.buttonFirst.setOnClickListener {
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            //Login stuff
            val username = binding.edUsername.text.toString()
            val password = binding.edPassword.text.toString()

            if (username =="jack" && password =="1234"){
                //save username to preferences
                if (remember){
                val pref = requireContext().getSharedPreferences("atm", Context.MODE_PRIVATE)
                // pref.getString("USER","")//讀
                pref.edit()//寫
                    .putString("USER",username)
                    .putInt("LEVEL",3)
                    .apply()//有空趕快寫 不是下一行要用.commit()
                    }

                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            }else{
                //error
                AlertDialog.Builder(requireContext())
                    .setTitle("Login")
                    .setMessage("Login Failed")
                    .setPositiveButton("OK",null)
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}