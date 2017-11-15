package com.example.cccho.inventory;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cccho.inventory.data.ProductContract;

/**
 * Created by cccho on 2017/11/14.
 */

public class ProductCursorAdapter extends CursorAdapter {

    final static String LOG_TAG = "ProductCursorAdapter";

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView tvName = (TextView) view.findViewById(R.id.id_product);
        TextView tvPrice = (TextView) view.findViewById(R.id.id_price);
        TextView tvQuantity = (TextView) view.findViewById(R.id.id_quantity);
        ImageView ivPicture = (ImageView) view.findViewById(R.id.id_image);
        ImageView ivSale = (ImageView) view.findViewById(R.id.id_sale);

        String name = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME));
        int price = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE));
        final int quantity = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY));
        String picture = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PICTURE));
        Uri pictureUri = Uri.parse(picture);
        int currentProductId = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry._ID));

        final Uri currentProductUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, currentProductId);

        // Decrease the quantity when click the sale button
        ivSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver resolver = context.getContentResolver();
                ContentValues values = new ContentValues();
                if (quantity > 0) {
                    int sale = quantity - 1;
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, sale);
                    resolver.update(currentProductUri,
                            values,
                            null,
                            null);
                    context.getContentResolver().notifyChange(currentProductUri, null);
                } else {
                    Toast.makeText(context, "Item sold out.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvName.setText(name);
        tvPrice.setText(Integer.toString(price));
        tvQuantity.setText(Integer.toString(quantity));
        ivPicture.setImageURI(pictureUri);
    }
}
