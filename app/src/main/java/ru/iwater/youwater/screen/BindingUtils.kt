package ru.iwater.youwater.screen

import android.graphics.Paint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.iwater.youwater.R
import ru.iwater.youwater.data.*
import ru.iwater.youwater.screen.adapters.AdapterCatalogList
import ru.iwater.youwater.screen.adapters.AdapterProductList
import ru.iwater.youwater.screen.adapters.OrderProductAdapter
import timber.log.Timber
import java.text.SimpleDateFormat

/**
 * имя товара
 */
@BindingAdapter("nameProduct")
fun TextView.bindNameProduct(product: Product?) {
    text = product?.name
}

/**
 * отображение стоимости товара
 */
@BindingAdapter("costProduct")
fun TextView.bindCostProduct(product: Product?) {
    if (product != null) {
        when (product.id) {
            81 -> {
                if (product.price.isNotEmpty()) {
                    val price = product.price.split(";")[0].split(":")[1]
                    "от ${price.toInt() - 15}₽".also { text = it }
                }
            }
            84 -> {
                if (product.price.isNotEmpty()) {
                    val price = product.price.split(";")[0].split(":")[1]
                    "от ${price.toInt() - 15}₽".also { text = it }
                }
            }
            else -> {
                if (product.price.isNotEmpty() && product.price.length > 4) {
                    val price = product.price.split(";")[0].split(":")[1]
                    "от ${price}₽".also { text = it }
                }
            }
        }
    }
}

@BindingAdapter("costProductNoDiscount")
fun TextView.bindCostNoDiscount(product: Product?) {
    if (product != null) {
        when (product.id) {
            81 -> {
                visibility = View.VISIBLE
                paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                if (product.price.isNotEmpty()) {
                    val price = product.price.split(";")[0].split(":")[1]
                    "от ${price.toInt()}₽".also { text = it }
                }
            }
            84 -> {
                visibility = View.VISIBLE
                paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                if (product.price.isNotEmpty()) {
                    val price = product.price.split(";")[0].split(":")[1]
                    "от ${price.toInt()}₽".also { text = it }
                }
            }
            else -> {
                visibility = View.GONE
            }
        }
    }
}

@BindingAdapter("setCostNoDiscount")
fun TextView.bindNoDiscount(product: Product?) {
    if (product != null) {
        when (product.id) {
            81 -> {
                visibility = View.VISIBLE
                val prices = product.price.removeSuffix(";")
                val priceList = prices.split(";")
                val count = product.count
                var price = 0
                priceList.forEach {
                    Timber.d("COUNT = $count")
                    val priceCount = it.split(":")
                    if (priceCount[0].toInt() <= count) {
                        Timber.d("Place = ${priceCount[1].toInt()}")
                        price = priceCount[1].toInt() * count
                    }
                }
                "$price₽".also { text = it }
            }
            84 -> {
                visibility = View.VISIBLE
                val prices = product.price.removeSuffix(";")
                val priceList = prices.split(";")
                val count = product.count
                var price = 0
                priceList.forEach {
                    Timber.d("COUNT = $count")
                    val priceCount = it.split(":")
                    if (priceCount[0].toInt() <= count) {
                        Timber.d("Place = ${priceCount[1].toInt()}")
                        price = priceCount[1].toInt() * count
                    }
                }
                "$price₽".also { text = it }
            }
            else -> {
                visibility = View.GONE
            }
        }
    }
}

@BindingAdapter("setCostProduct")
fun TextView.bindPriceProduct(product: Product?) {
    if (!product?.price.isNullOrEmpty()) {
        val prices = product?.price?.removeSuffix(";")
        Timber.d("PRICES $prices ${product?.count}")
        val priceList = prices?.split(";")
        val count = product?.count ?: 0
        var price = 0
        Timber.d("COUNT $count PRICE $price")
        when (product?.id) {
            81 -> {
                priceList?.forEach {
                    Timber.d("COUNT = $count")
                    val priceCount = it.split(":")
                    if (priceCount[0].toInt() <= count) {
                        Timber.d("Place = ${priceCount[1].toInt()}")
                        price = (priceCount[1].toInt() - 15) * count
                    }
                }
            }
            84 -> {
                priceList?.forEach {
                    Timber.d("COUNT = $count")
                    val priceCount = it.split(":")
                    if (priceCount[0].toInt() <= count) {
                        Timber.d("Place = ${priceCount[1].toInt()}")
                        price = (priceCount[1].toInt() - 15) * count
                    }
                }
            }
            else -> {
                priceList?.forEach {
                    Timber.d("COUNT = $count")
                    val priceCount = it.split(":")
                    if (priceCount[0].toInt() <= count) {
                        Timber.d("Place = ${priceCount[1].toInt()}")
                        price = priceCount[1].toInt() * count
                    }
                }
            }
        }
        "$price₽".also { text = it }
    }
}

