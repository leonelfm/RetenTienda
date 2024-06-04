package com.qnecesitas.retentienda.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class File (
    @PrimaryKey(autoGenerate = true) var id:Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "store") var store:String
)