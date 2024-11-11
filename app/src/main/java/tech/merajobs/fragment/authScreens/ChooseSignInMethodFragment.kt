package tech.merajobs.fragment.authScreens


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import tech.merajobs.R
import tech.merajobs.activity.CreateCVActivity
import tech.merajobs.activity.FindJobActivity
import tech.merajobs.databinding.FragmentChooseSignInMethodBinding
import tech.merajobs.networkModel.authNetworkPannel.AuthViewModel
import tech.merajobs.utility.AppConstant
import tech.merajobs.utility.BaseFragment
import tech.merajobs.utility.LinkedInConstants
import tech.merajobs.utility.SessionManager
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class ChooseSignInMethodFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentChooseSignInMethodBinding
    lateinit var sessionManager: SessionManager
    private lateinit var authViewModel: AuthViewModel

    private lateinit var linkedinAuthURLFull: String
    lateinit var linkedInDialog: Dialog
    lateinit var linkedinCode: String
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChooseSignInMethodBinding.inflate(
            LayoutInflater.from(requireActivity()), container, false
        )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        linkedinAuthURLFull = LinkedInConstants.buildLinkedInAuthUrl()


        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.data != null) {
                    try {
                        if (result.resultCode == Activity.RESULT_OK) {
                            Log.e("Result Code ", result.toString())
                            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                            val account: GoogleSignInAccount? =
                                task.getResult(ApiException::class.java)


                            if (account != null) {
                                if (isNetworkAvailable()) {
                                    Log.e("Google Sign In Email", account.email.toString())
                                    getDeviceToken(account.displayName!!, account.email!!)
                                } else {
                                    binding.tvProgressBar.visibility = View.GONE
                                    alertErrorDialog(getString(R.string.no_internet))
                                }
                            }
                        } else if (result.resultCode == Activity.RESULT_CANCELED) {
                            alertErrorDialog("Sign in failed !")
                            binding.tvProgressBar.visibility = View.GONE
                            Log.e("Social Login", "User canceled autocomplete")
                        }
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                        binding.tvProgressBar.visibility = View.GONE
                        Log.e("Auth Error", result.resultCode.toString())
                    }
                } else {
                    binding.tvProgressBar.visibility = View.GONE
                    alertErrorDialog("Gmail Not Found !")
                }

            }




        binding.btGoogleSignIn.setOnClickListener(this)
        binding.btLinkdenSignIn.setOnClickListener(this)
        binding.btOpenSignInScreen.setOnClickListener(this)
        binding.btOpenSignUpScreen.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btOpenSignInScreen -> {
                findNavController().navigate(R.id.openSignInScreen)
            }

            R.id.btOpenSignUpScreen -> {
                sessionManager.setSourceFragment(AppConstant.CHOOSE_SIGN_IN_FRAGMENT)
                findNavController().navigate(R.id.openSignUpScreen)
            }

            R.id.btLinkdenSignIn -> {
                setupLinkedinWebViewDialog(linkedinAuthURLFull)
            }

            R.id.btGoogleSignIn -> {
                binding.tvProgressBar.visibility = View.VISIBLE
                val mGoogleSignInClient = GoogleSignIn.getClient(
                    requireActivity(),
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build()
                )

                mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
                    val signInIntent: Intent = mGoogleSignInClient.signInIntent
                    activityResultLauncher.launch(signInIntent)
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupLinkedinWebViewDialog(url: String) {
        linkedInDialog = Dialog(requireContext())
        val webView = WebView(requireContext())
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.webViewClient = LinkedInWebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
        linkedInDialog.setContentView(webView)
        linkedInDialog.show()
    }

    @Suppress("OverridingDeprecatedMember")
    inner class LinkedInWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?,
        ): Boolean {
            if (request?.url.toString().startsWith(LinkedInConstants.REDIRECT_URI)) {
                handleUrl(request?.url.toString())
                // Close the dialog after getting the authorization code
                if (request?.url.toString().contains("?code=")) {
                    linkedInDialog.dismiss()
                }
                return true
            }
            return false
        }

        // For API 19 and below
        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.startsWith(LinkedInConstants.REDIRECT_URI)) {
                handleUrl(url)
                // Close the dialog after getting the authorization code
                if (url.contains("?code=")) {
                    linkedInDialog.dismiss()
                }
                return true
            }
            return false
        }

        // Check web view url for access token code or error
        private fun handleUrl(url: String) {
            val uri = Uri.parse(url)
            if (url.contains("code")) {
                linkedinCode = uri.getQueryParameter("code") ?: ""

                linkedInRequestForAccessToken()
            } else if (url.contains("error")) {
                val error = uri.getQueryParameter("error") ?: ""
                Log.e("Error: ", error)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun linkedInRequestForAccessToken() {
        GlobalScope.launch(Dispatchers.Default) {
            val grantType = "authorization_code"
            val postParams =
                "grant_type=" + grantType + "&code=" + linkedinCode + "&redirect_uri=" + LinkedInConstants.REDIRECT_URI + "&client_id=" + LinkedInConstants.CLIENT_ID + "&client_secret=" + LinkedInConstants.CLIENT_SECRET
            val url = URL(LinkedInConstants.TOKENURL)
            val httpsURLConnection =
                withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
            httpsURLConnection.requestMethod = "POST"
            httpsURLConnection.setRequestProperty(
                "Content-Type", "application/x-www-form-urlencoded"
            )
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = true
            val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
            withContext(Dispatchers.IO) {
                outputStreamWriter.write(postParams)
                outputStreamWriter.flush()
            }
            val response = httpsURLConnection.inputStream.bufferedReader()
                .use { it.readText() }  // defaults to UTF-8
            val jsonObject = JSONTokener(response).nextValue() as JSONObject
            val accessToken = jsonObject.getString("access_token") //The access token
            Log.d("accessToken is: ", accessToken)
            val expiresIn = jsonObject.getInt("expires_in") //When the access token expires
            Log.d("expires in: ", expiresIn.toString())
            withContext(Dispatchers.Main) {
                Log.e("jsonObject", jsonObject.toString())
                Log.e("Token",accessToken )
                fetchLinkedInUserProfile(accessToken)
            }
        }
    }

    private fun fetchLinkedInUserProfile(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
           /*     val tokenURLFull =
                    "https://api.linkedin.com/v2/me?projection=(id,firstName,lastName,profilePicture(displayImage~:playableStreams))"
         */     val tokenURLFull =
                    "https://api.linkedin.com/v2/me"
                val url = URL(tokenURLFull)
                val httpsURLConnection = url.openConnection() as HttpURLConnection
                httpsURLConnection.requestMethod = "GET"
                httpsURLConnection.setRequestProperty("Authorization", "Bearer $token")
                httpsURLConnection.connectTimeout = 15000
                httpsURLConnection.readTimeout = 15000

                val responseCode = httpsURLConnection.responseCode
                if (responseCode in 200..299) {
                    val response = httpsURLConnection.inputStream.bufferedReader().use(
                        BufferedReader::readText
                    )
                    Log.e("Response", response)
                    val linkedInProfileModel = Gson().fromJson(
                        response,
                        LinkedInConstants.LinkedInProfileModel::class.java
                    )

                    val linkedinId = linkedInProfileModel.id
                    val linkedinFirstName =
                        linkedInProfileModel.firstName.localized["en_US"] ?: "N/A"
                    val linkedinLastName =
                        linkedInProfileModel.lastName.localized["en_US"] ?: "N/A"
                    val linkedinProfilePic =
                        linkedInProfileModel.profilePicture.displayImage.elements.getOrNull(2)?.identifiers?.getOrNull(
                            0
                        )?.identifier ?: "N/A"

                    Log.w("linkedinId", linkedinId)
                    Log.w("linkedinFirstName", linkedinFirstName)
                    Log.w("linkedinLastName", linkedinLastName)
                    Log.w("linkedinProfilePic", linkedinProfilePic)

                    fetchLinkedInEmailAddress(
                        token,
                        linkedinFirstName,
                        linkedinLastName
                    )

                } else {
                    val errorResponse = httpsURLConnection.errorStream?.bufferedReader()?.use(
                        BufferedReader::readText
                    )
                    Log.e("Error Response", "HTTP $responseCode: $errorResponse")
                }
            } catch (e: Exception) {
                Log.e("Exception", "Error fetching LinkedIn profile", e)
            }
        }
    }

    private fun fetchLinkedInEmailAddress(
        token: String,
        linkedinFirstName: String,
        linkedinLastName: String,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tokenURLFull =
                    "https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))"

                val url = URL(tokenURLFull)
                val httpsURLConnection = url.openConnection() as HttpURLConnection
                httpsURLConnection.requestMethod = "GET"
                httpsURLConnection.setRequestProperty("Authorization", "Bearer $token")
                httpsURLConnection.connectTimeout = 15000
                httpsURLConnection.readTimeout = 15000

                val responseCode = httpsURLConnection.responseCode
                if (responseCode in 200..299) {
                    val response = httpsURLConnection.inputStream.bufferedReader().use(
                        BufferedReader::readText
                    )
                    val linkedInEmailModel =
                        Gson().fromJson(response, LinkedInConstants.LinkedInEmailModel::class.java)

                    withContext(Dispatchers.Main) {
                        val linkedinEmail =
                            linkedInEmailModel.elements.getOrNull(0)?.handle?.emailAddress ?: "N/A"
                        Log.d("LinkedIn Email: ", linkedinEmail)
                        socialLogIn(linkedinFirstName + linkedinLastName, linkedinEmail, token)


                    }
                } else {
                    val errorResponse = httpsURLConnection.errorStream?.bufferedReader()?.use(
                        BufferedReader::readText
                    )
                    Log.e("Error Response", "HTTP $responseCode: $errorResponse")
                }
            } catch (e: Exception) {
                Log.e("Exception", "Error fetching LinkedIn email address", e)
            }
        }
    }

    // This function is used for get device token of firebase
    private fun getDeviceToken(displayName: String, email: String) {

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (!isNetworkAvailable()) {
                    alertErrorDialog(getString(R.string.no_internet))
                } else {
                    socialLogIn(displayName, email, task.result!!)
                }
            } else {
                binding.tvProgressBar.visibility = View.GONE
                Log.e("Token Error", task.exception?.message.toString())
            }
        }

    }

    private fun socialLogIn(displayName: String, email: String, token: String) {
        Log.e("Name", displayName)
        Log.e("email", email)
        Log.e("token", token)
        lifecycleScope.launch {
            binding.tvProgressBar.visibility = View.VISIBLE
            authViewModel.socialLogIn(
                displayName,
                email,
                "google",
                token
            ).observe(viewLifecycleOwner) { jsonObject ->
                binding.tvProgressBar.visibility = View.GONE
                val jsonObjectData = checkResponse(jsonObject)

                if (jsonObjectData != null) {
                    try {
                        val data = jsonObjectData["data"].asJsonObject
                        sessionManager.setBearerToken(data["access_token"].asString)
                        sessionManager.setUserID(checkFieldSting(data["id"]).toInt())
                        Log.e("Token", sessionManager.getBearerToken())
                        if (checkFieldSting(data["profile_complete_percent"]).toInt() < 50) {
                            val intent = Intent(requireActivity(), CreateCVActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()

                        } else {
                            val intent = Intent(requireActivity(), FindJobActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    } catch (e: Exception) {
                        alertErrorDialog(e.toString())
                    }
                }

            }

        }

    }


}

