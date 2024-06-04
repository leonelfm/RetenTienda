package com.qnecesitas.retentienda.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qnecesitas.retentienda.data.Store
import com.qnecesitas.retentienda.database.FileDao
import com.qnecesitas.retentienda.database.ProductDao
import com.qnecesitas.retentienda.database.StoreDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StoreViewModel(private val storeDao: StoreDao,private val productDao: ProductDao,private val fileDao: FileDao): ViewModel() {

    // List store
    private val _listStore = MutableLiveData<MutableList<Store>>()
    val listStore: LiveData<MutableList<Store>> get() = _listStore

    // List store filter
    private val _listStoreFilter = MutableLiveData<MutableList<Store>>()
    val listStoreFilter: LiveData<MutableList<Store>> get() = _listStoreFilter

    fun getAllStore() {
        viewModelScope.launch(Dispatchers.IO) {
            val stores = storeDao.fetchStore()
            _listStore.postValue(stores)
        }
    }

    fun insertStore(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val store = Store(0, name)
            storeDao.insertStore(store)
            getAllStore() // Update list after inserting
        }
    }

    fun editStore(id: Int, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storeDao.updateStore(id, name)
            getAllStore() // Update list after editing
        }
    }

    fun deleteStore(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            storeDao.deleteStore(id)
            getAllStore() // Update list after deleting
        }
    }

    fun filterByTextStore(text: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val filterList = if (text.trim().isNotEmpty()) {
                _listStore.value?.filter {
                    it.name.contains(text, ignoreCase = true)
                }?.toMutableList()
            } else {
                _listStore.value
            }

            _listStoreFilter.postValue(filterList ?: mutableListOf())
        }
    }
    fun deleteProduct(store:String){
        viewModelScope.launch {
            productDao.deleteProductStore(store)
        }
    }

    fun deleteFile(store:String){
        viewModelScope.launch {
            fileDao.deleteFileStore(store)
        }
    }
    fun editProductStore(storeOld: String,storeNew: String) {
        viewModelScope.launch(Dispatchers.IO) {
            productDao.updateProductStore(storeOld,storeNew)
        }
    }
    fun editFileStore(storeOld: String,storeNew: String) {
        viewModelScope.launch(Dispatchers.IO) {
            fileDao.updateFileStore(storeOld,storeNew)
        }
    }
}

class StoreViewModelFactory(private val storeDao: StoreDao,private val productDao: ProductDao,private val fileDao: FileDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoreViewModel(storeDao,productDao,fileDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
