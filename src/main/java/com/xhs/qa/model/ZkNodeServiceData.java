package com.xhs.qa.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created on 18/3/8 11:09
 *
 * @author sunyumei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZkNodeServiceData {
    private String tags;
    private int backlog;
    private Stats stats;

    @Data
    public static class Stats{
        private int y;
        private float t;
        private int n;

    }
}
