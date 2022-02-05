package com.example.classmate.ui.profile;

import static com.example.classmate.statics.Bitmaps.MAX_SIZE;
import static com.example.classmate.statics.Bitmaps.getBytes;
import static com.example.classmate.statics.Bitmaps.getCircularBitmap;
import static com.example.classmate.statics.Bitmaps.getCircularBytes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.classmate.Print;
import com.example.classmate.R;
import com.example.classmate.objects.User;
import com.example.classmate.databinding.FragmentProfileBinding;
import com.example.classmate.statics.Bitmaps;

import java.util.Arrays;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    StorageReference storageReference;

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::onReturn);

    SharedPreferences sharedPreferences;

    TextView nameTV, emailTV, schoolTV, gradeTV;
    ImageView profilePicIV;

    String name, email, school, grade;
    byte[] bytes;

    private String userID;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        sharedPreferences = requireContext().getSharedPreferences("com.example.classmate", Context.MODE_PRIVATE);

        firebase();
        setResourceObjects(root);
        setListeners();
        setDefault();

        pullFromDatabase();

        return root;
    }

    private void firebase() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        userID = auth.getUid();
        if (userID == null) requireActivity().finish();
    }

    private void setResourceObjects(View root) {
        nameTV = root.findViewById(R.id.profile_name_text_view);
        emailTV = root.findViewById(R.id.profile_email_text_view);
        schoolTV = root.findViewById(R.id.profile_school_text_view);
        gradeTV = root.findViewById(R.id.profile_grade_text_view);
        profilePicIV = root.findViewById(R.id.profile_picture);
    }

    public void setListeners() {
        profilePicIV.setOnClickListener(this::getPhoto);
    }

    public void getPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityLauncher.launch(intent);
    }

    public void setDefault() {
        name = sharedPreferences.getString("name", null);
        email = sharedPreferences.getString("email", null);
        school = sharedPreferences.getString("school", null);
        grade = String.valueOf(sharedPreferences.getInt("grade", 13));

        String pfp = sharedPreferences.getString("pfp", null);
        if(pfp != null) this.bytes = getBytes(pfp);

        setTextViews();
        setPFP();
    }

    public static byte[] getBytes(String pfp) {
        byte[] bytes;
        String[] pfpStringArray = pfp.substring(1, pfp.length() - 1).split(",");
        bytes = new byte[pfpStringArray.length];
        for (int i = 0; i < pfpStringArray.length; i++)
            bytes[i] = Byte.parseByte(pfpStringArray[i].substring(1));
        return bytes;
    }

    public void setTextViews() {
        nameTV.setText(name);
        emailTV.setText(email);
        schoolTV.setText(school);
        String grade = "Grade: " + this.grade;
        gradeTV.setText(grade);
    }

    public void setPFP() {
        if (bytes != null)
            Bitmaps.setBytes(profilePicIV, bytes);
    }

    public void pullFromDatabase() {
        firestore.collection("users").document(userID).get()
                .addOnSuccessListener(this::firestoreSuccess);

        storageReference.child("pfp").child(userID).getBytes(MAX_SIZE)
                .addOnSuccessListener(this::storageSuccess)
                .addOnFailureListener(this::storageFailure);
    }

    private void storageFailure(Exception e) {
        profilePicIV.setImageResource(R.drawable.account_circle_grey);
    }

    public void firestoreSuccess(DocumentSnapshot snapshot) {
        if (snapshot.getData() == null) return;
        User user = User.Companion.from(snapshot.getData());
        name = user.getName();
        email = user.getEmail();
        school = user.getSchool();
        grade = String.valueOf(user.getGrade());
        sharedPreferences.edit().putString("name", name).apply();
        sharedPreferences.edit().putString("email", email).apply();
        sharedPreferences.edit().putString("school", school).apply();
        sharedPreferences.edit().putInt("grade", user.getGrade()).apply();
        setTextViews();
    }

    public void storageSuccess(byte[] bytes) {
        this.bytes = bytes;
        sharedPreferences.edit().putString("bytes", Arrays.toString(bytes)).apply();
        setPFP();
    }

    public void onReturn(ActivityResult result) {
        if (result.getData() == null) return;
        Uri selectedImage = result.getData().getData();
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImage);
            storageReference.child("pfp").child(userID).putBytes(getCircularBytes(bmp));
            bytes = Bitmaps.getBytes(bmp);
            sharedPreferences.edit().putString("bytes", Arrays.toString(bytes)).apply();
            setPFP();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
