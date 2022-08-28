package ru.iwater.youwater.screen.more

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.IntentCompat
import ru.iwater.youwater.R
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.databinding.FragmentContactBinding
import timber.log.Timber

class ContactFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding = FragmentContactBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.btnCallMe.setOnClickListener {
            val callIntent: Intent = Uri.parse("tel:+78129477993").let { number ->
                Intent(Intent.ACTION_DIAL, number)
            }
            startActivity(callIntent)
        }

        binding.btnCallBoss.setOnClickListener {
            val callBoss = Intent(Intent.ACTION_SENDTO)
            callBoss.data = Uri.parse("mailto:nadobnikov@allforwater.ru")
            startActivity(callBoss)
        }

        binding.btnSendMail.setOnClickListener {
            val sendMail = Intent(Intent.ACTION_SENDTO)
            sendMail.data = Uri.parse("mailto:info@yourwater.ru")
            startActivity(sendMail)
        }

        binding.btnTelegram.setOnClickListener {
            val openTelegram = Intent(Intent.ACTION_VIEW)
            openTelegram.data = Uri.parse("https://t.me/yourwater_ru_bot")
            startActivity(openTelegram)
        }

        binding.btnVk.setOnClickListener {
            val openVK = Intent(Intent.ACTION_VIEW)
            openVK.data = Uri.parse("https://vk.com/write-23344137")
            startActivity(openVK)
        }

        binding.btnWhatsapp.setOnClickListener {
            val openWhatsApp = Intent(Intent.ACTION_VIEW)
            openWhatsApp.data = Uri.parse("https://wa.me/78129477993")
            startActivity(openWhatsApp)
        }

        binding.btnInsta.setOnClickListener {
            val openInsta = Intent(Intent.ACTION_VIEW)
            openInsta.data = Uri.parse("https://instagram.com/yourwater_delivery")
            startActivity(openInsta)
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ContactFragment()
    }
}