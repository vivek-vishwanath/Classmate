package com.example.classmate.statics;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Serializer {

    public static String serialize(Object o) throws IOException {
        String serialized = "";
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
        objectStream.writeObject(o);
        objectStream.flush();
        byte[] bytes = Base64.encode(byteStream.toByteArray(), 0);
        serialized = new String(bytes);
        return serialized;
    }

    public static <T> T deserialize(String string) {
        try {
            byte[] bytes = Base64.decode(string.getBytes(), 0);
            ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectStream = new ObjectInputStream(byteStream);
            return (T) objectStream.readObject();
        } catch (Exception e) {
            return (T) new ArrayList<String>();
        }
    }
}
