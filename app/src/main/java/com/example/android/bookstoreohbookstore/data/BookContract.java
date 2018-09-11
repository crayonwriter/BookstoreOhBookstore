package com.example.android.bookstoreohbookstore.data;

import android.provider.BaseColumns;

public final class BookContract {
public static abstract class BookEntry implements BaseColumns {
    public static final String TABLE_NAME = "books";

    public static final String _Id = BaseColumns._ID;
    public static final String COLUMN_BOOK_TITLE = "title";
    public static final String COLUMN_BOOK_PRICE = "price";
    public static final String COLUMN_BOOK_SUPPLIER_NAME = "supplier";
    public static final String COLUMN_BOOK_SUPPLIER_PHONE = "supplier phone #";

    public static final int BOOK_QUANTITY_1000 = 1;
    public static final int BOOK_QUANTITY_2000 = 2;
    public static final int BOOK_QUANTITY_3000 = 3;
    }
}
