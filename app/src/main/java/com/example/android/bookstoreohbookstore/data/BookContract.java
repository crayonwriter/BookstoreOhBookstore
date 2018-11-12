package com.example.android.bookstoreohbookstore.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.bookstoreohbookstore";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "books";
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private BookContract() {
    }

    /**
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represents a single book.
     */
    public static abstract class BookEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);
        public static final String TABLE_NAME = "books";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_BOOK_TITLE = "title";
        public static final String COLUMN_BOOK_PRICE = "price";
        public static final String COLUMN_BOOK_SUPPLIER_NAME = "supplier";
        public static final String COLUMN_BOOK_SUPPLIER_PHONE = "supplier_phone";
        public static final String COLUMN_BOOK_QUANTITY = "quantity";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static boolean isValidQuantity(int quantity) {
            if (quantity >= 0) {
                return true;
            }
            return false;
        }
    }
}
