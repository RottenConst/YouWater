package ru.iwater.youwater.screen.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.CatalogListViewModel
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.databinding.FragmentHomeBinding
import ru.iwater.youwater.screen.adapters.AdapterProductList
import ru.iwater.youwater.screen.adapters.CatalogWaterAdapter
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * Фрагмент для домашнего экрана
 */
class HomeFragment : BaseFragment(), AdapterProductList.OnProductItemClickListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val screenComponent = App().buildScreenComponent()
    private val viewModel: CatalogListViewModel by viewModels { factory }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater)
        initRV(binding)
        val adapterWatter = CatalogWaterAdapter(CatalogWaterAdapter.OnClickListener{
            viewModel.displayProduct(it)
        }, this)
        binding.rvTypeProductList.adapter = adapterWatter
        viewModel.catalogProductMap.observe(viewLifecycleOwner, { catalogs ->
            adapterWatter.submitList(catalogs.toList())
        })

        viewModel.navigateToSelectProduct.observe(this.viewLifecycleOwner, { if (null != it) {
                this.findNavController().navigate(
                    HomeFragmentDirections.actionShowAboutProductFragment(it)
                )
                viewModel.displayProductComplete()
            }
        })

        viewModel.productLiveData.observe(viewLifecycleOwner, {

        })
        return binding.root
    }

    override fun onProductItemClicked(product: Product) {
        viewModel.addProductInBasket(product)
        Toast.makeText(this.context, "Товар ${product.app_name} добавлн в корзину", Toast.LENGTH_LONG).show()
    }

    private fun initRV(binding: FragmentHomeBinding) {
        binding.apply {
//            rvCategoryList.adapter = adapterCategory

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}