package com.example.android.bookstoreohbookstore;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import com.example.android.bookstoreohbookstore.data.BookContract.BookEntry;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class BookstoreEditor extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_PET_LOADER = 0;
    /**
     * EditText field to enter the book's title
     */
    private EditText mTitleEditText;

    /**
     * EditText field to enter the book's price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the book's supplier
     */
    private EditText mSupplierNameEditText;

    /**
     * EditText field to enter the book supplier's phone number
     */
    private EditText mSupplierPhoneEditText;

    /**
     * EditText field to enter the quantity of books
     */
    private Spinner mQuantitySpinner;

    /**
     * Quantity of books. The possible values are:
     * 1 for 1000, 2 for 2000, 3 for 3000.
     */
    private int mQuantity = 1;

    /**
     * Content URI for the existing pet (null if it's a new pet)
     */
    private Uri mCurrentBookUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);

        //Use getIntent() and getData() to get the associated URI
        //Set title of BookstoreEditor depending on which situation we have
        //If opened using ListView item, we will have uri of book so change app bar to Edit Book
        //Otherwise, if a new book, uri is null so change app bar to Add A Book

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null) {
            setTitle(R.string.bookstore_editor_add_book);
        } else {
            setTitle(R.string.bookstore_editor_edit_book);
            getSupportLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mTitleEditText = (EditText) findViewById(R.id.edit_book_title);
        mPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.edit_supplier_phone);
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
                        mQuantity = BookEntry.BOOK_QUANTITY_3000; // 3000
                    } else if (selection.equals(getString(R.string.quantity_2000))) {
                        mQuantity = BookEntry.BOOK_QUANTITY_2000; // 2000
                    } else {
                        mQuantity = BookEntry.BOOK_QUANTITY_1000; // 1000
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mQuantity = 0; // 1000
            }
        });
    }

    /**
     * Get user input from editor and save book into database.
     */
    private void saveBook() {
        String titleString = mTitleEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();
        int price = Integer.parseInt(priceString);
//        // Check if this is supposed to be a new book
//        // and check if all the fields in the editor are blank
//        if (mCurrentBookUri == null &&
//                TextUtils.isEmpty(titleString) &&
//                TextUtils.isEmpty(priceString) &&
//                TextUtils.isEmpty(supplierNameString) &&
//                TextUtils.isEmpty(supplierPhoneString)) {
//            // Since no fields were modified, we can return early without creating a new pet.
//            // No need to create ContentValues and no need to do any ContentProvider operations.
//            return;
//        }
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_TITLE, titleString);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierNameString);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, supplierPhoneString);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, mQuantity);
//        int price = 0;
//        if (!TextUtils.isEmpty(priceString)) {
//            price = Integer.parseInt(priceString);
//        }
        values.put(BookEntry.COLUMN_BOOK_PRICE, price);

        // Determine if this is a new or existing book by checking if mCurrentBookUri is null or not
        if (mCurrentBookUri == null) {
            // This is a NEW book, so insert a new book into the provider,
            // returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING book, so update the book with content URI: mCurrentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentBookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
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
                saveBook();
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_TITLE,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE,
                BookEntry.COLUMN_BOOK_QUANTITY
        };
        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);


            // Update the views on the screen with the values from the database
            mTitleEditText.setText(title);
            mPriceEditText.setText(price);
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(supplierPhone);


            // Quantity is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (1 is 1000, 2 is 2000, 3 is 3000).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (quantity) {
                case BookEntry.BOOK_QUANTITY_3000:
                    mQuantitySpinner.setSelection(3);
                    break;
                case BookEntry.BOOK_QUANTITY_2000:
                    mQuantitySpinner.setSelection(2);
                    break;
                default:
                    mQuantitySpinner.setSelection(1);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
// If the loader is invalidated, clear out all the data from the input fields.
        mTitleEditText.setText("");
        mPriceEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");

        mQuantitySpinner.setSelection(1);
    }
}
