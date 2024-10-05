package marwen.gameid.gamenews.data

import marwen.gameid.gamenews.data.model.Gamenews
import retrofit2.http.GET
import retrofit2.http.Query

interface GameApi {

    @GET("ISteamNews/GetNewsForApp/v2")
    suspend fun getNewsList(
        @Query("appid") appid: String
    ): Gamenews

    companion object {
        const val BASE_URL = "https://api.steampowered.com/"
    }
}