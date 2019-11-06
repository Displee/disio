# DisIO
DisIO is a lightweight standalone library which allows you to perform I/O operations like Java's `ByteBuffer` class. However the implementations of this class are all package-private and thus limits the usage of it. For example: you can't extend the `ByteBuffer` or any of its implementations.

The goal of this library is not alone to take away the limitations of Java's `ByteBuffer` class, but also to provide more I/O features.

Features:
* Basic I/O operations
* Array operations
* Signed and unsigned values
* LSB (least significant bit) and MSB (most significant bit)

## Gradle
```
implementation 'com.displee.io:disio:1.0'
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
inputBuffer.msb()
val value = inputBuffer.readShort()
...
```

### MSB mode (most significant bit)
```kotlin
val outputBuffer = OutputBuffer(4)
outputBuffer.lsb()
outputBuffer.writeInt(61263)
val inputBuffer = InputBuffer(outputBuffer.array())
inputBuffer.lsb()
val value = inputBuffer.readInt()
...
```