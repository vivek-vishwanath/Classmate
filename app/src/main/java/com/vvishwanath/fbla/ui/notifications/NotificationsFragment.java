package com.vvishwanath.fbla.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.vvishwanath.fbla.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"vivs005@gmail.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "Sample Subject");
        email.putExtra(Intent.EXTRA_TEXT, "Sample Body");

        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "Choose an Email client :"));

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
