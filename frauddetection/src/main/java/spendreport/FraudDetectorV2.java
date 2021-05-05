package spendreport;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.walkthrough.common.entity.Alert;
import org.apache.flink.walkthrough.common.entity.Transaction;

public class FraudDetectorV2 extends KeyedProcessFunction<Long, Transaction, Alert> {
    private static final long serialVersionUID = -8041543906529646103L;

    private static final int LARGE_AMOUNT = 500;
    private static final int SMALL_AMOUNT = 1;

    private transient ValueState<Boolean> flagState;

    @Override
    public void open(Configuration parameters) throws Exception {
        ValueStateDescriptor<Boolean> flagDescriptor = new ValueStateDescriptor<>("flap", Types.BOOLEAN);
        flagState = getRuntimeContext().getState(flagDescriptor);
    }

    @Override
    public void processElement(Transaction transaction, Context context, Collector<Alert> collector) throws Exception {
        Boolean lastTransactionWasSmall = flagState.value();

        if (lastTransactionWasSmall != null) {
            if (transaction.getAmount() > LARGE_AMOUNT) {
                Alert alert = new Alert();
                alert.setId(transaction.getAccountId());
                collector.collect(alert);
            }
            flagState.clear();
        }

        if (transaction.getAmount() < SMALL_AMOUNT) {
            flagState.update(true);
        }

    }
}
