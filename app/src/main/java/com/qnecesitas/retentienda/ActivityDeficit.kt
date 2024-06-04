package com.qnecesitas.retentienda

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.qnecesitas.retentienda.adapters.AdapterDeficit
import com.qnecesitas.retentienda.data.Product
import com.qnecesitas.retentienda.databinding.ActivityDeficitBinding
import com.qnecesitas.retentienda.databinding.LiAddEntryBinding
import com.qnecesitas.retentienda.viewmodels.DeficitViewModel
import com.qnecesitas.retentienda.viewmodels.DeficitViewModelFactory
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ActivityDeficit : AppCompatActivity() {

    //Binding
    private lateinit var binding: ActivityDeficitBinding

    private var li_amount_binding: LiAddEntryBinding? = null

    //Recycler
    private lateinit var alDeficit: MutableList<Product>
    private lateinit var adapterDeficit: AdapterDeficit

    // ViewModel
    private val viewModel:DeficitViewModel by viewModels {
        DeficitViewModelFactory((application as RetenTienda).database.productDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeficitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            val intent=Intent(this,ActivityHome::class.java)
            startActivity(intent) }

        //Recycler
        binding.recycler.setHasFixedSize(true)
        alDeficit = mutableListOf()
        adapterDeficit = AdapterDeficit(this)
        binding.recycler.adapter = adapterDeficit

        //Observe
        viewModel.listDeficit.observe(this) {
            adapterDeficit.submitList(it)
        }
        viewModel.listDeficitFilter.observe(this) {
            adapterDeficit.submitList(it)
        }

        //Settings
        binding.ivIconSetting.setOnClickListener {
            val intent = Intent(this , ActivitySettings::class.java)
            startActivity(intent)
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
            binding.ivIconSetting.visibility = View.GONE
            binding.search.setQuery("" , false)
        }
        binding.ivCloseSearch.setOnClickListener {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY , 0)
            binding.clSearch.visibility = View.GONE
            binding.ivIconSearch.visibility = View.VISIBLE
            binding.ivIconSetting.visibility = View.VISIBLE
            binding.search.setQuery("" , false)
        }


        adapterDeficit.setClickEntry(object : AdapterDeficit.ITouchEntry {
            override fun onClickEntry(product: Product) {
                entryAmount(product)
            }

        })
        loadRecyclerInfoAll()
    }
    fun entryAmount(product: Product) {
        val inflater = LayoutInflater.from(binding.root.context)
        li_amount_binding = LiAddEntryBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(li_amount_binding!!.root)
        val alertDialog = builder.create()

        //Filling and listeners
        loadRecyclerInfoAll()
        var currentAmount = 0
        li_amount_binding!!.et.setText(currentAmount.toString())

        li_amount_binding!!.ivBtnMore.setOnClickListener {
            if (currentAmount != 99999) {
                currentAmount++
                li_amount_binding!!.et.setText(currentAmount.toString())
            }
        }

        li_amount_binding!!.ivBtnLess.setOnClickListener {
            if (currentAmount != 0) {
                currentAmount--
                li_amount_binding!!.et.setText(currentAmount.toString())
            }
        }

        li_amount_binding!!.et.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(charSequence: CharSequence , i: Int , i1: Int , i2: Int) {
                if (li_amount_binding!!.et.text.toString() == "0") {
                    currentAmount = 1
                    li_amount_binding!!.et.setText(currentAmount.toString())
                } else if (li_amount_binding!!.et.text.toString() == "") {
                    currentAmount = 1
                } else {
                    currentAmount = li_amount_binding!!.et.text.toString().toInt()
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

        li_amount_binding!!.btnAccept.setOnClickListener {
            alertDialog.dismiss()
            if (li_amount_binding!!.et.text.toString().isNotBlank()) {
                lifecycleScope.launch {
                    viewModel.updateAmount(
                        product.code ,
                        li_amount_binding!!.et.text.toString().toInt() + product.amount
                    )
                    FancyToast.makeText(
                        this@ActivityDeficit,
                        getString(R.string.operaci_n_realizada_con_xito) ,
                        FancyToast.LENGTH_LONG ,
                        FancyToast.SUCCESS ,
                        false
                    ).show()
                }

            } else {
                li_amount_binding!!.et.error = getString(R.string.este_campo_no_debe_estar_vac_o)
            }
        }

        li_amount_binding!!.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        //Finished
        alertDialog.setCancelable(false)
        alertDialog.window!!.setGravity(Gravity.CENTER)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun loadRecyclerInfoAll() {
        GlobalScope.launch(Dispatchers.IO) {
            viewModel.getAllDeficit()

        }
    }

    fun callFilterByText(text: String) {
        viewModel.filterByTextDeficit(text)
    }


}