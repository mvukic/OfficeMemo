package ruazosa.hr.fer.officememo.View

import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.widget.*
import com.github.b3er.rxfirebase.auth.rxGetCurrentUser
import com.github.b3er.rxfirebase.database.data
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crash.FirebaseCrash
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.reactivex.rxkotlin.Observables
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.startActivity
import ruazosa.hr.fer.officememo.Controller.GlobalData
import ruazosa.hr.fer.officememo.Model.FirebaseHandler
import ruazosa.hr.fer.officememo.Model.OfficeMemo
import ruazosa.hr.fer.officememo.Model.User
import ruazosa.hr.fer.officememo.R
import java.util.*

class LoginAdditionalActivity : RxAppCompatActivity() {

    lateinit var user:FirebaseUser
    private val PROFILE_CODE = 0
    private val COVER_CODE = 1
    lateinit var currentProfile: Uri
    lateinit var currentCover:Uri
    lateinit var imageViewProfile: ImageView
    lateinit var imageViewCover: ImageView
    var dob: Date = Date()
    lateinit var existingUser: User
    lateinit var indefProgress: ProgressDialog
    var currentProfileUpdated = false
    var currentCoverUpdated = false
    var firstTimeOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_additional)
        indefProgress = indeterminateProgressDialog(message = "Please wait a bit…", title = "Fetching user.")

        val buttonCreateUser = findViewById(R.id.buttonCreateUser) as Button
        buttonCreateUser.isEnabled = false

        val firstNameInput = findViewById(R.id.editTextUserFName) as EditText
        val lastNameInput = findViewById(R.id.editTextUserLName) as EditText
        val aboutInput = findViewById(R.id.editTextUserAbout) as EditText
        val emailInput = findViewById(R.id.editTextUserEmail) as EditText
        val locationInput = findViewById(R.id.editTextUserLocation) as EditText
        val imageProfileViewButton = findViewById(R.id.imageViewProfileUserPicker) as ImageView
        val imageCoverViewButton = findViewById(R.id.imageViewCoverUserPicker) as ImageView

        imageViewProfile = findViewById(R.id.imageViewProfileUser) as ImageView
        imageViewCover = findViewById(R.id.imageViewCoverUser) as ImageView
        firstTimeOpened = intent.getBooleanExtra("first_time",false)


        imageProfileViewButton.clicks().compose(bindToLifecycle())
                .subscribe({v->
                    val getIntent = Intent(Intent.ACTION_GET_CONTENT)
                    getIntent.type = "image/*"
                    val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    pickIntent.type = "image/*"
                    val chooserIntent = Intent.createChooser(getIntent, "Select Profile Image ")
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
                    startActivityForResult(chooserIntent, PROFILE_CODE)
                })

        imageCoverViewButton.clicks().compose(bindToLifecycle())
                .subscribe({v->
                    val getIntent = Intent(Intent.ACTION_GET_CONTENT)
                    getIntent.type = "image/*"
                    val pickIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    pickIntent.type = "image/*"
                    val chooserIntent = Intent.createChooser(getIntent, "Select Cover Image ")
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
                    startActivityForResult(chooserIntent, COVER_CODE)
                })

        buttonCreateUser.clicks().compose(bindToLifecycle()).subscribe({
            val u = User(
                    uid = user.uid,
                    aboutMe = aboutInput.text.toString(),
                    coverUrl = currentCover.toString(),
                    email = emailInput.text.toString(),
                    name = firstNameInput.text.toString(),
                    lastName = lastNameInput.text.toString(),
                    dateOfRegistration = Date().toString(),
                    dateOfBirth = OfficeMemo.dateToString(dob),
                    token = FirebaseInstanceId.getInstance().token.toString(),
                    location = locationInput.text.toString(),
                    profileUrl = currentProfile.toString(),
                    subscriptions = listOf()
            )
            // Save previous subscriptions
            if(!firstTimeOpened){
                u.subscriptions = existingUser.subscriptions
            }
            //Push images to storage
            indefProgress.setTitle("Updating profile")
            indefProgress.show()

            if(firstTimeOpened){
                currentProfileUpdated = true
                currentCoverUpdated = true
            }
            if(currentProfileUpdated && currentCoverUpdated){
                val a1 = FirebaseHandler.pushUriToStorage(
                        currentProfile,
                        FirebaseStorage.getInstance().getReference(user.uid).child("profileImage.jpg"))
                        .toObservable()

                val a2 = FirebaseHandler.pushUriToStorage(
                        currentCover,
                        FirebaseStorage.getInstance().getReference(user.uid).child("coverImage.jpg"))
                        .toObservable()
                Observables.zip(a1,a2,{a,b -> listOf(a,b) }).compose(bindToLifecycle())
                        .subscribe({(profileUriTask,coverUriTask)->
                            u.coverUrl = coverUriTask.downloadUrl.toString()
                            u.profileUrl = profileUriTask.downloadUrl.toString()
                            saveUser(u)
                        })
            }else if(currentCoverUpdated){
                FirebaseHandler.pushUriToStorage(
                        currentCover,
                        FirebaseStorage.getInstance().getReference(user.uid).child("coverImage.jpg"))
                        .toObservable().compose(bindToLifecycle()).subscribe({
                    u.coverUrl = it.downloadUrl.toString()
                    u.profileUrl = currentProfile.toString()
                    saveUser(u)
                })
            }else if(currentProfileUpdated){
                FirebaseHandler.pushUriToStorage(
                        currentProfile,
                        FirebaseStorage.getInstance().getReference(user.uid).child("profileImage.jpg"))
                        .toObservable().compose(bindToLifecycle()).subscribe({
                    u.coverUrl = currentCover.toString()
                    u.profileUrl = it.downloadUrl.toString()
                    saveUser(u)
                })
            }else{
                u.coverUrl = currentCover.toString()
                u.profileUrl = currentProfile.toString()
                saveUser(u)
            }

        })

        FirebaseAuth.getInstance().rxGetCurrentUser().compose(bindToLifecycle())
                .subscribe({
                    user = it
                    if(firstTimeOpened){
                        val flnames = it.displayName.toString().split(" ").toMutableList()
                        if(flnames.size > 1){
                            firstNameInput.setText(flnames[0])
                            flnames.removeAt(0)
                            lastNameInput.setText(flnames.joinToString(" "))
                        }else{
                            firstNameInput.setText(it.displayName)
                            lastNameInput.setText("")
                        }
                        emailInput.setText(it.email)
                        currentProfile = OfficeMemo.placeholderImage
                        currentCover = OfficeMemo.placeholderImage
                        OfficeMemo.setImageToView(this, imageViewProfile)
                        OfficeMemo.setImageToView(this,imageViewCover)
                        indefProgress.hide()
                    }else{
                        val ref = FirebaseDatabase.getInstance().getReference("users").child(it.uid)
//                        ref.data().compose(bindToLifecycle()).subscribe({
//                            val u = it.getValue(User::class.java)
                            val u = GlobalData.user
                            existingUser = u
                            firstNameInput.setText(u.name)
                            lastNameInput.setText(u.lastName)
                            emailInput.setText(u.email)
                            locationInput.setText(u.location)
                            aboutInput.setText(u.aboutMe)
                            if(u.coverUrl.isEmpty()) {
                                currentCover = OfficeMemo.placeholderImage
                            }else{
                                currentCover = Uri.parse(u.coverUrl)
                            }
                            if(u.profileUrl.isEmpty()) {
                                currentProfile = OfficeMemo.placeholderImage
                            }else{
                                currentProfile = Uri.parse(u.profileUrl)
                            }
                            OfficeMemo.setImageToView(this,imageViewProfile, currentProfile)
                            OfficeMemo.setImageToView(this,imageViewCover, currentCover)
                            indefProgress.hide()
//                        },{
//                            FirebaseCrash.log("LoginAdditionActivity: Error getting user from database.")
//                            indefProgress.hide()
//                        })
                    }
                },{error->
                    FirebaseCrash.log("LoginAdditionActivity: Error getting current user from firebase.")
                })

        val zipped = Observables.combineLatest(firstNameInput.textChanges(),
                lastNameInput.textChanges(), aboutInput.textChanges(),
                emailInput.textChanges(),locationInput.textChanges())
        {a,b,c,d,e -> listOf(a.toString(),b.toString(),c.toString(),d.toString(),e.toString())}

        zipped.compose(bindToLifecycle())
                .subscribe { (fname, lname, about, email, location) ->
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
                    if (location.isEmpty()) {
                        locationInput.error = getString(R.string.empty_location)
                    } else {
                        locationInput.error = null
                    }
                    buttonCreateUser.isEnabled = !fname.isEmpty() && !lname.isEmpty() && !about.isEmpty() && !email.isEmpty() && !location.isEmpty()
        }

    }

    fun saveUser(u:User){
        FirebaseHandler.pushUser(u).compose(bindToLifecycle()).subscribe({key->
            indefProgress.hide()
            if(firstTimeOpened) startActivity<MainActivity>()
            else Snackbar.make(window.decorView,"Saved user ${u.name}",Snackbar.LENGTH_SHORT).show()
            GlobalData.user = u
            GlobalData.hasUser = true
        },{
            indefProgress.hide()
            FirebaseCrash.log("LoginAdditionalActivity: Error while saving a user.")
            Snackbar.make(window.decorView,"Error happened.",Snackbar.LENGTH_SHORT).show()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            val image = data.data
            when (requestCode) {
                PROFILE_CODE -> {
                    OfficeMemo.setImageToView(this, imageViewProfile, image)
                    currentProfile = image
                    currentProfileUpdated = true
                }
                COVER_CODE -> {
                    OfficeMemo.setImageToView(this, imageViewCover, image)
                    currentCover = image
                    currentCoverUpdated = true
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        indefProgress.dismiss()
    }
}