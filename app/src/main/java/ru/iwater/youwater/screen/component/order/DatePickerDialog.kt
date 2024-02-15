package ru.iwater.youwater.screen.component.order

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.iwater.youwater.R
import ru.iwater.youwater.theme.Blue500
import ru.iwater.youwater.vm.OrderViewModel
import ru.iwater.youwater.vm.WatterViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDatePickerDialog(showDatePicker: Boolean, watterViewModel: OrderViewModel, setShowDatePicker: (Boolean) -> Unit, calendar: Calendar, setDateOrder: (String) -> Unit) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = null,
        selectableDates = object : SelectableDates {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val calendar1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar1.timeInMillis = utcTimeMillis
                return watterViewModel.disableDays(utcTimeMillis) &&
                        (utcTimeMillis >= watterViewModel.getStartDate(calendar).timeInMillis)
            }
        }
    )
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                setShowDatePicker(false)
            },
            confirmButton = {
                TextButton(onClick = {
                    setShowDatePicker(false)
                    if (datePickerState.selectedDateMillis != null) {
                        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))
                        watterViewModel.getTimeList(
                            datePickerState.selectedDateMillis ?: 0, watterViewModel.getStartDate(
                                Calendar.getInstance()
                            )
                        )
                        setDateOrder(
                            formatter.format(
                                Date(
                                    datePickerState.selectedDateMillis ?: 0
                                )
                            )
                        )
                    }
                }) {
                    Text(text = "Выбрать")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    setShowDatePicker(false)
                }) {
                    Text(text = "Отмена")
                }
            },
        ) {
            DatePicker(
                state = datePickerState,
                title =  {
                    Text(
                        text = stringResource(id = R.string.get_date_order_text),
                        modifier = Modifier.padding(
                            PaddingValues(
                                start = 24.dp,
                                end = 12.dp,
                                top = 16.dp
                            )
                        )
                    )
                },
                showModeToggle = false,
//                colors = DatePickerDefaults.colors(
//                    containerColor = Color.White,
//                    selectedDayContainerColor = Blue500,
//                    selectedDayContentColor = Color.White,
//                    todayDateBorderColor = Blue500,
//                    todayContentColor = Blue500
//                )
            )
        }
    }
}