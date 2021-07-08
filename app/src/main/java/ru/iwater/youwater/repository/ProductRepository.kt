package ru.iwater.youwater.repository

import ru.iwater.youwater.domain.Product
import ru.iwater.youwater.utils.Generator
import javax.inject.Inject

class ProductRepository @Inject constructor() {

    fun getProductList(generator: Generator): List<Product> {
        return generator.getProduct()
    }
}