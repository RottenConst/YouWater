package ru.iwater.youwater.screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.R
import java.util.zip.Inflater

class CatalogWaterAdapter() : RecyclerView.Adapter<CatalogWaterAdapter.CatalogWaterHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogWaterHolder {
        return CatalogWaterHolder(LayoutInflater.from(parent.context), parent, R.layout.item_card_product)
    }

    override fun onBindViewHolder(holder: CatalogWaterHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    inner class CatalogWaterHolder(inflater: LayoutInflater, parent: ViewGroup, resource: Int) :
            RecyclerView.ViewHolder(inflater.inflate(resource, parent, false)) {

            }
}