package marwen.gameid.gamenews

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.flow.collectLatest
import marwen.gameid.gamenews.data.GameNewsRepositoryImpl
import marwen.gameid.gamenews.data.model.Newsitem
import marwen.gameid.gamenews.presentation.GameNewsViewModel
import org.jsoup.Jsoup
import java.util.regex.Pattern

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<GameNewsViewModel>(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GameNewsViewModel(GameNewsRepositoryImpl(RetrofitInstance.api)) as T
            }
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val newsList = viewModel.gameNews.collectAsState().value
                val context = LocalContext.current

                LaunchedEffect(key1 = viewModel.showErrorToastChannel) {
                    viewModel.showErrorToastChannel.collectLatest { show ->
                        if (show){
                            Toast.makeText(
                                context, "Error with data in Main", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                if (newsList.isEmpty()){
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(newsList.size){ index ->
                            Newsitem(newsList[index], context)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

            }
        }
    }
}

// Extract the HTML or BBCode image link and returns it to be displayed.
// If no image is found in the description, it returns the game image.
fun extractImageUrl(newsitem: Newsitem): String? {
    // 1. Parse for HTML <img> tags
    val doc = Jsoup.parse(newsitem.contents)
    val imgElement = doc.selectFirst("img[src]") // Select first <img> tag with src attribute
    if (imgElement != null) {
        return imgElement.attr("src") // Return the src attribute value
    }

    // 2. Parse for BBCode-like [img]...[/img]
    val bbcodePattern = Pattern.compile("\\[img\\](.*?)\\[/img\\]", Pattern.DOTALL)
    val bbcodeMatcher = bbcodePattern.matcher(newsitem.contents)
    if (bbcodeMatcher.find()) {
        return bbcodeMatcher.group(1) // Return the content between [img] and [/img]
    }

    // 3. Return game if no image found
    return "https://steamcdn-a.akamaihd.net/steam/apps/" + newsitem.appid + "/header.jpg"
}

@Composable
fun Newsitem(newsitem: Newsitem, context: Context){
    val newsImage = extractImageUrl(newsitem)
    val imageState = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current).data(newsImage)
            .size(Size.ORIGINAL).build()
    ).state

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .height(300.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsitem.url))
                context.startActivity(intent)
            }
    ) {
        if (imageState is AsyncImagePainter.State.Error){
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        if (imageState is AsyncImagePainter.State.Success){
            Image(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                painter = imageState.painter,
                contentDescription = newsitem.title,
                contentScale = ContentScale.Crop
                )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // News title
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = newsitem.title,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        // News description
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = newsitem.contents,
            fontSize = 13.sp,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis // ...
        )


    }
}