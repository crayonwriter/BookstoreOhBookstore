package com.example.android.bookstoreohbookstore;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.bookstoreohbookstore.data.BookContract.BookEntry;
import com.example.android.bookstoreohbookstore.data.BookDbHelper;

public class BookstoreActivity extends AppCompatActivity {

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

        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(
                    BookEntry.CONTENT_URI, projection, null, null, null);

            TextView displayView = (TextView) findViewById(R.id.text_view_book);


            //This is the header for the limited UI
            displayView.setText("The books table contains " + cursor.getCount() + " book(s).\n\n");
            displayView.append(
                    BookEntry._ID + " - "
                            + BookEntry.COLUMN_BOOK_TITLE + " - "
                            + BookEntry.COLUMN_BOOK_PRICE + " - "
                            + BookEntry.COLUMN_BOOK_SUPPLIER_NAME + " - "
                            + BookEntry.COLUMN_BOOK_SUPPLIER_PHONE + " - "
                            + BookEntry.COLUMN_BOOK_QUANTITY + "\n"
            );

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        // Figure out the index of each column
                        int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
                        int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
                        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
                        int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
                        int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);
                        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);

                        // Iterate through all the returned rows in the cursor
                        {
                            // Use that index to extract the String or Int value of the word
                            // at the current row the cursor is on.
                            int currentID = cursor.getInt(idColumnIndex);
                            String currentTitle = cursor.getString(titleColumnIndex);
                            int currentPrice = cursor.getInt(priceColumnIndex);
                            String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                            String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);
                            int currentQuantity = cursor.getInt(quantityColumnIndex);

                            // Display the values from each column of the current row in the cursor in the TextView
                            displayView.append(("\n"
                                    + currentID + " - "
                                    + currentTitle + " - "
                                    + currentPrice + " - "
                                    + currentSupplierName + " - "
                                    + currentSupplierPhone + " - "
                                    + currentQuantity));
                        }
                    } while (cursor.moveToNext());
                }
            }

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            assert cursor != null;
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_bookstore.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_bookstore, menu);
        return true;
    }

    private void insertBook() {

        android.content.ContentValues values = new android.content.ContentValues();
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