@BindingAdapter("setPriceProduct")
fun TextView.bindBtnPriceProduct(product: Product?) {
    if (product == null) {
        text = ""
    } else {
        val prices = product.price.removeSuffix(";")
        val priceList = prices.split(";")
        val count = product.count
        var price = 0
        when (product.id) {
            81 -> {
                priceList.forEach {
                    Timber.d("COUNT = $count")
                    val priceCount = it.split(":")
                    if (priceCount[0].toInt() <= count) {
                        Timber.d("Place = ${priceCount[1].toInt()}")
                        price = (priceCount[1].toInt() - 15) * count
                    }
                }
            }
            84 -> {
                priceList.forEach {
                    Timber.d("COUNT = $count")
                    val priceCount = it.split(":")
                    if (priceCount[0].toInt() <= count) {
                        Timber.d("Place = ${priceCount[1].toInt()}")
                        price = (priceCount[1].toInt() - 15) * count
                    }
                }
            }
            else -> {
                priceList.forEach {
                    Timber.d("COUNT 1 = $count")
                    val priceCount = it.split(":")
                    if (priceCount[0].toInt() <= count) {
                        Timber.d("Place 1 = ${priceCount[1].toInt()}")
                        price = priceCount[1].toInt() * count
                    }
                }
            }
        }
        "Добавить за ${price}₽".also { text = it }
    }
}

@BindingAdapter("countProduct")
fun TextView.bindCountProduct(product: Product?) {
    text = product?.count.toString()
}

@BindingAdapter("setCountProduct")
fun TextView.bindCount(product: Product?) {
    "${product?.count}шт.".also { text = it }
}

@BindingAdapter("setAboutProduct")
fun TextView.bindAboutProduct(product: Product?) {
    text = product?.about ?: ""
}

/**
 * подгрузка картинок товаров
 */
@BindingAdapter("imageUrl")
fun bindImageProduct(imgView: ImageView, file: String?) {
    val imgUrl = "https://dev.new.iwatercrm.ru/iwatercrm/images/$file" //test
//    val imgUrl = "https://crm.new.iwatercrm.ru/iwatercrm/images/$file" //prod
    imgUrl.let {
        val imgUrl = imgUrl.toUri().buildUpon().build()
        Glide.with(imgView.context)
            .load(imgUrl)
            .apply(RequestOptions()
                .placeholder(R.drawable.ic_youwater_logo)
                .error(R.drawable.ic_youwater_logo))
            .into(imgView)
    }

}

