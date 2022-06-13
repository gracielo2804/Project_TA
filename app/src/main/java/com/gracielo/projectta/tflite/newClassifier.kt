package com.gracielo.projectta.tflite

import android.content.Context
import android.graphics.Bitmap
import com.gracielo.projectta.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class newClassifier (private val image: Bitmap, private val context: Context){

     fun detectImage() : List<Category>{

        val model = Model.newInstance(context)

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)

        var tbuffer = TensorImage.fromBitmap(image)
        var byteBuffer = tbuffer.buffer

        inputFeature0.loadBuffer(byteBuffer)


        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val scores = outputs.scoresAsCategoryList

        // Releases model resources if no longer used.
        model.close()
        return scores
    }

}