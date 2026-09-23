package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register
    var isOtpLoginMode by remember { mutableStateOf(false) }

    // Login Fields
    var loginIdentifier by remember { mutableStateOf("+91 98480 11223") }
    var loginPassword by remember { mutableStateOf("demo1234") }
    var loginOtp by remember { mutableStateOf("849201") }
    var loginPasswordVisible by remember { mutableStateOf(false) }
    var loginErrorMessage by remember { mutableStateOf<String?>(null) }

    // Register Fields
    var regRole by remember { mutableStateOf("BUYER") } // "BUYER", "SELLER", "BOTH"
    var regFullName by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regPasswordVisible by remember { mutableStateOf(false) }
    var regFarmName by remember { mutableStateOf("") }
    var regDistrict by remember { mutableStateOf("West Godavari") }
    var regMandal by remember { mutableStateOf("") }
    var regAddress by remember { mutableStateOf("") }
    var regDistrictExpanded by remember { mutableStateOf(false) }
    var regPrivacyConsent by remember { mutableStateOf(true) }
    var regAnimalWelfareConsent by remember { mutableStateOf(true) }
    var regErrorMessage by remember { mutableStateOf<String?>(null) }
    var regSuccessMessage by remember { mutableStateOf<String?>(null) }

    val districtOptions = listOf("West Godavari", "East Godavari", "Krishna", "Guntur", "Visakhapatnam", "Nellore")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("AseelMart AP Identity", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Secure Buyer & Breeder Authentication", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            // Quick Demo Switcher Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Quick Demo Authentication",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Instantly switch into pre-verified demo personas with active records:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.switchDemoAccount("user_buyer_vijay")
                                onAuthSuccess()
                            },
                            modifier = Modifier.weight(1f).testTag("quick_login_buyer"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Buyer (Vijay)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.switchDemoAccount("user_seller_varma")
                                onAuthSuccess()
                            },
                            modifier = Modifier.weight(1f).testTag("quick_login_seller"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Breeder (Varma)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs: Login vs Register
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Sign In", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_login")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Create Account", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_register")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                // ==================== LOGIN TAB ====================
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
                            Text(
                                text = if (isOtpLoginMode) "Fast OTP Verification (+91 SMS)" else "Password Authentication",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = { isOtpLoginMode = !isOtpLoginMode }) {
                                Text(if (isOtpLoginMode) "Use Password" else "Use OTP")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = loginIdentifier,
                            onValueChange = { loginIdentifier = it },
                            label = { Text(if (isOtpLoginMode) "Registered Phone Number" else "Phone Number or Email") },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (loginIdentifier.contains("@")) Icons.Default.Email else Icons.Default.Phone,
                                    contentDescription = null
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("login_identifier_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isOtpLoginMode) {
                            OutlinedTextField(
                                value = loginOtp,
                                onValueChange = { loginOtp = it },
                                label = { Text("6-Digit OTP Code") },
                                leadingIcon = { Icon(imageVector = Icons.Default.Pin, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("login_otp_input")
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "OTP sent to Indian cellular network. For testing: enter '849201'.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            OutlinedTextField(
                                value = loginPassword,
                                onValueChange = { loginPassword = it },
                                label = { Text("Account Password") },
                                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                                trailingIcon = {
                                    IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                        Icon(
                                            imageVector = if (loginPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle password"
                                        )
                                    }
                                },
                                visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("login_password_input")
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Default demo password for seeded accounts: demo1234",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        loginErrorMessage?.let { err ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = err,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                loginErrorMessage = null
                                if (isOtpLoginMode) {
                                    viewModel.loginWithOtp(loginIdentifier, loginOtp) { success, err ->
                                        if (success) onAuthSuccess() else loginErrorMessage = err
                                    }
                                } else {
                                    viewModel.login(loginIdentifier, loginPassword) { success, err ->
                                        if (success) onAuthSuccess() else loginErrorMessage = err
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_submit_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Secure Sign In", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            } else {
                // ==================== REGISTER TAB ====================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Select Account Profile Type",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = regRole == "BUYER",
                                onClick = { regRole = "BUYER" },
                                label = { Text("Heritage Buyer") },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = regRole == "SELLER",
                                onClick = { regRole = "SELLER" },
                                label = { Text("Breeder Farm") },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = regRole == "BOTH",
                                onClick = { regRole = "BOTH" },
                                label = { Text("Both") },
                                modifier = Modifier.weight(0.8f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = regFullName,
                            onValueChange = { regFullName = it },
                            label = { Text("Full Legal Name *") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("reg_name_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regPhone,
                            onValueChange = { regPhone = it },
                            label = { Text("Phone Number (+91) *") },
                            placeholder = { Text("+91 98480 XXXXX") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("reg_phone_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("Email Address (Optional)") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text("Create Strong Password *") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { regPasswordVisible = !regPasswordVisible }) {
                                    Icon(
                                        imageVector = if (regPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password"
                                    )
                                }
                            },
                            visualTransformation = if (regPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("reg_password_input")
                        )

                        // Seller-Specific Farm Details
                        if (regRole == "SELLER" || regRole == "BOTH") {
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Agriculture, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Breeder Farm Verification Details", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = regFarmName,
                                        onValueChange = { regFarmName = it },
                                        label = { Text("Farm / Stud Name *") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth().testTag("reg_farm_name_input")
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    ExposedDropdownMenuBox(
                                        expanded = regDistrictExpanded,
                                        onExpandedChange = { regDistrictExpanded = !regDistrictExpanded },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        OutlinedTextField(
                                            value = regDistrict,
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("Farm District in AP *") },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = regDistrictExpanded) },
                                            modifier = Modifier.fillMaxWidth().menuAnchor()
                                        )
                                        ExposedDropdownMenu(
                                            expanded = regDistrictExpanded,
                                            onDismissRequest = { regDistrictExpanded = false }
                                        ) {
                                            districtOptions.forEach { d ->
                                                DropdownMenuItem(
                                                    text = { Text(d) },
                                                    onClick = {
                                                        regDistrict = d
                                                        regDistrictExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = regMandal,
                                        onValueChange = { regMandal = it },
                                        label = { Text("Mandal / Town *") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = regAddress,
                                        onValueChange = { regAddress = it },
                                        label = { Text("Farm Survey Address *") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Regulatory & DPDP Act 2023 Consent Checkboxes
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = regPrivacyConsent,
                                onCheckedChange = { regPrivacyConsent = it }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "I agree to the Digital Personal Data Protection (DPDP) Act 2023 terms. My identity documents and farm GPS are securely stored in AES-256 encrypted vaults.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 15.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = regAnimalWelfareConsent,
                                onCheckedChange = { regAnimalWelfareConsent = it }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Animal Welfare Declaration: I commit strictly to legitimate aviculture heritage conservation under the Prevention of Cruelty to Animals Act, 1960.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 15.sp
                            )
                        }

                        regErrorMessage?.let { err ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = err,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                regErrorMessage = null
                                if (regFullName.isBlank()) {
                                    regErrorMessage = "Please enter your full legal name."
                                    return@Button
                                }
                                if (regPhone.isBlank()) {
                                    regErrorMessage = "Please enter your phone number."
                                    return@Button
                                }
                                if (regPassword.length < 6) {
                                    regErrorMessage = "Password must be at least 6 characters."
                                    return@Button
                                }
                                if (!regPrivacyConsent || !regAnimalWelfareConsent) {
                                    regErrorMessage = "You must agree to the DPDP Act and Animal Welfare declarations."
                                    return@Button
                                }

                                viewModel.register(
                                    fullName = regFullName,
                                    phone = regPhone,
                                    email = regEmail,
                                    passwordPlain = regPassword,
                                    role = regRole,
                                    farmName = if (regRole != "BUYER") regFarmName else null,
                                    district = regDistrict,
                                    mandal = regMandal.ifBlank { "Andhra Pradesh" },
                                    address = regAddress.ifBlank { regDistrict },
                                    privacyConsentAccepted = regPrivacyConsent
                                ) { success, err ->
                                    if (success) {
                                        onAuthSuccess()
                                    } else {
                                        regErrorMessage = err
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("register_submit_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Register & Verify Account", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
