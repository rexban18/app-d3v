package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.model.*
import com.example.repository.AnimeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(private val repository: AnimeRepository) : ViewModel() {

    // Onboarding
    val onboardingCompleted = repository.onboardingCompleted.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // Auth status
    val currentUser = repository.currentUserState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // UI Auth State
    private val _authState = MutableStateFlow<UiState<SahidUser>>(UiState.Idle)
    val authState = _authState.asStateFlow()

    // Home Screen Lists
    val trendingAnimes = repository.getTrendingAnimes().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val latestAnimes = repository.getLatestAnimes().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val premiumPicks = repository.getPremiumPicks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(listOf("Ninja", "Cyberpunk", "Romance"))
    val recentSearches = _recentSearches.asStateFlow()

    val searchResults = searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            repository.getSearchResults(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Plans list
    val premiumPlans = repository.getPlans().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Redemption states
    private val _codeRedeemState = MutableStateFlow<UiState<Int>>(UiState.Idle)
    val codeRedeemState = _codeRedeemState.asStateFlow()

    // Subscriptions
    private val _paymentState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val paymentState = _paymentState.asStateFlow()

    // Filter Chips
    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter = _selectedFilter.asStateFlow()

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    // Methods
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addRecentSearch(query: String) {
        val current = _recentSearches.value.toMutableList()
        if (!current.contains(query) && query.isNotBlank()) {
            current.add(0, query)
            _recentSearches.value = current.take(5)
        }
    }

    fun removeRecentSearch(query: String) {
        val current = _recentSearches.value.toMutableList()
        current.remove(query)
        _recentSearches.value = current
    }

    fun clearQuery() {
        _searchQuery.value = ""
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            repository.completeOnboarding()
        }
    }

    fun login(email: String, password: String) {
        _authState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.login(email, password)
            result.onSuccess {
                _authState.value = UiState.Success(it)
            }
            result.onFailure {
                _authState.value = UiState.Error(it.message ?: "Invalid Email or Password Credentials")
            }
        }
    }

    fun registerUser(name: String, email: String, password: String) {
        _authState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.register(name, email, password)
            result.onSuccess {
                _authState.value = UiState.Success(it)
            }
            result.onFailure {
                _authState.value = UiState.Error(it.message ?: "Authentication creation failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _authState.value = UiState.Idle
        }
    }

    fun clearAuthState() {
        _authState.value = UiState.Idle
    }

    fun toggleLikeAnime(animeId: String) {
        viewModelScope.launch {
            repository.toggleLikeAnime(animeId)
        }
    }

    fun recordWatchProgress(animeId: String, epId: String, epNumber: Int, progressPercentage: Int) {
        viewModelScope.launch {
            repository.updateWatchProgress(animeId, epId, epNumber, progressPercentage)
        }
    }

    fun redeemCode(code: String) {
        _codeRedeemState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.redeemCode(code)
            result.onSuccess { days ->
                _codeRedeemState.value = UiState.Success(days)
            }
            result.onFailure { err ->
                _codeRedeemState.value = UiState.Error(err.message ?: "Invalid Code")
            }
        }
    }

    fun clearRedeemState() {
        _codeRedeemState.value = UiState.Idle
    }

    fun purchasePlan(planId: String) {
        _paymentState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.purchasePlan(planId)
            result.onSuccess {
                _paymentState.value = UiState.Success(it)
            }
            result.onFailure {
                _paymentState.value = UiState.Error(it.message ?: "Payment flow failed")
            }
        }
    }

    fun clearPaymentState() {
        _paymentState.value = UiState.Idle
    }

    fun updateProfileAvatar(avatarText: String) {
        viewModelScope.launch {
            repository.updateAvatar(avatarText)
        }
    }

    fun removeAccount() {
        viewModelScope.launch {
            repository.deleteAccount()
            _authState.value = UiState.Idle
        }
    }

    fun getEpisodes(animeId: String): Flow<List<Episode>> {
        return repository.getEpisodes(animeId)
    }

    fun insertAnime(anime: Anime) {
        viewModelScope.launch {
            repository.insertAnime(anime)
        }
    }

    fun insertEpisode(episode: Episode) {
        viewModelScope.launch {
            repository.insertEpisode(episode)
        }
    }

    fun clearAllAnime() {
        viewModelScope.launch {
            repository.clearAllAnime()
        }
    }
}

sealed interface UiState<out T> {
    object Idle : UiState<Nothing>
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class MainViewModelFactory(private val repository: AnimeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
