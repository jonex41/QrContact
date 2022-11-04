package com.jonex41.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aminography.commonutils.ContactData;
import com.aminography.commonutils.ContactUtilsKt;
import com.jonex41.myapplication.VModels.ContactViewModel;
import com.jonex41.myapplication.adapters.ContactAdapter;
import com.jonex41.myapplication.databinding.ContactLayoutBinding;
import com.jonex41.myapplication.models.ContactModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.paperdb.Paper;

public class ContactActivity extends AppCompatActivity {

    private ContactLayoutBinding binding;
    private final int REQUEST_CODE_READ_CONTACTS = 17;
    List<ContactModel> list = new ArrayList<>();
    List<ContactModel> listSearch = new ArrayList<>();
    List<ContactModel> listSearchNew = new ArrayList<>();
    List<ContactModel> listCopy = new ArrayList<>();
    private ContactAdapter adapter;
    private Menu menu;
    private boolean refreshOwn = true;
    private boolean doneOwn = false;
    private int count = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = DataBindingUtil.setContentView(this, R.layout.contact_layout);
        setSupportActionBar(binding.toolbar.toolbar);
        getSupportActionBar().setTitle("");
        initStuff();
        doEditTextStuff();
        binding.progressLoader.show();
        //dobackgroundStuff();
        List<ContactModel> listj = Paper.book().read("contact");
        if(listj != null){
            if(!listj.isEmpty()){
                list.addAll(listj);
                adapter.notifyDataSetChanged();
                binding.progressLoader.hide();
                listSearch.addAll(listj);
            }else {
                checkPermissionAndShow();
            }

        }else {
            checkPermissionAndShow();


        }



    }

    private void doEditTextStuff() {
        binding.edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String value = charSequence.toString();


                if (i2 ==0) {
                    list.clear();

                    Collections.sort(listSearch);
                    list.addAll(listSearch);
                    adapter.notifyDataSetChanged();
                    return;
                }

                    listSearchNew.clear();
                    for(ContactModel model : listSearch){
                      //  Toast.makeText(ContactActivity.this, "i am still here", Toast.LENGTH_SHORT).show();
                        if(model.getName().contains(value)){
                        listSearchNew.add(model);
                        }
                   }
                list.clear();
                    list.addAll(listSearchNew);
                    adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void dobackgroundStuff() {
        new Thread(() -> {

            runOnUiThread(() -> {
                // change UI elements here

            });

        });
    }

    private void initStuff() {

        binding.toolbar.viewTxt.setText("My Contacts");
        iniRecyclerView();
        binding.toolbar.backBtn.setOnClickListener(view -> {

            finish();
        });
    }

    private void iniRecyclerView() {

        binding.recyclerview.setHasFixedSize(true);

        //viewModel
        //viewModel

        //set adapter
        adapter = new ContactAdapter(ContactActivity.this, list, false);

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setAdapter(adapter);

        //storeViewModel
        //view model



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_CONTACTS) {
            checkPermissionAndShow();
        }
    }

    private void checkPermissionAndShow() {
        binding.progressLoader.show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

            ContactViewModel contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
            contactViewModel.getAllContact(ContactActivity.this).observe(this, response -> {
                    if(list != null){
                        list.clear();
                        listSearch.clear();
                    }
                if(response != null){
                   Paper.book().write("contact", response);
                    list.addAll(response);
                    Collections.sort(list);
                    adapter.notifyDataSetChanged();
                    binding.progressLoader.hide();
                    listSearch.addAll(response);

                }else {
                    binding.progressLoader.hide();
                    Toast.makeText(this, "Unable to get Contact", Toast.LENGTH_SHORT).show();
                }



            });;
           // textView.setText(sb.toString());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contact_menu, menu);
        MenuItem doneMenu = menu.findItem(R.id.done);
        MenuItem refreshMenu = menu.findItem(R.id.refresh);
        MenuItem countMenu = menu.findItem(R.id.count);
        MenuItem cancelMenu = menu.findItem(R.id.cancel);
        refreshMenu.setVisible(refreshOwn);
        doneMenu.setVisible(doneOwn);
        cancelMenu.setVisible(doneOwn);
        countMenu.setTitle(Html.fromHtml("<font color='#ffffff'>"+count+"</font>"));

        countMenu.setVisible(doneOwn);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            binding.progressLoader.show();
            checkPermissionAndShow();

            return true;
        }else  if (id == R.id.done) {
            Paper.book().write("selected",listCopy );
            if(listCopy.isEmpty()){
                Toast.makeText(this, "Please make sure atleast one contact is selected", Toast.LENGTH_SHORT).show();
                return true;
            }
            Intent intent = new Intent(getApplicationContext(), SelectedContactActivity.class);
          //  intent.putExtra("key",(ArrayList<ContactModel>)listCopy);
            startActivity(intent);

            return true;
        }else if(id == R.id.cancel){
            for(ContactModel model : listCopy){
                model.setSelected(false);
               int pos= list.indexOf(model);
                list.get(pos).setSelected(false);
              // listCopy.get( listCopy.indexOf(model)).setSelected(false);
               adapter.notifyItemChanged(pos, model);

            }
            listCopy.clear();
           // adapter.notifyDataSetChanged();
            doneOwn= false;
            invalidateOptionsMenu();
            return  true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addRemoveNew(ContactModel model){

        if(!listCopy.contains(model)){
            listCopy.add(model);
        }else {
            listCopy.remove(model);
        }

        int countD = listCopy.size();
        if(countD >0){
            count = countD;
            doneOwn = true;
            refreshOwn = false;
            invalidateOptionsMenu();

        }else {
            doneOwn = false;
            refreshOwn = true;
            invalidateOptionsMenu();
        }
    }

    public List<ContactModel> getAllSelectedContact(){

       return listCopy;
    }



}
