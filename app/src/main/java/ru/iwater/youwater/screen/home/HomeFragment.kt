package ru.iwater.youwater.screen.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import ru.iwater.youwater.R
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.domain.Product
import ru.iwater.youwater.domain.ProductListViewModel
import ru.iwater.youwater.screen.adapters.CatalogWaterAdapter
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * Фрагмент для домашнего экрана
 */
class HomeFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val screenComponent = App().buildScreenComponent()
    private val adapterWatter = CatalogWaterAdapter()
    private val adapterContainers = CatalogWaterAdapter()
    private val adapterCoolers = CatalogWaterAdapter()
    private val adapterEquipments = CatalogWaterAdapter()
    private val adapterRacks = CatalogWaterAdapter()
    private val adapterRelated = CatalogWaterAdapter()
    private val adapterPomp = CatalogWaterAdapter()
    private val adapterDishes = CatalogWaterAdapter()
    private val viewModel: ProductListViewModel by viewModels { factory }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRV(this.context, LinearLayoutManager.HORIZONTAL)
        viewModel.productLiveData.observe(viewLifecycleOwner, Observer {
            addGenerateProductWatter(it)
            addGenerateContainer(it)
            addGenerateCoolers(it)
            addGenerateDishes(it)
            addGenerateEquipments(it)
            addGeneratePomp(it)
            addGenerateRacks(it)
            addGenerateRelated(it)
        })
    }


    private fun initRV(context: Context?, orientation: Int) {
        rv_water.layoutManager = LinearLayoutManager(context, orientation, false)
        rv_dishes_product.layoutManager = LinearLayoutManager(context, orientation, false)
        rv_contaners.layoutManager = LinearLayoutManager(context, orientation, false)
        rv_coolers.layoutManager = LinearLayoutManager(context, orientation, false)
        rv_equpments.layoutManager = LinearLayoutManager(context, orientation, false)
        rv_racks_bottle.layoutManager = LinearLayoutManager(context, orientation, false)
        rv_related_prod.layoutManager = LinearLayoutManager(context, orientation, false)
        rv_water_pomp.layoutManager = LinearLayoutManager(context, orientation, false)
        adapterWatter.notifyDataSetChanged()
        adapterContainers.notifyDataSetChanged()
        adapterCoolers.notifyDataSetChanged()
        adapterDishes.notifyDataSetChanged()
        adapterEquipments.notifyDataSetChanged()
        adapterPomp.notifyDataSetChanged()
        adapterRacks.notifyDataSetChanged()
        adapterRelated.notifyDataSetChanged()
        rv_water.adapter = adapterWatter
        rv_contaners.adapter = adapterContainers
        rv_water_pomp.adapter = adapterPomp
        rv_related_prod.adapter = adapterRelated
        rv_racks_bottle.adapter = adapterRacks
        rv_equpments.adapter = adapterEquipments
        rv_coolers.adapter = adapterCoolers
        rv_dishes_product.adapter = adapterDishes
    }

    private fun addGenerateProductWatter(products: List<Product>) {
        adapterWatter.productsList.clear()
        adapterWatter.productsList.addAll(products)
        adapterWatter.notifyDataSetChanged()
    }

    private fun addGenerateContainer(products: List<Product>) {
        adapterContainers.productsList.clear()
        adapterContainers.productsList.addAll(products)
        adapterContainers.notifyDataSetChanged()
    }

    private fun addGeneratePomp(products: List<Product>) {
        adapterPomp.productsList.clear()
        adapterPomp.productsList.addAll(products)
        adapterPomp.notifyDataSetChanged()
    }

    private fun addGenerateRacks(products: List<Product>) {
        adapterRacks.productsList.clear()
        adapterRacks.productsList.addAll(products)
        adapterRacks.notifyDataSetChanged()
    }

    private fun addGenerateEquipments(products: List<Product>) {
        adapterEquipments.productsList.clear()
        adapterEquipments.productsList.addAll(products)
        adapterEquipments.notifyDataSetChanged()
    }

    private fun addGenerateRelated(products: List<Product>) {
        adapterRelated.productsList.clear()
        adapterRelated.productsList.addAll(products)
        adapterRelated.notifyDataSetChanged()
    }

    private fun addGenerateCoolers(products: List<Product>) {
        adapterCoolers.productsList.clear()
        adapterCoolers.productsList.addAll(products)
        adapterCoolers.notifyDataSetChanged()
    }

    private fun addGenerateDishes(products: List<Product>) {
        adapterDishes.productsList.clear()
        adapterDishes.productsList.addAll(products)
        adapterDishes.notifyDataSetChanged()
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