package ruazosa.hr.fer.officememo.View;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.support.design.widget.RxTextInputLayout;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import ruazosa.hr.fer.officememo.R;

public class NewDepartmentActivity extends AppCompatActivity {
    private static final int PROFILE_CODE = 0;
    private static final int COVER_CODE = 1;
    TextInputEditText name, shortName, about, location;
    ImageView profile, cover, pickProfile, pickCover;
    Uri currentProfile, currentCover;
    Button create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_department);
        instanceAllComponents();
        addListeners();

    }

    private void addListeners() {
        RxView.clicks(pickProfile).subscribe(o -> {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Profile Image ");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

            startActivityForResult(chooserIntent, PROFILE_CODE);
        });

        RxView.longClicks(pickProfile).subscribe(o -> {
            Toast.makeText(this, pickProfile.getContentDescription().toString()
                    , Toast.LENGTH_SHORT).show();
        });

        RxView.clicks(pickCover).subscribe(o -> {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Cover Image ");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

            startActivityForResult(chooserIntent, COVER_CODE);
        });

        RxView.longClicks(pickCover).subscribe(o -> {
            Toast.makeText(this, pickCover.getContentDescription().toString()
                    , Toast.LENGTH_SHORT).show();
        });

        RxTextView.textChanges(name).subscribe(charSequence -> {
            if(charSequence.toString().isEmpty())
                name.setError("Name can't be empty");
            else
                name.setError(null);
        });

        RxTextView.textChanges(shortName).subscribe(charSequence -> {
            if(charSequence.toString().isEmpty())
                shortName.setError("Short name can't be empty");
            else
                shortName.setError(null);
        });

        RxTextView.textChanges(location).subscribe(charSequence -> {
            if(charSequence.toString().isEmpty())
                location.setError("Location can't be empty");
            else
                location.setError(null);
        });

        RxTextView.textChanges(about).subscribe(charSequence -> {
            if(charSequence.toString().isEmpty())
                about.setError("About can't be empty");
            else
                about.setError(null);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            Uri image = data.getData();
            switch (requestCode){
                case PROFILE_CODE:
                    setImageToView(profile, image);
                    currentProfile = image;
                    break;
                case COVER_CODE:
                    setImageToView(cover, image);
                    currentCover = image;
                    break;
            }
        }
    }

    private void setImageToView(ImageView view, Uri image) {
        Picasso.with(this)
                .load(image)
                .resize(800, 800).centerInside()
                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imageBitmap = ((BitmapDrawable) view.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(50.f);
                        view.setImageDrawable(imageDrawable);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private void instanceAllComponents() {
        name = (TextInputEditText) findViewById(R.id.editTextNameDepartment);
        shortName = (TextInputEditText) findViewById(R.id.editTextShortNameDepartment);
        about = (TextInputEditText) findViewById(R.id.editTextAboutDepartment);
        location = (TextInputEditText) findViewById(R.id.editTextLocationDepartment);
        profile = (ImageView) findViewById(R.id.imageViewProfileDepartment);
        cover = (ImageView) findViewById(R.id.imageViewCoverDepartment);
        pickProfile = (ImageView) findViewById(R.id.imageViewProfilePickerDepartment);
        pickCover = (ImageView) findViewById(R.id.imageViewCoverPickerDepartment);
    }
}
