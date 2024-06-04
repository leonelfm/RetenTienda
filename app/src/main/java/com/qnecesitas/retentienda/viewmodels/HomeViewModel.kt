package com.qnecesitas.retentienda.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qnecesitas.retentienda.data.File
import com.qnecesitas.retentienda.data.Product
import com.qnecesitas.retentienda.data.Store
import com.qnecesitas.retentienda.data.StoreHome
import com.qnecesitas.retentienda.database.FileDao
import com.qnecesitas.retentienda.database.ProductDao
import com.qnecesitas.retentienda.database.StoreDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val storeDao: StoreDao,private val productDao: ProductDao,private val fileDao: FileDao): ViewModel() {

    //List store
    private val _listStore = MutableLiveData<MutableList<Store>>()
    private val listStore: LiveData<MutableList<Store>> get() = _listStore

    //List store
    private val _listStoreHome = MutableLiveData<MutableList<StoreHome>>()
    val listStoreHome: LiveData<MutableList<StoreHome>> get() = _listStoreHome

    //List product
    private val _listProduct = MutableLiveData<MutableList<Product>>()
    val listProduct: LiveData<MutableList<Product>> get() = _listProduct

    //List product
    private val _listFile = MutableLiveData<MutableList<File>>()
    val listFile: LiveData<MutableList<File>> get() = _listFile

    //List product
    private val _listAllProduct = MutableLiveData<MutableList<Product>>()
    val listAllProduct: LiveData<MutableList<Product>> get() = _listAllProduct

    //List product filter
    private val _listProductFilter = MutableLiveData<MutableList<Product>>()
    val listProductFilter: LiveData<MutableList<Product>> get() = _listProductFilter

    private val _storeSelected = MutableLiveData<MutableList<StoreHome>>()
    val storeSelected: LiveData<MutableList<StoreHome>> get() = _storeSelected

    private val _listAuxiliaryProduct = MutableLiveData<MutableList<Product>>()
    private val listAuxiliaryProduct: LiveData<MutableList<Product>> get() = _listAuxiliaryProduct

    private val _listAuxiliaryFile = MutableLiveData<MutableList<File>>()
    private val listAuxiliaryFile: LiveData<MutableList<File>> get() = _listAuxiliaryFile

    fun getAllInfo() {
        _storeSelected.value?.clear()
        val listStoreHomeAuxiliary = mutableListOf<StoreHome>()
        viewModelScope.launch {
            _listAllProduct.value = productDao.getAllProduct()
            _listStore.value = storeDao.fetchStore()
            if (listStore.value != null) {
                for ((index , store) in listStore.value!!.withIndex()) {
                    if (index == 0) {
                        listStoreHomeAuxiliary.add(
                            StoreHome(
                                store.id ,
                                store.name ,
                                "Seleccionado"
                            )
                        )
                        _storeSelected.value?.add(listStoreHomeAuxiliary[index])
                    } else {
                        listStoreHomeAuxiliary.add(
                            StoreHome(
                                store.id ,
                                store.name ,
                                "No seleccionado"
                            )
                        )
                    }

                }
            }
            _listStoreHome.value = listStoreHomeAuxiliary
            if (listStoreHome.value?.isNotEmpty() == true) {
                _listFile.value = fileDao.getFileStore(listStoreHome.value?.get(0)!!.name)
            }

        }
    }

    fun selectStore(store: StoreHome , position: Int) {
        viewModelScope.launch {
            val auxiliaryList = mutableListOf<StoreHome>()
            listStoreHome.value?.let { auxiliaryList.addAll(it) }
            if (storeSelected.value?.get(0)!!.id != store.id) {
                for (storeHome in auxiliaryList) {
                    if (storeHome.id == storeSelected.value!![0].id) {
                        storeHome.state = "No Seleccionado"
                    }

                }
                _storeSelected.value?.clear()
                auxiliaryList[position].state = "Seleccionado"
                _storeSelected.value?.add(auxiliaryList[position])
                _listStoreHome.value = auxiliaryList
                _listAuxiliaryFile.value = fileDao.getFileStore(store.name)
                if (listAuxiliaryFile.value?.isNotEmpty() == true) {
                    _listFile.value = listAuxiliaryFile.value
                } else {
                    val list = mutableListOf<File>()
                    _listFile.value = list
                }


            }


        }
    }

    suspend fun updateAmount(
        code: String ,
        amount: Int
    ) {
        productDao.updateAmount(
            code ,
            amount
        )
    }

    fun deleteProduct(
        code: String
    ) {
        viewModelScope.launch {
            productDao.deleteProduct(
                code
            )
        }

    }

    fun filterByTextDeficit(text: String) {
        if (text.trim().isNotEmpty()) {
            val filterList = _listAllProduct.value?.filter {
                it.name.contains(text , ignoreCase = true)

            }?.toMutableList()

            if (filterList != null) {
                _listProductFilter.postValue(filterList!!)
            }

        } else {
            _listProductFilter.postValue(listAllProduct.value)
        }
    }

    suspend fun getDeficitCount(): Int {
        return productDao.getDeficitCount()
    }

    init {
        _storeSelected.value = mutableListOf()
        viewModelScope.launch {
            _listAllProduct.value = productDao.getAllProduct()
        }
    }

    fun getProduct(store: String , file: String) {
        viewModelScope.launch {
            _listProduct.value = productDao.getProductStore(store , file)
        }
    }

    fun insertFile(file: String , store: String) {
        viewModelScope.launch {
            val fileNew = File(0 , file , store)
            fileDao.insertFile(fileNew)
        }

    }

    fun editFile(id: Int , file: String) {
        viewModelScope.launch {
            fileDao.updateFile(id , file)
        }
    }

    fun editProductFile(fileOld: String , fileNew: String,store: String) {
        viewModelScope.launch(Dispatchers.IO) {
            productDao.updateProductFile(fileOld , fileNew,store)
        }
    }

    fun deleteProductFile(file:String,store: String){
        viewModelScope.launch {
            productDao.deleteProductFile(file,store)
        }
    }

    fun deleteFile(id: Int){
        viewModelScope.launch {
            fileDao.deleteFile(id)
        }
    }

}
class HomeViewModelFactory(
    private val storeDao: StoreDao,
    private val productDao: ProductDao,
    private val fileDao: FileDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(storeDao,productDao,fileDao) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}