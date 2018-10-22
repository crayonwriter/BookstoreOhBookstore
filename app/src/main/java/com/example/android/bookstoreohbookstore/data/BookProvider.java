package com.example.android.bookstoreohbookstore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.example.android.bookstoreohbookstore.data.BookContract.BookEntry;
import static com.example.android.bookstoreohbookstore.data.BookContract.BookEntry.COLUMN_BOOK_PRICE;
import static com.example.android.bookstoreohbookstore.data.BookContract.BookEntry.COLUMN_BOOK_QUANTITY;
import static com.example.android.bookstoreohbookstore.data.BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME;
import static com.example.android.bookstoreohbookstore.data.BookContract.BookEntry.COLUMN_BOOK_TITLE;

/**
 * {@link ContentProvider} for bookstore app.
 */
public class BookProvider extends ContentProvider {

    /** URI matcher code for the content URI for the books table */
    private static final int BOOKS = 100;

    /** URI matcher code for the content URI for a single book in the books table */
    private static final int BOOK_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

    sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
    sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "#", BOOK_ID);
    }

    //Global database helper object
    private BookDbHelper mDbHelper;

    /** Tag for the log messages */
    public static final String LOG_TAG = com.example.android.bookstoreohbookstore.data.BookProvider.class.getSimpleName();

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
    mDbHelper = new BookDbHelper(getContext());
        // BookDbHelper object to gain access to the books database.
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the book table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
                default: throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a book title into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {
        // Check that the title is not null
        String name = values.getAsString(COLUMN_BOOK_TITLE);
        if (name == null) {
            throw new IllegalArgumentException("Book requires a title");
        }

        Integer price = values.getAsInteger(BookEntry.COLUMN_BOOK_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Book requires a valid price");
        }

        String supplierName = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Supplier requires a name");
        }

        Integer quantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
        if (quantity == null || !BookEntry.isValidQuantity(quantity)) {
            throw new IllegalArgumentException("Book requires valid quantity");
        }

        int lengthPhone = String.valueOf(price).length();

        Integer supplierPhone = values.getAsInteger(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);
        if (supplierPhone != null && lengthPhone != 10) {
            throw new IllegalArgumentException("Phone number must have 10 digits");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new book with the given values
        long id = database.insert(BookEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                // For the Book_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update books in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more books).
     * Return the number of rows that were successfully updated.
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(COLUMN_BOOK_TITLE)) {
            String name = values.getAsString(COLUMN_BOOK_TITLE);
            if (name == null) {
                throw new IllegalArgumentException("Book requires a title");
            }

            if (values.containsKey(COLUMN_BOOK_PRICE)) {
                Integer price = values.getAsInteger(BookEntry.COLUMN_BOOK_PRICE);
                if (price != null && price < 0) {
                    throw new IllegalArgumentException("Book requires a valid price");
                }

                if (values.containsKey(COLUMN_BOOK_SUPPLIER_NAME)) {
                    String supplierName = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
                    if (supplierName == null) {
                        throw new IllegalArgumentException("Supplier requires a name");
                    }
                }
                if (values.containsKey(COLUMN_BOOK_QUANTITY)) {
                    Integer quantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
                    if (quantity == null || !BookEntry.isValidQuantity(quantity)) {
                        throw new IllegalArgumentException("Book requires valid quantity");
                    }
                }
                int lengthPhone = String.valueOf(price).length();

                Integer supplierPhone = values.getAsInteger(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);
                if (values.containsKey(COLUMN_BOOK_SUPPLIER_NAME)) {
                    if (supplierPhone != null && lengthPhone != 10) {
                        throw new IllegalArgumentException("Phone number must have 10 digits");
                    }
                }
            }

            if (values.size() == 0) {
                return 0;
            }
        }
        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        return database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);
    }


    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                return database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}