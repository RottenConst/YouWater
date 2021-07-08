package ru.iwater.youwater.screen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_card_product.view.*
import ru.iwater.youwater.R
import ru.iwater.youwater.domain.Product

class CatalogWaterAdapter(
    val productsList: MutableList<Product> = mutableListOf()
) : RecyclerView.Adapter<CatalogWaterAdapter.CatalogWaterHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogWaterHolder {
        return CatalogWaterHolder(LayoutInflater.from(parent.context), parent, R.layout.item_card_product)
    }

    override fun onBindViewHolder(holder: CatalogWaterHolder, position: Int) {
        holder.bindCardProduct(productsList[position])
    }

    override fun getItemCount(): Int = productsList.size

    inner class CatalogWaterHolder(inflater: LayoutInflater, parent: ViewGroup, resource: Int) :
            RecyclerView.ViewHolder(inflater.inflate(resource, parent, false)) {

                fun bindCardProduct(product: Product) {
                    itemView.tv_name_product.text = product.name
                    itemView.tv_cost_product.text = "от ${product.cost}р"

                    Glide.with(itemView.context)
                        .load(R.mipmap.product_image)
                        .into(itemView.iv_product)
                }

            }
}