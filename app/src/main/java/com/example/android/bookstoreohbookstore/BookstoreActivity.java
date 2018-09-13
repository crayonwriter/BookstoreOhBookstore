package com.example.android.bookstoreohbookstore;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.bookstoreohbookstore.data.BookContract;
import com.example.android.bookstoreohbookstore.data.BookContract.BookEntry;
import com.example.android.bookstoreohbookstore.data.BookDbHelper;

public class BookstoreActivity extends AppCompatActivity {

    /**
     * Database helper that will provide us access to the database
     */
    private BookDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookstore_activity);

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new BookDbHelper(this);
        displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the bookstore database.
     */
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
            android.provider.BaseColumns._ID,
            BookEntry.COLUMN_BOOK_PRICE,
            BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
            BookEntry.COLUMN_BOOK_SUPPLIER_PHONE,
            BookEntry.COLUMN_BOOK_QUANTITY,
            BookEntry.COLUMN_BOOK_TITLE
    };

Cursor cursor = db.query(
    BookEntry.TABLE_NAME,
    projection,
    null,
    null,
    null,
    null,
    null
        );

        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_book);
            displayView.setText("Number of rows in book database table: " + cursor.getCount());
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    private void insertBook() {
        // Allows database to become writeable
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        android.content.ContentValues values = new android.content.ContentValues();
        values.put(BookEntry.COLUMN_BOOK_TITLE, "Harry Potter");
        values.put(BookEntry.COLUMN_BOOK_PRICE, "30.00");
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, BookEntry.BOOK_QUANTITY_3000);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, "Acme");
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, "555-1212");

        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);
        android.util.Log.v("Bookstore Activity", "New RowId " + newRowId);
    }
}
