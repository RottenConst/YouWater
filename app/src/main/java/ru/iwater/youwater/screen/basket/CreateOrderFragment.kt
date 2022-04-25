package ru.iwater.youwater.screen.basket

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonObject
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import ru.iwater.youwater.R
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.*
import ru.iwater.youwater.databinding.FragmentCreateOrderBinding
import ru.iwater.youwater.screen.adapters.OrderProductAdapter
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Фрагмент оформления заказа
 */
class CreateOrderFragment : BaseFragment(),
    DatePickerDialog.OnDateSetListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    val viewModel: OrderViewModel by viewModels { factory }

    private val productClear = mutableListOf<Product>()
    private var periodOne = ""
    private var periodTwo = ""
    private var dayOrder = ""
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
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding.lifecycleOwner = this
        viewModel.client.observe(viewLifecycleOwner) {
            binding.tvNameClient.text = it.name
            binding.tvTelNumber.text = it.phone
            order.clientId = it.client_id
            order.name = it.name
            order.contact = it.phone
        }
        viewModel.address.observe(viewLifecycleOwner) { listAddress ->
            if (!listAddress.isNullOrEmpty()) {
                binding.tvAddressOrder.text = "Выбрать адрес доставки"
                val addresses = mutableListOf<String>()
                listAddress.forEach { address ->
                    when {
                        address.building.isNullOrEmpty() -> {
                            if (address.entrance == null && address.floor == null && address.flat == null) {
                                addresses.add("${address.region} ул.${address.street} д.${address.house}")
                            } else if (address.entrance == null && address.floor == null && address.flat != null) {
                                addresses.add("${address.region} ул.${address.street} д.${address.house} кв.${address.flat}")
                            } else if (address.entrance == null && address.floor != null && address.flat == null){
                                addresses.add("${address.region} ул.${address.street} д.${address.house} этаж${address.floor}")
                            } else if (address.entrance != null && address.floor == null && address.flat == null) {
                                addresses.add("${address.region} ул.${address.street} д.${address.house} подьезд ${address.entrance}")
                            } else if (address.entrance == null && address.floor != null && address.flat != null) {
                                addresses.add("${address.region} ул.${address.street} д.${address.house} этаж${address.floor} кв.${address.flat}")
                            } else if (address.entrance != null && address.floor == null && address.flat != null) {
                                addresses.add("${address.region} ул.${address.street} д.${address.house} подьезд ${address.entrance} кв.${address.flat}")
                            }
                        }
                        address.entrance == null -> {
                            if (address.building.isNullOrEmpty() && address.floor == null && address.flat == null) {
                                addresses.add("${address.region} ул.${address.street} д.${address.house}")
                            } else if (address.building.isNullOrEmpty() && address.floor == null && address.flat != null) {
                                addresses.add("${address.region} ул.${address.street} д.${address.house} кв.${address.flat}")
                            } else if (address.building.isNullOrEmpty() && address.floor != null && address.flat == null) {
                                addresses.add("${address.region} ул.${address.street} д.${address.house} этаж${address.floor}")
                            } else if (!address.building.isNullOrEmpty() && address.floor == null && address.flat == null) {
                                addresses.add("${address.region} ул.${address.street} д.${address.house} ст. ${address.building}")
                            } else if (!address.building.isNullOrEmpty() && address.floor != null && address.flat == null) {
                                addresses.add("${address.region} ул.${address.street} д.${address.house} ст. ${address.building} этаж${address.floor}")
                            } else if (!address.building.isNullOrEmpty() && address.floor == null && address.flat != null) {
                                addresses.add("${address.region} ул.${address.street} д.${address.house} ст. ${address.building} кв. ${address.flat}")
                            }

                        }
                        address.floor == null -> {
                            if (address.flat == null && address.building.isNullOrEmpty()) {
                                addresses.add("${address.region} ул.${address.street} д.${address.house} подьезд ${address.entrance}")
                            } else if (address.flat == null && !address.building.isNullOrEmpty()){
                                addresses.add("${address.region} ул.${address.street} д.${address.house} ст. ${address.building} подьезд ${address.entrance}")
                            } else if (address.flat != null && address.building.isNullOrEmpty()) {
                                addresses.add("${address.region} ул.${address.street} д.${address.house} подьезд ${address.entrance} кв.${address.flat}")
                            } else if (address.flat != null && !address.building.isNullOrEmpty()) {
                                addresses.add("${address.region} ул.${address.street} д.${address.house} ст. ${address.building} подьезд ${address.entrance} кв.${address.flat}")
                            }
                        }
                        address.flat == null -> addresses.add("${address.region} ул.${address.street} д.${address.house} подьезд ${address.entrance} этаж${address.floor}")
                        else -> addresses.add("${address.region} ул.${address.street} д.${address.house} cт.${address.building} подьезд ${address.entrance} этаж${address.floor} кв.${address.flat}")
                    }
                }
                binding.tvAddressOrder.setOnClickListener {
                    AlertDialog.Builder(this.requireContext())
                        .setSingleChoiceItems(addresses.toTypedArray(), -1) { dialog, witch ->
                            binding.tvAddressOrder.text = addresses[witch]
                            order.addressJson.apply {
                                addProperty("region", listAddress[witch].region)
                                addProperty("street", listAddress[witch].street)
                                addProperty("house", listAddress[witch].house)
                                addProperty("building", listAddress[witch].building)
                                addProperty("entrance", listAddress[witch].entrance)
                                addProperty("floor", listAddress[witch].floor)
                                addProperty("flat", listAddress[witch].flat)
                            }
                            addressString = addresses[witch]
                            dialog.cancel()
                        }
                        .create().show()
                }
            } else {
                binding.tvAddressOrder.text = "Добавить адрес"
                binding.tvAddressOrder.setOnClickListener {
                    viewModel.getAllFactAddress(this.context)
                }
            }
        }
        binding.tvTimeOrder.text = "Укажите дату"
        binding.tvTimeOrder.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            //Установить дату
            val datePickerDialog = DatePickerDialog.newInstance(this, year, month, day)
            datePickerDialog.setTitle("Укажите время заказа")
            if (hour >= 15) {
                calendar.set(year, month, day + 1)
            }
            datePickerDialog.minDate = calendar
            datePickerDialog.show(parentFragmentManager, "SetDateDialog")
        }
        val adapterOrder = OrderProductAdapter()
        val product = mutableListOf<Product>()
        binding.rvOrderProduct.adapter = adapterOrder
        viewModel.products.observe(viewLifecycleOwner) { products ->
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
            order.orderCost = priceTotalDiscount
        }
        val context = this.context
        if (context != null) {
            val typesOfPay = mutableListOf(
                "Оплата по карте курьеру",
                "Оплата наличными",
                "Оплата по карте",
                "Выберите способ оплаты",
            )

            val spinnerAdapter =
                ArrayAdapter(context,
                    R.layout.spinner_item_layout_resource,
                    R.id.TextView,
                    typesOfPay)

            binding.spinnerPaymentType.adapter = spinnerAdapter
            binding.spinnerPaymentType.setSelection(3)
            //выбор способа оплаты
            val itemTypePeySelectedListener: AdapterView.OnItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long,
                    ) {
                        order.paymentType = parent?.getItemAtPosition(position).toString()
                        if (order.paymentType == "Оплата по карте") binding.btnCreateOrder.text = "Перейти к оплате"
                        typesOfPay.remove("Выберите способ оплаты")
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
            binding.spinnerPaymentType.onItemSelectedListener = itemTypePeySelectedListener
            if (binding.spinnerPaymentType.isFocused) {
                spinnerAdapter.notifyDataSetChanged()
            }
        }
        binding.btnCreateOrder.setOnClickListener {
            order.period = "$periodOne - $periodTwo"
            Timber.d("PERIOD ${order.period}")
            if (binding.tvAddressOrder.text != "Выбрать адрес доставки" &&
                binding.tvTimeOrder.text != "Укажите дату" &&
                periodOne != "--:--" &&
                periodTwo != "--:--" &&
                order.paymentType == "Оплата по карте курьеру") {
                viewModel.sendAndSaveOrder(order, product, addressString)
                createOrder(viewModel)


            }

            else if (binding.tvAddressOrder.text != "Выбрать адрес доставки" &&
                binding.tvTimeOrder.text != "Укажите дату" &&
                periodOne != "--:--" &&
                periodTwo != "--:--" &&
                order.paymentType == "Оплата наличными") {
                viewModel.sendAndSaveOrder(order, product, addressString)
                createOrder(viewModel)
            }

            else if (binding.tvAddressOrder.text != "Выбрать адрес доставки" &&
                binding.tvTimeOrder.text != "Укажите дату" &&
                periodOne != "--:--" &&
                periodTwo != "--:--" &&
                order.paymentType == "Оплата по карте") {
                viewModel.sendAndSaveOrder(order, product, addressString)
                viewModel.statusOrder.observe(this.viewLifecycleOwner) { status ->
                    when(status) {
                        Status.SEND -> {
                            viewModel.numberOrder.observe(this.viewLifecycleOwner) { numberOrder ->
                                viewModel.payToCard(numberOrder, amount = order.orderCost * 100, order.contact)
                                viewModel.clearProduct(productClear)
                                viewModel.dataPayment.observe(this.viewLifecycleOwner) { dataPayment ->
                                    Timber.d("SBER LINK: ${dataPayment[0]}; ${dataPayment[1]}")
                                    val orderId = dataPayment[0].removePrefix("\"").removeSuffix("\"")
                                    val url = dataPayment[1].removePrefix("\"").removeSuffix("\"")
                                    this.findNavController().navigate(CreateOrderFragmentDirections.actionCreateOrderFragmentToCardPaymentFragment(url, orderId))
                                }
                            }
                        }
                        else -> {
                            Toast.makeText(this.context,
                                "Ошибка, возвожно проблемы с интернетом",
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Timber.d("${order.period}, ${order.paymentType}")
                Toast.makeText(this.context, "Укажите время и тип оплаты", Toast.LENGTH_LONG).show()
            }
        }
        return binding.root
    }

    private fun createOrder(viewModel: OrderViewModel) {
        viewModel.statusOrder.observe(this.viewLifecycleOwner) { status ->
            when (status) {
                Status.SEND -> {
                    viewModel.numberOrder.observe(this.viewLifecycleOwner) { numberOrder ->
                        viewModel.clearProduct(productClear)
                        Toast.makeText(this.context, "Заявка отправлена", Toast.LENGTH_LONG)
                            .show()
                        this.findNavController().navigate(
                            CreateOrderFragmentDirections.actionCreateOrderFragmentToCompleteOrderFragment(numberOrder, false)
                        )
                    }
                }
                else -> {
                    Toast.makeText(this.context,
                        "Ошибка, возвожно проблемы с интернетом",
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setPeriodTime(
        spinnerAdapterBefore: ArrayAdapter<String>,
        spinnerAdapterAfter: ArrayAdapter<String>,
        beforeTimeArray: MutableList<String>,
        countBefore: Int,
        afterTimeArray: MutableList<String>,
        countAfter: Int
        ) {
        binding.beforeTimeSpinner.adapter = spinnerAdapterBefore
        binding.beforeTimeSpinner.setSelection(countBefore)
        val itemTimeBeforeSelectListener: AdapterView.OnItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    periodOne = parent?.getItemAtPosition(position).toString()
                    beforeTimeArray.remove("--:--")

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        binding.beforeTimeSpinner.onItemSelectedListener = itemTimeBeforeSelectListener
        if (binding.beforeTimeSpinner.isFocused) spinnerAdapterBefore.notifyDataSetChanged()

        //установить время посде
        binding.afterTimeSpinner.adapter = spinnerAdapterAfter
        binding.afterTimeSpinner.setSelection(countAfter)
        val itemAfterSelectedListener: AdapterView.OnItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    periodTwo = parent?.getItemAtPosition(position).toString()
                    afterTimeArray.remove("--:--")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        binding.afterTimeSpinner.onItemSelectedListener = itemAfterSelectedListener
        if (binding.afterTimeSpinner.isFocused) {
            spinnerAdapterAfter.notifyDataSetChanged()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CreateOrderFragment()
    }

    //Калбэк для устовки даты
    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        if (monthOfYear + 1 > 10) "$dayOfMonth.${monthOfYear + 1}.$year".also {
            val calendar = Calendar.getInstance()
            calendar.set(year, monthOfYear, dayOfMonth)
            order.date = "${calendar.timeInMillis / 1000}"
            binding.tvTimeOrder.text = it
            binding.tvTimeSetLogo.visibility = View.VISIBLE
            binding.cvTimeSet.visibility = View.VISIBLE
            dayOrder = SimpleDateFormat("dd.MM").format(Date(calendar.timeInMillis))
            val calendarNow = Calendar.getInstance().timeInMillis / 1000
            val dayNow = SimpleDateFormat("dd.MM").format(Date(calendarNow * 1000))
            Timber.d("DATE ===== $dayNow, $dayOrder")
            when (dayNow) {
                dayOrder -> {
                    val beforeTimeArray = mutableListOf(
                        "9:00",
                        "10:00",
                        "11:00",
                        "--:--",
                    )
                    val afterTimeArray = mutableListOf(
                        "15:00",
                        "--:--",
                    )
                    val spinnerBeforeTimeAdapter = ArrayAdapter(this.requireContext(),
                        R.layout.spinner_item_layout_resource,
                        R.id.TextView,
                        beforeTimeArray
                    )
                    val spinnerAfterTimeAdapter = ArrayAdapter(this.requireContext(),
                        R.layout.spinner_item_layout_resource,
                        R.id.TextView,
                        afterTimeArray
                    )
                    setPeriodTime(spinnerBeforeTimeAdapter, spinnerAfterTimeAdapter, beforeTimeArray, 3, afterTimeArray, 1)
                }
                else -> {
                    val beforeTimeArray = mutableListOf(
                        "9:00",
                        "10:00",
                        "11:00",
                        "17:00",
                        "19:00",
                        "--:--",
                    )
                    val afterTimeArray = mutableListOf(
                        "15:00",
                        "16:00",
                        "17:00",
                        "22:00",
                        "--:--",
                    )
                    val spinnerBeforeTimeAdapter = ArrayAdapter(this.requireContext(),
                        R.layout.spinner_item_layout_resource,
                        R.id.TextView,
                        beforeTimeArray
                    )
                    val spinnerAfterTimeAdapter = ArrayAdapter(this.requireContext(),
                        R.layout.spinner_item_layout_resource,
                        R.id.TextView,
                        afterTimeArray
                    )
                    setPeriodTime(spinnerBeforeTimeAdapter, spinnerAfterTimeAdapter, beforeTimeArray, 5, afterTimeArray, 4)
                }
            }
            Timber.d("ORDER DATE = ${dayOrder}")
        } else "$dayOfMonth.0${monthOfYear + 1}.$year".also {
            val calendar = Calendar.getInstance()
            calendar.set(year, monthOfYear, dayOfMonth)
            order.date = "${calendar.timeInMillis / 1000}"
            binding.tvTimeOrder.text = it
            binding.tvTimeLogo.visibility = View.VISIBLE
            binding.cvTimeSet.visibility = View.VISIBLE
            dayOrder = SimpleDateFormat("dd.MM").format(Date(calendar.timeInMillis))
            val calendarNow = Calendar.getInstance().timeInMillis / 1000
            val dayNow = SimpleDateFormat("dd.MM").format(Date(calendarNow * 1000))
            Timber.d("DATE ===== $dayNow, $dayOrder")
            when (dayNow) {
                dayOrder -> {
                    val beforeTimeArray = mutableListOf(
                        "9:00",
                        "10:00",
                        "11:00",
                        "--:--",
                    )
                    val afterTimeArray = mutableListOf(
                        "15:00",
                        "--:--",
                    )
                    val spinnerBeforeTimeAdapter = ArrayAdapter(this.requireContext(),
                        R.layout.spinner_item_layout_resource,
                        R.id.TextView,
                        beforeTimeArray
                    )
                    val spinnerAfterTimeAdapter = ArrayAdapter(this.requireContext(),
                        R.layout.spinner_item_layout_resource,
                        R.id.TextView,
                        afterTimeArray
                    )
                    setPeriodTime(spinnerBeforeTimeAdapter, spinnerAfterTimeAdapter, beforeTimeArray, 3, afterTimeArray, 1)
                }
                else -> {
                    val beforeTimeArray = mutableListOf(
                        "9:00",
                        "10:00",
                        "11:00",
                        "17:00",
                        "19:00",
                        "--:--",
                    )
                    val afterTimeArray = mutableListOf(
                        "15:00",
                        "16:00",
                        "17:00",
                        "22:00",
                        "--:--",
                    )
                    val spinnerBeforeTimeAdapter = ArrayAdapter(this.requireContext(),
                        R.layout.spinner_item_layout_resource,
                        R.id.TextView,
                        beforeTimeArray
                    )
                    val spinnerAfterTimeAdapter = ArrayAdapter(this.requireContext(),
                        R.layout.spinner_item_layout_resource,
                        R.id.TextView,
                        afterTimeArray
                    )
                    setPeriodTime(spinnerBeforeTimeAdapter, spinnerAfterTimeAdapter, beforeTimeArray, 5, afterTimeArray, 4)
                }
            }
        }
    }
}