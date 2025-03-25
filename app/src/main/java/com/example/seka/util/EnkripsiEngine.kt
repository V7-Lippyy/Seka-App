package com.example.seka.util

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class EnkripsiEngine @Inject constructor() {

    private val charset = ('A'..'Z').toList() + ('a'..'z').toList() +
            ('0'..'9').toList() + listOf(' ', '.', ',', '!', '?', '-', '_', '@', '#', '$', '%', '&', '*', '(', ')', '+', '=', ';', ':', '<', '>', '/', '\\', '\'', '"')

    fun encrypt(text: String, code: Int): String {
        if (text.isEmpty()) return ""

        val rotorSets = generateRotorSets(code)
        val result = StringBuilder()

        for (i in text.indices) {
            val char = text[i]
            val position = i % 997  // Use prime number for position to reduce predictability

            if (char in charset) {
                val charIndex = charset.indexOf(char)
                val encrypted = encryptChar(charIndex, rotorSets, position, code + i)
                result.append(charset[encrypted % charset.size])
            } else {
                // Keep special characters unchanged
                result.append(char)
            }
        }

        return result.toString()
    }

    fun decrypt(text: String, code: Int): String {
        if (text.isEmpty()) return ""

        val rotorSets = generateRotorSets(code)
        val result = StringBuilder()

        for (i in text.indices) {
            val char = text[i]
            val position = i % 997  // Use same prime number as encryption

            if (char in charset) {
                val charIndex = charset.indexOf(char)
                val decrypted = decryptChar(charIndex, rotorSets, position, code + i)
                result.append(charset[decrypted])
            } else {
                // Keep special characters unchanged
                result.append(char)
            }
        }

        return result.toString()
    }

    private fun generateRotorSets(code: Int): List<List<List<Int>>> {
        val absCode = abs(code).toString().padStart(8, '0')
        val rotorSets = mutableListOf<List<List<Int>>>()

        // Create 4 different rotor sets with different seeds
        for (setIndex in 0 until 4) {
            val baseOffset = setIndex * 2
            val setSeed = absCode.substring(baseOffset, baseOffset + 2).toInt() + code

            val rotors = mutableListOf<List<Int>>()
            for (rotorIndex in 0 until 5) {  // Use 5 rotors per set for complexity
                val rotorSeed = setSeed * (rotorIndex + 1) + code * (rotorIndex + 7)
                rotors.add(generateRotor(rotorSeed))
            }

            rotorSets.add(rotors)
        }

        return rotorSets
    }

    private fun generateRotor(seed: Int): List<Int> {
        val random = java.util.Random(seed.toLong())
        return charset.indices.toList().shuffled(random)
    }

    private fun encryptChar(charIndex: Int, rotorSets: List<List<List<Int>>>, position: Int, uniqueCode: Int): Int {
        var result = charIndex

        // Apply multiple rotor sets with position-dependent selection
        val activeSetIndex = (position + uniqueCode) % rotorSets.size
        val activeSet = rotorSets[activeSetIndex]

        // Apply each rotor in the active set
        for (rotor in activeSet) {
            val shift = (position + uniqueCode) % rotor.size
            result = rotor[(result + shift) % rotor.size]
        }

        return result
    }

    private fun decryptChar(charIndex: Int, rotorSets: List<List<List<Int>>>, position: Int, uniqueCode: Int): Int {
        var result = charIndex

        // Use same selection logic as encryption
        val activeSetIndex = (position + uniqueCode) % rotorSets.size
        val activeSet = rotorSets[activeSetIndex]

        // Apply rotors in reverse order
        for (rotor in activeSet.reversed()) {
            val shift = (position + uniqueCode) % rotor.size

            // Find the original value by reverse lookup
            var originalIndex = -1
            for (i in rotor.indices) {
                if (rotor[(i + shift) % rotor.size] == result) {
                    originalIndex = i
                    break
                }
            }

            if (originalIndex != -1) {
                result = originalIndex
            }
        }

        return result % charset.size
    }
}