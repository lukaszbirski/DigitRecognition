package pl.birski.digitrecognition

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import pl.birski.digitrecognition.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var classifier: Classifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setAllValues(number = 0, prob = 0, time = 0L)
        setContentView(binding.root)

        try {
            classifier = Classifier(this)
        } catch (e: IOException) {
            Toast.makeText(this, "Error while using classifier", Toast.LENGTH_LONG).show()
        }

        binding.btnDetect.setOnClickListener {
            val bitmap = binding.fpvPaint.exportToBitmap(Classifier.IMG_WIDTH, Classifier.IMG_HEIGHT)
            val result = classifier.classify(bitmap)
            val percents: Int = (result.mProbability * 100).toInt()
            setAllValues(
                number = result.mNumber,
                prob = percents,
                time = result.mTimeCost
            )
        }

        binding.btnClear.setOnClickListener {
            binding.fpvPaint.clear()
            setAllValues(number = 0, prob = 0, time = 0L)
        }
    }

    private fun createText(@StringRes res: Int, value: Number) =
        String.format(getString(res), value)

    private fun setAllValues(number: Int, prob: Int, time: Long) {
        binding.prediction.text = createText(R.string.button_prediction_text, number)
        binding.probability.text = createText(R.string.button_probability_text, prob)
        binding.timecost.text = createText(R.string.button_time_text, time)
    }
}
