package ru.iwater.youwater.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ru.iwater.youwater.R
import ru.iwater.youwater.screen.login.ButtonEnter
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.theme.YourWaterTheme

@Composable
fun AddAddressScreen(
    createNewAddress: (String, String, String, String, String, String, String, String, String, String, Boolean, NavHostController) -> Unit,
    navController: NavHostController,
    isFromOrder: Boolean
) {
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
    var block by rememberSaveable {
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
    var isValidateCity by rememberSaveable {
        mutableStateOf(true)
    }
    var isValidateStreet by rememberSaveable {
        mutableStateOf(true)
    }
    var isValidateHouse by rememberSaveable {
        mutableStateOf(true)
    }
    var isValidateStructure by rememberSaveable {
        mutableStateOf(true)
    }
    var isValidateEntrance by rememberSaveable {
        mutableStateOf(true)
    }
    var isValidateApartment by rememberSaveable {
        mutableStateOf(true)
    }
    var isValidateFloor by rememberSaveable {
        mutableStateOf(true)
    }

    var isVisibleDialog by remember {
        mutableStateOf(false)
    }
    val scrollState = rememberScrollState()
    Column(modifier = modifier
        .fillMaxSize()
        .padding(8.dp)
        .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top) {
        TitleAddAddress(modifier = modifier.padding(8.dp))
        SaveAddressDialog(
            isVisibleDialog = isVisibleDialog,
            setVisible = {isVisibleDialog = !isVisibleDialog},
            saveAddress = {
                createNewAddress(
                    selectRegion,
                    city,
                    street,
                    house,
                    block,
                    entrance,
                    floor,
                    apartment,
                    contact,
                    notice,
                    isFromOrder,
                    navController
                    )
            }
        )
        Box {
            Column {
                SetRegion(regionList = listRegion, selectRegion = selectRegion, expandedRegion = expandedRegion, setExpandedRegion = {expandedRegion = !expandedRegion}, setRegion = {
                    selectRegion = it
                })
                SetCity(city = city, setCity = {city = it}, isValidateText = isValidateCity, setValidation = {isValidateCity = it})
                SetStreet(street = street, setStreet = {street = it}, isValidateText = isValidateStreet, setValidation = {isValidateStreet = it})
            }
        }
        Box {
            Column {
                Row (modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Box(modifier = modifier.weight(1f)) {
                        SetHome(home = house, setHome = {house = it}, isValidateText = isValidateHouse, setValidation = {isValidateHouse = it})
                    }
                    Box(modifier = modifier.weight(1f)) {
                        SetStructure(structure = block, setStructure = {block = it}, isValidateText = isValidateStructure, setValidation = {isValidateStructure = it})
                    }
                }
                Row (modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Box(modifier = modifier.weight(1f)) {
                        SetEntrance(entrance = entrance, setEntrance = {entrance = it}, isValidateText = isValidateEntrance, setValidation = {isValidateEntrance = it})
                    }
                    Box(modifier = modifier.weight(1f)) {
                        SetApartment(apartment = apartment, setApartment = {apartment = it}, isValidateText = isValidateApartment, setValidation = {isValidateApartment = it})
                    }
                    Box(modifier = modifier.weight(1f)) {
                        SetFloor(floor = floor, setFloor = {floor = it}, isValidateText = isValidateFloor, setValidation = {isValidateFloor = it})
                    }
                }
                Box {
                    SetAltContact(contact = contact, setContact = {contact = it})
                }
                Box {
                    Column {
                        SetNotice(notice = notice, setNotice = {notice = it})
                        isEnabledButton = selectRegion != "Выберете регион" && city.isNotEmpty() && street.isNotEmpty() && house.isNotEmpty()
                                && isValidateCity && isValidateStreet && isValidateHouse && isValidateStructure && isValidateEntrance && isValidateApartment && isValidateFloor
                        ButtonEnter(text = stringResource(id = R.string.fragment_create_order_add_address_to_order), isEnabledButton = isEnabledButton) {
                            isVisibleDialog = true
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TitleAddAddress(modifier: Modifier) {
    Text(
        text = stringResource(id = R.string.fragment_add_address_label),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetRegion(
    regionList: List<String>,
    selectRegion: String,
    expandedRegion: Boolean,
    setExpandedRegion: (Boolean) -> Unit,
    setRegion: (String) -> Unit
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)) {
        ExposedDropdownMenuBox(
            expanded = expandedRegion,
            onExpandedChange = {
                setExpandedRegion(!expandedRegion)
                setRegion("Выберете регион")
            }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                value = selectRegion,
                onValueChange = {setRegion(selectRegion)},
                readOnly = true,
                label = {
                    Text(
                        text = stringResource(id = R.string.region),
                        style = MaterialTheme.typography.bodyLarge
                    )},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRegion)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            ExposedDropdownMenu(
                expanded = expandedRegion,
                onDismissRequest = { setExpandedRegion(false) }
            ) {
                regionList.forEach { region ->
                    DropdownMenuItem(
                    modifier = Modifier.fillMaxSize(),
                        onClick = {
                            setRegion(region)
                            setExpandedRegion(false)
                        },
                        text = {
                            Text(
                                text = region,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        colors = MenuDefaults.itemColors(
                            leadingIconColor = MaterialTheme.colorScheme.primary,
                            textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            trailingIconColor = MaterialTheme.colorScheme.error,
                            disabledTextColor = MaterialTheme.colorScheme.outline,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.tertiary,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SetCity(city: String, setCity: (String) -> Unit, isValidateText: Boolean = true, setValidation: (Boolean) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        value = city,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        onValueChange = {
            setCity(it)
            setValidation(!it.contains(Regex("""[^А-я\s] -""")))
                        },
        label = {
            Text(
                text = stringResource(id = R.string.fragment_add_address_city),
                style = MaterialTheme.typography.bodyLarge
            )},
        isError = !isValidateText,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.primary,
            errorContainerColor = MaterialTheme.colorScheme.errorContainer,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        maxLines = 1
    )
}

@Composable
fun SetStreet(street: String, setStreet: (String) -> Unit, isValidateText: Boolean = true, setValidation: (Boolean) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        value = street,
        onValueChange = {
            setStreet(it)
            setValidation(!it.contains(Regex("""[^А-я\s]""")))
                        },
        label = {
            Text(
                text = stringResource(id = R.string.fragment_add_address_street),
                style = MaterialTheme.typography.bodyLarge
            )},
        isError = !isValidateText,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            errorContainerColor = MaterialTheme.colorScheme.errorContainer,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        maxLines = 1
    )
}

@Composable
fun SetHome(home: String, setHome: (String) -> Unit, isValidateText: Boolean = true, setValidation: (Boolean) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        value = home,
        onValueChange = {
            setHome(it)
            setValidation(!it.contains(Regex("""[^0-9]""")))
                        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        label = {
            Text(
                text = stringResource(id = R.string.fragment_add_address_home),
                style = MaterialTheme.typography.bodyLarge
            )},
        isError = !isValidateText,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            errorContainerColor = MaterialTheme.colorScheme.errorContainer,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        maxLines = 1
    )
}

@Composable
fun SetStructure(structure: String, setStructure: (String) -> Unit, isValidateText: Boolean = true, setValidation: (Boolean) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        value = structure,
        onValueChange = {
            setStructure(it)
            setValidation(!it.contains(Regex("""[^А-я0-9]""")))
                        },
        label = {
            Text(
                text = stringResource(id = R.string.fragment_add_address_structure),
                style = MaterialTheme.typography.bodyLarge
            )},
        isError = !isValidateText,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            errorContainerColor = MaterialTheme.colorScheme.errorContainer,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        maxLines = 1
    )
}

@Composable
fun SetEntrance(entrance: String, setEntrance: (String) -> Unit, isValidateText: Boolean = true, setValidation: (Boolean) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        value = entrance,
        onValueChange = {
            setEntrance(it)
            setValidation(!it.contains(Regex("""[^0-9]""")))
                        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        label = {
            Text(
                text = stringResource(id = R.string.fragment_add_address_entrance),
                style = MaterialTheme.typography.bodyLarge
            )},
        isError = !isValidateText,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            errorContainerColor = MaterialTheme.colorScheme.errorContainer,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        maxLines = 1
    )
}

@Composable
fun SetApartment(apartment: String, setApartment: (String) -> Unit, isValidateText: Boolean = true, setValidation: (Boolean) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        value = apartment,
        onValueChange = {
            setApartment(it)
            setValidation(!it.contains(Regex("""[^0-9]""")))
                        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        label = {
            Text(
                text = stringResource(id = R.string.fragment_add_address_apartment),
                style = MaterialTheme.typography.bodyLarge
            )},
        isError = !isValidateText,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            errorContainerColor = MaterialTheme.colorScheme.errorContainer,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        maxLines = 1
    )
}

@Composable
fun SetFloor(floor: String, setFloor: (String) -> Unit, isValidateText: Boolean = true, setValidation: (Boolean) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        value = floor,
        onValueChange = {
            setFloor(it)
            setValidation(!it.contains(Regex("""[^0-9]""")))
                        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        label = {
            Text(
                text = stringResource(id = R.string.fragment_add_address_floor),
                style = MaterialTheme.typography.bodyLarge
            )},
        isError = !isValidateText,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            errorContainerColor = MaterialTheme.colorScheme.errorContainer,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        maxLines = 1
    )
}

@Composable
fun SetNotice(notice: String, setNotice: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(4.dp),
        value = notice,
        onValueChange = {setNotice(it)},
        label = {
            Text(
                text = stringResource(id = R.string.fragment_add_address_notice),
                style = MaterialTheme.typography.bodyLarge
            )},
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun SetAltContact(contact: String, setContact: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        value = contact,
        onValueChange = {setContact(it)},
        label = {
            Text(
                text = stringResource(id = R.string.fragment_add_address_contact),
                style = MaterialTheme.typography.bodyLarge
            )},
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun SaveAddressDialog(isVisibleDialog: Boolean, setVisible: (Boolean) -> Unit, saveAddress: () -> Unit) {
    if (isVisibleDialog) {
        AlertDialog(
            icon = {
                Icon(imageVector = Icons.Filled.Info, contentDescription = "", tint = Blue500)
            },
            title = {
                Text(text = stringResource(id = R.string.save_address_quest_text))
            },
            onDismissRequest = { setVisible(false) },
            dismissButton = {
                 TextButton(onClick = {
                     setVisible(false)
                 }) {
                     Text(text = stringResource(id = R.string.general_no), color = Blue500)
                 }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        setVisible(false)
                        saveAddress()
                    }) {
                    Text(text = stringResource(id = R.string.general_yes), color = Blue500)
                }
            }
        )
    }
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
    var isVisibleDialog by remember {
        mutableStateOf(false)
    }
    var isValidate by rememberSaveable {
        mutableStateOf(false)
    }
    val scrollState = rememberScrollState()
    YourWaterTheme {
        Column(modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(scrollState)) {
            TitleAddAddress(modifier = modifier.padding(8.dp))
            SaveAddressDialog(isVisibleDialog = isVisibleDialog, setVisible = {isVisibleDialog = !isVisibleDialog}, saveAddress = {})
            Box {
                Column {
                    SetRegion(regionList = listRegion, selectRegion = selectRegion, expandedRegion = expandedRegion, setExpandedRegion = {expandedRegion = !expandedRegion}, setRegion = {
                        selectRegion = it
                    })
                    SetCity(city = city, setCity = {city = it}, setValidation = {isValidate = it})
                    SetStreet(street = street, setStreet = {street = it}, setValidation = {isValidate = it})
                }
            }
            Box {
                Column {
                    Row (modifier = modifier.fillMaxWidth()) {
                        Box(modifier.weight(1f)) {
                            SetHome(home = home, setHome = {home = it}, setValidation = {isValidate = it})
                        }
                        Box(modifier.weight(1f)) {
                            SetStructure(structure = structure, setStructure = {structure = it}, setValidation = {})
                        }
                    }
                    Row (modifier = modifier.fillMaxWidth()) {
                        Box(modifier.weight(1f)) {
                            SetEntrance(entrance = entrance, setEntrance = {entrance = it}, setValidation = {})
                        }
                        Box(modifier.weight(1f)) {
                            SetApartment(apartment = apartment, setApartment = {apartment = it}, setValidation = {})
                        }
                        Box(modifier.weight(1f)) {
                            SetFloor(floor = floor, setFloor = {floor = it}, setValidation = {})
                        }
                    }
                    Box {
                        SetAltContact(contact = contact, setContact = {contact = it})
                    }
                    Box {

                    }
                    Column {
                        SetNotice(notice = notice, setNotice = {notice = it})
                        ButtonEnter(text = stringResource(id = R.string.fragment_create_order_add_address_to_order), isEnabledButton = true) {
                            isVisibleDialog = true
                        }
                    }
                }

            }



        }
    }
}