package com.example.android.bookstoreohbookstore;

import android.content.Intent;
import android.content.ContentValues;
import android.net.Uri;
import android.widget.CursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

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

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookstoreActivity.this, BookstoreEditor.class);
                startActivity(intent);
            }
        });
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.

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

        String[] projection = {
                android.provider.BaseColumns._ID,
                BookEntry.COLUMN_BOOK_TITLE,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE,
                BookEntry.COLUMN_BOOK_QUANTITY,

        };

        Cursor cursor = getContentResolver().query(
                BookEntry.CONTENT_URI, projection,
                null,
                null,
                null
        );

        // Find ListView to populate
        ListView listviewBook = (ListView) findViewById(R.id.listview_book);
        // Setup cursor adapter using cursor from last step
        BookCursorAdapter adapter = new BookCursorAdapter(this, cursor);
        // Attach cursor adapter to the ListView
        listviewBook.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_bookstore, menu);
        return true;
    }
    private void insertBook() {

        ContentValues values = new android.content.ContentValues();
        values.put(BookEntry.COLUMN_BOOK_TITLE, "Harry Potter");
        values.put(BookEntry.COLUMN_BOOK_PRICE, "30.00");
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, BookEntry.BOOK_QUANTITY_3000);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, "Acme");
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, "555-1212");

        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
        Log.v("Catalog Activity", "New Row" + newUri);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertBook();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

