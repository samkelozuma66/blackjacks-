package com.example.blackjacks.ui.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.blackjacks.R;
import com.example.blackjacks.databinding.FragmentGalleryBinding;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private FragmentGalleryBinding binding;
    private CodeScanner mCodeScanner;
    JSONArray jobj;
    JSONObject userDetailsObj;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        String userDetails = getUserdata();
        try {
            jobj = new JSONArray(userDetails);
            userDetailsObj = new JSONObject(jobj.getString(0));



        } catch (Exception e) {
            e.printStackTrace();
        }

        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Activity activity = getActivity();

        CodeScannerView scannerView = binding.scannerView;
        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String done ="mm";
                        try {
                            done = confirmCP(userDetailsObj.getString("id"),
                                                    result.getText().trim());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(activity, done, Toast.LENGTH_LONG).show();

                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

        //final TextView textView = binding.textGallery;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
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
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return adJson;
    }
    public String confirmCP(String user_id,
                            String cp_code)
    {

        String ad = "http://192.168.1.41/userm/confirm_cp.php?"
                  + "user_id=" + user_id
                  + "&cp_code=" + cp_code;
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
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return adJson;
    }
}