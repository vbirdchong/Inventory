package com.example.cccho.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.cccho.inventory.data.ProductContract;

/**
 * Created by cccho on 2017/11/14.
 */

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvName = (TextView) view.findViewById(R.id.id_product);
        TextView tvPrice = (TextView) view.findViewById(R.id.id_price);
        TextView tvQuantity = (TextView) view.findViewById(R.id.id_quantity);

        // 图片不知道怎么添加 
        // TODO: 2017/11/14
//        ImageView picture = (ImageView) view.findViewById(R.id.id_image);

        String name = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME));
        int price = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY));

        tvName.setText(name);
        tvPrice.setText(Integer.toString(price));
        tvQuantity.setText(Integer.toString(quantity));
    }
}
