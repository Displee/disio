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
        outputBuffer.write(value)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.read() == value)
    }

    @Test
    fun testNegativeByte() {
        val value = (-120).toByte()

        val outputBuffer = OutputBuffer(1)
        outputBuffer.write(value)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.read() == value)
    }

    @Test
    fun testUnsignedByte() {
        val value = (-57).toByte()
        val unsigned = value.toInt() and 0xFF

        val outputBuffer = OutputBuffer(1)
        outputBuffer.write(value)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.readUnsigned() == unsigned)
    }

    @Test
    fun testShort() {
        val value = 65532.toShort()

        val outputBuffer = OutputBuffer(2)
        outputBuffer.writeShort(value)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.readShort() == value)
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
    fun testBytes() {
        val data = generateRandomData()
        val size = data.size

        val outputBuffer = OutputBuffer(size)
        outputBuffer.write(data)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.read(size).contentEquals(data))
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
        outputBuffer.write(byte)
        outputBuffer.startBitAccess()
        outputBuffer.writeBit(bitId, bitValue)
        outputBuffer.finishBitAccess()
        outputBuffer.writeInt(int)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.readUnsigned() == byte)
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
        val outputBuffer = OutputBuffer(0)
        outputBuffer.write(value1)
        outputBuffer.writeInt(value2)
        val startOffset = outputBuffer.offset
        outputBuffer.write(data)
        outputBuffer.encryptXTEA(keys, startOffset, outputBuffer.offset)
        outputBuffer.write(value3)

        val inputBuffer = outputBuffer.toInputBuffer(false)
        assert(inputBuffer.read().toInt() == value1)
        assert(inputBuffer.readInt() == value2)
        inputBuffer.decryptXTEA(keys, startOffset, inputBuffer.raw().size)
        val decryptedData = inputBuffer.read(data.size)
        assert(decryptedData.contentEquals(data))
        assert(inputBuffer.read().toInt() == value3)
    }

    @Test
    fun testRSA() {
        val privateExponent = BigInteger("95776340111155337321344029627634178888626101791582245228586750697996713454019354716577077577558156976177994479837760989691356438974879647293064177555518187567327659793331431421153203931914933858526857396428052266926507860603166705084302845740310178306001400777670591958466653637275131498866778592148380588481")
        val privateModulus = BigInteger("119555331260995530494627322191654816613155476612603817103079689925995402263457895890829148093414135342420807287820032417458428763496565605970163936696811485500553506743979521465489801746973392901885588777462023165252483988431877411021816445058706597607453280166045122971960003629860919338852061972113876035333")
        val publicExponent = BigInteger("10001", 16)
        val publicModulus = BigInteger("119555331260995530494627322191654816613155476612603817103079689925995402263457895890829148093414135342420807287820032417458428763496565605970163936696811485500553506743979521465489801746973392901885588777462023165252483988431877411021816445058706597607453280166045122971960003629860919338852061972113876035333")
        val randomData = byteArrayOf(0, 1, 3, 5, 7, 7, 8, 8, 2, 1)

        val encryptedData = Buffer.cryptRSA(randomData, privateExponent, privateModulus)
        assert(!encryptedData.contentEquals(randomData))

        val decryptedData = Buffer.cryptRSA(encryptedData, publicExponent, publicModulus)
        println(decryptedData.contentEquals(randomData))
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