package ru.iwater.youwater.screen.profile

import android.os.Bundle
import android.text.Editable
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonObject
import ru.iwater.youwater.R
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.ClientProfileViewModel
import ru.iwater.youwater.data.StatusSendData
import ru.iwater.youwater.databinding.FragmentEditUserDataBinding
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class EditUserDataFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: ClientProfileViewModel by viewModels { factory }
    private val screenComponent = App().buildScreenComponent()
    private val binding: FragmentEditUserDataBinding by lazy { initBinding(LayoutInflater.from(this.context)) }

    private var clientId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding.ibClearEmail.setOnClickListener {
            binding.tvEditEmail.text.clear()
        }
        binding.ibClearPhone.setOnClickListener {
            binding.tvEditPhone.text.clear()
        }
        binding.ibClearLastname.setOnClickListener {
            binding.tvEditLastname.text.clear()
        }
        binding.ibClearName.setOnClickListener {
            binding.tvEditName.text.clear()
        }
        viewModel.client.observe(this.viewLifecycleOwner) { client ->
            //clientId
            clientId = client.client_id


            //lastname

            var lastName = false
            client.name.forEach {
                if (it.isWhitespace()) {
                    lastName = true
                }
            }
            if (lastName) {
                //name
                binding.tvEditName.text.clear()
                binding.tvEditName.text = Editable.Factory.getInstance().newEditable(client.name)
                //lastName
                binding.tvEditLastname.text.clear()
                binding.tvEditLastname.text =
                    Editable.Factory.getInstance().newEditable(client.lastname)
                //phone
                binding.tvEditPhone.text.clear()
                binding.tvEditPhone.text = Editable.Factory.getInstance().newEditable(client.phone)
                //email
                binding.tvEditEmail.text.clear()
                binding.tvEditEmail.text = Editable.Factory.getInstance().newEditable(client.email)
            } else {
                //name
                binding.tvEditName.text.clear()
                binding.tvEditName.text = Editable.Factory.getInstance().newEditable(client.name)
                //lastname
                binding.tvEditLastname.text.clear()
                //phone
                binding.tvEditPhone.text.clear()
                binding.tvEditPhone.text = Editable.Factory.getInstance().newEditable(client.phone)
                //email
                binding.tvEditEmail.text.clear()
                binding.tvEditEmail.text = Editable.Factory.getInstance().newEditable(client.email)
            }
        }
        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.login_out_menu).isVisible = false
        menu.findItem(R.id.send_edit).isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.send_edit -> {
                val sdf = SimpleDateFormat("dd.MM.yyyy")
                val date = sdf.format(Calendar.getInstance().time)
                val clientData = JsonObject()
                clientData.addProperty("name", binding.tvEditName.text.toString())
                clientData.addProperty("lastname", binding.tvEditLastname.text.toString())
                clientData.addProperty("phone", binding.tvEditPhone.text.toString())
                clientData.addProperty("email", binding.tvEditEmail.text.toString())
                viewModel.createAutoTask(clientId, date, clientData)
                viewModel.statusSend.observe(this.viewLifecycleOwner) { status ->
                    when (status) {
                        StatusSendData.SUCCESS -> {
                            this.findNavController().navigate(
                                EditUserDataFragmentDirections.actionEditUserDataFragmentToUserDataFragment2(true))
                        }
                        else -> Toast.makeText(context, "Ошибка, данные не были отправлены", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initBinding(inflater: LayoutInflater) = FragmentEditUserDataBinding.inflate(inflater)

    companion object {
        @JvmStatic
        fun newInstance() =
            EditUserDataFragment()
    }
}