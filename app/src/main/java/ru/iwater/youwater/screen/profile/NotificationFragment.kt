package ru.iwater.youwater.screen.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.ClientProfileViewModel
import ru.iwater.youwater.databinding.FragmentNotificationBinding
import ru.iwater.youwater.theme.YourWaterTheme
import javax.inject.Inject

class NotificationFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: ClientProfileViewModel by viewModels { factory }

    private val screenComponent = App().buildScreenComponent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNotificationBinding.inflate(inflater)
        binding.composeViewNotificationScreen.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.Default
            )
            setContent {
                YourWaterTheme {
                    NotificationScreen(clientProfileViewModel = viewModel)
                }
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = NotificationFragment()
    }
}