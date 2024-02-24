package org.example.service;

import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;
import org.example.model.TimeOutListener;
import org.example.model.TimeTrackListener;

public class TimeManager implements Runnable {

    @Getter
    @Setter
    private LocalTime time;

    private final TimeOutListener onFinishListener;
    private final TimeTrackListener onTimeChangedListener;

    @Getter
    @Setter
    private volatile Boolean enabled = true;

    public TimeManager(LocalTime time,
                       TimeOutListener onFinishListener,
                       TimeTrackListener onTimeChangedListener) {
        this.time = time;
        this.onFinishListener = onFinishListener;
        this.onTimeChangedListener = onTimeChangedListener;
    }

    public void Engage() {
        long timestamp = System.currentTimeMillis();
        while (time.isAfter(LocalTime.of(0, 0, 0))) {
            if (System.currentTimeMillis() > timestamp + 1000 && enabled) {
                time = time.minusSeconds(1);
                onTimeChangedListener.track(time);
                timestamp = System.currentTimeMillis();
            }
        }
        onFinishListener.timeOut();
    }

    @Override
    public void run() {
        Engage();
    }
}
