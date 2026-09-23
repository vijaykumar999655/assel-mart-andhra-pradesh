package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.EncryptedMessageEntity
import com.example.data.model.EscrowOrderEntity
import com.example.data.model.RoosterEntity
import com.example.data.model.SellerProfileEntity
import com.example.data.model.UserAccountEntity
import com.example.data.model.UserTransactionEntity
import com.example.data.repository.AseelRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class UserLocation(
    val cityName: String,
    val latitude: Double,
    val longitude: Double
)

enum class SortOption {
    NEAREST_DISTANCE,
    PRICE_LOW_HIGH,
    PRICE_HIGH_LOW,
    TOP_RATED
}

private data class FilterCriteria(
    val location: UserLocation,
    val query: String,
    val breed: String,
    val district: String,
    val sort: SortOption
)

@OptIn(ExperimentalCoroutinesApi::class)
class MarketplaceViewModel(application: Application) : AndroidViewModel(application) {
    val repository = AseelRepository(application)

    // Andhra Locations for Real-Time GPS simulation & user testing
    val availableLocations = listOf(
        UserLocation("Bhimavaram (WG)", 16.5449, 81.5212),
        UserLocation("Rajahmundry (EG)", 16.9891, 81.7840),
        UserLocation("Vijayawada (KRI)", 16.5062, 80.6480),
        UserLocation("Guntur (GNT)", 16.3067, 80.4365),
        UserLocation("Kakinada (EG)", 16.9890, 82.2474),
        UserLocation("Tanuku (WG)", 16.7583, 81.6889)
    )

    private val _currentUserLocation = MutableStateFlow(availableLocations[0])
    val currentUserLocation: StateFlow<UserLocation> = _currentUserLocation

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedBreedFilter = MutableStateFlow("All")
    val selectedBreedFilter: StateFlow<String> = _selectedBreedFilter

    private val _selectedDistrictFilter = MutableStateFlow("All")
    val selectedDistrictFilter: StateFlow<String> = _selectedDistrictFilter

    private val _selectedSort = MutableStateFlow(SortOption.NEAREST_DISTANCE)
    val selectedSort: StateFlow<SortOption> = _selectedSort

    val allSellers: StateFlow<List<SellerProfileEntity>> = repository.allSellers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEscrowOrders: StateFlow<List<EscrowOrderEntity>> = repository.allEscrowOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current Authenticated User Session
    private val _currentUserId = MutableStateFlow<String?>("user_buyer_vijay")
    val currentUserId: StateFlow<String?> = _currentUserId

