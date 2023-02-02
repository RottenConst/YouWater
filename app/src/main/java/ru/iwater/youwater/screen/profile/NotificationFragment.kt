package ru.iwater.youwater.screen.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ru.iwater.youwater.base.App
import ru.iwater.youwater.data.ClientProfileViewModel
import ru.iwater.youwater.databinding.FragmentNotificationBinding
import javax.inject.Inject

class NotificationFragment : Fragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    private val viewModel: ClientProfileViewModel by viewModels { factory }

    val binding: FragmentNotificationBinding by lazy {
        FragmentNotificationBinding.inflate(
            LayoutInflater.from(this.context)
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.client.observe(this.viewLifecycleOwner) {client ->
            when (client.mailing) {
                0 -> binding.switchNotification.isChecked = false
                1 -> binding.switchNotification.isChecked = true
                else -> {
                    Toast.makeText(this.context, "Не удается получить состояние", Toast.LENGTH_SHORT).show()
                }
            }
            binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
                when (isChecked) {
                    true -> viewModel.setMailing(client.client_id, true)
                    false -> viewModel.setMailing(client.client_id, false)
                }
            }
        }
        return binding.root
    }

    companion object {
        fun newInstance() =
            NotificationFragment()
    }
}