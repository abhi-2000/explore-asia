package com.example.exploringasia.Room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Information.class} , version = 10,exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {
    public abstract CountryAsiaDao countryAsiaDao();

}