    val currentUser: StateFlow<UserAccountEntity?> = _currentUserId
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else repository.getUserById(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userTransactions: StateFlow<List<UserTransactionEntity>> = _currentUserId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getUserTransactions(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserAccountEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val filterCriteria: Flow<FilterCriteria> = combine(
        _currentUserLocation,
        _searchQuery,
        _selectedBreedFilter,
        _selectedDistrictFilter,
        _selectedSort
    ) { location, query, breed, district, sort ->
        FilterCriteria(location, query, breed, district, sort)
    }

    // Filtered & Distance-Annotated Roosters
    val filteredRoosters: StateFlow<List<Pair<RoosterEntity, Double>>> = combine(
        repository.allRoosters,
        filterCriteria
    ) { roosters, criteria ->
        roosters
            .filter { rooster ->
                val matchesQuery = criteria.query.isBlank() ||
                        rooster.breedName.contains(criteria.query, ignoreCase = true) ||
                        rooster.farmDistrict.contains(criteria.query, ignoreCase = true) ||
                        rooster.farmMandal.contains(criteria.query, ignoreCase = true) ||
                        rooster.sellerName.contains(criteria.query, ignoreCase = true)

                val matchesBreed = criteria.breed == "All" || rooster.breedType.equals(criteria.breed, ignoreCase = true)
                val matchesDistrict = criteria.district == "All" || rooster.farmDistrict.equals(criteria.district, ignoreCase = true)

                matchesQuery && matchesBreed && matchesDistrict
            }
            .map { rooster ->
                val distance = repository.calculateDistanceKm(
                    criteria.location.latitude,
                    criteria.location.longitude,
                    rooster.farmLatitude,
                    rooster.farmLongitude
                )
                Pair(rooster, distance)
            }
            .sortedWith { a, b ->
                when (criteria.sort) {
                    SortOption.NEAREST_DISTANCE -> a.second.compareTo(b.second)
                    SortOption.PRICE_LOW_HIGH -> a.first.priceRupees.compareTo(b.first.priceRupees)
                    SortOption.PRICE_HIGH_LOW -> b.first.priceRupees.compareTo(a.first.priceRupees)
                    SortOption.TOP_RATED -> b.first.rating.compareTo(a.first.rating)
                }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setUserLocation(location: UserLocation) {
        _currentUserLocation.value = location
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setBreedFilter(breed: String) {
        _selectedBreedFilter.value = breed
    }

    fun setDistrictFilter(district: String) {
        _selectedDistrictFilter.value = district
    }

    fun setSortOption(sort: SortOption) {
        _selectedSort.value = sort
    }

    // Active Chat Management
    private val _activeChatRoosterId = MutableStateFlow<String?>(null)
    val activeChatRoosterId: StateFlow<String?> = _activeChatRoosterId

    val activeChatMessages: StateFlow<List<EncryptedMessageEntity>> = _activeChatRoosterId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getMessagesForRooster(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectRoosterForChat(roosterId: String) {
        _activeChatRoosterId.value = roosterId
    }

    fun sendBuyerMessage(roosterId: String, sellerId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val senderName = currentUser.value?.fullName ?: "You (Buyer)"
            repository.sendEncryptedMessage(
                roosterId = roosterId,
                sellerId = sellerId,
                senderName = senderName,
                senderRole = if (currentUser.value?.userRole == "SELLER") "SELLER" else "BUYER",
                plainText = text
            )

            // Realistic Breeder simulated auto-response
            kotlinx.coroutines.delay(1200)
            val autoReply = when {
                text.contains("visit", ignoreCase = true) || text.contains("see", ignoreCase = true) ->
                    "Sure! You are welcome to visit our farm. Please come between 8:00 AM and 5:00 PM. We will show you the parents' bloodline and live physical demonstration."
                text.contains("price", ignoreCase = true) || text.contains("discount", ignoreCase = true) ->
                    "The price reflects 100% verified pure genetics and pedigree health vaccinations. We can offer a small goodwill courtesy for sincere heritage enthusiasts upon farm visit."
                text.contains("video", ignoreCase = true) ->
                    "I can send a high-resolution live video of the rooster running and crowing via our encrypted media channel. Leg ring ID is clearly visible."
                else ->
                    "Thank you for contacting! This rooster has active veterinary health clearance. You can lock in the purchase via Escrow for 100% security."
            }
            repository.sendEncryptedMessage(
                roosterId = roosterId,
                sellerId = sellerId,
                senderName = "Breeder Farm",
                senderRole = "SELLER",
                plainText = autoReply
            )
        }
    }

    // ==================== User Authentication & Profile Logic ====================

    fun login(
        phoneOrEmail: String,
        passwordPlain: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.loginUser(phoneOrEmail, passwordPlain)
            result.onSuccess { user ->
                _currentUserId.value = user.userId
                onResult(true, null)
            }.onFailure { err ->
                onResult(false, err.message)
            }
        }
    }

    fun loginWithOtp(
        phone: String,
        otp: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.loginWithOtp(phone, otp)
            result.onSuccess { user ->
                _currentUserId.value = user.userId
                onResult(true, null)
            }.onFailure { err ->
                onResult(false, err.message)
            }
        }
    }

    fun register(
        fullName: String,
        phone: String,
        email: String,
        passwordPlain: String,
        role: String,
        farmName: String?,
        district: String,
        mandal: String,
        address: String,
        privacyConsentAccepted: Boolean,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.registerUser(
                fullName = fullName,
                phone = phone,
                email = email,
                passwordPlain = passwordPlain,
                userRole = role,
                farmName = farmName,
                district = district,
                mandal = mandal,
                address = address,
                privacyConsentAccepted = privacyConsentAccepted
            )
            result.onSuccess { user ->
                _currentUserId.value = user.userId
                onResult(true, null)
            }.onFailure { err ->
                onResult(false, err.message)
            }
        }
    }

    fun switchDemoAccount(userId: String) {
        _currentUserId.value = userId
    }

    fun logout() {
        _currentUserId.value = null
    }

    fun submitKycVerification(docType: String, docNumber: String) {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            repository.updateKycVerification(uid, docType, docNumber)
        }
    }

    fun updateFarmInspection(status: String, notes: String, vetCertId: String) {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            val inspectorName = "Dr. K. Satyanarayana, AP Animal Husbandry Dept"
            repository.updateFarmInspectionStatus(uid, status, inspectorName, notes, vetCertId)
        }
    }

