package com.jonex41.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.zxing.BarcodeFormat;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.jonex41.myapplication.databinding.ContentMainBinding;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 233;
    private AppBarConfiguration appBarConfiguration;
    private ContentMainBinding binding;
    private TextView textView;
    private  ActivityResultLauncher<ScanOptions> barcodeLauncher;
    private String filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Paper.init(this);

       // binding =
        binding = DataBindingUtil.setContentView(this, R.layout.content_main);
        String savedString = Paper.book().read("save");
        if(!TextUtils.isEmpty(savedString)){
            binding.empty.setVisibility(View.GONE);
            binding.toolbar.setVisibility(View.GONE);
           // binding.generateCode.setVisibility(View.GONE);
            binding.shareImage.setVisibility(View.VISIBLE);
            generateCode(savedString, false);
        }else {
            binding.shareImage.setVisibility(View.GONE);
            binding.empty.setVisibility(View.VISIBLE);
           // binding.generateCode.setVisibility(View.GONE);
        }
       setSupportActionBar(binding.toolbar);
        binding.shareImage.setOnClickListener(view -> {
           // Bitmap b =BitmapFactory.decodeResource(getResources(),R.drawable.userimage);
           checkPermission();
        });

        binding.genLayout.setOnClickListener(view -> {
            if(binding.toolbar.getVisibility() == View.GONE){
                binding.toolbar.setVisibility(View.VISIBLE);
            }else {
                binding.toolbar.setVisibility(View.GONE);
            }
        });

        binding.shareContact.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ContactActivity.class));
        });

        binding.empty.setOnClickListener(view -> {
            launchSomeActivity.launch(new Intent(getApplicationContext(), InfoActivity.class));
        });

        barcodeLauncher = registerForActivityResult(new ScanContract(),
                result -> {
                    if(result.getContents() == null) {

                        Toast.makeText(MainActivity.this, "Error: " , Toast.LENGTH_LONG).show();
                    } else {
                        Paper.book().write("recieved", result.getContents());
                        startActivity(new Intent(getApplicationContext(), RecievedContactActivity.class));

                       // textView.setText(result.getContents());
                       // Toast.makeText(MainActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    }
                });

       /* binding.generateCode.setOnClickListener(view -> {
            String another = Paper.book().read("save");
            if(!TextUtils.isEmpty(another)){
                generateCode(another, true);
            }

        });*/

        binding.scanFromGallery.setOnClickListener(view -> {

            pickFile();
        });


       binding.scanFromCamera.setOnClickListener(view -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Volume up to flash on");
            options.setBeepEnabled(true);
            options.setOrientationLocked(true);
            options.setCaptureActivity(CaptureAct.class);
            barcodeLauncher.launch(options);



            /*ScanOptions options = new ScanOptions();
            options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES);
            options.setPrompt("Scan a barcode");
           // options.setCameraId(0);  // Use a specific camera of the device
            options.setBeepEnabled(false);
            options.setBarcodeImageEnabled(true);
            barcodeLauncher.launch(options);*/
        });
    }
    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if(data.getStringExtra("result") != null){

                            if(data.getStringExtra("result").equals("d")){
                                String savedString = Paper.book().read("save");
                                if(!TextUtils.isEmpty(savedString)){
                                    binding.empty.setVisibility(View.GONE);
                                    binding.toolbar.setVisibility(View.GONE);
                                    generateCode(savedString, false);
                                }else {
                                    binding.empty.setVisibility(View.VISIBLE);
                                }
                            }else if(data.getStringExtra("result").equals("other")){

                            }
                        }


                        // finish();
                        // your operation....
                    }
                }
            });

    @Override
    protected void onResume() {
        super.onResume();
        String savedString = Paper.book().read("save");
        if(!TextUtils.isEmpty(savedString)){
            binding.empty.setVisibility(View.GONE);
            binding.toolbar.setVisibility(View.GONE);
           // binding.generateCode.setVisibility(View.GONE);
            binding.shareImage.setVisibility(View.VISIBLE);
            generateCode(savedString, false);
        }else {
            binding.shareImage.setVisibility(View.GONE);
            binding.empty.setVisibility(View.VISIBLE);
           // binding.generateCode.setVisibility(View.GONE);
        }
    }

    public void shareFile(){
        Bitmap b=((BitmapDrawable)binding.image.getDrawable()).getBitmap();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        Log.d("KOP", "onCreate: "+ b);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        Log.d("KOP", "onCreate: "+ b);
        String pathway = MediaStore.Images.Media.insertImage(getContentResolver(), b, "Qrcode", null);
        Log.d("KOP", "onCreate: "+ pathway);
        Uri imageUri =  Uri.parse(pathway);
        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(share, "Select"));
    }

    public void checkPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Permission")
                        .setMessage("Please you need to accept permission, before you can share QRcode")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
                                // Continue with delete operation
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.

                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            shareFile();
            // Permission has already been granted
        }
    }

    private void generateCode(String savedString, boolean value){

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(savedString, BarcodeFormat.QR_CODE, 400, 400);
            ImageView imageViewQrCode = (ImageView) findViewById(R.id.image);
            imageViewQrCode.setImageBitmap(bitmap);
            if(value){
            Toast.makeText(this, "Profile generated", Toast.LENGTH_SHORT).show();

            }
        } catch(Exception e) {
            Toast.makeText(this, "Unable to generate code", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_contact) {
                startActivity(new Intent(getApplicationContext(), ContactActivity.class));
            return true;
        }else  if (id == R.id.action_setup_profile) {
            launchSomeActivity.launch(new Intent(getApplicationContext(), InfoActivity.class));
            return true;
        }else if(id == R.id.share_others){
            startActivity(new Intent(getApplicationContext(), OtherShareActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void pickFile(){

        Intent data = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        data.addCategory(Intent.CATEGORY_OPENABLE);
        data.setType("*/*");
        data = Intent.createChooser(data, "Choose a File");
        sActivityResultLauncher.launch(data);

    }

    ActivityResultLauncher<Intent> sActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                       Uri uri = data.getData();
                       String content = "";
                       try{
                           Bitmap bitmap =  getDriveFilePath(uri ,MainActivity.this);
                            content = getContentFromImage(bitmap);
                           Paper.book().write("recieved", content);
                           startActivity(new Intent(getApplicationContext(), RecievedContactActivity.class));
                       }catch (Exception e){
                           Toast.makeText(MainActivity.this, "Unable to get the content, please try again later", Toast.LENGTH_SHORT).show();
                       }



                    }
                }
            }
    );

