package ru.iwater.youwater.screen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_catalog.view.*
import ru.iwater.youwater.R
import ru.iwater.youwater.domain.TypeProduct
import ru.iwater.youwater.screen.adapters.AdapterCatalogList.*

class AdapterCatalogList(
    private val typesProductList: List<TypeProduct> = listOf(
        TypeProduct("Питьевая вода"),
        TypeProduct("Сопутствующие товары"),
        TypeProduct("Одноразовая посуда"),
        TypeProduct("Помпы для воды"),
        TypeProduct("Кулеры для воды"),
        TypeProduct("Оборудование")
    )
): RecyclerView.Adapter<HolderCatalogList>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCatalogList {
        return HolderCatalogList(LayoutInflater.from(parent.context), parent, R.layout.item_catalog)
    }

    override fun onBindViewHolder(holder: HolderCatalogList, position: Int) {
        holder.bindCatalog(typesProductList[position], position)
    }

    override fun getItemCount(): Int = typesProductList.size

    inner class HolderCatalogList(inflater: LayoutInflater, parent: ViewGroup, resource: Int) :
            RecyclerView.ViewHolder(inflater.inflate(resource, parent, false)) {

                fun bindCatalog(typeProduct: TypeProduct, position: Int) {
                    itemView.tv_label_type_product.text = typeProduct.label
                    if (position == 0) {
                        Glide.with(itemView.context)
                            .load(R.drawable.water)
                            .into(itemView.iv_icon_product)
                    }
                    if (position == 1) {
                        Glide.with(itemView.context)
                            .load(R.drawable.other)
                            .into(itemView.iv_icon_product)
                    }
                    if (position == 2) {
                        Glide.with(itemView.context)
                            .load(R.drawable.tableware)
                            .into(itemView.iv_icon_product)
                    }
                    if (position == 3) {
                        Glide.with(itemView.context)
                            .load(R.drawable.pump)
                            .into(itemView.iv_icon_product)
                    }
                    if (position == 4) {
                        Glide.with(itemView.context)
                            .load(R.drawable.cooler)
                            .into(itemView.iv_icon_product)
                    }
                    if (position == 5) {
                        Glide.with(itemView.context)
                            .load(R.drawable.equipment)
                            .into(itemView.iv_icon_product)
                    }
                }
            }
}