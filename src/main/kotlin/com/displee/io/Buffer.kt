package com.displee.io

public abstract class Buffer {

    protected var data: ByteArray
    protected var offset = 0

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
     * Switch to 'most significant bit' mode. This is also the default mode.
     */
    public fun msb() {
        msb = true
    }

    /**
     * Switch to 'least significant bit' mode.
     */
    public fun lsb() {
        msb = false
    }

    /**
     * Start bit access.
     */
    public fun startBitAccess() {
        if (offset == 0) {
            throw IllegalAccessException("Offset has to be at least 1!")
        }
        bitPosition = offset * BIT_OFFSET
    }

    public fun getBitPosition(i: Int): Int {
        return BIT_OFFSET * i - bitPosition
    }

    /**
     * Finish the bit access.
     */
    public fun finishBitAccess() {
        offset = (bitPosition + 7) / BIT_OFFSET
        bitPosition = 0
    }

    /**
     * Check if we've bit access.
     */
    public fun hasBitAccess(): Boolean {
        return bitPosition != 0
    }

    /**
     * Increase the offset with the specified {@code offset}.
     */
    public fun jump(offset: Int) {
        this.offset += offset
    }

    /**
     * Check if we're in MSB mode.
     */
    public fun isMsb(): Boolean {
        return msb
    }

    /**
     * Check if we're in LSB mode.
     */
    public fun isLsb(): Boolean {
        return !msb
    }

    /**
     * Check if there are any bytes remaining to be read or write.
     */
    public fun hasRemaining(): Boolean {
        return remaining() > 0
    }

    /**
     * Get the remaining bytes.
     */
    public fun remaining(): Int {
        return data.size - offset
    }

    /**
     * Get the data until the {@code offset}.
     */
    public fun array(): ByteArray {
        val array = ByteArray(offset)
        System.arraycopy(data, 0, array, 0, offset)
        return array
    }

    /**
     * Get the raw data that's being used to read from or write to.
     */
    public fun rawArray(): ByteArray {
        return data
    }

    companion object {
        private const val BIT_OFFSET = 8
        val BIT_MASK = IntArray(32) { i ->
            (1 shl i) - 1
        }
    }

}