    // Seller Registration & Add Rooster Form
    fun registerNewRooster(
        breedName: String,
        breedType: String,
        lineage: String,
        ageMonths: Int,
        weightKg: Double,
        heightInches: Double,
        plumageColor: String,
        spursCondition: String,
        vaccinationStatus: String,
        priceRupees: Long,
        sellerName: String,
        sellerPhone: String,
        farmName: String,
        farmDistrict: String,
        farmMandal: String,
        farmAddress: String,
        description: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val newId = "rooster_" + UUID.randomUUID().toString().take(8)
            val ringTag = "AP-${farmDistrict.take(2).uppercase()}-2024-${(1000..9999).random()}"

            // Coordinates roughly near the district
            val (lat, lon) = when (farmDistrict) {
                "West Godavari" -> Pair(16.5449 + (Math.random() - 0.5) * 0.05, 81.5212 + (Math.random() - 0.5) * 0.05)
                "East Godavari" -> Pair(16.9891 + (Math.random() - 0.5) * 0.05, 81.7840 + (Math.random() - 0.5) * 0.05)
                "Krishna" -> Pair(16.5062 + (Math.random() - 0.5) * 0.05, 80.6480 + (Math.random() - 0.5) * 0.05)
                "Guntur" -> Pair(16.3067 + (Math.random() - 0.5) * 0.05, 80.4365 + (Math.random() - 0.5) * 0.05)
                else -> Pair(16.5449, 81.5212)
            }

            val rooster = RoosterEntity(
                id = newId,
                breedName = breedName,
                breedType = breedType,
                lineage = lineage,
                ageMonths = ageMonths,
                weightKg = weightKg,
                heightInches = heightInches,
                plumageColor = plumageColor,
                spursCondition = spursCondition,
                vaccinationStatus = vaccinationStatus,
                ringTagId = ringTag,
                priceRupees = priceRupees,
                sellerId = _currentUserId.value ?: ("seller_reg_" + sellerName.hashCode()),
                sellerName = sellerName,
                sellerPhone = sellerPhone,
                sellerVerified = true,
                farmName = farmName,
                farmDistrict = farmDistrict,
                farmMandal = farmMandal,
                farmAddress = farmAddress,
                farmLatitude = lat,
                farmLongitude = lon,
                description = description,
                imageResName = "rooster_dega_nemali",
                badgeLevel = "Farmer Registered",
                rating = 4.8f,
                isFeatured = true
            )
            repository.insertRooster(rooster)

            // Record transaction listing entry if logged in
            val uid = _currentUserId.value
            if (uid != null) {
                repository.recordUserTransaction(
                    userId = uid,
                    roosterId = newId,
                    roosterBreed = breedName,
                    transactionType = "SALE",
                    amountRupees = priceRupees,
                    counterpartyName = "Listed on AseelMart AP",
                    counterpartyPhone = sellerPhone,
                    farmLocation = "$farmMandal, $farmDistrict",
                    escrowOrderId = "LISTING-" + (1000..9999).random(),
                    status = "ACTIVE_LISTED"
                )
            }

            onSuccess()
        }
    }

    // Escrow Order Flow
    fun initiateEscrow(
        rooster: RoosterEntity,
        paymentMethod: String,
        buyerName: String,
        onOrderCreated: (String) -> Unit
    ) {
        viewModelScope.launch {
            val orderId = repository.createEscrowOrder(
                roosterId = rooster.id,
                roosterBreed = rooster.breedName,
                amountRupees = rooster.priceRupees,
                buyerName = buyerName,
                sellerName = rooster.sellerName,
                farmLocation = "${rooster.farmMandal}, ${rooster.farmDistrict}",
                paymentMethod = paymentMethod
            )

            // Record in Buyer's Purchase History
            val uid = _currentUserId.value ?: "user_buyer_vijay"
            repository.recordUserTransaction(
                userId = uid,
                roosterId = rooster.id,
                roosterBreed = rooster.breedName,
                transactionType = "PURCHASE",
                amountRupees = rooster.priceRupees,
                counterpartyName = rooster.sellerName,
                counterpartyPhone = rooster.sellerPhone,
                farmLocation = "${rooster.farmMandal}, ${rooster.farmDistrict}",
                escrowOrderId = orderId,
                status = "ESCROW_LOCKED"
            )

            // Also record in Breeder's Sale History if breeder has account
            if (rooster.sellerId.isNotBlank()) {
                repository.recordUserTransaction(
                    userId = rooster.sellerId,
                    roosterId = rooster.id,
                    roosterBreed = rooster.breedName,
                    transactionType = "SALE",
                    amountRupees = rooster.priceRupees,
                    counterpartyName = buyerName,
                    counterpartyPhone = "+91 98480 XXXXX",
                    farmLocation = "${rooster.farmMandal}, ${rooster.farmDistrict}",
                    escrowOrderId = orderId,
                    status = "ESCROW_LOCKED"
                )
            }

            onOrderCreated(orderId)
        }
    }

    fun progressEscrowStage(orderId: String, currentStatus: String) {
        viewModelScope.launch {
            val nextStatus = when (currentStatus) {
                "ESCROW_LOCKED" -> "FARM_INSPECTION_PENDING"
                "FARM_INSPECTION_PENDING" -> "VET_HEALTH_VERIFIED"
                "VET_HEALTH_VERIFIED" -> "PAYOUT_RELEASED"
                else -> currentStatus
            }
            repository.updateEscrowStatus(orderId, nextStatus)
        }
    }
}
