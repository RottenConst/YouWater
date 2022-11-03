package ru.iwater.youwater.screen.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import ru.iwater.youwater.R
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.AuthViewModel
import ru.iwater.youwater.data.StatusSession
import ru.iwater.youwater.databinding.StartFragmentBinding
import ru.iwater.youwater.screen.MainActivity
import javax.inject.Inject

class StartFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    private val viewModel: AuthViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = StartFragmentBinding.inflate(inflater)
        val navController = NavHostFragment.findNavController(this)
        binding.btnStart.visibility = View.GONE
        viewModel.checkSession()
        viewModel.statusSession.observe(viewLifecycleOwner) { statusSession ->
            when (statusSession) {
                StatusSession.TRY -> {
                    MainActivity.start(this.context)
                    this.activity?.finish()
                }
                StatusSession.FALSE -> binding.btnStart.visibility = View.VISIBLE
                StatusSession.ERROR -> Toast.makeText(context,
                    "ОШИБКА СОЕДИНЕНИЯ",
                    Toast.LENGTH_LONG).show()
                else -> Toast.makeText(context,
                    "НЕ ИЗВЕСНАЯ ОШИБКА",
                    Toast.LENGTH_LONG).show()
            }
        }
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