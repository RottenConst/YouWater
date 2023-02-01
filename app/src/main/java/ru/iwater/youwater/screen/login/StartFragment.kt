package ru.iwater.youwater.screen.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.vm.AuthViewModel
import ru.iwater.youwater.databinding.StartFragmentBinding
import ru.iwater.youwater.theme.YourWaterTheme
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
        val fragmentActivity = this.requireActivity()
        binding.composeViewSplash.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {
                YourWaterTheme {
                    StartAppScreen(fragmentActivity, viewModel, navController)
                }
            }
        }

        return binding.root
    }

    companion object {
        fun newInstance(): StartFragment {
            return StartFragment()
        }
    }
}