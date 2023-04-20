package ru.iwater.youwater.screen.basket

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import ru.iwater.youwater.data.PaymentStatus.*
import ru.iwater.youwater.vm.ProductListViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoadUrl(
    productListViewModel: ProductListViewModel,
    orderId: String,
    url: String,
    navController: NavController
) {
    val endLink = "http://605d3ea8e59a.ngrok.io"
    var endUrl by rememberSaveable {
        mutableStateOf("")
    }
    if (!endUrl.contentEquals("http://605d3ea8e59a.ngrok.io/") || !endUrl.contentEquals("https://605d3ea8e59a.ngrok.io")) {
        AndroidView(factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        endUrl =
                            url?.removeRange(endLink.lastIndex + 2, url.lastIndex + 1).toString()
                        if (endUrl.contentEquals("http://605d3ea8e59a.ngrok.io/") || endUrl.contentEquals("https://605d3ea8e59a.ngrok.io")) {
                            productListViewModel.getPaymentStatus(orderId, navController)
                        }
                        super.onPageStarted(view, url, favicon)
                    }
                }
                settings.javaScriptEnabled = true
                loadUrl(url)
            }
        }, update = {
            it.loadUrl(url)
        }
        )
    }

}