@BindingAdapter("setAddress")
fun TextView.bindingAddress(address: Address?) {
    if (address != null) {
        if (address.note.isNullOrEmpty()) {
            when {
                address.building == "" -> {
                    if (address.entrance == null && address.floor == null && address.flat == null) {
                        "${address.region} ул.${address.street} д.${address.house}".also { text = it }
                    } else if (address.entrance == null && address.floor == null && address.flat != null) {
                        "${address.region} ул.${address.street} д.${address.house} кв.${address.flat}".also { text = it }
                    } else if (address.entrance == null && address.floor != null && address.flat == null){
                        "${address.region} ул.${address.street} д.${address.house} этаж${address.floor}".also { text = it }
                    } else if (address.entrance != null && address.floor == null && address.flat == null) {
                        "${address.region} ул.${address.street} д.${address.house} подьезд ${address.entrance}".also { text = it }
                    } else if (address.entrance == null && address.floor != null && address.flat != null) {
                        "${address.region} ул.${address.street} д.${address.house} этаж${address.floor} кв.${address.flat}".also { text = it }
                    } else if (address.entrance != null && address.floor == null && address.flat != null) {
                        "${address.region} ул.${address.street} д.${address.house} подьезд ${address.entrance} кв.${address.flat}"
                    }
                }
                address.entrance == null -> {
                    if (address.building.isNullOrEmpty() && address.floor == null && address.flat == null) {
                        "${address.region} ул.${address.street} д.${address.house}".also { text = it }
                    } else if (address.building.isNullOrEmpty() && address.floor == null && address.flat != null) {
                        "${address.region} ул.${address.street} д.${address.house} кв.${address.flat}".also { text = it }
                    } else if (address.building.isNullOrEmpty() && address.floor != null && address.flat == null) {
                        "${address.region} ул.${address.street} д.${address.house} этаж${address.floor}".also { text = it }
                    } else if (!address.building.isNullOrEmpty() && address.floor == null && address.flat == null) {
                        "${address.region} ул.${address.street} д.${address.house} ст. ${address.building}".also { text = it }
                    } else if (!address.building.isNullOrEmpty() && address.floor != null && address.flat == null) {
                        "${address.region} ул.${address.street} д.${address.house} ст. ${address.building} этаж${address.floor}".also { text = it }
                    } else if (!address.building.isNullOrEmpty() && address.floor == null && address.flat != null) {
                        "${address.region} ул.${address.street} д.${address.house} ст. ${address.building} кв. ${address.flat}".also { text = it }
                    }
                }
                address.floor == null -> {
                    if (address.flat == null && address.building.isNullOrEmpty()) {
                        "${address.region} ул.${address.street} д.${address.house} подьезд ${address.entrance}".also { text = it }
                    } else if (address.flat == null && !address.building.isNullOrEmpty()){
                        "${address.region} ул.${address.street} д.${address.house} ст. ${address.building} подьезд ${address.entrance}".also { text = it }
                    } else if (address.flat != null && address.building.isNullOrEmpty()) {
                        "${address.region} ул.${address.street} д.${address.house} подьезд ${address.entrance} кв.${address.flat}".also { text = it }
                    } else if (address.flat != null && !address.building.isNullOrEmpty()) {
                        "${address.region} ул.${address.street} д.${address.house} ст. ${address.building} подьезд ${address.entrance} кв.${address.flat}".also { text = it }
                    }
                }
                address.flat == null -> {
                    "${address.region}, ул.${address.street}, д.${address.house}, подьезд ${address.entrance}, этаж ${address.floor}".also {
                        text = it
                    }
                }
                else -> {
                    "${address.region}, ул.${address.street}, д.${address.house}, подьезд ${address.entrance}, этаж ${address.floor}, кв.${address.flat} ".also {
                        text = it
                    }
                }
            }
        } else {
            when {
                address.building == null -> {
                    if (address.entrance == null && address.floor == null && address.flat == null) {
                        "${address.region} ул.${address.street} д.${address.house} Примичание: ${address.note}".also { text = it }
                    } else if (address.entrance == null && address.floor == null && address.flat != null) {
                        "${address.region} ул.${address.street} д.${address.house} кв.${address.flat} Примичание: ${address.note}".also { text = it }
                    } else if (address.entrance == null && address.floor != null && address.flat == null){
                        "${address.region} ул.${address.street} д.${address.house} этаж${address.floor} Примичание: ${address.note}".also { text = it }
                    } else if (address.entrance != null && address.floor == null && address.flat == null) {
                        "${address.region} ул.${address.street} д.${address.house} подьезд ${address.entrance} Примичание: ${address.note}".also { text = it }
                    } else if (address.entrance == null && address.floor != null && address.flat != null) {
                        "${address.region} ул.${address.street} д.${address.house} этаж${address.floor} кв.${address.flat} Примичание: ${address.note}".also { text = it }
                    } else if (address.entrance != null && address.floor == null && address.flat != null) {
                        "${address.region} ул.${address.street} д.${address.house} подьезд ${address.entrance} кв.${address.flat} Примичание: ${address.note}"
                    }
                }
                address.entrance == null -> {
                    if (address.building.isNullOrEmpty() && address.floor == null && address.flat == null) {
                        "${address.region} ул.${address.street} д.${address.house} Примичание: ${address.note}".also { text = it }
                    } else if (address.building.isNullOrEmpty() && address.floor == null && address.flat != null) {
                        "${address.region} ул.${address.street} д.${address.house} кв.${address.flat} Примичание: ${address.note}".also { text = it }
                    } else if (address.building.isNullOrEmpty() && address.floor != null && address.flat == null) {
                        "${address.region} ул.${address.street} д.${address.house} этаж${address.floor} Примичание: ${address.note}".also { text = it }
                    } else if (!address.building.isNullOrEmpty() && address.floor == null && address.flat == null) {
                        "${address.region} ул.${address.street} д.${address.house} ст. ${address.building} Примичание: ${address.note}".also { text = it }
                    } else if (!address.building.isNullOrEmpty() && address.floor != null && address.flat == null) {
                        "${address.region} ул.${address.street} д.${address.house} ст. ${address.building} этаж${address.floor} Примичание: ${address.note}".also { text = it }
                    } else if (!address.building.isNullOrEmpty() && address.floor == null && address.flat != null) {
                        "${address.region} ул.${address.street} д.${address.house} ст. ${address.building} кв. ${address.flat} Примичание: ${address.note}".also { text = it }
                    }
                }
                address.floor == null -> {
                    if (address.flat == null && address.building.isNullOrEmpty()) {
                        "${address.region} ул.${address.street} д.${address.house} подьезд ${address.entrance} Примичание: ${address.note}".also { text = it }
                    } else if (address.flat == null && !address.building.isNullOrEmpty()){
                        "${address.region} ул.${address.street} д.${address.house} ст. ${address.building} подьезд ${address.entrance} Примичание: ${address.note}".also { text = it }
                    } else if (address.flat != null && address.building.isNullOrEmpty()) {
                        "${address.region} ул.${address.street} д.${address.house} подьезд ${address.entrance} кв.${address.flat} Примичание: ${address.note}".also { text = it }
                    } else if (address.flat != null && !address.building.isNullOrEmpty()) {
                        "${address.region} ул.${address.street} д.${address.house} ст. ${address.building} подьезд ${address.entrance} кв.${address.flat} Примичание: ${address.note}".also { text = it }
                    }
                }
                address.flat == null -> {
                    "${address.region}, ул.${address.street}, д.${address.house}, подьезд ${address.entrance}, этаж ${address.floor} Примичание: ${address.note}".also {
                        text = it
                    }
                }
                else -> {
                    "${address.region}, ул.${address.street}, д.${address.house}, подьезд ${address.entrance}, этаж ${address.floor}, кв.${address.flat} Примичание: ${address.note}".also {
                        text = it
                    }
                }
            }
        }
    }
}

