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
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonObject
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import ru.iwater.youwater.R
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.*
import ru.iwater.youwater.databinding.FragmentCreateOrderBinding
import ru.iwater.youwater.screen.adapters.OrderProductAdapter
import ru.iwater.youwater.screen.dialog.AddAddressDialog
import ru.iwater.youwater.screen.dialog.AddNoticeDialog
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Фрагмент оформления заказа
 */
class CreateOrderFragment : BaseFragment(),
    DatePickerDialog.OnDateSetListener, AddNoticeDialog.AddNoticeDialogListener, AddAddressDialog.ChoiceAddressDialog {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    val viewModel: OrderViewModel by viewModels { factory }

    private val productClear = mutableListOf<Product>()
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
        warningPay(isShowMessage)

        binding.lifecycleOwner = this
        /**
         * информация о клиенте
         */
        viewModel.client.observe(viewLifecycleOwner) {
            binding.tvNameClient.text = it.name
            binding.tvTelNumber.text = it.contact
            order.clientId = it.client_id
            order.name = it.name
            order.contact = it.contact
            if (it.email.isNotEmpty()) order.email = it.email
        }

        viewModel.getInfoLastOrder(lastOrder)

        // выбор адреса доставки
        viewModel.rawAddress.observe(viewLifecycleOwner) { listRawAddress ->
            if (!listRawAddress.isNullOrEmpty()) {
                for (rawAddress in listRawAddress) {
                    Timber.d("Note in address ${rawAddress.id} ${rawAddress.notice}")
                }
                binding.btnSetAddress.text = "Выбрать адрес доставки"
                val listAddress = mutableListOf<Address>()
                val addresses = mutableListOf<String>()
                listRawAddress.forEach { rawAddress ->
                    val region = rawAddress.fullAddress.split(",")[0]
                    addresses.add(rawAddress.factAddress)
                    listAddress.add(viewModel.getAddressFromString(rawAddress.factAddress.split(","), region, rawAddress.id, rawAddress.notice))
                }
                binding.btnSetAddress.setOnClickListener {
                    listAddress.forEach {
                        Timber.d("List Address ${it.note}")
                    }
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

        //выбор даты заказа
        binding.btnSetTime.text = "Укажите дату"
        binding.btnSetTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            if (order.date.isNotEmpty()) {
                val year = order.date.split('-')[0].toInt()
                val month = order.date.split('-')[1].toInt() - 1
                val day = order.date.split('-')[2].toInt()
                Timber.d("DATE = $year, $month, $day")
                calendar.set(year, month, day)
                getCalendar(calendar)
            } else {
                getCalendar(calendar)
            }
        }
        // детали заказа
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
            Timber.d("PERIOD ${order.period}")
            if (binding.btnSetAddress.text != "Выбрать адрес доставки" &&
                binding.btnSetAddress.text != "Адрес устарел, выберете другой" &&
                binding.btnSetTime.text != "Укажите дату" &&
                order.period.isNotEmpty() &&
                order.period != "**:**-**:**" &&
                order.paymentType == "4") {
                viewModel.sendAndSaveOrder(order)
                createOrder(viewModel)


            }

            else if (binding.btnSetAddress.text != "Выбрать адрес доставки" &&
                binding.btnSetAddress.text != "Адрес устарел, выберете другой" &&
                binding.btnSetTime.text != "Укажите дату" &&
                order.period.isNotEmpty() &&
                order.period != "**:**-**:**" &&
                order.paymentType == "0") {
                viewModel.sendAndSaveOrder(order)
                createOrder(viewModel)
            }

            else if (binding.btnSetAddress.text != "Выбрать адрес доставки" &&
                binding.btnSetAddress.text != "Адрес устарел, выберете другой" &&
                binding.btnSetTime.text != "Укажите дату" &&
                order.period.isNotEmpty() &&
                order.period != "**:**-**:**" &&
                order.paymentType == "2") {
                viewModel.sendAndSaveOrder(order)
                viewModel.statusOrder.observe(this.viewLifecycleOwner) { status ->
                    when(status) {
                        Status.SEND -> {
                            viewModel.numberOrder.observe(this.viewLifecycleOwner) { numberOrder ->
                                viewModel.payToCard(numberOrder, amount = order.orderCost * 100, order.contact)//*100
                                viewModel.dataPayment.observe(this.viewLifecycleOwner) { dataPayment ->
//                                    Timber.d("SBER LINK: ${dataPayment[0]}; ${dataPayment[1]}")
                                    val orderId = dataPayment[0].removePrefix("\"").removeSuffix("\"")
                                    val url = dataPayment[1].removePrefix("\"").removeSuffix("\"")
                                    this.findNavController().navigate(CreateOrderFragmentDirections.actionCreateOrderFragmentToCardPaymentFragment(url, orderId))
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
        if (id != null) {
            order.addressId = id
        }
        if (notice != null) {
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
        val todayTimes = mutableListOf(
            "17:00-22:00",
            "19:00-22:00",
            "**:**-**:**"
        )
        val saturdayTimes = mutableListOf(
            "09:00-16:00",
            "**:**-**:**"
        )
        val otherTimes = mutableListOf(
            "09:00-16:00",
            "17:00-22:00",
            "19:00-22:00",
            "**:**-**:**"
        )
        if (monthOfYear + 1 >= 10) "$dayOfMonth.${monthOfYear + 1}.$year".also {
            setTimeOrderText(it)
            choiceDateAndTime(year, monthOfYear, dayOfMonth, todayTimes, saturdayTimes, otherTimes)
        } else "$dayOfMonth.0${monthOfYear + 1}.$year".also {
            setTimeOrderText(it)
            choiceDateAndTime(year, monthOfYear, dayOfMonth, todayTimes, saturdayTimes, otherTimes)
        }
    }

    private fun getCalendar(calendar: Calendar) {
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
        maxDate.set(Calendar.YEAR, year + 2)
        datePickerDialog.maxDate = maxDate

        disableSunday(minDate, maxDate, datePickerDialog)

        datePickerDialog.firstDayOfWeek = Calendar.MONDAY
        datePickerDialog.show(parentFragmentManager, "SetDateDialog")
    }

    // выбор даты и времени доставки
    private fun choiceDateAndTime(year: Int, monthOfYear: Int, dayOfMonth: Int, todayTimes: MutableList<String>, saturdayTimes: MutableList<String>, otherTimes: MutableList<String>) {
        val calendar = Calendar.getInstance()
        //дата заказа
        calendar.set(year, monthOfYear, dayOfMonth)
        order.date = SimpleDateFormat("yyyy-MM-dd").format(Date(calendar.timeInMillis))
        // дата заказа, день.месяц
        val dayOrder = SimpleDateFormat("dd.MM").format(Date(calendar.timeInMillis))
        // день недели
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val calendarNow = Calendar.getInstance().timeInMillis / 1000
        // сегодняшняя дата, день.месяц
        val dayNow = SimpleDateFormat("dd.MM").format(Date(calendarNow * 1000))
        Timber.d("DATE ===== $dayNow, $dayOrder")
        // заказ на сегодня
        if (dayNow == dayOrder) {
            val spinnerBeforeTimeAdapter = ArrayAdapter(this.requireContext(),
                R.layout.spinner_item_layout_resource,
                R.id.TextView,
                todayTimes
            )
            setPeriodTime(spinnerBeforeTimeAdapter, todayTimes, todayTimes.size - 1)
        }
        //заказ на субботу
        else if (dayOfWeek == Calendar.SATURDAY){
            val spinnerBeforeTimeAdapter = ArrayAdapter(this.requireContext(),
                R.layout.spinner_item_layout_resource,
                R.id.TextView,
                saturdayTimes
            )
            setPeriodTime(spinnerBeforeTimeAdapter, saturdayTimes, saturdayTimes.size - 1)
        }
        //заказ на другой день
        else {
            val spinnerBeforeTimeAdapter = ArrayAdapter(this.requireContext(),
                R.layout.spinner_item_layout_resource,
                R.id.TextView,
                otherTimes
            )
            setPeriodTime(spinnerBeforeTimeAdapter, otherTimes, otherTimes.size - 1)
        }
    }

    //Отключить все ВОСКРЕСЕНЬЯ между минимальной и максимальной датами
    private fun disableSunday(minDate: Calendar, maxDate: Calendar, datePickerDialog: DatePickerDialog) {
        var loopDate = minDate
        while (minDate.before(maxDate)) {
            val dayOfWeek = loopDate[Calendar.DAY_OF_WEEK]
            if (dayOfWeek == Calendar.SUNDAY) {
                val disabledDays = arrayOfNulls<Calendar>(1)
                disabledDays[0] = loopDate
                datePickerDialog.disabledDays = disabledDays
            }
            minDate.add(Calendar.DATE, 1)
            loopDate = minDate
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