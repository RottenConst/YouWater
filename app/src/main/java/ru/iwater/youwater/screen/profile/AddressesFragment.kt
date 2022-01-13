package ru.iwater.youwater.screen.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddressesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.lifecycleOwner = this
        val adapter = AdapterAddresses(this)
        binding.rvAddresses.adapter = adapter
        viewModel.addressList.observe(this.viewLifecycleOwner, {
            adapter.submitList(it)
        })
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddressesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddressesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}