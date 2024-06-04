package com.qnecesitas.retentienda.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.qnecesitas.retentienda.data.Store

@Dao
interface StoreDao {

    @Query("SELECT * FROM Store ")
    suspend fun fetchStore(): MutableList<Store>

    @Insert
    suspend fun insertStore(store: Store)

    @Query("UPDATE Store SET name=:name WHERE id=:id ")
    suspend fun updateStore(id:Int,name:String)

    @Query("DELETE FROM Store WHERE id=:id")
    suspend fun deleteStore(id: Int)
}