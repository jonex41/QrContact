package com.jonex41.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import io.paperdb.Paper;

import com.jonex41.myapplication.databinding.ContactLayoutBinding;
import com.jonex41.myapplication.databinding.InfoLayoutBinding;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {

    private InfoLayoutBinding binding;
    String forSplitting = " &%# ";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.info_layout);

        initStuff();
    }

    private void initStuff() {

        binding.toolbar.viewTxt.setText("My Contact");

        binding.toolbar.backBtn.setOnClickListener(this);
        binding.submit.setOnClickListener(this);
        getIntentStuff();
    }

    private void getIntentStuff() {
        String savedString = Paper.book().read("save");
        if(!TextUtils.isEmpty(savedString)){
            String newString =savedString.replaceFirst("Profile- ", "");
            String[] myList = newString.split(forSplitting);
            try{

                for(String valr : myList){



                }

                String[] name = myList[0].split("n -");
                String[] firstContact = myList[1].split("f -");
                String[] secondContact = myList[2].split("s -");
                String[] gmail = myList[3].split("e -");
                Log.d("HHHW", "getIntentStuf   "+ name[1].trim());
                if(!name[1].trim().equals("n"))binding.name.setText(name[1].trim());
                if(!firstContact[1].trim().equals("n"))binding.firstContact.setText(firstContact[1].trim());
                if(!secondContact[1].trim().equals("n"))binding.secondContact.setText(secondContact[1].trim());
                if(!gmail[1].trim().equals("n"))binding.emailContact.setText(gmail[1].trim());

            }catch (Exception e){

            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.backBtn){
            finish();
        }else  if(view.getId() == R.id.submit){
            getTextFromViews();
        }
    }

    private void getTextFromViews() {
        String savedString= "";
        String name = "";
        String firstcontact = "";
        String secondContact = "";
        String email = "";

        if(!TextUtils.isEmpty(binding.name.getText().toString())){
            name = binding.name.getText().toString();
            savedString = "Profile- "+"n -"+name+ forSplitting;

        }else {

            binding.nameLayout.setError("Please the your name must be filled");
            return;
        }

        if(!TextUtils.isEmpty(binding.firstContact.getText().toString())){
            firstcontact = binding.firstContact.getText().toString();
            savedString = savedString+"f -"+firstcontact+forSplitting;
        }else {
            binding.firstContactLayout.setError("Please the your name must be filled");
            return;
        }
        if(!TextUtils.isEmpty(binding.secondContact.getText().toString())){
            secondContact = binding.secondContact.getText().toString();
            savedString = savedString+"s -"+secondContact+forSplitting;
        }else {
            savedString = savedString+"s -"+"n"+forSplitting;
        }

        if(!TextUtils.isEmpty(binding.emailContact.getText().toString())){
            email = binding.emailContact.getText().toString();
            savedString = savedString+"e -"+email+forSplitting;
        }else {
            savedString = savedString+"e -"+"n"+forSplitting;
        }
        saveInPref(savedString);
    }

    private void saveInPref(String savedString) {
        Paper.book().write("save", savedString);
        Intent returnIntent = getIntent();
        returnIntent.putExtra("result","d");
        setResult(RESULT_OK,returnIntent);
        finish();
    }
}
