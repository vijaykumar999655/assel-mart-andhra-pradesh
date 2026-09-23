package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.MessageDigest

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testSha256PasswordHashing() {
        val password = "SecurePassword2026!"
        val bytes1 = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        val hash1 = bytes1.joinToString("") { "%02x".format(it) }

        val bytes2 = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        val hash2 = bytes2.joinToString("") { "%02x".format(it) }

        assertEquals(hash1, hash2)
        assertEquals(64, hash1.length) // SHA-256 is 64 hex characters

        val wrongHash = MessageDigest.getInstance("SHA-256").digest("WrongPassword".toByteArray())
            .joinToString("") { "%02x".format(it) }
        assertNotEquals(hash1, wrongHash)
    }

    @Test
    fun testMaskedAadhaarFormatting() {
        val aadhaar = "5829 4410 8921"
        val last4 = aadhaar.takeLast(4)
        val masked = "XXXX-XXXX-$last4"
        assertEquals("XXXX-XXXX-8921", masked)
    }
}
