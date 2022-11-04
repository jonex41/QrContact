package com.jonex41.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.jonex41.myapplication.databinding.OtherShareLayoutBinding;

import io.paperdb.Paper;

public class OtherShareActivity extends AppCompatActivity {
    OtherShareLayoutBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.other_share_layout);

        binding.toolbar.viewTxt.setText("Share text or link");
        binding.submit.setOnClickListener(view -> {
            getStuff();
        });
        binding.toolbar.backBtn.setOnClickListener(view -> {
            finish();
        });

    }

    private void getStuff() {

        if(!TextUtils.isEmpty( binding.textShare.getText().toString())){
           String value =  binding.textShare.getText().toString();
          //  Toast.makeText(this, ""+value, Toast.LENGTH_SHORT).show();
           if(URLUtil.isValidUrl(value)){
               // is a url
               value = "link- "+value;
           }else {
               value = "Random- "+value;
               // not a url
           }
           saveInPref(value);
        }else {
            Toast.makeText(this, "Please enter a text or a link", Toast.LENGTH_SHORT).show();
        }

    }

    private void saveInPref(String savedString) {
        Paper.book().write("saver", savedString);
       startActivity(new Intent(getApplicationContext(), OtherTextQrCodeActivity.class));
        finish();
    }
}
