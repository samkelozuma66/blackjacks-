package com.example.blackjacks.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blackjacks.R;
import com.example.blackjacks.checkpoinAdapter;
import com.example.blackjacks.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    JSONArray jobj;
    JSONObject userDetailsObj;
    JSONObject cpObj;
    String cpInfo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        String userDetails = getUserdata();
        try {
            jobj = new JSONArray(userDetails);
            userDetailsObj = new JSONObject(jobj.getString(0));

            cpInfo = getCpInfo(userDetailsObj.getString("id"));
            jobj = new JSONArray(cpInfo);
            cpObj = new JSONObject(jobj.getString(0));


        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("userDetails " + userDetails);
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView cpListView = binding.cpList;
        checkpoinAdapter checkpoinAdapter = new checkpoinAdapter(getContext(),jobj);

        cpListView.setHasFixedSize(true);
        cpListView.setLayoutManager(new LinearLayoutManager(getContext()));
        cpListView.setAdapter(checkpoinAdapter);

        ImageView user_image = binding.userImage;
        TextView name        = binding.name;
        TextView email       = binding.email;
        TextView contact     = binding.contact;
        TextView gender       = binding.gender;
        TextView user_type     = binding.userType;

        try {

            name.setText(userDetailsObj.getString("name"));
            email.setText(userDetailsObj.getString("email"));
            contact.setText(userDetailsObj.getString("mobile"));
            gender.setText(userDetailsObj.getString("gender"));
            user_type.setText(userDetailsObj.getString("user_type"));


            String uril = "http://192.168.1.41/userm/images/"+userDetailsObj.getString("image");
            URL url_img = new URL(uril);
            System.out.println("url_img '" + url_img+"'");
            Bitmap img = BitmapFactory.decodeStream((InputStream) url_img.getContent());
            user_image.setImageBitmap(img);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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

    public String getCpInfo(String user_id)
    {
        String ad = "http://192.168.1.41/userm/get_cpInfo.php?"
                + "user_id=" + user_id;
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