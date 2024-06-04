package com.qnecesitas.retentienda.database

import androidx.room.Dao
import androidx.room.Query
import com.qnecesitas.retentienda.data.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM Product WHERE code = :code")
    suspend fun selectDuplicate(code: String): List<Product>

    @Query("INSERT INTO Product VALUES (:code,:name,:image,:brand,:amount,:buyPrice,:salePrice,:deficit,:descr,:store,:file)")
    suspend fun insertProduct(
        code: String ,
        name: String ,
        image: String,
        brand: String,
        amount: Int ,
        buyPrice: Double ,
        salePrice: Double ,
        deficit: Int ,
        descr: String ,
        store: String,
        file:String
    )

    @Query("SELECT * FROM Product WHERE code = :code")
    suspend fun getProduct(code: String): MutableList<Product>

    @Query("UPDATE Product SET code=:code, name=:name, image=:image, brand=:brand, amount=:amount,buyPrice=:buyPrice,salePrice =:salePrice, deficit=:deficit, ` descr`=:descr WHERE code=:code ")
    suspend fun updateProduct(
        code: String ,
        name: String ,
        image: String,
        brand: String,
        amount: Int ,
        buyPrice: Double ,
        salePrice: Double ,
        deficit: Int ,
        descr: String
    )
    @Query("UPDATE Product SET amount=:amount WHERE code=:code")
    suspend fun updateAmount(code: String , amount: Int)

    @Query("SELECT * FROM Product WHERE amount <= deficit")
    fun getDeficit(): Flow<List<Product>>

    @Query("SELECT * FROM Product WHERE store=:store AND file=:file ")
    suspend fun getProductStore(store: String,file:String): MutableList<Product>

    @Query("DELETE FROM Product WHERE code=:code")
    suspend fun deleteProduct(code: String)

    @Query("SELECT COUNT(*) FROM Product WHERE amount <= deficit")
    suspend fun getDeficitCount(): Int

    @Query("SELECT * FROM Product")
    suspend fun getAllProduct(): MutableList<Product>

    @Query("DELETE FROM Product WHERE store=:store")
    suspend fun deleteProductStore(store: String)

    @Query("UPDATE Product SET store=:storeNew WHERE store=:storeOld")
    suspend fun updateProductStore(storeOld:String,storeNew:String)

    @Query("UPDATE Product SET file=:fileNew WHERE file=:fileOld AND store=:store ")
    suspend fun updateProductFile(fileOld:String,fileNew:String,store: String)

    @Query("DELETE FROM Product WHERE file=:file AND store=:store")
    suspend fun deleteProductFile(file: String,store: String)
}