package ru.iwater.youwater.screen.catalog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ru.iwater.youwater.base.App
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.databinding.FragmentCatalogProductBinding
import ru.iwater.youwater.data.ProductListViewModel
import ru.iwater.youwater.screen.adapters.AdapterProductList
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * Фрагмент списка товаров определённой категории
 */
class CatalogProductFragment : Fragment(), AdapterProductList.OnProductItemClickListener {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    val viewModel: ProductListViewModel by viewModels { factory }
//    private val adapterProductList = AdapterProductList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCatalogProductBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val catalogId = CatalogProductFragmentArgs.fromBundle(arguments!!).typeId
        val catalogTitle = CatalogProductFragmentArgs.fromBundle(arguments!!).typeString
        val adapterProductList = AdapterProductList(AdapterProductList.OnClickListener {

        }, this)
        viewModel.setCatalogItem(catalogId)
        binding.tvLabelCatalog.text = catalogTitle
        binding.rvProductList.adapter = adapterProductList
        viewModel.productsList.observe(this.viewLifecycleOwner, {
            adapterProductList.submitList(it)
        })
        return binding.root
    }

    override fun onProductItemClicked(product: Product) {
        Toast.makeText(this.context, "Товар ${product.app_name} добавлн в корзину", Toast.LENGTH_LONG).show()
        viewModel.addProductInBasket(product)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CatalogProductFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CatalogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}