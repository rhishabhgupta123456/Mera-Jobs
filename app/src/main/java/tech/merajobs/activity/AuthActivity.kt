package tech.merajobs.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import tech.merajobs.databinding.ActivityAuthBinding
import tech.merajobs.utility.BaseActivity


class AuthActivity : BaseActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}