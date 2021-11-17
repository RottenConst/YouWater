package ru.iwater.youwater.screen.profile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.AddressViewModel
import ru.iwater.youwater.databinding.FragmentAddresessBinding
import javax.inject.Inject
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.model.CameraPosition

import com.google.android.gms.maps.model.MarkerOptions

import com.google.android.gms.maps.model.LatLng




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddressesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddressesFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: AddressViewModel by viewModels { factory }
    private val binding: FragmentAddresessBinding by lazy { initBinding(LayoutInflater.from(this.context)) }
    private lateinit var googleMap: GoogleMap

    private val screenComponent = App().buildScreenComponent()
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.addAddressItem.mapView.onCreate(savedInstanceState)
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.addAddressItem.addAddress)
        bottomSheetBehavior.setPeekHeight(0, true)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        binding.btnAddAddress.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.addAddressItem.mapView.getMapAsync { mMap ->
                googleMap = mMap
                val context = this.context
                if (context != null)
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@getMapAsync
                }
                googleMap.isMyLocationEnabled = true

                // For dropping a marker at a point on the Map
                val sydney = LatLng(-34.0, 151.0)
                googleMap.addMarker(
                    MarkerOptions().position(sydney).title("Marker Title")
                        .snippet("Marker Description")
                )

                // For zooming automatically to the location of the marker
                val cameraPosition = CameraPosition.Builder().target(sydney).zoom(12f).build()
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.addAddressItem.mapView.onResume()

    }

    override fun onPause() {
        super.onPause()
        binding.addAddressItem.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.addAddressItem.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.addAddressItem.mapView.onLowMemory()
    }

    private fun initBinding(inflater: LayoutInflater) = FragmentAddresessBinding.inflate(inflater)

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddressesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddressesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}