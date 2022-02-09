package com.example.blackjacks.ui.slideshow;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.blackjacks.R;
import com.example.blackjacks.databinding.FragmentSlideshowBinding;
import com.example.blackjacks.ui.home.HomeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    private FragmentSlideshowBinding binding;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView imView;
    File uploadFile;

    String fileFormat;
    private JSONArray jobj;
    private JSONObject userDetailsObj;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        String userDetails = getUserdata();
        try {
            jobj = new JSONArray(userDetails);
            userDetailsObj = new JSONObject(jobj.getString(0));

            //cpInfo = getCpInfo(userDetailsObj.getString("id"));
            //jobj = new JSONArray(cpInfo);
            //cpObj = new JSONObject(jobj.getString(0));


        } catch (Exception e) {
            e.printStackTrace();
        }

        slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button btn_upload = binding.idCard;
        imView = binding.imView;
        Button send = binding.send;;
        EditText title = binding.title;
        EditText message = binding.message;

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadFile.isFile())
                {
                    try {
                        String resp = uploadFileMedCard(userDetailsObj.getString("email"),
                                                        title.getText().toString(),
                                                        message.getText().toString());
                        Toast.makeText(getContext(), resp, Toast.LENGTH_LONG).show();
                        if (resp == "Feedback Sent")
                        {
                            Fragment home = new HomeFragment();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.content_main, home);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getContext(), "Please Attache Image on the Report ", Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        //final TextView textView = binding.textSlideshow;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            // display error state to the user
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("1TESTING RESULT " + requestCode );
        System.out.println("1TESTING FINE   " + resultCode );

        System.out.println("1TESTING RESULT_OK   " + RESULT_OK );
        System.out.println("1TESTING REQUEST_IMAGE_CAPTURE   " + REQUEST_IMAGE_CAPTURE );

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            try {
                uploadFile = convertToFile(imageBitmap);
                uploadFile.getPath();

                String testPath = uploadFile.getPath();
                fileFormat = testPath.substring(testPath.lastIndexOf('.') + 1);

                Toast.makeText(getContext(), "file fileFormat " + fileFormat, Toast.LENGTH_LONG).show();
                System.out.println("f is file " + uploadFile.isFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
            imView.setImageBitmap(imageBitmap);
            System.out.println("SAMKELO ZUMA");
            //imageView.setImageBitmap(imageBitmap);
        }
    }
    public File convertToFile(Bitmap btm) throws IOException {
        //create a file to write bitmap data
        String filename = Math.round(Math.random()*1000) + "attach.png";
        File f = new File(getContext().getCacheDir(), filename);
        f.createNewFile();

//Convert bitmap to byte array
        Bitmap bitmap = btm;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return  f;
    }

    public String uploadFileMedCard(String email,String title, String message)
    {
        String resp = "";
        System.out.println("Start Upload " + fileFormat );
        try {
            Uri fileUri = Uri.fromFile(uploadFile);
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            System.out.println("Upload medCardFile.isFile() " + uploadFile.isFile() );
            System.out.println("medCardFile" + uploadFile );
            if(uploadFile.isFile())
            {
                String upLoadServerUri = "http://192.168.1.41/userm/app_feedback.php?email=" + email + "&title=" +title + "&description=" +message;
                FileInputStream fileInputStream = new FileInputStream(
                        uploadFile);
                URL url = new URL(upLoadServerUri);
                // Open a HTTP connection to the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE",
                        "multipart/form-data");
                conn.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("bill", fileUri.toString());

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"bill\";filename=\""
                        + fileUri.toString() + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math
                            .min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0,
                            bufferSize);

                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens
                        + lineEnd);

                // Responses from the server (code and message)
                int serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn
                        .getResponseMessage();

                if (serverResponseCode == 200) {

                    resp = "Feedback Sent";
                    // messageText.setText(msg);
                    //Toast.makeText(ctx, "File Upload Complete.",
                    //      Toast.LENGTH_SHORT).show();

                    // recursiveDelete(mDirectory1);

                }
                else {
                    resp = "" + serverResponseCode;
                }

                // close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();
            }
        }catch (Exception e)
        {
            System.out.println("Upload Error " + e);
            e.printStackTrace();
        }
        System.out.println("End Upload " );

        return resp;
    }

    public String getUserdata() {
        Intent i = this.getActivity().getIntent();
        String username = i.getStringExtra("username");
        System.out.println("Medical Appointment " + username);
        String ad = "http://192.168.1.41/userm/app_get_userdetails.php?"
                + "username=" + username;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        String adJson = "";
        JSONArray jobj = null;
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(ad);
            InputStream ist = (InputStream) url.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());

            adJson = result.toString();
            //jobj = new JSONArray(adJson);
            //jobj.length();

            //System.out.println("ss " + jobj.getJSONObject(0).getString("header"));
        } catch (IOException  e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return adJson;
    }
}