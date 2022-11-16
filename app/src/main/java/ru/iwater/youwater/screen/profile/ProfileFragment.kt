package ru.iwater.youwater.screen.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.ClientProfileViewModel
import ru.iwater.youwater.databinding.FragmentProfileBinding
import javax.inject.Inject

class ProfileFragment : BaseFragment() {

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
        val binding = FragmentProfileBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.clientVM = viewModel
        binding.btnMyData.setOnClickListener {
            this.findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToUserDataFragment(false)
            )
        }
        binding.btnAddress.setOnClickListener {
            this.findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToAddresessFragment()
            )
        }
        binding.btnBankCard.setOnClickListener {
            this.findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToBankCardFragment()
            )
        }
        binding.btnNotification.setOnClickListener {
            this.findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToNotificationFragment()
            )
        }
        binding.btnMyFavorite.setOnClickListener {
            this.findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToFavoriteFragment()
            )
        }
        binding.btnMyOrder.setOnClickListener {
            this.findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToMyOrdersFragment()
            )
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ProfileFragment()
    }
}