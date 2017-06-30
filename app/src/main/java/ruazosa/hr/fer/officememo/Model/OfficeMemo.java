package ruazosa.hr.fer.officememo.Model;

import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shimu on 30.6.2017..
 */

public class OfficeMemo {


    public static final SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");


    public static String dateToString(Date date){
        return dateFormat.format(date);
    }
    public static String timeStampToString(Date timeStamp){
        return timeStampFormat.format(timeStamp);
    }
    public static Date getDateFromString(String date) throws ParseException {
        return dateFormat.parse(date);
    }
    public static Date getTimeStampFromString(String timeStamp) throws ParseException {
        return timeStampFormat.parse(timeStamp);
    }
    public static String getUserUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }



}
