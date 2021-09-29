package ru.iwater.youwater.screen.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import ru.iwater.youwater.R
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.databinding.StartFragmentBinding

class StartFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = StartFragmentBinding.inflate(inflater)

        val navController = NavHostFragment.findNavController(this)
        binding.btnStart.setOnClickListener {
            navController.navigate(R.id.loginFragment)
        }

        return binding.root
    }

    companion object {
        fun newInstance(): StartFragment {
            return StartFragment()
        }
    }
}