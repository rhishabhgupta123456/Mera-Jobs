package tech.merajobs.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import tech.merajobs.R
import tech.merajobs.databinding.ActivityEmployerBinding
import tech.merajobs.databinding.ActivityFindJobBinding
import tech.merajobs.databinding.LayoutSideEmployerNavBarBinding
import tech.merajobs.databinding.LayoutSideNavBarBinding
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseActivity
import tech.merajobs.utility.PostJobSessionManager
import tech.merajobs.utility.SessionManager

class EmployerActivity : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityEmployerBinding
    lateinit var sessionManager: SessionManager
    lateinit var navBinding: LayoutSideEmployerNavBarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployerBinding.inflate(layoutInflater)
        sessionManager = SessionManager(this)
        setContentView(binding.root)


        val headerView = binding.navigationSideNavBar.getHeaderView(0)
        navBinding = LayoutSideEmployerNavBarBinding.bind(headerView)


        navBinding.btLogOut.setOnClickListener(this)
        navBinding.btCloseDrawer.setOnClickListener(this)
        navBinding.btOpenBranch.setOnClickListener(this)
        navBinding.btOpenSettingPage.setOnClickListener(this)
        navBinding.btOpenAboutPage.setOnClickListener(this)
        navBinding.btOpenTermAndConditionPage.setOnClickListener(this)
        navBinding.btOpenPrivacyPolicyPage.setOnClickListener(this)

        binding.btPostJobs.setOnClickListener(this)
        binding.btOpenHome.setOnClickListener(this)
        binding.btOpenMessage.setOnClickListener(this)
        binding.btOpenPostedJob.setOnClickListener(this)
        binding.btOpenProfile.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btCloseDrawer -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }

            R.id.btOpenBranch -> {
                sessionManager.setSourceFragment(AppConstant.HOME_SCREEN)
                findNavController(R.id.fragmentEmployerContainerView).navigate(R.id.branchFragment)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }


            R.id.btOpenHome -> {
                findNavController(R.id.fragmentEmployerContainerView).navigate(R.id.employerHomeFragment)
                openHomeScreen()
            }

            R.id.btOpenMessage -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                findNavController(R.id.fragmentEmployerContainerView).navigate(R.id.messageInboxFragment)
                openMessageScreen()
            }

            R.id.btOpenPostedJob -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                findNavController(R.id.fragmentEmployerContainerView).navigate(R.id.postedJobFragment)
                openBookMarkScreen()
            }

            R.id.btOpenProfile -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                findNavController(R.id.fragmentEmployerContainerView).navigate(R.id.MyProfileScreen)
                openProfileScreen()
            }

            R.id.btOpenSettingPage -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                findNavController(R.id.fragmentEmployerContainerView).navigate(R.id.settingScreen)
                openHomeScreen()
            }

            R.id.btPostJobs -> {
                val postJobSessionManager = PostJobSessionManager(this)
                postJobSessionManager.setJobId(null)
                postJobSessionManager.setTitle("")
                postJobSessionManager.setNoOFVacancy("")
                postJobSessionManager.setJobLabel("")
                postJobSessionManager.setIndustry("")
                postJobSessionManager.setIndustryLabel("")
                postJobSessionManager.setDepartment("")
                postJobSessionManager.setDepartmentLabel("")
                postJobSessionManager.setSkill(null)
                postJobSessionManager.setRole("")
                postJobSessionManager.setRoleLabel("")
                postJobSessionManager.setGoodToHave("")
                postJobSessionManager.setDescription("")
                postJobSessionManager.setLastDateToApply("")
                postJobSessionManager.setQualification("")
                postJobSessionManager.setCourse("")
                postJobSessionManager.setSpecialization("")
                postJobSessionManager.setExperience("")
                postJobSessionManager.setNoticePeriod("")
                postJobSessionManager.setQualificationLabel("")
                postJobSessionManager.setCourseLabel("")
                postJobSessionManager.setSpecializationLabel("")
                postJobSessionManager.setExperienceLabel("")
                postJobSessionManager.setNoticePeriodLabel("")
                postJobSessionManager.setSalaryRange("")
                postJobSessionManager.setSalaryType("")
                postJobSessionManager.setNegotiable("")
                postJobSessionManager.setCurrency("")
                postJobSessionManager.setEmploymentType("")
                postJobSessionManager.setWorkLocationType("")
                postJobSessionManager.setLocation("")
                postJobSessionManager.setSalaryRangeLabel("")
                postJobSessionManager.setSalaryTypeLabel("")
                postJobSessionManager.setNegotiableLabel("")
                postJobSessionManager.setCurrencyLabel("")
                postJobSessionManager.setEmploymentTypeLabel("")
                postJobSessionManager.setWorkLocationTypeLabel("")
                postJobSessionManager.setLocationLabel("")
                startActivity(Intent(this, PostJobActivity::class.java))
            }

            R.id.btLogOut -> {
                logOutDialog()
            }


            R.id.btOpenAboutPage -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                sessionManager.setSourceFragment(AppConstant.ABOUT_US)
                findNavController(R.id.fragmentFindJobContainerView).navigate(R.id.CMSFragment)
                openHomeScreen()
            }

            R.id.btOpenTermAndConditionPage -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                sessionManager.setSourceFragment(AppConstant.TERM_CONDITION)
                findNavController(R.id.fragmentFindJobContainerView).navigate(R.id.CMSFragment)
                openHomeScreen()
            }

            R.id.btOpenPrivacyPolicyPage -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                sessionManager.setSourceFragment(AppConstant.PRIVACY_POLICY)
                findNavController(R.id.fragmentFindJobContainerView).navigate(R.id.CMSFragment)
                openHomeScreen()
            }
        }
    }

    fun setNavigationItem(name: String?, mobile: String?, image: String?, imageApproved: String) {

        navBinding.tvNavUserName.text = name
        navBinding.tvNavUserMobile.text = mobile

        if (imageApproved == "0") {
            Glide.with(this)
                .load(AppConstant.MEDIA_BASE_URL + image)
                .placeholder(R.drawable.demo_user)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(AppConstant.BLUR_RADIUS)))
                .into(navBinding.tvNavUserImg)
        } else {
            Glide.with(this)
                .load(AppConstant.MEDIA_BASE_URL + image)
                .placeholder(R.drawable.demo_user)
                .into(navBinding.tvNavUserImg)
        }
    }

    fun openDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    fun openHomeScreen() {
        binding.homeIcon.setImageResource(R.drawable.home_select_icon)
        binding.bookmarkIcon.setImageResource(R.drawable.bookmark_icon)
        binding.messageIcon.setImageResource(R.drawable.message_icon)
        binding.profileIcon.setImageResource(R.drawable.profile_icon)
    }

    private fun openBookMarkScreen() {
        binding.homeIcon.setImageResource(R.drawable.home_icon)
        binding.bookmarkIcon.setImageResource(R.drawable.bookmark_select_icon)
        binding.messageIcon.setImageResource(R.drawable.message_icon)
        binding.profileIcon.setImageResource(R.drawable.profile_icon)
    }

    fun openMessageScreen() {
        binding.homeIcon.setImageResource(R.drawable.home_icon)
        binding.bookmarkIcon.setImageResource(R.drawable.bookmark_icon)
        binding.messageIcon.setImageResource(R.drawable.message_select_icon)
        binding.profileIcon.setImageResource(R.drawable.profile_icon)
    }

    fun openProfileScreen() {
        binding.homeIcon.setImageResource(R.drawable.home_icon)
        binding.bookmarkIcon.setImageResource(R.drawable.bookmark_icon)
        binding.messageIcon.setImageResource(R.drawable.message_icon)
        binding.profileIcon.setImageResource(R.drawable.profile_select_icon)
    }

    fun showHideBottomMenu(key: Boolean) {
        if (key) {
            binding.bottomMenu.visibility = View.VISIBLE
            binding.midButtonBg.visibility = View.VISIBLE
            binding.btPostJobs.visibility = View.VISIBLE
        } else {
            binding.bottomMenu.visibility = View.GONE
            binding.midButtonBg.visibility = View.GONE
            binding.btPostJobs.visibility = View.GONE
        }
    }


}