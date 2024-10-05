package marwen.gameid.gamenews.data.model

data class Newsitem(
    val appid: Int,
    val author: String,
    val contents: String,
    val date: Int,
    val feed_type: Int,
    val feedlabel: String,
    val feedname: String,
    val gid: String,
    val is_external_url: Boolean,
    val tags: List<String>,
    val title: String,
    val url: String
)