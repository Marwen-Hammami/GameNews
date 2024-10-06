package marwen.gameid.gamenews.data

import kotlinx.coroutines.flow.Flow
import marwen.gameid.gamenews.data.model.Newsitem

interface GameNewsRepository {
    suspend fun getNewsList(): Flow<Result<List<Newsitem>>>
}