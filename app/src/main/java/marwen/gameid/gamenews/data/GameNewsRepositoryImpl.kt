package marwen.gameid.gamenews.data

import coil.network.HttpException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import marwen.gameid.gamenews.data.model.Newsitem
import okio.IOException

class GameNewsRepositoryImpl(
    private val api: GameNewsApi
): GameNewsRepository{
    override suspend fun getNewsList(): Flow<Result<List<Newsitem>>> {
        return flow {
            val newsFromApi = try {
                api.getNewsList("730")  // Static App id of CS2
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Result.Error(message = "Error loading Game News - IOException"))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Result.Error(message = "Error loading Game News - HttpException"))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.Error(message = "Error loading Game News"))
                return@flow
            }

            emit(Result.Success(newsFromApi.appnews.newsitems))
        }
    }
}