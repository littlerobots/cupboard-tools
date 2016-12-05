package nl.littlerobots.cupboard.tools.sqlcipher;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteTransactionListener;

import nl.littlerobots.cupboard.tools.provider.SQLiteContentProvider;
import nl.littlerobots.cupboard.tools.provider.TransactionListener;

/**
 * Implementation of {@link nl.littlerobots.cupboard.tools.provider.SQLiteContentProvider.CupboardProviderDatabase} for SQLCipher.
 * Use return an instance of this class in {@link SQLiteContentProvider.CupboardProviderDatabase#openDatabase()}
 */
public class SQLCipherCupboardProviderDatabase extends SQLCipherCupboardDatabase implements SQLiteContentProvider.CupboardProviderDatabase {

    public SQLCipherCupboardProviderDatabase(SQLiteDatabase mDatabase) {
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
        return !mDatabase.isOpen();
    }

    @Override
    public boolean yieldIfContendedSafely(int sleepAfterYieldDelay) {
        return mDatabase.yieldIfContendedSafely(sleepAfterYieldDelay);
    }
}
