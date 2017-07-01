package ruazosa.hr.fer.officememo.View

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.widget.EditText
import com.github.b3er.rxfirebase.auth.rxGetCurrentUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crash.FirebaseCrash
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.enabled
import com.jakewharton.rxbinding2.widget.textChanges
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.reactivex.Observable
import io.reactivex.SingleObserver
import io.reactivex.rxkotlin.Observables
import org.jetbrains.anko.indeterminateProgressDialog
import ruazosa.hr.fer.officememo.Model.FirebaseHandler
import ruazosa.hr.fer.officememo.Model.User
import ruazosa.hr.fer.officememo.R
import java.util.*

class LoginAdditionalActivity : RxAppCompatActivity() {

    lateinit var user:FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_additional)
        val indefProgress = indeterminateProgressDialog(message = "Please wait a bit…", title = "Fetching user.")

        val fab = findViewById(R.id.fabSaveUser) as FloatingActionButton
        fab.isEnabled = false
        val firstNameInput = findViewById(R.id.editTextUserFName) as EditText
        val lastNameInput = findViewById(R.id.editTextUserLName) as EditText
        val aboutInput = findViewById(R.id.editTextUserAbout) as EditText
        val emailInput = findViewById(R.id.editTextUserEmail) as EditText

        fab.clicks().compose(bindToLifecycle()).subscribe({
            val u = User(
                    uid = user.uid,
                    aboutMe = lastNameInput.text.toString(),
                    coverUrl = "",
                    email = emailInput.text.toString(),
                    name = firstNameInput.text.toString(),
                    lastName = lastNameInput.text.toString(),
                    dateOfRegistration = Date().toString(),
                    dateOfBirth = Date().toString(),
                    token = FirebaseInstanceId.getInstance().getToken().toString(),
                    location = "",
                    profileUrl = user.photoUrl.toString()
            )
            FirebaseHandler.pushUser(u).compose(bindToLifecycle()).subscribe({key->
                println("Key is: ${key}")
                Snackbar.make(window.decorView,"Saved user ${user.displayName}",Snackbar.LENGTH_SHORT).show()
            },{
                Snackbar.make(window.decorView,"Error happened.",Snackbar.LENGTH_SHORT).show()
            })
        })

        FirebaseAuth.getInstance().rxGetCurrentUser().compose(bindToLifecycle())
                .subscribe({
                    user = it
                    firstNameInput.setText(it.displayName)
                    lastNameInput.setText(it.displayName)
                    emailInput.setText(it.email)
                    indefProgress.hide()
                },{error->
                    FirebaseCrash.log("LoginAdditionActivity: Error getting current user from firebase.")
                })

        val zipped = Observables.combineLatest(firstNameInput.textChanges(),
                lastNameInput.textChanges(), aboutInput.textChanges(),
                emailInput.textChanges()) {a,b,c,d -> listOf(a.toString(),b.toString(),c.toString(),d.toString())}

        zipped.compose(bindToLifecycle())
                .subscribe { (fname, lname, about, email) ->
                    if (fname.isEmpty()) {
                        firstNameInput.error = getString(R.string.empty_fname)
                    } else {
                        firstNameInput.error = null
                    }
                    if (lname.isEmpty()) {
                        lastNameInput.error = getString(R.string.empty_lname)
                    } else {
                        lastNameInput.error = null
                    }
                    if (about.isEmpty()) {
                        aboutInput.error = getString(R.string.empty_about)
                    } else{
                        aboutInput.error = null
                    }
                    if (email.isEmpty()) {
                        emailInput.error = getString(R.string.empty_email)
                    } else {
                        emailInput.error = null
                    }
                    fab.isEnabled = !fname.isEmpty() && !lname.isEmpty() && !about.isEmpty() && !email.isEmpty()
        }

    }
}