package tech.merajobs

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import tech.merajobs.activity.AuthActivity
import tech.merajobs.activity.EmployerActivity
import tech.merajobs.activity.FindJobActivity
import tech.merajobs.databinding.ActivitySplashBinding
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseActivity
import tech.merajobs.utility.SessionManager

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    lateinit var binding: ActivitySplashBinding
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)


        Handler(Looper.myLooper()!!).postDelayed({
            if (sessionManager.getBearerToken() != "Bearer ") {
                if (sessionManager.getUserType() == AppConstant.JOB_SEEKER){
                    val intent = Intent(this, FindJobActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    val intent = Intent(this, EmployerActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000)

    }


}