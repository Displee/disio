package com.displee.io.impl

import com.displee.io.Buffer

public open class OutputBuffer(capacity: Int) : Buffer(capacity) {

    public fun write(value: Int): OutputBuffer {
        return write(value.toByte())
    }

    public fun write(value: Byte): OutputBuffer {
        ensureCapacity(1)
        data[offset++] = value
        return this
    }

    public fun write(bytes: ByteArray): OutputBuffer {
        return write(bytes, 0, bytes.size)
    }

    public fun write(bytes: ByteArray, offset: Int, size: Int): OutputBuffer {
        ensureCapacity(size)
        for (i in offset until size) {
            write(bytes[i])
        }
        return this
    }

    public fun write(values: ShortArray): OutputBuffer {
        return write(values, 0, values.size)
    }

    public fun write(values: ShortArray, offset: Int, size: Int): OutputBuffer {
        ensureCapacity(size * 2)
        for (i in offset until size) {
            writeShort(values[i])
        }
        return this
    }

    public fun write(values: IntArray): OutputBuffer {
        return write(values, 0, values.size)
    }

    public fun write(values: IntArray, offset: Int, size: Int): OutputBuffer {
        ensureCapacity(size * 4)
        for (i in offset until size) {
            writeInt(values[i])
        }
        return this
    }

    public fun write(values: LongArray): OutputBuffer {
        return write(values, 0, values.size)
    }

    public fun write(values: LongArray, offset: Int, size: Int): OutputBuffer {
        ensureCapacity(size * 8)
        for (i in offset until size) {
            writeLong(values[i])
        }
        return this
    }

    public fun writeBoolean(value: Boolean): OutputBuffer {
        return write(if (value) 1 else 0)
    }

    public fun writeShort(value: Short): OutputBuffer {
        ensureCapacity(2)
        return if (isMsb()) writeShortMsb(value) else writeShortLsb(value)
    }

    private fun writeShortMsb(value: Short): OutputBuffer {
        return write(value.toInt() shr 8)
            .write(value.toInt())
    }

    private fun writeShortLsb(value: Short): OutputBuffer {
        return write(value.toInt())
            .write(value.toInt() shr 8)
    }

    public fun writeInt(value: Int): OutputBuffer {
        ensureCapacity(4)
        return if (isMsb()) writeIntMsb(value) else writeIntLsb(value)
    }

    private fun writeIntMsb(value: Int): OutputBuffer {
        return write(value shr 24)
            .write(value shr 16)
            .write(value shr 8)
            .write(value)
    }

    private fun writeIntLsb(value: Int): OutputBuffer {
        return write(value)
            .write(value shr 8)
            .write(value shr 16)
            .write(value shr 24)
    }

    public fun writeLong(value: Long): OutputBuffer {
        ensureCapacity(8)
        return if (isMsb()) writeLongMsb(value) else writeLongLsb(value)
    }

    private fun writeLongMsb(value: Long): OutputBuffer {
        return write((value shr 56).toInt())
            .write((value shr 48).toInt())
            .write((value shr 40).toInt())
            .write((value shr 32).toInt())
            .write((value shr 24).toInt())
            .write((value shr 16).toInt())
            .write((value shr 8).toInt())
            .write(value.toInt())
    }

    private fun writeLongLsb(value: Long): OutputBuffer {
        return write(value.toInt())
            .write((value shr 8).toInt())
            .write((value shr 16).toInt())
            .write((value shr 24).toInt())
            .write((value shr 32).toInt())
            .write((value shr 40).toInt())
            .write((value shr 48).toInt())
            .write((value shr 56).toInt())
    }

    public fun write24BitInt(value: Int): OutputBuffer {
        ensureCapacity(3)
        return if (isMsb()) write24BitIntMsb(value) else write24BitIntLsb(value)
    }

    private fun write24BitIntMsb(value: Int): OutputBuffer {
        return write(value shr 16)
            .write(value shr 8)
            .write(value)
    }

    private fun write24BitIntLsb(value: Int): OutputBuffer {
        return write(value)
            .write(value shr 8)
            .write(value shr 16)
    }

