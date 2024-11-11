package tech.merajobs.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import tech.merajobs.R
import tech.merajobs.databinding.ActivityEmployerBinding
import tech.merajobs.databinding.ActivityPostJobBinding
import tech.merajobs.databinding.LayoutSideNavBarBinding
import tech.merajobs.utility.BaseActivity
import tech.merajobs.utility.SessionManager

class PostJobActivity : BaseActivity() {

    lateinit var binding: ActivityPostJobBinding
    lateinit var sessionManager: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostJobBinding.inflate(layoutInflater)
        sessionManager = SessionManager(this)
        setContentView(binding.root)


    }

}