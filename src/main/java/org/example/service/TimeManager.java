package org.example.service;

import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;
import org.example.model.TimeTrackListener;

public class TimeManager implements Runnable {

    @Getter
    @Setter
    private LocalTime time;
    private final TimeTrackListener onTimeChangedListener;

    @Getter
    @Setter
    private volatile Boolean enabled = false;

    public TimeManager(LocalTime time,
                       TimeTrackListener onTimeChangedListener) {
        this.time = time;
        this.onTimeChangedListener = onTimeChangedListener;
    }

    public void Engage() {
        long timestamp = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() > timestamp + 1000) {
                if (enabled) {
                    //only reduce remaining time if enabled
                    time = time.minusSeconds(1);
                }
                onTimeChangedListener.track(time);
                timestamp = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void run() {
        Engage();
    }
}
