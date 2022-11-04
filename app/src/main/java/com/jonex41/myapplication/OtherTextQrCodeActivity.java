package com.jonex41.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.zxing.BarcodeFormat;
import com.jonex41.myapplication.databinding.PickToQrLayoutBinding;
import com.jonex41.myapplication.models.ContactModel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.List;

import io.paperdb.Paper;

public class OtherTextQrCodeActivity extends AppCompatActivity {

    private PickToQrLayoutBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Paper.init(this);
        binding = DataBindingUtil.setContentView(this, R.layout.pick_to_qr_layout);

        setSupportActionBar(binding.toolbar.toolbar);
        getSupportActionBar().setTitle("");
        binding.toolbar.viewTxt.setText("Text Qrcode");
        String string = Paper.book().read("saver");
      //  Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

        if(!TextUtils.isEmpty(string)) {
            generateCode(string, false);
        }
        binding.toolbar.backBtn.setOnClickListener(view -> finish());
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {

                handleSendText(intent); // Handle text being sent
            }
        }

// if this is from the share menu

    }
    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
           // Toast.makeText(this, ""+sharedText, Toast.LENGTH_SHORT).show();
            binding.myContent.setText(sharedText);
            generateCode(sharedText, true);
            // Update UI to reflect text being shared
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
       

    }

    private void generateCode(String savedString, boolean value){

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(savedString, BarcodeFormat.QR_CODE, 400, 400);
            ImageView imageViewQrCode = (ImageView) findViewById(R.id.image);
            imageViewQrCode.setImageBitmap(bitmap);
            if(value){
                Toast.makeText(this, "Text generated", Toast.LENGTH_SHORT).show();

            }
        } catch(Exception e) {
            Toast.makeText(this, "Unable to generate code", Toast.LENGTH_SHORT).show();
        }
    }

}

