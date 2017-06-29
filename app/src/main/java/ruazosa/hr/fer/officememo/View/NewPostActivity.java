package ruazosa.hr.fer.officememo.View;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;


import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;


import java.io.FileNotFoundException;
import java.io.InputStream;

import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import ruazosa.hr.fer.officememo.Model.Department;
import ruazosa.hr.fer.officememo.Model.FirebaseHandler;
import ruazosa.hr.fer.officememo.Model.ObservableList;
import ruazosa.hr.fer.officememo.Model.Post;
import ruazosa.hr.fer.officememo.R;

// TODO: 29.6.2017. add description of image using Google Vision API
public class NewPostActivity extends AppCompatActivity implements IPickResult {
    private static final int PICK_IMAGE = 13;
    ObservableList<Department> listOfDepartment = new ObservableList<>();
    private View currentView;
    private Post newPost = new Post();
    Spinner spinner;
    Button postButton;
    ArrayAdapter adapter;
    TextInputEditText title;
    TextInputEditText content;
    ImageView selectImage;
    ImageView selectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        //initialize

        spinner = (Spinner)findViewById(R.id.spinnerDepartments);
        adapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, listOfDepartment.getList());
        spinner.setAdapter(adapter);
        postButton = (Button)findViewById(R.id.buttonPost);
        title=(TextInputEditText)findViewById(R.id.editTextTitle);
        content=(TextInputEditText)findViewById(R.id.editTextContent);
        selectImage= (ImageView)findViewById(R.id.imageViewImage);
        selectedImage= (ImageView)findViewById(R.id.imageView);
        addListeners();

    }

    private void addListeners() {

        RxFirebaseDatabase.observeSingleValueEvent(FirebaseDatabase.getInstance().getReference().child("departments"),
                DataSnapshotMapper.listOf(Department.class))
                .subscribe(departments -> {
                    listOfDepartment.clear();
                    departments.forEach(department -> listOfDepartment.add(department));
                    adapter.notifyDataSetChanged();
                });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                newPost.setDid(((Department)parent.getItemAtPosition(position)).getDid());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                newPost.setDid("");
            }
        });
        RxTextView.textChanges(title).subscribe(charSequence -> {
            if(charSequence.toString().isEmpty()){
                title.setError(getString(R.string.empty_title));
            }
            else
                title.setError(null);
        });
        RxTextView.textChanges(content).subscribe(charSequence -> {
            if(charSequence.toString().isEmpty()){
                content.setError(getString(R.string.empty_content));
            }
            else
                content.setError(null);
        });
        RxView.clicks(postButton).subscribe(o -> {
           if(newPost.getDid().isEmpty()|| title.getText().toString().isEmpty()|| content.getText().toString().isEmpty()) {
               Snackbar.make(getCurrentFocus(), R.string.check_your_values,Snackbar.LENGTH_LONG).show();

           }
           else {
               newPost.setContent(content.getText().toString());
               newPost.setTitle(title.getText().toString());
               newPost.setUid("sdfasd4");
               newPost.setTimeStamp("2017-06-29 22-38");

               FirebaseHandler.pushPost(newPost);


           }
        });
        RxView.clicks(selectImage).subscribe(o -> {
//            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
//            getIntent.setType("image/*");
//
//            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            pickIntent.setType("image/*");
//
//            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
//            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent, takePicture});
//
//
//            startActivityForResult(chooserIntent, PICK_IMAGE);

            PickImageDialog.build(new PickSetup()
                    .setCancelText("Cancel")
                    .setFlip(true)
                    .setMaxSize(500).setSystemDialog(true).setButtonOrientation(LinearLayoutCompat.VERTICAL)
                    .setPickTypes(EPickType.CAMERA, EPickType.GALLERY).setGalleryButtonText("Choose from gallery")).show(this);


        });


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }

               // InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
            Picasso.with(this)
                    .load(data.getData())
                    .resize(800, 800).centerInside()
                    .into(selectedImage);


            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    @Override
    public void onPickResult(PickResult pickResult) {
        Picasso.with(this)
                .load(pickResult.getUri())
                .resize(800, 800).centerInside()
                .into(selectedImage);
    }
}
