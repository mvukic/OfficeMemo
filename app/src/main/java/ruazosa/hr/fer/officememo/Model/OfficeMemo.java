package ruazosa.hr.fer.officememo.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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

    public static void setImageToView(Context context, ImageView view, Uri image,int targetWith , int targetHeight) {
        Picasso.with(context)
                .load(image)
                .resize(targetWith, targetHeight).centerInside()
                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imageBitmap = ((BitmapDrawable) view.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), imageBitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(50.f);
                        view.setImageDrawable(imageDrawable);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }
    public static void setImageToView(Context context, ImageView view, Uri image) {
        Picasso.with(context)
                .load(image)
                .resize(800, 800).centerInside()
                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imageBitmap = ((BitmapDrawable) view.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), imageBitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(50.f);
                        view.setImageDrawable(imageDrawable);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }



}
