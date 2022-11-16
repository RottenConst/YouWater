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
import ru.iwater.youwater.data.StatusLoading
import ru.iwater.youwater.data.OrderViewModel
import ru.iwater.youwater.databinding.FragmentMyOrdersBinding
import ru.iwater.youwater.screen.adapters.MyOrderAdapter
import javax.inject.Inject

class MyOrdersFragment : BaseFragment(), MyOrderAdapter.onReplayLastOrder, SwipeRefreshLayout.OnRefreshListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val screenComponent = App().buildScreenComponent()
    val viewModel: OrderViewModel by viewModels { factory }

    val binding: FragmentMyOrdersBinding by lazy { FragmentMyOrdersBinding.inflate(LayoutInflater.from(this.context)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.lifecycleOwner = this
        binding.refreshContainer.setOnRefreshListener(this)
        viewModel.getOrderFromCrm()
        val myOrderAdapter = MyOrderAdapter(this)
        binding.rvOrders.adapter = myOrderAdapter
        viewModel.listMyOrder.observe(this.viewLifecycleOwner) { myOrders ->
            myOrderAdapter.submitList(myOrders)
            if (myOrders.isNullOrEmpty()) binding.tvNothingOrderText.visibility = View.VISIBLE
            else binding.tvNothingOrderText.visibility = View.GONE
        }
        statusLoad()
        return binding.root
    }

    override fun onRefresh() {
        viewModel.getOrderFromCrm()
    }

    override fun onClickReplayButton(idOrder: Int) {
        findNavController().navigate(
            MyOrdersFragmentDirections.actionMyOrdersFragmentToCreateOrderFragment(false, idOrder)
        )

    }

    private fun statusLoad() {
        viewModel.statusLoad.observe(this.viewLifecycleOwner) { statusLoad ->
            when(statusLoad) {
                StatusLoading.LOADING -> binding.refreshContainer.isRefreshing = true
                StatusLoading.DONE -> binding.refreshContainer.isRefreshing = false
                StatusLoading.EMPTY -> binding.refreshContainer.isRefreshing = false
                else -> {
                    Toast.makeText(this.context, "Ошибка", Toast.LENGTH_SHORT).show()
                    binding.refreshContainer.isRefreshing = false
                }

            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyOrdersFragment()
    }
}