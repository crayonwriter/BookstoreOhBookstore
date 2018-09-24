package com.example.android.bookstoreohbookstore;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.bookstoreohbookstore.data.BookContract;
import com.example.android.bookstoreohbookstore.data.BookContract.BookEntry;
import com.example.android.bookstoreohbookstore.data.BookDbHelper;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class BookstoreEditor extends AppCompatActivity {

    /**
     * EditText field to enter the pet's name
     */
    private EditText mTitleEditText;

    /**
     * EditText field to enter the pet's breed
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the pet's weight
     */
    private EditText mSupplierNameEditText;

    /**
     * EditText field to enter the pet's gender
     */
    private Spinner mQuantitySpinner;

    /**
     * Quantity of books. The possible values are:
     * 1 for 1000, 2 for 2000, 3 for 3000.
     */
    private int mQuantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);

        // Find all relevant views that we will need to read user input from
        mTitleEditText = (EditText) findViewById(R.id.edit_book_title);
        mPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mQuantitySpinner = (Spinner) findViewById(R.id.spinner_quantity);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the quantity of books.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter quantitySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_quantity_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        quantitySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mQuantitySpinner.setAdapter(quantitySpinnerAdapter);

        // Set the integer mSelected to the constant values
        mQuantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.quantity_3000))) {
                        mQuantity = 3; // 3000
                    } else if (selection.equals(getString(R.string.quantity_2000))) {
                        mQuantity = 2; // 2000
                    } else {
                        mQuantity = 1; // 1000
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mQuantity = 1; // 1000
            }
        });
    }

    private void insertPet() {
        BookDbHelper mDbHelper = new BookDbHelper(this);

        // Allows database to become writeable
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String titleString = mTitleEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        int price = Integer.parseInt(priceString);

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_TITLE, titleString);
        values.put(BookEntry.COLUMN_BOOK_PRICE, priceString);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierNameString);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, mQuantity);

        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving book", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Book saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_bookstore options from the res/menu_bookstore/menu_editor.xml file.
        // This adds menu_bookstore items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu_bookstore option in the app bar overflow menu_bookstore
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu_bookstore option
            case R.id.action_save:
                insertPet();
                finish();
                return true;
            // Respond to a click on the "Delete" menu_bookstore option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
