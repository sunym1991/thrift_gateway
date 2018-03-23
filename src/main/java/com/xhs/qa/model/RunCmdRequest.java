package com.xhs.qa.model;

import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class RunCmdRequest {
    private String name;
    private String version;
    private String service;
    private String tags;
    private String method;
    private List<String> args;
}
