package ru.iwater.youwater.screen.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.databinding.FragmentFavoriteBinding
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.CatalogListViewModel
import javax.inject.Inject

class FavoriteFragment : BaseFragment() {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val screenComponent = App().buildScreenComponent()
    val viewModel: CatalogListViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFavoriteBinding.inflate(inflater)
        val navController = NavHostFragment.findNavController(this)
        binding.lifecycleOwner = this
        binding.composeViewFavoriteProducts.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.Default
            )
            setContent {
                YourWaterTheme {
                    FavoriteScreen(catalogListViewModel = viewModel, navController = navController)
                }
            }
        }
        return binding.root
    }

    override fun onResume() {
        viewModel.getFavoriteProductList()
        super.onResume()
    }

    companion object {
        @JvmStatic
        fun newInstance() = FavoriteFragment()
    }
}