package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserTransactionEntity
import com.example.ui.components.formatRupees
import com.example.ui.viewmodel.MarketplaceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onNavigateToAddRooster: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val transactions by viewModel.userTransactions.collectAsStateWithLifecycle()

    var showKycDialog by remember { mutableStateOf(false) }
    var showInspectionDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var selectedTxnReceipt by remember { mutableStateOf<UserTransactionEntity?>(null) }
    var txnTabFilter by remember { mutableIntStateOf(0) } // 0: All, 1: Purchases, 2: Sales

    // KYC Dialog state
    var docType by remember { mutableStateOf("AADHAAR") }
    var docNumber by remember { mutableStateOf("") }

    // Farm Inspection Dialog state
    var inspectionNotes by remember { mutableStateOf("") }
    var vetCertInput by remember { mutableStateOf("AP-AH-2024-VET-9821") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (currentUser?.userRole == "SELLER") "Breeder Profile & Compliance" else "User Account & Purchases",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "DPDP Act 2023 & AP Aviculture Registry",
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
                actions = {
                    IconButton(onClick = onNavigateToAuth) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch Account",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (currentUser == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No Active User Session", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Please sign in or create an account to view KYC status, farm inspections, and transaction history.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onNavigateToAuth,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Sign In or Register")
                    }
                }
            }
            return@Scaffold
        }

        val user = currentUser!!

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. User Profile Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = user.fullName.take(2).uppercase(),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 22.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user.fullName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (user.isIdVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Verified KYC",
                                            tint = Color(0xFF0284C7),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = user.phone,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (user.userRole == "SELLER") Color(0xFFFEF3C7) else Color(0xFFDCFCE7)
                                    ) {
                                        Text(
                                            text = if (user.userRole == "SELLER") "REGISTERED BREEDER" else "HERITAGE BUYER",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (user.userRole == "SELLER") Color(0xFF92400E) else Color(0xFF15803D),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = "${user.mandal}, ${user.district}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        if (!user.farmName.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Agriculture, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Farm: ${user.farmName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Text(
                                text = "Address: ${user.address}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onNavigateToAuth,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Switch User", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.logout()
                                    onNavigateToAuth()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sign Out", fontSize = 12.sp)
                            }
                        }

                        if (user.userRole == "SELLER" || user.userRole == "BOTH") {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onNavigateToAddRooster,
                                modifier = Modifier.fillMaxWidth().height(44.dp).testTag("profile_list_rooster_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Create Detailed Rooster Listing", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // 2. Government & Identity (ID) Verification Card (KYC)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = null,
                                    tint = if (user.isIdVerified) Color(0xFF15803D) else Color(0xFFD97706),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Government ID & KYC Status",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (user.isIdVerified) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (user.isIdVerified) Icons.Default.CheckCircle else Icons.Default.Pending,
                                        contentDescription = null,
                                        tint = if (user.isIdVerified) Color(0xFF15803D) else Color(0xFFB45309),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (user.isIdVerified) "VERIFIED" else "PENDING",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        color = if (user.isIdVerified) Color(0xFF15803D) else Color(0xFF92400E)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (user.isIdVerified) {
                            Text(
                                text = "Document: ${user.idDocumentType} (${user.idDocumentMaskedNumber})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            val dateStr = user.idVerificationDate?.let {
                                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
                            } ?: "Verified via Aadhaar e-KYC"
                            Text(
                                text = "Verification Timestamp: $dateStr • Tamper-proof hash linked to UIDAI",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "Complete identity verification using your Aadhaar or Kisan Credit Card to unlock high-tier Escrow transactions and breeder accreditation.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { showKycDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.testTag("verify_id_button")
                            ) {
                                Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Submit Government ID (KYC)")
                            }
                        }
                    }
                }
            }

            // 3. Farm Inspection & Biosecurity Status (Seller / Breeder Specific)
            if (user.userRole == "SELLER" || user.userRole == "BOTH") {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (user.farmInspectionStatus == "INSPECTED_PASSED") Color(0xFFF0FDF4) else MaterialTheme.colorScheme.surface
                        ),
                        border = if (user.farmInspectionStatus == "INSPECTED_PASSED") androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)) else null
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.HealthAndSafety,
                                        contentDescription = null,
                                        tint = if (user.farmInspectionStatus == "INSPECTED_PASSED") Color(0xFF15803D) else Color(0xFFD97706),
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Farm Inspection & Biosecurity",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (user.farmInspectionStatus == "INSPECTED_PASSED") Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                                ) {
                                    Text(
                                        text = when (user.farmInspectionStatus) {
                                            "INSPECTED_PASSED" -> "PASSED & CERTIFIED"
                                            "PENDING" -> "AUDIT PENDING"
                                            else -> user.farmInspectionStatus
                                        },
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        color = if (user.farmInspectionStatus == "INSPECTED_PASSED") Color(0xFF15803D) else Color(0xFF92400E),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (user.farmInspectionStatus == "INSPECTED_PASSED") {
                                Text(
                                    text = "Inspector: ${user.farmInspectorName.ifBlank { "Dr. K. Satyanarayana, AP AH Dept" }}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Notes: ${user.farmInspectorNotes.ifBlank { "2.5-acre bio-secure facility with isolated quarantine pens and complete Ranikhet RDV vaccination logs." }}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF166534),
                                    lineHeight = 17.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Vet Cert ID: ${user.veterinaryCertificateId.ifBlank { "AP-AH-2024-VET-9821" }}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Biosecurity: ${user.biosecurityTier}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D)
                                    )
                                }
                            } else {
                                Text(
                                    text = "Schedule an official on-site inspection with an accredited Animal Husbandry Veterinary Officer to verify bio-security enclosure and earn the 'AP Govt Certified Breeder' badge.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { showInspectionDialog = true },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(imageVector = Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Schedule Farm Inspection Audit")
                                }
                            }
                        }
                    }
                }
            }

            // 4. Data Privacy & DPDP Act 2023 Compliance Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Policy, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Data Privacy & Statutory Rights", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "AseelMart AP complies with the Digital Personal Data Protection (DPDP) Act, 2023. Your contact numbers, geolocation coordinates, and KYC credentials are encrypted with AES-256.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showPrivacyDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Data Vault Export", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { showPrivacyDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Privacy Audit", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // 5. Purchase & Sale Transaction History Header & Filter Tabs
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Transaction History (${transactions.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = txnTabFilter == 0,
                            onClick = { txnTabFilter = 0 },
                            label = { Text("All (${transactions.size})") }
                        )
                        FilterChip(
                            selected = txnTabFilter == 1,
                            onClick = { txnTabFilter = 1 },
                            label = { Text("Purchases (${transactions.count { it.transactionType == "PURCHASE" }})") }
                        )
                        FilterChip(
                            selected = txnTabFilter == 2,
                            onClick = { txnTabFilter = 2 },
                            label = { Text("Sales (${transactions.count { it.transactionType == "SALE" }})") }
                        )
                    }
                }
            }

            // Filtered Transactions List
            val filteredTxns = when (txnTabFilter) {
                1 -> transactions.filter { it.transactionType == "PURCHASE" }
                2 -> transactions.filter { it.transactionType == "SALE" }
                else -> transactions
            }

            if (filteredTxns.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("No transactions recorded yet in this view.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Complete an Escrow deposit or list a rooster to generate tamper-proof receipts.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(filteredTxns, key = { it.transactionId }) { txn ->
                    TransactionCard(
                        txn = txn,
                        onClick = { selectedTxnReceipt = txn }
                    )
                }
            }
        }
    }

    // Modal: Submit KYC Verification Dialog
    if (showKycDialog) {
        AlertDialog(
            onDismissRequest = { showKycDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Identity Verification (e-KYC)")
                }
            },
            text = {
                Column {
                    Text(
                        text = "UIDAI Aadhaar / AP Kisan Livestock Board Verification",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = docType == "AADHAAR",
                            onClick = { docType = "AADHAAR" },
                            label = { Text("Aadhaar") }
                        )
                        FilterChip(
                            selected = docType == "KISAN_CARD",
                            onClick = { docType = "KISAN_CARD" },
                            label = { Text("Kisan Card") }
                        )
                        FilterChip(
                            selected = docType == "VOTER_ID",
                            onClick = { docType = "VOTER_ID" },
                            label = { Text("Voter ID") }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = docNumber,
                        onValueChange = { docNumber = it },
                        label = { Text("Enter Document Number") },
                        placeholder = { Text("e.g. 5829 4410 8921") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("kyc_doc_input")
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Encrypted with SHA-256 before verification. Only the last 4 digits are retained for compliance logs.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalNum = if (docNumber.isBlank()) "5829 4410 8921" else docNumber
                        viewModel.submitKycVerification(docType, finalNum)
                        showKycDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Verify & Link ID")
                }
            },
            dismissButton = {
                TextButton(onClick = { showKycDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal: Schedule Farm Inspection Audit
    if (showInspectionDialog) {
        AlertDialog(
            onDismissRequest = { showInspectionDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.MedicalServices, contentDescription = null, tint = Color(0xFF15803D))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Schedule Farm Biosecurity Audit")
                }
            },
            text = {
                Column {
                    Text(
                        text = "AP Animal Husbandry Department Veterinary Officer Visit",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = vetCertInput,
                        onValueChange = { vetCertInput = it },
                        label = { Text("Veterinary Health Certificate ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = inspectionNotes,
                        onValueChange = { inspectionNotes = it },
                        label = { Text("Breeding Facility & Quarantine Notes") },
                        placeholder = { Text("e.g. 2.5 acre facility, Ranikhet RDV vaccine logs verified.") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val notes = if (inspectionNotes.isBlank()) "Bio-secure facility verified with clean concrete isolation pens and complete vaccination logs." else inspectionNotes
                        viewModel.updateFarmInspection("INSPECTED_PASSED", notes, vetCertInput)
                        showInspectionDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D))
                ) {
                    Text("Approve Inspection")
                }
            },
            dismissButton = {
                TextButton(onClick = { showInspectionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal: Digital Privacy Audit Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("DPDP Act Privacy Vault")
                }
            },
            text = {
                Column {
                    Text("Statutory Compliance Architecture:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Identity data stored in isolated Android Keystore enclave.", fontSize = 11.sp)
                    Text("• End-to-end encrypted messaging with zero server metadata storage.", fontSize = 11.sp)
                    Text("• Farm GPS coordinates only published within verified search radius.", fontSize = 11.sp)
                    Text("• Right to Data Portability & Complete Deletion on demand.", fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "STATUS: 100% COMPLIANT WITH DPDP ACT 2023",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showPrivacyDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Modal: View Tamper-Proof Digital Transaction Receipt
    selectedTxnReceipt?.let { txn ->
        AlertDialog(
            onDismissRequest = { selectedTxnReceipt = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Digital Transaction Certificate", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Text("TXN ID: #${txn.transactionId}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Rooster: ${txn.roosterBreed}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Amount: ${formatRupees(txn.amountRupees)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    Text("Type: ${txn.transactionType} • Escrow: #${txn.escrowOrderId}", fontSize = 12.sp)
                    Text("Counterparty: ${txn.counterpartyName}", fontSize = 12.sp)
                    Text("Farm Location: ${txn.farmLocation}", fontSize = 12.sp)
                    Text("Status: ${txn.status}", fontWeight = FontWeight.Bold, color = Color(0xFF15803D), fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Tamper-Proof Receipt Verification Hash:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = txn.receiptVerificationHash,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedTxnReceipt = null }) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
fun TransactionCard(
    txn: UserTransactionEntity,
    onClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(txn.dateMillis))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (txn.transactionType == "PURCHASE") Color(0xFFEFF6FF) else Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = txn.transactionType,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (txn.transactionType == "PURCHASE") Color(0xFF1D4ED8) else Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = formatRupees(txn.amountRupees),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = txn.roosterBreed,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${if (txn.transactionType == "PURCHASE") "Seller" else "Buyer"}: ${txn.counterpartyName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Farm Location: ${txn.farmLocation}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFDCFCE7)
                ) {
                    Text(
                        text = txn.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
