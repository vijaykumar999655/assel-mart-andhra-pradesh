package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechnicalArchitectureScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Features", "System Arch", "Auth & KYC", "E2EE Protocol", "Escrow Plan", "Compliance")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Architecture & Technical Blueprint", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Platform Engineering Specifications", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        ) {
            PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_$index")
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> FeatureListSection()
                    1 -> SystemArchitectureSection()
                    2 -> AuthKYCSection()
                    3 -> E2EESecuritySection()
                    4 -> EscrowPaymentSection()
                    5 -> ComplianceLegalSection()
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun FeatureListSection() {
    Column {
        ArchitectureHeaderCard(
            title = "AseelMart AP Comprehensive Feature Matrix",
            subtitle = "Full-stack enterprise capabilities designed for buyers, breeders, and agricultural inspectors.",
            icon = Icons.Default.Architecture
        )

        Spacer(modifier = Modifier.height(14.dp))

        FeatureBlock(
            category = "1. Real-Time Geolocation & Farm Area Mapping",
            items = listOf(
                "Dynamic Haversine Distance Engine: Continuously calculates driving distance (in km) from buyer GPS coordinates to breeders across Andhra Pradesh (Godavari, Krishna, Guntur, Rayalaseema).",
                "Canvas Regional Farm Locator: Visual pins for verified studs with live status, open visit hours, and turn-by-turn map directions.",
                "Geofenced Physical Verification: Triggers automated handover check-in when buyer arrives within 50 meters of the registered farm latitude/longitude."
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        FeatureBlock(
            category = "2. Verified Breeder / Seller Profiles & Registration",
            items = listOf(
                "KYC Verification Pipeline: Aadhaar e-KYC + AP Animal Husbandry Department Breeder Registration Number.",
                "Farm Profile: Breeder lineage history, bio-secure facility photo records, customer reviews, and veterinary affiliation.",
                "Breeder Inventory Management: Sellers self-register and list roosters with weight (kg), age, height, plumage color, spurs condition, ring tag IDs, and price."
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        FeatureBlock(
            category = "3. End-to-End Encrypted (E2EE) Buyer-Seller Messaging",
            items = listOf(
                "Zero-Knowledge Protocol: Client-side cryptographic message encryption using Signal Double Ratchet and AES-256-GCM.",
                "Public/Private Key Pair Generation on Android KeyStore hardware enclave.",
                "Fingerprint Verification: 60-digit safety key verification to protect negotiations and farm visits from third-party interception.",
                "Direct Negotiation Presets: One-tap queries for video calls, lineage proof, and health cards."
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        FeatureBlock(
            category = "4. Secure Payment Gateway & 100% Escrow Hold",
            items = listOf(
                "RBI Escrow Trustee Integration: Funds locked safely in nodal account; no direct prepayment to breeder.",
                "Multi-Rail Gateway: UPI (GPay, PhonePe, Paytm, BHIM), Net Banking, and Card payments.",
                "Milestone-Gated Release: Funds released only after (1) Farm physical inspection, (2) Leg-ring verification, and (3) Buyer provides the 6-digit handover OTP."
            )
        )
    }
}

@Composable
fun SystemArchitectureSection() {
    Column {
        ArchitectureHeaderCard(
            title = "Platform Technical Architecture Plan",
            subtitle = "Distributed, event-driven reactive mobile & cloud infrastructure.",
            icon = Icons.Default.Storage
        )

        Spacer(modifier = Modifier.height(14.dp))

        ArchLayerCard(
            layerName = "Layer 1: Mobile Client (Android Jetpack)",
            techStack = "Kotlin • Jetpack Compose • MVVM • Android Architecture Components",
            points = listOf(
                "Unidirectional Data Flow (UDF) with Kotlin StateFlow & Coroutines.",
                "Room Database local cache with KSP compiler for instantaneous offline lookup and quick sync.",
                "Hardware Android KeyStore / StrongBox for private crypto keys.",
                "FusedLocationProviderClient for low-power high-accuracy location tracking."
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        ArchLayerCard(
            layerName = "Layer 2: API Gateway & Microservices",
            techStack = "Envoy Proxy • Go / Node.js Microservices • GraphQL & gRPC",
            points = listOf(
                "Auth & KYC Service: Integrates with UIDAI Aadhaar sandbox & AP livestock department database.",
                "Catalog & Lineage Service: Manages Aseel breed taxonomic trees, ring tag registry, and pedigree certificates.",
                "Geo-Spatial Service: PostGIS / Redis Geohash spatial queries indexing farm coordinates and radius matching."
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        ArchLayerCard(
            layerName = "Layer 3: Messaging Relay & Pub/Sub",
            techStack = "WebSockets • Redis Pub/Sub • Apache Kafka",
            points = listOf(
                "Blind relay server delivering encrypted ciphertexts without reading payloads.",
                "Push notifications via Firebase Cloud Messaging (FCM) with ephemeral data wakeups."
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        ArchLayerCard(
            layerName = "Layer 4: Escrow Banking & Nodal Trustee",
            techStack = "Razorpay Escrow / Cashfree Trustee API • NPCI UPI 2.0 Autopay",
            points = listOf(
                "Webhook-driven state machine managing Escrow deposit -> In-Hold -> OTP Handover Verified -> Payout Settled."
            )
        )
    }
}

@Composable
fun E2EESecuritySection() {
    Column {
        ArchitectureHeaderCard(
            title = "End-to-End Encryption (E2EE) Protocol Plan",
            subtitle = "Implementation details for zero-knowledge privacy between buyers and breeders.",
            icon = Icons.Default.Lock
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Key Agreement: Extended Triple Diffie-Hellman (X3DH)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "When buyer opens chat with breeder, the app combines Identity Keys, Signed Prekeys, and One-Time Prekeys to establish a shared master secret without any server knowledge.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                Text("Session Encryption: Double Ratchet Algorithm", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Each message derives a unique ephemeral key via KDF (Key Derivation Function). Compromise of one message key gives zero backward or forward visibility into past or future conversations.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                Text("Cipher: AES-256-GCM + SHA-256 Mac", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Authenticated encryption guarantees both confidentiality and tamper detection. Fingerprints can be verified directly at the farm via QR scan.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EscrowPaymentSection() {
    Column {
        ArchitectureHeaderCard(
            title = "Escrow Gateway Architecture & State Machine",
            subtitle = "Preventing fraud, safeguarding farmer payouts, and protecting buyers.",
            icon = Icons.Default.AccountBalance
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Escrow Transaction Lifecycle:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                LifecycleStep(title = "Stage 1: Intent & UPI Lock", desc = "Buyer selects rooster and locks deposit via UPI/NetBanking into Trustee Escrow. Nodal account locks amount.")
                LifecycleStep(title = "Stage 2: Appointment & Farm Route", desc = "Buyer receives appointment time and GPS route to the breeder farm in AP.")
                LifecycleStep(title = "Stage 3: Inspection & Vet Check", desc = "Buyer inspects bird, verifies leg ring ID against app registration, checks comb, eyes, vitality and vaccine card.")
                LifecycleStep(title = "Stage 4: OTP Confirmation", desc = "Buyer enters or shares secret 6-digit OTP only upon satisfying inspection.")
                LifecycleStep(title = "Stage 5: Instant RTGS/IMPS Payout", desc = "Automated webhook triggers immediate payout release to the breeder's verified bank account.")
            }
        }
    }
}

@Composable
fun ComplianceLegalSection() {
    Column {
        ArchitectureHeaderCard(
            title = "Animal Welfare & Legal Aviculture Framework",
            subtitle = "Preservation of Andhra indigenous genetic biodiversity and statutory compliance.",
            icon = Icons.Default.Policy
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.HealthAndSafety, contentDescription = null, tint = Color(0xFF15803D))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Legal & Avicultural Preservation Purpose", fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "AseelMart AP is dedicated exclusively to the legitimate preservation, pedigree registration, and farm breeding of historic Andhra Pradesh poultry heritage (Aseel Dega, Nemali, Kaki, Sethuva, Kulang, Reza).",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF166534),
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Strict Regulatory Safeguards Enforced:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF15803D)
                )
                Text("• Zero Tolerance for Illegal Wagering or Cruelty: Prohibits razor spurs, dog fighting, or unapproved spectacles.", fontSize = 11.sp, color = Color(0xFF166534))
                Text("• Mandatory Veterinary Health Passports: All listed birds must have certified Ranikhet and Marek vaccination records.", fontSize = 11.sp, color = Color(0xFF166534))
                Text("• Tamper-Proof Leg Band Identification: Every specimen is indexed with an official AP state breeder ring ID tag.", fontSize = 11.sp, color = Color(0xFF166534))
            }
        }
    }
}

@Composable
fun ArchitectureHeaderCard(title: String, subtitle: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun FeatureBlock(category: String, items: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(category, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            items.forEach { item ->
                Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(item, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, lineHeight = 18.sp)
                }
            }
        }
    }
}

@Composable
fun ArchLayerCard(layerName: String, techStack: String, points: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(layerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = techStack,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            points.forEach { pt ->
                Text("• $pt", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 2.dp))
            }
        }
    }
}

@Composable
fun LifecycleStep(title: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 20.dp, top = 2.dp))
    }
}

@Composable
fun AuthKYCSection() {
    Column {
        ArchitectureHeaderCard(
            title = "Identity, Verification & Compliance Architecture",
            subtitle = "Zero-Knowledge Authentication, Government e-KYC, Farm Audits & DPDP Act 2023.",
            icon = Icons.Default.Security
        )

        Spacer(modifier = Modifier.height(14.dp))

        ArchLayerCard(
            layerName = "1. Zero-Knowledge Authentication Engine",
            techStack = "Salted SHA-256 + SMS Gateway + Android Keystore",
            points = listOf(
                "Multi-Role Registry: Separate cryptographic profiles for Heritage Buyers, Certified Stud Breeders, and Livestock Officers.",
                "OTP Fast Verification: Cellular SMS fallback with 6-digit cryptographic token generation and 5-minute expiry.",
                "Password Protection: Passwords hashed client-side with PBKDF2/SHA-256 with unique salting to protect against rainbow table attacks.",
                "Session Management: JWT bearer tokens stored securely in EncryptedSharedPreferences backed by Android Keystore hardware."
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        ArchLayerCard(
            layerName = "2. Government e-KYC & Breeder Accreditation",
            techStack = "UIDAI Aadhaar Sandbox + AP Kisan Board API",
            points = listOf(
                "Document Masking: Masked Aadhaar / Voter ID storage (retaining only last 4 digits) to comply with UIDAI circulars.",
                "State Board Registry: Cross-verification with Andhra Pradesh Animal Husbandry Department register of indigenous poultry studs.",
                "Breeder Reputation Index: Algorithmic rating combining physical inspection scores, transaction completions, and verified customer reviews.",
                "Tamper-Proof KYC Badge: High-tier blue verification badge issued only upon documentary attestation."
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        ArchLayerCard(
            layerName = "3. On-Site Farm Biosecurity Audit Protocol",
            techStack = "Veterinary Officer Sign-Off + GPS Geofencing",
            points = listOf(
                "Physical Enclosure Inspection: Verification of minimum 2.0-acre open-air enclosure, quarantine pens, and clean water drainage.",
                "Immunization Audit: Mandatory physical inspection of Ranikhet Disease Vaccine (RDV Lasota/R2B) and Marek's disease cold-chain documentation.",
                "Veterinary Health Certificate: Assigned digital certificate ID (e.g. AP-AH-2024-VET-XXXX) stamped by registered MVSc / BVSc officers.",
                "GPS Farm Pinning: High-precision latitude/longitude coordinate binding preventing phantom listings."
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        ArchLayerCard(
            layerName = "4. Purchase & Sale History Audit Trails",
            techStack = "Immutable Merkle Hash + Ledger Logs",
            points = listOf(
                "Cryptographic Receipt Certificates: Every completed escrow transaction generates a unique SHA-256 tamper-proof verification hash.",
                "Dual-Ledger Persistence: Buyers and sellers maintain mirrored transaction entries with counterparty verification metadata.",
                "Leg Ring Traceability: Closed leg band identifier (#AP-XX-2024-XXXX) permanently recorded in purchase record."
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        ArchLayerCard(
            layerName = "5. DPDP Act 2023 & Animal Welfare Compliance",
            techStack = "Statutory Mandates + Privacy By Design",
            points = listOf(
                "Digital Personal Data Protection Act, 2023: Explicit consent capture, right to data access, right to rectification, and right to complete data erasure.",
                "Data Vault Export: User can export all personal profile, chat transcripts, and payment logs in a portable cryptographic bundle.",
                "Prevention of Cruelty to Animals Act, 1960: Zero-tolerance policy prohibiting spur sharpening, artificial blades, and non-heritage wagering.",
                "Aviculture Genetic Conservation: Platform strictly restricted to preserving indigenous breeds (Dega, Nemali, Kaki, Sethuva, Pingala, Rasangi)."
            )
        )
    }
}

