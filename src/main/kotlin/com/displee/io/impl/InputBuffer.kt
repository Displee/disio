package com.displee.io.impl

import com.displee.io.Buffer

open class InputBuffer(data: ByteArray) : Buffer(data) {

    fun readByte(): Byte {
        check(offset < data.size) { "No data left to read." }
        return data[offset++]
    }

    fun readUnsignedByte(): Int {
        return readByte().toInt() and 0xFF
    }

    fun readBytes(length: Int): ByteArray {
        val data = ByteArray(length)
        readBytes(data, 0, data.size)
        return data
    }

    fun readBytes(data: ByteArray) {
        readBytes(data, 0, data.size)
    }

    fun readBytes(data: ByteArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = readByte()
        }
    }

    fun readUnsignedBytes(length: Int): IntArray {
        val data = IntArray(length)
        readUnsignedBytes(data, 0, data.size)
        return data
    }

    fun readUnsignedBytes(data: IntArray) {
        readUnsignedBytes(data, 0, data.size)
    }

    fun readUnsignedBytes(data: IntArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = readUnsignedByte()
        }
    }

    fun readShorts(length: Int): ShortArray {
        val data = ShortArray(length)
        readShorts(data, 0, data.size)
        return data
    }

    fun readShorts(data: ShortArray) {
        readShorts(data, 0, data.size)
    }

    fun readShorts(data: IntArray) {
        readShorts(data, 0, data.size)
    }

    fun readShorts(data: ShortArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = readShort().toShort()
        }
    }

    fun readShorts(data: IntArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = readShort().toInt()
        }
    }

    fun readUnsignedShorts(length: Int): IntArray {
        val data = IntArray(length)
        readUnsignedShorts(data, 0, data.size)
        return data
    }

    fun readUnsignedShorts(data: IntArray) {
        readUnsignedShorts(data, 0, data.size)
    }

    fun readUnsignedShorts(data: IntArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = readUnsignedShort()
        }
    }

    fun readInts(length: Int): IntArray {
        val data = IntArray(length)
        readInts(data, 0, data.size)
        return data
    }

    fun readInts(data: IntArray) {
        readInts(data, 0, data.size)
    }

    fun readInts(data: IntArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = readInt()
        }
    }

    fun readUnsignedInts(length: Int): LongArray {
        val data = LongArray(length)
        readUnsignedInts(data, 0, data.size)
        return data
    }

    fun readUnsignedInts(data: LongArray) {
        readUnsignedInts(data, 0, data.size)
    }

    fun readUnsignedInts(data: LongArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = readUnsignedInt()
        }
    }

    fun readLongs(length: Int): LongArray {
        val data = LongArray(length)
        readLongs(data, 0, data.size)
        return data
    }

    fun readLongs(data: LongArray) {
        readLongs(data, 0, data.size)
    }

    fun readLongs(data: LongArray, offset: Int, length: Int) {
        for (i in offset until length + offset) {
            data[i] = readLong()
        }
    }

    fun readBoolean(): Boolean {
        return readByte() == 1.toByte()
    }

    fun readNegativeByte(): Byte {
        return (-readByte()).toByte()
    }

    fun readUnsignedNegativeByte(): Int {
        return -readUnsignedByte()
    }

    fun readByte128(): Byte {
        return (readByte() - 128).toByte()
    }

    fun readUnsignedByte128(): Int {
        return (readByte() - 128) and 0xFF
    }

    fun readNegativeByte128(): Byte {
        return (128 - readByte()).toByte()
    }

    fun readUnsignedNegativeByte128(): Int {
        return (128 - readByte()) and 0xFF
    }

    fun readShort(): Int {
        var s = readUnsignedShort()
        if (s > Short.MAX_VALUE) {
            s -= 65536
        }
        return s
    }

    private fun readUnsignedShortMsb(): Int {
        return (readUnsignedByte() shl 8) + readUnsignedByte()
    }

    private fun readUnsignedShortLsb(): Int {
        return readUnsignedByte() + (readUnsignedByte() shl 8)
    }

    fun readUnsignedShort(): Int {
        return if (isMsb()) readUnsignedShortMsb() else readUnsignedShortLsb()
    }

    fun readShortLE(): Int {
        var s = readUnsignedShortLE()
        if (s > Short.MAX_VALUE) {
            s -= 65536
        }
        return s
    }

    fun readUnsignedShortLE(): Int {
        return if (isMsb()) readUnsignedShortLsb() else readUnsignedShortMsb()
    }

    private fun readShort128Msb(): Int {
        var i = (readUnsignedByte() shl 8) + (readByte() - 128 and 0xff)
        if (i > 32767) {
            i -= 65536
        }
        return i
    }

    private fun readShort128Lsb(): Int {
        var i = (readByte() - 128 and 0xff) + (readUnsignedByte() shl 8)
        if (i > 32767) {
            i -= 65536
        }
        return i
    }

    fun readShort128(): Int {
        return if (isMsb()) readShort128Msb() else readShort128Lsb()
    }

    fun readShortLE128(): Int {
        return if (isMsb()) readShort128Lsb() else readShort128Msb()
    }

    fun read24BitInt(): Int {
        return if (isLsb())
            readUnsignedByte() + (readUnsignedByte() shl 8) + (readUnsignedByte() shl 16)
        else
            (readUnsignedByte() shl 16) + (readUnsignedByte() shl 8) + readUnsignedByte()
    }

    private fun readIntMsb(): Int {
        return (readUnsignedByte() shl 24) + (readUnsignedByte() shl 16) + (readUnsignedByte() shl 8) + readUnsignedByte()
    }

    private fun readIntLsb(): Int {
        return readUnsignedByte() + (readUnsignedByte() shl 8) + (readUnsignedByte() shl 16) + (readUnsignedByte() shl 24)
    }

    fun readInt(): Int {
        return if (isMsb()) readIntMsb() else readIntLsb()
    }

    fun readIntLE(): Int {
        return if (isMsb()) readIntLsb() else readIntMsb()
    }

    private fun readIntV1Msb(): Int {
        return (readUnsignedByte() shl 8) + readUnsignedByte() + (readUnsignedByte() shl 24) + (readUnsignedByte() shl 16)
    }

    private fun readIntV1Lsb(): Int {
        return (readUnsignedByte() shl 16) + (readUnsignedByte() shl 24) + readUnsignedByte() + (readUnsignedByte() shl 8)
    }

    fun readIntV1(): Int {
        return if (isMsb()) readIntV1Msb() else readIntV1Lsb()
    }

    fun readIntV2(): Int {
        return if (isMsb()) readIntV1Lsb() else readIntV1Msb()
    }

    fun readUnsignedInt(): Long {
        return readInt().toLong() and 0xFFFFFFFFL
    }

    fun readUnsignedIntLE(): Long {
        return readIntLE().toLong() and 0xFFFFFFFFL
    }

    fun readLong(): Long {
        val v = readUnsignedInt()
        val k = readUnsignedInt()
        return (v shl 32) + k
    }

    fun readLongLE(): Long {
        val v = readUnsignedIntLE()
        val k = readUnsignedIntLE()
        return (v shl 32) + k
    }

    fun readSmart(): Int {
        val i = data[offset].toInt() and 0xFF
        if (i < 128) {
            return readUnsignedByte() - 64
        }
        return readUnsignedShort() - 49152
    }

    fun readUnsignedSmart(): Int {
        val i = data[offset].toInt() and 0xFF
        if (i < 128) {
            return readUnsignedByte()
        }
        return readUnsignedShort() - 32768
    }

	fun readUnsignedSmartMin1(): Int {
		return readUnsignedSmart() - 1
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
        while (readByte().toInt() != 0) {
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

    fun readStringNull(): String? {
        if (data[offset].toInt() == 0) {
            ++offset
            return null
        }
        return readString()
    }

    fun readIntAsFloat(): Float {
        return java.lang.Float.intBitsToFloat(readInt())
    }

    fun readIntAsFloatLE(): Float {
        return java.lang.Float.intBitsToFloat(readIntLE())
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
            var sum = -0x3910c8e0L
            val delta = -0x61c88647L
            var k2 = 32
	        while (k2-- > 0) {
		        l1 -= (keys[(sum and 6451L ushr 11).toInt()].toLong() + sum xor (k1 + (k1 shl 4 xor (k1 ushr 5))).toLong()).toInt()
		        sum -= delta
		        k1 -= ((((l1 shl 4) xor (l1 ushr 5)) + l1).toLong() xor keys[(sum and 3L).toInt()].toLong() + sum).toInt()
	        }
            val outputBuffer = toOutputBuffer()
            outputBuffer.offset -= 8
            outputBuffer.writeInt(k1).writeInt(l1)
            data = outputBuffer.raw()
        }
        offset = l
    }

    @JvmOverloads
    fun toOutputBuffer(copyOffset: Boolean = true): OutputBuffer {
        val outputBuffer = OutputBuffer(0)
        outputBuffer.writeBytes(data.clone())
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
	            val char = byteToChar(data[startOffset + i]) ?: continue
                chars[offset++] = char
            }
            return String(chars, 0, offset)
        }

	    fun byteToChar(value: Byte): Char? {
			var v = value.toInt() and 0xFF
		    if (v == 0) {
			    return null
		    }
		    if (value in 128..159) {
			    var specialCharacter = SPECIAL_CHARACTERS[value - 128].toInt()
			    if (specialCharacter == 0) {
				    specialCharacter = 63
			    }
			    v = specialCharacter
		    }
		    return v.toChar()
	    }
    }

}