package tech.merajobs.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import tech.merajobs.R
import tech.merajobs.databinding.ActivityWelcomeBinding
import tech.merajobs.utility.BaseActivity

class WelcomeActivity : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btOpenSearchJob.setOnClickListener(this)
        binding.btOpenPostJob.setOnClickListener(this)
        binding.btOpenUploadCreateCV.setOnClickListener(this)


    }

    override fun onClick(p0: View?) {

        when (p0!!.id) {

            R.id.btOpenSearchJob -> {
                val intent = Intent(this, FindJobActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.btOpenPostJob -> {

            }

            R.id.btOpenUploadCreateCV -> {
                val intent = Intent(this, CreateCVActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }


}