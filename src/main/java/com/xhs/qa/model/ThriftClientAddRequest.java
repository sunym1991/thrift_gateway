package com.xhs.qa.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by sunyumei on 18/1/14.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ThriftClientAddRequest extends ThriftClient {
    private String base64Pack;
    private String mainFile;

    public boolean valid() {
        return super.valid() && base64Pack != null;
    }
}
