package com.example.cccho.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.cccho.inventory.data.ProductContract.CONTENT_AUTHORITY;
import static com.example.cccho.inventory.data.ProductContract.PATH_PRODUCTS;
import static com.example.cccho.inventory.data.ProductContract.ProductEntry.TABLE_NAME;

/**
 * Created by cccho on 2017/11/13.
 */

public class ProductProvider extends ContentProvider {

    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    public ProductDbHelper mProductDbHelper;

    private static final int PRODUCTS = 100;

    private static final int PRODUCT_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        mProductDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        SQLiteDatabase database = mProductDbHelper.getReadableDatabase();

        Cursor cursor;


        int match = sUriMatcher.match(uri);
        Log.i(LOG_TAG, "before query " + uri + ", match " + match);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Can not query unknown URI: " + uri);
        }

        Log.i(LOG_TAG, "query " + uri);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Insertion in not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return deleteProduct(uri, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return deleteProduct(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Delete not support for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues values,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not support for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        String productName = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        if (productName == null) {
            throw new IllegalArgumentException("Product require a name");
        }

        Integer productPrice = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);

        if (productPrice == null || !ProductContract.ProductEntry.isValidPrice(productPrice)) {
            throw new IllegalArgumentException("Product price is not valid");
        }

        Integer productQuantity = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (productQuantity == null || !ProductContract.ProductEntry.isValidQuantity(productQuantity)) {
            throw new IllegalArgumentException("Product quantity is not valid");
        }

        String productSupplierName = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        if (productSupplierName == null) {
            throw new IllegalArgumentException("Product supplier name is not valid");
        }

        String productSupplierEmailAddress =
                values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL_ADDRESS);
        if (productSupplierEmailAddress == null) {
            throw new IllegalArgumentException("Product supplier email address is not valid");
        }

        String productImage = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_PICTURE);
        if (productImage == null) {
            throw new IllegalArgumentException("Product picture is not valid");
        }

        SQLiteDatabase db = mProductDbHelper.getWritableDatabase();
        long id = db.insert(TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        Log.i(LOG_TAG, "insertProduct " + uri);
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product require a name");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            if (price == null || !ProductContract.ProductEntry.isValidPrice(price)) {
                throw new IllegalArgumentException("Product require a valid price");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null || !ProductContract.ProductEntry.isValidQuantity(quantity)) {
                throw new IllegalArgumentException("Product require a valid quantity");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            if (supplierName == null ) {
                throw new IllegalArgumentException("Product require a valid supplier name");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL_ADDRESS)) {
            String supplierEmailAddress = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL_ADDRESS);
            if (supplierEmailAddress == null ) {
                throw new IllegalArgumentException("Product require a valid supplier email address");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_PICTURE)) {
            String image = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_PICTURE);
            if (image == null ) {
                throw new IllegalArgumentException("Product require a valid picture");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mProductDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(ProductContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
    
    private int deleteProduct(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mProductDbHelper.getWritableDatabase();

        int rowsDelete = database.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);

        if (rowsDelete != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDelete;
    }
}
