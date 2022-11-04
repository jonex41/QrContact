package com.jonex41.myapplication.adapters;

import android.content.Context;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;


import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.jonex41.myapplication.ContactActivity;
import com.jonex41.myapplication.R;
import com.jonex41.myapplication.SelectedContactActivity;
import com.jonex41.myapplication.databinding.ContactItemLayoutBinding;
import com.jonex41.myapplication.models.ContactModel;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder>{

    private List<ContactModel> contactModels;
    private LayoutInflater layoutInflater;

    private Context context;
    private boolean showCancel;


    public ContactAdapter(Context context, List<ContactModel> contactModels, boolean showCancel) {

        this.contactModels = contactModels;

        this.context = context;
        this.showCancel = showCancel;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
         ContactItemLayoutBinding binding = DataBindingUtil.inflate(layoutInflater,
                 R.layout.contact_item_layout,
                 parent,
                 false);

                /* DataBindingUtil.inflate(
                layoutInflater,
                R.layout.contact_item_layout,
                parent,
                false
        );*/

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.bindSupplier(contactModels.get(position), position);
    }

    @Override
    public int getItemCount() {
        return contactModels.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private ContactItemLayoutBinding binding;

        public MyViewHolder(ContactItemLayoutBinding binding){

            super(binding.getRoot());
            this.binding = binding;

        }

        public void bindSupplier(ContactModel contactModel, int position){

            //bind data

           // binding.setPurchase(purchase);
            binding.executePendingBindings();
            if(showCancel){
                binding.cancelButton.setVisibility(View.VISIBLE);
            }else {
                binding.cancelButton.setVisibility(View.GONE);
            }
            binding.cancelButton.setOnClickListener(view -> {
                int pos = getAdapterPosition();

                // check if item still exists
                if(pos != RecyclerView.NO_POSITION){
                ((SelectedContactActivity)context).removeFromList(contactModel, pos);

                }
            });

            binding.contactName.setText(contactModel.getName());
            binding.contactNumber.setText(contactModel.getNumber());
            binding.contactEmail.setText(contactModel.getGmail());
            final Drawable drawable = ContextCompat.getDrawable(context, R.drawable.selected_background).mutate();
            final Drawable drawable2 = ContextCompat.getDrawable(context, R.drawable.rectanguler_background).mutate();

            binding.view.setBackground(contactModel.isSelected() ? drawable : drawable2);
                   // setBackgroundColor(contactModel.isSelected() ? drawable : drawable2);
            binding.view.setOnClickListener(view -> {

                if( ((ContactActivity)context).getAllSelectedContact().size() >= 80){
                    Toast.makeText(context, "Please you cant select more than 80 Contacts", Toast.LENGTH_LONG).show();
                    return;
                }

                contactModel.setSelected(!contactModel.isSelected());

                binding.view.setBackground(contactModel.isSelected() ? drawable : drawable2);
                        //setBackgroundColor(contactModel.isSelected() ? context.getResources().getColor(R.drawable.selected_background) : Color.WHITE);
                ((ContactActivity)context).addRemoveNew(contactModel);
            });


        }

    }

}
