package ru.iwater.youwater.screen.basket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.iwater.youwater.R
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.OrderViewModel
import ru.iwater.youwater.data.PaymentStatus
import ru.iwater.youwater.databinding.FragmentCompleteOrderBinding
import ru.iwater.youwater.screen.adapters.MyOrderAdapter
import javax.inject.Inject

class CompleteOrderFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    val viewModel: OrderViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val orderId = CompleteOrderFragmentArgs.fromBundle(this.requireArguments()).orderId
        val binding = FragmentCompleteOrderBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val adapter = MyOrderAdapter()
        binding.cardOrderPay.adapter = adapter
        viewModel.getPaymentStatus(orderId)
        viewModel.listMyOrder.observe(this.viewLifecycleOwner) { myOrder ->
            adapter.submitList(myOrder)
        }
        viewModel.paymentStatus.observe(viewLifecycleOwner) {
            when (it) {
                PaymentStatus.SUCCESSFULLY -> {

                }
                else -> {
                    binding.ivPayComplete.setImageResource(R.drawable.ic_cancel)
                    binding.tvLogoPayComplete.text = "Ошибка, не удалось оплатить заказ"
                    binding.tvOrderPayComment.text = ""
                }
            }
        }
        binding.btnGoHome.setOnClickListener {
            findNavController().navigate(CompleteOrderFragmentDirections.actionCompleteOrderFragmentToHomeFragment())
        }
        activity?.actionBar?.hide()
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CompleteOrderFragment()
    }
}