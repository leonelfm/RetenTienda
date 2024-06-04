package com.qnecesitas.retentienda.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Product(
    @PrimaryKey(autoGenerate = false)var code: String,
    @ColumnInfo(name = "name") var name:String,
    @ColumnInfo(name = "image") var image:String,
    @ColumnInfo(name = "brand") var brand:String,
    @ColumnInfo(name = "amount") var amount: Int,
    @ColumnInfo(name = "buyPrice") var buyPrice: Double,
    @ColumnInfo(name = "salePrice") var salePrice: Double,
    @ColumnInfo(name = "deficit") var deficit: Int,
    @ColumnInfo(name = " descr") var descr: String,
    @ColumnInfo(name = "store") var store: String,
    @ColumnInfo(name = "file") var file:String

)
