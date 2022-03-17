package ru.iwater.youwater.screen.basket

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.OrderViewModel
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.databinding.FragmentCardPaymentBinding
import timber.log.Timber
import javax.inject.Inject


class CardPaymentFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    val viewModel: OrderViewModel by viewModels { factory }
    val binding: FragmentCardPaymentBinding by lazy { inflateBindingLazy(LayoutInflater.from(this.context)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val formUrl = CardPaymentFragmentArgs.fromBundle(this.requireArguments()).formUrl
        val orderId = CardPaymentFragmentArgs.fromBundle(this.requireArguments()).orderId
        val productClear = mutableListOf<Product>()
        viewModel.products.observe(this.viewLifecycleOwner) {
            productClear.addAll(it)
        }
        Timber.d("SBERLINK $formUrl, $orderId")
        binding.wvCardPayment.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                Timber.d("FINISH $url")
                super.onPageFinished(view, url)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                Timber.d("START $url")
                val endLink = "http://605d3ea8e59a.ngrok.io"
                val endUrl = url?.removeRange(endLink.lastIndex + 2, url.lastIndex + 1)
                Timber.d(endUrl)
                if (endUrl.contentEquals("http://605d3ea8e59a.ngrok.io/") || endUrl.contentEquals("https://605d3ea8e59a.ngrok.io")){
                    viewModel.getPaymentStatus(orderId)
                    binding.wvCardPayment.visibility = View.GONE
                    findNavController().navigate(CardPaymentFragmentDirections.actionCardPaymentFragmentToCompleteOrderFragment(orderId))
                }
                super.onPageStarted(view, url, favicon)
            }
        }
        binding.wvCardPayment.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(message: ConsoleMessage): Boolean {
                Timber.d("${message.message()} -- From line ${message.lineNumber()} of ${message.sourceId()}")
                return true
            }
        }
        binding.wvCardPayment.settings.javaScriptEnabled = true
        viewModel.setLinkHttp(formUrl)
        viewModel.linkPayment.observe(this.viewLifecycleOwner) {
            binding.wvCardPayment.loadUrl(it)
        }
        return binding.root
    }

    private fun inflateBindingLazy(inflater: LayoutInflater) = FragmentCardPaymentBinding.inflate(inflater)

    companion object {
        @JvmStatic
        fun newInstance() = CardPaymentFragment()
    }
}