/**
 * название категории
 */
@BindingAdapter("labelType")
fun TextView.bindLabelType(category: TypeProduct) {
    text = category.category
}

/**
 * инициализация адаптера и добавление списка продуктов
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Product>?) {
    if (data != null) {
        val adapter = recyclerView.adapter as AdapterProductList
        adapter.submitList(data)
    }
}

/**
 * инициализация адаптера и добавление списка категорий товаров
 */
@BindingAdapter("listCatalog")
fun bindCatalogList(recyclerView: RecyclerView, data: List<TypeProduct>?) {
    val adapter = recyclerView.adapter as AdapterCatalogList
    adapter.submitList(data)
}

@BindingAdapter("setNameClient")
fun TextView.bidingNameClient(clientName: String?) {
    text = clientName
}

@BindingAdapter("setNameAndLastnameClient")
fun TextView.bindingNameAndLastnameClient(client: Client?) {
    if (client != null) {
        text = client.name
    }
}

@BindingAdapter("setPhoneClient")
fun TextView.bindingPhoneClient(phone: String?) {
    text = phone
}

@BindingAdapter("setEmailClient")
fun TextView.bindingEmailClient(email: String?) {
    text = if (email != "NULL") email else ""
}

@BindingAdapter("setTanks")
fun TextView.bindTanksClient(clientTank: Int?) {
    "$clientTank шт.".also { text = it }
}

@BindingAdapter("setDateMyOrder")
fun TextView.bindDateMyOrder(dateOrder: String?) {
    if (dateOrder != null) {
        val date = dateOrder.split(";")[0]
        val period = dateOrder.split(";")[1]
        val format = SimpleDateFormat("dd.MM.yyyy")
        val unixDate = date.toLong() * 1000
        text = "${format.format(unixDate)}, $period"
    }


}

@BindingAdapter("setTypeCash")
fun TextView.bindTypeCash(typeCash: String?) {
    text = when (typeCash) {
        "0" -> {
            "Оплата наличными"
        }
        "2" -> {
            "Оплата по карте"
        }
        "4" -> {
            "Оплата по карте курьеру"
        }
        else -> {
            typeCash
        }
    }
}

@BindingAdapter("setIdOrder")
fun TextView.bindIdOrder(id: Int?) {
    if (id != null) text = id.toString()
}

@BindingAdapter("setOrderStatus")
fun TextView.bindOrderStatus(status: Int) {
    when {
        status == 0 -> {
            text = "Заказ принят"
        }
        status == 1 -> {
            text = "Передан в доставку"
        }
        status == 2 -> {
            text = "Заказ отменён"
        }
        status == 3 -> {
            text = "Заказ доставлен"
        }
        status == 4 -> {
            text = "Заказ перенесён"
        }
    }
}

@BindingAdapter("setRawAddress")
fun TextView.bindRawAddress(rawAddress: RawAddress?) {
    text = "${rawAddress?.fullAddress?.split(",")?.get(0)}, ${rawAddress?.factAddress}"
}

@BindingAdapter("setAddress")
fun TextView.bindAddressOrder(address: String?) {
    if (address != null) text = address
}

@BindingAdapter("setTotalCost")
fun TextView.bindTotalCost(totalCost: String?) {
    if (totalCost != null) text = totalCost
}

@BindingAdapter("setListMyOrdersProduct")
fun bindListMyOrders(recyclerView: RecyclerView, products: List<Product>?) {
    if (products != null) {
        val adapter = recyclerView.adapter as OrderProductAdapter
        adapter.submitList(products)
    }
}

@BindingAdapter("setNameBankCard")
fun TextView.bindNameBankCard(bankCard: BankCard?) {
    if (bankCard != null) {
        val nameCard = bankCard.numberCard.toString().removeRange(0, 12)
        "Банковская карта $nameCard".also { text = it }
    }
}