package ru.iwater.youwater.screen

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.iwater.youwater.R
import ru.iwater.youwater.domain.Product
import ru.iwater.youwater.domain.TypeProduct
import ru.iwater.youwater.screen.adapters.CatalogWaterAdapter

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

@BindingAdapter("imageUrl")
fun bindImageProduct(imgView: ImageView, file: String?) {
    val imgUrl = "http://dev.iwatercrm.ru/images/$file"
    imgUrl.let {
        val imgUrl = imgUrl.toUri().buildUpon().build()
        Glide.with(imgView.context)
            .load(imgUrl)
            .apply(RequestOptions()
                .placeholder(R.drawable.ic_youwater_logo)
                .error(R.mipmap.product_image))
            .into(imgView)
    }

}

@BindingAdapter("labelType")
fun TextView.bindLabelType(typeProduct: TypeProduct) {
    text = typeProduct.category
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Product>?) {
    val adapter = recyclerView.adapter as CatalogWaterAdapter
    adapter.submitList(data)

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