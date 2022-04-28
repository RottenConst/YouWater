package ru.iwater.youwater.screen.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.gson.JsonObject
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.Address
import ru.iwater.youwater.data.AddressResult
import ru.iwater.youwater.data.AddressViewModel
import ru.iwater.youwater.data.StatusSendData
import ru.iwater.youwater.databinding.FragmentAddAddressBinding
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AddAddressFragment : BaseFragment(), GoogleMap.OnPoiClickListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: AddressViewModel by viewModels { factory }
    private val screenComponent = App().buildScreenComponent()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var clientId = 0

    val binding: FragmentAddAddressBinding by lazy {
        FragmentAddAddressBinding.inflate(
            LayoutInflater.from(this.context)
        )
    }
    private lateinit var googleMap: GoogleMap
    private val marker: Marker? by lazy { googleMap.addMarker(MarkerOptions().position(LatLng(0.0, 0.0))) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        viewModel.client.observe(this.viewLifecycleOwner) {
            clientId = this.id
        }

        viewModel.addressResult.observe(this.viewLifecycleOwner) {
            if (it != null) {
                val addressList = it.results[0].formatted_address.split(", ")
                val district =
                    if (it.results[0].address_components[3].long_name != "город") "район ${it.results[0].address_components[3].long_name}" else ""
                val address = "${addressList[2]}, $district ${addressList[0]}, ${addressList[1]}"
                binding.searchAddress.text.clear()
                binding.searchAddress.text.insert(0, address)
                binding.etHome.text?.clear()
                binding.etHome.text?.insert(0, addressList[1])
                marker?.position =
                    LatLng(it.results[0].geometry.location.lat, it.results[0].geometry.location.lng)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.results[0].geometry.location.lat,
                    it.results[0].geometry.location.lng), 17.0f))
                addressSave = it
            }
        }
        binding.btnSaveAddress.setOnClickListener {
            if (!binding.etHome.text.isNullOrEmpty()) {
                if (addressSave != null) {
                    val sdf = SimpleDateFormat("dd.MM.yyyy")
                    val date = sdf.format(Calendar.getInstance().time)
                    val clientData = JsonObject()

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
                    clientData.addProperty("region", address.region)
                    clientData.addProperty("street", address.street)
                    clientData.addProperty("house", address.house)
                    clientData.addProperty("building", address.building)
                    clientData.addProperty("entrance", address.entrance)
                    clientData.addProperty("floor", address.floor)
                    clientData.addProperty("flat", address.flat)
                    clientData.addProperty("note", address.note)
                    viewModel.createAutoTask(clientId, date, clientData)
                    viewModel.saveAddress(address)
                    viewModel.statusSend.observe(this.viewLifecycleOwner) { status ->
                        when (status) {
                            StatusSendData.SUCCESS -> {
                                this.findNavController().navigate(
                                    AddAddressFragmentDirections.actionAddAddressFragmentToAddresessFragment()
                                )
                            }
                            else -> Toast.makeText(context, "Ошибка, данные не были отправлены", Toast.LENGTH_LONG).show()
                        }
                    }

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
        @JvmStatic
        fun newInstance() = AddAddressFragment()
    }
}