package ru.iwater.youwater.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import ru.iwater.youwater.base.BaseActivity
import ru.iwater.youwater.screen.navigation.PaymentScreen
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.PaymentViewModel
import ru.iwater.youwater.vm.PaymentViewModelFactory
import ru.yoomoney.sdk.kassa.payments.Checkout
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.Amount
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentMethodType
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentParameters
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.SavePaymentMethod
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.UiParameters
import ru.yoomoney.sdk.kassa.payments.ui.color.ColorScheme
import timber.log.Timber
import java.math.BigDecimal
import java.util.Currency

class PaymentActivity : BaseActivity() {

    private val viewModel: PaymentViewModel by viewModels { PaymentViewModelFactory() }
    private lateinit var amount: String
    private lateinit var idOrder: String
    private var tel = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("Activity on create")
        val payData = intent.extras
        if (payData != null) {
            Timber.d("payment status ")
            val orderId = payData.getInt("orderId")
            val orderCost = payData.getInt("orderCost")
            val telNum = payData.getString("tel", "")
            viewModel.setCompleteOrderId(orderId)
            amount = orderCost.toString()
            idOrder = orderId.toString()
            tel = telNum
            setContent {
                YourWaterTheme {
                    PaymentScreen(orderId = orderId, watterViewModel = viewModel)
                }
            }
            onTokenizeButtonCLick(orderId, orderCost, tel)
        } else {
            viewModel.errorPay()
            setContent {
                YourWaterTheme {
                    PaymentScreen(watterViewModel = viewModel, orderId = 0)
                }
            }
        }


    }
    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_TOKENIZE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    if (data != null) {
                        val token = Checkout.createTokenizationResult(data).paymentToken
                        Timber.d("Token $token amount: $amount description $idOrder")
                        viewModel.createPay(idOrder.toInt(), amount, "Заказ №$idOrder", token, true)
                    }
                }
                Activity.RESULT_CANCELED -> viewModel.errorPay()
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun onTokenizeButtonCLick(orderId: Int, orderCost: Int, tel:String) {
        val paymentMethodTypes = setOf(
            PaymentMethodType.BANK_CARD
        )
        Timber.d("Order cost = $orderCost")
        val paymentParameters = PaymentParameters (
            amount = Amount(BigDecimal.valueOf(orderCost.toLong()), Currency.getInstance("RUB")),
            title = "Заказ №$orderId",
            subtitle = "Description",
            clientApplicationKey = sdkKey,
            shopId = shopId,
            savePaymentMethod = SavePaymentMethod.OFF,
            paymentMethodTypes = paymentMethodTypes,
            customReturnUrl = "https://yourwater.ru/",
            userPhoneNumber = tel
        )
        val uiParameters = UiParameters(
            colorScheme = ColorScheme(Color.rgb(60, 136, 240))
        )
        amount = orderCost.toString()
        idOrder = orderId.toString()

        val intent = Checkout.createTokenizeIntent(
            context = this,
            paymentParameters = paymentParameters,
            uiParameters = uiParameters
        )
        startActivityForResult(intent, REQUEST_CODE_TOKENIZE)
    }

    companion object {
        const val REQUEST_CODE_TOKENIZE = 1

        const val shopId = "684191"//"225217"//"684191"
        const val sdkKey = "test_MjI2MTA4MxLB9siXJQUoFkTR2ahVwsFFtNDAzEXf-XY"//"test_MjI2MTA4MxLB9siXJQUoFkTR2ahVwsFFtNDAzEXf-XY"


//        const val shopId = "248648"
//        const val sdkKey = "live_MjQ4NjQ4c9hBQMlOgjz-KHodsqsNf-td6vsQn6JIjTo"

        fun start(context: Context?) {
            if (context != null) {
                ContextCompat.startActivity(
                    context, Intent(context, PaymentActivity::class.java),
                    null
                )
            }
        }

        fun startGenerateToken(context: Context?, orderId: Int, orderCost: Int, tel: String) {
            if (context != null) {
                val options = Bundle()
                options.putInt("orderId", orderId)
                options.putInt("orderCost", orderCost)
                options.putString("tel", tel)
                val intent = Intent(context, PaymentActivity::class.java)
                intent.putExtras(options)
                ContextCompat.startActivity(
                    context, intent,
                    options
                )
            }
        }

    }
}