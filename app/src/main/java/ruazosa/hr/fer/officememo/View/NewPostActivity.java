package ruazosa.hr.fer.officememo.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.common.io.BaseEncoding;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import ruazosa.hr.fer.officememo.Model.Department;
import ruazosa.hr.fer.officememo.Model.FirebaseHandler;
import ruazosa.hr.fer.officememo.Model.ObservableList;
import ruazosa.hr.fer.officememo.Model.OfficeMemo;
import ruazosa.hr.fer.officememo.Model.Post;
import ruazosa.hr.fer.officememo.R;

// TODO: 29.6.2017. add description of image using Google Vision API
public class NewPostActivity extends AppCompatActivity {//implements IPickResult {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyCFoJ_GfzlUddBllKTLehvIBSLnZpkEF8E";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int PLACE_REQUEST_CODE = 13;
    private static final int GALLER_CODE = 17;
    ObservableList<Department> listOfDepartment = new ObservableList<>();
    private Post newPost = new Post();
    Spinner spinner;
    Button postButton;
    ArrayAdapter adapter;
    TextInputEditText title;
    TextInputEditText content;
    ImageView selectImage;
    ImageView selectedImage;
    ImageView locationTag;
    ImageView labelImage;
    Bitmap currentBitMap;
    TextView locationText;
    ImageView locationImage;
    ProgressDialog dialog;
    Uri currentImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        //initialize
        spinner = (Spinner) findViewById(R.id.spinnerDepartments);
        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, listOfDepartment.getList());
        spinner.setAdapter(adapter);
        postButton = (Button) findViewById(R.id.buttonPost);
        title = (TextInputEditText) findViewById(R.id.editTextTitle);
        content = (TextInputEditText) findViewById(R.id.editTextContent);
        selectImage = (ImageView) findViewById(R.id.imageViewImage);
        selectedImage = (ImageView) findViewById(R.id.imageView);
        labelImage = (ImageView) findViewById(R.id.imageViewLabel);
        locationTag = (ImageView) findViewById(R.id.imageViewLocation);
        locationText = (TextView) findViewById(R.id.textViewLocation);
        locationImage = (ImageView) findViewById(R.id.locationImageView);

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

                newPost.setDid(((Department) parent.getItemAtPosition(position)).getDid());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                newPost.setDid("");
            }
        });

        RxTextView.textChanges(title).subscribe(charSequence -> {
            if (charSequence.toString().isEmpty()) {
                title.setError(getString(R.string.empty_title));
            } else
                title.setError(null);
        });

        RxTextView.textChanges(content).subscribe(charSequence -> {
            if (charSequence.toString().isEmpty()) {
                content.setError(getString(R.string.empty_content));
            } else
                content.setError(null);
        });

        RxView.clicks(postButton).subscribe(o -> {
            if (newPost.getDid().isEmpty() || title.getText().toString().isEmpty() || content.getText().toString().isEmpty()) {
                Snackbar.make(getCurrentFocus(), R.string.check_your_values, Snackbar.LENGTH_LONG).show();

            } else {
                newPost.setContent(content.getText().toString());
                newPost.setTitle(title.getText().toString());
                newPost.setUid("sdfasd4");
                newPost.setTimeStamp(OfficeMemo.timeStampToString(new Date()));
                if (currentImageUri == null)
                    FirebaseHandler.pushPost(newPost);
                else
                    FirebaseHandler.pushPost(newPost, currentImageUri);
                finish();
            }
        });

        RxView.longClicks(selectImage).subscribe(o -> {
            Toast.makeText(this, selectImage.getContentDescription().toString()
                    , Toast.LENGTH_SHORT).show();
        });

        RxView.longClicks(locationTag).subscribe(o -> {
            Toast.makeText(this, locationTag.getContentDescription().toString()
                    , Toast.LENGTH_SHORT).show();
        });

        RxView.longClicks(labelImage).subscribe(o -> {
            Toast.makeText(this, labelImage.getContentDescription().toString()
                    , Toast.LENGTH_SHORT).show();
        });

        RxView.clicks(selectImage).subscribe(o -> {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});


            startActivityForResult(chooserIntent, GALLER_CODE);

        });

        RxView.clicks(labelImage).subscribe(o -> {
            if (currentBitMap != null) {
                dialog = ProgressDialog.show(NewPostActivity.this, "",
                        "Classifying your image. Doing lots of dark magic. Please wait...", true);
                getImageDescription(currentBitMap);
            }
        });

        RxView.longClicks(selectedImage).subscribe(o -> {
            currentBitMap = null;
            currentImageUri = null;
            selectedImage.setImageResource(R.drawable.gallery);
            Snackbar.make(getCurrentFocus(), R.string.removed_image, Snackbar.LENGTH_SHORT).show();
        });

        RxView.clicks(locationTag).subscribe(o -> {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            try {

                Intent intent = intentBuilder.build(this);
                startActivityForResult(intent, PLACE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        });

        RxView.longClicks(locationText).subscribe(o -> {
            locationText.setVisibility(View.GONE);
            locationImage.setVisibility(View.GONE);

            newPost.setLocation("");
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Place place = PlacePicker.getPlace(data, this);
            newPost.setLocation(place.getAddress().toString());
            locationText.setText(place.getAddress().toString());
            locationText.setVisibility(View.VISIBLE);
            locationImage.setVisibility(View.VISIBLE);
        } else if (requestCode == GALLER_CODE && resultCode == Activity.RESULT_OK) {
            selectedImage.setImageURI(data.getData());
            currentImageUri = data.getData();
            OfficeMemo.setImageToView(this, selectedImage, currentImageUri);
            try {
                currentBitMap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @SuppressLint("StaticFieldLeak")
    private void getImageDescription(Bitmap bitmap) {
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... objects) {
                try {

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                                /**
                                 * We override this so we can inject important identifying fields into the HTTP
                                 * headers. This enables use of a restricted cloud platform API key.
                                 */
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);

                                    String packageName = getPackageName();
                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = getSignature(getPackageManager(), packageName);

                                    visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                                }
                            };

                    Vision.Builder builder = new Vision.Builder(AndroidHttp.newCompatibleTransport(),
                            GsonFactory.getDefaultInstance(), null);
                    builder.setVisionRequestInitializer(requestInitializer);
                    Vision vision = builder.build();
                    List<Feature> featureList = new ArrayList<>();
                    Feature labelDetection = new Feature();
                    labelDetection.setType("LABEL_DETECTION");
                    labelDetection.setMaxResults(10);
                    featureList.add(labelDetection);

                    List<AnnotateImageRequest> imageList = new ArrayList<>();
                    AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
                    Image base64EncodedImage = getBase64EncodedJpeg(bitmap);
                    annotateImageRequest.setImage(base64EncodedImage);
                    annotateImageRequest.setFeatures(featureList);
                    imageList.add(annotateImageRequest);
                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(imageList);

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d("d", "sending request");

                    BatchAnnotateImagesResponse response = null;

                    response = annotateRequest.execute();

                    return convertResponseToString(response);
                } catch (Exception e) {
                    Log.e("ERRPR", e.getMessage());
                }
                return "Failed";
            }

            @Override
            protected void onPostExecute(String s) {
                content.setText(content.getText().toString() + "\n" + s);
                dialog.dismiss();
            }
        }.execute();
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder();
        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                message.append("#");
                message.append(label.getDescription());
                message.append(" ");
            }
        } else {
            Snackbar.make(getCurrentFocus(), R.string.not_found, Snackbar.LENGTH_LONG).show();
        }
        Log.d("RESULT", message.toString());
        return message.toString();
    }

    public Image getBase64EncodedJpeg(Bitmap bitmap) {
        Image image = new Image();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        image.encodeContent(imageBytes);
        return image;
    }

    public static String getSignature(@NonNull PackageManager pm, @NonNull String packageName) {
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            if (packageInfo == null
                    || packageInfo.signatures == null
                    || packageInfo.signatures.length == 0
                    || packageInfo.signatures[0] == null) {
                return null;
            }
            return signatureDigest(packageInfo.signatures[0]);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private static String signatureDigest(Signature sig) {
        byte[] signature = sig.toByteArray();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] digest = md.digest(signature);
            return BaseEncoding.base16().lowerCase().encode(digest);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
