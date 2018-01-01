package com.innovativeproposals.inventorypokus2.InventarVMiestnosti;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
    List<Inventar> data = new ArrayList<Inventar>();

    public CustomListInventoryAdapter(@NonNull Context context, int resource, @NonNull List<Inventar> data) {
        super(context, resource, data);

        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;
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

        Inventar inventar = data.get(position);
        itemBarcode.setText(inventar.getItemBarcode());
        itemDescription.setText(inventar.getItemDescription());
        itemStatus.setText(inventar.getStatus());
        itemDatum.setText(inventar.getDatum());

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
        return data.size();
    }
}
