@file:OptIn(ExperimentalMaterialApi::class)

package ru.iwater.youwater.screen.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.iwater.youwater.R
import ru.iwater.youwater.data.ClientProfileViewModel
import ru.iwater.youwater.screen.login.ButtonEnter
import ru.iwater.youwater.theme.YouWaterTypography
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun AddAddressScreen(clientProfileViewModel: ClientProfileViewModel = viewModel(), navController: NavController, isFromOrder: Boolean) {
    val modifier = Modifier
    var selectRegion by remember {
        mutableStateOf("Выберете регион")
    }
    var expandedRegion by remember {
        mutableStateOf(false)
    }
    val listRegion = listOf("Санкт-Петербург", "Ленинградская область")
    var city by rememberSaveable {
        mutableStateOf("")
    }
    var street by rememberSaveable {
        mutableStateOf("")
    }
    var house by rememberSaveable {
        mutableStateOf("")
    }
    var structure by rememberSaveable {
        mutableStateOf("")
    }
    var entrance by rememberSaveable {
        mutableStateOf("")
    }
    var apartment by rememberSaveable {
        mutableStateOf("")
    }
    var floor by rememberSaveable {
        mutableStateOf("")
    }
    var notice by rememberSaveable {
        mutableStateOf("")
    }
    var contact by rememberSaveable {
        mutableStateOf("")
    }
    var isEnabledButton by rememberSaveable {
        mutableStateOf(false)
    }

    Column(modifier = modifier
        .fillMaxSize()
        .padding(8.dp)) {
        TitleAddAddress(modifier = modifier.padding(8.dp))
        SetRegion(regionList = listRegion, selectRegion = selectRegion, expandedRegion = expandedRegion, setRegion = {
            selectRegion = it
            expandedRegion = !expandedRegion
        })
        SetCity(city = city, setCity = {city = it})
        SetStreet(street = street, setStreet = {street = it})
        SetHome(home = house, setHome = {house = it})
        SetStructure(structure = structure, setStructure = {structure = it})
        SetEntrance(entrance = entrance, setEntrance = {entrance = it})
        SetApartment(apartment = apartment, setApartment = {apartment = it})
        SetFloor(floor = floor, setFloor = {floor = it})
        SetNotice(notice = notice, setNotice = {notice = it})
        SetAltContact(contact = contact, setContact = {contact = it})
        isEnabledButton = selectRegion != "Выберете регион" && city.isNotEmpty() && street.isNotEmpty() && house.isNotEmpty()
        ButtonEnter(text = stringResource(id = R.string.fragment_create_order_add_address_to_order), isEnabledButton = isEnabledButton) {
            clientProfileViewModel.createNewAddress(region = selectRegion, city = city, street = street, house = house, building = structure, entrance = entrance, floor = floor, flat = apartment, contact = contact, notice = notice, isFromOrder, navController = navController)
        }
    }
}

@Composable
fun TitleAddAddress(modifier: Modifier) {
    Text(
        text = stringResource(id = R.string.fragment_add_address_label),
        textAlign = TextAlign.Center,
        style = YouWaterTypography.body1,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .fillMaxWidth()
    )
}

@Composable
fun SetRegion(
    regionList: List<String>,
    selectRegion: String,
    expandedRegion: Boolean,
    setRegion: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        expanded = expandedRegion,
        onExpandedChange = {
            setRegion("Выберете регион")

        }
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = selectRegion,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = "регион") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRegion)
            },
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
        )
        ExposedDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = expandedRegion,
            onDismissRequest = { setRegion("Выберете регион") }
        ) {
            regionList.forEach { region ->
                DropdownMenuItem(
                    onClick = {
                        setRegion(region)
                    }
                ) {
                    Text(text = region)
                }
            }
        }
    }
}

@Composable
fun SetCity(city: String, setCity: (String) -> Unit) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        value = city,
        onValueChange = {setCity(it)},
        label = {Text(text = stringResource(id = R.string.fragment_add_address_city))},
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
    )
}

