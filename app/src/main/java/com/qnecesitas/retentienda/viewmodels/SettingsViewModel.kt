package com.qnecesitas.retentienda.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qnecesitas.retentienda.database.AppDatabase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception

class SettingsViewModel : ViewModel() {
    fun exportBD(context: Context , outputStream: OutputStream?) {
        val database = AppDatabase.getDatabase(context)
        val currentDbPath = context.getDatabasePath(database.openHelper.databaseName)

        try {
            database.close()
            outputStream?.let { output ->
                val inputChannel = FileInputStream(currentDbPath).channel
                val outputChannel = (output as FileOutputStream).channel
                inputChannel.transferTo(0 , inputChannel.size() , outputChannel)
                inputChannel.close()
                outputChannel.close()
            }
            outputStream?.close()
        } catch (e: Exception) {
            Log.e("Error" , e.toString())
        }
    }

    @SuppressLint("Recycle")
    fun importBD(context: Context , uri: Uri) {
        val inputStream = context.contentResolver.openInputStream(uri)
        inputStream?.let {
            val importDir = File(context.getExternalFilesDir(null) , "app_database.db")
            val outputStream = FileOutputStream(importDir)
            inputStream.copyTo(outputStream)
            outputStream.close()

            if (importDir.exists()) {
                val appDatabase = AppDatabase.getDatabase(context)
                val currentDatabasePath =
                    context.getDatabasePath(appDatabase.openHelper.databaseName)
                appDatabase.close()
                importDir.copyTo(currentDatabasePath , overwrite = true)
                importDir.delete()
            }

        }
    }
}

class SettingsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel() as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}