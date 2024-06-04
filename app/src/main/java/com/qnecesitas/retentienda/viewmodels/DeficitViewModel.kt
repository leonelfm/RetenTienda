package com.qnecesitas.retentienda.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qnecesitas.retentienda.data.Product
import com.qnecesitas.retentienda.database.ProductDao
import com.qnecesitas.retentienda.database.StoreDao

class DeficitViewModel(private val productDao: ProductDao): ViewModel() {

    //List deficit
    private val _listDeficit = MutableLiveData<MutableList<Product>>()
    val listDeficit: LiveData<MutableList<Product>> get() = _listDeficit

    //List deficit filter
    private val _listDeficitFilter = MutableLiveData<MutableList<Product>>()
    val listDeficitFilter: LiveData<MutableList<Product>> get() = _listDeficitFilter


    suspend fun updateAmount(
        code: String ,
        amount: Int
    ) {
        productDao.updateAmount(
            code ,
            amount
        )
    }

    suspend fun getAllDeficit() {
        productDao.getDeficit().collect {
            _listDeficit.postValue(it as MutableList<Product>?)
        }
    }

    fun filterByTextDeficit(text: String) {
        if (text.trim().isNotEmpty()) {
            val filterList = _listDeficit.value?.filter {
                it.name.contains(text , ignoreCase = true)

            }?.toMutableList()

            if (filterList != null) {
                _listDeficitFilter.postValue(filterList!!)
            }

        } else {
            _listDeficitFilter.postValue(listDeficit.value)
        }
    }
}
class DeficitViewModelFactory(
    private val productDao: ProductDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeficitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeficitViewModel(productDao) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}