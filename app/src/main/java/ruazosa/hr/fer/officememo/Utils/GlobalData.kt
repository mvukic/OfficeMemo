package ruazosa.hr.fer.officememo.Utils

import com.google.firebase.auth.FirebaseUser
import ruazosa.hr.fer.officememo.Model.User


object GlobalData {

    // User class
    lateinit var user: User

    // Firebase user class
    lateinit var firebaseUser: FirebaseUser

    // Indication if user is saved here (must be true. always.)
    var hasUser: Boolean = false

    // Indication that header info should be refreshed in navigation drawer
    var shouldRefreshHeader: Boolean = true

}