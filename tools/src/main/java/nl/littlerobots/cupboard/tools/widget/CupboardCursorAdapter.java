package nl.littlerobots.cupboard.tools.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import nl.qbusict.cupboard.CursorCompartment;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


/**
 * CupboardCursorLoader is a Loader for a cupboard model class 'T' backed by a Cursor
 */
public abstract class CupboardCursorAdapter<T> extends CursorAdapter {

    protected Class<T> clz;
    protected CursorCompartment cc;

    public CupboardCursorAdapter(Context context, Cursor cursor, boolean autoRequery, Class<T> clz) {
        super(context, cursor, autoRequery);

        this.clz = clz;
        cc(cursor);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CupboardCursorAdapter(Context context, Cursor cursor, int flags, Class<T> clz) {
        super(context, cursor, flags);

        this.clz = clz;
        cc(cursor);
    }

    public abstract View newView(Context context, T model, ViewGroup parent);
    public abstract void bindView(View view, Context context, T model);

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return newView(context, get(), parent);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        bindView(view, context, get());
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        cc(newCursor);

        return super.swapCursor(newCursor);
    }

    protected T get() {
        return cc.get(clz);
    }

    protected void cc(Cursor cursor) {
        cc = cupboard().withCursor(cursor);
    }
}