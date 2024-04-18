import android.content.Context
import com.example.robocv.util.Resource
import com.example.robocv.data.RoboCvDbRepository
import com.example.robocv.domain.ErrorType
import com.example.robocv.domain.model.Garbage
import com.example.robocv.domain.model.StoragePlaceRoboCV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

class RoboCvDbRepositoryImpl(
    private val context: Context
) : RoboCvDbRepository {

    override suspend fun connectionToDb(connString: String): Connection {
        return withContext(Dispatchers.IO) {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            DriverManager.getConnection(connString)
        }
    }

    override fun selectTableForSpinerFrom(connString: String): Flow<Resource<ArrayList<StoragePlaceRoboCV>>> =
        flow {
            try {
                val connection = connectionToDb(connString)
                val statement = connection.createStatement()
                val resultSet: ResultSet =
                    statement.executeQuery("SELECT [id], convert(nvarchar(60),name,0) COLLATE DATABASE_DEFAULT as [name] FROM [WinCE].[dbo].[V_MES_StoragePlaceRoboCV] where [idKatPodr]=23 order by [name] ")
                val data = ArrayList<StoragePlaceRoboCV>()
                while (resultSet.next()) {
                    val id = resultSet.getInt("id")
                    val name = resultSet.getString("name")
                    val storagePlace = StoragePlaceRoboCV(id, name)
                    data.add(storagePlace)
                }
                emit(Resource.Success(data))
                connection.close()
            } catch (ex: Exception) {
                emit(Resource.Error(ErrorType.ERROR, ex.message.toString()))
            }
        }

    override suspend fun sendTaskForRobot(
        connString: String,
        storagePlaceFrom: Int,
        storagePlaceTo: Int,
        tabNum: String,
        type: Int
    ): Flow<Resource<Unit>> = flow {
        try {

            var preparedStatement: PreparedStatement? = null
            val connection = connectionToDb(connString)
            val sql =
                "INSERT INTO [WinCE].[dbo].[V_RoboCV_TasksFromTerminal] ([storagePlaceFrom], [storagePlaceTo], [TabNum], [type]) VALUES (?, ?, ?, ?)"
            preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setInt(1, storagePlaceFrom)
            preparedStatement.setInt(2, storagePlaceTo)
            preparedStatement.setInt(3, tabNum.toInt())
            preparedStatement.setInt(4, type)
            preparedStatement.executeUpdate()
            connection.close()
        }  catch (ex: Exception) {
            emit(Resource.Error(ErrorType.ERROR, ex.message.toString()))
        }
    }

    override fun selectedGarbageOut(
        connString: String,
        floor: String
    ): Flow<Resource<ArrayList<Garbage>>> = flow {
        try {

            val connection = connectionToDb(connString)
            val statement = connection.createStatement()
            val sql =
                "SELECT [StoragePlace],convert(nvarchar(60),[StoragePlaceName],0) COLLATE DATABASE_DEFAULT as [StoragePlaceName],convert(nvarchar(60),max(name),0) COLLATE DATABASE_DEFAULT as [name] " +
                        "FROM [WinCE].[dbo].[V_MES_StoragePlaceGarbage] where [floor] =  " + floor + " group by [StoragePlace],[StoragePlaceName] order by [StoragePlaceName]"
            val resultSet: ResultSet =
                statement.executeQuery(sql)
            val data = ArrayList<Garbage>()
            while (resultSet.next()) {
                val StoragePlace = resultSet.getInt("StoragePlace")
                val StoragePlaceName = resultSet.getString("StoragePlaceName") ?: "0"
                val name = resultSet.getString("name") ?: "0"

                val dataGarbage = Garbage(StoragePlace, StoragePlaceName, name)
                data.add(dataGarbage)
            }
            emit(Resource.Success(data))
            connection.close()


        } catch (ex: Exception) {
            emit(Resource.Error(ErrorType.ERROR, ex.message.toString()))
        }
    }

    override suspend fun deleteAllItemsInGarbage(
        connString: String,
        storagePlace: List<Int>
    ): Flow<Resource<Unit>> = flow {
        try {

            val connection = connectionToDb(connString)
            val statement = connection.createStatement()
            val storagePlaceIds = storagePlace.joinToString(",")
            val sql =
                "delete FROM [WinCE].[dbo].V_MES_inSklad where StoragePlace IN ($storagePlaceIds)"
            statement.executeUpdate(sql)
            connection.close()


        }catch (ex: Exception) {
            emit(Resource.Error(ErrorType.ERROR, ex.message.toString()))
        }
    }

    override suspend fun deleteSelectedItemInGarbage(
        connString: String,
        selectedStoragePlace: Int
    ): Flow<Resource<Unit>> = flow {
        try {

            val connection = connectionToDb(connString)
            val statement = connection.createStatement()
            val sql =
                "delete FROM [WinCE].[dbo].V_MES_inSklad where StoragePlace=${selectedStoragePlace}"
            statement.executeUpdate(sql)
            connection.close()
            emit(Resource.Success(Unit))

        }catch (ex: Exception) {
            emit(Resource.Error(ErrorType.ERROR, ex.message.toString()))
        }

    }
}