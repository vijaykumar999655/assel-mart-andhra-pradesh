package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.formatRupees
import com.example.ui.viewmodel.MarketplaceViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoosterScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Section Tabs: 0: Breed & Morphology, 1: Health & Pedigree, 2: Farm & Location, 3: Pricing & Review
    var currentStep by remember { mutableIntStateOf(0) }

    // Breeder & Farm Details (Auto-populated from active profile if available)
    var sellerName by remember { mutableStateOf("") }
    var sellerPhone by remember { mutableStateOf("") }
    var farmName by remember { mutableStateOf("") }
    var farmDistrict by remember { mutableStateOf("West Godavari") }
    var farmMandal by remember { mutableStateOf("Bhimavaram") }
    var farmAddress by remember { mutableStateOf("Survey No. 142/2, Rayalam Road") }
    var farmLatitude by remember { mutableDoubleStateOf(16.5449) }
    var farmLongitude by remember { mutableDoubleStateOf(81.5212) }

    // Auto-populate when user is logged in
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            sellerName = user.fullName
            sellerPhone = user.phone
            farmName = user.farmName ?: "Heritage Aseel Stud Farm"
            farmDistrict = user.district
            farmMandal = user.mandal
            farmAddress = user.address
            when (user.district) {
                "West Godavari" -> { farmLatitude = 16.5449; farmLongitude = 81.5212 }
                "East Godavari" -> { farmLatitude = 16.9891; farmLongitude = 81.7840 }
                "Krishna" -> { farmLatitude = 16.5062; farmLongitude = 80.6480 }
                "Guntur" -> { farmLatitude = 16.3067; farmLongitude = 80.4365 }
            }
        }
    }

    // Breed & Morphological Details
    var breedTitle by remember { mutableStateOf("Pure Dega Champion Specimen") }
    var breedType by remember { mutableStateOf("Dega") } // Dega, Nemali, Kaki, Sethuva, Pingala, Rasangi, Maiyle, Teha
    var lineage by remember { mutableStateOf("Gaja (Heavy Bone & Broad Stance)") }
    var ageMonthsText by remember { mutableStateOf("14") }
    var weightKgText by remember { mutableStateOf("4.2") }
    var heightInchesText by remember { mutableStateOf("27.5") }
    var plumageColor by remember { mutableStateOf("Fiery Crimson & Deep Amber Luster") }
    var combType by remember { mutableStateOf("Pea Comb (Small & Tight)") }
    var eyeColor by remember { mutableStateOf("Ruby Red Iris") }
    var shankColor by remember { mutableStateOf("Yellow with Prominent Scales") }
    var spursCondition by remember { mutableStateOf("Natural Intact, Symmetrical & Unaltered") }

    // Health, Ring Tag & Certification Details
    var ringTagId by remember { mutableStateOf("AP-${farmDistrict.take(2).uppercase()}-2024-${(1000..9999).random()}") }
    var hasRanikhetVaccine by remember { mutableStateOf(true) }
    var hasMarekVaccine by remember { mutableStateOf(true) }
    var hasFowlPoxVaccine by remember { mutableStateOf(true) }
    var hasDeworming by remember { mutableStateOf(true) }
    var vetHealthCertId by remember { mutableStateOf("AP-AH-2024-VET-9821") }
    var vetSurgeonName by remember { mutableStateOf("Dr. K. Satyanarayana, Senior Vet Officer") }

    // Media & Visuals
    var selectedImageIndex by remember { mutableIntStateOf(0) }
    val sampleImages = listOf("rooster_dega_nemali", "banner_aseel_heritage")
    var videoDemoLink by remember { mutableStateOf("https://aseelmart.ap.gov/media/verify_gaja_dega_01.mp4") }

    // Commercial Terms & Statutory Declaration
    var priceRupeesText by remember { mutableStateOf("32000") }
    var minEscrowDepositText by remember { mutableStateOf("5000") }
    var handoverMode by remember { mutableStateOf("Farm In-Person Inspection (Recommended)") }
    var description by remember { mutableStateOf("Exceptional specimen of pure Andhra Dega bred under traditional natural grain regimen (ragi, sprouted moong, boiled eggs). Certified free of artificial modifications. Visitors welcome for live physical appraisal.") }
    var animalWelfareDeclaration by remember { mutableStateOf(true) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showLiveCardPreview by remember { mutableStateOf(true) }

    val breedOptions = listOf("Dega", "Nemali", "Kaki", "Sethuva", "Pingala", "Rasangi", "Maiyle", "Teha")
    val lineageOptions = listOf(
        "Gaja (Heavy Bone & Broad Stance)",
        "Reza (High Stance & Swift Agility)",
        "Kulang (Tall Upright Specimen 28\"+)"
    )
    val districtOptions = listOf("West Godavari", "East Godavari", "Krishna", "Guntur", "Visakhapatnam", "Nellore")
    val combOptions = listOf("Pea Comb (Small & Tight)", "Walnut Comb", "Strawberry Comb")
    val eyeColorOptions = listOf("Ruby Red Iris", "Pearl White", "Golden Amber")
    val shankOptions = listOf("Yellow Scaled", "Slate / Dark Black", "Ivory White")

    var breedDropdownExpanded by remember { mutableStateOf(false) }
    var lineageDropdownExpanded by remember { mutableStateOf(false) }
    var districtDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Create Rooster Listing", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Andhra Pradesh Pedigree Stud Registry", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { showLiveCardPreview = !showLiveCardPreview }) {
                        Icon(imageVector = Icons.Default.Preview, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (showLiveCardPreview) "Hide Preview" else "Live Preview", fontSize = 12.sp)
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
            // Step Progression Tabs
            PrimaryTabRow(selectedTabIndex = currentStep) {
                Tab(
                    selected = currentStep == 0,
                    onClick = { currentStep = 0 },
                    text = { Text("1. Traits", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = currentStep == 1,
                    onClick = { currentStep = 1 },
                    text = { Text("2. Health", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = currentStep == 2,
                    onClick = { currentStep = 2 },
                    text = { Text("3. Farm", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = currentStep == 3,
                    onClick = { currentStep = 3 },
                    text = { Text("4. Terms", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Live Card Preview Toggle
                AnimatedVisibility(visible = showLiveCardPreview) {
                    Column {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Preview, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Live Marketplace Listing Preview", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                        Text("PREVIEW", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Visual Mini Card
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = painterResource(
                                                id = if (selectedImageIndex == 0) R.drawable.rooster_dega_nemali else R.drawable.banner_aseel_heritage
                                            ),
                                            contentDescription = "Listing Thumbnail",
                                            modifier = Modifier
                                                .size(76.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = breedTitle.ifBlank { "Aseel Rooster" },
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    maxLines = 1
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(14.dp))
                                            }

                                            Text(
                                                text = "${breedType} • ${weightKgText}kg • ${heightInchesText}\" • ${lineage.take(4)}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )

                                            Text(
                                                text = "Tag: $ringTagId",
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = MaterialTheme.colorScheme.primary
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = formatRupees(priceRupeesText.toLongOrNull() ?: 25000),
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    text = "$farmMandal, $farmDistrict",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                when (currentStep) {
                    // ==================== STEP 0: BREED & MORPHOLOGY ====================
                    0 -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Pets, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Breed Specification & Stance", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = breedTitle,
                                    onValueChange = { breedTitle = it },
                                    label = { Text("Listing Title *") },
                                    placeholder = { Text("e.g. Pure Bhimavaram Dega Heavy Bone Specimen") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("listing_title_input")
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Breed Variety (Andhra Indigenous Class)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    breedOptions.take(4).forEach { b ->
                                        FilterChip(
                                            selected = breedType == b,
                                            onClick = { breedType = b },
                                            label = { Text(b, fontSize = 11.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    breedOptions.drop(4).forEach { b ->
                                        FilterChip(
                                            selected = breedType == b,
                                            onClick = { breedType = b },
                                            label = { Text(b, fontSize = 11.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                ExposedDropdownMenuBox(
                                    expanded = lineageDropdownExpanded,
                                    onExpandedChange = { lineageDropdownExpanded = !lineageDropdownExpanded },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = lineage,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Lineage & Stance Classification *") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = lineageDropdownExpanded) },
                                        modifier = Modifier.fillMaxWidth().menuAnchor()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = lineageDropdownExpanded,
                                        onDismissRequest = { lineageDropdownExpanded = false }
                                    ) {
                                        lineageOptions.forEach { opt ->
                                            DropdownMenuItem(
                                                text = { Text(opt) },
                                                onClick = {
                                                    lineage = opt
                                                    lineageDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Physical Metrics Grid
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = ageMonthsText,
                                        onValueChange = { ageMonthsText = it },
                                        label = { Text("Age (Months)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("age_input")
                                    )
                                    OutlinedTextField(
                                        value = weightKgText,
                                        onValueChange = { weightKgText = it },
                                        label = { Text("Weight (Kg)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("weight_input")
                                    )
                                    OutlinedTextField(
                                        value = heightInchesText,
                                        onValueChange = { heightInchesText = it },
                                        label = { Text("Height (\")") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("height_input")
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = plumageColor,
                                    onValueChange = { plumageColor = it },
                                    label = { Text("Plumage & Feather Characteristics") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = combType,
                                        onValueChange = { combType = it },
                                        label = { Text("Comb Structure") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = eyeColor,
                                        onValueChange = { eyeColor = it },
                                        label = { Text("Eye Iris Color") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = spursCondition,
                                    onValueChange = { spursCondition = it },
                                    label = { Text("Spurs Condition & Morphology (Unaltered)") },
                                    supportingText = { Text("Must be natural and unmodified. Prohibited weapons strictly barred.", fontSize = 10.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { currentStep = 1 },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Next: Health & Ring Tag Identification")
                                }
                            }
                        }
                    }

                    // ==================== STEP 1: HEALTH & PEDIGREE ====================
                    1 -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.HealthAndSafety, contentDescription = null, tint = Color(0xFF15803D))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Veterinary Health & Ring Tag ID", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Ring Tag ID Generator
                                OutlinedTextField(
                                    value = ringTagId,
                                    onValueChange = { ringTagId = it },
                                    label = { Text("Official Closed Leg Band / Ring Tag ID *") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Badge, contentDescription = null) },
                                    trailingIcon = {
                                        IconButton(onClick = {
                                            ringTagId = "AP-${farmDistrict.take(2).uppercase()}-2024-${(1000..9999).random()}"
                                        }) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = "Regenerate")
                                        }
                                    },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("ring_tag_input")
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Mandatory Immunization Protocol Attestation", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = hasRanikhetVaccine, onCheckedChange = { hasRanikhetVaccine = it })
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Ranikhet Disease Vaccine (RDV Lasota / R2B Booster)", fontSize = 12.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = hasMarekVaccine, onCheckedChange = { hasMarekVaccine = it })
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Marek's Disease Vaccine (HVT/CVI 988)", fontSize = 12.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = hasFowlPoxVaccine, onCheckedChange = { hasFowlPoxVaccine = it })
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Fowl Pox Active Immunization", fontSize = 12.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = hasDeworming, onCheckedChange = { hasDeworming = it })
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Quarterly Broad-Spectrum Deworming Verified", fontSize = 12.sp)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = vetHealthCertId,
                                    onValueChange = { vetHealthCertId = it },
                                    label = { Text("Veterinary Health Certificate Number") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.MedicalServices, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = vetSurgeonName,
                                    onValueChange = { vetSurgeonName = it },
                                    label = { Text("Attending Veterinary Practitioner Name") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { currentStep = 0 },
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Previous")
                                    }
                                    Button(
                                        onClick = { currentStep = 2 },
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Next: Farm Info")
                                    }
                                }
                            }
                        }
                    }

                    // ==================== STEP 2: FARM & LOCATION ====================
                    2 -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Agriculture, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Farm Location & Photo Gallery", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = sellerName,
                                    onValueChange = { sellerName = it },
                                    label = { Text("Breeder Legal Full Name *") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = sellerPhone,
                                    onValueChange = { sellerPhone = it },
                                    label = { Text("Contact Phone (+91) *") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = farmName,
                                    onValueChange = { farmName = it },
                                    label = { Text("Stud / Farm Enterprise Name *") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Agriculture, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("farm_name_input")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                ExposedDropdownMenuBox(
                                    expanded = districtDropdownExpanded,
                                    onExpandedChange = { districtDropdownExpanded = !districtDropdownExpanded },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = farmDistrict,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Andhra Pradesh District *") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtDropdownExpanded) },
                                        modifier = Modifier.fillMaxWidth().menuAnchor()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = districtDropdownExpanded,
                                        onDismissRequest = { districtDropdownExpanded = false }
                                    ) {
                                        districtOptions.forEach { dist ->
                                            DropdownMenuItem(
                                                text = { Text(dist) },
                                                onClick = {
                                                    farmDistrict = dist
                                                    districtDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = farmMandal,
                                    onValueChange = { farmMandal = it },
                                    label = { Text("Mandal / Town *") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = farmAddress,
                                    onValueChange = { farmAddress = it },
                                    label = { Text("Farm Survey Address / Landmark *") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Visual Media Selector
                                Text("Select Rooster Feature Photo", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    sampleImages.forEachIndexed { idx, resName ->
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            border = androidx.compose.foundation.BorderStroke(
                                                2.dp,
                                                if (selectedImageIndex == idx) MaterialTheme.colorScheme.primary else Color.Transparent
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(80.dp)
                                                .clickable { selectedImageIndex = idx }
                                        ) {
                                            Image(
                                                painter = painterResource(
                                                    id = if (idx == 0) R.drawable.rooster_dega_nemali else R.drawable.banner_aseel_heritage
                                                ),
                                                contentDescription = "Sample $idx",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { currentStep = 1 },
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Previous")
                                    }
                                    Button(
                                        onClick = { currentStep = 3 },
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Next: Pricing")
                                    }
                                }
                            }
                        }
                    }

                    // ==================== STEP 3: PRICING, ESCROW & PUBLISH ====================
                    3 -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Pricing, Escrow Terms & Attestation", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = priceRupeesText,
                                    onValueChange = { priceRupeesText = it },
                                    label = { Text("Asking Price in INR (₹) *") },
                                    leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("price_input")
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = description,
                                    onValueChange = { description = it },
                                    label = { Text("Detailed Lineage & Farm Pedigree Notes") },
                                    minLines = 3,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("AseelMart AP Safe Escrow Handover Guarantee:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("1. Buyer deposits ${formatRupees(priceRupeesText.toLongOrNull() ?: 25000)} into Trustee Escrow.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("2. Buyer conducts in-person physical inspection at $farmMandal.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("3. Veterinary health certificate and leg band #$ringTagId verified.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("4. Handover OTP entered to release payout to seller's bank.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = animalWelfareDeclaration,
                                        onCheckedChange = { animalWelfareDeclaration = it }
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Statutory Declaration: I certify under the Prevention of Cruelty to Animals Act, 1960 that this rooster has intact natural spurs without artificial attachments and is raised exclusively for legal heritage pedigree conservation.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 15.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { currentStep = 2 },
                                        modifier = Modifier.weight(0.7f).height(50.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Back")
                                    }

                                    Button(
                                        onClick = {
                                            if (breedTitle.isBlank()) {
                                                scope.launch { snackbarHostState.showSnackbar("Please enter a listing title.") }
                                                return@Button
                                            }
                                            val priceVal = priceRupeesText.toLongOrNull()
                                            if (priceVal == null || priceVal <= 0) {
                                                scope.launch { snackbarHostState.showSnackbar("Please enter a valid price in INR.") }
                                                return@Button
                                            }
                                            if (!animalWelfareDeclaration) {
                                                scope.launch { snackbarHostState.showSnackbar("You must accept the statutory animal welfare declaration.") }
                                                return@Button
                                            }

                                            isSubmitting = true
                                            val ageVal = ageMonthsText.toIntOrNull() ?: 12
                                            val weightVal = weightKgText.toDoubleOrNull() ?: 3.8
                                            val heightVal = heightInchesText.toDoubleOrNull() ?: 26.0

                                            val fullVaccineStatus = buildString {
                                                if (hasRanikhetVaccine) append("Ranikhet (RDV), ")
                                                if (hasMarekVaccine) append("Marek's, ")
                                                if (hasFowlPoxVaccine) append("Fowl Pox, ")
                                                if (hasDeworming) append("Dewormed")
                                            }.trimEnd(',', ' ')

                                            viewModel.registerNewRooster(
                                                breedName = breedTitle,
                                                breedType = breedType,
                                                lineage = lineage,
                                                ageMonths = ageVal,
                                                weightKg = weightVal,
                                                heightInches = heightVal,
                                                plumageColor = plumageColor,
                                                spursCondition = spursCondition,
                                                vaccinationStatus = fullVaccineStatus,
                                                priceRupees = priceVal,
                                                sellerName = sellerName.ifBlank { "Penmatsa Varma Raju" },
                                                sellerPhone = sellerPhone.ifBlank { "+91 98480 23145" },
                                                farmName = farmName.ifBlank { "Heritage Aseel Stud Farm" },
                                                farmDistrict = farmDistrict,
                                                farmMandal = farmMandal,
                                                farmAddress = farmAddress,
                                                description = description,
                                                onSuccess = {
                                                    isSubmitting = false
                                                    onSuccess()
                                                }
                                            )
                                        },
                                        modifier = Modifier.weight(1.3f).height(50.dp).testTag("publish_rooster_button"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (isSubmitting) "Publishing..." else "Publish Listing", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
