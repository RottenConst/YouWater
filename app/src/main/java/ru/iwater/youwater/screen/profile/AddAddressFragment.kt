package ru.iwater.youwater.screen.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.CancellationToken
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.Address
import ru.iwater.youwater.data.AddressResult
import ru.iwater.youwater.data.AddressViewModel
import ru.iwater.youwater.databinding.FragmentAddAddressBinding
import timber.log.Timber
import javax.inject.Inject

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddAddressFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddAddressFragment : BaseFragment(), GoogleMap.OnPoiClickListener {

    private var param1: String? = null
    private var param2: String? = null

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: AddressViewModel by viewModels { factory }
    private val screenComponent = App().buildScreenComponent()

    private lateinit var lastLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    val binding: FragmentAddAddressBinding by lazy {
        FragmentAddAddressBinding.inflate(
            LayoutInflater.from(this.context)
        )
    }
    private lateinit var googleMap: GoogleMap
    private val marker: Marker? by lazy { googleMap.addMarker(MarkerOptions().position(LatLng(0.0, 0.0))) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.lifecycleOwner = this
        binding.mapView.onCreate(savedInstanceState)
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        )
        var addressSave: AddressResult? = null
        binding.searchAddress.isSingleLine = true
        binding.searchAddress.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                binding.searchAddress.requestFocus()
                viewModel.getInfoOnAddress(binding.searchAddress.text.toString())
                binding.searchAddress.clearFocus()
                val imm: InputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
            return@setOnKeyListener false
        }
        viewModel.addressResult.observe(this.viewLifecycleOwner, {
            if (it != null) {
                val addressList = it.results[0].formatted_address.split(", ")
                val address = "${addressList[2]}, ${addressList[0]}, ${addressList[1]}"
                binding.searchAddress.text.clear()
                binding.searchAddress.text.insert(0, address)
                binding.etHome.text?.clear()
                binding.etHome.text?.insert(0, addressList[1])
                marker?.position = LatLng(it.results[0].geometry.location.lat, it.results[0].geometry.location.lng)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.results[0].geometry.location.lat, it.results[0].geometry.location.lng), 17.0f))
                addressSave = it
            }
        })
        binding.btnSaveAddress.setOnClickListener {
            if (!binding.etHome.text.isNullOrEmpty()) {
                if (addressSave != null) {
                    val address = Address(
                        addressSave!!.results[0].address_components[2].short_name,
                        addressSave!!.results[0].address_components[1].long_name.removeSuffix(" улица"),
                        binding.etHome.text.toString().toInt(),
                        binding.etStructure.text.toString(),
                        if (binding.etEntrance.text?.isNotBlank() == true) {
                            binding.etEntrance.text.toString().toInt()
                        } else null,
                        if (binding.etFloor.text?.isNotBlank() == true) {
                            binding.etFloor.text.toString().toInt()
                        } else null,
                        if (binding.etApartment.text?.isNotBlank() == true){
                            binding.etApartment.text.toString().toInt()
                        } else null,
                        binding.etNote.text.toString())
//                    Timber.d("${address.street}")
                    viewModel.saveAddress(address)
                    this.findNavController().navigate(
                        AddAddressFragmentDirections.actionAddAddressFragmentToAddresessFragment()
                    )
                } else {
                    Toast.makeText(this.context, "Не был указан корректный адрес", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this.context, "Не был указан корректный адрес", Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()

    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onPoiClick(poi: PointOfInterest) {
        marker?.position = poi.latLng
        viewModel.getPlace(poi.placeId)
        Timber.d("PlaceId = ${poi.placeId}")
//        Toast.makeText(this.context, """Clicked: ${poi.name}
//            Place ID:${poi.placeId}
//            Latitude:${poi.latLng.latitude} Longitude:${poi.latLng.longitude}""",
//            Toast.LENGTH_SHORT
//        ).show()
    }

    private val locationPermissionRequest = this.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    )
    { permissions -> permissions.entries.forEach {
//        val permissionName = it.key
        val isGranted = it.value
        if (isGranted) {
            useGoogleMap()
        }
    }
    }

    private fun useGoogleMap() {
        binding.mapView.getMapAsync { mMap ->
            googleMap = mMap
            val context = this.context
            if (context != null) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionRequest.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                    )
                    return@getMapAsync
                }
                googleMap.isMyLocationEnabled = true
                googleMap.setOnPoiClickListener(this)
                fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(context)
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 17.0f))
                    } else {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(59.93, 30.31 ), 10f))
                    }
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddAddressFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddAddressFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}