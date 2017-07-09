package ruazosa.hr.fer.officememo.View

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.github.b3er.rxfirebase.auth.rxGetCurrentUser
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.view.clicks
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.startActivity
import ruazosa.hr.fer.officememo.R
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.github.b3er.rxfirebase.database.data
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ruazosa.hr.fer.officememo.Utils.GlobalData
import ruazosa.hr.fer.officememo.Model.User


class LoginActivity : RxAppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private val RC_SIGN_IN = 9001
    lateinit var mGoogleApiClient: GoogleApiClient
    lateinit var mAuth: FirebaseAuth
    lateinit var indefProgress:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signInButton = findViewById(R.id.sign_in_button) as SignInButton;
        signInButton.setSize(SignInButton.SIZE_WIDE)

        signInButton.clicks().compose(bindToLifecycle()).subscribe {
            signIn()
        }

        indefProgress = indeterminateProgressDialog(message = "Please wait a bit…", title = "Fetching user.")

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mAuth = FirebaseAuth.getInstance()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        FirebaseAuth.getInstance().rxGetCurrentUser()
                .compose(bindToLifecycle())
                .subscribe({user->
                    //onSuccess
                    val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.uid)
                    ref.data().compose(bindToLifecycle())
                            .subscribe({
                                indefProgress.dismiss()
                                if (it.exists()) {
                                    val u: User = it.getValue(User::class.java)
                                    GlobalData.user = u
                                    startActivity<MainActivity>("has_user" to true)
                                    finish()
                                }
                                else startActivity<LoginAdditionalActivity>("first_time" to true)
                                finish()
                            }) {
                                println("Error")
                            }
                },{error->
                    // onError
                    println("Error")
                },{
                    // onComplete
                    indefProgress.hide()
                })
    }

    fun signIn(){
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener(this){ task ->
            if (task.isSuccessful) {
                val user = mAuth.currentUser
                indefProgress.show()
                val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(user?.uid)
                ref.data().compose(bindToLifecycle())
                        .subscribe({
                            indefProgress.hide()
                            if (it.exists()) {
                                println("Saving to GlobalData.")
                                val u: User = it.getValue(User::class.java)
                                GlobalData.user = u
                                GlobalData.hasUser = true
                                startActivity<MainActivity>()
                            }
                            else startActivity<LoginAdditionalActivity>("first_time" to true)
                            finish()
                        }) {
                            println("Error")
                        }
            } else {
                indefProgress.hide()
                Snackbar.make(this.currentFocus,"Authentication failed.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        println("Failed connection.")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                val account = result.signInAccount
                firebaseAuthWithGoogle(account)
            } else {
                Snackbar.make(this.currentFocus,"Authentication failed.", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        indefProgress.dismiss()
    }
}
