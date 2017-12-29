package com.innovativeproposals.inventorypokus2.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lubos on 29.12.17.
 */

public class Inventar implements Parcelable {
    private int Id;
    private String itemBarcode;
    private String itemDescription;
    private String rommCode;
    private String status;
    private String datum;
    private String datum_added;
    private String datum_discarded;
    private String serialNr;
    private String zodpovednaOsoba;
    private String poznamka;
    private String typeMajetku;
    private byte[] image;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getItemBarcode() {
        return itemBarcode;
    }

    public void setItemBarcode(String itemBarcode) {
        this.itemBarcode = itemBarcode;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getRommCode() {
        return rommCode;
    }

    public void setRommCode(String rommCode) {
        this.rommCode = rommCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getDatum_added() {
        return datum_added;
    }

    public void setDatum_added(String datum_added) {
        this.datum_added = datum_added;
    }

    public String getDatum_discarded() {
        return datum_discarded;
    }

    public void setDatum_discarded(String datum_discarded) {
        this.datum_discarded = datum_discarded;
    }

    public String getSerialNr() {
        return serialNr;
    }

    public void setSerialNr(String serialNr) {
        this.serialNr = serialNr;
    }


    public String getZodpovednaOsoba() {
        return zodpovednaOsoba;
    }

    public void setZodpovednaOsoba(String zodpovednaOsoba) {
        this.zodpovednaOsoba = zodpovednaOsoba;
    }

    public String getPoznamka() {
        return poznamka;
    }

    public void setPoznamka(String poznamka) {
        this.poznamka = poznamka;
    }

    public String getTypeMajetku() {
        return typeMajetku;
    }

    public void setTypeMajetku(String typeMajetku) {
        this.typeMajetku = typeMajetku;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(itemBarcode);
        dest.writeString(itemDescription);
        dest.writeString(rommCode);
//        dest.writeString(status);
//        dest.writeString(datum);
//        dest.writeString(datum_added);
//        dest.writeString(datum_discarded);
//        dest.writeString(serialNr);
//        dest.writeString(zodpovednaOsoba);
//        dest.writeString(poznamka);
//        dest.writeString(typeMajetku);
        dest.writeByteArray(image);
    }

    public static final Parcelable.Creator<Inventar> CREATOR = new Parcelable.Creator<Inventar>() {

        @Override
        public Inventar createFromParcel(Parcel source) {
            return new Inventar(source);

        }

        @Override
        public Inventar[] newArray(int size) {
            return new Inventar[size];
        }
    };

    public Inventar() {
    }

    public Inventar(Parcel in) {
        this.Id = in.readInt();
        this.itemBarcode = in.readString();
        this.itemDescription = in.readString();
        this.rommCode = in.readString();


//        this.itemDescription = in.readString();
//        this.itemBarcode = in.readString();
//        this.rommCode = in.readString();
        this.image = in.createByteArray();
    }
}
