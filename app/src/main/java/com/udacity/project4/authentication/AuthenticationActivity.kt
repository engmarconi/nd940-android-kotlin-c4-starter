package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.ReminderDescriptionActivity
import com.udacity.project4.locationreminders.RemindersActivity


/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    companion object {
        const val SIGN_IN_RESULT_CODE = 1001
    }

    private val viewModel by viewModels<AuthViewModel>()
    lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        binding = DataBindingUtil.setContentView<ActivityAuthenticationBinding>(
            this,
            R.layout.activity_authentication
        )
        observeAuthenticationState()
        binding.lifecycleOwner = this
    }

    private fun observeAuthenticationState() {
        viewModel.authenticationState.observe(this, Observer { authenticationState ->
            when (authenticationState) {
                AuthViewModel.AuthenticationState.AUTHENTICATED -> {
                    binding.loginButton.visibility = View.GONE
                    redirectToReminderActivity()
                }
                else -> {
                    binding.loginButton.visibility = View.VISIBLE
                    binding.loginButton.setOnClickListener {
                        launchSignInFlow()
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in user.
                Toast.makeText(
                    this,
                    "Welcome ${FirebaseAuth.getInstance().currentUser?.displayName}!",
                    Toast.LENGTH_LONG
                ).show()
                redirectToReminderActivity()
            } else {
                Toast.makeText(
                    this,
                    "Fail to sign in!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val authMethodPickerLayout = AuthMethodPickerLayout.Builder(R.layout.custom_auth_layout)
            .setGoogleButtonId(R.id.googleSignInButton)
            .setEmailButtonId(R.id.emailSignInButton)
            .build()

        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setAuthMethodPickerLayout(authMethodPickerLayout)
                .build(), SIGN_IN_RESULT_CODE
        )
    }

    private fun redirectToReminderActivity() {
        this.finish()
        val intent = Intent(this, RemindersActivity::class.java)
        startActivity(intent)
    }
}
