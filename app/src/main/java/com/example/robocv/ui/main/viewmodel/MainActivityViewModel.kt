import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.robocv.util.Resource
import com.example.robocv.domain.ErrorType
import com.example.robocv.domain.main.RoboCvDbInteractor
import com.example.robocv.domain.model.Garbage
import com.example.robocv.domain.model.StoragePlaceRoboCV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val interactor: RoboCvDbInteractor
) : ViewModel() {
    private val _spinerLiveData = MutableLiveData<ArrayList<StoragePlaceRoboCV>>()
    val spinerLiveData: LiveData<ArrayList<StoragePlaceRoboCV>> = _spinerLiveData

    private val _garbageLiveData = MutableLiveData<ArrayList<Garbage>>()
    val garbageLiveData: LiveData<ArrayList<Garbage>> = _garbageLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    private val _loadingLiveData = MutableLiveData<Boolean>()
    val loadingLiveData: LiveData<Boolean> = _loadingLiveData

    private val _noResultLiveData = MutableLiveData<Boolean>()
    val noResultLiveData: LiveData<Boolean> = _noResultLiveData

    private val _resultLiveData = MutableLiveData<Boolean>()
    val resultLiveData: LiveData<Boolean> = _resultLiveData

    private val _mainResultLiveData = MutableLiveData<Boolean>()
    val mainResultLiveData: LiveData<Boolean> = _mainResultLiveData

    private val _mainLoadingLiveData = MutableLiveData<Boolean>()
    val mainLoadingLiveData: LiveData<Boolean> = _mainLoadingLiveData

    private val _taskSentLiveData = MutableLiveData<Boolean>()
    val taskSentLiveData: LiveData<Boolean> = _taskSentLiveData

    fun getDataForSpiner(connString: String) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.selectTableForSpinerFrom(connString)
                .collect {
                    errorHandler(it)
                    handleResourceSpiner(it)
                }
        }
    }


    fun sendTaskForRobot(
        connString: String,
        storagePlaceFrom: Int,
        storagePlaceTo: Int,
        tabNum: String,
        type: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.sendTaskForRobot(connString, storagePlaceFrom, storagePlaceTo, tabNum, type)
                .collect { resource ->
                    when(resource){
                        is Resource.Success -> {
                            errorHandler(resource)
                            _taskSentLiveData.postValue(true)
                        }
                        is Resource.Error -> {
                            errorHandler(resource)
                            _taskSentLiveData.postValue(false)
                        }
                    }
                }
        }
    }



    fun selectedGarbageOut(connString: String, floor: String) {
        _loadingLiveData.value = true
        _noResultLiveData.postValue(false)
        viewModelScope.launch(Dispatchers.IO) {
            interactor.selectedGarbageOut(connString, floor).collect {
                errorHandler(it)
                handleResourceGarbage(it)
            }
        }
    }

    fun deleteAllItemsFromGarbages(connString: String, storagePlace: List<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.deleteAllItemsInGarbage(connString, storagePlace).collect {
                errorHandler(it)
            }
        }
    }

    fun deleteSelectedItemFromGarbages(connString: String, selectedstoragePlace: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.deleteSelectedItemInGarbage(connString, selectedstoragePlace).collect {
                errorHandler(it)
            }
        }
    }

    fun <T> errorHandler(item: Resource<T>) {

        if (item.message.toString().contains("Network")) {
            item.error = ErrorType.CONNECTION_ERROR
        }
        when (item.error) {
            ErrorType.ERROR -> {
                _errorLiveData.postValue(item.message.toString())
            }
            ErrorType.CONNECTION_ERROR -> {
                _errorLiveData.postValue("Отсутствует подключение к интернету")
            }
            else -> {}
        }
    }

    private fun handleResourceGarbage(item: Resource<ArrayList<Garbage>>) {
        if (item.error == null) {
            item.data?.let {
                if (it.isNotEmpty()) {
                    _garbageLiveData.postValue(it)
                    _loadingLiveData.postValue(false)
                    _noResultLiveData.postValue(false)
                    _resultLiveData.postValue(true)
                } else {
                    _loadingLiveData.postValue(false)
                    _noResultLiveData.postValue(true)
                    _resultLiveData.postValue(false)
                }
            }
        }
    }
    private fun handleResourceSpiner(item: Resource<ArrayList<StoragePlaceRoboCV>>) {
        if (item.error == null) {
            item.data?.let {
                if (it.isNotEmpty()) {
                    _mainResultLiveData.postValue(true)
                    _spinerLiveData.postValue(it)
                    _mainLoadingLiveData.postValue(false)
                } else {
                    _mainLoadingLiveData.postValue(false)
                    _mainResultLiveData.postValue(false)
                }
            }
        }

    }
}
