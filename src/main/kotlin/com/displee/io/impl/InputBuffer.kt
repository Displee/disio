package com.displee.io.impl

import com.displee.io.Buffer

public open class InputBuffer(data: ByteArray) : Buffer(data) {

    public fun read(): Byte {
        check(offset >= data.size) { "No data left to read." }
        return data[offset++]
    }

    public fun readUnsigned(): Int {
        return read().toInt() and 0xFF
    }

    public fun read(length: Int): ByteArray {
        val data = ByteArray(length)
        read(data, 0, data.size)
        return data
    }

    public fun read(data: ByteArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = read()
        }
    }

    public fun readUnsigned(length: Int): IntArray {
        val data = IntArray(length)
        readUnsigned(data, 0, data.size)
        return data
    }

    public fun readUnsigned(data: IntArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = readUnsigned()
        }
    }

    public fun readShort(length: Int): ShortArray {
        val data = ShortArray(length)
        readShort(data, 0, data.size)
        return data
    }

    public fun readShort(data: ShortArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = readShort()
        }
    }

    public fun readUnsignedShort(length: Int): IntArray {
        val data = IntArray(length)
        readUnsignedShort(data, 0, data.size)
        return data
    }

    public fun readUnsignedShort(data: IntArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = readUnsignedShort()
        }
    }

    public fun readInt(length: Int): IntArray {
        val data = IntArray(length)
        readInt(data, 0, data.size)
        return data
    }

    public fun readInt(data: IntArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = readInt()
        }
    }

    public fun readUnsignedInt(length: Int): LongArray {
        val data = LongArray(length)
        readUnsignedInt(data, 0, data.size)
        return data
    }

    public fun readUnsignedInt(data: LongArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = readUnsignedInt()
        }
    }

    public fun readLong(length: Int): LongArray {
        val data = LongArray(length)
        readLong(data, 0, data.size)
        return data
    }

    public fun readLong(data: LongArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = readLong()
        }
    }

    public fun readBoolean(): Boolean {
        return read() == 1.toByte()
    }

    public fun readShort(): Short {
        var s = if (isLsb()) readUnsigned() + (readUnsigned() shl 8) else (readUnsigned() shl 8) + readUnsigned()
        if (s > Short.MAX_VALUE) {
            s -= 65536
        }
        return s.toShort()
    }

    public fun readUnsignedShort(): Int {
        return readShort().toInt() and 0xFFFF
    }

    public fun readInt(): Int {
        return if (isLsb())
            readUnsigned() + (readUnsigned() shl 8) + (readUnsigned() shl 16) + (readUnsigned() shl 24)
        else
            (readUnsigned() shl 24) + (readUnsigned() shl 16) + (readUnsigned() shl 8) + readUnsigned()
    }

    public fun readUnsignedInt(): Long {
        return readInt().toLong() and 0xFFFFFFFFL
    }

    public fun readLong(): Long {
        val v = readUnsignedInt()
        val k = readUnsignedInt()
        return (v shl 32) + k
    }

    public fun read24BitInt(): Int {
        return if (isLsb())
            readUnsigned() + (readUnsigned() shl 8) + (readUnsigned() shl 16)
        else
            (readUnsigned() shl 16) + (readUnsigned() shl 8) + readUnsigned()
    }

    public fun readSmart(): Int {
        val i = data[offset].toInt() and 0xFF
        if (i < 128) {
            return readUnsigned() - 64
        }
        return readUnsignedShort() - 49152
    }

    public fun readUnsignedSmart(): Int {
        val i = data[offset].toInt() and 0xFF
        if (i < 128) {
            return readUnsigned()
        }
        return readUnsignedShort() - 32768
    }

    public fun readSmart2(): Int {
        var i = 0
        var v = readUnsignedSmart()
        while (v == Short.MAX_VALUE.toInt()) {
            v = readUnsignedSmart()
            i += Short.MAX_VALUE.toInt()
        }
        i += v
        return i
    }

    public fun readBigSmart(): Int {
        if (data[offset] < 0) {
            return readInt() and 0x7FFFFFFF
        }
        val value = readUnsignedShort()
        if (value == Short.MAX_VALUE.toInt()) {
            return -1
        }
        return value
    }

    public fun readString(): String {
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

    public fun readStringRaw(): String {
        val start = offset
        while (data[offset++].toInt() != 10) {

        }
        return String(data, start, offset - start - 1)
    }

    public fun readBit(position: Int): Int {
        check(!hasBitAccess()) { "No bit access." }
        var i = position
        var byteOffset = this.bitPosition shr 3
        var bitMaskIndex = BYTE_SIZE - (this.bitPosition and (BYTE_SIZE - 1))
        var value = 0
        this.bitPosition += i
        while (i > bitMaskIndex) {
            value += data[byteOffset++].toInt() and BIT_MASK[bitMaskIndex] shl i - bitMaskIndex
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