package com.example.robocv.domain.main

import com.example.robocv.data.Resource
import com.example.robocv.domain.model.Garbage
import com.example.robocv.domain.model.StoragePlaceRoboCV
import kotlinx.coroutines.flow.Flow

interface RoboCvDbInteractor {
    fun selectTableForSpinerFrom(connString: String) : Flow<Resource<ArrayList<StoragePlaceRoboCV>>>
    suspend fun sendTaskForRobot(connString: String, storagePlaceFrom: Int, storagePlaceTo: Int, tabNum: String, type: Int): Flow<Resource<Unit>>
    fun selectedGarbageOut(connString: String,floor: String) : Flow<Resource<ArrayList<Garbage>>>
    suspend fun deleteAllItemsInGarbage(connString: String, storagePlace : List<Int>) : Flow<Resource<Unit>>
    suspend fun deleteSelectedItemInGarbage(connString: String, selectedstoragePlace: Int) : Flow<Resource<Unit>>


}