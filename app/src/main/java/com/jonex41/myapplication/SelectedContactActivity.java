package com.jonex41.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jonex41.myapplication.adapters.ContactAdapter;
import com.jonex41.myapplication.databinding.SelectedContactLayoutBinding;
import com.jonex41.myapplication.models.ContactModel;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class SelectedContactActivity extends AppCompatActivity {
    private List<ContactModel> list = new ArrayList<>();

    private SelectedContactLayoutBinding binding;
    private ContactAdapter adapter;
    private boolean doneOwn = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = DataBindingUtil.setContentView(this, R.layout.selected_contact_layout);
        setSupportActionBar(binding.toolbar.toolbar);
        getSupportActionBar().setTitle("");
        binding.toolbar.viewTxt.setText("Selected Contact");
        initStuff();
        getIncomingIntent();
        binding.toolbar.backBtn.setOnClickListener(view -> {

            finish();
        });
    }
    private void getIncomingIntent() {

        List<ContactModel> listt = Paper.book().read("selected");
            if(!listt.isEmpty()){
                for(ContactModel model : listt){
                    model.setSelected(false);
                    list.add(model);
                }

                adapter.notifyDataSetChanged();
            }

    }
    private void initStuff() {
        binding.recyclerview.setHasFixedSize(true);


        //set adapter
        adapter = new ContactAdapter(SelectedContactActivity.this, list, true);

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.selected_menu, menu);

        MenuItem doneMenu = menu.findItem(R.id.done);


       // countMenu.setTitle(Html.fromHtml("<font color='#ffffff'>"+count+"</font>"));

        doneMenu.setVisible(doneOwn);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        //noinspection SimplifiableIfStatement
         if (id == R.id.done) {
             Paper.book().write("picked", list);
            Intent intent = new Intent(getApplicationContext(), SelectedContactQrActivity.class);
            //  intent.putExtra("key",(ArrayList<ContactModel>)listCopy);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void removeFromList(ContactModel contactModel, int position) {

        list.remove(contactModel);
        adapter.notifyItemRemoved(position);
        /*list.remove(position);
        adapter.notifyItemRemoved(position);*/
        if(list.isEmpty()){
            doneOwn = false;
            invalidateOptionsMenu();

        }
    }
}
