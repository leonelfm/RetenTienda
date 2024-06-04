package com.qnecesitas.retentienda.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qnecesitas.retentienda.data.Product
import com.qnecesitas.retentienda.database.ProductDao
import kotlinx.coroutines.launch

class EditProductViewModel(private val productDao: ProductDao):ViewModel() {

    private val _listProduct = MutableLiveData<MutableList<Product>>()
    val listProduct: LiveData<MutableList<Product>> get() = _listProduct


    suspend fun getProduct(id:String){
        viewModelScope.launch {
            _listProduct.value=productDao.getProduct(id)
        }
    }
    suspend fun selectDuplicate(code: String): Boolean {
        return productDao.selectDuplicate(code).isNotEmpty()
    }

    suspend fun updateProduct(
        code: String ,
        name: String ,
        image: String ,
        brand: String ,
        amount: Int ,
        buyPrice: Double ,
        salePrice: Double ,
        deficit: Int ,
        descr: String ,
    ) {
        productDao.updateProduct(
            code ,
            name ,
            image ,
            brand ,
            amount ,
            buyPrice ,
            salePrice ,
            deficit ,
            descr
        )


    }

}
class EditProductViewModelFactory(
    private val productDao: ProductDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditProductViewModel(productDao) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}