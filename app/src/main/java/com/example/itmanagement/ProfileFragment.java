package com.example.itmanagement;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import static androidx.appcompat.app.AppCompatActivity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

   // StorageReference storageReference;
    StorageReference storageReference;

    String storagePath = "Users_Profile_Cover_Imgs/";

    ImageView avatarprofile,cover_profile;
    TextView firstName, lastName, email;
    FloatingActionButton fab;

    ProgressDialog pd;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_GALLERY_CODE = 300;
    private static final int CAMERA_IMAGE_CODE = 400;
    private static final int CROP_IMAGE_ACTIVITY_REQUEST_CODE = 500;


    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;

    String profileOrCoverPhoto;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();


        cameraPermission = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        avatarprofile = view.findViewById(R.id.avatarprofile);
        cover_profile = view.findViewById(R.id.cover_profile);
        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        email = view.findViewById(R.id.email);
        fab = view.findViewById(R.id.fab);
        pd = new ProgressDialog(getActivity());

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    String firstname = ""+ ds.child("firstName").getValue();
                    String lastname = ""+ ds.child("lastName").getValue();
                    String emails = ""+ ds.child("email").getValue();
                    String images = ""+ ds.child("image").getValue();
                    String cover = ""+ ds.child("cover").getValue();

                    firstName.setText(firstname);
                    lastName.setText(lastname);
                    email.setText(emails);

                    try {
                        Picasso.get().load(images).into(avatarprofile);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.ic_default_profile_white).into(avatarprofile);
                    }
                    try {
                        Picasso.get().load(cover).into(cover_profile);
                        
                    }
                    catch (Exception e){

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

        return view;
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
       requestPermissions( storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission(){
        requestPermissions( cameraPermission, CAMERA_REQUEST_CODE);
    }

    private void showEditProfileDialog() {

        String[] option = {"Edit Profile Picture", "Edit Cover Photo", "Edit First Name", "Edit Last Name"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Choose Action");

        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0){
                    pd.setMessage("Updating Profile Picture");
                    profileOrCoverPhoto = "image";
                    showImagePictureDialog();
                }
                else if (which == 1){
                    pd.setMessage("Updating cover Photo");
                    profileOrCoverPhoto = "cover";
                    showImagePictureDialog();
                }
                else if (which == 2){
                    pd.setMessage("Updating First Name");
                    showNameUpdateDialog("firstName");
                }
                else if (which == 3){
                    pd.setMessage("Updating Last Name");
                    showNameUpdateDialog("lastName");

                }


            }
        });
        builder.create().show();
    }

    private void showNameUpdateDialog(String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update "+ key);

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);

        EditText editText = new EditText(getActivity());
        editText.setHint("Enter "+key);
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = editText.getText().toString().trim();

                if (!TextUtils.isEmpty(value)){
                    pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);

                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(),"Updated...", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(),""+ e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                }
                else {
                    Toast.makeText(getActivity(), "Please Enter "+ key , Toast.LENGTH_SHORT).show();

                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }

    private void showImagePictureDialog() {
        String option[] = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Pick Image From");

        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0){
                    pd.setMessage("Updating Profile Picture");
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }

                }
                else if (which == 1){
                    pd.setMessage("Updating cover Photo");
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else {
                        pickFromGAllery();
                    }

                }


            }
        });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_IMAGE_CODE:{
                if (grantResults.length >0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    }
                    else {
                        Toast.makeText(getActivity(), "Please enable Camera & storage Permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length >0){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        pickFromGAllery();
                    }
                    else {
                        Toast.makeText(getActivity(), "Please enable & storage Permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if (resultCode == RESULT_OK){

            if (requestCode == IMAGE_GALLERY_CODE){
                image_uri = data.getData();
                uploadProfileCoverPhoto(image_uri);

                CropImage.activity(image_uri)
                        .start(getContext(), this);

            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    image_uri = result.getUri();
                    uploadProfileCoverPhoto(image_uri);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }

            if (requestCode == CAMERA_IMAGE_CODE){

                CropImage.activity(image_uri)
                        .start(getContext(), this);

            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    image_uri = result.getUri();
                    uploadProfileCoverPhoto(image_uri);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri uri) {

        pd.show();

        String filePathAndName = storagePath +""+profileOrCoverPhoto +"_"+ user.getUid();

        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        if (uriTask.isSuccessful()){
                            HashMap<String, Object> result = new HashMap<>();
                            result.put(profileOrCoverPhoto,downloadUri.toString());

                            databaseReference.child(user.getUid()).updateChildren(result)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        pd.dismiss();
                                        Toast.makeText(getActivity(),"Image Update...", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(getActivity(),"Error Updating...", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        }
                        else {
                            pd.dismiss();
                            Toast.makeText(getActivity(),"Some error occured", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");


        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, CAMERA_IMAGE_CODE);
    }

    private void pickFromGAllery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_GALLERY_CODE);
    }
    private void startCropActivity(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(getContext(), this);
    }
    private void checkUserStatus(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user !=null){

        }else {

            startActivity(new Intent(getActivity(), Login.class));
            getActivity().finish();
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        if (id == R.id.action_add_Post){
            startActivity(new Intent(getActivity(),AddPostActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}