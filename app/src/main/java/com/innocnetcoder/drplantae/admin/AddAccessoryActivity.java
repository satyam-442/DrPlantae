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

public class AddAccessoryActivity extends AppCompatActivity {

    Spinner accessorySizeSpinner, accessoriesNameSpinner;
    MaterialButton addAccessoryBtn;
    TextView accessorySizeText, accessoriesNameText;
    ImageView imageOne;
    Uri uriOne;
    StorageReference storagePicRef;
    ProgressDialog loadingBar;
    CollectionReference prodRef;
    String myUrlOne = "";
    StorageTask uploadTaskOne;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextInputLayout accessoryName, accessoryDescription, accessoryPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_accessory);

        loadingBar = new ProgressDialog(this);

        storagePicRef = FirebaseStorage.getInstance().getReference().child("AccessoryPictures");
        prodRef = FirebaseFirestore.getInstance().collection("Accessory");

        imageOne = findViewById(R.id.imageOne);

        accessoryName = findViewById(R.id.accessoryName);
        accessoryDescription = findViewById(R.id.accessoryDescription);
        accessoryPrice = findViewById(R.id.accessoryPrice);

        accessorySizeText = findViewById(R.id.accessorySizeText);
        accessorySizeSpinner = findViewById(R.id.accessorySizeSpinner);
        ArrayAdapter<CharSequence> adapter4Size = ArrayAdapter.createFromResource(this, R.array.accessorySize, android.R.layout.simple_spinner_item);
        adapter4Size.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accessorySizeSpinner.setAdapter(adapter4Size);
        accessorySizeSpinner.setOnItemSelectedListener(new AccoryTypeSpinner());

        accessoriesNameText = findViewById(R.id.accessoriesNameText);
        accessoriesNameSpinner = findViewById(R.id.accessoriesNameSpinner);
        ArrayAdapter<CharSequence> adapter4Name = ArrayAdapter.createFromResource(this, R.array.accessoryName, android.R.layout.simple_spinner_item);
        adapter4Name.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accessoriesNameSpinner.setAdapter(adapter4Name);
        accessoriesNameSpinner.setOnItemSelectedListener(new AccessoriesNameSpinner());

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
                if (ContextCompat.checkSelfPermission(AddAccessoryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.Companion.with(AddAccessoryActivity.this)
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
                    ActivityCompat.requestPermissions(AddAccessoryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });

        addAccessoryBtn = findViewById(R.id.addAccessoryBtn);
        addAccessoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pName = accessoryName.getEditText().getText().toString();
                String pDesc = accessoryDescription.getEditText().getText().toString();
                String pPrice = accessoryPrice.getEditText().getText().toString();

                String availSizeStr = accessorySizeText.getText().toString().toUpperCase();
                String availTypeStr = accessoriesNameText.getText().toString().toUpperCase();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat time = new SimpleDateFormat("HH-MM-ss-SS");//HOUR-MINUTE-SECOND
                SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");//DAY-MONTH-YEAR
                String currentTime = time.format(calendar.getTime());
                String currentDate = date.format(calendar.getTime());
                String plantID = availTypeStr + currentTime;

                if (pName.isEmpty() || pDesc.isEmpty() || pPrice.isEmpty()) {
                    Toast.makeText(AddAccessoryActivity.this, "Accessory details missing!", Toast.LENGTH_SHORT).show();
                } else if (uriOne != null) {
                    loadingBar.setMessage("please wait...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    HashMap propertyMap = new HashMap();
                    propertyMap.put("AccessoryName", pName);
                    propertyMap.put("AccessoryDescription", pDesc);
                    propertyMap.put("AccessoryPrice", pPrice);
                    propertyMap.put("AccessorySize", availSizeStr);
                    propertyMap.put("AccessoryType", availTypeStr);
                    propertyMap.put("AccessoryID", plantID);

                    propertyMap.put("CurrentTime", currentTime);
                    propertyMap.put("CurrentDate", currentDate);

                    prodRef.document(plantID).set(propertyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddAccessoryActivity.this, "Accessory Added!", Toast.LENGTH_SHORT).show();
                            } else {
                                String msg = task.getException().getMessage();
                                Toast.makeText(AddAccessoryActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                                DocumentReference ref = db.collection("Accessory").document(plantID);
                                HashMap<String, Object> userMapImg = new HashMap<String, Object>();
                                userMapImg.put("ImageOne", myUrlOne);
                                ref.update(userMapImg);
                                loadingBar.dismiss();
                                finish();
                            } else {
                                String msg = task.getException().getMessage();
                                Toast.makeText(AddAccessoryActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private class AccoryTypeSpinner implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String itemSpinner = adapterView.getItemAtPosition(i).toString();
            accessorySizeText.setText(itemSpinner);
            Toast.makeText(AddAccessoryActivity.this, "Accessory size " + itemSpinner, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class AccessoriesNameSpinner implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String itemSpinner = adapterView.getItemAtPosition(i).toString();
            accessoriesNameText.setText(itemSpinner);
            Toast.makeText(AddAccessoryActivity.this, "Accessory name " + itemSpinner, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}