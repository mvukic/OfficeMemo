package ruazosa.hr.fer.officememo.Messaging

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.auth.FirebaseAuth
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import ruazosa.hr.fer.officememo.View.MainActivity
import android.content.Intent
import android.support.v7.app.NotificationCompat
import io.reactivex.rxkotlin.toSingle
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import ruazosa.hr.fer.officememo.R
import ruazosa.hr.fer.officememo.Utils.GlobalData
import android.media.RingtoneManager




class OMFirebaseMessagingService: FirebaseMessagingService() {

    private val TAG = "FB_MESSSAGE"
    private var showNotification = true
    lateinit var map:Map<String,Any>

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        if(remoteMessage == null) return;
        Log.d(TAG, "From: " + remoteMessage.from)
        Log.e("MSG","Received")
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            map = remoteMessage.data

            val postId = map["pid"]
            val departmentId = map["did"]
            val userId = map["uid"]
            val commentPosted = map["commentPosted"]

            showNotification = if(commentPosted == "yes") true else GlobalData.user.uid != userId
        }
        if (showNotification) {
            val resultIntent = intentFor<MainActivity>(
                    "pid" to map["pid"],
                    "did" to map["did"],
                    "uid" to map["uid"],
                    "commentPosted" to map["commentPosted"]
            ).singleTop()
            Log.e("MSG","IN")
            val resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val mBuilder = NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_sms_black_24dp)
                    .setContentTitle(remoteMessage.notification.title)
                    .setContentText(remoteMessage.notification.body)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .setSound(alarmSound)
            if(map["commentPosted"] == "yes"){
                mBuilder.setVibrate(longArrayOf(500,500))
            }else{
                mBuilder.setVibrate(longArrayOf(1000,1000))
            }

            val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotifyMgr.notify(1889, mBuilder.build())
        }
    }
}