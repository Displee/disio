package com.displee.io.impl

import com.displee.io.Buffer

open class InputBuffer(data: ByteArray) : Buffer(data) {

    fun read(): Byte {
        check(offset < data.size) { "No data left to read." }
        return data[offset++]
    }

    fun readUnsigned(): Int {
        return read().toInt() and 0xFF
    }

    fun read(length: Int): ByteArray {
        val data = ByteArray(length)
        read(data, 0, data.size)
        return data
    }

    fun read(data: ByteArray) {
        read(data, 0, data.size)
    }

    fun read(data: ByteArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = read()
        }
    }

    fun readUnsigned(length: Int): IntArray {
        val data = IntArray(length)
        readUnsigned(data, 0, data.size)
        return data
    }

    fun readUnsigned(data: IntArray) {
        readUnsigned(data, 0, data.size)
    }

    fun readUnsigned(data: IntArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = readUnsigned()
        }
    }

    fun readShort(length: Int): ShortArray {
        val data = ShortArray(length)
        readShort(data, 0, data.size)
        return data
    }

    fun readShort(data: ShortArray) {
        readShort(data, 0, data.size)
    }

    fun readShort(data: ShortArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = readShort()
        }
    }

    fun readUnsignedShort(length: Int): IntArray {
        val data = IntArray(length)
        readUnsignedShort(data, 0, data.size)
        return data
    }

    fun readUnsignedShort(data: IntArray) {
        readUnsignedShort(data, 0, data.size)
    }

    fun readUnsignedShort(data: IntArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = readUnsignedShort()
        }
    }

    fun readInt(length: Int): IntArray {
        val data = IntArray(length)
        readInt(data, 0, data.size)
        return data
    }

    fun readInt(data: IntArray) {
        readInt(data, 0, data.size)
    }

    fun readInt(data: IntArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = readInt()
        }
    }

    fun readUnsignedInt(length: Int): LongArray {
        val data = LongArray(length)
        readUnsignedInt(data, 0, data.size)
        return data
    }

    fun readUnsignedInt(data: LongArray) {
        readUnsignedInt(data, 0, data.size)
    }

    fun readUnsignedInt(data: LongArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = readUnsignedInt()
        }
    }

    fun readLong(length: Int): LongArray {
        val data = LongArray(length)
        readLong(data, 0, data.size)
        return data
    }

    fun readLong(data: LongArray) {
        readLong(data, 0, data.size)
    }

    fun readLong(data: LongArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = readLong()
        }
    }

    fun readBoolean(): Boolean {
        return read() == 1.toByte()
    }

    fun readShort(): Short {
        var s = if (isLsb()) readUnsigned() + (readUnsigned() shl 8) else (readUnsigned() shl 8) + readUnsigned()
        if (s > Short.MAX_VALUE) {
            s -= 65536
        }
        return s.toShort()
    }

    fun readUnsignedShort(): Int {
        return readShort().toInt() and 0xFFFF
    }

    private fun readIntLsb(): Int {
        return readUnsigned() + (readUnsigned() shl 8) + (readUnsigned() shl 16) + (readUnsigned() shl 24)
    }

    private fun readIntMsb(): Int {
        return (readUnsigned() shl 24) + (readUnsigned() shl 16) + (readUnsigned() shl 8) + readUnsigned()
    }

    fun readInt(): Int {
        return if (isLsb()) readIntLsb() else readIntMsb()
    }

    fun readIntReversed(): Int {
        return if (isLsb()) readIntMsb() else readIntLsb()
    }

    fun readUnsignedInt(): Long {
        return readInt().toLong() and 0xFFFFFFFFL
    }

    fun readLong(): Long {
        val v = readUnsignedInt()
        val k = readUnsignedInt()
        return (v shl 32) + k
    }

    fun read24BitInt(): Int {
        return if (isLsb())
            readUnsigned() + (readUnsigned() shl 8) + (readUnsigned() shl 16)
        else
            (readUnsigned() shl 16) + (readUnsigned() shl 8) + readUnsigned()
    }

    fun readSmart(): Int {
        val i = data[offset].toInt() and 0xFF
        if (i < 128) {
            return readUnsigned() - 64
        }
        return readUnsignedShort() - 49152
    }

    fun readUnsignedSmart(): Int {
        val i = data[offset].toInt() and 0xFF
        if (i < 128) {
            return readUnsigned()
        }
        return readUnsignedShort() - 32768
    }

    fun readSmart2(): Int {
        var i = 0
        var v = readUnsignedSmart()
        while (v == Short.MAX_VALUE.toInt()) {
            v = readUnsignedSmart()
            i += Short.MAX_VALUE.toInt()
        }
        i += v
        return i
    }

    fun readBigSmart(): Int {
        if (data[offset] < 0) {
            return readInt() and 0x7FFFFFFF
        }
        val value = readUnsignedShort()
        if (value == Short.MAX_VALUE.toInt()) {
            return -1
        }
        return value
    }

    fun readString(): String {
        val currentOffset = offset
        while (read().toInt() != 0) {
            /* empty */
        }
        val length = offset - currentOffset - 1
        if (length == 0) {
            return ""
        }
        return replaceSpecialCharacters(data, currentOffset, length)
    }

    fun readStringRaw(): String {
        val start = offset
        while (data[offset++].toInt() != 10) {
            /*
             * Empty
             */
        }
        return String(data, start, offset - start - 1)
    }

    fun readIntAsFloat(): Float {
        return java.lang.Float.intBitsToFloat(readInt())
    }

    fun readIntAsFloatReversed(): Float {
        return java.lang.Float.intBitsToFloat(readIntReversed())
    }

    fun readBit(position: Int): Int {
        check(hasBitAccess()) { "No bit access." }
        var i = position
        var byteOffset = bitPosition shr 3
        var bitMaskIndex = BYTE_SIZE - (bitPosition and (BYTE_SIZE - 1))
        var value = 0
        bitPosition += i
        while (i > bitMaskIndex) {
            value += (data[byteOffset++].toInt() and BIT_MASK[bitMaskIndex]) shl i - bitMaskIndex
            i -= bitMaskIndex
            bitMaskIndex = BYTE_SIZE
        }
        value += if (i == bitMaskIndex) {
            data[byteOffset].toInt() and BIT_MASK[bitMaskIndex]
        } else {
            data[byteOffset].toInt() shr bitMaskIndex - i and BIT_MASK[i]
        }
        return value
    }

    fun decryptXTEA(keys: IntArray, start: Int, end: Int) {
        val l = offset
        offset = start
        val i1 = (end - start) / 8
        for (j1 in 0 until i1) {
            var k1: Int = readInt()
            var l1: Int = readInt()
            var sum = -0x3910c8e0
            val delta = -0x61c88647
            var k2 = 32
            while (k2-- > 0) {
                l1 -= keys[sum and 0x1c84 ushr 11] + sum xor (k1 ushr 5 xor k1 shl 4) + k1
                sum -= delta
                k1 -= (l1 ushr 5 xor l1 shl 4) + l1 xor keys[sum and 3] + sum
            }
            val outputBuffer = toOutputBuffer()
            outputBuffer.jump(-8)
            outputBuffer.writeInt(k1).writeInt(l1)
            data = outputBuffer.raw()
        }
        offset = l
    }

    @JvmOverloads
    fun toOutputBuffer(copyOffset: Boolean = true): OutputBuffer {
        val outputBuffer = OutputBuffer(0)
        outputBuffer.write(data.clone())
        if (copyOffset) {
            outputBuffer.offset = offset
        }
        return outputBuffer
    }

    companion object {
        private val SPECIAL_CHARACTERS = charArrayOf(
            '€',
            '\u0000',
            '‚',
            'ƒ',
            '„',
            '…',
            '†',
            '‡',
            'ˆ',
            '‰',
            'Š',
            '‹',
            'Œ',
            '\u0000',
            'Ž',
            '\u0000',
            '\u0000',
            '‘',
            '’',
            '“',
            '”',
            '•',
            '–',
            '—',
            '˜',
            '™',
            'š',
            '›',
            'œ',
            '\u0000',
            'ž',
            'Ÿ'
        )

        private fun replaceSpecialCharacters(data: ByteArray, startOffset: Int, length: Int): String {
            val chars = CharArray(length)
            var offset = 0
            for (i in 0 until length) {
                var value = data[startOffset + i].toInt() and 0xFF
                if (value == 0) {
                    continue
                }
                if (value in 128..159) {
                    var specialCharacter = SPECIAL_CHARACTERS[value - 128].toInt()
                    if (specialCharacter == 0) {
                        specialCharacter = 63
                    }
                    value = specialCharacter
                }
                chars[offset++] = value.toChar()
            }
            return String(chars, 0, offset)
        }
    }

}