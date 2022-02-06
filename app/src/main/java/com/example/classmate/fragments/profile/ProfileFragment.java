package com.example.classmate.fragments.profile;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classmate.fragments.profile.Course;
import com.example.classmate.login.AddCoursesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.classmate.Print;
import com.example.classmate.R;
import com.example.classmate.databinding.FragmentProfileBinding;
import com.example.classmate.statics.Bitmaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    StorageReference storageReference;

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::onReturn);

    SharedPreferences sharedPreferences;

    TextView nameTV, emailTV, gradeTV;
    ImageView profilePicIV;
    RecyclerView recyclerView;

    CourseListAdapter adapter;

    ArrayList<Course> courses;
    String name, email, grade;
    byte[] bytes;
    private String userID;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        sharedPreferences = requireContext().getSharedPreferences("com.example.classmate", Context.MODE_PRIVATE);

        firebase();
        setResourceObjects(root);
        setListeners();
        setRecyclerView();
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
        gradeTV = root.findViewById(R.id.profile_grade_text_view);
        profilePicIV = root.findViewById(R.id.profile_picture);
        recyclerView = root.findViewById(R.id.profile_course_list_recycler_view);
    }

    public void setListeners() {
        profilePicIV.setOnClickListener(this::getPhoto);
    }

    public void setRecyclerView() {
        if(courses == null) return;
        adapter = new CourseListAdapter(requireActivity(), courses, false);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new AddCoursesActivity.VerticalSpacingItemDecorator(8));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    public void getPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityLauncher.launch(intent);
    }

    public void setDefault() {
        name = sharedPreferences.getString("name", null);
        email = sharedPreferences.getString("email", null);
        grade = String.valueOf(sharedPreferences.getInt("grade", 12));
        courses = Course.Companion.deserialize(sharedPreferences.getString("courses", null));

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
            bytes[i] = Byte.parseByte(pfpStringArray[i]);
        return bytes;
    }

    public void setTextViews() {
        nameTV.setText(name);
        emailTV.setText(email);
        String grade = "Grade: " + this.grade;
        gradeTV.setText(grade);
    }

    public void setPFP() {
        if (bytes != null) {
            Print.i(bytes.length);
            Bitmaps.setBytes(profilePicIV, bytes);
        } else {
            Print.i("null");
        }
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
        grade = String.valueOf(user.getGrade());
        courses = new ArrayList<>();
        List<Map<String, ?>> list = (List<Map<String, ?>>) snapshot.get("courses");
        if (list != null) {
            for(Map<String, ?> map : list) {
                courses.add(Course.Companion.from(map));
            }
        }
        Print.i(courses);
        sharedPreferences.edit().putString("name", name).apply();
        sharedPreferences.edit().putString("email", email).apply();
        sharedPreferences.edit().putInt("grade", user.getGrade()).apply();
        sharedPreferences.edit().putString("course", Course.Companion.serialize(courses)).apply();
        setTextViews();
        setRecyclerView();
    }

    public void storageSuccess(byte[] bytes) {
        this.bytes = bytes;
        sharedPreferences.edit().putString("bytes", Arrays.toString(bytes)).apply();
        setPFP();
    }

    public void onReturn(ActivityResult result) {
        Print.i("OnReturn()");
        if (result.getData() == null) return;
        Uri selectedImage = result.getData().getData();
        Print.i("Selected Image");
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImage);
            Print.i("Gotten Image");
            storageReference.child("pfp").child(userID).putBytes(getCircularBytes(bmp));
            bytes = Bitmaps.getBytes(bmp);
            Print.i("Converted to Bytes");
            sharedPreferences.edit().putString("bytes", Arrays.toString(bytes)).apply();
            setPFP();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Print.i("Finished OnReturn()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
