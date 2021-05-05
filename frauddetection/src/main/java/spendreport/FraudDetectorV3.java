package spendreport;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.walkthrough.common.entity.Alert;
import org.apache.flink.walkthrough.common.entity.Transaction;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class FraudDetectorV3 extends KeyedProcessFunction<Long, Transaction, Alert> {
    private static final long serialVersionUID = -2459569187253983649L;

    private transient ValueState<Boolean> flagState;

    private transient ValueState<Long> timeState;

    private static final int LARGE_AMOUNT = 500;
    private static final int SMALL_AMOUNT = 1;

    @Override
    public void open(Configuration parameters) throws Exception {
        ValueStateDescriptor<Boolean> flagDescription = new ValueStateDescriptor<>("flag", Types.BOOLEAN);
        flagState = getRuntimeContext().getState(flagDescription);

        ValueStateDescriptor<Long> timestampDescription = new ValueStateDescriptor<Long>("timestamp", Types.LONG);
        timeState = getRuntimeContext().getState(timestampDescription);
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
            clearUp(context);
        }


        if (transaction.getAmount() < SMALL_AMOUNT) {
            flagState.update(true);

            long timer = context.timerService().currentProcessingTime() + TimeUnit.MINUTES.toMillis(1);
            context.timerService().registerEventTimeTimer(timer);
            timeState.update(timer);
        }
    }


    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<Alert> out) throws Exception {
        timeState.clear();
        flagState.clear();
    }


    private void clearUp(Context context) throws IOException {
        Long timer = timeState.value();
        context.timerService().deleteEventTimeTimer(timer);

        timeState.clear();
        flagState.clear();
    }
}
