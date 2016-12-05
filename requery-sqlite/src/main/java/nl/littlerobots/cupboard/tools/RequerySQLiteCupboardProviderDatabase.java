package nl.littlerobots.cupboard.tools;

import android.database.sqlite.SQLiteTransactionListener;

import io.requery.android.database.sqlite.SQLiteDatabase;
import nl.littlerobots.cupboard.tools.provider.SQLiteContentProvider;
import nl.littlerobots.cupboard.tools.provider.TransactionListener;

/**
 * Implementation of {@link nl.littlerobots.cupboard.tools.provider.SQLiteContentProvider.CupboardProviderDatabase} for requery sqlite.
 * Use return an instance of this class in {@link SQLiteContentProvider.CupboardProviderDatabase#openDatabase()}
 */
public class RequerySQLiteCupboardProviderDatabase extends RequerySQLiteCupboardDatabase implements SQLiteContentProvider.CupboardProviderDatabase {
    public RequerySQLiteCupboardProviderDatabase(SQLiteDatabase mDatabase) {
        super(mDatabase);
    }

    @Override
    public void beginTransactionWithListener(final TransactionListener listener) {
        mDatabase.beginTransactionWithListener(new SQLiteTransactionListener() {
            @Override
            public void onBegin() {
                listener.onBegin();
            }

            @Override
            public void onCommit() {
                listener.onCommit();
            }

            @Override
            public void onRollback() {
                listener.onRollback();
            }
        });
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean yieldIfContendedSafely(int sleepAfterYieldDelay) {
        return false;
    }
}
