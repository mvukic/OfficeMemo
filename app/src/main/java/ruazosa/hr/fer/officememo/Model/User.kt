package ruazosa.hr.fer.officememo.Model

import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by shimu on 29.6.2017..
 */

data class User(
        var uid: String = "",
        var name: String = "",
        var lastName: String = "",
        var dateOfBirth: String = "",
        var token: String = "",
        var profileUrl: String = "",
        var coverUrl: String = "",
        var aboutMe: String = "",
        var location: String = "",
        var email: String = "",
        var dateOfRegistration: String = "",
        var subscriptions: List<String> = ArrayList()
        )