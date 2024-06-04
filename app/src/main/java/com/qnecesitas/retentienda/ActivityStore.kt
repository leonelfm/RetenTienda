package com.qnecesitas.retentienda

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.qnecesitas.retentienda.adapters.AdapterStore
import com.qnecesitas.retentienda.data.Store
import com.qnecesitas.retentienda.databinding.ActivityStoreBinding
import com.qnecesitas.retentienda.databinding.LiAddstoreBinding
import com.qnecesitas.retentienda.databinding.LiEditstoreBinding
import com.qnecesitas.retentienda.viewmodels.StoreViewModel
import com.qnecesitas.retentienda.viewmodels.StoreViewModelFactory
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.launch

class ActivityStore : AppCompatActivity() {

    // Binding
    private lateinit var binding: ActivityStoreBinding

    // Recycler
    private lateinit var alStore: MutableList<Store>
    private lateinit var adapterStore: AdapterStore

    // ViewModel
    private val viewModel: StoreViewModel by viewModels {
        StoreViewModelFactory((application as RetenTienda).database.storeDao(),(application as RetenTienda).database.productDao(),(application as RetenTienda).database.fileDao())
    }

    // Layout Inflado
    private var li_add_binding: LiAddstoreBinding? = null
    private var li_edit_binding: LiEditstoreBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            val intent=Intent(this,ActivityHome::class.java)
            startActivity(intent)
        }

        // Recycler
        binding.recycler.setHasFixedSize(true)
        alStore = mutableListOf()
        adapterStore = AdapterStore(this)
        binding.recycler.adapter = adapterStore

        // Observe
        viewModel.listStore.observe(this) {
            adapterStore.submitList(it)
        }
        viewModel.listStoreFilter.observe(this) {
            adapterStore.submitList(it)
        }

        // SearchView
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
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            binding.clSearch.visibility = View.VISIBLE
            binding.ivIconSearch.visibility = View.GONE
            binding.ivIconSetting.visibility = View.GONE
            binding.ivIconAdd.visibility = View.GONE
            binding.search.setQuery("", false)
        }
        binding.ivCloseSearch.setOnClickListener {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            binding.clSearch.visibility = View.GONE
            binding.ivIconSearch.visibility = View.VISIBLE
            binding.ivIconSetting.visibility = View.VISIBLE
            binding.ivIconAdd.visibility = View.VISIBLE
            binding.search.setQuery("", false)
        }

        // Listener
        binding.ivIconSetting.setOnClickListener {
            val intent = Intent(this, ActivitySettings::class.java)
            startActivity(intent)
        }
        binding.ivIconAdd.setOnClickListener {
            liAddStore()
        }
        adapterStore.setClickEdit(object : AdapterStore.ITouchEdit {
            override fun onClickEdit(store: Store) {
                liEditStore(store)
            }
        })

        adapterStore.setClickDelete(object : AdapterStore.ITouchDelete {
            override fun onClickDelete(store: Store) {
                deleteStore(store)
            }
        })

        loadRecyclerInfoAll()
    }

    private fun loadRecyclerInfoAll() {
        lifecycleScope.launch {
            viewModel.getAllStore()
        }
    }

    private fun liAddStore() {
        val inflater = LayoutInflater.from(binding.root.context)
        li_add_binding = LiAddstoreBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(li_add_binding?.root)
        val alertDialog = builder.create()

        // Variables
        li_add_binding?.btnCancel?.setOnClickListener { alertDialog.dismiss() }
        li_add_binding?.btnAccept?.setOnClickListener {
            if (li_add_binding?.et?.text!!.trim().isNotEmpty()) {
                // Instances
                val name = li_add_binding?.et?.text.toString()

                alertDialog.dismiss()

                lifecycleScope.launch {
                    viewModel.insertStore(name)
                    FancyToast.makeText(
                        this@ActivityStore,
                        getString(R.string.operaci_n_realizada_con_xito),
                        FancyToast.LENGTH_LONG,
                        FancyToast.SUCCESS,
                        false
                    ).show()
                }
            } else {
                li_add_binding?.et?.error = getString(R.string.este_campo_no_debe_estar_vac_o)
            }
        }

        // Finished
        alertDialog.setCancelable(false)
        alertDialog.window!!.setGravity(Gravity.CENTER)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    private fun liEditStore(store: Store) {
        val inflater = LayoutInflater.from(binding.root.context)
        li_edit_binding = LiEditstoreBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(li_edit_binding?.root)
        val alertDialog = builder.create()

        // Variables
        li_edit_binding?.et?.setText(store.name)

        li_edit_binding?.btnCancel?.setOnClickListener { alertDialog.dismiss() }
        li_edit_binding?.btnAccept?.setOnClickListener {
            if (li_edit_binding?.et?.text!!.trim().isNotEmpty()) {
                // Instances
                val name = li_edit_binding?.et?.text.toString()

                lifecycleScope.launch {
                    viewModel.editStore(store.id, name)
                    viewModel.editFileStore(store.name,name)
                    viewModel.editProductStore(store.name,name)
                    FancyToast.makeText(
                        this@ActivityStore,
                        getString(R.string.operaci_n_realizada_con_xito),
                        FancyToast.LENGTH_LONG,
                        FancyToast.SUCCESS,
                        false
                    ).show()
                }
                alertDialog.dismiss()
            } else {
                li_edit_binding?.et?.error = getString(R.string.este_campo_no_debe_estar_vac_o)
            }
        }

        // Finished
        alertDialog.setCancelable(false)
        alertDialog.window!!.setGravity(Gravity.CENTER)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    private fun deleteStore(store: Store) {
        // Init alert dialog
        val builder = android.app.AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle(getString(R.string.eliminar))
        builder.setMessage(getString(R.string.est_seguro_de_eliminar_esta_tienda))
        // Set listeners for dialog buttons
        builder.setPositiveButton(getString(R.string.si)) { _, _ ->
            lifecycleScope.launch {
                viewModel.deleteProduct(store.name)
                viewModel.deleteFile(store.name)
                viewModel.deleteStore(store.id)
                FancyToast.makeText(
                    this@ActivityStore,
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

    private fun callFilterByText(text: String) {
        viewModel.filterByTextStore(text)
    }
}
