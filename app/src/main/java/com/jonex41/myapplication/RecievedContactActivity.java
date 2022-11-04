package com.jonex41.myapplication;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jonex41.myapplication.adapters.ContactAdapter;
import com.jonex41.myapplication.adapters.RecievedAdapter;
import com.jonex41.myapplication.databinding.RecievedContactBinding;
import com.jonex41.myapplication.models.ContactModel;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class RecievedContactActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_WRITE_CONTACTS = 23333;
    private RecievedContactBinding binding;
    String forSplitting = " &%# ";
    private RecievedAdapter adapter;
    List<ContactModel> list = new ArrayList<>();
    private int PERMISSION_REQUEST_CONTACT = 8;
    private boolean addallVisible = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.recieved_contact);
        setSupportActionBar(binding.toolbar.toolbar);
        getSupportActionBar().setTitle("");
        checkPermission();
        iniRecyclerView();
        binding.toolbar.backBtn.setOnClickListener(view -> {
            finish();
        });

        binding.toolbar.viewTxt.setText("Recieved information");
      String recievedString = Paper.book().read("recieved", "");
        if(!TextUtils.isEmpty(recievedString)){
            doFormatting(recievedString);
        }

        binding.btnText.setOnClickListener(view -> {
            String value = binding.text.getText().toString();
            shareText(value.trim());
           /* ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Aguda Text", binding.textCopy.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Text copied to Clipboard", Toast.LENGTH_SHORT).show();
       */
        });

        binding.btnLink.setOnClickListener(view -> {
            String value = binding.linkText.getText().toString();

            if (!value.startsWith("http://") && !value.startsWith("https://"))
                value = "http://" + value.trim();

            shareText(value);
        });
    }

    public void shareText( String value){



        /*Create an ACTION_SEND Intent*/
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        /*This will be the actual content you wish you share.*/

        /*The type of the content is text, obviously.*/
        intent.setType("text/plain");
        /*Applying information Subject and Body.*/
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, value);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, value);
        /*Fire!*/
        startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
    }

    public void checkPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(RecievedContactActivity.this,
                Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(RecievedContactActivity.this,
                    Manifest.permission.WRITE_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(RecievedContactActivity.this)
                        .setTitle("Permission")
                        .setMessage("Please you need to accept permission, before you can share QRcode")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(RecievedContactActivity.this,
                                        new String[]{Manifest.permission.WRITE_CONTACTS},
                                        MY_PERMISSIONS_WRITE_CONTACTS);
                                // Continue with delete operation
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.

                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(RecievedContactActivity.this,
                        new String[]{Manifest.permission.WRITE_CONTACTS},
                        MY_PERMISSIONS_WRITE_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
           // shareFile();
            // Permission has already been granted
        }
    }


    private void iniRecyclerView() {

        binding.recyclerview.setHasFixedSize(true);

        //viewModel
        //viewModel

        //set adapter
        adapter = new RecievedAdapter(RecievedContactActivity.this, list, false);

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setAdapter(adapter);

        //storeViewModel
        //view model



    }


    private void doFormatting(String recievedString) {
      //  Log.d("PROW", "getAllContactStuff: "+ recievedString);
        if(recievedString.split(" ")[0].equals("Profile-")){

            getProfileStuff(recievedString);

        }else if (recievedString.split(" ")[0].equals("Contact-")){

            getAllContactStuff(recievedString);
        }else if (recievedString.split(" ")[0].equals("link-")){
            addallVisible= false;

            invalidateOptionsMenu();


                    binding.linkLayout.setVisibility(View.VISIBLE);
            String newString =recievedString.replaceFirst("link-", "");
            binding.linkText.setText(newString);

        }else if (recievedString.split(" ")[0].equals("Random-")){
            addallVisible= false;
           // Toast.makeText(this, "Random", Toast.LENGTH_SHORT).show();

            invalidateOptionsMenu();
            String newString =recievedString.replaceFirst("Random-", "");
            binding.textLayout.setVisibility(View.VISIBLE);
          //  Toast.makeText(this, ""+ newString, Toast.LENGTH_SHORT).show();
            binding.text.setText(newString);
        }else {
            Toast.makeText(this, "Please Qrcode is from an unknown source", Toast.LENGTH_SHORT).show();
        }
    }

    private void getAllContactStuff(String recievedString) {
        /*for(int i = 0; i < listt.size(); i++){
            if(i == 0){
                longString = "Contact- "+"@n@"+listt.get(i).getName()+"--"+listt.get(i).getNumber();
            }else {
                longString = " @n@"+listt.get(i).getName()+"--"+listt.get(i).getNumber();
            }
        }*/
        String newString =recievedString.replaceFirst("Contact- ", "");
        Log.d("PROW", "getAllContactStuff: "+ newString);
        String[] allContact = newString.split("@n@");
            for(String oneContact : allContact){
                if(!TextUtils.isEmpty(oneContact)){
                    Log.d("PROW", "getAllContactStuff: "+ oneContact);
                    String[] contact = oneContact.split("--");
                    Log.d("PROW", "getAllContactStuff: "+ contact[0]);
                    ContactModel model = new ContactModel();
                    model.setName(contact[0]);
                    model.setNumber(contact[1]);
                    list.add(model);
                }


            }
            adapter.notifyDataSetChanged();

    }

    private void getProfileStuff(String recievedString) {

        if(!TextUtils.isEmpty(recievedString)){
            String newString =recievedString.replaceFirst("Profile- ", "");
            String[] myList = newString.split(forSplitting);
            try{

                for(String valr : myList){



                }

                String[] name = myList[0].split("n -");
                String[] firstContact = myList[1].split("f -");
                String[] secondContact = myList[2].split("s -");
                String[] gmail = myList[3].split("e -");
                Log.d("HHHW", "getIntentStuf   "+ name[1].trim());
                ContactModel model = new ContactModel();

                if(!name[1].trim().equals("n"))model.setName(name[1].trim());
                String phone = "Phone 1 - ";

                if(!firstContact[1].trim().equals("n")) phone= phone+firstContact[1].trim();
                if(!secondContact[1].trim().equals("n"))phone = phone+", Phone 2 - "+secondContact[1].trim();
                model.setNumber(phone);
                if(!gmail[1].trim().equals("n"))model.setGmail(gmail[1].trim());

                list.add(model);
                adapter.notifyDataSetChanged();

            }catch (Exception e){

            }
        }
    }


  /*  public void addContact() {
        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
        operationList.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // first and last names
        operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, "jjjjjj")
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, "jjjjjjj")
                .build());

        operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "0000000000000000")
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build());
        operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)

                .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, "ggg@gmail.com")
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build());

        try{
            ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList);
        }catch(Exception e){
            e.printStackTrace();
        }
    }*/
    public void checkPermission(String name, String phoneNumber,String phoneNumber2, String email){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            // Do something ...
            buttonAddContact(name, phoneNumber, phoneNumber2, email);
        }else {
            askForContactPermission();
        }
    }
