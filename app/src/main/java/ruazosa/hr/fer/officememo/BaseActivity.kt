package ruazosa.hr.fer.officememo

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.R.attr.password
import com.github.b3er.rxfirebase.auth.authStateChanges
import com.google.firebase.auth.FirebaseAuth
import durdinapps.rxfirebase2.RxFirebaseAuth



/**
 * Created by matija on 30.06.17..
 */
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        FirebaseAuth.getInstance().authStateChanges()
                .subscribe({
                    println("State changed")
                }, {
                    println("Error occured")
                })
    }
}