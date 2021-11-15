package ru.iwater.youwater.screen.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.AboutProductViewModel
import ru.iwater.youwater.databinding.FragmentAboutProductBinding
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AboutProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AboutProductFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    val viewModel: AboutProductViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAboutProductBinding.inflate(inflater)
        val productId = AboutProductFragmentArgs.fromBundle(arguments!!).orderId
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.initProduct(productId)
        binding.btnPlusCount.setOnClickListener {
            viewModel.plusCountProduct()
        }
        binding.btnMinusCount.setOnClickListener {
            viewModel.minusCountProduct()
        }
        binding.btnBuyProduct.setOnClickListener {
            viewModel.product.observe(this.viewLifecycleOwner, {
                Toast.makeText(this.context, "Товар ${it.app_name} добавлн в корзину", Toast.LENGTH_LONG).show()
                viewModel.addProductToBasket(it)
            })
        }
//        viewModel.product.observe(this.viewLifecycleOwner, {
//            binding.apply {
//                tvLabelName.text = it.name
//                tvAboutProduct.text = it.about
//            }
//
//        })
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AboutProductFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AboutProductFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}