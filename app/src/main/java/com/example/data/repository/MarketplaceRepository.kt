package com.example.data.repository

import com.example.data.db.MarketplaceDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class MarketplaceRepository(private val dao: MarketplaceDao) {

    val allProducers: Flow<List<Producer>> = dao.getAllProducers()
    val allProducts: Flow<List<Product>> = dao.getAllProducts()
    val featuredProducts: Flow<List<Product>> = dao.getFeaturedProducts()
    val seasonalProducts: Flow<List<Product>> = dao.getSeasonalProducts()
    val allOrders: Flow<List<Order>> = dao.getAllOrders()
    val allFavorites: Flow<List<Favorite>> = dao.getAllFavorites()

    suspend fun getProducerById(id: String): Producer? = dao.getProducerById(id)
    suspend fun getProductById(id: String): Product? = dao.getProductById(id)
    
    fun getProductsByProducer(producerId: String): Flow<List<Product>> = 
        dao.getProductsByProducer(producerId)
        
    fun getProductsByCategory(category: String): Flow<List<Product>> = 
        dao.getProductsByCategory(category)

    suspend fun addProduct(product: Product) = dao.insertProduct(product)
    suspend fun updateProduct(product: Product) = dao.updateProduct(product)
    suspend fun deleteProduct(product: Product) = dao.deleteProduct(product)

    suspend fun addProducer(producer: Producer) = dao.insertProducer(producer)
    suspend fun updateProducer(producer: Producer) = dao.updateProducer(producer)

    // Favorites
    suspend fun toggleFavorite(producerId: String) {
        val favs = dao.getAllFavorites().first()
        val exists = favs.any { it.producerId == producerId }
        if (exists) {
            dao.deleteFavorite(producerId)
        } else {
            dao.insertFavorite(Favorite(producerId))
        }
    }
    
    fun isFavorite(producerId: String): Flow<Boolean> = dao.isFavorite(producerId)

    // Orders
    suspend fun placeOrder(order: Order, items: List<OrderItem>) {
        dao.insertOrder(order)
        dao.insertOrderItems(items)
        
        // Subtract stock level
        for (item in items) {
            dao.getProductById(item.productId)?.let { product ->
                val newQty = (product.quantityAvailable - item.quantity).coerceAtLeast(0)
                val updatedStatus = if (newQty == 0) "Sold Out" else product.status
                dao.insertProduct(product.copy(quantityAvailable = newQty, status = updatedStatus))
            }
        }
    }

    suspend fun getOrderById(orderId: String): Order? = dao.getOrderById(orderId)
    suspend fun getOrderItems(orderId: String): List<OrderItem> = dao.getOrderItemsForOrder(orderId)
    suspend fun updateOrder(order: Order) = dao.updateOrder(order)

    // Seed Data
    suspend fun populateDefaultDataIfEmpty() {
        val producersList = dao.getAllProducers().first()
        if (producersList.isNotEmpty()) return

        // Create default producers matching "American Hypermodernism"
        val producers = listOf(
            Producer(
                id = "prod_1",
                name = "Sweetwater Organic Farm",
                logoUrl = "https://images.unsplash.com/photo-1500937386664-56d1dfef3854?w=120&auto=format&fit=crop&q=80",
                description = "Operating a state-of-the-art geothermal solar-powered greenhouse in Hood River, Oregon. We specialize in pristine salad greens, heritage heirloom vegetables, and organic kitchen herbs grown without synthetic inputs.",
                location = "Hood River, Oregon",
                region = "Pacific Northwest",
                certifications = "USDA Organic, Salmon-Safe, Certified Biodynamic",
                deliveryRegions = "Portland Metro, Hood River, Columbia Gorge",
                openingHours = "08:00 AM - 05:00 PM (Mon-Sat)",
                rating = 4.9,
                yearsTrading = 12,
                sustainabilityPractices = "Geothermal greenhouse climate control, closed-loop rainwater collection, solar-powered delivery vehicles, 100% compostable packaging."
            ),
            Producer(
                id = "prod_2",
                name = "Cascade Meadow Dairy",
                logoUrl = "https://images.unsplash.com/photo-1527156279075-f187ee3363ad?w=120&auto=format&fit=crop&q=80",
                description = "Our cows roam the lush alpine foothills of Cascade Range. We practice rotational intensive grazing to produce high-fat, cream-on-top raw milk, cultured butter, and traditional cave-aged farmhouse cheese.",
                location = "Eatonville, Washington",
                region = "Cascade Foothills",
                certifications = "Animal Welfare Approved, Non-GMO Project Verified",
                deliveryRegions = "Seattle Metro, Tacoma, Bellevue",
                openingHours = "07:00 AM - 04:00 PM (Mon-Fri)",
                rating = 4.8,
                yearsTrading = 20,
                sustainabilityPractices = "Rotational intensive pasture grazing, anaerobic manure digestion generating 100% farm electricity, zero antibiotics or synthetic hormones."
            ),
            Producer(
                id = "prod_3",
                name = "Golden Valley Beekeeping",
                logoUrl = "https://images.unsplash.com/photo-1473081556163-2a17de81fc97?w=120&auto=format&fit=crop&q=80",
                description = "Sustainably managing over two hundred beehives across wild wildflower meadows in the Willamette Valley. We harvest pure raw honeycomb, unfiltered single-varietal honey, and hand-pour beeswax candles.",
                location = "Silverton, Oregon",
                region = "Willamette Valley",
                certifications = "Certified Naturally Grown, Bee Friendly Farming",
                deliveryRegions = "Portland, Salem, Eugene",
                openingHours = "09:00 AM - 06:00 PM (Mon-Sat)",
                rating = 4.9,
                yearsTrading = 8,
                sustainabilityPractices = "No chemical hive treatments, organic meadow land buffers to prevent pesticide exposure, support of wild pollinator habitat restoration."
            ),
            Producer(
                id = "prod_4",
                name = "Sunrise Stoneground Bakery",
                logoUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=120&auto=format&fit=crop&q=80",
                description = "Baking real wood-fired sourdough bread using 100% locally grown heritage grains. Every grain is milled fresh daily on our natural Austrian stone mills to retain nutrient density and flavor.",
                location = "Snohomish, Washington",
                region = "Snohomish Valley",
                certifications = "Organic Grain Coalition Approved",
                deliveryRegions = "Everett, Seattle, Snohomish County",
                openingHours = "06:00 AM - 02:00 PM (Daily)",
                rating = 4.7,
                yearsTrading = 6,
                sustainabilityPractices = "Exclusively local heritage grains, wild natural yeast cultures (no commercial baker's yeast), fuel-efficient wood ovens heated with local forest scrap timber."
            ),
            Producer(
                id = "prod_5",
                name = "Pacific Hook & Line",
                logoUrl = "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?w=120&auto=format&fit=crop&q=80",
                description = "A cooperative of independent generational fishermen catching Sockeye Salmon, Pacific Halibut, and Rockfish. We use hand-operated hook-and-line fishing to eliminate bycatch and protect ocean floor ecosystems.",
                location = "Newport, Oregon",
                region = "Oregon Coast",
                certifications = "Marine Stewardship Council (MSC) Certified, Seafood Watch Green Light",
                deliveryRegions = "Coastal Counties, Portland Metro",
                openingHours = "05:00 AM - 03:00 PM (Mon-Sat)",
                rating = 4.9,
                yearsTrading = 15,
                sustainabilityPractices = "Strictly hook-and-line single catch method, no net dragging, real-time catch tracking and digital QR code origins."
            )
        )

        dao.insertProducers(producers)

        // Create default products matching the design specifications
        val products = listOf(
            Product(
                id = "p_1",
                producerId = "prod_1",
                producerName = "Sweetwater Organic Farm",
                name = "Cascadia Organic Heirloom Tomato Box",
                description = "A colorful assortment of rare heirloom tomatoes (Brandywine, Cherokee Purple, and Green Zebra) harvested at peak sugar levels. Incredibly sweet, deeply flavorful, and perfect for salads or slicing.",
                category = "Vegetables",
                origin = "Hood River Greenhouse #4",
                harvestDate = "Harvested yesterday morning",
                bestBefore = "5-7 days from delivery",
                organicCertification = "USDA Organic",
                weight = "1.5 kg box",
                unitPrice = 12.50,
                quantityAvailable = 45,
                status = "Live",
                nutritionInfo = "High in Lycopene, Vitamin C, Potassium, and Vitamin K.",
                allergens = "None",
                deliveryOptions = "Local Delivery, Collection",
                collectionTimes = "Wed & Sat (09:00 AM - 04:00 PM)",
                isFeatured = true,
                isSeasonal = true,
                imageResName = "img_hero_banner" // Fallback to our gorgeous banner image
            ),
            Product(
                id = "p_2",
                producerId = "prod_3",
                producerName = "Golden Valley Beekeeping",
                name = "Raw Wildflower Honeycomb Jar",
                description = "A beautiful section of fresh, hand-cut raw honeycomb suspended in pure, unfiltered summer wildflower honey. Contains active pollen, propolis, and natural enzymes straight from the hive.",
                category = "Honey",
                origin = "Willamette Meadow Hives",
                harvestDate = "Harvested July 10, 2026",
                bestBefore = "Indefinite storage life",
                organicCertification = "Certified Naturally Grown",
                weight = "450g glass jar",
                unitPrice = 14.00,
                quantityAvailable = 30,
                status = "Live",
                nutritionInfo = "100% Pure Honey. Rich in enzymes, antioxidant compounds, and active pollen.",
                allergens = "Honey (Not recommended for infants under 1 year)",
                deliveryOptions = "Local Delivery, Collection, Mail Shipping",
                collectionTimes = "Daily (10:00 AM - 05:00 PM)",
                isFeatured = true,
                isSeasonal = false,
                imageResName = "img_app_icon"
            ),
            Product(
                id = "p_3",
                producerId = "prod_2",
                producerName = "Cascade Meadow Dairy",
                name = "Grass-Fed Raw Milk Cheddar",
                description = "A semi-hard farmhouse cheddar made from unpasteurized summer milk and aged for six months in our stone cellars. Bold, sharp, complex, and buttery with tiny crystalline calcium crunches.",
                category = "Dairy",
                origin = "Alpine Foot-Hills Cellar #2",
                harvestDate = "Aged 6 Months",
                bestBefore = "3 months (keep refrigerated)",
                organicCertification = "Animal Welfare Certified",
                weight = "250g wedge",
                unitPrice = 16.50,
                quantityAvailable = 60,
                status = "Live",
                nutritionInfo = "High in Calcium, Protein, Vitamin B12, and healthy Omega-3 fats.",
                allergens = "Milk (Lactose)",
                deliveryOptions = "Local Delivery, Collection",
                collectionTimes = "Tue & Thu (08:00 AM - 12:00 PM)",
                isFeatured = true,
                isSeasonal = false,
                imageResName = "img_hero_banner"
            ),
            Product(
                id = "p_4",
                producerId = "prod_4",
                producerName = "Sunrise Stoneground Bakery",
                name = "Stoneground Wood-fired Sourdough Boule",
                description = "Our signature rustic sourdough boule baked on a stone hearth. Made from 100% snohomish-grown heritage red wheat milled daily, using a wild yeast culture active since 2018. Crisp dark blistered crust and a moist, airy, sour crumb.",
                category = "Bakery",
                origin = "Snohomish Bakery Oven #1",
                harvestDate = "Baked daily at 04:00 AM",
                bestBefore = "3-4 days (store in paper bag)",
                organicCertification = "Organic Heritage Grains",
                weight = "800g loaf",
                unitPrice = 8.00,
                quantityAvailable = 25,
                status = "Live",
                nutritionInfo = "No sugar, no commercial yeast, high in natural lactobacilli which aids digestion.",
                allergens = "Wheat (Gluten)",
                deliveryOptions = "Local Delivery, Collection",
                collectionTimes = "Mon-Sat (07:00 AM - 02:00 PM)",
                isFeatured = false,
                isSeasonal = false,
                imageResName = "img_app_icon"
            ),
            Product(
                id = "p_5",
                producerId = "prod_5",
                producerName = "Pacific Hook & Line",
                name = "Fresh Wild Sockeye Salmon Fillet",
                description = "Sustainably caught in cold Pacific waters and immediately flash-chilled. Deep ruby red flesh with rich fat lines, yielding an incredibly rich, buttery taste. Perfect for grilling or pan-searing.",
                category = "Fish",
                origin = "Newport Coast - Vessel F/V Cascadia",
                harvestDate = "Caught July 13, 2026",
                bestBefore = "3 days from delivery (or freeze)",
                organicCertification = "Seafood Watch Green Light",
                weight = "500g fillet",
                unitPrice = 24.00,
                quantityAvailable = 15,
                status = "Live",
                nutritionInfo = "Outstanding source of Omega-3 EPA/DHA fatty acids, Vitamin D, and clean lean protein.",
                allergens = "Fish",
                deliveryOptions = "Local Delivery, Collection",
                collectionTimes = "Fri & Sat (10:00 AM - 03:00 PM)",
                isFeatured = false,
                isSeasonal = true,
                imageResName = "img_hero_banner"
            ),
            Product(
                id = "p_6",
                producerId = "prod_1",
                producerName = "Sweetwater Organic Farm",
                name = "Weekly Farm-to-Table Harvest Box",
                description = "The ultimate farm-to-table box representing our fields' finest current yields! A typical box contains: 1 bag of tender microgreens, 1 bunch of sweet baby carrots, 1 bunch of crisp radishes, 500g of heirloom cherry tomatoes, 1 bunch of organic sweet basil, and a head of crispy butterhead lettuce. Content changes weekly.",
                category = "Vegetables",
                origin = "Sweetwater Fields A & C",
                harvestDate = "Harvested on delivery morning",
                bestBefore = "Best consumed within 7 days",
                organicCertification = "100% Certified Organic",
                weight = "approx 3.5 kg box",
                unitPrice = 35.00,
                quantityAvailable = 30,
                status = "Live",
                nutritionInfo = "A complete nutritional spectrum of vitamins, dietary fiber, and trace minerals.",
                allergens = "None",
                deliveryOptions = "Local Delivery Only (Scheduled)",
                collectionTimes = "Delivery on Fridays only",
                isFeatured = true,
                isSeasonal = true,
                imageResName = "img_hero_banner"
            ),
            Product(
                id = "p_7",
                producerId = "prod_4",
                producerName = "Sunrise Stoneground Bakery",
                name = "Premium Dairy & Baker Breakfast Hamper",
                description = "An exquisite, curated local breakfast basket! Includes: 1 Cascadia Sourdough Boule, 1 wedge of Cascade Meadow Aged Cheddar, 250g of Alpine cultured Salted Butter, and 1 jar of raw Golden Valley honey. Everything you need for the perfect regional weekend brunch.",
                category = "Bakery",
                origin = "Collaborative Farm Box",
                harvestDate = "Freshly assembled on morning of order",
                bestBefore = "Consume fresh dairy and bread in 5 days",
                organicCertification = "100% Locally Sourced",
                weight = "approx 2.0 kg hamper",
                unitPrice = 45.00,
                quantityAvailable = 12,
                status = "Live",
                nutritionInfo = "Delicious, nutrient-dense breakfast pairing.",
                allergens = "Wheat (Gluten), Milk (Lactose)",
                deliveryOptions = "Local Delivery, Collection",
                collectionTimes = "Friday & Saturday Mornings",
                isFeatured = true,
                isSeasonal = false,
                imageResName = "img_app_icon"
            )
        )

        dao.insertProducts(products)
    }
}
