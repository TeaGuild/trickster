package dev.enjarai.trickster.util;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class Meowing {
    public static void addBubblingCallback(TransactionContext transaction, TransactionContext.OuterCloseCallback callback) {
        transaction.addCloseCallback((trans, result) -> {
            if (result.wasCommitted()) {
                int depth = trans.nestingDepth();

                if (depth == 0) {
                    trans.addOuterCloseCallback(callback);
                } else {
                    addBubblingCallback(trans.getOpenTransaction(depth - 1), callback);
                }
            }
        });
    }
}
