package marwen.gameid.gamenews.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import marwen.gameid.gamenews.data.GameNewsRepository
import marwen.gameid.gamenews.data.Result
import marwen.gameid.gamenews.data.model.Newsitem

class GameNewsViewModel(
    private val gameNewsRepository: GameNewsRepository
): ViewModel() {

    private val _gameNews = MutableStateFlow<List<Newsitem>>(emptyList())
    val gameNews = _gameNews.asStateFlow()

    private val _showErrorToastChannel = Channel<Boolean>()
    val showErrorToastChannel = _showErrorToastChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            gameNewsRepository.getNewsList().collectLatest { result ->
                when(result){
                    is Result.Error -> {
                        _showErrorToastChannel.send(true)
                    }
                    is Result.Success -> {
                        result.data?.let { news ->
                            _gameNews.update { news }
                        }
                    }
                }
            }
        }
    }
}