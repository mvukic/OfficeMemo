package ruazosa.hr.fer.officememo.Utils

import ruazosa.hr.fer.officememo.Model.User


object GlobalData {

    lateinit var user: User
    var hasUser: Boolean = false
    var shouldRefreshHeader: Boolean = true

}