@Composable
fun SetStreet(street: String, setStreet: (String) -> Unit) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        value = street,
        onValueChange = {setStreet(it)},
        label = {Text(text = stringResource(id = R.string.fragment_add_address_street))},
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
    )
}

@Composable
fun SetHome(home: String, setHome: (String) -> Unit) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        value = home,
        onValueChange = {setHome(it)},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        label = {Text(text = stringResource(id = R.string.fragment_add_address_home))},
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
    )
}

@Composable
fun SetStructure(structure: String, setStructure: (String) -> Unit) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        value = structure,
        onValueChange = {setStructure(it)},
        label = {Text(text = stringResource(id = R.string.fragment_add_address_structure))},
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
    )
}

@Composable
fun SetEntrance(entrance: String, setEntrance: (String) -> Unit) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        value = entrance,
        onValueChange = {setEntrance(it)},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        label = {Text(text = stringResource(id = R.string.fragment_add_address_entrance))},
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
    )
}

@Composable
fun SetApartment(apartment: String, setApartment: (String) -> Unit) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        value = apartment,
        onValueChange = {setApartment(it)},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        label = {Text(text = stringResource(id = R.string.fragment_add_address_apartment))},
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
    )
}

@Composable
fun SetFloor(floor: String, setFloor: (String) -> Unit) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        value = floor,
        onValueChange = {setFloor(it)},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        label = {Text(text = stringResource(id = R.string.fragment_add_address_floor))},
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
    )
}

@Composable
fun SetNotice(notice: String, setNotice: (String) -> Unit) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        value = notice,
        onValueChange = {setNotice(it)},
        label = {Text(text = stringResource(id = R.string.fragment_add_address_notice))},
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
    )
}

@Composable
fun SetAltContact(contact: String, setContact: (String) -> Unit) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        value = contact,
        onValueChange = {setContact(it)},
        label = {Text(text = stringResource(id = R.string.fragment_add_address_contact))},
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
    )
}

@Preview
@Composable
fun AddAddressScreenPreview() {
    val modifier = Modifier
    var selectRegion by remember {
        mutableStateOf("Выберете регион")
    }
    var expandedRegion by remember {
        mutableStateOf(false)
    }
    val listRegion = listOf("Санкт-Петербург", "Ленинградская область")
    var city by rememberSaveable {
        mutableStateOf("")
    }
    var street by rememberSaveable {
        mutableStateOf("")
    }
    var home by rememberSaveable {
        mutableStateOf("")
    }
    var structure by rememberSaveable {
        mutableStateOf("")
    }
    var entrance by rememberSaveable {
        mutableStateOf("")
    }
    var apartment by rememberSaveable {
        mutableStateOf("")
    }
    var floor by rememberSaveable {
        mutableStateOf("")
    }
    var notice by rememberSaveable {
        mutableStateOf("")
    }
    var contact by rememberSaveable {
        mutableStateOf("")
    }

    YourWaterTheme {
        Column(modifier = modifier
            .fillMaxSize()
            .padding(8.dp)) {
            TitleAddAddress(modifier = modifier.padding(8.dp))
            SetRegion(regionList = listRegion, selectRegion = selectRegion, expandedRegion = expandedRegion, setRegion = {
                selectRegion = it
                expandedRegion = !expandedRegion
            })
            SetCity(city = city, setCity = {city = it})
            SetStreet(street = street, setStreet = {street = it})
            SetHome(home = home, setHome = {home = it})
            SetStructure(structure = structure, setStructure = {structure = it})
            SetEntrance(entrance = entrance, setEntrance = {entrance = it})
            SetApartment(apartment = apartment, setApartment = {apartment = it})
            SetFloor(floor = floor, setFloor = {floor = it})
            SetNotice(notice = notice, setNotice = {notice = it})
            SetAltContact(contact = contact, setContact = {contact = it})
            ButtonEnter(text = stringResource(id = R.string.fragment_create_order_add_address_to_order), isEnabledButton = true) {
                
            }
        }
    }
}