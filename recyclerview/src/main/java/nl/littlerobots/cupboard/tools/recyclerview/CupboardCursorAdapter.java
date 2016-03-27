package nl.littlerobots.cupboard.tools.recyclerview;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import nl.qbusict.cupboard.Cupboard;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * An adapter for a cursor that returns a particular object using Cupboard. This adapter will use stable id's {@link android.support.v7.widget.RecyclerView.Adapter#setHasStableIds(boolean)}
 * based on the {@link BaseColumns#_ID} field of the cursor.
 *
 * @param <T>  the entity type
 * @param <VH> the ViewHolder type
 */
public abstract class CupboardCursorAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private static final Cursor EMTPY_CURSOR = new MatrixCursor(new String[]{BaseColumns._ID});
    private final Class<T> mEntityClass;
    private final Cupboard mCupboard;
    private Cursor mCursor;
    private int mIdColumnIndex;

    /**
     * Create the adapter for the given entity using the default instance of Cupboard
     *
     * @param entityClass the entity class
     */
    public CupboardCursorAdapter(Class<T> entityClass) {
        this(entityClass, null);
    }

    /**
     * Create the adapter for the given entity using the default instance of Cupboard
     *
     * @param entityClass the entity class
     * @param cursor      the cursor, may be null
     */
    public CupboardCursorAdapter(Class<T> entityClass, @Nullable Cursor cursor) {
        this(entityClass, cupboard(), cursor);
    }

    /**
     * Create the adapter for the given entity using the supplied instance of Cupboard
     *
     * @param entityClass the entity class
     * @param cupboard    the instance of Cupboard to use
     * @param cursor      the cursor, may be null
     */
    public CupboardCursorAdapter(Class<T> entityClass, Cupboard cupboard, @Nullable Cursor cursor) {
        setHasStableIds(true);
        mEntityClass = entityClass;
        mCupboard = cupboard;
        swapCursor(cursor);
    }

    @Override
    public void onBindViewHolder(VH holder, int position, List<Object> payloads) {
        if (mCursor.moveToPosition(position)) {
            onBindViewHolder(holder, mCupboard.withCursor(mCursor).get(mEntityClass), payloads);
        } else {
            throw new IllegalStateException("Invalid position: " + position);
        }
    }

    /**
     * Bind the view holder for the given entity
     *
     * @param holder the view holder
     * @param object the entity
     */
    public abstract void onBindViewHolder(VH holder, T object);

    /**
     * Bind the view holder for the given entity. By default this method calls {@link #onBindViewHolder(RecyclerView.ViewHolder, Object)}
     *
     * @param holder   the view holder
     * @param object   the entity
     * @param payloads any payloads that may have been passed with {@link #notifyItemChanged(int, Object)} or {@link #notifyItemRangeChanged(int, int, Object)}
     */
    public void onBindViewHolder(VH holder, T object, List<Object> payloads) {
        onBindViewHolder(holder, object);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    @Override
    public final long getItemId(int position) {
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalArgumentException("Invalid position: " + position);
        }
        return mCursor.getLong(mIdColumnIndex);
    }

    /**
     * Replace the current cursor with a new one. The cursor must have a {@link BaseColumns#_ID} column
     *
     * @param cursor the cursor, may be null
     */
    public void swapCursor(@Nullable Cursor cursor) {
        mCursor = cursor != null ? cursor : EMTPY_CURSOR;
        mIdColumnIndex = mCursor.getColumnIndexOrThrow(BaseColumns._ID);
        notifyDataSetChanged();
    }
}
