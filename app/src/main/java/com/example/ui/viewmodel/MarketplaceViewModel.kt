package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.MarketplaceDatabase
import com.example.data.model.*
import com.example.data.repository.MarketplaceRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class MarketplaceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MarketplaceRepository
    
    init {
        val database = MarketplaceDatabase.getDatabase(application)
        repository = MarketplaceRepository(database.marketplaceDao())
        
        // Populate sample data if DB is empty
        viewModelScope.launch {
            repository.populateDefaultDataIfEmpty()
        }
    }

    // --- DB Flow Expositions ---
    val producers: StateFlow<List<Producer>> = repository.allProducers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val products: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredProducts: StateFlow<List<Product>> = repository.featuredProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val seasonalProducts: StateFlow<List<Product>> = repository.seasonalProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<Order>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<Favorite>> = repository.allFavorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Search & Filtering State ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _organicFilterOnly = MutableStateFlow(false)
    val organicFilterOnly = _organicFilterOnly.asStateFlow()

    private val _selectedRegion = MutableStateFlow("All")
    val selectedRegion = _selectedRegion.asStateFlow()

    private val _maxPriceFilter = MutableStateFlow(100.0)
    val maxPriceFilter = _maxPriceFilter.asStateFlow()

    private val _deliveryOnlyFilter = MutableStateFlow(false)
    val deliveryOnlyFilter = _deliveryOnlyFilter.asStateFlow()

    private val _isGridView = MutableStateFlow(true)
    val isGridView = _isGridView.asStateFlow()

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setSelectedCategory(category: String) { _selectedCategory.value = category }
    fun setOrganicFilter(organic: Boolean) { _organicFilterOnly.value = organic }
    fun setSelectedRegion(region: String) { _selectedRegion.value = region }
    fun setMaxPrice(price: Double) { _maxPriceFilter.value = price }
    fun setDeliveryOnly(delivery: Boolean) { _deliveryOnlyFilter.value = delivery }
    fun toggleGridView() { _isGridView.value = !_isGridView.value }

    // Unified filtered product pipeline
    val filteredProducts: StateFlow<List<Product>> = combine(
        products, _searchQuery, _selectedCategory, _organicFilterOnly, _selectedRegion, _maxPriceFilter, _deliveryOnlyFilter
    ) { arr ->
        val pList = arr[0] as List<Product>
        val query = arr[1] as String
        val category = arr[2] as String
        val organic = arr[3] as Boolean
        val region = arr[4] as String
        val maxPrice = arr[5] as Double
        val deliveryOnly = arr[6] as Boolean

        pList.filter { p ->
            val matchesQuery = p.name.contains(query, ignoreCase = true) || p.description.contains(query, ignoreCase = true) || p.producerName.contains(query, ignoreCase = true)
            val matchesCategory = category == "All" || p.category.equals(category, ignoreCase = true)
            val matchesOrganic = !organic || (p.organicCertification != null && p.organicCertification.isNotEmpty())
            
            // Region match requires finding the producer first
            val producer = producers.value.find { it.id == p.producerId }
            val matchesRegion = region == "All" || (producer != null && producer.region.equals(region, ignoreCase = true))
            
            val matchesPrice = p.unitPrice <= maxPrice
            val matchesDelivery = !deliveryOnly || p.deliveryOptions.contains("Delivery", ignoreCase = true)
            
            matchesQuery && matchesCategory && matchesOrganic && matchesRegion && matchesPrice && matchesDelivery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Unique regions extracted from producers
    val availableRegions: StateFlow<List<String>> = producers.map { list ->
        list.map { it.region }.distinct()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Cart Management State ---
    private val _cart = MutableStateFlow<Map<String, Int>>(emptyMap()) // productId -> quantity
    val cart = _cart.asStateFlow()

    fun addToCart(product: Product, quantity: Int = 1) {
        val current = _cart.value.toMutableMap()
        val currentQty = current[product.id] ?: 0
        val targetQty = currentQty + quantity
        current[product.id] = targetQty.coerceAtMost(product.quantityAvailable)
        _cart.value = current
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        val current = _cart.value.toMutableMap()
        if (quantity <= 0) {
            current.remove(productId)
        } else {
            val product = products.value.find { it.id == productId }
            if (product != null) {
                current[productId] = quantity.coerceAtMost(product.quantityAvailable)
            }
        }
        _cart.value = current
    }

    fun removeFromCart(productId: String) {
        val current = _cart.value.toMutableMap()
        current.remove(productId)
        _cart.value = current
    }

    fun clearCart() {
        _cart.value = emptyMap()
    }

    val cartItemCount: StateFlow<Int> = cart.map { map ->
        map.values.sum()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cartTotal: StateFlow<Double> = cart.map { map ->
        map.entries.sumOf { entry ->
            val product = products.value.find { it.id == entry.key }
            (product?.unitPrice ?: 0.0) * entry.value
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // --- Favorites Management ---
    fun toggleFavorite(producerId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(producerId)
        }
    }

    fun isFavorite(producerId: String): Flow<Boolean> = repository.isFavorite(producerId)

    // --- Checkout & Payment States ---
    private val _activeOrder = MutableStateFlow<Order?>(null)
    val activeOrder = _activeOrder.asStateFlow()

    private val _activeOrderItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val activeOrderItems = _activeOrderItems.asStateFlow()

    private var countdownJob: Job? = null
    private var blockchainDetectionJob: Job? = null

    // Wallet Addresses mapping
    private val btcWalletAddress = "bc1qharvesthq9824m32x707dt03yuxz707ohwt"
    private val usdtWalletAddress = "0xHarvestUSDT707d707abcde1234567890fghijk"

    fun startCheckout(
        customerName: String,
        customerEmail: String,
        customerPhone: String,
        deliveryAddress: String,
        deliveryType: String,
        scheduledTime: String,
        orderNotes: String,
        discountCode: String,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            val orderId = "OH-${(1000..9999).random()}-${('A'..'Z').random()}"
            val totalUsd = cartTotal.value
            
            // Convert to cryptocurrency amounts roughly (e.g. BTC = $60k, USDT = $1)
            val btcRate = 60000.0
            val amountCrypto = if (paymentMethod.contains("Bitcoin")) {
                totalUsd / btcRate
            } else {
                totalUsd // 1:1 for USDT
            }

            val address = if (paymentMethod.contains("Bitcoin")) btcWalletAddress else usdtWalletAddress
            val qrCodeData = if (paymentMethod.contains("Bitcoin")) {
                "bitcoin:$address?amount=$amountCrypto&label=OpenHarvest&message=Order-$orderId"
            } else {
                "ethereum:$address?value=$amountCrypto"
            }

            val order = Order(
                id = orderId,
                customerName = customerName.ifEmpty { "Guest Customer" },
                customerEmail = customerEmail.ifEmpty { "guest@openharvest.com" },
                customerPhone = customerPhone.ifEmpty { "555-0199" },
                deliveryAddress = deliveryAddress.ifEmpty { "123 Community Hub" },
                deliveryType = deliveryType,
                scheduledTime = scheduledTime,
                orderNotes = orderNotes,
                discountCode = discountCode,
                totalAmount = totalUsd,
                paymentMethod = paymentMethod,
                paymentStatus = "Pending",
                orderStatus = "Processing",
                walletAddress = address,
                qrCodeData = qrCodeData,
                amountCrypto = amountCrypto,
                countdownSeconds = 900 // 15 mins
            )

            // Convert current cart to Order Items
            val items = cart.value.map { entry ->
                val product = products.value.find { it.id == entry.key }
                OrderItem(
                    orderId = orderId,
                    productId = entry.key,
                    productName = product?.name ?: "Unknown Product",
                    quantity = entry.value,
                    priceAtPurchase = product?.unitPrice ?: 0.0
                )
            }

            // Save in Database
            repository.placeOrder(order, items)
            
            // Set active states
            _activeOrder.value = order
            _activeOrderItems.value = items

            // Clear Cart upon checkout success
            clearCart()

            // Launch countdown and transaction monitoring simulation loops
            startCountdownTimer()
            startBlockchainDetectionSimulation()
        }
    }

    private fun startCountdownTimer() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val order = _activeOrder.value ?: break
                if (order.countdownSeconds <= 1) {
                    val expired = order.copy(paymentStatus = "Failed", countdownSeconds = 0)
                    _activeOrder.value = expired
                    repository.updateOrder(expired)
                    break
                }
                val updated = order.copy(countdownSeconds = order.countdownSeconds - 1)
                _activeOrder.value = updated
                repository.updateOrder(updated)
            }
        }
    }

    private fun startBlockchainDetectionSimulation() {
        blockchainDetectionJob?.cancel()
        blockchainDetectionJob = viewModelScope.launch {
            // Simulate the blockchain node listening and detecting the transaction after 10-12 seconds
            delay(12000)
            val order = _activeOrder.value ?: return@launch
            if (order.paymentStatus == "Pending") {
                val mockTxHash = "0x" + UUID.randomUUID().toString().replace("-", "").substring(0, 32)
                val confirmedOrder = order.copy(
                    paymentStatus = "Confirmed",
                    txHash = mockTxHash
                )
                _activeOrder.value = confirmedOrder
                repository.updateOrder(confirmedOrder)
            }
        }
    }

    fun forceMockPaymentDetection() {
        // Allows user to manually bypass the delay and simulate instant confirmation
        viewModelScope.launch {
            val order = _activeOrder.value ?: return@launch
            if (order.paymentStatus == "Pending") {
                val mockTxHash = "0x" + UUID.randomUUID().toString().replace("-", "").substring(0, 32)
                val confirmedOrder = order.copy(
                    paymentStatus = "Confirmed",
                    txHash = mockTxHash
                )
                _activeOrder.value = confirmedOrder
                repository.updateOrder(confirmedOrder)
            }
        }
    }

    fun dismissActiveOrder() {
        _activeOrder.value = null
        _activeOrderItems.value = emptyList()
        countdownJob?.cancel()
        blockchainDetectionJob?.cancel()
    }

    // --- Producer Dashboard Actions ---
    private val _selectedProducerId = MutableStateFlow("prod_1") // Sweetwater by default
    val selectedProducerId = _selectedProducerId.asStateFlow()

    val currentProducerProfile: StateFlow<Producer?> = combine(
        producers, _selectedProducerId
    ) { list, id ->
        list.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentProducerProducts: StateFlow<List<Product>> = combine(
        products, _selectedProducerId
    ) { list, id ->
        list.filter { it.producerId == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateProducerProfile(name: String, description: String, location: String, region: String, certifications: String, deliveryRegions: String, openingHours: String, sustainabilityPractices: String) {
        viewModelScope.launch {
            val current = currentProducerProfile.value ?: return@launch
            val updated = current.copy(
                name = name,
                description = description,
                location = location,
                region = region,
                certifications = certifications,
                deliveryRegions = deliveryRegions,
                openingHours = openingHours,
                sustainabilityPractices = sustainabilityPractices
            )
            repository.updateProducer(updated)
        }
    }

    fun addNewProduct(name: String, description: String, category: String, origin: String, harvestDate: String, bestBefore: String, organicCert: String?, weight: String, price: Double, qty: Int, isFeatured: Boolean, isSeasonal: Boolean) {
        viewModelScope.launch {
            val pId = "p_user_" + System.currentTimeMillis().toString().takeLast(6)
            val producer = currentProducerProfile.value ?: return@launch
            val newProd = Product(
                id = pId,
                producerId = producer.id,
                producerName = producer.name,
                name = name,
                description = description,
                category = category,
                origin = origin,
                harvestDate = harvestDate,
                bestBefore = bestBefore,
                organicCertification = organicCert,
                weight = weight,
                unitPrice = price,
                quantityAvailable = qty,
                status = "Live",
                nutritionInfo = "High quality homegrown fresh artisan selection.",
                allergens = "None",
                deliveryOptions = "Local Delivery, Collection",
                collectionTimes = "Contact producer",
                isFeatured = isFeatured,
                isSeasonal = isSeasonal,
                imageResName = "img_hero_banner"
            )
            repository.addProduct(newProd)
        }
    }

    fun editProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun duplicateProduct(product: Product) {
        viewModelScope.launch {
            val duplicated = product.copy(
                id = "p_dup_" + System.currentTimeMillis().toString().takeLast(6),
                name = "${product.name} (Copy)"
            )
            repository.addProduct(duplicated)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    // --- Admin Portal Controls ---
    fun setProducerApproval(producerId: String, isApproved: Boolean) {
        viewModelScope.launch {
            val p = producers.value.find { it.id == producerId }
            if (p != null) {
                repository.updateProducer(p.copy(isApproved = isApproved))
            }
        }
    }
}
