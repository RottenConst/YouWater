package ru.iwater.youwater.screen.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.vm.StatusLoading
import ru.iwater.youwater.vm.AddressViewModel
import ru.iwater.youwater.data.RawAddress
import ru.iwater.youwater.databinding.FragmentAddresessBinding
import ru.iwater.youwater.screen.adapters.AdapterAddresses
import javax.inject.Inject

/**
 * фрагмент выводит список активных адресов клиента
 */
class AddressesFragment : BaseFragment(), AdapterAddresses.OnAddressItemListener, SwipeRefreshLayout.OnRefreshListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: AddressViewModel by viewModels { factory }
    private val binding: FragmentAddresessBinding by lazy { initBinding(LayoutInflater.from(this.context)) }

    private val screenComponent = App().buildScreenComponent()

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
        binding.refreshAddressContainer.setOnRefreshListener(this)
        viewModel.statusLoad.observe(this.viewLifecycleOwner) { status ->
            when (status) {
                StatusLoading.LOADING -> binding.refreshAddressContainer.isRefreshing = true
                StatusLoading.EMPTY -> {
                    binding.nothingAddressString.visibility = View.VISIBLE
                    binding.rvAddresses.visibility = View.GONE
                    binding.refreshAddressContainer.isRefreshing = false
                }
                StatusLoading.DONE -> {
                    binding.refreshAddressContainer.isRefreshing = false
                    binding.nothingAddressString.visibility = View.GONE
                    binding.rvAddresses.visibility = View.VISIBLE
                    viewModel.rawAddress.observe(this.viewLifecycleOwner) {
                        adapter.submitList(it)
                    }
                }
                else -> {
                    warning("Oшибка загрузки адресов")
                    binding.refreshAddressContainer.isRefreshing = false
                }
            }
        }
        binding.btnAddAddress.setOnClickListener {
            this.findNavController().navigate(
                AddressesFragmentDirections.actionAddresessFragmentToAddAddressFragment(false)
            )

        }
        return binding.root
    }

    override fun onDeleteAddressClick(address: RawAddress) {
        viewModel.deleteAddress(address)
    }

    override fun onRefresh() {
        viewModel.getRawAddress()
    }

    private fun warning(message: String) {
        Toast.makeText(this.context, message, Toast.LENGTH_LONG).show()
    }

    private fun initBinding(inflater: LayoutInflater) = FragmentAddresessBinding.inflate(inflater)

    companion object {
        @JvmStatic
        fun newInstance() =
            AddressesFragment()
    }
}