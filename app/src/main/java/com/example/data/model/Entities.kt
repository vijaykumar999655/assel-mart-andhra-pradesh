package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roosters")
data class RoosterEntity(
    @PrimaryKey val id: String,
    val breedName: String,
    val breedType: String, // Dega, Nemali, Kaki, Sethuva, Pingala, Rasangi, Maiyle, Teha
    val lineage: String, // Gaja (Heavy Bone), Reza (Medium Stance), Kulang (Tall)
    val ageMonths: Int,
    val weightKg: Double,
    val heightInches: Double,
    val plumageColor: String,
    val spursCondition: String,
    val vaccinationStatus: String,
    val ringTagId: String,
    val priceRupees: Long,
    val sellerId: String,
    val sellerName: String,
    val sellerPhone: String,
    val sellerVerified: Boolean,
    val farmName: String,
    val farmDistrict: String,
    val farmMandal: String,
    val farmAddress: String,
    val farmLatitude: Double,
    val farmLongitude: Double,
    val description: String,
    val imageResName: String,
    val badgeLevel: String, // "Elite Breeder", "Verified Farm", "Govt Reg Breeder"
    val rating: Float,
    val isFeatured: Boolean = false,
    val isSold: Boolean = false,
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "sellers")
data class SellerProfileEntity(
    @PrimaryKey val sellerId: String,
    val name: String,
    val farmName: String,
    val district: String,
    val mandal: String,
    val villageAddress: String,
    val contactPhone: String,
    val whatsappNumber: String,
    val latitude: Double,
    val longitude: Double,
    val isKycVerified: Boolean,
    val verificationBadge: String,
    val rating: Float,
    val reviewCount: Int,
    val experienceYears: Int,
    val activeListingsCount: Int,
    val aboutFarm: String,
    val openVisitHours: String,
    val vetAffiliation: String
)

@Entity(tableName = "messages")
data class EncryptedMessageEntity(
    @PrimaryKey val messageId: String,
    val roosterId: String,
    val sellerId: String,
    val senderName: String,
    val senderRole: String, // "BUYER" or "SELLER"
    val cipherText: String, // AES-256-GCM Base64 simulation
    val plainText: String,
    val keyFingerprint: String,
    val timestampMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "escrow_orders")
data class EscrowOrderEntity(
    @PrimaryKey val orderId: String,
    val roosterId: String,
    val roosterBreed: String,
    val amountRupees: Long,
    val buyerName: String,
    val sellerName: String,
    val farmLocation: String,
    val status: String, // "ESCROW_LOCKED", "FARM_INSPECTION_PENDING", "VET_HEALTH_VERIFIED", "PAYOUT_RELEASED"
    val paymentMethod: String,
    val verificationOtp: String,
    val timestampMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey val userId: String,
    val fullName: String,
    val phone: String,
    val email: String,
    val passwordHash: String,
    val userRole: String, // "BUYER", "SELLER", "BOTH"
    val farmName: String? = null,
    val district: String,
    val mandal: String,
    val address: String,
    val isPhoneVerified: Boolean = true,
    val isIdVerified: Boolean = false,
    val idDocumentType: String = "AADHAAR", // "AADHAAR", "VOTER_ID", "KISAN_CREDIT_CARD"
    val idDocumentMaskedNumber: String = "", // "XXXX-XXXX-4819"
    val idVerificationDate: Long? = null,
    val farmInspectionStatus: String = "NOT_APPLICABLE", // "NOT_APPLICABLE", "PENDING", "INSPECTED_PASSED", "RE_INSPECTION_REQUIRED"
    val farmInspectorName: String = "",
    val farmInspectorNotes: String = "",
    val farmInspectionDate: Long? = null,
    val veterinaryCertificateId: String = "",
    val biosecurityTier: String = "Standard Tier 2",
    val privacyConsentAccepted: Boolean = true,
    val dataSharingConsent: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_transactions")
data class UserTransactionEntity(
    @PrimaryKey val transactionId: String,
    val userId: String,
    val roosterId: String,
    val roosterBreed: String,
    val roosterImage: String = "rooster_dega_nemali",
    val transactionType: String, // "PURCHASE" or "SALE"
    val amountRupees: Long,
    val counterpartyName: String,
    val counterpartyPhone: String,
    val farmLocation: String,
    val escrowOrderId: String,
    val status: String, // "ESCROW_LOCKED", "FARM_INSPECTION_PASSED", "COMPLETED_HANDOVER", "CANCELLED"
    val dateMillis: Long = System.currentTimeMillis(),
    val receiptVerificationHash: String = ""
)
