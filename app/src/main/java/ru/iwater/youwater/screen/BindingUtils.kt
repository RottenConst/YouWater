package ru.iwater.youwater.screen

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.iwater.youwater.R
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.data.TypeProduct
import ru.iwater.youwater.screen.adapters.AdapterCatalogList
import ru.iwater.youwater.screen.adapters.AdapterProductList

/**
 * имя товара
 */
@BindingAdapter("nameProduct")
fun TextView.bindNameProduct(product: Product) {
    text = product.name
}

/**
 * отображение стоимости товара
 */
@BindingAdapter("costProduct")
fun TextView.bindCostProduct(product: Product) {
    if (product.price.isNullOrBlank()) {
        text = ""
    } else {
        val price = product.price.split(";")[0].split(":")[1]
        "от ${price}р".also { text = it }
    }
}

/**
 * подгрузка картинок товаров
 */
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