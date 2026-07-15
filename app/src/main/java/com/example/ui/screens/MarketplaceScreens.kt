package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.R
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarketplaceViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceApp(viewModel: MarketplaceViewModel) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf("home") }
    
    // Dialog / Detail States
    var selectedProductForDetail by remember { mutableStateOf<Product?>(null) }
    var selectedProducerForDetail by remember { mutableStateOf<Producer?>(null) }
    
    val cartCount by viewModel.cartItemCount.collectAsStateWithLifecycle()
    val activeOrder by viewModel.activeOrder.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_icon),
                            contentDescription = "Open Harvest Logo",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "EST. 2024",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 2.sp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "Open Harvest",
                                fontFamily = FontFamily.Serif,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                fontWeight = FontWeight.Light,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "Bitcoin & USDT Payments Secured natively", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("crypto_badge_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Secured Checkout",
                            tint = HoneyGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == "home",
                    onClick = { currentTab = "home" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_home")
                )
                NavigationBarItem(
                    selected = currentTab == "marketplace",
                    onClick = { currentTab = "marketplace" },
                    icon = { Icon(Icons.Default.Storefront, contentDescription = "Market") },
                    label = { Text("Market", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_market")
                )
                NavigationBarItem(
                    selected = currentTab == "farms",
                    onClick = { currentTab = "farms" },
                    icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Farms") },
                    label = { Text("Farms", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_farms")
                )
                NavigationBarItem(
                    selected = currentTab == "cart",
                    onClick = { currentTab = "cart" },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge { Text(cartCount.toString()) }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    },
                    label = { Text("Cart", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_cart")
                )
                NavigationBarItem(
                    selected = currentTab == "portals",
                    onClick = { currentTab = "portals" },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Portals") },
                    label = { Text("Portals", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_portals")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "tab_navigation"
            ) { targetTab ->
                when (targetTab) {
                    "home" -> HomeScreen(
                        viewModel = viewModel,
                        onProductSelect = { selectedProductForDetail = it },
                        onProducerSelect = { selectedProducerForDetail = it },
                        onExploreMarketplace = { currentTab = "marketplace" }
                    )
                    "marketplace" -> MarketplaceGridScreen(
                        viewModel = viewModel,
                        onProductSelect = { selectedProductForDetail = it },
                        onProducerSelect = { selectedProducerForDetail = it }
                    )
                    "farms" -> FarmsScreen(
                        viewModel = viewModel,
                        onProducerSelect = { selectedProducerForDetail = it }
                    )
                    "cart" -> CartCheckoutScreen(
                        viewModel = viewModel,
                        onProductSelect = { selectedProductForDetail = it }
                    )
                    "portals" -> PortalsScreen(
                        viewModel = viewModel,
                        onProductSelect = { selectedProductForDetail = it }
                    )
                }
            }
            
            // Render Blockchain Payment Screen overlays if there is an active checkout order
            activeOrder?.let { order ->
                CryptoPaymentOverlay(
                    order = order,
                    onDismiss = { viewModel.dismissActiveOrder() },
                    onPaidClick = { viewModel.forceMockPaymentDetection() }
                )
            }

            // Dialogs for detailed views
            selectedProductForDetail?.let { product ->
                ProductDetailDialog(
                    product = product,
                    onDismiss = { selectedProductForDetail = null },
                    onAddToCart = { viewModel.addToCart(product, it) },
                    onViewProducer = {
                        viewModel.producers.value.find { p -> p.id == product.producerId }?.let { prod ->
                            selectedProducerForDetail = prod
                        }
                        selectedProductForDetail = null
                    }
                )
            }

            selectedProducerForDetail?.let { producer ->
                ProducerDetailDialog(
                    producer = producer,
                    viewModel = viewModel,
                    onDismiss = { selectedProducerForDetail = null },
                    onProductSelect = { selectedProductForDetail = it }
                )
            }
        }
    }
}

// --- SCREEN 1: HOMEPAGE ---
@Composable
fun HomeScreen(
    viewModel: MarketplaceViewModel,
    onProductSelect: (Product) -> Unit,
    onProducerSelect: (Producer) -> Unit,
    onExploreMarketplace: () -> Unit
) {
    val featuredList by viewModel.featuredProducts.collectAsStateWithLifecycle()
    val seasonalList by viewModel.seasonalProducts.collectAsStateWithLifecycle()
    val producersList by viewModel.producers.collectAsStateWithLifecycle()
    
    var newsletterEmail by remember { mutableStateOf("") }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Image Editorial Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_banner),
                    contentDescription = "Greenhouse Hero Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                                startY = 100f
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(HoneyGold, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "DECENTRALIZED AGRI-TECH",
                            color = SoftWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Honest Food. Direct Sourcing.",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 34.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Connecting American micro-farms directly to regional community tables.",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Summer Bounty Editorial Headline Section from Design HTML
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Summer\nBounty",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Light,
                        fontSize = 44.sp,
                        lineHeight = 40.sp,
                        letterSpacing = (-1).sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Hyper-local seasonal produce, harvested at peak maturity and delivered within 24 hours.",
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
            }
        }

        // Crypto Badges Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftCharcoal),
                border = BorderStroke(1.dp, EcoGreen.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "NATIVE CRYPTO SUPPORT",
                            color = FreshLime,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Bitcoin (BTC) & Tether (USDT)",
                            color = SoftWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Zero-intermediary secure self-hosted payments.",
                            color = SlateGray,
                            fontSize = 11.sp
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .background(HoneyGold.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .border(1.dp, HoneyGold, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("BTC", color = HoneyGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .background(EcoGreen.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .border(1.dp, FreshLime, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("USDT", color = FreshLime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Horizontal Category Selectors
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "BROWSE CATEGORIES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateGray,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                    letterSpacing = 1.2.sp
                )
                val categories = listOf(
                    "Vegetables" to "🥬",
                    "Dairy" to "🧀",
                    "Honey" to "🍯",
                    "Bakery" to "🥖",
                    "Fish" to "🐟",
                    "Eggs" to "🥚",
                    "Drinks" to "☕",
                    "Artisan" to "🏺",
                    "Flowers" to "🌸"
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories) { (cat, emoji) ->
                        ElevatedCard(
                            onClick = {
                                viewModel.setSelectedCategory(cat)
                                onExploreMarketplace()
                            },
                            modifier = Modifier
                                .width(120.dp)
                                .height(64.dp)
                                .testTag("cat_button_$cat"),
                            colors = CardDefaults.elevatedCardColors(containerColor = SoftCharcoal)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(emoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(cat, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = SoftWhite)
                            }
                        }
                    }
                }
            }
        }

        // Weekly Farm Harvest Boxes (Highly requested editorial block)
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "WEEKLY HARVEST BOXES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateGray,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    letterSpacing = 1.2.sp
                )
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            val box = featuredList.find { it.id == "p_6" }
                            if (box != null) onProductSelect(box)
                        },
                    colors = CardDefaults.cardColors(containerColor = SoftCharcoal),
                    border = BorderStroke(1.dp, EcoGreen)
                ) {
                    Column {
                        Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                            Image(
                                painter = painterResource(id = R.drawable.img_hero_banner),
                                contentDescription = "Harvest Box Illustration",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                        )
                                    )
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text("WEEKLY HARVEST BOX", color = HoneyGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text("Fresh Organic Veggie Medley", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .background(EcoGreen, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("$35.00", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                        PaddingValues(12.dp).let {
                            Text(
                                text = "A meticulously curated seasonal box with microgreens, sweet heirloom tomatoes, basil, crisp baby carrots, and crisp radishes harvested fresh on Friday mornings.",
                                fontSize = 12.sp,
                                color = SoftWhite.copy(alpha = 0.8f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }

        // Horizontal list of Featured Farms
        item {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    text = "MEET OUR PRODUCERS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateGray,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp),
                    letterSpacing = 1.2.sp
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(producersList) { producer ->
                        Card(
                            onClick = { onProducerSelect(producer) },
                            modifier = Modifier
                                .width(220.dp)
                                .testTag("farm_card_${producer.id}"),
                            colors = CardDefaults.cardColors(containerColor = SoftCharcoal)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(EcoGreen.copy(alpha = 0.2f))
                                ) {
                                    Text(
                                        text = producer.name.take(1),
                                        modifier = Modifier.align(Alignment.Center),
                                        fontWeight = FontWeight.ExtraBold,
                                        color = FreshLime,
                                        fontSize = 18.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = producer.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SoftWhite,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = producer.location,
                                        fontSize = 11.sp,
                                        color = SlateGray,
                                        maxLines = 1
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = "Rating", modifier = Modifier.size(10.dp), tint = HoneyGold)
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(producer.rating.toString(), color = HoneyGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("${producer.yearsTrading} yrs", color = SlateGray, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Editorial Grid for Best Sellers / Featured Products
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "FEATURED HARVESTS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateGray,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    letterSpacing = 1.2.sp
                )
                
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    featuredList.filter { it.id != "p_6" }.forEach { product ->
                        Card(
                            onClick = { onProductSelect(product) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("featured_item_${product.id}"),
                            colors = CardDefaults.cardColors(containerColor = SoftCharcoal)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(
                                        id = if (product.imageResName == "img_app_icon") R.drawable.img_app_icon else R.drawable.img_hero_banner
                                    ),
                                    contentDescription = product.name,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        product.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SoftWhite,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        product.producerName,
                                        fontSize = 11.sp,
                                        color = FreshLime,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .background(EcoGreen.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(product.weight, color = FreshLime, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        if (product.organicCertification != null) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(HoneyGold.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("ORGANIC", color = HoneyGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "$${String.format("%.2f", product.unitPrice)}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = SoftWhite
                                    )
                                    Text("Live", color = FreshLime, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Newsletter signup
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftCharcoal),
                border = BorderStroke(1.dp, SlateGray.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "THE OPEN HARVEST CHRONICLE",
                        color = FreshLime,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Subscribe to regional supply drops",
                        color = SoftWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Receive alerts when local beekeepers and organic farms update their weekly harvest stock.",
                        color = SlateGray,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newsletterEmail,
                            onValueChange = { newsletterEmail = it },
                            placeholder = { Text("Enter email address", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("newsletter_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EcoGreen,
                                unfocusedBorderColor = SlateGray.copy(alpha = 0.4f)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newsletterEmail.isNotEmpty() && newsletterEmail.contains("@")) {
                                    Toast.makeText(context, "Subscribed successfully! Welcome to Open Harvest.", Toast.LENGTH_SHORT).show()
                                    newsletterEmail = ""
                                } else {
                                    Toast.makeText(context, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EcoGreen),
                            modifier = Modifier.testTag("newsletter_submit")
                        ) {
                            Text("Join", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN 2: MARKETPLACE GRID & FILTERS ---
@Composable
fun MarketplaceGridScreen(
    viewModel: MarketplaceViewModel,
    onProductSelect: (Product) -> Unit,
    onProducerSelect: (Producer) -> Unit
) {
    val filteredList by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val categories = listOf("All", "Vegetables", "Dairy", "Honey", "Bakery", "Fish", "Eggs", "Drinks", "Artisan", "Flowers")
    
    val currentCat by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isGridView by viewModel.isGridView.collectAsStateWithLifecycle()
    
    // Filtering panel drawer visibility
    var showFilterSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("marketplace_screen")
    ) {
        // Search & Filter Action Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SoftCharcoal),
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search fresh harvests, farms...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = SlateGray) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("search_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EcoGreen,
                            unfocusedBorderColor = SlateGray.copy(alpha = 0.3f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { showFilterSheet = true },
                        modifier = Modifier
                            .size(48.dp)
                            .background(EcoGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .testTag("filter_button")
                    ) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filters", tint = FreshLime)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { viewModel.toggleGridView() },
                        modifier = Modifier
                            .size(48.dp)
                            .background(EcoGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.List else Icons.Default.GridView,
                            contentDescription = "Toggle Grid/List",
                            tint = FreshLime
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Horizontal category selector row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = cat == currentCat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) EcoGreen else SlateGray.copy(alpha = 0.15f))
                                .clickable { viewModel.setSelectedCategory(cat) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .testTag("cat_tab_$cat")
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else SoftWhite
                            )
                        }
                    }
                }
            }
        }

        // Active filters list indicators
        val organicOnly by viewModel.organicFilterOnly.collectAsStateWithLifecycle()
        val selectedRegion by viewModel.selectedRegion.collectAsStateWithLifecycle()
        val maxPrice by viewModel.maxPriceFilter.collectAsStateWithLifecycle()
        val deliveryOnly by viewModel.deliveryOnlyFilter.collectAsStateWithLifecycle()

        if (organicOnly || selectedRegion != "All" || maxPrice < 100.0 || deliveryOnly) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Active filters:", fontSize = 11.sp, color = SlateGray, fontWeight = FontWeight.Bold)
                if (organicOnly) {
                    FilterChipIndicator("Organic") { viewModel.setOrganicFilter(false) }
                }
                if (selectedRegion != "All") {
                    FilterChipIndicator(selectedRegion) { viewModel.setSelectedRegion("All") }
                }
                if (maxPrice < 100.0) {
                    FilterChipIndicator("< $${maxPrice.toInt()}") { viewModel.setMaxPrice(100.0) }
                }
                if (deliveryOnly) {
                    FilterChipIndicator("Delivery Only") { viewModel.setDeliveryOnly(false) }
                }
            }
        }

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Storefront,
                        contentDescription = "No Products",
                        modifier = Modifier.size(64.dp),
                        tint = SlateGray.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No harvests found matching filters.", color = SoftWhite, fontWeight = FontWeight.Bold)
                    Text("Try resetting filters or expanding search query.", color = SlateGray, fontSize = 12.sp)
                }
            }
        } else {
            if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredList) { product ->
                        ProductGridItem(product, onProductSelect)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredList) { product ->
                        ProductListItem(product, onProductSelect)
                    }
                }
            }
        }
    }

    // Advanced Filtering Drawer Sheet
    if (showFilterSheet) {
        Dialog(onDismissRequest = { showFilterSheet = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftCharcoal),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ADVANCED FILTERS", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = FreshLime)
                        IconButton(onClick = { showFilterSheet = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = SoftWhite)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Organic Certification switch
                    val organicVal by viewModel.organicFilterOnly.collectAsStateWithLifecycle()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("USDA Organic Only", color = SoftWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Filter for verified organic certification.", color = SlateGray, fontSize = 11.sp)
                        }
                        Switch(
                            checked = organicVal,
                            onCheckedChange = { viewModel.setOrganicFilter(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = FreshLime)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Same-Day Delivery switch
                    val deliveryVal by viewModel.deliveryOnlyFilter.collectAsStateWithLifecycle()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Same-Day Delivery Only", color = SoftWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Hide farms that only support pickup.", color = SlateGray, fontSize = 11.sp)
                        }
                        Switch(
                            checked = deliveryVal,
                            onCheckedChange = { viewModel.setDeliveryOnly(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = FreshLime)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Regional Hub Selector
                    val currentRegion by viewModel.selectedRegion.collectAsStateWithLifecycle()
                    val regions by viewModel.availableRegions.collectAsStateWithLifecycle()
                    Text("Select Local Region Hub", color = SoftWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val allRegs = listOf("All") + regions
                        allRegs.forEach { reg ->
                            val active = currentRegion == reg
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) EcoGreen else SlateGray.copy(alpha = 0.15f))
                                    .clickable { viewModel.setSelectedRegion(reg) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(reg, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (active) Color.White else SoftWhite)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Price Slider
                    val currentMaxPrice by viewModel.maxPriceFilter.collectAsStateWithLifecycle()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Maximum Unit Price", color = SoftWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("$${currentMaxPrice.toInt()}", color = FreshLime, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = currentMaxPrice.toFloat(),
                        onValueChange = { viewModel.setMaxPrice(it.toDouble()) },
                        valueRange = 5f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = FreshLime,
                            activeTrackColor = EcoGreen
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { showFilterSheet = false },
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreen),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Apply Filters", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChipIndicator(label: String, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(EcoGreen.copy(alpha = 0.2f))
            .border(1.dp, EcoGreen, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = FreshLime, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove Filter",
                modifier = Modifier
                    .size(10.dp)
                    .clickable { onRemove() },
                tint = FreshLime
            )
        }
    }
}

// --- SCREEN 3: FARMS SECTION ---
@Composable
fun FarmsScreen(
    viewModel: MarketplaceViewModel,
    onProducerSelect: (Producer) -> Unit
) {
    val producersList by viewModel.producers.collectAsStateWithLifecycle()
    val favoritesList by viewModel.favorites.collectAsStateWithLifecycle()
    
    var favsOnlyFilter by remember { mutableStateOf(false) }
    var searchFarmQuery by remember { mutableStateOf("") }

    val filteredFarms = producersList.filter { farm ->
        val matchesSearch = farm.name.contains(searchFarmQuery, ignoreCase = true) || farm.description.contains(searchFarmQuery, ignoreCase = true)
        val matchesFav = !favsOnlyFilter || favoritesList.any { f -> f.producerId == farm.id }
        matchesSearch && matchesFav
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("farms_screen")
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SoftCharcoal),
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "LOCAL PRODUCER DIRECTORY",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = FreshLime,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = searchFarmQuery,
                        onValueChange = { searchFarmQuery = it },
                        placeholder = { Text("Search growers, diaries, apiaries...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Farms", tint = SlateGray) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EcoGreen,
                            unfocusedBorderColor = SlateGray.copy(alpha = 0.3f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { favsOnlyFilter = !favsOnlyFilter },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (favsOnlyFilter) HoneyGold.copy(alpha = 0.2f) else EcoGreen.copy(alpha = 0.2f),
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            imageVector = if (favsOnlyFilter) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Show Wishlist Only",
                            tint = if (favsOnlyFilter) HoneyGold else FreshLime
                        )
                    }
                }
            }
        }

        if (filteredFarms.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Text(
                    text = "No farms found matching criteria.",
                    color = SlateGray,
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredFarms) { farm ->
                    val isFav = favoritesList.any { it.producerId == farm.id }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onProducerSelect(farm) },
                        colors = CardDefaults.cardColors(containerColor = SoftCharcoal),
                        border = BorderStroke(1.dp, SlateGray.copy(alpha = 0.1f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(EcoGreen.copy(alpha = 0.15f))
                                ) {
                                    Text(
                                        text = farm.name.take(1),
                                        modifier = Modifier.align(Alignment.Center),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = FreshLime
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(farm.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SoftWhite)
                                    Text(farm.location, fontSize = 12.sp, color = SlateGray)
                                }
                                IconButton(onClick = { viewModel.toggleFavorite(farm.id) }) {
                                    Icon(
                                        imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Toggle Favorite",
                                        tint = if (isFav) HoneyGold else SlateGray
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(farm.description, fontSize = 12.sp, color = SoftWhite.copy(alpha = 0.8f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = SlateGray.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = "Rating", modifier = Modifier.size(14.dp), tint = HoneyGold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(farm.rating.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HoneyGold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("•", color = SlateGray, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("${farm.yearsTrading} Years Trading", fontSize = 12.sp, color = SlateGray)
                                }
                                Box(
                                    modifier = Modifier
                                        .background(EcoGreen.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(farm.region, color = FreshLime, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN 4: SHOPPING CART & COUNTDOWN CHECKOUT ---
@Composable
fun CartCheckoutScreen(
    viewModel: MarketplaceViewModel,
    onProductSelect: (Product) -> Unit
) {
    val cartMap by viewModel.cart.collectAsStateWithLifecycle()
    val productsList by viewModel.products.collectAsStateWithLifecycle()
    val cartList = cartMap.entries.mapNotNull { entry ->
        val product = productsList.find { p -> p.id == entry.key }
        if (product != null) product to entry.value else null
    }

    val totalAmount by viewModel.cartTotal.collectAsStateWithLifecycle()
    
    // Checkout inputs
    var customerName by remember { mutableStateOf("") }
    var customerEmail by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var orderNotes by remember { mutableStateOf("") }
    var discountCode by remember { mutableStateOf("") }
    
    var deliveryType by remember { mutableStateOf("Local Delivery") }
    var scheduledWindow by remember { mutableStateOf("Friday Drop-off (08:00 AM - 12:00 PM)") }
    var paymentMethod by remember { mutableStateOf("Bitcoin (BTC)") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("cart_checkout_screen"),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = "YOUR SHOPPING BASKET",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SlateGray,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (cartList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftCharcoal)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Empty Basket", modifier = Modifier.size(48.dp), tint = SlateGray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Your farm basket is empty.", fontWeight = FontWeight.Bold, color = SoftWhite)
                        Text("Add fresh items from the Marketplace.", color = SlateGray, fontSize = 11.sp)
                    }
                }
            }
        } else {
            items(cartList) { (product, qty) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftCharcoal)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(
                                id = if (product.imageResName == "img_app_icon") R.drawable.img_app_icon else R.drawable.img_hero_banner
                            ),
                            contentDescription = product.name,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onProductSelect(product) },
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SoftWhite, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(product.producerName, fontSize = 11.sp, color = FreshLime)
                            Text("$${String.format("%.2f", product.unitPrice)} each", fontSize = 11.sp, color = SlateGray)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.updateCartQuantity(product.id, qty - 1) }) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrement", tint = SoftWhite)
                            }
                            Text(qty.toString(), color = SoftWhite, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { viewModel.updateCartQuantity(product.id, qty + 1) }) {
                                Icon(Icons.Default.Add, contentDescription = "Increment", tint = SoftWhite)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = SlateGray.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Subtotal", fontWeight = FontWeight.Bold, color = SoftWhite)
                    Text("$${String.format("%.2f", totalAmount)}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = FreshLime)
                }
            }

            // Streaming Checkout Configuration Panel
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "SECURE CRYPTO CHECKOUT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateGray,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = SoftCharcoal)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Customer Information", fontWeight = FontWeight.Bold, color = FreshLime, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Full Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("checkout_name"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EcoGreen)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customerEmail,
                            onValueChange = { customerEmail = it },
                            label = { Text("Email Address") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("checkout_email"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EcoGreen)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text("Mobile Phone") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("checkout_phone"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EcoGreen)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Logistics & Delivery Mode", fontWeight = FontWeight.Bold, color = FreshLime, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Local Delivery", "Producer Pickup").forEach { type ->
                                val active = deliveryType == type
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) EcoGreen else SlateGray.copy(alpha = 0.1f))
                                        .border(1.dp, if (active) FreshLime else Color.Transparent, RoundedCornerShape(8.dp))
                                        .clickable { deliveryType = type }
                                        .padding(vertical = 12.dp)
                                ) {
                                    Text(
                                        text = type,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) Color.White else SoftWhite
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = deliveryAddress,
                            onValueChange = { deliveryAddress = it },
                            label = { Text("Physical Delivery Address") },
                            modifier = Modifier.fillMaxWidth().testTag("checkout_address"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EcoGreen)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = orderNotes,
                            onValueChange = { orderNotes = it },
                            label = { Text("Special Order Notes / Gate Codes") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EcoGreen)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Promotional Coupon Code", fontWeight = FontWeight.Bold, color = FreshLime, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = discountCode,
                            onValueChange = { discountCode = it },
                            label = { Text("Coupon (e.g., HARVEST10)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EcoGreen)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cryptocurrency Asset Choice", fontWeight = FontWeight.Bold, color = FreshLime, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Bitcoin (BTC)", "Tether (USDT)").forEach { coin ->
                                val active = paymentMethod == coin
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) EcoGreen else SlateGray.copy(alpha = 0.1f))
                                        .border(1.dp, if (active) HoneyGold else Color.Transparent, RoundedCornerShape(8.dp))
                                        .clickable { paymentMethod = coin }
                                        .padding(vertical = 12.dp)
                                ) {
                                    Text(
                                        text = coin,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) Color.White else SoftWhite
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Button(
                            onClick = {
                                viewModel.startCheckout(
                                    customerName = customerName,
                                    customerEmail = customerEmail,
                                    customerPhone = customerPhone,
                                    deliveryAddress = deliveryAddress,
                                    deliveryType = deliveryType,
                                    scheduledTime = scheduledWindow,
                                    orderNotes = orderNotes,
                                    discountCode = discountCode,
                                    paymentMethod = paymentMethod
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EcoGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("checkout_submit"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("PROCEED TO SECURE COIN PAYMENT", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN 5: PORTALS (PRODUCER DASHBOARD, ADMISSIONS & ACCOUNT) ---
@Composable
fun PortalsScreen(
    viewModel: MarketplaceViewModel,
    onProductSelect: (Product) -> Unit
) {
    var activePortalTab by remember { mutableStateOf("producer") }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = if (activePortalTab == "producer") 0 else if (activePortalTab == "admin") 1 else 2,
            containerColor = SoftCharcoal,
            contentColor = FreshLime
        ) {
            Tab(
                selected = activePortalTab == "producer",
                onClick = { activePortalTab = "producer" },
                text = { Text("Producer Dashboard", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_producer")
            )
            Tab(
                selected = activePortalTab == "admin",
                onClick = { activePortalTab = "admin" },
                text = { Text("Market Admin", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_admin")
            )
            Tab(
                selected = activePortalTab == "account",
                onClick = { activePortalTab = "account" },
                text = { Text("My Account", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_account")
            )
        }

        when (activePortalTab) {
            "producer" -> ProducerDashboardView(viewModel, onProductSelect)
            "admin" -> MarketAdminView(viewModel)
            "account" -> CustomerAccountView(viewModel)
        }
    }
}

@Composable
fun ProducerDashboardView(viewModel: MarketplaceViewModel, onProductSelect: (Product) -> Unit) {
    val activeProducer by viewModel.currentProducerProfile.collectAsStateWithLifecycle()
    val producerProducts by viewModel.currentProducerProducts.collectAsStateWithLifecycle()
    val ordersList by viewModel.orders.collectAsStateWithLifecycle()

    var showAddProductDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        activeProducer?.let { producer ->
            // Farm profile editor quick preview card
            item {
                Card(colors = CardDefaults.cardColors(containerColor = SoftCharcoal)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(EcoGreen.copy(alpha = 0.2f))
                            ) {
                                Text(
                                    producer.name.take(1),
                                    modifier = Modifier.align(Alignment.Center),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FreshLime
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(producer.name, fontWeight = FontWeight.Bold, color = SoftWhite, fontSize = 16.sp)
                                Text("Manager: Tom G.", color = SlateGray, fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(producer.description, fontSize = 12.sp, color = SlateGray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .background(EcoGreen.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(producer.region, color = FreshLime, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .background(HoneyGold.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("PRODUCER PORTAL ACTIVE", color = HoneyGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Sales Analytics Dashboard summary
            item {
                Column {
                    Text(
                        text = "ANALYTICS & SALES HISTORY",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = SlateGray,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = SoftCharcoal)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Active Listings", color = SlateGray, fontSize = 11.sp)
                                Text(producerProducts.size.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = FreshLime)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = SoftCharcoal)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Total Revenue", color = SlateGray, fontSize = 11.sp)
                                Text("$1,482.00", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = HoneyGold)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = SoftCharcoal)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Crypto Ratio", color = SlateGray, fontSize = 11.sp)
                                Text("100% BTC/USDT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FreshLime, modifier = Modifier.padding(top = 10.dp))
                            }
                        }
                    }
                }
            }

            // Product Inventory Control List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MANAGE OFFERS (${producerProducts.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = SlateGray,
                        letterSpacing = 1.sp
                    )
                    Button(
                        onClick = { showAddProductDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("add_product_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add New Offer", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Harvest", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (producerProducts.isEmpty()) {
                item {
                    Text("No offers listed yet.", color = SlateGray, modifier = Modifier.padding(12.dp))
                }
            } else {
                items(producerProducts) { product ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SoftCharcoal)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SoftWhite)
                                    Text("Category: ${product.category} • Weight: ${product.weight}", color = SlateGray, fontSize = 11.sp)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Stock: ", color = SlateGray, fontSize = 11.sp)
                                        Text("${product.quantityAvailable} left", color = if (product.quantityAvailable == 0) CoralAlert else FreshLime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Price: ", color = SlateGray, fontSize = 11.sp)
                                        Text("$${String.format("%.2f", product.unitPrice)}", color = HoneyGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                
                                Row {
                                    IconButton(onClick = { viewModel.duplicateProduct(product) }) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate Offer", tint = SlateGray)
                                    }
                                    IconButton(onClick = { viewModel.deleteProduct(product) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Offer", tint = CoralAlert)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal dialog to Add a New Product
    if (showAddProductDialog) {
        Dialog(onDismissRequest = { showAddProductDialog = false }) {
            var newName by remember { mutableStateOf("") }
            var newDesc by remember { mutableStateOf("") }
            var newCat by remember { mutableStateOf("Vegetables") }
            var newOrigin by remember { mutableStateOf("Hood River Greenhouse #2") }
            var newWeight by remember { mutableStateOf("1 kg") }
            var newPriceStr by remember { mutableStateOf("9.50") }
            var newStockStr by remember { mutableStateOf("30") }
            
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftCharcoal),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("ADD NEW FRESH HARVEST", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = FreshLime)
                    
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Product / Harvest Name") },
                        modifier = Modifier.fillMaxWidth().testTag("add_prod_name")
                    )
                    OutlinedTextField(
                        value = newDesc,
                        onValueChange = { newDesc = it },
                        label = { Text("Detailed Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Simple Category selection dropdown simulation
                    Text("Harvest Category", color = SlateGray, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val cats = listOf("Vegetables", "Dairy", "Honey", "Bakery", "Fish", "Eggs", "Drinks", "Artisan", "Flowers")
                        cats.forEach { cat ->
                            val selected = newCat == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) EcoGreen else SlateGray.copy(alpha = 0.15f))
                                    .clickable { newCat = cat }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(cat, fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = newOrigin,
                        onValueChange = { newOrigin = it },
                        label = { Text("Origin (Field / Hive #)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newWeight,
                        onValueChange = { newWeight = it },
                        label = { Text("Unit Weight / Volume") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPriceStr,
                        onValueChange = { newPriceStr = it },
                        label = { Text("Price in USD Equivalent") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newStockStr,
                        onValueChange = { newStockStr = it },
                        label = { Text("Available Stock Qty") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAddProductDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = SoftWhite)
                        }
                        Button(
                            onClick = {
                                if (newName.isNotEmpty()) {
                                    val price = newPriceStr.toDoubleOrNull() ?: 5.0
                                    val stock = newStockStr.toIntOrNull() ?: 20
                                    viewModel.addNewProduct(
                                        name = newName,
                                        description = newDesc,
                                        category = newCat,
                                        origin = newOrigin,
                                        harvestDate = "Harvested today morning",
                                        bestBefore = "Consume fresh in 7 days",
                                        organicCert = "USDA Organic",
                                        weight = newWeight,
                                        price = price,
                                        qty = stock,
                                        isFeatured = true,
                                        isSeasonal = false
                                    )
                                    showAddProductDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EcoGreen),
                            modifier = Modifier.weight(1f).testTag("add_prod_submit")
                        ) {
                            Text("Add Offer")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarketAdminView(viewModel: MarketplaceViewModel) {
    val producersList by viewModel.producers.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "ENTERPRISE PRODUCER APPROVALS",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = SlateGray,
                letterSpacing = 1.sp
            )
            Text(
                text = "Moderate local growers before publishing catalog",
                fontSize = 11.sp,
                color = SlateGray
            )
        }

        items(producersList) { producer ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftCharcoal)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(producer.name, fontWeight = FontWeight.Bold, color = SoftWhite)
                        Text(producer.location, fontSize = 11.sp, color = SlateGray)
                        Text("Rating: ${producer.rating} • Region: ${producer.region}", color = SlateGray, fontSize = 11.sp)
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (producer.isApproved) "Approved" else "Suspended",
                            color = if (producer.isApproved) FreshLime else CoralAlert,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Switch(
                            checked = producer.isApproved,
                            onCheckedChange = { viewModel.setProducerApproval(producer.id, it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = FreshLime)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerAccountView(viewModel: MarketplaceViewModel) {
    val ordersList by viewModel.orders.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = SoftCharcoal)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(EcoGreen)
                    ) {
                        Text(
                            "T",
                            modifier = Modifier.align(Alignment.Center),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Tom G. (Enterprise Buyer)", fontWeight = FontWeight.Bold, color = SoftWhite, fontSize = 16.sp)
                        Text("Email: buyer@openharvest.org", color = SlateGray, fontSize = 12.sp)
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .background(HoneyGold.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("BTC & USDT PREFERRED SEEDER", color = HoneyGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "YOUR DECENTRALIZED ORDER HISTORY",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = SlateGray,
                letterSpacing = 1.sp
            )
        }

        if (ordersList.isEmpty()) {
            item {
                Text("No previous transactions found.", color = SlateGray, fontSize = 12.sp, modifier = Modifier.padding(12.dp))
            }
        } else {
            items(ordersList) { order ->
                Card(colors = CardDefaults.cardColors(containerColor = SoftCharcoal)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Ref: ${order.id}", fontWeight = FontWeight.Bold, color = SoftWhite)
                            Text(
                                text = order.paymentStatus.uppercase(),
                                color = if (order.paymentStatus == "Confirmed") FreshLime else if (order.paymentStatus == "Pending") HoneyGold else CoralAlert,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Type: ${order.deliveryType} • Window: ${order.scheduledTime}", fontSize = 11.sp, color = SlateGray)
                        Text("Cryptocurrency: ${order.paymentMethod} (${String.format("%.6f", order.amountCrypto)})", fontSize = 11.sp, color = SlateGray)
                        
                        if (order.txHash.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Blockchain TX: ${order.txHash.take(16)}...",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = FreshLime
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = SlateGray.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Order Total equivalent:", color = SlateGray, fontSize = 11.sp)
                            Text("$${String.format("%.2f", order.totalAmount)}", color = SoftWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- ITEM COMPOSABLES ---

@Composable
fun ProductGridItem(product: Product, onProductSelect: (Product) -> Unit) {
    Card(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .clickable { onProductSelect(product) }
            .testTag("product_grid_item_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = SoftCharcoal)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(110.dp)) {
                Image(
                    painter = painterResource(
                        id = if (product.imageResName == "img_app_icon") R.drawable.img_app_icon else R.drawable.img_hero_banner
                    ),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (product.organicCertification != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .background(HoneyGold, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("BIO", color = DeepForestDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.producerName,
                    fontSize = 11.sp,
                    color = FreshLime,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${String.format("%.2f", product.unitPrice)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SoftWhite
                    )
                    Box(
                        modifier = Modifier
                            .background(EcoGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(product.weight, color = FreshLime, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductListItem(product: Product, onProductSelect: (Product) -> Unit) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 6.dp)
            .fillMaxWidth()
            .clickable { onProductSelect(product) }
            .testTag("product_list_item_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = SoftCharcoal)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    id = if (product.imageResName == "img_app_icon") R.drawable.img_app_icon else R.drawable.img_hero_banner
                ),
                contentDescription = product.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SoftWhite, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(product.producerName, fontSize = 11.sp, color = FreshLime)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(EcoGreen.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(product.weight, color = FreshLime, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    if (product.organicCertification != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(HoneyGold.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("ORGANIC", color = HoneyGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Text(
                text = "$${String.format("%.2f", product.unitPrice)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SoftWhite,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

// --- DETAILED OVERLAY DIALOGS ---

@Composable
fun ProductDetailDialog(
    product: Product,
    onDismiss: () -> Unit,
    onAddToCart: (Int) -> Unit,
    onViewProducer: () -> Unit
) {
    var qtyToAdd by remember { mutableStateOf(1) }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SoftCharcoal),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Image(
                        painter = painterResource(
                            id = if (product.imageResName == "img_app_icon") R.drawable.img_app_icon else R.drawable.img_hero_banner
                        ),
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(product.name, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = SoftWhite)
                        Text(
                            text = "by ${product.producerName}",
                            fontWeight = FontWeight.Bold,
                            color = FreshLime,
                            modifier = Modifier.clickable { onViewProducer() },
                            fontSize = 13.sp
                        )
                    }
                    Text(
                        text = "$${String.format("%.2f", product.unitPrice)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = HoneyGold
                    )
                }

                Divider(color = SlateGray.copy(alpha = 0.2f))

                // Logistics / Info table
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    DetailRow("Harvest Origin", product.origin)
                    DetailRow("Harvest Timing", product.harvestDate)
                    DetailRow("Shelf Life Limit", product.bestBefore)
                    DetailRow("Weight Spec", product.weight)
                    DetailRow("Nutrition Value", product.nutritionInfo)
                    DetailRow("Active Allergens", product.allergens)
                    DetailRow("Organic Cert", product.organicCertification ?: "None")
                }

                Divider(color = SlateGray.copy(alpha = 0.2f))

                Text(product.description, fontSize = 12.sp, color = SoftWhite.copy(alpha = 0.8f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Quantity", fontWeight = FontWeight.Bold, color = SoftWhite, fontSize = 14.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (qtyToAdd > 1) qtyToAdd-- }) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = SoftWhite)
                        }
                        Text(qtyToAdd.toString(), color = SoftWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        IconButton(onClick = { if (qtyToAdd < product.quantityAvailable) qtyToAdd++ }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", tint = SoftWhite)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Close", color = SoftWhite)
                    }
                    Button(
                        onClick = {
                            onAddToCart(qtyToAdd)
                            Toast.makeText(context, "$qtyToAdd x ${product.name} added to cart", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreen),
                        modifier = Modifier.weight(1.5f).testTag("add_to_cart_detail_submit")
                    ) {
                        Text("Add to Basket")
                    }
                }
            }
        }
    }
}

@Composable
fun ProducerDetailDialog(
    producer: Producer,
    viewModel: MarketplaceViewModel,
    onDismiss: () -> Unit,
    onProductSelect: (Product) -> Unit
) {
    val producerProducts by viewModel.products.collectAsStateWithLifecycle()
    val matchingProducts = producerProducts.filter { it.producerId == producer.id }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SoftCharcoal),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(EcoGreen)
                    ) {
                        Text(
                            producer.name.take(1),
                            modifier = Modifier.align(Alignment.Center),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(producer.name, fontWeight = FontWeight.Bold, color = SoftWhite, fontSize = 16.sp)
                        Text(producer.location, fontSize = 12.sp, color = SlateGray)
                    }
                }

                Divider(color = SlateGray.copy(alpha = 0.2f))

                Text("FARM BIOGRAPHY & PHILOSOPHY", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = FreshLime)
                Text(producer.description, fontSize = 12.sp, color = SoftWhite.copy(alpha = 0.8f))

                DetailRow("Opening Hours", producer.openingHours)
                DetailRow("Regions Serviced", producer.deliveryRegions)
                DetailRow("Certifications", producer.certifications)
                DetailRow("Sustainability Practices", producer.sustainabilityPractices)

                Divider(color = SlateGray.copy(alpha = 0.2f))

                Text("PRODUCTS FROM THIS FARM (${matchingProducts.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = FreshLime)
                matchingProducts.forEach { product ->
                    Card(
                        onClick = { onProductSelect(product) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DeepForestDark)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(product.name, fontWeight = FontWeight.Bold, color = SoftWhite, fontSize = 12.sp)
                                Text("${product.weight} • ${product.origin}", color = SlateGray, fontSize = 10.sp)
                            }
                            Text("$${String.format("%.2f", product.unitPrice)}", fontWeight = FontWeight.Bold, color = HoneyGold, fontSize = 12.sp)
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close Farm Profile")
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = SlateGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(16.dp))
        Text(value, color = SoftWhite, fontSize = 11.sp, textAlign = TextAlign.End, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

// --- SCREEN 6: BLOCKCHAIN PAYMENT TRACKER DIALOG OVERLAY ---
@Composable
fun CryptoPaymentOverlay(
    order: Order,
    onDismiss: () -> Unit,
    onPaidClick: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val isPending = order.paymentStatus == "Pending"
    val isConfirmed = order.paymentStatus == "Confirmed"

    val min = order.countdownSeconds / 60
    val sec = order.countdownSeconds % 60
    val countdownText = String.format("%02d:%02d", min, sec)

    Dialog(onDismissRequest = { if (!isPending) onDismiss() }) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepForestDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .border(2.dp, if (isConfirmed) FreshLime else HoneyGold, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("DECENTRALIZED INVOICE", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SlateGray)
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("dismiss_payment_overlay")) {
                        Icon(Icons.Default.Close, contentDescription = "Close Overlay", tint = SoftWhite)
                    }
                }

                if (isPending) {
                    Text("AWAITING COIN SETTLEMENT", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = HoneyGold)
                    
                    // Countdown timer
                    Box(
                        modifier = Modifier
                            .background(HoneyGold.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = "Timer Icon", tint = HoneyGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(countdownText, color = HoneyGold, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }
                    }

                    // QR code custom drawing canvas simulation
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                            // Draw a beautiful custom simulated QR matrix pattern
                            val w = size.width / 10
                            for (x in 0..9) {
                                for (y in 0..9) {
                                    if ((x + y) % 2 == 0 || (x == 0 && y < 3) || (y == 0 && x < 3) || (x == 9 && y > 6)) {
                                        drawRect(
                                            color = Color.Black,
                                            topLeft = androidx.compose.ui.geometry.Offset(x * w, y * w),
                                            size = androidx.compose.ui.geometry.Size(w - 2f, w - 2f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Amount to send equivalent:", color = SlateGray, fontSize = 11.sp)
                        Text(
                            text = "${String.format("%.6f", order.amountCrypto)} ${order.paymentMethod.substringAfter("(").substringBefore(")")}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = SoftWhite
                        )
                        Text("Total: $${String.format("%.2f", order.totalAmount)} USD equivalent", color = SlateGray, fontSize = 11.sp)
                    }

                    // Wallet copy field
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SoftCharcoal)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("WALLET RECEIVER ADDRESS", color = FreshLime, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    order.walletAddress,
                                    color = SoftWhite,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(order.walletAddress))
                                    Toast.makeText(context, "Address copied to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ContentPaste, contentDescription = "Copy Wallet", tint = FreshLime, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Text(
                        text = "Our self-hosted blockchain node is actively listening. Once detected, the order will reconcile automatically in seconds.",
                        color = SlateGray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    
                    Button(
                        onClick = onPaidClick,
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreen),
                        modifier = Modifier.fillMaxWidth().testTag("simulate_payment_button")
                    ) {
                        Text("SIMULATE INSTANT BLOCKCHAIN DEPOSIT", fontWeight = FontWeight.Bold)
                    }
                } else if (isConfirmed) {
                    // Success state
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        modifier = Modifier.size(64.dp),
                        tint = FreshLime
                    )
                    Text("PAYMENT RECEIVED SUCCESSFULLY", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = FreshLime)
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Reconciled under Order Ref: ${order.id}", color = SoftWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Tx Hash: ${order.txHash.take(16)}...", color = SlateGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("A digital invoice and shipping update has been queued to ${order.customerEmail}.", color = SlateGray, fontSize = 11.sp, textAlign = TextAlign.Center)
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Return to Marketplace", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