//    private void getThumbnail(String filepath){
//        try {
////            AppSettings.init(args);
//            Thumbnailer.start();
//            File in = new File(filepath);
//            if(in.exists()) {
//                ThumbnailCandidate candidate = new ThumbnailCandidate(in,"unique_code");
//
//                Thumbnailer.createThumbnail(candidate, new ThumbnailListener() {
//                    @Override
//                    public void onThumbnailReady(String hash, File thumbnail) {
//                        System.out.println("FILE created in : " + thumbnail.getAbsolutePath());
//                    }
//
//                    @Override
//                    public void onThumbnailFailed(String hash, String message, int code) {
//
//                    }
//                });
//            }
//        } catch (IOException | ThumbnailerException e) {
//            e.printStackTrace();
//        }
//    }

    private  Bitmap getDriveFilePath(Uri uri, Context context) {
        Bitmap myBitmap = null;
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
       File file = new File(context.getCacheDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
             myBitmap = BitmapFactory.decodeFile(file.getPath());
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Path", "Name: " + file.getName() );

           // fileName = file.getName();
            filepath = file.getPath();
           // fileSize = file.length();



        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }

        return myBitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    shareFile();
                  //  Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();

                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Permission")
                            .setMessage("Please you need to accept permission, before you can share QRcode")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton("Dismiss", null)

                            // A null listener allows the button to dismiss the dialog and take no further action.

                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public String getContentFromImage(Bitmap bMap){


        String contents = "";

        int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];
//copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        Result result = null;
        try {
            result = reader.decode(bitmap);
            contents = result.getText();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return contents;
    }
}