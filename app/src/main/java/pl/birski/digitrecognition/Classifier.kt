package pl.birski.digitrecognition

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class Classifier(context: Context) {

    private val LOG_TAG = Classifier::class.java.simpleName

    private val MODEL_NAME = "digit.tflite"
    private val BATCH_SIZE = 1
    private val NUM_CHANNEL = 1
    private val NUM_CLASSES = 10

    private var mInterpreter: Interpreter
    private var mImageData: ByteBuffer

    private val options: Interpreter.Options = Interpreter.Options()
    private val mImagePixels = IntArray(IMG_HEIGHT * IMG_WIDTH)

    private val mResult = Array(1) { FloatArray(NUM_CLASSES) }

    init {
        this.mInterpreter = Interpreter(loadModelFile(context), options)
        this.mImageData = ByteBuffer.allocateDirect(
            4 * BATCH_SIZE * IMG_HEIGHT * IMG_WIDTH * NUM_CHANNEL
        )
        this.mImageData.order(ByteOrder.nativeOrder())
    }

    @Throws(IOException::class)
    private fun loadModelFile(context: Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(MODEL_NAME)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun classify(bitmap: Bitmap): Result {
        convertBitmapToByteBuffer(bitmap)
        val startTime = SystemClock.uptimeMillis()
        mInterpreter.run(mImageData, mResult)
        val endTime = SystemClock.uptimeMillis()
        val timeCost = endTime - startTime
        Log.v(LOG_TAG, "classify(): result = ${mResult[0].contentToString()}, timeCost = $timeCost")
        return Result(mResult[0], timeCost)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap) {
        mImageData.rewind()
        bitmap.getPixels(
            mImagePixels,
            0,
            bitmap.width,
            0,
            0,
            bitmap.width,
            bitmap.height
        )
        var pixel = 0
        for (i in 0 until IMG_WIDTH) {
            for (j in 0 until IMG_HEIGHT) {
                val value = mImagePixels[pixel++]
                mImageData.putFloat(convertPixel(value))
            }
        }
    }

    private fun convertPixel(color: Int) = (
        255 - (
            (color shr 16 and 0xFF) * 0.299f +
                (color shr 8 and 0xFF) * 0.587f +
                (color and 0xFF) * 0.114f
            )
        ) / 255.0f

    companion object {
        val IMG_HEIGHT = 28
        val IMG_WIDTH = 28
    }
}
