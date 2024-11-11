package tech.merajobs.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonElement
import jp.wasabeef.glide.transformations.BlurTransformation
import tech.merajobs.R
import tech.merajobs.databinding.ActivityFindJobBinding
import tech.merajobs.databinding.LayoutSideNavBarBinding
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseActivity
import tech.merajobs.utility.PostJobSessionManager
import tech.merajobs.utility.SessionManager

class FindJobActivity : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityFindJobBinding
    lateinit var navBinding: LayoutSideNavBarBinding
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindJobBinding.inflate(layoutInflater)
        sessionManager = SessionManager(this)
        setContentView(binding.root)

        val headerView = binding.navigationSideNavBar.getHeaderView(0)
        navBinding = LayoutSideNavBarBinding.bind(headerView)

        binding.btOpenHome.setOnClickListener(this)
        binding.btOpenBookMark.setOnClickListener(this)
        binding.btOpenMessage.setOnClickListener(this)
        binding.btOpenProfile.setOnClickListener(this)
        binding.btSearchJob.setOnClickListener(this)


        navBinding.btOpenHomePage.setOnClickListener(this)
        navBinding.btOpenMessagePage.setOnClickListener(this)
        navBinding.btOpenSaveJobPage.setOnClickListener(this)
        navBinding.tvNavUserImg.setOnClickListener(this)
        navBinding.btOpenSettingPage.setOnClickListener(this)
        navBinding.btCloseDrawer.setOnClickListener(this)
        navBinding.btOpenAboutPage.setOnClickListener(this)
        navBinding.btOpenTermAndConditionPage.setOnClickListener(this)
        navBinding.btOpenPrivacyPolicyPage.setOnClickListener(this)
         openHomeScreen()
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btOpenHome -> {
                findNavController(R.id.fragmentFindJobContainerView).navigate(R.id.findJobHomeScreen)
                openHomeScreen()
            }

            R.id.btCloseDrawer -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }

            R.id.btOpenHomePage -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                findNavController(R.id.fragmentFindJobContainerView).navigate(R.id.findJobHomeScreen)
                openHomeScreen()
            }

            R.id.btOpenBookMark -> {
                findNavController(R.id.fragmentFindJobContainerView).navigate(R.id.bookmarkJobScreen)
                openBookMarkScreen()
            }

            R.id.btSearchJob -> {
                val bundle = Bundle()
                bundle.putString(AppConstant.PAGE_TITLE, "Search Job")
                findNavController(R.id.fragmentFindJobContainerView).navigate(
                    R.id.searchJobScreen,
                    bundle
                )
            }

            R.id.btOpenSaveJobPage -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                findNavController(R.id.fragmentFindJobContainerView).navigate(R.id.bookmarkJobScreen)
                openBookMarkScreen()
            }


            R.id.btOpenMessage -> {
                findNavController(R.id.fragmentFindJobContainerView).navigate(R.id.messageInboxFragment)
                openMessageScreen()
            }

            R.id.btOpenMessagePage -> {
                findNavController(R.id.fragmentFindJobContainerView).navigate(R.id.messageInboxFragment)
                openMessageScreen()
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }

            R.id.btOpenProfile -> {
                findNavController(R.id.fragmentFindJobContainerView).navigate(R.id.MyProfileScreen)
                openProfileScreen()
            }


            R.id.tvNavUserImg -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                findNavController(R.id.fragmentFindJobContainerView).navigate(R.id.MyProfileScreen)
                openProfileScreen()
            }

            R.id.btOpenSettingPage -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                findNavController(R.id.fragmentFindJobContainerView).navigate(R.id.settingScreen)
                openHomeScreen()
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

    fun setNavigationItem(name: String?, mobile: String?, image: String?, imageApprove: String?) {

        navBinding.tvNavUserName.text = name
        navBinding.tvNavUserMobile.text = mobile

        if (imageApprove == "0") {
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

    fun openHomeScreen() {
        binding.homeIcon.setImageResource(R.drawable.home_select_icon)
        binding.bookmarkIcon.setImageResource(R.drawable.bookmark_icon)
        binding.messageIcon.setImageResource(R.drawable.message_icon)
        binding.profileIcon.setImageResource(R.drawable.profile_icon)
    }

    fun openBookMarkScreen() {
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

    fun openDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    fun showHideBottomMenu(key: Boolean) {
        if (key) {
            binding.bottomMenu.visibility = View.VISIBLE
            binding.midButtonBg.visibility = View.VISIBLE
            binding.btSearchJob.visibility = View.VISIBLE
        } else {
            binding.bottomMenu.visibility = View.GONE
            binding.midButtonBg.visibility = View.GONE
            binding.btSearchJob.visibility = View.GONE
        }
    }
}