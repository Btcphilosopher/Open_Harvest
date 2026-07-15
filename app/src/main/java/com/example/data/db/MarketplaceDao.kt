package com.example.data.db

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketplaceDao {
    
    // Producers
    @Query("SELECT * FROM producers WHERE isApproved = 1")
    fun getAllProducers(): Flow<List<Producer>>

    @Query("SELECT * FROM producers WHERE id = :id")
    suspend fun getProducerById(id: String): Producer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducer(producer: Producer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducers(producers: List<Producer>)

    @Update
    suspend fun updateProducer(producer: Producer)

    // Products
    @Query("SELECT * FROM products WHERE status = 'Live'")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: String): Product?

    @Query("SELECT * FROM products WHERE producerId = :producerId")
    fun getProductsByProducer(producerId: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE category = :category AND status = 'Live'")
    fun getProductsByCategory(category: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE isFeatured = 1 AND status = 'Live'")
    fun getFeaturedProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE isSeasonal = 1 AND status = 'Live'")
    fun getSeasonalProducts(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    // Favorites
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<Favorite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Favorite)

    @Query("DELETE FROM favorites WHERE producerId = :producerId")
    suspend fun deleteFavorite(producerId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE producerId = :producerId)")
    fun isFavorite(producerId: String): Flow<Boolean>

    // Orders
    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: String): Order?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Update
    suspend fun updateOrder(order: Order)

    // Order Items
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItemsForOrder(orderId: String): List<OrderItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(orderItem: OrderItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(orderItems: List<OrderItem>)
}
