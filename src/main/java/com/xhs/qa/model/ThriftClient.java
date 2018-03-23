package com.xhs.qa.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThriftClient {
    private String name;
    private String version;
    private String branch;

    public boolean valid() {
        return name != null && version != null && branch != null;
    }
}
