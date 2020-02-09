package com.displee.io

import com.displee.io.impl.InputBuffer
import com.displee.io.impl.OutputBuffer

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

    /**
     * Start bit access.
     */
    fun startBitAccess() {
        check(offset > 0) { "Offset has to be at least 1! "}
        bitPosition = offset * BYTE_SIZE
    }

    fun getBitPosition(i: Int): Int {
        return BYTE_SIZE * i - bitPosition
    }

    /**
     * Finish the bit access.
     */
    fun finishBitAccess() {
        offset = (bitPosition + (BYTE_SIZE - 1)) / BYTE_SIZE
        bitPosition = 0
    }

    /**
     * Check if we've bit access.
     */
    fun hasBitAccess(): Boolean {
        return bitPosition != 0
    }

    /**
     * Increase the offset with the specified {@code offset}.
     */
    fun jump(offset: Int) {
        this.offset += offset
    }

    /**
     * Check if we're in MSB mode.
     */
    fun isMsb(): Boolean {
        return msb
    }

    /**
     * Check if we're in LSB mode.
     */
    fun isLsb(): Boolean {
        return !msb
    }

    /**
     * Check if there are any bytes remaining to be read or write.
     */
    fun hasRemaining(): Boolean {
        return remaining() > 0
    }

    /**
     * Get the remaining bytes.
     * @return Int The remaining bytes.
     */
    fun remaining(): Int {
        return data.size - offset
    }

    /**
     * Get the data until the {@code offset}.
     * @return ByteArray The data.
     */
    fun array(): ByteArray {
        val array = ByteArray(offset)
        System.arraycopy(data, 0, array, 0, offset)
        return array
    }

    /**
     * Get the raw data that's being used to read from or write to.
     * @return ByteArray {@code data}
     */
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

    }

}