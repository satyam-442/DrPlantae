package com.innocnetcoder.drplantae.admin;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.innocnetcoder.drplantae.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class AddPlantActivity extends AppCompatActivity {

    Spinner availableTypeSpinner, availableSizeSpinner;
    MaterialButton addPlantBtn;
    TextView availableTypeText, availableSizeText;
    ImageView imageOne;

    Uri uriOne, uriTwo, uriThree;
    StorageReference storagePicRef;
    ProgressDialog loadingBar;

    CollectionReference prodRef;
    String myUrlOne = "";
    StorageTask uploadTaskOne;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextInputLayout plantName, plantDescription, plantPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);

        loadingBar = new ProgressDialog(this);

        storagePicRef = FirebaseStorage.getInstance().getReference().child("PlantPictures");
        prodRef = FirebaseFirestore.getInstance().collection("Plants");

        imageOne = findViewById(R.id.imageOne);

        plantName = findViewById(R.id.plantName);
        plantDescription = findViewById(R.id.plantDescription);
        plantPrice = findViewById(R.id.plantPrice);

        availableTypeText = findViewById(R.id.availableTypeText);

        availableTypeSpinner = findViewById(R.id.availableTypeSpinner);
        ArrayAdapter<CharSequence> adapter4Type = ArrayAdapter.createFromResource(this, R.array.plantType, android.R.layout.simple_spinner_item);
        adapter4Type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availableTypeSpinner.setAdapter(adapter4Type);
        availableTypeSpinner.setOnItemSelectedListener(new AvailableTypeSpinner());

        availableSizeText = findViewById(R.id.availableSizeText);

        availableSizeSpinner = findViewById(R.id.availableSizeSpinner);
        ArrayAdapter<CharSequence> adapter4Size = ArrayAdapter.createFromResource(this, R.array.plantSize, android.R.layout.simple_spinner_item);
        adapter4Size.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availableSizeSpinner.setAdapter(adapter4Size);
        availableSizeSpinner.setOnItemSelectedListener(new AvailableSizeSpinner());

        ActivityResultLauncher<Intent> launcherOne = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
            if (result.getResultCode() == RESULT_OK) {
                uriOne = result.getData().getData();
                // Use the uri to load the image
                imageOne.setImageURI(uriOne);
            } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                // Use ImagePicker.Companion.getError(result.getData()) to show an error
            }
        });

        imageOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AddPlantActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.Companion.with(AddPlantActivity.this)
                            .crop()
                            .cropOval()
                            .maxResultSize(512, 512, true)
                            .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                            .createIntentFromDialog((Function1) (new Function1() {
                                public Object invoke(Object var1) {
                                    this.invoke((Intent) var1);
                                    return Unit.INSTANCE;
                                }

                                public final void invoke(@NotNull Intent it) {
                                    Intrinsics.checkNotNullParameter(it, "it");
                                    launcherOne.launch(it);
                                }
                            }));
                } else {
                    ActivityCompat.requestPermissions(AddPlantActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });

        addPlantBtn = findViewById(R.id.addPlantBtn);
        addPlantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pName = plantName.getEditText().getText().toString();
                String pDesc = plantDescription.getEditText().getText().toString();
                String pPrice = plantPrice.getEditText().getText().toString();

                String availStr = availableTypeText.getText().toString().toUpperCase();
                String availSizeStr = availableSizeText.getText().toString().toUpperCase();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat time = new SimpleDateFormat("HH-MM-ss-SS");//HOUR-MINUTE-SECOND
                SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");//DAY-MONTH-YEAR
                String currentTime = time.format(calendar.getTime());
                String currentDate = date.format(calendar.getTime());
                String plantID = availStr + currentTime;

                if (pName.isEmpty() || pDesc.isEmpty() || pPrice.isEmpty()) {
                    Toast.makeText(AddPlantActivity.this, "Plant details missing!", Toast.LENGTH_SHORT).show();
                } else if (uriOne != null) {
                    loadingBar.setMessage("please wait...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    HashMap propertyMap = new HashMap();
                    propertyMap.put("PlantName", pName);
                    propertyMap.put("PlantDescription", pDesc);
                    propertyMap.put("PlantPrice", pPrice);

                    propertyMap.put("PlantType", availStr);
                    propertyMap.put("PlantSize", availSizeStr);

                    propertyMap.put("CurrentTime", currentTime);
                    propertyMap.put("CurrentDate", currentDate);
                    propertyMap.put("PlantID", plantID);

                    prodRef.document(plantID).set(propertyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddPlantActivity.this, "Plant Added!", Toast.LENGTH_SHORT).show();
                            } else {
                                String msg = task.getException().getMessage();
                                Toast.makeText(AddPlantActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //LOADING OF FIRST IMAGE
                    final StorageReference fileref = storagePicRef.child(plantID + currentTime + "one.jpg");
                    uploadTaskOne = fileref.putFile(uriOne);
                    uploadTaskOne.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return fileref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUrl = task.getResult();
                                myUrlOne = downloadUrl.toString();
                                DocumentReference ref = db.collection("Plants").document(plantID);
                                HashMap<String, Object> userMapImg = new HashMap<String, Object>();
                                userMapImg.put("ImageOne", myUrlOne);
                                ref.update(userMapImg);
                                loadingBar.dismiss();
                                finish();
                            } else {
                                String msg = task.getException().getMessage();
                                Toast.makeText(AddPlantActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private class AvailableTypeSpinner implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            String itemSpinner = adapterView.getItemAtPosition(position).toString();
            availableTypeText.setText(itemSpinner);
            Toast.makeText(AddPlantActivity.this, "Plant type " + itemSpinner, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    private class AvailableSizeSpinner implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String itemSpinner = adapterView.getItemAtPosition(i).toString();
            availableSizeText.setText(itemSpinner);
            Toast.makeText(AddPlantActivity.this, "Plant size " + itemSpinner, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}