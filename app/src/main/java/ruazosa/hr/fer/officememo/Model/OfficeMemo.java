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
import java.util.Locale;

import ruazosa.hr.fer.officememo.R;

/**
 * Created by shimu on 30.6.2017..
 */

public class OfficeMemo {


    private static final SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.GERMANY);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.GERMANY);


    public static String dateToString(Date date) {
        return dateFormat.format(date);
    }

    public static String timeStampToString(Date timeStamp) {
        return timeStampFormat.format(timeStamp);
    }

    public static Date getDateFromString(String date) throws ParseException {
        return dateFormat.parse(date);
    }

    public static Date getTimeStampFromString(String timeStamp) throws ParseException {
        return timeStampFormat.parse(timeStamp);
    }

    public static String getUserUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static void setImageToView(Context context, ImageView view, Uri image, int targetWith, int targetHeight) {
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

    public static void setImageToViewFullCircle(Context context, ImageView view, Uri image, int targetWith, int targetHeight) {
        Picasso.with(context)
                .load(image)
                .resize(targetWith, targetHeight)
                .centerInside()
                .placeholder(R.drawable.progress_animation)
                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imageBitmap = ((BitmapDrawable) view.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), imageBitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(Math.max(view.getMaxWidth(), view.getMaxHeight()) / 2.f);
                        view.setImageDrawable(imageDrawable);
                    }

                    @Override
                    public void onError() {
                        Picasso.with(context)
                                .load(R.drawable.placeholder)
                                .resize(targetWith, targetHeight)
                                .centerInside()
                                .into(view, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Bitmap imageBitmap = ((BitmapDrawable) view.getDrawable()).getBitmap();
                                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), imageBitmap);
                                        imageDrawable.setCircular(true);
                                        imageDrawable.setCornerRadius(Math.max(view.getMaxWidth(), view.getMaxHeight()) / 2.f);
                                        view.setImageDrawable(imageDrawable);
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });


                    }
                });
    }


}
