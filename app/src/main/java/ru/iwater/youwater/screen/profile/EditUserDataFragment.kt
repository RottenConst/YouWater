package ru.iwater.youwater.screen.profile

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.iwater.youwater.R
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.ClientProfileViewModel
import ru.iwater.youwater.data.StatusSendData
import ru.iwater.youwater.databinding.FragmentEditUserDataBinding
import ru.iwater.youwater.theme.YourWaterTheme
import javax.inject.Inject

class EditUserDataFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: ClientProfileViewModel by viewModels { factory }
    private val screenComponent = App().buildScreenComponent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentEditUserDataBinding.inflate(inflater)
        val clientName = EditUserDataFragmentArgs.fromBundle(this.requireArguments()).clientName
        val clientPhone = EditUserDataFragmentArgs.fromBundle(this.requireArguments()).clientPhone
        val clientEmail = EditUserDataFragmentArgs.fromBundle(this.requireArguments()).clientEmail
        binding.composeViewEditUserDataScreen.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.Default
            )
            setContent {
                YourWaterTheme {
                    EditUserDataScreen(viewModel, clientName, clientPhone, clientEmail)
                }
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
                viewModel.editUserData()
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

    companion object {
        @JvmStatic
        fun newInstance() =
            EditUserDataFragment()
    }
}