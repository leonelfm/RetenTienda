package com.qnecesitas.retentienda.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.qnecesitas.retentienda.data.File
import com.qnecesitas.retentienda.data.Store

@Dao
interface FileDao {

    @Query("SELECT * FROM File WHERE store=:name ")
    suspend fun getFileStore(name: String): MutableList<File>

    @Query("UPDATE File SET store=:storeNew WHERE store=:storeOld")
    suspend fun updateFileStore(storeOld:String,storeNew:String)

    @Query("DELETE FROM File WHERE store=:store")
    suspend fun deleteFileStore(store: String)

    @Insert
    suspend fun insertFile(file: File)

    @Query("UPDATE File SET name=:file WHERE id=:id")
    suspend fun updateFile(id:Int,file:String)

    @Query("DELETE FROM File WHERE id=:id")
    suspend fun deleteFile(id: Int)

}