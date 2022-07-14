package ru.iwater.youwater.screen.profile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.iwater.youwater.R
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.ClientProfileViewModel
import ru.iwater.youwater.databinding.FragmentUserDataBinding
import timber.log.Timber
import javax.inject.Inject

class UserDataFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: ClientProfileViewModel by viewModels { factory }
    private val screenComponent = App().buildScreenComponent()

    private val binding by lazy { FragmentUserDataBinding.inflate(LayoutInflater.from(this.context)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val binding = FragmentUserDataBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.clientVM = viewModel
        val sendUserData = UserDataFragmentArgs.fromBundle(this.requireArguments()).userDataSend
        Timber.d("USER SEND DATA $sendUserData")
        if (sendUserData) {
            binding.tvEditUserData.visibility = View.VISIBLE
        }
        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.login_out_menu).isVisible = false
        menu.findItem(R.id.edit_profile).isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_profile -> {
                findNavController().navigate(
                    UserDataFragmentDirections.actionUserDataFragmentToEditUserDataFragment()
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserDataFragment()
    }
}