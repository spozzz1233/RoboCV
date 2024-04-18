package com.example.robocv.ui.main.activity

import MainActivityViewModel
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.robocv.R
import com.example.robocv.databinding.ActivityMainBinding
import com.example.robocv.domain.ErrorType
import com.example.robocv.domain.model.StoragePlaceRoboCV
import com.example.robocv.ui.main.adapter.GarbageAdapter
import com.example.robocv.ui.main.adapter.SpinerAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.FieldPosition


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModel<MainActivityViewModel>()
    private lateinit var garbageAdapter: GarbageAdapter
    private lateinit var connString: String
    private var storagePlace: List<Int> = ArrayList()
    private var tabNum: String? = ""
    private lateinit var floor: String
    private var errorDialogShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        connString = resources.getString(R.string.connection_string)
        tabNum = intent.getStringExtra("tabNum")

        getDataForSpinersFrom(connString)
        allVisibility()
        errorDialog(cancelable = false){
            if (!errorDialogShown) {
                finish()
                errorDialogShown = true
            }
        }

        floor = binding.spinerType.selectedItemPosition.toString()

        binding.spinerType.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                getGarbageInformation(connString)
                initialGarbageAdapter()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.clearAll.setOnClickListener {
            deleteAllElements(connString)
        }

        binding.sendButton.setOnClickListener {
            createTaskForRobot(connString)
        }

        binding.deleteButton.setOnClickListener {
            deleteTaskForRobot(connString)
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
            if( data.isNotEmpty()){
                getDataForSpinersTo(connString)
                getDataForSpinersType(connString)
                initialGarbageAdapter()
            }
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

    private fun getDataForSpinersType(connString: String) {
        //spiner type
        val adapter = ArrayAdapter.createFromResource(
            this, R.array.Type,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinerType.setAdapter(adapter)
    }

    private fun createTaskForRobot(connString: String) {
        val storagePlaceFrom = binding.spinerFrom.selectedItem as StoragePlaceRoboCV
        val storagePlaceTo = binding.spinerTo.selectedItem as StoragePlaceRoboCV
        positiveDialog("Отправить задание роботу ?") {
            tabNum?.let {
                viewModel.sendTaskForRobot(
                    connString,
                    storagePlaceFrom.id ?: 0,
                    storagePlaceTo.id ?: 0,
                    it,
                    1
                )
            }
        }
    }

    private fun deleteTaskForRobot(connString: String) {
        val storagePlaceFrom = binding.spinerFrom.selectedItem as StoragePlaceRoboCV
        val storagePlaceTo = binding.spinerTo.selectedItem as StoragePlaceRoboCV
        positiveDialog("Удалить задания робота ?") {
            tabNum?.let {
                viewModel.sendTaskForRobot(
                    connString,
                    storagePlaceFrom.id ?: 0,
                    storagePlaceTo.id ?: 0,
                    it,
                    3
                )
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initialGarbageAdapter() {
        garbageAdapter = GarbageAdapter(clickListener = { item, position ->
            positiveDialog("Очистить выбраную ячейку ?") {
                viewModel.deleteSelectedItemFromGarbages(connString, item.StoragePlace)
                item.name = "0"
                garbageAdapter.notifyItemChanged(position)
            }
        })
        binding.recyclerView.adapter = garbageAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }


    private fun getGarbageInformation(connString: String) {
        viewModel.selectedGarbageOut(connString, floor)
        viewModel.garbageLiveData.observe(this) { data ->
            if (data.isNotEmpty()) {
                binding.headerTitle.visibility = View.VISIBLE
                garbageAdapter.setItems(data)
            } else {
                binding.headerTitle.visibility = View.GONE
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteAllElements(connString: String) {
        positiveDialog("Очистить все ячейки ?") {
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
        }

    }

    private fun allVisibility() {
        viewModel.mainLoadingLiveData.observe(this) { isLoading ->
            binding.MainProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.mainResultLiveData.observe(this) { isLoading ->
            binding.main.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.loadingLiveData.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.noResultLiveData.observe(this) { isLoading ->
            binding.placeholderMesage.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.headerTitle.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
        viewModel.resultLiveData.observe(this) { isLoading ->
            binding.recyclerView.visibility = if (isLoading) View.VISIBLE else View.GONE
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
            .setPositiveButton("Да") { dialog, _ ->
                onPositiveButtonClick?.invoke()
                dialog.dismiss()
            }
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
            }

        builder.show()
    }

}
