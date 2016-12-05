package nl.littlerobots.cupboard.tools.provider;

public interface TransactionListener {
    void onBegin();
    void onCommit();
    void onRollback();
}
