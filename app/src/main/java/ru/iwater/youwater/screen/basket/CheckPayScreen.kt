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
import androidx.navigation.NavHostController
import ru.iwater.youwater.vm.PaymentViewModel
import timber.log.Timber

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoadUrl(
    watterViewModel: PaymentViewModel,
    navController: NavHostController
) {
    Timber.d("started LOAD URL SCREEN")
    val checkUrl = watterViewModel.getCheckUrl()
    var endUrl by rememberSaveable {
        mutableStateOf("")
    }
    Timber.d("check url = $checkUrl")
    if (!endUrl.contentEquals("https://yourwater.ru/")) {
        AndroidView(factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        endUrl = url.toString()
                        if (endUrl.contentEquals("https://yourwater.ru/")) {
                            watterViewModel.setPaymentStatus(navController)
                        }
                        super.onPageStarted(view, url, favicon)
                    }
                }
                settings.javaScriptEnabled = true
                loadUrl(checkUrl)
            }
        }, update = {
            it.loadUrl(checkUrl)
        }
        )
    }

}