/*
    public void addContact(){

        String DisplayName = "johnjohnjohn";
        String MobileNumber = "44404303";
        String HomeNumber = "122334";
        String WorkNumber = "22444422";
        String emailID = "email@nomail.com";
        String company = "bad";
        String jobTitle = "Programmer";

        ArrayList < ContentProviderOperation > ops = new ArrayList < ContentProviderOperation > ();

        ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names
        if (DisplayName != null) {
            ops.add(ContentProviderOperation.newInsert(
                            ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            DisplayName).build());
        }

        //------------------------------------------------------ Mobile Number
        if (MobileNumber != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        //------------------------------------------------------ Home Numbers
        if (HomeNumber != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, HomeNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                    .build());
        }

        //------------------------------------------------------ Work Numbers
        if (WorkNumber != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, WorkNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                    .build());
        }

        //------------------------------------------------------ Email
        if (emailID != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        //------------------------------------------------------ Organization
        if (!company.equals("") && !jobTitle.equals("")) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .build());
        }

        // Asking the Contact provider to create a new contact
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
*/

    public void buttonAddContact( String name, String phoneNumber,String phoneNumber2, String email){
        ArrayList<ContentProviderOperation> contentProviderOperations
                = new ArrayList<ContentProviderOperation>();

        contentProviderOperations.add(ContentProviderOperation.newInsert(
                        ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // Adding Name
        contentProviderOperations.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                       name)
                .build());

        // Adding Number
        contentProviderOperations.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                       phoneNumber)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                .build());

        if(!TextUtils.isEmpty(phoneNumber2)){
            contentProviderOperations.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                            phoneNumber2)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                    .build());

        }



        if(!TextUtils.isEmpty(email)){
            contentProviderOperations.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA,
                            email)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE,
                            ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());

        }


      /*  if (emailID != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }*/

        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, contentProviderOperations);
        } catch (OperationApplicationException e) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (RemoteException e) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void askForContactPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CONTACTS)) {
                    showDialog();
                }
            }
        } else {
            showDialog();
        }
    }


    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Contacts access needed");
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setMessage("please confirm Contacts access");
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // Only call the permission request api on Android M
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, PERMISSION_REQUEST_CONTACT);
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      //  buttonAddContact(name, phoneNumber, phoneNumber2, email);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recieved_layout, menu);

        MenuItem addall = menu.findItem(R.id.add_all);

        addall.setVisible(addallVisible);
         addall.setTitle(Html.fromHtml("<font color='#ffffff'>"+"Add all"+"</font>"));


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        //noinspection SimplifiableIfStatement
        if (id == R.id.add_all) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add All contact");
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                   try{
                       for(ContactModel contactModel : list){

                           String phone1 = "";
                           String phone2 = "";
                           if(contactModel.getNumber().contains("Phone 2")|| contactModel.getNumber().contains("Phone 2 - ")){
                               //  Toast.makeText(context, "i am heree", Toast.LENGTH_SHORT).show();
                               String[] list = contactModel.getNumber().split("Phone 2 - ");
                               phone1 = list[0].split("one 1 - ")[1].trim();
                               if(phone1.contains(",")){
                                   phone1 = phone1.substring(0, phone1.length() - 1);
                               }
                               phone2 = list[1];


                           }else {
                               // Toast.makeText(context, "not in that place", Toast.LENGTH_SHORT).show();
                               phone1 = contactModel.getNumber().split("one 1 - ")[1].trim();
                           }

                           checkPermission(contactModel.getName(), phone1,phone2, contactModel.getGmail());




                       }
                       Toast.makeText(RecievedContactActivity.this, "All contact added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                   }catch (Exception e){
                       Toast.makeText(RecievedContactActivity.this, "Please unable to add contacts", Toast.LENGTH_SHORT).show();
                   }

                }
            });
            builder.setMessage("Are you sure you want to add all contact");

            builder.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
