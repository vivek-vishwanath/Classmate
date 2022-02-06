package com.example.classmate.ui.notifications;

import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.classmate.Print;
import com.example.classmate.R;
import com.example.classmate.databinding.FragmentNotificationsBinding;
import com.example.classmate.objects.Email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    final String sender = "ceimentbrick.22@gmail.com";
    final String password = "rs12@213";

    Button button;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Email email = new Email("2000003266@fcstu.org", "Sending email test subject", "test message 223");
        sendEmail(email);

        setResourceObjects(root);

        return root;
    }

    private void sendEmail(Email email) {
        Thread thread = new Thread(() -> asyncEmail(email));
        thread.start();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void asyncEmail(Email email) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props, new Authenticator(sender, password));
        Looper.prepare();
        try {
            Transport.send(getMessage(session, email));
            Toast.makeText(getContext(), "Email sent to " + email.getRecipient(), Toast.LENGTH_LONG).show();
        } catch (MessagingException e){
            if(e.getMessage() != null && e.getMessage().startsWith("535-5.7.8 Username and Password not accepted")) {
                Print.i("Correct Error Message");
                Toast.makeText(requireContext(), "Email Blocked by Service Provider", Toast.LENGTH_LONG).show();
            } else {
                Print.i(e.getMessage());
                Print.i(e.getMessage().length());
            }
        }
    }

    private Message getMessage(Session session, Email email) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getRecipient()));
        message.setSubject(email.getSubject());
        message.setText(email.getBody());
        return message;
    }

    public static class Authenticator extends javax.mail.Authenticator {

        private final String email, password;

        public Authenticator(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(email, password);
        }
    }


    public void setResourceObjects(View root) {
        button = root.findViewById(R.id.testing_button);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}