    public fun writeSmart(value: Int): OutputBuffer {
        if (value < 64 && value >= -64) {
            return write(value + 64)
        }
        return writeShort((value + 49152).toShort())
    }

    public fun writeUnsignedSmart(value: Int): OutputBuffer {
        if (value < 128) {
            return write(value.toByte())
        }
        return writeShort((value + 32768).toShort())
    }

    public fun writeSmart2(i: Int): OutputBuffer {
        var value = i
        while (value >= Short.MAX_VALUE) {
            writeUnsignedSmart(Short.MAX_VALUE.toInt())
            value -= Short.MAX_VALUE.toInt()
        }
        return writeUnsignedSmart(value)
    }

    public fun writeBigSmart(value: Int): OutputBuffer {
        if (value >= Short.MAX_VALUE) {
            return writeInt(value - Integer.MAX_VALUE - 1)
        }
        return writeShort(if (value >= 0) value.toShort() else Short.MAX_VALUE)
    }

    public fun writeString(value: String): OutputBuffer {
        val length = value.length
        ensureCapacity(length + 1)
        for (i in 0 until length) {
            val char = value[i]
            if (char.toInt() > 0 && char < '\u0080' || char in '\u00a0'..'\u00ff') {
                data[offset++] = char.toByte()
            } else {
                data[offset++] = (SPECIAL_CHARS_MAPPED[char] ?: 63).toByte()
            }
        }
        write(0)
        return this
    }

    fun writeStringRaw(string: String): OutputBuffer {
        return write(string.toByteArray()).write(10)
    }

    public fun writeBit(bit: Int, value: Int): OutputBuffer {
        check(!hasBitAccess()) { "No bit access." }
        var numBits = bit
        var bytePos = bitPosition shr 3
        var bitMaskIndex = 8 - (bitPosition and 7)
        bitPosition += numBits
        while (numBits > bitMaskIndex) {
            ensureCapacity(bytePos)
            data[bytePos] = (data[bytePos].toInt() and BIT_MASK[bitMaskIndex].inv()).toByte()
            data[bytePos++] = (data[bytePos++].toInt() or (value shr numBits - bitMaskIndex and BIT_MASK[bitMaskIndex])).toByte()
            numBits -= bitMaskIndex
            bitMaskIndex = 8
        }
        ensureCapacity(bytePos)
        if (numBits == bitMaskIndex) {
            data[bytePos] = (data[bytePos].toInt() and BIT_MASK[bitMaskIndex].inv()).toByte()
            data[bytePos] = (data[bytePos].toInt() or (value and BIT_MASK[bitMaskIndex])).toByte()
        } else {
            data[bytePos] = (data[bytePos].toInt() and (BIT_MASK[numBits] shl bitMaskIndex - numBits).inv()).toByte()
            data[bytePos] = (data[bytePos].toInt() or (value and BIT_MASK[numBits] shl bitMaskIndex - numBits)).toByte()
        }
        return this
    }

    private fun ensureCapacity(size: Int) {
        val newOffset = offset + size;
        if (newOffset < data.size) {
            return
        }
        val newData = ByteArray(newOffset)
        System.arraycopy(data, 0, newData, 0, data.size)
        data = newData
    }

    companion object {
        private val SPECIAL_CHARS_MAPPED = hashMapOf(
            '\u20ac' to -128,
            '\u201a' to -126,
            '\u0192' to -125,
            '\u201e' to -124,
            '\u2026' to -123,
            '\u2020' to -122,
            '\u2021' to -121,
            '\u02c6' to -120,
            '\u2030' to -119,
            '\u0160' to -118,
            '\u2039' to -117,
            '\u0152' to -116,
            '\u017d' to -114,
            '\u2018' to -111,
            '\u2019' to -110,
            '\u201c' to -109,
            '\u201d' to -108,
            '\u2022' to -107,
            '\u2013' to -106,
            '\u2014' to -105,
            '\u02dc' to -104,
            '\u2122' to -103,
            '\u0161' to -102,
            '\u203a' to -101,
            '\u0153' to -100,
            '\u017e' to -98,
            '\u0178' to -97
        )
    }

}