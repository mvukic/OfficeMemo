package ruazosa.hr.fer.officememo

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.R.attr.password
import android.app.Activity
import android.content.Context
import android.support.v4.app.FragmentActivity
import com.github.b3er.rxfirebase.auth.authStateChanges
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crash.FirebaseCrash
import org.jetbrains.anko.startActivity
import ruazosa.hr.fer.officememo.View.LoginActivity


open class BaseActivity : AppCompatActivity(),GoogleApiClient.OnConnectionFailedListener  {

    override fun onConnectionFailed(p0: ConnectionResult) {
        FirebaseCrash.log("BaseActivity: GoogleApi connection failed.")
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    fun signOut(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        val mAuth = FirebaseAuth.getInstance()
        val mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
        mGoogleApiClient.connect()
        mGoogleApiClient.registerConnectionCallbacks(object:GoogleApiClient.ConnectionCallbacks{
            override fun onConnected(p0: Bundle?) {
                mAuth.signOut()
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback {
                    startActivity<LoginActivity>()
                }
            }

            override fun onConnectionSuspended(p0: Int) {
            }
        })

    }

}