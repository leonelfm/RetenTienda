package com.qnecesitas.retentienda

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.qnecesitas.retentienda.auxiliary.Constants
import com.qnecesitas.retentienda.databinding.ActivityLoginBinding

class ActivityLogin : AppCompatActivity() {
    //Binding
    private lateinit var binding: ActivityLoginBinding

    //Password
    private lateinit var password: String

    //Shared Preferences
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedEditor: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Preferences
        sharedPreferences = getSharedPreferences("RetenTienda" , 0)
        sharedEditor = sharedPreferences.edit()


        password = sharedPreferences.getString("password" , Constants.INITIAL_PASSWORD).toString()

        binding.ALBTNStartSession.setOnClickListener {
            if (binding.ALTIETPassword.text.toString().trim().isNotEmpty()) {
                if (binding.ALTIETPassword.text.toString() == password) {
                    binding.ALTILPassword.error = null

                    val intent = Intent(this , ActivityHome::class.java)
                    startActivity(intent)

                } else {
                    binding.ALTILPassword.error = getString(R.string.contrase_a_incorrecta)
                }
            } else {
                binding.ALTILPassword.error = getString(R.string.este_campo_no_debe_estar_vac_o)
            }
        }


        binding.tvAboutUs.setOnClickListener {
            val intent = Intent(this , ActivityAboutUs::class.java)
            startActivity(intent)
        }

        binding.tvAboutDev.setOnClickListener {
            val intent = Intent(this , ActivityAboutDev::class.java)
            startActivity(intent)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        showAlertDialogExit()
    }

    private fun showAlertDialogExit() {
        //init alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle(getString(R.string.salir))
        builder.setMessage("¿Desea salir de la aplicación?")
        //set listeners for dialog buttons
        builder.setPositiveButton(getString(R.string.si)) { _ , _ ->
            //finish the activity
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog , _ ->
            //dialog gone
            dialog.dismiss()
        }
        //create the alert dialog and show it
        builder.create().show()
    }
}