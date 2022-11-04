package com.jonex41.myapplication.VModels;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aminography.commonutils.ContactData;
import com.aminography.commonutils.ContactUtilsKt;
import com.jonex41.myapplication.models.ContactModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ContactViewModel extends ViewModel {


    public ContactViewModel() {

    }
    MutableLiveData<List<ContactModel>> data = new MutableLiveData<>();

    //get all purchase
    public LiveData<List<ContactModel>> getAllContact(Context context) {

        ExecutorService service =  Executors.newSingleThreadExecutor();
        service.submit(new Runnable() {
            @Override
            public void run() {
                // on background thread, obtain a fresh list of users
                List<ContactData> contacts = ContactUtilsKt.retrieveAllContacts(context);
                List<ContactModel> anotherContact = new ArrayList<>();
                for (ContactData contact : contacts) {
                    ContactModel contactModel = new ContactModel();
                    contactModel.setName(contact.getName());
                    String num = "";
                    int i = 1;
                    for(String number : contact.getPhoneNumber()) {
                        if(i == 1){
                            num = "Phone "+i+" - "+number;
                        }else {
                            num = num+", Phone "+i+" - "+number;
                        }


                        i++;
                    }
                    contactModel.setNumber(num);
                    contactModel.setGmail(contact.getContactId()+"");
                    anotherContact.add(contactModel);
                }

                // now that you have the fresh user data in freshUserList,
                // make it available to outside observers of the "users"
                // MutableLiveData object
                data.postValue(anotherContact);
            }
        });
       // data.setValue(anotherContact);

        return data;
    }


}