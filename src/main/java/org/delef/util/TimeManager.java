package org.delef.util;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import lombok.Getter;
import lombok.Setter;
import org.delef.model.TimeTrackListener;

public class TimeManager implements Runnable {

    @Getter
    @Setter
    private LocalTime time;
    @Getter
    private final List<TimeTrackListener> onTimeChangedListener = new ArrayList<>();

    @Getter
    @Setter
    private volatile Boolean enabled = false;

    public TimeManager(LocalTime time,
                       TimeTrackListener onTimeChangedListener) {
        this.time = time;
        this.onTimeChangedListener.add(onTimeChangedListener);
    }

    public void Engage() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (enabled) {
                    //only reduce remaining time if enabled
                    time = time.minusSeconds(1);
                }
                onTimeChangedListener.forEach(l -> l.track(time));
            }
        }, 0, 1000);
    }

    @Override
    public void run() {
        Engage();
    }
}
