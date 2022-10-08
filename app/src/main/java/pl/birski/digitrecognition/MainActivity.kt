package pl.birski.digitrecognition

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.birski.digitrecognition.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var classifier: Classifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            classifier = Classifier(this)
        } catch (e: IOException) {
            Toast.makeText(this, "Error while using classifier", Toast.LENGTH_LONG).show()
        }

        binding.btnDetect.setOnClickListener {
        }

        binding.btnClear.setOnClickListener {
        }
    }
}
