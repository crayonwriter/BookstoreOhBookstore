package com.example.android.bookstoreohbookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;


import com.example.android.bookstoreohbookstore.data.BookContract;
import com.example.android.bookstoreohbookstore.data.BookContract.BookEntry;

/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source. This adapter knows
 * how to create list items for each row of book data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {
    private long id;

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        //Find individual views that we want to modify in the list item view
        TextView textViewTitle = (TextView) view.findViewById(R.id.title);
        TextView textViewPrice = (TextView) view.findViewById(R.id.price);
        final TextView textViewQuantity = (TextView) view.findViewById(R.id.quantity);

        // Find the columns of book attributes that we're interested in
        int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
        int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);

        //Read the book attributes from the cursor for the current book
        String bookTitle = cursor.getString(titleColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        String bookQuantity = cursor.getString(quantityColumnIndex);
        final int id = cursor.getInt(idColumnIndex);

        //Update the textviews with the attributes for the current book
        textViewTitle.setText(bookTitle);
        textViewPrice.setText(bookPrice);
        textViewQuantity.setText(bookQuantity);

        //Set up listener for Sale button
        Button saleButton = view.findViewById(R.id.sale_button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(textViewQuantity.getText().toString());

                if (quantity > 0) {
                    quantity = quantity - 1;
                    textViewQuantity.setText(Integer.toString(quantity));

                    Uri book = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_BOOK_QUANTITY, textViewQuantity.getText().toString());
                    context.getContentResolver().update(book, values, null, null);
                }
            }
        });
    }
}