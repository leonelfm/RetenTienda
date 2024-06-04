package com.qnecesitas.retentienda

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.qnecesitas.retentienda.auxiliary.Constants
import com.qnecesitas.retentienda.databinding.ActivitySettingsBinding
import com.qnecesitas.retentienda.viewmodels.SettingsViewModel
import com.qnecesitas.retentienda.viewmodels.SettingsViewModelFactory
import com.shashank.sony.fancytoastlib.FancyToast

class ActivitySettings : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    //ViewModel
    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory()
    }

    //Password
    private lateinit var password: String

    //Shared Preferences
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedEditor: SharedPreferences.Editor


    //ActivityResultExport
    private lateinit var activityResultExport: ActivityResultLauncher<Intent>

    //ActivityResultImport
    private lateinit var activityResultImport: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Preferences
        sharedPreferences = getSharedPreferences("RetenTienda" , 0)
        sharedEditor = sharedPreferences.edit()
        password = sharedPreferences.getString("password" , Constants.INITIAL_PASSWORD).toString()

        //Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnAdminAccept.setOnClickListener {
            clickAcceptPassword()
        }

        binding.clExpBd.setOnClickListener {
            exportDb()
        }
        binding.clImpBd.setOnClickListener {
            importDB()
        }


        //ActivityResultExport
        activityResultExport =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if (data != null && data.data != null) {
                        val uri = data.data
                        val outputStream = contentResolver.openOutputStream(uri!!)
                        viewModel.exportBD(this , outputStream)
                        FancyToast.makeText(
                            this ,
                            getString(R.string.base_de_datos_exportada_con_xito) ,
                            FancyToast.LENGTH_LONG ,
                            FancyToast.SUCCESS ,
                            false
                        ).show()
                    } else {
                        FancyToast.makeText(
                            this ,
                            getString(R.string.error_al_exportar_la_base_de_datos) ,
                            FancyToast.LENGTH_LONG ,
                            FancyToast.ERROR ,
                            false
                        ).show()
                    }
                }
            }

        //ActivityResultImport
        activityResultImport =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if (data != null && data.data != null) {
                        val uri = data.data

                        uri?.let { viewModel.importBD(this , it) }
                        showAlertDialogExit()
                    } else {
                        FancyToast.makeText(
                            this ,
                            getString(R.string.error_al_importar_la_base_de_datos) ,
                            FancyToast.LENGTH_LONG ,
                            FancyToast.ERROR ,
                            false
                        ).show()
                    }
                }
            }

    }


    private fun clickAcceptPassword() {
        checkInfoPassword()
    }

    private fun checkInfoPassword() {
        val introducedPassword = binding.tietPasswAdminCurrent.text.toString()
        val newPassword = binding.tietPasswAdminNew.text.toString()

        var result = true

        if (binding.tietPasswAdminCurrent.text?.isEmpty() == true) {
            result = false
            binding.tietPasswAdminCurrent.error = getString(R.string.este_campo_no_debe_estar_vac_o)
        } else {
            binding.tietPasswAdminCurrent.error = null
        }

        if (binding.tietPasswAdminNew.text?.isEmpty() == true) {
            result = false
            binding.tietPasswAdminNew.error = getString(R.string.este_campo_no_debe_estar_vac_o)
        } else {
            binding.tietPasswAdminNew.error = null
        }

        if (binding.tietPasswAdminConfirm.text?.isEmpty() == true) {
            result = false
            binding.tietPasswAdminConfirm.error = getString(R.string.este_campo_no_debe_estar_vac_o)
        } else {
            binding.tietPasswAdminConfirm.error = null
        }

        if (binding.tietPasswAdminConfirm.text.toString() !=
            binding.tietPasswAdminNew.text.toString()
        ) {
            result = false
            binding.tietPasswAdminConfirm.error =
                getString(R.string.no_coinciden_la_contrase_a_a_confirmar_y_la_nueva)
        } else {
            binding.tietPasswAdminConfirm.error = null
        }

        if (introducedPassword == password) {
            binding.tietPasswAdminCurrent.error = null
        } else {
            binding.tietPasswAdminCurrent.error = getString(R.string.contrase_a_incorrecta)
            result = false
        }

        if (result) updatePassword(newPassword)
    }

    private fun updatePassword(newPassword: String) {
        sharedEditor.putString("password" , newPassword)
        sharedEditor.apply()
        binding.tietPasswAdminCurrent.text = null
        binding.tietPasswAdminConfirm.text = null
        binding.tietPasswAdminNew.text = null
        FancyToast.makeText(
            this ,
            getString(R.string.operaci_n_realizada_con_xito) ,
            FancyToast.LENGTH_SHORT ,
            FancyToast.SUCCESS ,
            false
        ).show()
    }

    private fun exportDb() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.putExtra(Intent.EXTRA_TITLE , "App_DataBase.db")
        intent.type = "application/x-sqlite3"
        activityResultExport.launch(intent)
    }

    private fun importDB() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "*/*"
        activityResultImport.launch(intent)
    }


    private fun showAlertDialogExit() {
        //init alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle(getString(R.string.importado_con_xito))
        builder.setMessage(R.string.Debe_reiniciar)
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.si) { _ , _ ->
            //finish the activity
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        //create the alert dialog and show it
        builder.create().show()
    }
}