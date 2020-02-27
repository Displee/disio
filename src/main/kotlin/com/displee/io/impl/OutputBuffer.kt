package com.displee.io.impl

import com.displee.io.Buffer

open class OutputBuffer(capacity: Int) : Buffer(capacity) {

    private fun ensureCapacity(size: Int) {
        val newOffset = offset + size
        if (newOffset <= data.size) {
            return
        }
        val newData = ByteArray(newOffset)
        get(newData, 0, data.size)
        data = newData
    }

    fun writeByte(value: Byte): OutputBuffer {
        ensureCapacity(1)
        data[offset++] = value
        return this
    }

    fun writeByte(value: Int): OutputBuffer {
        return writeByte(value.toByte())
    }

    fun writeBytes(bytes: ByteArray): OutputBuffer {
        return writeBytes(bytes, 0, bytes.size)
    }

    fun writeBytes(bytes: ByteArray, offset: Int, size: Int): OutputBuffer {
        ensureCapacity(size - offset)
        System.arraycopy(bytes, offset, this.data, this.offset, size)
        this.offset += size - offset
        return this
    }

    fun writeShorts(values: ShortArray): OutputBuffer {
        return writeShorts(values, 0, values.size)
    }

    fun writeShorts(values: IntArray): OutputBuffer {
        return writeShorts(values, 0, values.size)
    }

    fun writeShorts(values: ShortArray, offset: Int, size: Int): OutputBuffer {
        ensureCapacity(size * 2)
        for (i in offset until size) {
            writeShort(values[i])
        }
        return this
    }

    fun writeShorts(values: IntArray, offset: Int, size: Int): OutputBuffer {
        ensureCapacity(size * 2)
        for (i in offset until size) {
            writeShort(values[i])
        }
        return this
    }

    fun writeInts(values: IntArray): OutputBuffer {
        return writeInts(values, 0, values.size)
    }

    fun writeInts(values: IntArray, offset: Int, size: Int): OutputBuffer {
        ensureCapacity(size * 4)
        for (i in offset until size) {
            writeInt(values[i])
        }
        return this
    }

    fun writeLongs(values: LongArray): OutputBuffer {
        return writeLongs(values, 0, values.size)
    }

    fun writeLongs(values: LongArray, offset: Int, size: Int): OutputBuffer {
        ensureCapacity(size * 8)
        for (i in offset until size) {
            writeLong(values[i])
        }
        return this
    }

    fun writeBoolean(value: Boolean): OutputBuffer {
        return writeByte(if (value) 1 else 0)
    }

    fun writeNegativeByte(value: Int): OutputBuffer {
        return writeNegativeByte(value.toByte())
    }

    fun writeNegativeByte(value: Byte): OutputBuffer {
        return writeByte(-value)
    }

    fun writeByte128(value: Byte): OutputBuffer {
        return writeByte128(value.toInt())
    }

    fun writeByte128(value: Int): OutputBuffer {
        return writeByte(128 + value)
    }

    fun writeNegativeByte128(value: Byte): OutputBuffer {
        return writeNegativeByte128(value.toInt())
    }

    fun writeNegativeByte128(value: Int): OutputBuffer {
        return writeByte(128 - value)
    }

    fun writeShort(value: Short): OutputBuffer {
        return writeShort(value.toInt())
    }

    private fun writeShortMsb(value: Int): OutputBuffer {
        return writeByte(value shr 8).writeByte(value)
    }

    private fun writeShortLsb(value: Int): OutputBuffer {
        return writeByte(value).writeByte(value shr 8)
    }

    fun writeShort(value: Int): OutputBuffer {
        ensureCapacity(2)
        return if (isMsb()) writeShortMsb(value) else writeShortLsb(value)
    }

    fun writeShortLE(value: Int): OutputBuffer {
        ensureCapacity(2)
        return if (isMsb()) writeShortLsb(value) else writeShortMsb(value)
    }

    private fun writeShort128Msb(value: Int): OutputBuffer {
        return writeByte(value shr 8)
            .writeByte(value + 128)
    }

    private fun writeShort128Lsb(value: Int): OutputBuffer {
        return writeByte(value + 128)
            .writeByte(value shr 8)
    }

    fun writeShort128(value: Int): OutputBuffer {
        ensureCapacity(2)
        return if (isMsb()) writeShort128Msb(value) else writeShort128Lsb(value)
    }

    fun writeShortLE128(value: Int): OutputBuffer {
        ensureCapacity(2)
        return if (isMsb()) writeShort128Lsb(value) else writeShort128Msb(value)
    }

    private fun write24BitIntMsb(value: Int): OutputBuffer {
        return writeByte(value shr 16)
            .writeByte(value shr 8)
            .writeByte(value)
    }

    private fun write24BitIntLsb(value: Int): OutputBuffer {
        return writeByte(value)
            .writeByte(value shr 8)
            .writeByte(value shr 16)
    }

    fun write24BitInt(value: Int): OutputBuffer {
        ensureCapacity(3)
        return if (isMsb()) write24BitIntMsb(value) else write24BitIntLsb(value)
    }

    private fun writeIntMsb(value: Int): OutputBuffer {
        return writeByte(value shr 24)
            .writeByte(value shr 16)
            .writeByte(value shr 8)
            .writeByte(value)
    }

