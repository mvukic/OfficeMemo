package ruazosa.hr.fer.officememo.View;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.jakewharton.rxbinding2.support.design.widget.RxTextInputLayout;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import io.reactivex.Observable;
import ruazosa.hr.fer.officememo.Model.Department;
import ruazosa.hr.fer.officememo.Model.FirebaseHandler;
import ruazosa.hr.fer.officememo.Model.OfficeMemo;
import ruazosa.hr.fer.officememo.R;

public class NewDepartmentActivity extends AppCompatActivity {
    private static final int PROFILE_CODE = 0;
    private static final int COVER_CODE = 1;
    TextInputEditText name, shortName, about, location;
    ImageView profile, cover, pickProfile, pickCover;
    Uri currentProfile, currentCover;
    Button create;
    Department newDepartment = new Department();
    boolean coverdone = false, profiledone = false;
    ProgressDialog dialog;
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
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

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
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

            startActivityForResult(chooserIntent, COVER_CODE);
        });

        RxView.longClicks(pickCover).subscribe(o -> {
            Toast.makeText(this, pickCover.getContentDescription().toString()
                    , Toast.LENGTH_SHORT).show();
        });

        RxTextView.textChanges(name).subscribe(charSequence -> {
            if (charSequence.toString().isEmpty())
                name.setError("Name can't be empty");
            else
                name.setError(null);
        });

        RxTextView.textChanges(shortName).subscribe(charSequence -> {
            if (charSequence.toString().isEmpty())
                shortName.setError("Short name can't be empty");
            else
                shortName.setError(null);
        });

        RxTextView.textChanges(location).subscribe(charSequence -> {
            if (charSequence.toString().isEmpty())
                location.setError("Location can't be empty");
            else
                location.setError(null);
        });

        RxTextView.textChanges(about).subscribe(charSequence -> {
            if (charSequence.toString().isEmpty())
                about.setError("About can't be empty");
            else
                about.setError(null);
        });

        RxView.clicks(create).subscribe(o -> {
            if (!checkValuesOfDepartment()) {
                Snackbar.make(getCurrentFocus(), R.string.check_your_values, Snackbar.LENGTH_LONG).show();
                return;
            }
            dialog = ProgressDialog.show(NewDepartmentActivity.this, "",
                    "Creating department. Please wait...", true);
            newDepartment.setName(name.getText().toString());
            newDepartment.setShortName(shortName.getText().toString());
            newDepartment.setAbout(about.getText().toString());
            newDepartment.setLocation(location.getText().toString());
            if (currentProfile != null) {
                Observable<UploadTask.TaskSnapshot> a1 = FirebaseHandler.pushUriToStorage(
                        currentProfile,
                        FirebaseStorage.getInstance().getReference("departments")
                                .child(FirebaseDatabase.getInstance().getReference("departmentimages")
                                        .push().getKey())).toObservable();

                a1.subscribe(snapshot -> {
                    newDepartment.setImageUrl(snapshot.getDownloadUrl().toString());
                    profiledone = true;
                    tryPush();
                });
            } else profiledone = true;
            if (currentCover != null) {
                Observable<UploadTask.TaskSnapshot> a1 = FirebaseHandler.pushUriToStorage(
                        currentCover,
                        FirebaseStorage.getInstance().getReference("departments")
                                .child(FirebaseDatabase.getInstance().getReference("departmentimages")
                                        .push().getKey())).toObservable();

                a1.subscribe(snapshot -> {
                    newDepartment.setCoverUrl(snapshot.getDownloadUrl().toString());
                    coverdone = true;
                    tryPush();
                });
            } else coverdone = true;
            if (profiledone && coverdone) {
                FirebaseHandler.pushDepartment(newDepartment);
                dialog.dismiss();
                finish();
            }
        });

        RxView.longClicks(profile).subscribe(o -> {
            profile.setImageResource(R.drawable.gallery);
            currentProfile = null;
            Snackbar.make(getCurrentFocus(), R.string.removed_image, Snackbar.LENGTH_SHORT).show();

        });

        RxView.longClicks(cover).subscribe(o -> {
            cover.setImageResource(R.drawable.gallery);
            currentCover = null;
            Snackbar.make(getCurrentFocus(), R.string.removed_image, Snackbar.LENGTH_SHORT).show();

        });
    }

    private void tryPush() {
        if (profiledone && coverdone) {
            FirebaseHandler.pushDepartment(newDepartment);
            dialog.dismiss();
            finish();
        }
    }

    private boolean checkValuesOfDepartment() {
        return !(name.getText().toString().isEmpty() || shortName.getText().toString().isEmpty()
                || about.getText().toString().isEmpty() || location.getText().toString().isEmpty()
                );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri image = data.getData();
                switch (requestCode) {
                    case PROFILE_CODE:
                        OfficeMemo.setImageToView(this, profile, image);
                        currentProfile = image;
                        break;
                    case COVER_CODE:
                        OfficeMemo.setImageToView(this, cover, image);
                        currentCover = image;
                        break;
                }
            }
        }
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
        create = (Button) findViewById(R.id.buttonCreateDepartment);
    }
}
