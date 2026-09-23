package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.EscrowOrderEntity
import com.example.ui.components.formatRupees
import com.example.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EscrowPaymentScreen(
    roosterId: String,
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val roosters by viewModel.filteredRoosters.collectAsStateWithLifecycle()
    val rooster = roosters.find { it.first.id == roosterId }?.first

    val orders by viewModel.allEscrowOrders.collectAsStateWithLifecycle()
    val activeOrder = orders.find { it.roosterId == roosterId }

    var selectedPaymentMode by remember { mutableStateOf("UPI (Google Pay / PhonePe)") }
    var buyerNameInput by remember { mutableStateOf("K. Vijay Kumar") }
    var enteredOtp by remember { mutableStateOf("") }

    if (rooster == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Rooster not found.")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Secure Escrow Checkout", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("100% Protected Handover Guarantee", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Escrow Guarantee Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFDCFCE7),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF15803D),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "RBI Regulated Escrow Trustee Account",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D),
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Funds are NEVER paid directly to breeder upfront. Money remains safely locked until you physically inspect the rooster at the farm and confirm the 6-digit OTP.",
                            fontSize = 11.sp,
                            color = Color(0xFF166534),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Order Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Order Summary & Specimen Details", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.rooster_dega_nemali),
                            contentDescription = rooster.breedName,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(rooster.breedName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Ring Tag: #${rooster.ringTagId}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            Text("Breeder: ${rooster.sellerName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Farm: ${rooster.farmMandal}, ${rooster.farmDistrict}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Specimen Price", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(formatRupees(rooster.priceRupees), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Escrow Protection Service Fee", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("₹0 (FREE Promo)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Veterinary Inspection Stamp", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Included", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Escrow Deposit", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                        Text(formatRupees(rooster.priceRupees), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Escrow Milestone Tracker (If Order Exists)
            if (activeOrder != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Active Escrow Order: #${activeOrder.orderId}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Handover OTP Display
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("YOUR CONFIDENTIAL HANDOVER OTP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = activeOrder.verificationOtp,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 4.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Share this ONLY when you are physically at the farm, inspected the rooster, and confirmed the leg ring ID matches #${rooster.ringTagId}.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 4 Milestone Steps
                        EscrowMilestoneStep(
                            stepNumber = "1",
                            title = "Escrow Deposit Locked",
                            subtitle = "Amount held safely in Trustee Escrow Account",
                            isCompleted = true,
                            isActive = activeOrder.status == "ESCROW_LOCKED"
                        )

                        EscrowMilestoneStep(
                            stepNumber = "2",
                            title = "Farm Visit & Physical Inspection",
                            subtitle = "Visit ${rooster.farmMandal} farm to verify vitality, weight & plumage",
                            isCompleted = activeOrder.status in listOf("FARM_INSPECTION_PENDING", "VET_HEALTH_VERIFIED", "PAYOUT_RELEASED"),
                            isActive = activeOrder.status == "FARM_INSPECTION_PENDING"
                        )

                        EscrowMilestoneStep(
                            stepNumber = "3",
                            title = "Veterinary Health Card Verification",
                            subtitle = "Check Ranikhet RDV & Marek vaccination certificate",
                            isCompleted = activeOrder.status in listOf("VET_HEALTH_VERIFIED", "PAYOUT_RELEASED"),
                            isActive = activeOrder.status == "VET_HEALTH_VERIFIED"
                        )

                        EscrowMilestoneStep(
                            stepNumber = "4",
                            title = "Handover Complete & Payout Released",
                            subtitle = "Breeder receives ₹${rooster.priceRupees} in bank account",
                            isCompleted = activeOrder.status == "PAYOUT_RELEASED",
                            isActive = activeOrder.status == "PAYOUT_RELEASED"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action button to advance the milestone (Interactive Demo)
                        if (activeOrder.status != "PAYOUT_RELEASED") {
                            Button(
                                onClick = { viewModel.progressEscrowStage(activeOrder.orderId, activeOrder.status) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(
                                    text = when (activeOrder.status) {
                                        "ESCROW_LOCKED" -> "Advance to: Farm Inspection Done"
                                        "FARM_INSPECTION_PENDING" -> "Advance to: Vet Health Passed"
                                        "VET_HEALTH_VERIFIED" -> "Release Payout to Breeder with OTP"
                                        else -> "Completed"
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFDCFCE7),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF15803D))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Transaction Fully Completed & Payout Released!", fontWeight = FontWeight.Bold, color = Color(0xFF15803D), fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            } else {
                // Payment Mode Selection & Buyer Form
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Select Secure Payment Gateway Method", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = buyerNameInput,
                            onValueChange = { buyerNameInput = it },
                            label = { Text("Buyer Name (Aadhaar / Bank Name)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val methods = listOf(
                            "UPI (Google Pay / PhonePe)",
                            "BHIM UPI / Any UPI ID",
                            "Net Banking Escrow (SBI, HDFC, ICICI, Andhra Bank)",
                            "Cash on Physical Farm Handover (Escrow Pre-Auth)"
                        )

                        methods.forEach { method ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedPaymentMode = method }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (selectedPaymentMode == method) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (selectedPaymentMode == method) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = method,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedPaymentMode == method) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.initiateEscrow(
                                    rooster = rooster,
                                    paymentMethod = selectedPaymentMode,
                                    buyerName = buyerNameInput.ifBlank { "Buyer" },
                                    onOrderCreated = { /* state updates reactively */ }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("confirm_escrow_deposit"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Lock ${formatRupees(rooster.priceRupees)} in Escrow", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun EscrowMilestoneStep(
    stepNumber: String,
    title: String,
    subtitle: String,
    isCompleted: Boolean,
    isActive: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = when {
                isCompleted -> Color(0xFF15803D)
                isActive -> Color(0xFFD97706)
                else -> Color.LightGray
            },
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isCompleted) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text(stepNumber, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = if (isActive || isCompleted) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp,
                color = if (isCompleted) Color(0xFF15803D) else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
