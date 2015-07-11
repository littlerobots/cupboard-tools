/*
 *    Copyright 2015 Little Robots
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package nl.littlerobots.example.provider;

import com.google.gson.Gson;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import nl.littlerobots.cupboard.tools.convert.ListFieldConverterFactory;
import nl.littlerobots.cupboard.tools.provider.CupboardContentProvider;
import nl.littlerobots.example.BuildConfig;
import nl.littlerobots.example.model.Cheese;
import nl.littlerobots.example.model.Plateau;
import nl.qbusict.cupboard.CupboardBuilder;
import nl.qbusict.cupboard.CupboardFactory;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MyProvider extends CupboardContentProvider {
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

    static {
        // register a ListFieldConverterFactory that will serialize any List<> to json in the database
        // using Gson
        CupboardFactory.setCupboard(new CupboardBuilder().
                registerFieldConverterFactory(new ListFieldConverterFactory(new Gson())).build());
        cupboard().register(Cheese.class);
        cupboard().register(Plateau.class);
    }

    public MyProvider() {
        super(AUTHORITY, 1);
    }

    @Override
    protected void onCreateDatabase(SQLiteDatabase db) {
        super.onCreateDatabase(db);
        String[] names = {"Gouda", "Brie", "Grevé", "Roquefort"};

        db.beginTransaction();
        try {
            for (String name : names) {
                Cheese cheese = new Cheese();
                cheese.name = name;
                cupboard().withDatabase(db).put(cheese);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        Plateau dutchPlate = new Plateau();
        dutchPlate.cheeses = new ArrayList<>(2);
        Cheese gouda = new Cheese();
        gouda.name = "Gouda";
        Cheese edammer = new Cheese();
        edammer.name = "Edammer";
        dutchPlate.cheeses.add(gouda);
        dutchPlate.cheeses.add(edammer);

        // note that the collections of cheeses in the cheeses array
        // won't be stored as separate entities here. They will be
        // embedded in the Plateau entity that is stored as json
        cupboard().withDatabase(db).put(dutchPlate);
    }
}
