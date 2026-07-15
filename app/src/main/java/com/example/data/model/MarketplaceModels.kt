package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "producers")
data class Producer(
    @PrimaryKey val id: String,
    val name: String,
    val logoUrl: String,
    val description: String,
    val location: String,
    val region: String,
    val certifications: String, // Comma-separated (e.g. "USDA Organic, Demeter Biodynamic")
    val deliveryRegions: String, // Comma-separated regions
    val openingHours: String,
    val rating: Double,
    val yearsTrading: Int,
    val sustainabilityPractices: String,
    val isApproved: Boolean = true
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String,
    val producerId: String,
    val producerName: String,
    val name: String,
    val description: String,
    val category: String, // "Fruit", "Vegetables", "Dairy", "Eggs", "Honey", "Bakery", "Meat", "Fish", "Drinks", "Preserves", "Flowers", "Artisan"
    val origin: String,
    val harvestDate: String,
    val bestBefore: String,
    val organicCertification: String?, // Nullable
    val weight: String, // e.g. "500g", "1kg", "1 Jar"
    val unitPrice: Double, // in USD equivalent (but can pay in BTC/USDT)
    val quantityAvailable: Int,
    val status: String, // "Draft", "Live", "Hidden", "Sold Out", "Archived", "Scheduled"
    val nutritionInfo: String, // Comma-separated or description
    val allergens: String, // Comma-separated or "None"
    val deliveryOptions: String, // Comma-separated e.g. "Same-Day Delivery, Collection"
    val collectionTimes: String,
    val isFeatured: Boolean = false,
    val isSeasonal: Boolean = false,
    val imageResName: String // Name of local placeholder or generated image (e.g., "img_hero_banner")
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey val id: String, // Order UUID or Reference (e.g. OH-9824-A)
    val customerName: String,
    val customerEmail: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val deliveryType: String, // "Local Delivery" or "Collection"
    val scheduledTime: String,
    val orderNotes: String,
    val discountCode: String,
    val totalAmount: Double, // USD
    val paymentMethod: String, // "Bitcoin (BTC)" or "Tether (USDT)"
    val paymentStatus: String, // "Pending", "Confirmed", "Failed", "Refunded"
    val orderStatus: String, // "Processing", "Delivered", "Cancelled"
    val orderDate: Long = System.currentTimeMillis(),
    
    // Crypto details
    val walletAddress: String,
    val qrCodeData: String,
    val amountCrypto: Double, // BTC/USDT amount
    val txHash: String = "",
    val countdownSeconds: Int = 900 // 15 mins
)

@Entity(tableName = "order_items")
data class OrderItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val priceAtPurchase: Double
)

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey val producerId: String
)
