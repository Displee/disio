package com.displee.io

import java.math.BigInteger

abstract class Buffer {

    protected var data: ByteArray
    var offset = 0

    /**
     * The default I/O mode is MSB (most significant bit).
     */
    private var msb = true

    protected var bitPosition = 0

    constructor(capacity: Int) {
        this.data = ByteArray(capacity)
    }

    constructor(data: ByteArray) {
        this.data = data
    }

    fun get(offset: Int): Byte {
        return data[offset]
    }

    fun get(data: ByteArray, offset: Int, length: Int) {
        System.arraycopy(this.data, offset, data, offset, length + offset)
    }

    fun get(offset: Int, length: Int): ByteArray {
        val data = ByteArray(length)
        get(data, offset, data.size)
        return data
    }

    /**
     * Switch to 'most significant bit' mode.
     */
    fun msb() {
        msb = true
    }

    /**
     * Switch to 'least significant bit' mode.
     */
    fun lsb() {
        msb = false
    }

    fun startBitAccess() {
        check(offset > 0) { "Offset has to be at least 1! "}
        bitPosition = offset * BYTE_SIZE
    }

    fun bitPosition(i: Int): Int {
        return BYTE_SIZE * i - bitPosition
    }

    fun finishBitAccess() {
        offset = (bitPosition + (BYTE_SIZE - 1)) / BYTE_SIZE
        bitPosition = 0
    }

    @JvmOverloads
    fun cryptRSA(exponent: BigInteger, modulus: BigInteger, startOffset: Int = 0, length: Int = offset): ByteArray {
        return cryptRSA(get(startOffset, length + startOffset), exponent, modulus)
    }

    fun hasBitAccess(): Boolean {
        return bitPosition != 0
    }

    fun isMsb(): Boolean {
        return msb
    }

    fun isLsb(): Boolean {
        return !msb
    }

    fun hasRemaining(): Boolean {
        return remaining() > 0
    }

    fun remaining(): Int {
        return data.size - offset
    }

    @JvmOverloads
    fun array(start: Int = 0, length: Int = offset): ByteArray {
        val array = ByteArray(length)
        get(array, start, length)
        return array
    }

    fun raw(): ByteArray {
        return data
    }

    companion object {

        /**
         * The size of a byte in bits
         */
        const val BYTE_SIZE = 8
        val BIT_MASK = IntArray(32) { i ->
            (1 shl i) - 1
        }

        @JvmStatic
        fun cryptRSA(data: ByteArray, exponent: BigInteger, modulus: BigInteger): ByteArray {
            return BigInteger(data).modPow(exponent, modulus).toByteArray()
        }

    }

}