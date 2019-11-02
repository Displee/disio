package com.displee.io

public abstract class Buffer {

    protected var data: ByteArray
    protected var offset = 0
    private var msb = true
    protected var bitPosition = 0

    constructor(capacity: Int) {
        this.data = ByteArray(capacity)
    }

    constructor(data: ByteArray) {
        this.data = data
    }

    public fun msb() {
        msb = true
    }

    public fun lsb() {
        msb = false
    }

    public fun startBitAccess() {
        bitPosition = offset * BIT_OFFSET
    }

    public fun getBitPosition(i: Int): Int {
        return BIT_OFFSET * i - bitPosition
    }

    public fun finishBitAccess() {
        offset = (bitPosition + 7) / BIT_OFFSET
    }

    public fun jump(offset: Int) {
        this.offset += offset
    }

    public fun isMsb(): Boolean {
        return msb
    }

    public fun isLsb(): Boolean {
        return !msb
    }

    public fun hasRemaining(): Boolean {
        return remaining() > 0
    }

    public fun remaining(): Int {
        return data.size - offset
    }

    public fun array(): ByteArray {
        val array = ByteArray(offset)
        System.arraycopy(data, 0, array, 0, offset)
        return array
    }

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