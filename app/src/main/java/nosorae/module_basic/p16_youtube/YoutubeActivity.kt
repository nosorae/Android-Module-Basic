package nosorae.module_basic.p16_youtube

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import nosorae.module_basic.databinding.ActivityYoutubeBinding

class YoutubeActivity: AppCompatActivity() {
    private lateinit var binding: ActivityYoutubeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYoutubeBinding.inflate(layoutInflater)


    }
}