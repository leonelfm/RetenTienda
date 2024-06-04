package com.qnecesitas.retentienda

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.PopupMenu
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.qnecesitas.retentienda.auxiliary.IDCreater
import com.qnecesitas.retentienda.auxiliary.ImageTools
import com.qnecesitas.retentienda.databinding.ActivityNewProductBinding
import com.qnecesitas.retentienda.viewmodels.NewProductViewModel
import com.qnecesitas.retentienda.viewmodels.NewProductViewModelFactory
import com.shashank.sony.fancytoastlib.FancyToast
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch

class ActivityNewProduct : AppCompatActivity() {

    //binding
    private lateinit var binding:ActivityNewProductBinding

    private var store:String=""
    private var file:String=""

    //View Model
    private val viewModel: NewProductViewModel by viewModels {
        NewProductViewModelFactory((application as RetenTienda).database.productDao())
    }
    //Image
    private var uriImageCut: Uri? = null

    //Results launchers
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }


        store= intent.getStringExtra("store")!!
        file = intent.getStringExtra("file")!!

        //Results launchers
                galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                imageReceivedGallery(result)
            }

        //Listener
        binding.TIETCode.setText(IDCreater.generate())
        binding.btnCancel.setOnClickListener {
            showAlertCancel()
        }

        binding.btnAcept.setOnClickListener {
            if(isInformationGood()){
                showAlertAcept()
            }
        }
        binding.cvImage.setOnClickListener {
            val popupMenu = PopupMenu(applicationContext, binding.cvImage)
            popupMenu.menuInflater.inflate(R.menu.menu_addimage, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_add -> {
                        choiceGalleryImage()
                    }
                    R.id.menu_delete -> {
                        binding.image.setImageDrawable(this.getDrawable(R.drawable.baseline_image_search_24))
                        uriImageCut = null
                    }
                }
                false
            }
            popupMenu.show()
        }
    }

    private fun showAlertCancel() {
        //init alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle(getString(R.string.cancelar1))
        builder.setMessage(getString(R.string.est_seguro_de_cancelar_estos_datos))
        //set listeners for dialog buttons
        builder.setPositiveButton(getString(R.string.si)) { _ , _ ->
            finish()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog , _ ->
            //dialog gone
            dialog.dismiss()
        }
        //create the alert dialog and show it
        builder.create().show()
    }


    private fun isInformationGood():Boolean{
        var productTrue = 0
        //Code
        if (binding.TIETCode.text?.trim()!!.isNotEmpty()){
            productTrue ++
        }else{
            binding.TILCode.error= getString(R.string.este_campo_no_debe_de_estar_vac_o)
        }
        //Name
        if (binding.TIETName.text?.trim()!!.isEmpty()){
            binding.TIETName.setText(getString(R.string.no_definido))
        }
        //Brand
        if (binding.TIETBrand.text?.trim()!!.isEmpty()){
            binding.TIETBrand.setText(getString(R.string.no_definido))
        }
        //Amount
        if (binding.TIETAmount.text?.trim()!!.isEmpty()){
            binding.TIETAmount.setText(getString(R.string._0))
        }
        //Deficit
        if (binding.TIETDeficit.text?.trim()!!.isEmpty()){
            binding.TIETDeficit.setText(getString(R.string._0))
        }
        //BuyPrice
        if (binding.TIETBuyPrice.text?.trim()!!.isEmpty()){
            binding.TIETBuyPrice.setText(getString(R.string._0))
        }
        //BuySales
        if (binding.TIETSalePrice.text?.trim()!!.isEmpty()){
            binding.TIETSalePrice.setText(getString(R.string._0))
        }
        //Description
        if (binding.TIETDescr.text?.trim()!!.isEmpty()){
            binding.TIETDescr.setText(getString(R.string.no_definido))
        }
        return productTrue ==1
    }

    private fun showAlertAcept() {
        //init alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle(getString(R.string.aceptar1))
        builder.setMessage(getString(R.string.est_s_seguro_de_guardar_estos_datos))
        //set listeners for dialog buttons
        builder.setPositiveButton(getString(R.string.si)) { _ , _ ->
            saveInformation()

        }
        builder.setNegativeButton(getString(R.string.no)) { dialog , _ ->
            //dialog gone
            dialog.dismiss()
        }
        //create the alert dialog and show it
        builder.create().show()
    }
    private fun saveInformation(){
        val code = binding.TIETCode.text.toString()
        val name = binding.TIETName.text.toString()
        val brand = binding.TIETBrand.text.toString()
        val amount = binding.TIETAmount.text.toString().toInt()
        val deficit = binding.TIETDeficit.text.toString().toInt()
        val buyPrice = binding.TIETBuyPrice.text.toString().toDouble()
        val salePrice = binding.TIETSalePrice.text.toString().toDouble()
        val description = binding.TIETDescr.text.toString()
        var image =""
        image = if( this.uriImageCut!=null){
            convertImageToBase64(this.uriImageCut)
        }else{
            "No"
        }

        val tienda = store
        val file = file

        lifecycleScope.launch {
            if (!viewModel.selectDuplicate(code)) {
               viewModel.addProduct(code,name,image,brand,amount,buyPrice,salePrice,deficit,description,tienda,file)
                val intent= Intent(this@ActivityNewProduct,ActivityHome::class.java)
                FancyToast.makeText(
                    this@ActivityNewProduct,
                    getString(R.string.operaci_n_realizada_con_xito) ,
                    FancyToast.LENGTH_LONG ,
                    FancyToast.SUCCESS ,
                    false
                ).show()
                startActivity(intent)

            } else {
                showAlertDialogDuplicated()
            }
        }
    }

    private fun convertImageToBase64(imageUri: Uri?): String {
        val inputStream = imageUri?.let { contentResolver.openInputStream(it) }
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        return bytes?.let { android.util.Base64.encodeToString(it, android.util.Base64.DEFAULT) } ?: ""
    }

    //Image logic
    private fun choiceGalleryImage() {
        val galleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    private fun imageReceivedGallery(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val contentUri = data?.data

            val file = ImageTools.createTempImageFile(
                this,
                ImageTools.getActualHour("yyMMddHHmmss")
            )

            if (contentUri != null) {
                cutImage(contentUri, Uri.fromFile(file))
            } else {
                FancyToast.makeText(
                    this,
                    getString(R.string.error_al_obtener_la_imagen),
                    FancyToast.LENGTH_SHORT,
                    FancyToast.ERROR,
                    false
                ).show()
            }

        } else {
            FancyToast.makeText(
                this,
                getString(R.string.error_al_obtener_la_imagen,),
                FancyToast.LENGTH_SHORT,
                FancyToast.ERROR,
                false
            ).show()
        }
    }
    private fun cutImage(uri1: Uri, uri2: Uri) {
        try {
            UCrop.of(uri1, uri2)
                .withAspectRatio(3f, 3f)
                .withMaxResultSize(
                    ImageTools.WIDTH_OF_PHOTO_TO_UPLOAD,
                    ImageTools.HEIGHT_OF_PHOTO_TO_UPLOAD
                )
                .start(this)
        } catch (e: Exception) {
            FancyToast.makeText(
                this,
                getString(R.string.error_al_obtener_la_imagen),
                FancyToast.LENGTH_SHORT,
                FancyToast.ERROR,
                false
            ).show()
        }
    }

    private fun imageCropped(data: Intent?) {
        if (data != null) {
            this.uriImageCut = UCrop.getOutput(data)
            binding.image.setImageURI(this.uriImageCut)
        }
    }
    //Activity utils
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_CANCELED) {
            return
        }

        //UCrop
        if (requestCode == UCrop.REQUEST_CROP) {


            if (data != null) {
                imageCropped(data)
            } else {
                FancyToast.makeText(
                    this,
                    getString(R.string.error_al_obtener_la_imagen),
                    FancyToast.LENGTH_SHORT,
                    FancyToast.ERROR,
                    false
                ).show()
            }
        }
        if (requestCode == UCrop.RESULT_ERROR) {
            FancyToast.makeText(
                this,
                getString(R.string.error_al_obtener_la_imagen),
                FancyToast.LENGTH_SHORT,
                FancyToast.ERROR,
                false
            ).show()
        }
    }

    private fun showAlertDialogDuplicated() {
        //init alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(getString(R.string.elemento_repetido))
        builder.setMessage(getString(R.string.no_pueden_existir_dos_productos_con_el_mismo_c_digo))
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.aceptar) { dialog , _ ->
            dialog.dismiss()
        }

        //create the alert dialog and show it
        builder.create().show()
    }
}