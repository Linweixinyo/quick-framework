package org.weixin.framework.common.toolkit.transaction;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Objects;

public class TransactionUtil {


    public static void doAfterTransaction(DoTransactionCompletion doTransactionCompletion) {
        if(TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(doTransactionCompletion);
        }
    }


    public static class DoTransactionCompletion implements TransactionSynchronization {

        private final Runnable runnable;

        public DoTransactionCompletion(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void afterCompletion(int status) {
            if(Objects.equals(status, TransactionSynchronization.STATUS_COMMITTED)) {
                this.runnable.run();
            }
        }
    }

}
