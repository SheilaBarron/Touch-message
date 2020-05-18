package com.example.a92gde.chatapp;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Storage
{
    public static void writeToFile(String fileName, Context context, Object data) {
        try {

            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(data);
            os.close();
            fos.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static ArrayList<float[]> readFromFile(String fileName, Context context) {

        ArrayList<float[]> retval = new ArrayList<float[]>();

         try {
            InputStream inputStream = context.openFileInput(fileName);
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);


            retval = (ArrayList<float[]>) is.readObject();

            is.close();

            fis.close();
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        } catch (ClassNotFoundException e) {
                Log.e("login activity", "Can not found class file: " + e.toString());
        }

        return retval;
    }

}
