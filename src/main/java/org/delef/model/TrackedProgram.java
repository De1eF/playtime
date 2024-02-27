package org.delef.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrackedProgram {
    private String name;
    private Boolean trackByTitle;

    public enum TrackType {
        ByProgramName,
        ByTitle
    }
}
