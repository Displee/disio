# DisIO
DisIO is a lightweight standalone library which allows you to perform I/O operations like Java's `ByteBuffer` class. However the implementations of this class are all package-private and thus limits the usage of it. For example: you can't extend the `ByteBuffer` or any of its implementations.

The goal of this library is not alone to take away the limitations of Java's `ByteBuffer` class, but also to provide more I/O features.

Features:
* Basic I/O operations
* Array operations
* Signed and unsigned operations
* Bit operations
* LSB (least significant bit) and MSB (most significant bit) support
* XTEA encryption and decryption
* RSA cryption

## Gradle
```
implementation 'com.displee:disio:1.7'
```

### Initialization
```Kotlin
//Set capacity for best performance
val outputBuffer = OutputBuffer(10)
...
val inputBuffer = InputBuffer(outputBuffer.array())
```
```kotlin
val array = byteArrayOf(...)
val inputBuffer = InputBuffer(array)
```

### LSB mode (least significant bit)
```kotlin
val outputBuffer = OutputBuffer(5)
outputBuffer.lsb()
outputBuffer.writeShort(67)
val inputBuffer = InputBuffer(outputBuffer.array())
inputBuffer.lsb()
val value = inputBuffer.readShort()
...
```

### MSB mode (most significant bit, this is also the default operation mode)
```kotlin
val outputBuffer = OutputBuffer(4)
outputBuffer.msb()
outputBuffer.writeInt(61263)
val inputBuffer = InputBuffer(outputBuffer.array())
inputBuffer.msb()
val value = inputBuffer.readInt()
...
```

### Switch to different buffer type
```kotlin
val outputBuffer = OutputBuffer(0)
outputBuffer.writeInt(4)
val inputBuffer = outputBuffer.toInputBuffer(copyOffset = false)
val value = inputBuffer.readInt()

val inputBuffer = InputBuffer(byteArrayOf(1, 3, 3, 7))
val value = inputBuffer.readInt()
val outputBuffer = inputBuffer.toOutputBuffer()
outputBuffer.writeShort(256)
//now outputBuffer.raw() results in [1, 3, 3, 7, 1, 0]
```

### XTEA encryption and decryption
```kotlin
val value1 = 12 //random
val value2 = 19238 //random
val value3 = 99 //random
val data = byteArrayOf(1, 3, 3, 4, 5, 6, 7, 8, 7, 6, 5)
val keys = intArrayOf(9, 5, 6, 4)
val outputBuffer = OutputBuffer(0)
outputBuffer.write(value1)
outputBuffer.writeInt(value2)
val startOffset = outputBuffer.offset
outputBuffer.write(data)
outputBuffer.encodeXTEA(keys, startOffset, outputBuffer.offset)
outputBuffer.write(value3)

val inputBuffer = outputBuffer.toInputBuffer(false)
assert(inputBuffer.read().toInt() == value1)
assert(inputBuffer.readInt() == value2)
inputBuffer.decodeXTEA(keys, startOffset, inputBuffer.raw().size)
val decryptedData = inputBuffer.read(data.size)
assert(decryptedData.contentEquals(data))
assert(inputBuffer.read().toInt() == value3)
```