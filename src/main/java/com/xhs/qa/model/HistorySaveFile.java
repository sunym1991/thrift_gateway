package com.xhs.qa.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Created on 18/2/8 14:28
 *
 * @author sunyumei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorySaveFile {

    private Integer logid;
    private String timeStamp = LocalDateTime.now().toString();
    private String name;
    private String version;
    private RunCmdRequest request;
    private CmdOutput output;
}
