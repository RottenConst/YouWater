package ru.iwater.youwater.screen.basket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonObject
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import ru.iwater.youwater.R
import ru.iwater.youwater.base.App
import ru.iwater.youwater.data.Order
import ru.iwater.youwater.data.OrderViewModel
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.databinding.FragmentCreateOrderBinding
import ru.iwater.youwater.screen.adapters.OrderProductAdapter
import timber.log.Timber
import java.util.*
import javax.inject.Inject

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateOrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateOrderFragment : Fragment(), TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener {
    private var param1: String? = null
    private var param2: String? = null

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    val viewModel: OrderViewModel by viewModels { factory }

    private val productClear = mutableListOf<Product>()
    private var periodOne = ""
    private var periodTwo = ""
    private var addressString = ""
    private var order =
        Order(0, 0, "", mutableListOf(), "", 0, "", "", 0, "", "", "", JsonObject(), "")

    private val binding: FragmentCreateOrderBinding by lazy {
        FragmentCreateOrderBinding.inflate(
            LayoutInflater.from(this.context)
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding.lifecycleOwner = this
        viewModel.client.observe(viewLifecycleOwner, {
            binding.tvNameClient.text = it.name
            binding.tvTelNumber.text = it.contact
            order.clientId = it.id
            order.name = it.name
            order.contact = it.contact
        })
        viewModel.address.observe(viewLifecycleOwner, {
            if (it != null) {
                when {
                    it.building == null -> {
                        "${it.region}, ул.${it.street}, д.${it.house} ".also {
                            binding.tvAddressOrder.text = it
                            addressString = it
                        }
                    }
                    it.entrance == null -> {
                        "${it.region}, ул.${it.street}, д.${it.house} ".also {
                            binding.tvAddressOrder.text = it
                            addressString = it
                        }
                    }
                    it.floor == null -> {
                        "${it.region}, ул.${it.street}, д.${it.house}, подьезд ${it.entrance}".also {
                            binding.tvAddressOrder.text = it
                            addressString = it
                        }
                    }
                    it.flat == null -> {
                        "${it.region}, ул.${it.street}, д.${it.house}, подьезд ${it.entrance}, этаж ${it.floor}".also {
                            binding.tvAddressOrder.text = it
                            addressString = it
                        }
                    }
                    else -> {
                        "${it.region}, ул.${it.street}, д.${it.house}, подьезд ${it.entrance}, этаж ${it.floor}, кв.${it.flat} ".also {
                            binding.tvAddressOrder.text = it
                            addressString = it
                        }
                    }
                }
                order.addressJson.apply {
                    addProperty("region", it.region)
                    addProperty("street", it.street)
                    addProperty("house", it.house)
                    addProperty("building", it.building)
                    addProperty("entrance", it.entrance)
                    addProperty("floor", it.floor)
                    addProperty("flat", it.flat)
                }
            } else {
                binding.tvAddressOrder.text = "Добавить адрес"
                binding.tvAddressOrder.setOnClickListener {
                    this.findNavController().navigate(
                        CreateOrderFragmentDirections.actionCreateOrderFragmentToAddAddressFragment()
                    )
                }
            }
        })
        binding.tvTimeOrder.text = "Укажите дату и время"
        binding.tvTimeOrder.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val hours = calendar.get(Calendar.HOUR_OF_DAY)
            val minutes = calendar.get(Calendar.MINUTE)
            //Установить время
            val timePickerDialog = TimePickerDialog.newInstance(this, hours, minutes, true)
            timePickerDialog.title = "Укажите время заказа"
            timePickerDialog.setMinTime(9, 0, 0)
            timePickerDialog.setMaxTime(20, 0, 0)
            timePickerDialog.show(parentFragmentManager, "SetTimeDialog")
            //Установить дату
            val datePickerDialog = DatePickerDialog.newInstance(this, year, month, day)
            datePickerDialog.setTitle("Укажите время заказа")
            datePickerDialog.minDate = calendar
            datePickerDialog.show(parentFragmentManager, "SetDateDialog")

            order.date = "${calendar.timeInMillis / 1000}"
        }
        val adapterOrder = OrderProductAdapter()
        val product = mutableListOf<Product>()
        binding.rvOrderProduct.adapter = adapterOrder
        viewModel.products.observe(viewLifecycleOwner, { products ->
            adapterOrder.submitList(products)
            productClear.addAll(products)
            var priceTotal = 0
            var priceCompleteDiscount = 0
            var priceComplete = 0
            var priceTotalDiscount = 0
            var discount = 0
            var price = 0
            product.addAll(products)
            products.forEach { product ->
                val prices = product.price.removeSuffix(";")
                val priceList = prices.split(";")
                val count = product.count
                if (product.category == 1) {
                    priceList.forEach {
                        val priceCount = it.split(":")
                        if (priceCount[0].toInt() <= count) {
                            discount = (priceCount[1].toInt() - 15) * count
                            price = priceCount[1].toInt() * count
                        }
                    }
                    priceCompleteDiscount += discount
                } else {
                    priceList.forEach {
                        val priceCount = it.split(":")
                        if (priceCount[0].toInt() <= count) {
                            price = priceCount[1].toInt() * count
                        }
                    }
                    priceComplete += price
                }
                val priceOne = if (discount != 0) {
                    discount / count
                } else price / count
                Timber.d("COST ==== $priceOne")
                val productJs = JsonObject()
                productJs.addProperty("id", product.id)
                productJs.addProperty("count", product.count)
                productJs.addProperty("price", priceOne)
                order.waterEquip.add(productJs)
                discount = 0
                priceTotal += price
                priceTotalDiscount = priceCompleteDiscount + priceComplete
            }
            "${priceTotal}₽".also { binding.tvCostOrder.text = it }
            "${priceTotalDiscount}₽".also { binding.tvTotalSumCost.text = it }
            order.orderCost = priceTotal
        })
        val context = this.context
        if (context != null) {
            val typesOfPay = mutableListOf(
                "Оплата по карте курьеру",
                "Оплата наличными",
                "Выберите способ оплаты",
            )
            val spinnerAdapter =
                ArrayAdapter(context,
                    R.layout.spinner_item_layout_resource,
                    R.id.TextView,
                    typesOfPay)
            binding.spinnerPaymentType.adapter = spinnerAdapter
            binding.spinnerPaymentType.setSelection(2)
            val itemSelectedListener: AdapterView.OnItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long,
                    ) {
                        order.paymentType = parent?.getItemAtPosition(position).toString()
                        typesOfPay.remove("Выберите способ оплаты")
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
            binding.spinnerPaymentType.onItemSelectedListener = itemSelectedListener
            if (binding.spinnerPaymentType.isFocused) {
                spinnerAdapter.notifyDataSetChanged()
            }
        }
        binding.btnCreateOrder.setOnClickListener {
            order.period = "$periodOne - $periodTwo"
            if (binding.tvTimeOrder.text != "Укажите дату и время" && order.paymentType != "Выберите способ оплаты" && addressString.isNotEmpty()) {
//                viewModel.orderCreate(order)
                viewModel.sendAndSaveOrder(order, product, addressString)
                viewModel.clearProduct(productClear)
                Toast.makeText(this.context, "Заявка отправлена", Toast.LENGTH_LONG).show()
                this.findNavController().navigate(
                    CreateOrderFragmentDirections.actionCreateOrderFragmentToHomeFragment()
                )
            } else {
                Timber.d("${order.period}, ${order.paymentType}")
                Toast.makeText(this.context, "Укажите время и тип оплаты", Toast.LENGTH_LONG).show()
            }
        }
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateOrderFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateOrderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //Калбэк для устовки времени
    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        if (minute < 10) {
            periodOne = "$hourOfDay:0$minute"
            periodTwo = "${hourOfDay + 2}:0${minute}"
        } else {
            periodOne = "$hourOfDay:$minute"
            periodTwo = "${hourOfDay + 2}:${minute}"
        }
        binding.tvTimeOrder.append(", $periodOne - $periodTwo")
    }

    //Калбэк для устовки даты
    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        if (monthOfYear + 1 > 10) "$dayOfMonth.${monthOfYear + 1}.$year".also {
            binding.tvTimeOrder.text = it
        } else "$dayOfMonth.0${monthOfYear + 1}.$year".also { binding.tvTimeOrder.text = it }
    }
}