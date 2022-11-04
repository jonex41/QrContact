package com.jonex41.myapplication;

import android.graphics.Bitmap;
import android.os.Bundle;
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

public class SelectedContactQrActivity extends AppCompatActivity {

    private PickToQrLayoutBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.pick_to_qr_layout);

        setSupportActionBar(binding.toolbar.toolbar);
        getSupportActionBar().setTitle("");
        binding.toolbar.viewTxt.setText("Contact Qrcode");
        List<ContactModel> listt = Paper.book().read("picked");
        String longString = "";
        for(int i = 0; i < listt.size(); i++){
            if(i == 0){
                longString = "Contact- "+"@n@"+listt.get(i).getName()+"--"+listt.get(i).getNumber();
            }else {
                longString = longString+" @n@"+listt.get(i).getName()+"--"+listt.get(i).getNumber();
            }
        }
        generateCode(longString, false );
        binding.toolbar.backBtn.setOnClickListener(view -> finish());

    }

    private void generateCode(String savedString, boolean value){

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(savedString, BarcodeFormat.QR_CODE, 400, 400);
            ImageView imageViewQrCode = binding.image;
            imageViewQrCode.setImageBitmap(bitmap);
            if(value){
                Toast.makeText(this, "Contact generated", Toast.LENGTH_SHORT).show();

            }
        } catch(Exception e) {
            Toast.makeText(this, "Unable to generate code", Toast.LENGTH_SHORT).show();
        }
    }

}
