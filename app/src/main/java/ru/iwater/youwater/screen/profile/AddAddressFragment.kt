package ru.iwater.youwater.screen.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonObject
import ru.iwater.youwater.R
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.vm.AddressViewModel
import ru.iwater.youwater.data.StatusSendData
import ru.iwater.youwater.databinding.FragmentAddAddressBinding
import javax.inject.Inject

/**
 * Фрагмент для добавления нового адреса клиента
 */
class AddAddressFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: AddressViewModel by viewModels { factory }
    private val screenComponent = App().buildScreenComponent()

    val binding: FragmentAddAddressBinding by lazy {
        FragmentAddAddressBinding.inflate(
            LayoutInflater.from(this.context)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.lifecycleOwner = this

        //true - перешли в добавление адреса из создание заявки, false - из меню адреса
        val isFromOrder = AddAddressFragmentArgs.fromBundle(this.requireArguments()).isFromOrder

        var regionName = ""
        val context = this.context
        if (context != null) {
            val listRegion = mutableListOf(
                "Санкт-Петербург",
                "Ленинградская область",
                "Выберете регион"
            )

            val spinnerAdapter = ArrayAdapter(
                context,
                R.layout.spinner_item_layout_resource,
                R.id.TextView,
                listRegion
            )
            binding.spinnerSetRegion.adapter = spinnerAdapter
            binding.spinnerSetRegion.setSelection(2)

            val itemRegionListener: AdapterView.OnItemSelectedListener =
                object: AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                      regionName = parent?.getItemAtPosition(position) as String
                      listRegion.remove("Выберете регион")
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }

            binding.spinnerSetRegion.onItemSelectedListener = itemRegionListener
            if (binding.spinnerSetRegion.isFocused) {
                spinnerAdapter.notifyDataSetChanged()
            }
        }

        binding.btnSaveAddress.setOnClickListener {
            //регион
            val region = regionName
            // город поселок
            val city =
                if (binding.etCity.text.isNullOrEmpty()) "" else binding.etCity.text.toString()
            //улица
            val street =
                if (binding.etStreet.text.isNullOrEmpty()) "" else binding.etStreet.text.toString()
            //дои
            val house = if (binding.etHome.text.isNullOrEmpty()) "" else binding.etHome.text.toString()
            //строение
            val building =
                if (binding.etStructure.text.isNullOrEmpty()) "" else binding.etStructure.text.toString()
            //подъезд
            val entrance =
                if (binding.etEntrance.text.isNullOrEmpty()) "" else binding.etEntrance.text.toString()
            //этаж
            val floor = if (binding.etFloor.text.isNullOrEmpty()) "" else binding.etFloor.text.toString()
            //квартира
            val flat =
                if (binding.etApartment.text.isNullOrEmpty()) "" else binding.etApartment.text.toString()
            //примичание
            val note = if (binding.etNote.text.isNullOrEmpty()) "" else binding.etNote.text.toString()
            // доп контакт
            val contact = if (binding.etContact.text.isNullOrEmpty()) "" else binding.etContact.text.toString()

            // перевод данных в формат для отправки в црм
            val addressJson =
                getJsonAddress(region, city, street, house, building, entrance, floor, flat)
            val factAddress = getFactAddress(city, street, house, building, entrance, floor, flat)
            val address = getAddress(street, house, building)
            val fullAddress = getFullAddress(region, street, house, building)

            if (addressJson != null && region != "Выберете регион") {
                viewModel.createNewAddress(
                    region,
                    factAddress,
                    address,
                    "",
                    fullAddress,
                    0,
                    addressJson,
                    contact,
                    note
                )

                viewModel.statusSend.observe(viewLifecycleOwner) { status ->
                    when (status) {
                        //адрес создался
                        StatusSendData.SUCCESS -> {
                            // перешли из заявки?
                            if (isFromOrder) {
                                this.findNavController().navigate(
                                    //отправляем назад к созданию заявки
                                    AddAddressFragmentDirections.actionAddAddressFragmentToCreateOrderFragment(
                                        false, 0
                                    )
                                )
                            } else {
                                this.findNavController().navigate(
                                    //отправляем назад в меню адреса
                                    AddAddressFragmentDirections.actionAddAddressFragmentToAddresessFragment()
                                )
                            }
                        }
                        //адрес не создан
                        else -> warning("Ошибка, данные не были отправлены")
                    }
                }
            } else {
                warning("Не был указан корректный адрес")
            }
        }

        return binding.root
    }

    private fun getFullAddress(region: String, street: String, house: String, building: String) =
        when {
            building.isEmpty() -> "$region, $street д. $house"
            else -> "$region, $street д. $house корп. $building"
        }


    private fun getAddress(street: String, house: String, building: String) =
        when {
            building.isEmpty() -> "$street д. $house"
            else -> "$street д. $house корп. $building"
        }


    private fun getFactAddress(
        city: String,
        street: String,
        house: String,
        building: String,
        entrance: String,
        floor: String,
        flat: String,
    ) = when {
        //5
        flat.isEmpty() && floor.isEmpty() && entrance.isEmpty() && building.isEmpty() && city.isEmpty() -> "$street, д. $house"
        //4
        flat.isEmpty() && floor.isEmpty() && entrance.isEmpty() && building.isEmpty() -> "$city, $street, д. $house"
        flat.isEmpty() && floor.isEmpty() && entrance.isEmpty() && city.isEmpty() -> "$street, д. $house, корп. $building"
        flat.isEmpty() && floor.isEmpty() && building.isEmpty() && city.isEmpty() -> "$street, д. $house,подъезд $entrance"
        flat.isEmpty() && entrance.isEmpty() && building.isEmpty() && city.isEmpty() -> "$street, д. $house, этаж $floor"
        floor.isEmpty() && entrance.isEmpty() && building.isEmpty() && city.isEmpty() -> "$street, д. $house, кв. $flat"
        //3
        flat.isEmpty() && floor.isEmpty() && entrance.isEmpty() -> "$city, $street, д. $house, корп. $building"
        flat.isEmpty() && floor.isEmpty() && building.isEmpty() -> "$city, $street, д. $house, подъезд $entrance"
        flat.isEmpty() && floor.isEmpty() && city.isEmpty() -> "$street, д. $house, корп. $building, подъезд $entrance"
        flat.isEmpty() && building.isEmpty() && city.isEmpty() -> "$street, д. $house, подъезд $entrance, этаж $floor"
        flat.isEmpty() && entrance.isEmpty() && city.isEmpty() -> "$street, д. $house, корп. $building, этаж $floor"
        flat.isEmpty() && entrance.isEmpty() && building.isEmpty() -> "$city, $street, д. $house, этаж $floor, кв. $flat"
        entrance.isEmpty() && building.isEmpty() && city.isEmpty() -> "$street, д. $house, этаж $floor, кв. $flat"
        entrance.isEmpty() && building.isEmpty() && floor.isEmpty() -> "$city, $street, д. $house, кв. $flat"
        entrance.isEmpty() && floor.isEmpty() && city.isEmpty() -> "$street, д. $house, корп. $building, кв. $flat"
        building.isEmpty() && floor.isEmpty() && city.isEmpty() -> "$street, д. $house,подъезд $entrance, кв. $flat"
        //2
        flat.isEmpty() && floor.isEmpty() -> "$city, $street, д. $house, корп. $building, подъезд $entrance"
        flat.isEmpty() && entrance.isEmpty() -> "$city, $street, д. $house, корп. $building, этаж $floor"
        flat.isEmpty() && building.isEmpty() -> "$city, $street, д. $house, подъезд $entrance, этаж $floor"
        flat.isEmpty() && city.isEmpty() -> "$street, д. $house, корп. $building, подъезд $entrance, этаж $floor"
        floor.isEmpty() && entrance.isEmpty() -> "$city, $street, д. $house, корп. $building, кв. $flat"
        floor.isEmpty() && building.isEmpty() -> "$city, $street, д. $house, подъезд $entrance, кв. $flat"
        floor.isEmpty() && city.isEmpty() -> "$street, д. $house, корп. $building, подъезд $entrance, кв. $flat"
        entrance.isEmpty() && building.isEmpty() -> "$city, $street, д. $house, этаж $floor, кв. $flat"
        entrance.isEmpty() && city.isEmpty() -> "$street, д. $house, корп. $building, этаж $floor, кв. $flat"
        building.isEmpty() && city.isEmpty() -> "$street, д. $house, подъезд $entrance, этаж $floor, кв. $flat"
        //1
        flat.isEmpty() -> "$city, $street, д. $house, корп. $building, подъезд $entrance, этаж $floor"
        floor.isEmpty() -> "$city, $street, д. $house, корп. $building, подъезд $entrance, кв. $flat"
        entrance.isEmpty() -> "$city, $street, д. $house, корп. $building, этаж $floor, кв. $flat"
        building.isEmpty() -> "$city, $street, д. $house, подъезд $entrance, этаж $floor, кв. $flat"
        city.isEmpty() -> "$street, д. $house, корп. $building, подъезд $entrance, этаж $floor, кв. $flat"
        else -> "$city, $street, д. $house, корп. $building, подъезд $entrance, этаж $floor, кв. $flat"
    }

    private fun getJsonAddress(
        region: String,
        city: String,
        street: String,
        house: String,
        building: String,
        entrance: String,
        floor: String,
        flat: String,
    ): JsonObject? {
        val addressJson = JsonObject()
        if (region.isNotEmpty()) {
            addressJson.addProperty("region", region)
        } else {
            return null
        }
        if (street.isEmpty()) {
            return null
        } else {
            addressJson.addProperty("street", street)
        }
        if (house.isEmpty()) {
            return null
        } else {
            addressJson.addProperty("house", house)
        }
        addressJson.addProperty("city", city)
        addressJson.addProperty("building", building)
        addressJson.addProperty("entrance", entrance)
        addressJson.addProperty("floor", floor)
        addressJson.addProperty("flat", flat)
        return addressJson
    }

    private fun warning(message: String) {
        Toast.makeText(this.context, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddAddressFragment()
    }
}