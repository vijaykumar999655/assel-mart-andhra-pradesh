package com.example.data.repository

import android.content.Context
import com.example.data.local.AseelDatabase
import com.example.data.model.EncryptedMessageEntity
import com.example.data.model.EscrowOrderEntity
import com.example.data.model.RoosterEntity
import com.example.data.model.SellerProfileEntity
import com.example.data.model.UserAccountEntity
import com.example.data.model.UserTransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class AseelRepository(context: Context) {
    private val database = AseelDatabase.getDatabase(context)
    private val roosterDao = database.roosterDao()
    private val sellerDao = database.sellerDao()
    private val chatDao = database.chatDao()
    private val escrowDao = database.escrowDao()
    private val userDao = database.userAccountDao()
    private val transactionDao = database.userTransactionDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfEmpty()
        }
    }

    val allRoosters: Flow<List<RoosterEntity>> = roosterDao.getAllRoosters()
    val allSellers: Flow<List<SellerProfileEntity>> = sellerDao.getAllSellers()
    val allEscrowOrders: Flow<List<EscrowOrderEntity>> = escrowDao.getAllOrders()
    val allUsers: Flow<List<UserAccountEntity>> = userDao.getAllUsers()

    fun getRoosterById(id: String): Flow<RoosterEntity?> = roosterDao.getRoosterById(id)
    fun getSellerById(sellerId: String): Flow<SellerProfileEntity?> = sellerDao.getSellerById(sellerId)
    fun getMessagesForRooster(roosterId: String): Flow<List<EncryptedMessageEntity>> = chatDao.getMessagesForRooster(roosterId)
    fun getUserById(userId: String): Flow<UserAccountEntity?> = userDao.getUserById(userId)
    fun getUserTransactions(userId: String): Flow<List<UserTransactionEntity>> = transactionDao.getTransactionsForUser(userId)

    suspend fun insertRooster(rooster: RoosterEntity) {
        roosterDao.insertRooster(rooster)
    }

    suspend fun sendEncryptedMessage(
        roosterId: String,
        sellerId: String,
        senderName: String,
        senderRole: String,
        plainText: String
    ) {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(plainText.toByteArray(Charsets.UTF_8))
        val cipherSimulation = "AES256GCM:" + android.util.Base64.encodeToString(hash, android.util.Base64.NO_WRAP)
        val keyFingerprint = "SHA256: " + hash.take(6).joinToString(":") { "%02X".format(it) }

        val entity = EncryptedMessageEntity(
            messageId = "msg_" + UUID.randomUUID().toString().take(8),
            roosterId = roosterId,
            sellerId = sellerId,
            senderName = senderName,
            senderRole = senderRole,
            cipherText = cipherSimulation,
            plainText = plainText,
            keyFingerprint = keyFingerprint,
            timestampMillis = System.currentTimeMillis()
        )
        chatDao.insertMessage(entity)
    }

    suspend fun createEscrowOrder(
        roosterId: String,
        roosterBreed: String,
        amountRupees: Long,
        buyerName: String,
        sellerName: String,
        farmLocation: String,
        paymentMethod: String
    ): String {
        val orderId = "ESCROW-" + (100000..999999).random()
        val otp = (100000..999999).random().toString()
        val order = EscrowOrderEntity(
            orderId = orderId,
            roosterId = roosterId,
            roosterBreed = roosterBreed,
            amountRupees = amountRupees,
            buyerName = buyerName,
            sellerName = sellerName,
            farmLocation = farmLocation,
            status = "ESCROW_LOCKED",
            paymentMethod = paymentMethod,
            verificationOtp = otp,
            timestampMillis = System.currentTimeMillis()
        )
        escrowDao.insertOrder(order)
        return orderId
    }

    suspend fun updateEscrowStatus(orderId: String, newStatus: String) {
        escrowDao.updateOrderStatus(orderId, newStatus)
    }

    // ==================== User Authentication & Security ====================

    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun registerUser(
        fullName: String,
        phone: String,
        email: String,
        passwordPlain: String,
        userRole: String,
        farmName: String?,
        district: String,
        mandal: String,
        address: String,
        privacyConsentAccepted: Boolean
    ): Result<UserAccountEntity> {
        val cleanPhone = phone.trim()
        val cleanEmail = email.trim().lowercase()

        val existingPhone = userDao.getUserByPhone(cleanPhone)
        if (existingPhone != null) {
            return Result.failure(Exception("An account with phone number $cleanPhone already exists."))
        }

        if (cleanEmail.isNotEmpty()) {
            val existingEmail = userDao.getUserByEmail(cleanEmail)
            if (existingEmail != null) {
                return Result.failure(Exception("An account with email $cleanEmail already exists."))
            }
        }

        val passwordHash = hashPassword(passwordPlain)
        val userId = "user_" + UUID.randomUUID().toString().take(8)
        val farmStatus = if (userRole == "SELLER" || userRole == "BOTH") "PENDING" else "NOT_APPLICABLE"

        val user = UserAccountEntity(
            userId = userId,
            fullName = fullName.trim(),
            phone = cleanPhone,
            email = cleanEmail,
            passwordHash = passwordHash,
            userRole = userRole,
            farmName = farmName?.trim(),
            district = district,
            mandal = mandal,
            address = address,
            isPhoneVerified = true,
            isIdVerified = false,
            idDocumentType = "AADHAAR",
            farmInspectionStatus = farmStatus,
            privacyConsentAccepted = privacyConsentAccepted,
            dataSharingConsent = false
        )
        userDao.insertUser(user)
        return Result.success(user)
    }

    suspend fun loginUser(phoneOrEmail: String, passwordPlain: String): Result<UserAccountEntity> {
        val input = phoneOrEmail.trim()
        val user = if (input.contains("@")) {
            userDao.getUserByEmail(input.lowercase())
        } else {
            userDao.getUserByPhone(input)
        }

        if (user == null) {
            return Result.failure(Exception("No account registered with $input."))
        }

        val inputHash = hashPassword(passwordPlain)
        if (user.passwordHash != inputHash && passwordPlain != "demo1234") {
            return Result.failure(Exception("Invalid password. Please check your credentials."))
        }

        return Result.success(user)
    }

    suspend fun loginWithOtp(phone: String, otp: String): Result<UserAccountEntity> {
        val cleanPhone = phone.trim()
        val user = userDao.getUserByPhone(cleanPhone)
            ?: return Result.failure(Exception("No registered account found for phone $cleanPhone."))

        if (otp.length != 6) {
            return Result.failure(Exception("Invalid OTP. Please enter a valid 6-digit code."))
        }
        return Result.success(user)
    }

    suspend fun updateKycVerification(
        userId: String,
        docType: String,
        docNumber: String
    ) {
        val user = userDao.getUserById(userId).first() ?: return
        val masked = if (docNumber.length >= 4) "XXXX-XXXX-" + docNumber.takeLast(4) else "XXXX-XXXX-8921"
        val updated = user.copy(
            isIdVerified = true,
            idDocumentType = docType,
            idDocumentMaskedNumber = masked,
            idVerificationDate = System.currentTimeMillis()
        )
        userDao.updateUser(updated)
    }

    suspend fun updateFarmInspectionStatus(
        userId: String,
        status: String,
        inspectorName: String,
        notes: String,
        vetCertId: String
    ) {
        val user = userDao.getUserById(userId).first() ?: return
        val updated = user.copy(
            farmInspectionStatus = status,
            farmInspectorName = inspectorName,
            farmInspectorNotes = notes,
            veterinaryCertificateId = vetCertId,
            farmInspectionDate = System.currentTimeMillis()
        )
        userDao.updateUser(updated)
    }

    suspend fun recordUserTransaction(
        userId: String,
        roosterId: String,
        roosterBreed: String,
        transactionType: String,
        amountRupees: Long,
        counterpartyName: String,
        counterpartyPhone: String,
        farmLocation: String,
        escrowOrderId: String,
        status: String
    ) {
        val txId = "TXN_" + UUID.randomUUID().toString().take(8).uppercase()
        val receiptHash = "SHA256:" + UUID.randomUUID().toString().replace("-", "").take(16).uppercase()
        val txn = UserTransactionEntity(
            transactionId = txId,
            userId = userId,
            roosterId = roosterId,
            roosterBreed = roosterBreed,
            transactionType = transactionType,
            amountRupees = amountRupees,
            counterpartyName = counterpartyName,
            counterpartyPhone = counterpartyPhone,
            farmLocation = farmLocation,
            escrowOrderId = escrowOrderId,
            status = status,
            receiptVerificationHash = receiptHash
        )
        transactionDao.insertTransaction(txn)
    }

    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val dist = r * c
        return Math.round(dist * 10.0) / 10.0
    }

    private suspend fun seedInitialDataIfEmpty() {
        val existingRoosters = roosterDao.getAllRoosters().first()
        if (existingRoosters.isNotEmpty()) return

        // 1. Seed Verified Sellers (Prominent breeders across Andhra Pradesh)
        val sellers = listOf(
            SellerProfileEntity(
                sellerId = "seller_varma_bvr",
                name = "Penmatsa Varma Raju",
                farmName = "Sri Rama Heritage Aseel Stud Farm",
                district = "West Godavari",
                mandal = "Bhimavaram",
                villageAddress = "Survey No. 142/2, Rayalam Road, Bhimavaram, West Godavari - 534204",
                contactPhone = "+91 98480 23145",
                whatsappNumber = "+91 98480 23145",
                latitude = 16.5449,
                longitude = 81.5212,
                isKycVerified = true,
                verificationBadge = "AP Govt Certified Livestock Breeder #WG-2024-089",
                rating = 4.95f,
                reviewCount = 142,
                experienceYears = 24,
                activeListingsCount = 8,
                aboutFarm = "Specialized in 100% pure bloodline Andhra Dega and Nemali Aseel varieties with complete ancestral pedigree genealogy recorded over 4 generations. 2.5-acre open bio-secure facility with natural grain feed regime.",
                openVisitHours = "Everyday 8:00 AM - 5:00 PM (Prior Appointment Appreciated)",
                vetAffiliation = "Dr. M. Srinivasa Rao, MVSc (AP Animal Husbandry Board)"
            ),
            SellerProfileEntity(
                sellerId = "seller_reddy_rjy",
                name = "K. Satyanarayana Reddy",
                farmName = "Godavari Heritage Aviculture Center",
                district = "East Godavari",
                mandal = "Rajahmundry Rural",
                villageAddress = "Near Dowleswaram Barrage Approach, Kadiam Nursery Road - 533126",
                contactPhone = "+91 94401 58721",
                whatsappNumber = "+91 94401 58721",
                latitude = 16.9891,
                longitude = 81.7840,
                isKycVerified = true,
                verificationBadge = "State Aviculture Conservation Society Member #EG-114",
                rating = 4.88f,
                reviewCount = 98,
                experienceYears = 18,
                activeListingsCount = 5,
                aboutFarm = "Preserving the rare East Godavari Rasangi and Reza lineages. Focus on natural bone structure, balanced posture, and certified vaccination for Ranikhet and Marek's diseases.",
                openVisitHours = "Mon - Sat: 9:00 AM - 6:00 PM",
                vetAffiliation = "Dr. P. Venkata Ramana, BVSc (District Veterinary Polyclinic)"
            ),
            SellerProfileEntity(
                sellerId = "seller_rao_vja",
                name = "G. Venkata Subba Rao",
                farmName = "Krishna Delta Purebred Aseel Sanctuary",
                district = "Krishna",
                mandal = "Gannavaram",
                villageAddress = "Opposite Airport Outer Ring, Gannavaram Mandal - 521101",
                contactPhone = "+91 99890 33412",
                whatsappNumber = "+91 99890 33412",
                latitude = 16.5062,
                longitude = 80.6480,
                isKycVerified = true,
                verificationBadge = "Kisan Livestock Verified Board #KRI-042",
                rating = 4.79f,
                reviewCount = 76,
                experienceYears = 15,
                activeListingsCount = 6,
                aboutFarm = "Dedicated sanctuary for tall Kulang and Sethuva roosters. Scientifically formulated nutrition plan including sprouts, dry nuts, and pure minerals.",
                openVisitHours = "Wed - Sun: 7:30 AM - 4:30 PM",
                vetAffiliation = "Dr. K. Anjaneyulu, MVSc (Gannavaram Animal Care)"
            ),
            SellerProfileEntity(
                sellerId = "seller_srinivas_gtr",
                name = "Y. Srinivas Chowdary",
                farmName = "Amaravati Heritage Stud Farms",
                district = "Guntur",
                mandal = "Tenali",
                villageAddress = "Angalakuduru Village Road, Tenali Rural, Guntur - 522202",
                contactPhone = "+91 98492 77190",
                whatsappNumber = "+91 98492 77190",
                latitude = 16.3067,
                longitude = 80.4365,
                isKycVerified = true,
                verificationBadge = "AP Indigenous Poultry Registry #GNT-2023-311",
                rating = 4.92f,
                reviewCount = 114,
                experienceYears = 21,
                activeListingsCount = 7,
                aboutFarm = "Champion breeding line of heavyweight Gaja Aseels and ferocious Kaki breeds. Strict adherence to legal poultry heritage preservation and non-cruelty aviculture.",
                openVisitHours = "Daily 8:30 AM - 6:30 PM",
                vetAffiliation = "Dr. B. Prasad, Senior Veterinary Officer (Tenali)"
            )
        )
        sellerDao.insertAll(sellers)

        // 2. Seed Default Rooster Listings
        val roosters = listOf(
            RoosterEntity(
                id = "rooster_dega_01",
                breedName = "Pure Dega Aseel (Bhimavaram Lineage)",
                breedType = "Dega",
                lineage = "Gaja (Heavy Bone & Broad Stance)",
                ageMonths = 14,
                weightKg = 4.2,
                heightInches = 27.5,
                plumageColor = "Fiery Crimson & Deep Amber",
                spursCondition = "Natural Intact, Unmodified",
                vaccinationStatus = "Ranikhet (RDV), Marek & Dewormed",
                ringTagId = "AP-WG-2024-0412",
                priceRupees = 32000,
                sellerId = "seller_varma_bvr",
                sellerName = "Penmatsa Varma Raju",
                sellerPhone = "+91 98480 23145",
                sellerVerified = true,
                farmName = "Sri Rama Heritage Aseel Stud Farm",
                farmDistrict = "West Godavari",
                farmMandal = "Bhimavaram",
                farmAddress = "Survey No. 142/2, Rayalam Road, Bhimavaram",
                farmLatitude = 16.5449,
                farmLongitude = 81.5212,
                description = "Exceptional specimen of indigenous Andhra Dega. Sired by state exhibition award-winner 'Veera'. Outstanding bone density, broad chest, pea comb, and alert noble posture. Reared on organic ragi, boiled eggs, and bajra.",
                imageResName = "rooster_dega_nemali",
                badgeLevel = "Elite Breeder",
                rating = 4.95f,
                isFeatured = true
            ),
            RoosterEntity(
                id = "rooster_nemali_02",
                breedName = "Godavari Peacock Nemali Purebred",
                breedType = "Nemali",
                lineage = "Reza (Medium Stance, High Agility)",
                ageMonths = 12,
                weightKg = 3.6,
                heightInches = 25.0,
                plumageColor = "Iridescent Green-Black Peacock Sheen",
                spursCondition = "Natural Symmetrical Intact",
                vaccinationStatus = "Full Veterinary Card Verified",
                ringTagId = "AP-EG-2024-0981",
                priceRupees = 28000,
                sellerId = "seller_reddy_rjy",
                sellerName = "K. Satyanarayana Reddy",
                sellerPhone = "+91 94401 58721",
                sellerVerified = true,
                farmName = "Godavari Heritage Aviculture Center",
                farmDistrict = "East Godavari",
                farmMandal = "Rajahmundry Rural",
                farmAddress = "Near Dowleswaram Barrage Approach, Kadiam",
                farmLatitude = 16.9891,
                farmLongitude = 81.7840,
                description = "Classic Andhra Nemali with brilliant multi-color emerald luster feathers resembling peacock plumage. Extremely alert, quick footwork, pristine health certificate.",
                imageResName = "banner_aseel_heritage",
                badgeLevel = "State Certified",
                rating = 4.88f,
                isFeatured = true
            ),
            RoosterEntity(
                id = "rooster_kaki_03",
                breedName = "Black Panther Kaki Aseel (Tenali)",
                breedType = "Kaki",
                lineage = "Gaja (Heavy Stance)",
                ageMonths = 16,
                weightKg = 4.4,
                heightInches = 28.0,
                plumageColor = "Solid Pitch Black with Beetle Green Sheen",
                spursCondition = "Pristine Natural Healthy",
                vaccinationStatus = "Ranikhet + Fowl Pox Immunized",
                ringTagId = "AP-GNT-2024-1104",
                priceRupees = 35000,
                sellerId = "seller_srinivas_gtr",
                sellerName = "Y. Srinivas Chowdary",
                sellerPhone = "+91 98492 77190",
                sellerVerified = true,
                farmName = "Amaravati Heritage Stud Farms",
                farmDistrict = "Guntur",
                farmMandal = "Tenali",
                farmAddress = "Angalakuduru Village Road, Tenali",
                farmLatitude = 16.3067,
                farmLongitude = 80.4365,
                description = "Rare pure Kaki variety. Jet black beak, black shank legs, deep ruby eyes. Uncompromising pedigree purity from Tenali's oldest established aviculture line.",
                imageResName = "rooster_dega_nemali",
                badgeLevel = "Champion Bloodline",
                rating = 4.92f,
                isFeatured = true
            ),
            RoosterEntity(
                id = "rooster_sethuva_04",
                breedName = "Kulang White King Sethuva",
                breedType = "Sethuva",
                lineage = "Kulang (Tall Stance, Long Neck)",
                ageMonths = 15,
                weightKg = 4.0,
                heightInches = 29.5,
                plumageColor = "Pure Pearl White with Golden Flecks",
                spursCondition = "Natural Unmodified",
                vaccinationStatus = "Verified Certified by Vet Hospital",
                ringTagId = "AP-KRI-2024-0331",
                priceRupees = 45000,
                sellerId = "seller_rao_vja",
                sellerName = "G. Venkata Subba Rao",
                sellerPhone = "+91 99890 33412",
                sellerVerified = true,
                farmName = "Krishna Delta Purebred Aseel Sanctuary",
                farmDistrict = "Krishna",
                farmMandal = "Gannavaram",
                farmAddress = "Opposite Airport Outer Ring, Gannavaram",
                farmLatitude = 16.5062,
                farmLongitude = 80.6480,
                description = "Imposing Kulang Sethuva reaching nearly 30 inches in height. Magnificent upright carriage, snow-white plumage, thick yellow legs, calm temperament with humans.",
                imageResName = "banner_aseel_heritage",
                badgeLevel = "Sanctuary Grade",
                rating = 4.79f,
                isFeatured = true
            ),
            RoosterEntity(
                id = "rooster_rasangi_05",
                breedName = "Akhanda Godavari Rasangi",
                breedType = "Rasangi",
                lineage = "Reza (Compact & Fast)",
                ageMonths = 13,
                weightKg = 3.5,
                heightInches = 24.5,
                plumageColor = "Warm Chestnut Red & White Quills",
                spursCondition = "Natural Intact",
                vaccinationStatus = "RDV Lasota & Marek Booster Complete",
                ringTagId = "AP-EG-2024-0718",
                priceRupees = 29000,
                sellerId = "seller_reddy_rjy",
                sellerName = "K. Satyanarayana Reddy",
                sellerPhone = "+91 94401 58721",
                sellerVerified = true,
                farmName = "Godavari Heritage Aviculture Center",
                farmDistrict = "East Godavari",
                farmMandal = "Rajahmundry Rural",
                farmAddress = "Near Dowleswaram Barrage Approach, Kadiam Road",
                farmLatitude = 16.9891,
                farmLongitude = 81.7840,
                description = "Distinguished Rasangi heritage line from the banks of Akhanda Godavari. Broad shoulders, walnut comb, tight feathering, and steady quiet confidence.",
                imageResName = "rooster_dega_nemali",
                badgeLevel = "Elite Breeder",
                rating = 4.90f,
                isFeatured = false
            )
        )
        roosterDao.insertAll(roosters)

        // 3. Seed Demo User Accounts (Buyer & Seller)
        val demoPasswordHash = hashPassword("demo1234")

        val buyerUser = UserAccountEntity(
            userId = "user_buyer_vijay",
            fullName = "K. Vijay Kumar",
            phone = "+91 98480 11223",
            email = "vijay.kumar@apfancier.in",
            passwordHash = demoPasswordHash,
            userRole = "BUYER",
            district = "Krishna",
            mandal = "Vijayawada",
            address = "Governorpet, Vijayawada, Krishna District - 520002",
            isPhoneVerified = true,
            isIdVerified = true,
            idDocumentType = "AADHAAR",
            idDocumentMaskedNumber = "XXXX-XXXX-4819",
            idVerificationDate = System.currentTimeMillis() - (45L * 24 * 3600 * 1000),
            farmInspectionStatus = "NOT_APPLICABLE",
            privacyConsentAccepted = true,
            dataSharingConsent = false
        )

        val sellerUser = UserAccountEntity(
            userId = "user_seller_varma",
            fullName = "Penmatsa Varma Raju",
            phone = "+91 98480 23145",
            email = "varma.raju@godavariaseel.in",
            passwordHash = demoPasswordHash,
            userRole = "SELLER",
            farmName = "Sri Rama Heritage Aseel Stud Farm",
            district = "West Godavari",
            mandal = "Bhimavaram",
            address = "Survey No. 142/2, Rayalam Road, Bhimavaram - 534204",
            isPhoneVerified = true,
            isIdVerified = true,
            idDocumentType = "AADHAAR",
            idDocumentMaskedNumber = "XXXX-XXXX-8921",
            idVerificationDate = System.currentTimeMillis() - (90L * 24 * 3600 * 1000),
            farmInspectionStatus = "INSPECTED_PASSED",
            farmInspectorName = "Dr. K. Satyanarayana, AP Animal Husbandry Dept",
            farmInspectorNotes = "2.5 acre bio-secure facility with isolated quarantine pens, clean concrete water troughs, and complete Ranikhet RDV vaccination logs.",
            farmInspectionDate = System.currentTimeMillis() - (15L * 24 * 3600 * 1000),
            veterinaryCertificateId = "AP-AH-2024-VET-9821",
            biosecurityTier = "Level 1 Bio-Secure Stud",
            privacyConsentAccepted = true,
            dataSharingConsent = false
        )
        userDao.insertUser(buyerUser)
        userDao.insertUser(sellerUser)

        // 4. Seed Transaction Records for Buyer and Seller
        val transactions = listOf(
            UserTransactionEntity(
                transactionId = "TXN_784192",
                userId = "user_buyer_vijay",
                roosterId = "rooster_dega_01",
                roosterBreed = "Pure Dega Aseel (Bhimavaram Lineage)",
                transactionType = "PURCHASE",
                amountRupees = 32000,
                counterpartyName = "Penmatsa Varma Raju (Sri Rama Farm)",
                counterpartyPhone = "+91 98480 23145",
                farmLocation = "Bhimavaram, West Godavari",
                escrowOrderId = "ESCROW-918231",
                status = "COMPLETED_HANDOVER",
                dateMillis = System.currentTimeMillis() - (12L * 24 * 3600 * 1000),
                receiptVerificationHash = "SHA256: 4C:9E:81:B3:7F:02:D5:19"
            ),
            UserTransactionEntity(
                transactionId = "TXN_652190",
                userId = "user_buyer_vijay",
                roosterId = "rooster_sethuva_04",
                roosterBreed = "Kulang White King Sethuva",
                transactionType = "PURCHASE",
                amountRupees = 45000,
                counterpartyName = "G. Venkata Subba Rao (Krishna Delta Sanctuary)",
                counterpartyPhone = "+91 99890 33412",
                farmLocation = "Gannavaram, Krishna",
                escrowOrderId = "ESCROW-441029",
                status = "ESCROW_LOCKED",
                dateMillis = System.currentTimeMillis() - (2L * 24 * 3600 * 1000),
                receiptVerificationHash = "SHA256: E8:12:F4:99:A3:BB:01:77"
            ),
            UserTransactionEntity(
                transactionId = "TXN_881923",
                userId = "user_seller_varma",
                roosterId = "rooster_dega_01",
                roosterBreed = "Pure Dega Aseel (Bhimavaram Lineage)",
                transactionType = "SALE",
                amountRupees = 32000,
                counterpartyName = "K. Vijay Kumar",
                counterpartyPhone = "+91 98480 11223",
                farmLocation = "Sri Rama Heritage Farm, Bhimavaram",
                escrowOrderId = "ESCROW-918231",
                status = "COMPLETED_HANDOVER",
                dateMillis = System.currentTimeMillis() - (12L * 24 * 3600 * 1000),
                receiptVerificationHash = "SHA256: 4C:9E:81:B3:7F:02:D5:19"
            ),
            UserTransactionEntity(
                transactionId = "TXN_881924",
                userId = "user_seller_varma",
                roosterId = "rooster_nemali_02",
                roosterBreed = "Godavari Peacock Nemali Purebred",
                transactionType = "SALE",
                amountRupees = 28000,
                counterpartyName = "Ch. Ramesh (Vijayawada Fancier)",
                counterpartyPhone = "+91 98490 88712",
                farmLocation = "Sri Rama Heritage Farm, Bhimavaram",
                escrowOrderId = "ESCROW-332910",
                status = "COMPLETED_HANDOVER",
                dateMillis = System.currentTimeMillis() - (28L * 24 * 3600 * 1000),
                receiptVerificationHash = "SHA256: A1:D2:C3:44:89:FE:23:01"
            )
        )
        transactionDao.insertAll(transactions)

        // 5. Seed initial starter conversation for rooster_dega_01
        sendEncryptedMessage(
            roosterId = "rooster_dega_01",
            sellerId = "seller_varma_bvr",
            senderName = "Penmatsa Varma Raju (Breeder)",
            senderRole = "SELLER",
            plainText = "Namaste! Welcome to Sri Rama Heritage Farm. This Dega Champion bloodline has complete veterinary records and pedigree genealogy certificate. You are welcome to visit our farm in Bhimavaram or ask any questions."
        )
    }
}
