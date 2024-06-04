package com.qnecesitas.retentienda.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qnecesitas.retentienda.database.ProductDao

class NewProductViewModel(private val productDao: ProductDao):ViewModel() {

    suspend fun selectDuplicate(code: String): Boolean {
        return productDao.selectDuplicate(code).isNotEmpty()
    }

    suspend fun addProduct(
        code: String ,
        name: String ,
        image: String ,
        brand: String ,
        amount: Int ,
        buyPrice: Double ,
        salePrice: Double ,
        deficit: Int ,
        descr: String ,
        store: String ,
        file:String,

        ) {
        productDao.insertProduct(
            code ,
            name ,
            image ,
            brand ,
            amount ,
            buyPrice ,
            salePrice ,
            deficit ,
            descr ,
            store,
            file
        )
    }

}

class NewProductViewModelFactory(
    private val productDao: ProductDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewProductViewModel(productDao) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}