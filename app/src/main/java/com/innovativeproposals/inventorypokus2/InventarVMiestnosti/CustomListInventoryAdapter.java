package com.innovativeproposals.inventorypokus2.InventarVMiestnosti;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.innovativeproposals.inventorypokus2.Models.Inventar;
import com.innovativeproposals.inventorypokus2.R;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomListInventoryAdapter extends ArrayAdapter<Inventar> {
    Context context;
    int layoutResourceId;
    List<Inventar> original_data = new ArrayList<Inventar>();
    List<Inventar> filtered_list = new ArrayList<Inventar>();


    public CustomListInventoryAdapter(@NonNull Context context, int resource, @NonNull List<Inventar> data) {
        super(context, resource, data);

        this.layoutResourceId = resource;
        this.context = context;
        this.original_data = data;
        this.filtered_list.addAll(data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ImageView image = null;
        TextView itemBarcode = null;
        TextView itemDescription = null;
        TextView itemStatus = null;
        TextView itemDatum = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }

        image = row.findViewById(R.id.detailView_Image);
        itemBarcode = row.findViewById(R.id.itembarcodeET);
        itemDescription = row.findViewById(R.id.itemdescriptionET);
        itemStatus = row.findViewById(R.id.statusET);
        itemDatum = row.findViewById(R.id.datumET);

        Inventar inventar = filtered_list.get(position);
        itemBarcode.setText(inventar.getItemBarcode());
        itemDescription.setText(inventar.getItemDescription());
        itemStatus.setText(inventar.getStatus());
        itemDatum.setText(inventar.getDatum());

        // ak status = 10 tak zmen farbu na zelenu // status bol null, row
        if(inventar.getStatus() != null)
            row.setBackgroundColor(Color.rgb(237,255,216)); //android:background="#DCEDC8"
        else
            row.setBackgroundColor(Color.WHITE); //android:background="#DCEDC8"

        //ulozenie ID-cka do riadku; ale mozeme sem ulozit aj cely objekt inventara (toto moze byy overkill pri vacsom obsahu dat)
        row.setTag(inventar.getId());

        if(inventar.getImage() != null && inventar.getImage().length > 1) {
            ByteArrayInputStream imageStream = new ByteArrayInputStream(inventar.getImage());
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            image.setImageBitmap(theImage);
        } else {
            image.setImageBitmap(null);
        }
        return row;

    }

    @Override
    public int getCount() {
        return filtered_list.size();
    }

    public void filter(final String searchText){
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Clear the filter list
                filtered_list.clear();

                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(searchText)) {

                    filtered_list.addAll(original_data);

                } else {
                    // Iterate in the original List and add it to filter list...
                    for (Inventar item : original_data) {
                        if (item.getItemDescription().toLowerCase().contains(searchText.toLowerCase())) {
                            // Adding Matched items
                            filtered_list.add(item);
                        }
                    }
                }

                // Set on UI Thread
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notify the List that the DataSet has changed...
                        notifyDataSetChanged();
                    }
                });

            }
        }).start();
    }
}
