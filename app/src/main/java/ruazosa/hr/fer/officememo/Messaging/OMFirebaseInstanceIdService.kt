package ruazosa.hr.fer.officememo.Messaging

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService


class OMFirebaseInstanceIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh(){
        val refreshedToken = FirebaseInstanceId.getInstance().token
        println("Refreshed token: ${refreshedToken}")

    }
}