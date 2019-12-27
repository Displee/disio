import com.displee.io.impl.InputBuffer
import com.displee.io.impl.OutputBuffer
import org.junit.jupiter.api.Test
import java.util.concurrent.ThreadLocalRandom

//TODO Finish writing tests
class InputBufferTest {

    @Test
    public fun testPositiveByte() {
        val value = 12.toByte()

        val outputBuffer = OutputBuffer(1)
        outputBuffer.write(value)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.read() == value)
    }

    @Test
    public fun testNegativeByte() {
        val value = (-120).toByte()

        val outputBuffer = OutputBuffer(1)
        outputBuffer.write(value)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.read() == value)
    }

    @Test
    public fun testUnsignedByte() {
        val value = (-57).toByte()
        val unsigned = value.toInt() and 0xFF

        val outputBuffer = OutputBuffer(1)
        outputBuffer.write(value)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.readUnsigned() == unsigned)
    }

    @Test
    public fun testShort() {
        val value = 65532.toShort()

        val outputBuffer = OutputBuffer(2)
        outputBuffer.writeShort(value)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.readShort() == value)
    }

    @Test
    public fun testInt() {
        val value = 8439843

        val outputBuffer = OutputBuffer(4)
        outputBuffer.writeInt(value)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.readInt() == value)
    }

    @Test
    public fun testBytes() {
        val data = generateRandomData()
        val size = data.size

        val outputBuffer = OutputBuffer(size)
        outputBuffer.write(data)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.read(size).contentEquals(data))
    }

    @Test
    public fun testNormalString() {
        val string = "Hello world"

        val outputBuffer = OutputBuffer(string.length)
        outputBuffer.writeString(string)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.readString() == string)
    }

    @Test
    public fun testBit() {
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