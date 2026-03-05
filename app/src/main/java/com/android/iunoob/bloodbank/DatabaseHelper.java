package com.android.iunoob.bloodbank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BloodBank.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_USERS = "users";
    public static final String TABLE_POSTS = "posts";

    public static final String KEY_USER_ID = "id";
    public static final String KEY_USER_NAME = "name";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_USER_PHONE = "phone";
    public static final String KEY_USER_PASSWORD = "password";
    public static final String KEY_USER_ADDRESS = "address";
    public static final String KEY_USER_GENDER = "gender";
    public static final String KEY_USER_BLOOD_GROUP = "blood_group";
    public static final String KEY_USER_DIVISION = "division";
    public static final String KEY_USER_IS_DONOR = "is_donor";
    public static final String KEY_USER_LAST_DONATE = "last_donate_date";
    public static final String KEY_USER_TOTAL_DONATE = "total_donations";

    public static final String KEY_POST_ID = "id";
    public static final String KEY_POST_USER_NAME = "user_name";
    public static final String KEY_POST_CONTACT = "contact";
    public static final String KEY_POST_ADDRESS = "address";
    public static final String KEY_POST_DIVISION = "division";
    public static final String KEY_POST_BLOOD_GROUP = "blood_group";
    public static final String KEY_POST_TIME = "time";
    public static final String KEY_POST_DATE = "date";
    public static final String KEY_POST_USER_EMAIL = "user_email";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_USER_NAME + " TEXT," +
                KEY_USER_EMAIL + " TEXT UNIQUE," +
                KEY_USER_PHONE + " TEXT," +
                KEY_USER_PASSWORD + " TEXT," +
                KEY_USER_ADDRESS + " TEXT," +
                KEY_USER_GENDER + " INTEGER," +
                KEY_USER_BLOOD_GROUP + " INTEGER," +
                KEY_USER_DIVISION + " INTEGER," +
                KEY_USER_IS_DONOR + " INTEGER," +
                KEY_USER_LAST_DONATE + " TEXT," +
                KEY_USER_TOTAL_DONATE + " INTEGER" + ");";

        String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_POSTS + "(" +
                KEY_POST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_POST_USER_NAME + " TEXT," +
                KEY_POST_CONTACT + " TEXT," +
                KEY_POST_ADDRESS + " TEXT," +
                KEY_POST_DIVISION + " TEXT," +
                KEY_POST_BLOOD_GROUP + " TEXT," +
                KEY_POST_TIME + " TEXT," +
                KEY_POST_DATE + " TEXT," +
                KEY_POST_USER_EMAIL + " TEXT" + ");";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_POSTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
            onCreate(db);
        }
    }

    public boolean addUser(String name, String email, String phone, String password, String address,
                           int gender, int bloodGroup, int division, boolean isDonor) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, name);
        values.put(KEY_USER_EMAIL, email);
        values.put(KEY_USER_PHONE, phone);
        values.put(KEY_USER_PASSWORD, password);
        values.put(KEY_USER_ADDRESS, address);
        values.put(KEY_USER_GENDER, gender);
        values.put(KEY_USER_BLOOD_GROUP, bloodGroup);
        values.put(KEY_USER_DIVISION, division);
        values.put(KEY_USER_IS_DONOR, isDonor ? 1 : 0);
        values.put(KEY_USER_LAST_DONATE, "N/A");
        values.put(KEY_USER_TOTAL_DONATE, 0);

        long id = db.insert(TABLE_USERS, null, values);
        return id != -1;
    }

    public void updateDonationHistory(String email, String lastDonateDate, int totalDonations) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_LAST_DONATE, lastDonateDate);
        values.put(KEY_USER_TOTAL_DONATE, totalDonations);
        db.update(TABLE_USERS, values, KEY_USER_EMAIL + "=?", new String[]{email});
    }

    public boolean authenticate(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS, new String[]{KEY_USER_ID},
                    KEY_USER_EMAIL + "=? AND " + KEY_USER_PASSWORD + "=?",
                    new String[]{email, password}, null, null, null);
            return cursor != null && cursor.moveToFirst();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS, new String[]{KEY_USER_ID},
                    KEY_USER_EMAIL + "=?", new String[]{email}, null, null, null);
            return cursor != null && cursor.moveToFirst();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, KEY_USER_EMAIL + "=?", new String[]{email}, null, null, null);
    }

    public void updateUser(String email, String name, String phone, String address, int gender, int bloodGroup,
                           int division, boolean isDonor) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, name);
        values.put(KEY_USER_PHONE, phone);
        values.put(KEY_USER_ADDRESS, address);
        values.put(KEY_USER_GENDER, gender);
        values.put(KEY_USER_BLOOD_GROUP, bloodGroup);
        values.put(KEY_USER_DIVISION, division);
        values.put(KEY_USER_IS_DONOR, isDonor ? 1 : 0);
        db.update(TABLE_USERS, values, KEY_USER_EMAIL + "=?", new String[]{email});
    }

    public boolean addPost(String name, String contact, String address, String division, String bloodGroup,
                           String time, String date, String userEmail) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_POST_USER_NAME, name);
        values.put(KEY_POST_CONTACT, contact);
        values.put(KEY_POST_ADDRESS, address);
        values.put(KEY_POST_DIVISION, division);
        values.put(KEY_POST_BLOOD_GROUP, bloodGroup);
        values.put(KEY_POST_TIME, time);
        values.put(KEY_POST_DATE, date);
        values.put(KEY_POST_USER_EMAIL, userEmail);

        long id = db.insert(TABLE_POSTS, null, values);
        return id != -1;
    }

    public Cursor getAllPosts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_POSTS, null, null, null, null, null, KEY_POST_ID + " DESC");
    }

    public Cursor searchDonors(int bloodGroupPosition, int divisionPosition) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = KEY_USER_BLOOD_GROUP + " = ? AND " + KEY_USER_DIVISION + " = ? AND " + KEY_USER_IS_DONOR + " = 1";
        String[] selectionArgs = { String.valueOf(bloodGroupPosition), String.valueOf(divisionPosition) };
        return db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);
    }
}
