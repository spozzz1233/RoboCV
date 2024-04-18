package com.example.robocv.domain.main.impl

import com.example.robocv.util.Resource
import com.example.robocv.data.RoboCvDbRepository
import com.example.robocv.domain.main.RoboCvDbInteractor
import com.example.robocv.domain.model.Garbage
import com.example.robocv.domain.model.StoragePlaceRoboCV
import kotlinx.coroutines.flow.Flow

class RoboCvDbInteractorImpl(private val repository : RoboCvDbRepository) : RoboCvDbInteractor {
    override fun selectTableForSpinerFrom(connString: String): Flow<Resource<ArrayList<StoragePlaceRoboCV>>> {
        return repository.selectTableForSpinerFrom(connString)
    }

    override suspend fun sendTaskForRobot(
        connString: String,
        storagePlaceFrom: Int,
        storagePlaceTo: Int,
        tabNum: String,
        type: Int
    ): Flow<Resource<Unit>> {
        return repository.sendTaskForRobot(connString, storagePlaceFrom, storagePlaceTo, tabNum, type)
    }

    override fun selectedGarbageOut(connString: String, floor: String): Flow<Resource<ArrayList<Garbage>>> {
        return repository.selectedGarbageOut(connString,floor)
    }

    override suspend fun deleteAllItemsInGarbage(connString: String, storagePlace: List<Int>): Flow<Resource<Unit>> {
        return repository.deleteAllItemsInGarbage(connString,storagePlace)
    }

    override suspend fun deleteSelectedItemInGarbage(connString: String, selectedstoragePlace: Int) : Flow<Resource<Unit>>
    {
        return repository.deleteSelectedItemInGarbage(connString,selectedstoragePlace)
    }
}