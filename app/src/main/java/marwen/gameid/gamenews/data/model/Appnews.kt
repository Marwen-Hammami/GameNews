package marwen.gameid.gamenews.data.model

data class Appnews(
    val appid: Int,
    val count: Int,
    val newsitems: List<Newsitem>
)