package ru.iwater.youwater.screen.catalog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.databinding.FragmentCatalogBinding
import ru.iwater.youwater.domain.TypeProduct
import ru.iwater.youwater.screen.adapters.AdapterCatalogList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CatalogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CatalogFragment : BaseFragment() {

    private val adapterCatalog = AdapterCatalogList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCatalogBinding.inflate(inflater)
        binding.rvCatalogList.adapter = adapterCatalog

        val typesProductList: List<TypeProduct> = listOf(
            TypeProduct(0,"Питьевая вода"),
            TypeProduct(1,"Сопутствующие товары"),
            TypeProduct(2,"Одноразовая посуда"),
            TypeProduct(3,"Помпы для воды"),
            TypeProduct(4,"Кулеры для воды"),
            TypeProduct(5,"Оборудование")
        )
        adapterCatalog.submitList(typesProductList)

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CatalogFragment.
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