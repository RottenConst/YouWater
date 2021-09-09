package ru.iwater.youwater.screen.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.databinding.FragmentHomeBinding
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
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater)
        initRV(binding)
        viewModel.productLiveData.observe(viewLifecycleOwner, Observer {
            adapterWatter.submitList(it)
            adapterContainers.submitList(it)
            adapterPomp.submitList(it)
            adapterRelated.submitList(it)
            adapterRacks.submitList(it)
            adapterEquipments.submitList(it)
            adapterCoolers.submitList(it)
            adapterDishes.submitList(it)
        })
        return binding.root
    }

    private fun initRV(binding: FragmentHomeBinding) {
        binding.apply {
            rvWater.adapter = adapterWatter
            rvContainers.adapter = adapterContainers
            rvWaterPomp.adapter = adapterPomp
            rvRelatedProd.adapter = adapterRelated
            rvRacksBottle.adapter = adapterRacks
            rvEquipment.adapter = adapterEquipments
            rvCoolers.adapter = adapterCoolers
            rvDishesProduct.adapter = adapterDishes
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