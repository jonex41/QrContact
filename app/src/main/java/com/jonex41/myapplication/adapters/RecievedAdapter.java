package com.jonex41.myapplication.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jonex41.myapplication.ContactActivity;
import com.jonex41.myapplication.R;
import com.jonex41.myapplication.RecievedContactActivity;
import com.jonex41.myapplication.SelectedContactActivity;
import com.jonex41.myapplication.databinding.ContactItemLayoutBinding;
import com.jonex41.myapplication.databinding.RecievedItemLayoutBinding;
import com.jonex41.myapplication.models.ContactModel;

import java.util.List;

public class RecievedAdapter  extends RecyclerView.Adapter<RecievedAdapter.MyViewHolder>{

    private List<ContactModel> contactModels;
    private LayoutInflater layoutInflater;

    private Context context;
    private boolean showCancel;


    public RecievedAdapter(Context context, List<ContactModel> contactModels, boolean showCancel) {

        this.contactModels = contactModels;

        this.context = context;
        this.showCancel = showCancel;
    }

    @NonNull
    @Override
    public RecievedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        RecievedItemLayoutBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.recieved_item_layout,
                parent,
                false);

                /* DataBindingUtil.inflate(
                layoutInflater,
                R.layout.contact_item_layout,
                parent,
                false
        );*/

        return new RecievedAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecievedAdapter.MyViewHolder holder, int position) {

        holder.bindSupplier(contactModels.get(position), position);
    }

    @Override
    public int getItemCount() {
        return contactModels.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private RecievedItemLayoutBinding binding;

        public MyViewHolder(RecievedItemLayoutBinding binding){

            super(binding.getRoot());
            this.binding = binding;

        }

        public void bindSupplier(ContactModel contactModel, int position){

            //bind data

            // binding.setPurchase(purchase);
            binding.executePendingBindings();
            
            binding.addButton.setOnClickListener(view -> {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Add All contact");
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       try{
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
                           ((RecievedContactActivity)context).checkPermission(contactModel.getName(), phone1,phone2, contactModel.getGmail());
                           Toast.makeText(context, "All contact added successfully", Toast.LENGTH_SHORT).show();
                       }catch (Exception e){
                           Toast.makeText(context, "Please unable to add contact", Toast.LENGTH_SHORT).show();
                       }

                    }
                });
                builder.setMessage("Are you sure you want to add all contact");

                builder.show();
               // Log.d("FrOG", "bindSupplier: "+contactModel.getNumber());
              //  Toast.makeText(context, "i am here", Toast.LENGTH_SHORT).show();
                 });

            binding.contactName.setText(contactModel.getName());
            binding.contactNumber.setText(contactModel.getNumber());
            binding.contactEmail.setText(contactModel.getGmail());
            final Drawable drawable = ContextCompat.getDrawable(context, R.drawable.selected_background).mutate();
            final Drawable drawable2 = ContextCompat.getDrawable(context, R.drawable.rectanguler_background).mutate();

           // binding.view.setBackground(contactModel.isSelected() ? drawable : drawable2);
            // setBackgroundColor(contactModel.isSelected() ? drawable : drawable2);
            binding.view.setOnClickListener(view -> {

               /* if( ((RecievedContactActivity)context).getAllSelectedContact().size() >= 201){
                    Toast.makeText(context, "Please you cant select more than 200 Contacts", Toast.LENGTH_LONG).show();
                    return;
                }

                contactModel.setSelected(!contactModel.isSelected());

                binding.view.setBackground(contactModel.isSelected() ? drawable : drawable2);
                //setBackgroundColor(contactModel.isSelected() ? context.getResources().getColor(R.drawable.selected_background) : Color.WHITE);
                ((ContactActivity)context).addRemoveNew(contactModel);*/
            });


        }

    }

}

