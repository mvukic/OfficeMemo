package ruazosa.hr.fer.officememo.Utils

import com.google.firebase.auth.FirebaseUser
import ruazosa.hr.fer.officememo.Model.User


object GlobalData {

    lateinit var user: User
    lateinit var firebaseUser: FirebaseUser
    var hasUser: Boolean = false
    var shouldRefreshHeader: Boolean = true

}