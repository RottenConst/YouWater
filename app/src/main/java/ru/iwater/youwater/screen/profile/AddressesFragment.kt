package ru.iwater.youwater.screen.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.Address
import ru.iwater.youwater.data.AddressViewModel
import ru.iwater.youwater.databinding.FragmentAddresessBinding
import ru.iwater.youwater.screen.adapters.AdapterAddresses
import javax.inject.Inject


class AddressesFragment : BaseFragment(), AdapterAddresses.OnAddressItemListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: AddressViewModel by viewModels { factory }
    private val binding: FragmentAddresessBinding by lazy { initBinding(LayoutInflater.from(this.context)) }


    private val screenComponent = App().buildScreenComponent()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.lifecycleOwner = this
        val adapter = AdapterAddresses(this)
        binding.rvAddresses.adapter = adapter
        viewModel.getAllFactAddress()
        viewModel.addressList.observe(this.viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                Toast.makeText(context, "Ошибка, не удаётся загрузить адреса", Toast.LENGTH_SHORT).show()
            } else {
                adapter.submitList(it)
            }
        }
        binding.btnAddAddress.setOnClickListener {
            this.findNavController().navigate(
                AddressesFragmentDirections.actionAddresessFragmentToAddAddressFragment()
            )

        }
        return binding.root
    }

    override fun onDeleteAddressClick(address: Address) {
        viewModel.deleteAddress(address)
    }

    private fun initBinding(inflater: LayoutInflater) = FragmentAddresessBinding.inflate(inflater)

    companion object {
        @JvmStatic
        fun newInstance() =
            AddressesFragment()
    }
}