    private fun writeIntLsb(value: Int): OutputBuffer {
        return writeByte(value)
            .writeByte(value shr 8)
            .writeByte(value shr 16)
            .writeByte(value shr 24)
    }

    fun writeInt(value: Int): OutputBuffer {
        ensureCapacity(4)
        return if (isMsb()) writeIntMsb(value) else writeIntLsb(value)
    }

    fun writeIntLE(value: Int): OutputBuffer {
        ensureCapacity(4)
        return if (isMsb()) writeIntLsb(value) else writeIntMsb(value)
    }

    fun write40BitInt(value: Int): OutputBuffer {
        return writeByte(value shr 32).writeInt(value)
    }

    fun writeLong(value: Long): OutputBuffer {
        ensureCapacity(8)
        return if (isMsb()) writeLongMsb(value) else writeLongLsb(value)
    }

    private fun writeLongMsb(value: Long): OutputBuffer {
        return writeByte((value shr 56).toInt())
            .writeByte((value shr 48).toInt())
            .writeByte((value shr 40).toInt())
            .writeByte((value shr 32).toInt())
            .writeByte((value shr 24).toInt())
            .writeByte((value shr 16).toInt())
            .writeByte((value shr 8).toInt())
            .writeByte(value.toInt())
    }

    private fun writeLongLsb(value: Long): OutputBuffer {
        return writeByte(value.toInt())
            .writeByte((value shr 8).toInt())
            .writeByte((value shr 16).toInt())
            .writeByte((value shr 24).toInt())
            .writeByte((value shr 32).toInt())
            .writeByte((value shr 40).toInt())
            .writeByte((value shr 48).toInt())
            .writeByte((value shr 56).toInt())
    }

    fun writeLongLE(value: Long): OutputBuffer {
        return if (isMsb()) writeLongLsb(value) else writeLongMsb(value)
    }

    fun writeSmart(value: Int): OutputBuffer {
        if (value < 64 && value >= -64) {
            return writeByte(value + 64)
        }
        return writeShort((value + 49152).toShort())
    }

    fun writeUnsignedSmart(value: Int): OutputBuffer {
        if (value < 128) {
            return writeByte(value.toByte())
        }
        return writeShort((value + 32768).toShort())
    }

    fun writeSmart2(i: Int): OutputBuffer {
        var value = i
        while (value >= Short.MAX_VALUE) {
            writeUnsignedSmart(Short.MAX_VALUE.toInt())
            value -= Short.MAX_VALUE.toInt()
        }
        return writeUnsignedSmart(value)
    }

    fun writeBigSmart(value: Int): OutputBuffer {
        if (value >= Short.MAX_VALUE) {
            return writeInt(value - Integer.MAX_VALUE - 1)
        }
        return writeShort(if (value >= 0) value.toShort() else Short.MAX_VALUE)
    }

    fun writeString(value: String): OutputBuffer {
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
        writeByte(0)
        return this
    }

    fun writeStringRaw(string: String): OutputBuffer {
        return writeBytes(string.toByteArray()).writeByte(10)
    }

    fun writeFloat(float: Float): OutputBuffer {
        return writeInt(java.lang.Float.floatToRawIntBits(float))
    }

    fun writeFloatLE(float: Float): OutputBuffer {
        return writeIntLE(java.lang.Float.floatToRawIntBits(float))
    }

    fun writeBit(bit: Int, value: Int): OutputBuffer {
        check(hasBitAccess()) { "No bit access." }
        var numBits = bit
        var bytePos = bitPosition shr 3
        var bitMaskIndex = BYTE_SIZE - (bitPosition and (BYTE_SIZE - 1))
        bitPosition += numBits
        while (numBits > bitMaskIndex) {
            ensureCapacity(bytePos)
            data[bytePos] = (data[bytePos].toInt() and BIT_MASK[bitMaskIndex].inv()).toByte()
            data[bytePos] =
                (data[bytePos++].toInt() or (value shr numBits - bitMaskIndex and BIT_MASK[bitMaskIndex])).toByte()
            numBits -= bitMaskIndex
            bitMaskIndex = BYTE_SIZE
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

    fun encryptXTEA(keys: IntArray, start: Int, end: Int) {
        val o = offset
        val j = (end - start) / 8
        offset = start
        for (k in 0 until j) {
            val inputBuffer = toInputBuffer()
            var l: Int = inputBuffer.readInt()
            var i1: Int = inputBuffer.readInt()
            offset = inputBuffer.offset
            var sum = 0
            val delta = -0x61c88647
            var l1 = 32
            while (l1-- > 0) {
                l += sum + keys[3 and sum] xor i1 + ((i1 ushr 5) xor (i1 shl 4))
                sum += delta
                i1 += l + ((l ushr 5) xor (l shl 4)) xor keys[0x1eec and sum ushr 11] + sum
            }
            offset -= 8
            writeInt(l)
            writeInt(i1)
        }
        offset = o
    }

    @JvmOverloads
    fun toInputBuffer(copyOffset: Boolean = true): InputBuffer {
        val inputBuffer = InputBuffer(data.clone())
        if (copyOffset) {
            inputBuffer.offset = offset
        }
        return inputBuffer
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