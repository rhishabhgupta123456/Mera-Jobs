package tech.merajobs.activity

import android.content.Intent
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import tech.merajobs.adapter.OnBoardingAdapter
import tech.merajobs.dataModel.OnBoardingDataModel
import tech.merajobs.utility.BaseActivity
import tech.merajobs.R
import tech.merajobs.databinding.ActivityOnBoardingBinding

class OnBoardingActivity : BaseActivity() {

    private lateinit var binding: ActivityOnBoardingBinding
    private lateinit var adapter: OnBoardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)



        adapter = OnBoardingAdapter(getOnBoardingData())
        binding.viewpager.adapter = adapter
        binding.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL


        binding.btNext.setOnClickListener {
            if (binding.viewpager.currentItem + 1 < adapter.itemCount) {
                binding.viewpager.currentItem += 1
            } else {
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


    }

    private fun getOnBoardingData(): ArrayList<OnBoardingDataModel> {
        val data: ArrayList<OnBoardingDataModel> = ArrayList()

        data.add(
            OnBoardingDataModel(
                R.drawable.onborad_pic1,
                getString(R.string.onBoardHead1),
                getString(R.string.onBoardHeadDescription1)
            )
        )
        data.add(
            OnBoardingDataModel(
                R.drawable.onborad_pic1,
                getString(R.string.onBoardHead2),
                getString(R.string.onBoardHeadDescription2)
            )
        )
        data.add(
            OnBoardingDataModel(
                R.drawable.onborad_pic1,
                getString(R.string.onBoardHead3),
                getString(R.string.onBoardHeadDescription3)
            )
        )




        return data

    }


}