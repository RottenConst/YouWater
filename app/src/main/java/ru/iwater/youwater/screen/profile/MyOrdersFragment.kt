package ru.iwater.youwater.screen.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ru.iwater.youwater.base.App
import ru.iwater.youwater.data.OrderViewModel
import ru.iwater.youwater.databinding.FragmentMyOrdersBinding
import ru.iwater.youwater.screen.adapters.MyOrderAdapter
import javax.inject.Inject

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyOrdersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyOrdersFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val screenComponent = App().buildScreenComponent()
    val viewModel: OrderViewModel by viewModels { factory }

    val binding: FragmentMyOrdersBinding by lazy { FragmentMyOrdersBinding.inflate(LayoutInflater.from(this.context)) }

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
        viewModel.getOrderFromCrm()
        val myOrderAdapter = MyOrderAdapter()
        binding.rvOrders.adapter = myOrderAdapter
        viewModel.listMyOrder.observe(this.viewLifecycleOwner) { myOrders ->
            myOrderAdapter.submitList(myOrders)
            if (myOrders.isNullOrEmpty()) binding.tvNothingOrderText.visibility = View.VISIBLE
            else binding.tvNothingOrderText.visibility = View.GONE
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyOrdersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}