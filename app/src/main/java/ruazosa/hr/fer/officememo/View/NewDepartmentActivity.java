package ruazosa.hr.fer.officememo.View;

import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;

import ruazosa.hr.fer.officememo.R;

public class NewDepartmentActivity extends AppCompatActivity {
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
        RxView.longClicks(pickProfile).subscribe(o -> {
            Toast.makeText(this, pickProfile.getContentDescription().toString()
                    , Toast.LENGTH_SHORT).show();
        });
        RxView.longClicks(pickCover).subscribe(o -> {
            Toast.makeText(this, pickCover.getContentDescription().toString()
                    , Toast.LENGTH_SHORT).show();
        });
    }

    private void instanceAllComponents() {
        name = (TextInputEditText) findViewById(R.id.editTextName);
        shortName = (TextInputEditText) findViewById(R.id.editTextShortName);
        about = (TextInputEditText) findViewById(R.id.editTextAbout);
        location = (TextInputEditText) findViewById(R.id.editTextLocation);
        profile = (ImageView) findViewById(R.id.imageViewProfile);
        cover = (ImageView) findViewById(R.id.imageViewCover);
        pickProfile = (ImageView) findViewById(R.id.imageViewProfilePicker);
        pickCover = (ImageView) findViewById(R.id.imageViewCoverPicker);

    }
}
