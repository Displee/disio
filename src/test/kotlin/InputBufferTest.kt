import impl.InputBuffer
import impl.OutputBuffer
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

        val outputBuffer = OutputBuffer(data.size)
        outputBuffer.write(data)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.read(data.size).contentEquals(data))
    }

    @Test
    public fun testNormalString() {
        val string = "Hello world"

        val outputBuffer = OutputBuffer(string.length)
        outputBuffer.writeString(string)

        val inputBuffer = InputBuffer(outputBuffer.array())
        assert(inputBuffer.readString() == string)
    }

    private fun generateRandomData(): ByteArray {
        return byteArrayOf(
            ThreadLocalRandom.current().nextInt(1, 128).toByte(),
            ThreadLocalRandom.current().nextInt(1, 128).toByte(),
            ThreadLocalRandom.current().nextInt(1, 128).toByte(),
            ThreadLocalRandom.current().nextInt(1, 128).toByte(),
            ThreadLocalRandom.current().nextInt(1, 128).toByte(),
            ThreadLocalRandom.current().nextInt(1, 128).toByte(),
            ThreadLocalRandom.current().nextInt(1, 128).toByte(),
            ThreadLocalRandom.current().nextInt(1, 128).toByte()
        )
    }

}