package com.example.imageclassification

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.imageclassification.ml.Cifar10Model1
import com.example.imageclassification.ui.theme.ImageClassificationTheme
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageClassificationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ImageClassifierScreen()
                        }

                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun ImageClassifierScreen() {
    val context = LocalContext.current

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var prediction by remember { mutableStateOf("Prediction will appear here") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = {
            launcher.launch("image/*") // Only image types
        }) {
            Text("Pick Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        imageUri?.let { uri ->
            // Show the selected image
            AsyncImage(
                model = uri,
                contentDescription = "Selected image"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                // Convert URI to Bitmap
                val bitmap = uriToBitmap(context, uri)

                // Resize to 32x32 (CIFAR-10 input size)
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 32, 32, true)

                // Convert bitmap to ByteBuffer format for TFLite model
                val byteBuffer = convertBitmapToByteBuffer(resizedBitmap)

                // Load model
                val model = Cifar10Model1.newInstance(context)

                // Prepare input tensor
                val input = TensorBuffer.createFixedSize(intArrayOf(1, 32, 32, 3), DataType.FLOAT32)
                input.loadBuffer(byteBuffer)

                // Run inference
                val outputs = model.process(input)
                val outputBuffer = outputs.outputFeature0AsTensorBuffer

                // Get index of highest confidence value
                val maxIndex = outputBuffer.floatArray.indices.maxByOrNull {
                    outputBuffer.floatArray[it]
                } ?: -1

                prediction = "Prediction Index: $maxIndex"

                // Clean up model resources
                model.close()
            }) {
                Text("Run Model")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = prediction)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
fun uriToBitmap(context: Context, uri: Uri): Bitmap {
    val source = ImageDecoder.createSource(context.contentResolver, uri)
    val bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
        decoder.isMutableRequired = true
        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE // 👈 Boom! Software rendering FTW
    }
    return bitmap
}

fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
    val byteBuffer = ByteBuffer.allocateDirect(4 * 32 * 32 * 3) // 4 bytes per float * width * height * channels
    byteBuffer.order(ByteOrder.nativeOrder())

    for (y in 0 until 32) {
        for (x in 0 until 32) {
            val pixel = bitmap.getPixel(x, y)
            // Normalize RGB values to 0..1 by dividing by 255f
            byteBuffer.putFloat(Color.red(pixel) / 255.0f)
            byteBuffer.putFloat(Color.green(pixel) / 255.0f)
            byteBuffer.putFloat(Color.blue(pixel) / 255.0f)
        }
    }

    return byteBuffer
}