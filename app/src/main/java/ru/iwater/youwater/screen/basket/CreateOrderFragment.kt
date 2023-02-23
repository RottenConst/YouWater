package ru.iwater.youwater.screen.basket

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonObject
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.coroutines.launch
import ru.iwater.youwater.R
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.*
import ru.iwater.youwater.databinding.FragmentCreateOrderBinding
import ru.iwater.youwater.screen.adapters.AdapterBasketList
import ru.iwater.youwater.screen.adapters.OrderProductAdapter
import ru.iwater.youwater.screen.dialog.AddAddressDialog
import ru.iwater.youwater.screen.dialog.AddNoticeDialog
import ru.iwater.youwater.vm.OrderViewModel
import ru.iwater.youwater.vm.Status
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Фрагмент оформления заказа
 */
class CreateOrderFragment : BaseFragment(),
    DatePickerDialog.OnDateSetListener,
    AddNoticeDialog.AddNoticeDialogListener,
    AddAddressDialog.ChoiceAddressDialog,
    AdapterBasketList.OnProductItemListener{

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    val viewModel: OrderViewModel by viewModels { factory }
    private val adapterOrder = OrderProductAdapter(this)

    private val productClear = mutableListOf<Product>()
    private val deliveryTime = mutableListOf<Common>()
    private val exceptionTime = mutableListOf<Exception>()
    private var order =
        Order(
            clientId = 0,
            acqOrderId = 0,
            notice = "",
            waterEquip = mutableListOf(),
            period = "",
            orderCost = 0,
            paymentType = "",
            status = 0,
            email = "",
            contact = "",
            date = "",
            addressId = 0,
            name = "",
            dateCreate =  java.sql.Date(Calendar.getInstance().timeInMillis)
        )

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
        // показывать сообщение о неудачной оплате:
        // true - да, false - нет
        val isShowMessage = CreateOrderFragmentArgs.fromBundle(this.requireArguments()).isShowMessage
        val lastOrder = CreateOrderFragmentArgs.fromBundle(this.requireArguments()).lastOrderId
        val calendar = Calendar.getInstance()
        warningPay(isShowMessage)

        binding.lifecycleOwner = this
        /**
         * информация о клиенте
         */
        viewModel.client.observe(viewLifecycleOwner) { client ->
            if (client != null) {
                binding.tvNameClient.text = client.name
                binding.tvTelNumber.text = client.contact
                order.clientId = client.client_id
                order.name = client.name
                order.contact = client.contact
                if (client.email.isNotEmpty()) order.email = client.email
            }
        }

        viewModel.getInfoLastOrder(lastOrder, findNavController())

        // выбор адреса доставки
        viewModel.rawAddress.observe(viewLifecycleOwner) { listRawAddress ->
            if (!listRawAddress.isNullOrEmpty()) {
                binding.btnSetAddress.text = "Выбрать адрес доставки"
                val listAddress = mutableListOf<Address>()
                val addresses = mutableListOf<String>()
                listRawAddress.forEach { rawAddress ->
                    val region = rawAddress.region ?: rawAddress.fullAddress.split(",")[0]
                    addresses.add(rawAddress.factAddress)
                    listAddress.add(viewModel.getAddressFromString(rawAddress.factAddress.split(","), region, rawAddress.id, rawAddress.notice))
                }
                binding.btnSetAddress.setOnClickListener {
                    AddAddressDialog.getAddressDialog(childFragmentManager, listAddress, addresses)
                }
            } else {
                // если адресов нету переводим на страницу создания адреса
                binding.btnSetAddress.text = "Добавить адрес"
//                binding.tvAddressOrder.setBackgroundColor(Color.WHITE)
                binding.btnSetAddress.setOnClickListener {
                    this.findNavController().navigate(
                        CreateOrderFragmentDirections.actionCreateOrderFragmentToAddAddressFragment(true)
                    )
                }

            }
        }

        viewModel.deliverySchedule.observe(viewLifecycleOwner) {delivery ->
            if (delivery != null) {
                deliveryTime.addAll(
                    delivery.common.filter { it.available }
                )
                exceptionTime.addAll(
                    delivery.exceptions.filter { it.available }
                )
                binding.btnSetTime.setOnClickListener {
                    getCalendar(calendar, delivery.common.filter { !it.available }, delivery.exceptions)
                }
                binding.btnSetTime.visibility = View.VISIBLE
            } else {
                binding.btnSetTime.visibility = View.GONE
                Toast.makeText(this.context, "Не удается получить график доставки к этому адресу", Toast.LENGTH_SHORT).show()
            }
        }

        //выбор даты заказа
        binding.btnSetTime.text = "Укажите дату"

        // детали заказа
        binding.rvOrderProduct.adapter = adapterOrder
        viewModel.products.observe(viewLifecycleOwner) { products ->
            adapterOrder.submitList(products)
            productClear.addAll(products)
            order.waterEquip.clear()
            var priceTotal = 0
            var priceCompleteDiscount = 0
            var priceComplete = 0
            var priceTotalDiscount = 0
            var discount = 0
            var price = 0
            products.forEach { product ->
                val prices = product.price.removeSuffix(";")
                val priceList = prices.split(";")
                val count = product.count
                if (product.id == 81 || product.id == 84) {
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
                val productJs = getJsonProduct(product, priceOne)
                order.waterEquip.add(productJs)
                discount = 0
                priceTotal += price
                priceTotalDiscount = priceCompleteDiscount + priceComplete
            }
            "${priceTotal}₽".also { binding.tvCostOrder.text = it }
            "${priceTotalDiscount}₽".also { binding.tvTotalSumCost.text = it }
            order.orderCost = priceTotalDiscount
        }
        //выбор оплаты
        val context = this.context
        if (context != null) {
            val typesOfPay = mutableListOf(
                "Оплата по карте курьеру",
                "Оплата наличными",
                "Оплата онлайн",
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
                        if (parent?.getItemAtPosition(position) == "Оплата по карте курьеру") order.paymentType = "4"
                        if (parent?.getItemAtPosition(position) == "Оплата наличными") order.paymentType = "0"
                        if (parent?.getItemAtPosition(position) == "Оплата онлайн") order.paymentType = "2"
                        binding.btnCreateOrder.text = if (order.paymentType == "2") "Перейти к оплате" else "Оформить заявку"
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

        binding.cvNoticeOrder.setOnClickListener {
            AddNoticeDialog.getAddNoticeDialog(childFragmentManager, order.notice)
        }

        viewModel.myOrder.observe(viewLifecycleOwner) { myOrder ->
            if (myOrder != null) {
                when (myOrder.payment_type) {
                    "0" -> binding.spinnerPaymentType.setSelection(1)
                    "2" -> binding.spinnerPaymentType.setSelection(2)
                    "4" -> binding.spinnerPaymentType.setSelection(0)
                    else -> {}
                }
                if (binding.btnSetAddress.text != "Адрес устарел, выберете другой") {
                    order.addressId = myOrder.address_id
                }
                if (myOrder.notice.isNotEmpty()) {
                    order.notice = myOrder.notice
                    binding.tvOrderNotice.text = myOrder.notice
                }
            }
        }

        viewModel.address.observe(viewLifecycleOwner) { address ->
            if (address != "Адрес устарел, выберете другой") {
                binding.btnSetAddress.text = address
            } else {
                binding.btnSetAddress.text = address
            }
        }

        binding.btnCreateOrder.setOnClickListener {
            if (binding.btnSetAddress.text != "Выбрать адрес доставки" &&
                binding.btnSetAddress.text != "Адрес устарел, выберете другой" &&
                binding.btnSetTime.text != "Укажите дату" &&
                order.period.isNotEmpty() &&
                order.period != "**:**-**:**" &&
                order.paymentType == "4") {
                binding.btnCreateOrder.isEnabled = false
                viewModel.sendAndSaveOrder(order)
                createOrder(viewModel)


            }

            else if (binding.btnSetAddress.text != "Выбрать адрес доставки" &&
                binding.btnSetAddress.text != "Адрес устарел, выберете другой" &&
                binding.btnSetTime.text != "Укажите дату" &&
                order.period.isNotEmpty() &&
                order.period != "**:**-**:**" &&
                order.paymentType == "0") {
                binding.btnCreateOrder.isEnabled = false
                viewModel.sendAndSaveOrder(order)
                createOrder(viewModel)
            }

            else if (binding.btnSetAddress.text != "Выбрать адрес доставки" &&
                binding.btnSetAddress.text != "Адрес устарел, выберете другой" &&
                binding.btnSetTime.text != "Укажите дату" &&
                order.period.isNotEmpty() &&
                order.period != "**:**-**:**" &&
                order.paymentType == "2") {
                binding.btnCreateOrder.isEnabled = false
                viewModel.sendAndSaveOrder(order)
                viewModel.statusOrder.observe(this.viewLifecycleOwner) { status ->
                    when(status) {
                        Status.SEND -> {
                            viewModel.numberOrder.observe(this.viewLifecycleOwner) { numberOrder ->
                                viewModel.payToCard(numberOrder, amount = order.orderCost * 100, order.contact)//*100
                                viewModel.dataPayment.observe(this.viewLifecycleOwner) { dataPayment ->
                                    if (dataPayment.isNotEmpty()) {
//                                        Timber.d("SBER LINK: ${dataPayment[0]}; ${dataPayment[1]}")
                                        val orderId =
                                            dataPayment[0].removePrefix("\"").removeSuffix("\"")
                                        val url =
                                            dataPayment[1].removePrefix("\"").removeSuffix("\"")
                                        this.findNavController().navigate(
                                            CreateOrderFragmentDirections.actionCreateOrderFragmentToCardPaymentFragment(
                                                url,
                                                orderId
                                            )
                                        )
                                    }
                                    else {
                                        Toast.makeText(
                                            this.context,
                                            "Ошибка подключения к сервису оплаты",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        binding.btnCreateOrder.isEnabled = true
                                    }
                                }
                            }
                        }
                        else -> {
                            warning("Ошибка, возвожно проблемы с интернетом")
                        }
                    }
                }
            } else {
                Timber.d("${order.period}, ${order.paymentType}")
                warning("Уточните основные данные: адрес, время, тип оплаты")
            }
        }
        return binding.root
    }

    // добавить примичание
    override fun onDialogAddNotice(inputNotice: String, dialog: DialogFragment) {
        if (TextUtils.isEmpty(inputNotice)){
            Toast.makeText(this.context, "Примичание не было добавлено", Toast.LENGTH_LONG).show()
            dialog.dismissNow()
        } else {
            order.notice = inputNotice
            binding.tvOrderNotice.text = inputNotice
            dialog.dismissNow()
        }
    }

    //выбрать адрес
    override fun choiceAddress(
        dialogFragment: DialogFragment,
        id: Int?,
        addressString: String?,
        notice: String?
    ) {
        binding.btnSetAddress.text = addressString
//        binding.btnSetTime.visibility = View.VISIBLE
        if (id != null) {
            order.addressId = id
            binding.btnSetTime.text = "Укажите дату"
            binding.tvTimeSetLogo.visibility = View.GONE
            binding.cvTimeSet.visibility = View.GONE
            deliveryTime.clear()
            exceptionTime.clear()
            viewModel.getDeliveryOnAddress(id)
        }
        if (!notice.isNullOrEmpty()) {
            binding.cvNoticeAddress.visibility = View.VISIBLE
            binding.tvAddressNotice.text = notice
        } else {
            binding.cvNoticeAddress.visibility = View.GONE
        }
        dialogFragment.dismissNow()
    }

    // отмена выбора адреса
    override fun cancelClick(dialogFragment: DialogFragment) {
        binding.btnSetAddress.text = "Выбрать адрес доставки"
        order.addressId = 0
        binding.btnSetTime.visibility = View.GONE
        binding.cvNoticeAddress.visibility = View.GONE
        dialogFragment.dismissNow()
    }

    // отмена добавления коментария
    override fun onDialogCancelNotice(dialog: DialogFragment) {
        dialog.dismissNow()
    }

    private fun createOrder(viewModel: OrderViewModel) {
        viewModel.statusOrder.observe(this.viewLifecycleOwner) { status ->
            when (status) {
                Status.SEND -> {
                    viewModel.numberOrder.observe(this.viewLifecycleOwner) { numberOrder ->
                            warning("Заявка отправлена")
                        this.findNavController().navigate(
                            CreateOrderFragmentDirections.actionCreateOrderFragmentToCompleteOrderFragment(numberOrder, false)
                        )
                    }
                }
                else -> {
                    warning("Ошибка, возвожно проблемы с интернетом")
                }
            }
        }
    }

    override fun addProduct(product: Product) {
        viewModel.viewModelScope.launch {
            if (product.category != 20) {
                viewModel.addProductCount(product)
                Timber.d("ITEM COUNT + ${adapterOrder.currentList.indexOf(product)}")
                adapterOrder.notifyItemChanged(adapterOrder.currentList.indexOf(product))
            } else {
                Toast.makeText(context, "Стартовый пакет можно заказать один", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun minusProduct(product: Product) {
        viewModel.viewModelScope.launch {
            viewModel.minusProductCount(product)
            adapterOrder.notifyItemChanged(adapterOrder.currentList.indexOf(product))
        }
    }

    override fun deleteProductClick(product: Product) {
    }

    //выбор периода доставки
    private fun setPeriodTime(
        spinnerAdapterBefore: ArrayAdapter<String>,
        beforeTimeArray: MutableList<String>,
        countBefore: Int
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
                    order.period = parent?.getItemAtPosition(position).toString()
                    beforeTimeArray.remove("**:**-**:**")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        binding.beforeTimeSpinner.onItemSelectedListener = itemTimeBeforeSelectListener
        if (binding.beforeTimeSpinner.isFocused) spinnerAdapterBefore.notifyDataSetChanged()
    }

    companion object {
        @JvmStatic
        fun newInstance() = CreateOrderFragment()
    }

    //Калбэк для устовки даты
    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        if (monthOfYear + 1 >= 10) "$dayOfMonth.${monthOfYear + 1}.$year".also {
            setTimeOrderText(it)
            choiceDateAndTime(year, monthOfYear, dayOfMonth)
        } else "$dayOfMonth.0${monthOfYear + 1}.$year".also {
            setTimeOrderText(it)
            choiceDateAndTime(year, monthOfYear, dayOfMonth)
        }
    }

    private fun getCalendar(calendar: Calendar, disabledDays: List<Common>, exceptions: List<Exception>) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        //Установить дату
        val datePickerDialog = DatePickerDialog.newInstance(this, year, month, day)
        datePickerDialog.setTitle("Укажите время заказа")

        val minDate = Calendar.getInstance()
        datePickerDialog.minDate = minDate

        if (hour >= 14) {
            val yearMin = minDate.get(Calendar.YEAR)
            val monthMin = minDate.get(Calendar.MONTH)
            val dayMin = minDate.get(Calendar.DAY_OF_MONTH)
            minDate.set(yearMin, monthMin, dayMin + 1)
            datePickerDialog.minDate = minDate
        }
        // установить мин дату на сегодня
        // Установить max дату year + 2
        val maxDate = Calendar.getInstance()
        maxDate.set(Calendar.DAY_OF_MONTH, day + 320)
        datePickerDialog.maxDate = maxDate

        disableOnDelivery(minDate, maxDate, disabledDays, exceptions, datePickerDialog)

        datePickerDialog.firstDayOfWeek = Calendar.MONDAY
        datePickerDialog.show(parentFragmentManager, "SetDateDialog")
    }

    // выбор даты и времени доставки
    private fun choiceDateAndTime(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        //дата заказа
        calendar.set(year, monthOfYear, dayOfMonth)
        val orderDate = calendar.get(Calendar.DAY_OF_WEEK) - 1
        order.date = SimpleDateFormat("yyyy-MM-dd", Locale("ru")).format(Date(calendar.timeInMillis))

        val exception = exceptionTime.filter { it.date == order.date }
        val commons = deliveryTime.filter { it.day_num == orderDate }
        val times =
        if (exception.isNotEmpty()) {
            addTime(exception[0].part_types)
        } else {
            Timber.d("Common ${commons.get(0).part_types.size}")
            commons[0].part_types.forEach {
                Timber.d("part_types = $it")
            }
            addTime(commons[0].part_types)
        }
        val spinnerBeforeTimeAdapter = ArrayAdapter(this.requireContext(),
            R.layout.spinner_item_layout_resource,
            R.id.TextView,
            times
        )
        setPeriodTime(spinnerBeforeTimeAdapter, times, times.size - 1)
    }

    private fun addTime(part_types: List<Int>): MutableList<String> {
        return when {
            part_types.size == 2 -> {
                arrayListOf("09:00-16:00", "17:00-22:00", "19:00-22:00", "**:**-**:**")
            }
            part_types[0] == 0 -> {
                arrayListOf("09:00-16:00", "**:**-**:**")
            }
            else -> {arrayListOf("17:00-22:00", "19:00-22:00", "**:**-**:**")}
        }
    }

    private fun disableDay(day: Int, dayOfWeek: Int, loopDate: Calendar, datePickerDialog: DatePickerDialog) {
        when(day) {
            1 -> if (dayOfWeek == Calendar.MONDAY) {
                    val disables = arrayOfNulls<Calendar>(1)
                    disables[0] = loopDate
                    datePickerDialog.disabledDays = disables
            }
            2 -> if (dayOfWeek == Calendar.TUESDAY) {
                    val disables = arrayOfNulls<Calendar>(1)
                    disables[0] = loopDate
                    datePickerDialog.disabledDays = disables
                }
            3 -> if (dayOfWeek == Calendar.WEDNESDAY) {
                    val disables = arrayOfNulls<Calendar>(1)
                    disables[0] = loopDate
                    datePickerDialog.disabledDays = disables
            }
            4 -> if (dayOfWeek == Calendar.THURSDAY) {
                    val disables = arrayOfNulls<Calendar>(1)
                    disables[0] = loopDate
                    datePickerDialog.disabledDays = disables
            }
            5 -> if (dayOfWeek == Calendar.FRIDAY) {
                    val disables = arrayOfNulls<Calendar>(1)
                    disables[0] = loopDate
                    datePickerDialog.disabledDays = disables
            }
            6 -> if (dayOfWeek == Calendar.SATURDAY) {
                    val disables = arrayOfNulls<Calendar>(1)
                    disables[0] = loopDate
                    datePickerDialog.disabledDays = disables
            }
            7 -> if (dayOfWeek == Calendar.SUNDAY) {
                    val disables = arrayOfNulls<Calendar>(1)
                    disables[0] = loopDate
                    datePickerDialog.disabledDays = disables
            }
        }
    }
    private fun disableOnDelivery(minDate: Calendar, maxDate: Calendar, disabledDays: List<Common>, exceptions: List<Exception>, datePickerDialog: DatePickerDialog) {
        var loopDate = minDate
        while (minDate.before(maxDate)) {
            val dayOfWeek = loopDate[Calendar.DAY_OF_WEEK]
            disabledDays.forEach { common ->
                disableDay(common.day_num, dayOfWeek, loopDate, datePickerDialog)
            }
            minDate.add(Calendar.DATE, 1)
            loopDate = minDate
        }
        datePickerDialog.disabledDays.forEach { calendar ->
            val date = SimpleDateFormat("yyyy-MM-dd", Locale("ru")).format(calendar.time)
            exceptions.forEach {
                if (it.date == date && it.available) {
                    calendar.clear()
                }
            }
        }
    }

    private fun setTimeOrderText(time: String) {
        binding.btnSetTime.text = time
        binding.tvTimeSetLogo.visibility = View.VISIBLE
        binding.cvTimeSet.visibility = View.VISIBLE
    }

    private fun getJsonProduct(product: Product, priceOne: Int): JsonObject {
        val productJs = JsonObject()
        productJs.addProperty("id", product.id)
        productJs.addProperty("name", product.name)
        productJs.addProperty("price", priceOne)
        productJs.addProperty("amount", product.count)
        return productJs
    }

    private fun warning(message: String) {
        Toast.makeText(this.context, message, Toast.LENGTH_LONG).show()
    }

    private fun warningPay(isShowMessage: Boolean) {
        if (isShowMessage) {
            warning("Извините оплатить заказ не удалось, попробуйте выбрать другой тип оплаты или отредактировать заказ")
        }
    }
}