package com.example.robocv.ui.main.activity

import MainActivityViewModel
import android.R.id.message
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColor
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.robocv.R
import com.example.robocv.databinding.ActivityMainBinding
import com.example.robocv.domain.model.StoragePlaceRoboCV
import com.example.robocv.ui.main.adapter.GarbageAdapter
import com.example.robocv.ui.main.adapter.SpinerAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModel<MainActivityViewModel>()
    private lateinit var garbageAdapter: GarbageAdapter
    private lateinit var connString: String
    private var storagePlace: List<Int> = ArrayList()
    private var tabNum: String? = "1"
    private var isChecked: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isChecked = getCheckedState()
        connString = resources.getString(R.string.connection_string)

        tabNum = intent.getStringExtra("tabNum")

        getDataForSpinersFrom(connString)
        getDataForSpinersTo(connString)
        getDataForSpinersType()
        initialGarbageAdapter()
        garbageVisibility(isChecked)

        initialToolbar()
        allVisibility()
        errorDialog(cancelable = false) {
            finish()
        }



        binding.garbageModule.spinerType.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val floor = binding.garbageModule.spinerType.selectedItemPosition
                if(floor != 0){
                    getGarbageInformation(connString)
                    initialGarbageAdapter()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.garbageModule.clearAll.setOnClickListener {
            deleteAllElements(connString)
        }

        binding.sendButton.setOnClickListener {
            taskForRobot(connString, 1)
        }

        binding.deleteButton.setOnClickListener {
            taskForRobot(connString, 3)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.robo_cv_menu, menu)
        val menuItem = menu.findItem(R.id.Contents_of_the_cart)
        menuItem.isChecked = isChecked
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Contents_of_the_cart -> {
                item.isChecked = !item.isChecked
                garbageVisibility(item.isChecked)
                saveCheckedState(item.isChecked)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }





private fun getDataForSpinersFrom(connString: String) {
        viewModel.getDataForSpiner(connString)
        viewModel.spinerLiveData.observe(this) { data ->
            allVisibility()
            //from spiner
            val adapter = data?.let {
                SpinerAdapter(
                    this, android.R.layout.simple_spinner_item,
                    it
                )
            }
            adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinerFrom.adapter = adapter
        }
    }

    private fun getDataForSpinersTo(connString: String) {
        viewModel.getDataForSpiner(connString)
        viewModel.spinerLiveData.observe(this) { data ->
            allVisibility()
            val adapter = data?.let {
                SpinerAdapter(
                    this, android.R.layout.simple_spinner_item,
                    it
                )
            }
            //to spiner
            adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinerTo.adapter = adapter
        }
    }

    private fun getDataForSpinersType() {
        //spiner type
        val adapter = ArrayAdapter.createFromResource(
            this, R.array.Type,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.garbageModule.spinerType.setAdapter(adapter)
    }

    private fun taskForRobot(connString: String, type: Int) {
        val storagePlaceFrom = binding.spinerFrom.selectedItem as StoragePlaceRoboCV
        val storagePlaceTo = binding.spinerTo.selectedItem as StoragePlaceRoboCV
        positiveDialog("Отправить задание роботу ?", onPositiveButtonClick = {
            viewModel.sendTaskForRobot(
                connString,
                storagePlaceFrom.id ?: 0,
                storagePlaceTo.id ?: 0,
                "1",
                type
            )
        })
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun initialGarbageAdapter() {
        garbageAdapter = GarbageAdapter(clickListener = { item, position ->
            positiveDialog("Очистить выбраную ячейку ?", onPositiveButtonClick = {
                viewModel.deleteSelectedItemFromGarbages(connString, item.StoragePlace)
                item.name = "0"
                garbageAdapter.notifyItemChanged(position)
            })
        })
        binding.garbageModule.recyclerView.adapter = garbageAdapter
        binding.garbageModule.recyclerView.layoutManager = LinearLayoutManager(this)
    }


    private fun getGarbageInformation(connString: String) {
        val floor: String = binding.garbageModule.spinerType.selectedItemPosition.toString()
        viewModel.selectedGarbageOut(connString, floor)
        viewModel.garbageLiveData.observe(this) { data ->
            garbageAdapter.setItems(data)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteAllElements(connString: String) {
        positiveDialog("Очистить все ячейки ?", onPositiveButtonClick = {
            viewModel.garbageLiveData.observe(this) { data ->
                storagePlace = data.map { it.StoragePlace }
                getGarbageInformation(connString)
            }
            if (storagePlace.size != 0) {
                viewModel.deleteAllItemsFromGarbages(connString, storagePlace)

            } else {
                Toast.makeText(this, "Нет элементов для удаления!", Toast.LENGTH_SHORT)
                    .show()
            }
        })

    }

    private fun allVisibility() {
        viewModel.mainLoadingLiveData.observe(this) { isLoading ->
            binding.MainProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.mainResultLiveData.observe(this) { isLoading ->
            binding.main.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.loadingLiveData.observe(this) { isLoading ->
            binding.garbageModule.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.noResultLiveData.observe(this) { isLoading ->
            binding.garbageModule.placeholderMesage.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.garbageModule.headerTitle.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
        viewModel.resultLiveData.observe(this) { isLoading ->
            binding.garbageModule.recyclerView.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun errorDialog(
        cancelable: Boolean = true,
        onPositiveButtonClick: (() -> Unit)? = null
    ) {
        viewModel.errorLiveData.observe(this) {
            val builder = MaterialAlertDialogBuilder(this)
                .setTitle("Произошла Ошибка:")
                .setMessage(it)
                .setCancelable(cancelable)
                .setPositiveButton("Ок") { dialog, _ ->
                    onPositiveButtonClick?.invoke()
                    dialog.dismiss()
                }
            builder.show()
        }
    }

    private fun positiveDialog(
        title: String,
        onPositiveButtonClick: (() -> Unit)? = null
    ) {
        val builder = MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Да") { dialog, _ ->
                onPositiveButtonClick?.invoke()
            }

        builder.show()
    }

    private fun initialToolbar(){
        setSupportActionBar(binding.toolbar)
        setTitle("Управление тележкой")
    }

    private fun garbageVisibility(isChecked: Boolean){
        if(isChecked){
            binding.garbageModule.garbageAll.visibility = View.VISIBLE
        }else{
            binding.garbageModule.garbageAll.visibility = View.GONE
        }
    }
    private fun saveCheckedState(isChecked: Boolean) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isChecked", isChecked)
        editor.apply()
    }
    private fun getCheckedState(): Boolean {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isChecked", false)
    }

}
