package ru.iwater.youwater.screen

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import ru.iwater.youwater.R
import ru.iwater.youwater.domain.Product
import ru.iwater.youwater.domain.TypeProduct

@BindingAdapter("nameProduct")
fun TextView.bindNameProduct(product: Product) {
    text = product.name
}

@BindingAdapter("costProduct")
fun TextView.bindCostProduct(product: Product) {
    if (product.price.isNullOrBlank()) {
        text = ""
    } else {
        val price = product.price.split(";")[0].split(":")[1]
        "от ${price}р".also { text = it }
    }
}

//fun ImageView.bindImageProduct() {
//    Glide.with()
//}

@BindingAdapter("labelType")
fun TextView.bindLabelType(typeProduct: TypeProduct) {
    text = typeProduct.label
}

@BindingAdapter("imageTypeProduct")
fun ImageView.bindImageTypeProduct(typeProduct: TypeProduct) {
    setImageResource(when (typeProduct.id) {
        0 -> R.drawable.water
        1 -> R.drawable.other
        2 -> R.drawable.tableware
        3 -> R.drawable.pump
        4 -> R.drawable.cooler
        5 -> R.drawable.equipment
        else -> R.color.white
    })
}