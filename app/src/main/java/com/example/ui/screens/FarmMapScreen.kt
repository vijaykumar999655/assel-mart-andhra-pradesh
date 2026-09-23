package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SellerProfileEntity
import com.example.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmMapScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    onSelectSeller: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sellers by viewModel.allSellers.collectAsStateWithLifecycle()
    val currentLocation by viewModel.currentUserLocation.collectAsStateWithLifecycle()
    var selectedSeller by remember { mutableStateOf<SellerProfileEntity?>(sellers.firstOrNull()) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Andhra Farm Area Locator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Real-time Breeder GPS & Geofencing",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Interactive Andhra Pradesh Map Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE8ECE9))
            ) {
                val width = size.width
                val height = size.height

                // Draw map grid
                val gridStep = 60f
                for (x in 0..(width / gridStep).toInt()) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.5f),
                        start = Offset(x * gridStep, 0f),
                        end = Offset(x * gridStep, height),
                        strokeWidth = 1f
                    )
                }
                for (y in 0..(height / gridStep).toInt()) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.5f),
                        start = Offset(0f, y * gridStep),
                        end = Offset(width, y * gridStep),
                        strokeWidth = 1f
                    )
                }

                // Stylized Bay of Bengal coastline path (Andhra Pradesh coast)
                val coastPath = Path().apply {
                    moveTo(width * 0.95f, 0f)
                    cubicTo(
                        width * 0.82f, height * 0.25f,
                        width * 0.70f, height * 0.45f,
                        width * 0.65f, height * 0.65f
                    )
                    cubicTo(
                        width * 0.60f, height * 0.78f,
                        width * 0.55f, height * 0.90f,
                        width * 0.50f, height
                    )
                    lineTo(width, height)
                    lineTo(width, 0f)
                    close()
                }

                drawPath(
                    path = coastPath,
                    color = Color(0xFFCCE4F7).copy(alpha = 0.85f)
                )

                // Godavari River tributary simulation line
                val godavariPath = Path().apply {
                    moveTo(0f, height * 0.28f)
                    quadraticBezierTo(width * 0.45f, height * 0.32f, width * 0.82f, height * 0.30f)
                }
                drawPath(
                    path = godavariPath,
                    color = Color(0xFF90C2E7),
                    style = Stroke(width = 5f)
                )

                // Krishna River simulation line
                val krishnaPath = Path().apply {
                    moveTo(0f, height * 0.50f)
                    quadraticBezierTo(width * 0.40f, height * 0.54f, width * 0.72f, height * 0.58f)
                }
                drawPath(
                    path = krishnaPath,
                    color = Color(0xFF90C2E7),
                    style = Stroke(width = 4.5f)
                )

                // User GPS pulse circle
                drawCircle(
                    color = Color(0xFF0284C7).copy(alpha = 0.18f),
                    radius = 80f,
                    center = Offset(width * 0.45f, height * 0.42f)
                )
                drawCircle(
                    color = Color(0xFF0284C7),
                    radius = 9f,
                    center = Offset(width * 0.45f, height * 0.42f)
                )
            }

            // Interactive Farm Pins Overlay
            val pins = listOf(
                Triple("seller_varma_bvr", 0.52f, 0.38f),   // Bhimavaram
                Triple("seller_reddy_rjy", 0.65f, 0.28f),   // Rajahmundry
                Triple("seller_rao_vja", 0.42f, 0.48f),     // Vijayawada
                Triple("seller_srinivas_gtr", 0.38f, 0.58f) // Tenali / Guntur
            )

            Box(modifier = Modifier.fillMaxSize()) {
                pins.forEach { (sellerId, xRatio, yRatio) ->
                    val seller = sellers.find { it.sellerId == sellerId } ?: return@forEach
                    val isSelected = selectedSeller?.sellerId == sellerId

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(
                                start = (LocalContext.current.resources.displayMetrics.widthPixels * xRatio / LocalContext.current.resources.displayMetrics.density).dp - 24.dp,
                                top = 140.dp + (350 * yRatio).dp
                            )
                            .clickable { selectedSeller = seller }
                            .testTag("farm_pin_${seller.sellerId}")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF15803D),
                            shadowElevation = 6.dp,
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                            modifier = Modifier.size(if (isSelected) 44.dp else 36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = seller.farmName,
                                    tint = Color.White,
                                    modifier = Modifier.size(if (isSelected) 26.dp else 20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Top Status Overlay (Current GPS)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = null,
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CURRENT GPS FIX (REAL-TIME)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7)
                        )
                        Text(
                            text = "${currentLocation.cityName} • Accuracy ±3m",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFDCFCE7)
                    ) {
                        Text(
                            text = "ONLINE",
                            color = Color(0xFF15803D),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Bottom Selected Farm Area Card
            val active = selectedSeller ?: sellers.firstOrNull()
            if (active != null) {
                val distance = viewModel.repository.calculateDistanceKm(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    active.latitude,
                    active.longitude
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                        .testTag("farm_detail_bottom_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = active.farmName.take(2).uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = active.farmName,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = "${active.mandal}, ${active.district}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFEF3C7)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.NearMe, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$distance km",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF92400E)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = active.verificationBadge,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0369A1)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Address: ${active.villageAddress}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Visiting Hours: ${active.openVisitHours}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF15803D),
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val geoUri = Uri.parse("geo:${active.latitude},${active.longitude}?q=${Uri.encode(active.farmName)}")
                                    val intent = Intent(Intent.ACTION_VIEW, geoUri)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Directions", fontSize = 12.sp)
                            }

                            Button(
                                onClick = { onSelectSeller(active.sellerId) },
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Breeder Profile", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
