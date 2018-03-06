package com.innovativeproposals.inventorypokus2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lubos on 04.02.18.
 */

public class Constants {

    public static final String PREF_FILE_NAME = ".my_pref_file";
    public static final String KEY_ADDRESS = "ip_address";
    public static final String KEY_PORT = "port";
    public static final String KEY_DAYS_OF_INVENTORY = "days_of_inventory";

    public static final String INTENT_INVENTORY = "inventar_object";


    public static final String FILE_DATA_PC2PDA = "transferDataPC2iPOD.db";
    public static final String FILE_DATA_PDA2PC = "transferDataiPOD2PC.db";
    public static final String FILE_DATABASE = "inventory.db";


    public static List<String> spinnerZodpovedneOsoby = new ArrayList<String>(); // nacitaj dynamicky  databazy
    public static List<String> spinnerListTypyMajetku = new ArrayList<String>(); // nacitaj dynamicky  z databazy

}

// ukrytie klavesnice
// http://karimvarela.com/2012/07/24/android-how-to-hide-keyboard-by-touching-screen-outside-keyboard/

