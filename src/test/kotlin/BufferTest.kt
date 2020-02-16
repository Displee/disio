import com.displee.io.Buffer
import com.displee.io.impl.InputBuffer
import com.displee.io.impl.OutputBuffer
import org.junit.jupiter.api.Test
import java.math.BigInteger
import java.util.concurrent.ThreadLocalRandom

//TODO Finish writing tests
class BufferTest {

    @Test
    fun testPositiveByte() {
        val value = 12.toByte()

        val outputBuffer = OutputBuffer(1)
        outputBuffer.writeByte(value)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.readByte() == value)
    }

    @Test
    fun testNegativeByte() {
        val value = (-120).toByte()

        val outputBuffer = OutputBuffer(1)
        outputBuffer.writeByte(value)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.readByte() == value)
    }

    @Test
    fun testUnsignedByte() {
        val value = (-57).toByte()
        val unsigned = value.toInt() and 0xFF

        val outputBuffer = OutputBuffer(1)
        outputBuffer.writeByte(value)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.readUnsignedByte() == unsigned)
    }

    @Test
    fun testShort() {
        var value = 65532

        val outputBuffer = OutputBuffer(2)
        outputBuffer.writeShort(value)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.readUnsignedShort() == value)

        inputBuffer.offset = 0
        value = 65532.toShort().toInt()
        assert(inputBuffer.readShort() == value)
    }

    @Test
    fun testBytePlus128() {
        val value = 110.toByte()
        val output = OutputBuffer(1)
        output.writeByte128(value)

        val input = output.toInputBuffer(false)
        assert(input.readByte128() == value)
    }

    @Test
    fun testByteMin128() {
        val value = 110.toByte()
        val output = OutputBuffer(1)
        output.writeNegativeByte128(value)

        val input = output.toInputBuffer(false)
        assert(input.readNegativeByte128() == value)
    }

    @Test
    fun testInt() {
        val value = 8439843

        val outputBuffer = OutputBuffer(4)
        outputBuffer.writeInt(value)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.readInt() == value)
    }

    @Test
    fun testLong() {
        val value = 29329032L

        val output = OutputBuffer(8)
        output.writeLong(value)

        val input = output.toInputBuffer(false)
        assert(input.readLong() == value)
    }

    @Test
    fun testBytes() {
        val data = generateRandomData()
        val size = data.size

        val outputBuffer = OutputBuffer(size)
        outputBuffer.writeBytes(data)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.readBytes(size).contentEquals(data))
    }

    @Test
    fun testNormalString() {
        val string = "Hello world"

        val outputBuffer = OutputBuffer(string.length)
        outputBuffer.writeString(string)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.readString() == string)
    }

    @Test
    fun testBit() {
        val byte = 32
        val bitId = 26
        val bitValue = 17
        val int = 1337

        val outputBuffer = OutputBuffer(10)
        outputBuffer.writeByte(byte)
        outputBuffer.startBitAccess()
        outputBuffer.writeBit(bitId, bitValue)
        outputBuffer.finishBitAccess()
        outputBuffer.writeInt(int)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.readUnsignedByte() == byte)
        inputBuffer.startBitAccess()
        val bit = inputBuffer.readBit(bitId)
        assert(bit == bitValue)
        inputBuffer.finishBitAccess()
        assert(inputBuffer.readInt() == int)
    }

    @Test
    fun testXTEA() {
        val value1 = 12
        val value2 = 19238
        val value3 = 99
        val data = byteArrayOf(1, 3, 3, 4, 5, 6, 7, 8, 7, 6, 5)
        val keys = intArrayOf(9, 5, 6, 4)
        val outputBuffer = OutputBuffer(5 + data.size + 1)
        outputBuffer.writeByte(value1)
        outputBuffer.writeInt(value2)
        val startOffset = outputBuffer.offset
        outputBuffer.writeBytes(data)
        outputBuffer.encryptXTEA(keys, startOffset, outputBuffer.offset)
        outputBuffer.writeByte(value3)

        val inputBuffer = outputBuffer.toInputBuffer(false)
        assert(inputBuffer.readByte().toInt() == value1)
        assert(inputBuffer.readInt() == value2)
        inputBuffer.decryptXTEA(keys, startOffset, inputBuffer.raw().size)
        val decryptedData = inputBuffer.readBytes(data.size)
        assert(decryptedData.contentEquals(data))
        assert(inputBuffer.readByte().toInt() == value3)
    }

    @Test
    fun testRSA() {
        val privateExponent = BigInteger("95776340111155337321344029627634178888626101791582245228586750697996713454019354716577077577558156976177994479837760989691356438974879647293064177555518187567327659793331431421153203931914933858526857396428052266926507860603166705084302845740310178306001400777670591958466653637275131498866778592148380588481")
        val privateModulus = BigInteger("119555331260995530494627322191654816613155476612603817103079689925995402263457895890829148093414135342420807287820032417458428763496565605970163936696811485500553506743979521465489801746973392901885588777462023165252483988431877411021816445058706597607453280166045122971960003629860919338852061972113876035333")
        val publicExponent = BigInteger("10001", 16)
        val publicModulus = BigInteger("119555331260995530494627322191654816613155476612603817103079689925995402263457895890829148093414135342420807287820032417458428763496565605970163936696811485500553506743979521465489801746973392901885588777462023165252483988431877411021816445058706597607453280166045122971960003629860919338852061972113876035333")
        val randomData = byteArrayOf(1, 4, 0, 5, 7, 7, 8, 8, 2, 1) //seems that if we start the array with a 0, it doesn't encrypt this first byte

        val encryptedData = Buffer.cryptRSA(randomData, privateExponent, privateModulus)
        assert(!encryptedData.contentEquals(randomData))

        val decryptedData = Buffer.cryptRSA(encryptedData, publicExponent, publicModulus)
        assert(decryptedData.contentEquals(randomData))
    }

    private fun generateRandomData(): ByteArray {
        return byteArrayOf(
            getRandomValue(1, 128).toByte(),
            getRandomValue(1, 128).toByte(),
            getRandomValue(1, 128).toByte(),
            getRandomValue(1, 128).toByte(),
            getRandomValue(1, 128).toByte(),
            getRandomValue(1, 128).toByte(),
            getRandomValue(1, 128).toByte(),
            getRandomValue(1, 128).toByte()
        )
    }
    
    private fun getRandomValue(min: Int, max: Int): Int {
        return ThreadLocalRandom.current().nextInt(min, max)
    }

}