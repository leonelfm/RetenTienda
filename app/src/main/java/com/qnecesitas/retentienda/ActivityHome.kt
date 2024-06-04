package com.qnecesitas.retentienda

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.qnecesitas.retentienda.adapters.AdapterFile
import com.qnecesitas.retentienda.adapters.AdapterProduct
import com.qnecesitas.retentienda.adapters.AdapterStoreHome
import com.qnecesitas.retentienda.auxiliary.Constants
import com.qnecesitas.retentienda.data.File
import com.qnecesitas.retentienda.data.Product
import com.qnecesitas.retentienda.data.Store
import com.qnecesitas.retentienda.data.StoreHome
import com.qnecesitas.retentienda.databinding.ActivityHomeBinding
import com.qnecesitas.retentienda.databinding.LiAddEntryBinding
import com.qnecesitas.retentienda.databinding.LiAddfileBinding
import com.qnecesitas.retentienda.databinding.LiDetailsBinding
import com.qnecesitas.retentienda.databinding.LiEditfileBinding
import com.qnecesitas.retentienda.databinding.LiSalesBinding
import com.qnecesitas.retentienda.viewmodels.HomeViewModel
import com.qnecesitas.retentienda.viewmodels.HomeViewModelFactory
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ActivityHome : AppCompatActivity() {

    //binding
    private lateinit var binding:ActivityHomeBinding


    //Recycler
    private lateinit var alStore: MutableList<StoreHome>
    private lateinit var adapterStore: AdapterStoreHome
    private lateinit var alProduct: MutableList<Product>
    private lateinit var adapterProduct:AdapterProduct
    private lateinit var alFile: MutableList<File>
    private lateinit var adapterFile:AdapterFile

    //Notification
    private val CHANNEL_ID: String = "ElReten"
    private val CHANNEL_NAME = "ElReten"
    private var fileLoad =""
    lateinit var notificationManager: NotificationManager


    //View Model
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory((application as RetenTienda).database.storeDao(),(application as RetenTienda).database.productDao(),(application as RetenTienda).database.fileDao())
    }

    //Layout Inflater
    private var li_add_binding: LiAddEntryBinding?=null
    private var li_sales_binding: LiSalesBinding?=null
    private var li_details_binding:LiDetailsBinding?=null
    private var li_addFiles_binding:LiAddfileBinding?=null
    private var li_editFiles_binding:LiEditfileBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Recycler
        binding.recycler.setHasFixedSize(true)
        alStore = mutableListOf()
        adapterStore = AdapterStoreHome(this)
        binding.recycler.adapter = adapterStore

        binding.recyclerproduct.setHasFixedSize(true)
        alProduct = mutableListOf()
        adapterProduct = AdapterProduct(this)
        binding.recyclerproduct.adapter = adapterProduct

        binding.recyclerfile.setHasFixedSize(true)
        alFile = mutableListOf()
        adapterFile = AdapterFile(this)
        binding.recyclerfile.adapter = adapterFile


        //Notification
        val context = applicationContext
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        //Observe
        viewModel.listStoreHome.observe(this) {
            if (viewModel.listStoreHome.value?.isNotEmpty() == true){
                binding.btnAcept.visibility = View.VISIBLE
                binding.recycler.setHasFixedSize(true)
            adapterStore = AdapterStoreHome(this@ActivityHome)
            binding.recycler.adapter = adapterStore
            binding.recycler.scrollToPosition(
                it.indexOfFirst { element ->
                    element.state == "Seleccionado"
                }
            )
            adapterStore.setClickSelector(object : AdapterStoreHome.ITouchSelector {
                override fun onClickSelector(store: StoreHome , position: Int) {
                    viewModel.selectStore(store , position)



                }

            })

            adapterStore.submitList(it)
        }else{
            binding.btnAcept.visibility = View.GONE
                binding.clStore.visibility = View.VISIBLE
            }
        }
        viewModel.listProduct.observe(this){
                adapterProduct.submitList(it)
        }
        viewModel.listFile.observe(this){

                adapterFile.submitList(it)
        }
        viewModel.listProductFilter.observe(this) {
            adapterProduct.submitList(it)
        }

        //SearchView
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                callFilterByText(newText.toString())

                return false
            }

        })

        binding.ivIconSearch.setOnClickListener {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED , 0)
            binding.clSearch.visibility = View.VISIBLE
            binding.ivIconSearch.visibility = View.GONE
            binding.ivIconOptions.visibility = View.GONE
            binding.recycler.visibility = View.GONE
            binding.recyclerfile.visibility =View.GONE
            binding.recyclerproduct.visibility = View.VISIBLE
            binding.btnAcept.visibility = View.GONE
            binding.search.setQuery("" , false)
            callFilterByText("")
        }
        binding.ivCloseSearch.setOnClickListener {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY , 0)
            binding.clSearch.visibility = View.GONE
            binding.ivIconSearch.visibility = View.VISIBLE
            binding.ivIconOptions.visibility = View.VISIBLE
            binding.recyclerproduct.visibility = View.GONE
            binding.recycler.visibility = View.VISIBLE
            binding.recyclerfile.visibility =View.VISIBLE
            binding.btnAcept.visibility = View.VISIBLE
            binding.search.setQuery("" , false)
            loadRecyclerInfo()
        }

        binding.ivIconOptions.setOnClickListener {
            val popupMenu = PopupMenu(applicationContext, binding.ivIconOptions)
            popupMenu.menuInflater.inflate(R.menu.menu_option, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_store -> {
                        val intent= Intent(this,ActivityStore::class.java)
                        startActivity(intent)
                    }
                    R.id.menu_deficit -> {
                        val intent= Intent(this, ActivityDeficit::class.java)
                        startActivity(intent)
                    }
                    R.id.menu_options -> {
                        val intent= Intent(this,ActivitySettings::class.java)
                        startActivity(intent)
                    }
                }
                false
            }
            popupMenu.show()
        }

        adapterStore.setClickSelector(object : AdapterStoreHome.ITouchSelector {
            override fun onClickSelector(store: StoreHome , position: Int) {


                viewModel.selectStore(store , position)


            }

        })
        binding.btnAcept.setOnClickListener {
            if (binding.recyclerproduct.visibility == View.VISIBLE){
                val intent=Intent(this,ActivityNewProduct::class.java)
                intent.putExtra("store", viewModel.storeSelected.value?.get(0)?.name)
                intent.putExtra("file",fileLoad)
                startActivity(intent)
            }else{
                liAddFile(viewModel.storeSelected.value?.get(0)!!.name)
            }

        }
        adapterProduct.setClickEntry(object :AdapterProduct.ITouchEntry{
            override fun onClickEntry(product: Product) {
                liAddEntry(product)
            }

        })
        adapterProduct.setClickSales(object :AdapterProduct.ITouchSales{
            override fun onClickSales(product: Product) {
                liSalesProduct(product)
            }

        })
        adapterProduct.setClickEdit(object :AdapterProduct.ITouchEdit{
            override fun onClickEdit(product: Product) {
                val intent= Intent(this@ActivityHome,ActivityEditProduct::class.java)
                intent.putExtra("id",product.code)
                startActivity(intent)
            }

        })
        adapterProduct.setClickDelete(object :AdapterProduct.ITouchDelete{
            override fun onClickDelete(product: Product) {
                deleteProduct(product)
            }

        })
        adapterProduct.setClickDetails(object :AdapterProduct.ITouchDetails{
            override fun onClickDetails(product: Product) {
                liProductDetails(product)
            }

        })

        adapterFile.setClickSelector(object :AdapterFile.ITouchSelector{
            override fun onClickSelector(file: File) {
                binding.recycler.visibility = View.GONE
                binding.recyclerfile.visibility = View.GONE
                binding.recyclerproduct.visibility = View.VISIBLE
                binding.ivIconSearch.visibility = View.GONE
                viewModel.getProduct(viewModel.storeSelected.value?.get(0)!!.name,file.name)
                fileLoad = file.name
            }
        })

        adapterFile.setClickEdit(object :AdapterFile.ITouchEdit{
            override fun onClickEdit(file: File) {
                liEditFiles(file)
            }

        })

        adapterFile.setClickDelete(object :AdapterFile.ITouchDelete{
            override fun onClickDelete(file: File) {
                liDeleteFile(file)
            }

        })

        //Visibility Button Accept
        if (binding.clSearch.visibility == View.GONE) {
            if(binding.recyclerproduct.visibility == View.VISIBLE) {
                binding.recyclerproduct.layoutManager = LinearLayoutManager(this)
                binding.recyclerproduct.addOnScrollListener(object :
                    RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView , dx: Int , dy: Int) {
                        super.onScrolled(recyclerView , dx , dy)
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                        val itemCount = layoutManager.itemCount
                        if (lastVisibleItemPosition == itemCount - 1 && itemCount > 6) {
                            binding.btnAcept.hide()
                        } else {
                            binding.btnAcept.show()
                        }
                    }
                })
            }else{
                binding.recyclerfile.layoutManager = LinearLayoutManager(this)
                binding.recyclerfile.addOnScrollListener(object :
                    RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView , dx: Int , dy: Int) {
                        super.onScrolled(recyclerView , dx , dy)
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                        val itemCount = layoutManager.itemCount
                        if (lastVisibleItemPosition == itemCount - 1 && itemCount > 12) {
                            binding.btnAcept.hide()
                        } else {
                            binding.btnAcept.show()
                        }
                    }
                })
            }
        }

        if (!Constants.Notificado) {
            Constants.Notificado = true
            ifNotification()
        }

        loadRecyclerInfo()
    }
    @OptIn(DelicateCoroutinesApi::class)
    fun loadRecyclerInfo() {
        GlobalScope.launch(Dispatchers.IO) {
            viewModel.getAllInfo()
        }
    }

    fun liAddEntry(product: Product) {
        val inflater = LayoutInflater.from(binding.root.context)
        li_add_binding = LiAddEntryBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(li_add_binding!!.root)
        val alertDialog = builder.create()

        //Filling and listeners

        var currentAmount = 0
        li_add_binding!!.et.setText(currentAmount.toString())

        li_add_binding!!.ivBtnMore.setOnClickListener {
            if (currentAmount != 99999) {
                currentAmount++
                li_add_binding!!.et.setText(currentAmount.toString())
            }
        }

        li_add_binding!!.ivBtnLess.setOnClickListener {
            if (currentAmount != 0) {
                currentAmount--
                li_add_binding!!.et.setText(currentAmount.toString())
            }
        }

        li_add_binding!!.et.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(charSequence: CharSequence , i: Int , i1: Int , i2: Int) {
                if (li_add_binding!!.et.text.toString() == "0") {
                    currentAmount = 1
                    li_add_binding!!.et.setText(currentAmount.toString())
                } else if (li_add_binding!!.et.text.toString() == "") {
                    currentAmount = 1
                } else {
                    currentAmount = li_add_binding!!.et.text.toString().toInt()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
            override fun beforeTextChanged(
                charSequence: CharSequence ,
                i: Int ,
                i1: Int ,
                i2: Int
            ) {
            }
        })

        li_add_binding!!.btnAccept.setOnClickListener {
            alertDialog.dismiss()
            if (li_add_binding!!.et.text.toString().isNotBlank()) {
                lifecycleScope.launch {
                    viewModel.updateAmount(
                        product.code ,
                        li_add_binding!!.et.text.toString().toInt() + product.amount
                    )
                    FancyToast.makeText(
                        this@ActivityHome,
                        getString(R.string.operaci_n_realizada_con_xito) ,
                        FancyToast.LENGTH_LONG ,
                        FancyToast.SUCCESS ,
                        false
                    ).show()
                    if (binding.clSearch.visibility==View.VISIBLE){
                        binding.clSearch.visibility = View.GONE
                        binding.recyclerproduct.visibility = View.GONE
                        binding.ivIconSearch.visibility = View.VISIBLE
                        binding.ivIconOptions.visibility = View.VISIBLE
                        binding.recycler.visibility = View.VISIBLE
                        binding.recyclerfile.visibility = View.VISIBLE
                        binding.btnAcept.visibility = View.VISIBLE
                        binding.search.setQuery("" , false)
                        loadRecyclerInfo()
                    }else {
                        viewModel.getProduct(viewModel.storeSelected.value?.get(0)!!.name,fileLoad)
                    }
                }

            } else {
                li_add_binding!!.et.error = getString(R.string.este_campo_no_debe_estar_vac_o)
            }
        }

        li_add_binding!!.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        //Finished
        alertDialog.setCancelable(false)
        alertDialog.window!!.setGravity(Gravity.CENTER)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    fun liSalesProduct(product: Product) {
        val inflater = LayoutInflater.from(binding.root.context)
        li_sales_binding = LiSalesBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(li_sales_binding!!.root)
        val alertDialog = builder.create()


        var currentAmount = 0
        li_sales_binding!!.et.setText(currentAmount.toString())

        li_sales_binding!!.ivBtnMore.setOnClickListener {
            if (currentAmount != product.amount) {
                currentAmount++
                li_sales_binding!!.et.setText(currentAmount.toString())
            }
        }

        li_sales_binding!!.ivBtnLess.setOnClickListener {
            if (currentAmount != 0) {
                currentAmount--
                li_sales_binding!!.et.setText(currentAmount.toString())
            }
        }

        li_sales_binding!!.et.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(charSequence: CharSequence , i: Int , i1: Int , i2: Int) {
                if (li_sales_binding!!.et.text.toString() == "0") {
                    currentAmount = 1
                    li_sales_binding!!.et.setText(currentAmount.toString())
                } else if (li_sales_binding!!.et.text.toString() == "") {
                    currentAmount = 1
                } else if (li_sales_binding!!.et.text.toString()
                        .toInt() > product.amount
                ) {
                    li_sales_binding!!.et.setText(product.amount.toString())
                } else {
                    currentAmount = li_sales_binding!!.et.text.toString().toInt()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
            override fun beforeTextChanged(
                charSequence: CharSequence ,
                i: Int ,
                i1: Int ,
                i2: Int
            ) {
            }
        })

        li_sales_binding!!.btnAccept.setOnClickListener {
            lifecycleScope.launch {
                if (li_sales_binding!!.et.text.toString()
                        .isNotBlank()
                ) {
                    viewModel.updateAmount(
                        product.code ,
                        product.amount - li_sales_binding!!.et.text.toString().toInt()
                    )

                    alertDialog.dismiss()

                    if (binding.clSearch.visibility==View.VISIBLE){
                        binding.clSearch.visibility = View.GONE
                        binding.recyclerproduct.visibility = View.GONE
                        binding.ivIconSearch.visibility = View.VISIBLE
                        binding.ivIconOptions.visibility = View.VISIBLE
                        binding.recycler.visibility = View.VISIBLE
                        binding.recyclerfile.visibility = View.VISIBLE
                        binding.btnAcept.visibility = View.VISIBLE
                        binding.search.setQuery("" , false)
                        loadRecyclerInfo()
                    }else {
                        viewModel.getProduct(viewModel.storeSelected.value?.get(0)!!.name,fileLoad)
                    }


                    FancyToast.makeText(
                        this@ActivityHome ,
                        getString(R.string.operaci_n_realizada_con_xito) ,
                        FancyToast.LENGTH_LONG ,
                        FancyToast.SUCCESS ,
                        false
                    ).show()


                } else {
                    li_sales_binding!!.et.error =
                        (getString(R.string.este_campo_no_debe_estar_vac_o))
                }
            }
        }

        li_sales_binding!!.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        //Finished
        alertDialog.setCancelable(false)
        alertDialog.window!!.setGravity(Gravity.CENTER)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun deleteProduct(product: Product) {
        //init alert dialog
        val builder = android.app.AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle(getString(R.string.eliminar))
        builder.setMessage("¿Estás seguro que desea eliminar este producto?")
        //set listeners for dialog buttons
        builder.setPositiveButton(getString(R.string.si)) { _ , _ ->

                viewModel.deleteProduct(product.code)

            if (binding.clSearch.visibility==View.VISIBLE){
                binding.clSearch.visibility = View.GONE
                binding.recyclerproduct.visibility = View.GONE
                binding.ivIconSearch.visibility = View.VISIBLE
                binding.ivIconOptions.visibility = View.VISIBLE
                binding.recycler.visibility = View.VISIBLE
                binding.recyclerfile.visibility = View.VISIBLE
                binding.btnAcept.visibility = View.VISIBLE
                binding.search.setQuery("" , false)
                loadRecyclerInfo()
            }else {
                viewModel.getProduct(viewModel.storeSelected.value?.get(0)!!.name,fileLoad)
            }
            FancyToast.makeText(
                this@ActivityHome ,
                getString(R.string.operaci_n_realizada_con_xito) ,
                FancyToast.LENGTH_LONG ,
                FancyToast.SUCCESS ,
                false
            ).show()

        }
        builder.setNegativeButton(getString(R.string.no)) { dialog , _ ->
            //dialog gone
            dialog.dismiss()
        }
        //create the alert dialog and show it
        builder.create().show()
    }

    fun liProductDetails(product: Product) {
        val inflater = LayoutInflater.from(binding.root.context)
        li_details_binding = LiDetailsBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(li_details_binding!!.root)
        val alertDialog = builder.create()


        li_details_binding!!.code.text= product.code
        li_details_binding!!.name.text = product.name
        loadBase64ImageIntoImageView(product.image,li_details_binding!!.image)
        li_details_binding!!.brand.text= product.brand
        li_details_binding!!.store.text = product.store
        li_details_binding!!.file.text = product.file
        li_details_binding!!.amount.text = product.amount.toString()
        li_details_binding!!.deficit.text = product.deficit.toString()
        li_details_binding!!.buyPrice.text = product.buyPrice.toString()
        li_details_binding!!.salePrice.text = product.salePrice.toString()
        li_details_binding!!.descr.text = product.descr


        li_details_binding!!.aceptar.setOnClickListener {
            alertDialog.dismiss()
        }

        //Finished
        alertDialog.setCancelable(false)
        alertDialog.window!!.setGravity(Gravity.CENTER)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }


    fun callFilterByText(text: String) {
        viewModel.filterByTextDeficit(text)

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.recyclerproduct.visibility == View.GONE){
            showAlertDialogExit()
        }else{
            binding.recyclerproduct.visibility=View.GONE
            binding.recycler.visibility=View.VISIBLE
            binding.recyclerfile.visibility = View.VISIBLE
            binding.ivIconSearch.visibility = View.VISIBLE
            loadRecyclerInfo()
        }

    }
    private fun showAlertDialogExit() {
        //init alert dialog
        val builder = android.app.AlertDialog.Builder(this)
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

    @OptIn(DelicateCoroutinesApi::class)
    fun ifNotification() {
        lifecycleScope.launch {
            val deficit = viewModel.getDeficitCount()
            if (deficit >= 1) {
                notificationManager.notify(8 , displayNotification(deficit))

            }
        }
    }

    private fun displayNotification(deficit: Int): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID , CHANNEL_NAME , NotificationManager.IMPORTANCE_HIGH)
            channel.description = getString(R.string.d_ficit)
            channel.enableLights(true)
            channel.lightColor = Color.RED
            notificationManager.createNotificationChannel(channel)
        }
        val context = applicationContext
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context , CHANNEL_ID)
                .setContentTitle(getString(R.string.d_ficit))
                .setContentText("Tiene $deficit productos en déficit.")
                .setSmallIcon(R.drawable.ic_launcher1_round)
                .setAutoCancel(false)

        return builder.build()

    }
    // Función para cargar una imagen desde una cadena Base64 en un ImageView usando Glide
    fun loadBase64ImageIntoImageView(base64Image: String?, imageView: ImageView) {
        base64Image?.let {
            val decodedBytes = Base64.decode(it, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

            Glide.with(imageView.context)
                .load(bitmap)
                .error(R.drawable.baseline_image_search_24)
                .diskCacheStrategy(DiskCacheStrategy.NONE) // Desactiva la caché para cargar la imagen cada vez
                .skipMemoryCache(true) // Desactiva la caché en memoria para cargar la imagen cada vez
                .into(imageView)
        }
    }

    fun liAddFile(store:String){
        val inflater = LayoutInflater.from(binding.root.context)
        li_addFiles_binding = LiAddfileBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(li_addFiles_binding?.root)
        val alertDialog = builder.create()

        // Variables
        li_addFiles_binding?.btnCancel?.setOnClickListener { alertDialog.dismiss() }
        li_addFiles_binding?.btnAccept?.setOnClickListener {
            if (li_addFiles_binding?.et?.text!!.trim().isNotEmpty()) {
                // Instances
                val name = li_addFiles_binding?.et?.text.toString()

                alertDialog.dismiss()

                loadRecyclerInfo()

                lifecycleScope.launch {
                    viewModel.insertFile(name,store)
                    FancyToast.makeText(
                        this@ActivityHome,
                        getString(R.string.operaci_n_realizada_con_xito),
                        FancyToast.LENGTH_LONG,
                        FancyToast.SUCCESS,
                        false
                    ).show()
                }
            } else {
                li_addFiles_binding?.et?.error = getString(R.string.este_campo_no_debe_estar_vac_o)
            }
        }

        // Finished
        alertDialog.setCancelable(false)
        alertDialog.window!!.setGravity(Gravity.CENTER)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    private fun liEditFiles(file: File) {
        val inflater = LayoutInflater.from(binding.root.context)
        li_editFiles_binding = LiEditfileBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(li_editFiles_binding?.root)
        val alertDialog = builder.create()

        // Variables
        li_editFiles_binding?.et?.setText(file.name)

        li_editFiles_binding?.btnCancel?.setOnClickListener { alertDialog.dismiss() }
        li_editFiles_binding?.btnAccept?.setOnClickListener {
            if (li_editFiles_binding?.et?.text!!.trim().isNotEmpty()) {
                // Instances
                val name = li_editFiles_binding?.et?.text.toString()

                lifecycleScope.launch {
                    viewModel.editFile(file.id,name)
                    viewModel.editProductFile(file.name,name, viewModel.storeSelected.value?.get(0)!!.name)
                    FancyToast.makeText(
                        this@ActivityHome,
                        getString(R.string.operaci_n_realizada_con_xito),
                        FancyToast.LENGTH_LONG,
                        FancyToast.SUCCESS,
                        false
                    ).show()
                }
                alertDialog.dismiss()
                loadRecyclerInfo()
            } else {
                li_editFiles_binding?.et?.error = getString(R.string.este_campo_no_debe_estar_vac_o)
            }
        }

        // Finished
        alertDialog.setCancelable(false)
        alertDialog.window!!.setGravity(Gravity.CENTER)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    private fun liDeleteFile(file: File) {
        // Init alert dialog
        val builder = android.app.AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle(getString(R.string.eliminar))
        builder.setMessage(getString(R.string.est_seguro_que_desea_eliminar_esta_carpeta_y_todos_sus_productos))
        // Set listeners for dialog buttons
        builder.setPositiveButton(getString(R.string.si)) { _, _ ->
            lifecycleScope.launch {
                viewModel.deleteProductFile(file.name,viewModel.storeSelected.value?.get(0)!!.name)
                viewModel.deleteFile(file.id)
                loadRecyclerInfo()
                FancyToast.makeText(
                    this@ActivityHome,
                    getString(R.string.operaci_n_realizada_con_xito),
                    FancyToast.LENGTH_LONG,
                    FancyToast.SUCCESS,
                    false
                ).show()
            }
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            // Dialog gone
            dialog.dismiss()
        }
        // Create the alert dialog and show it
        builder.create().show()
    }
}