package com.qnecesitas.retentienda

import android.app.Application
import com.qnecesitas.retentienda.database.AppDatabase

class RetenTienda : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}