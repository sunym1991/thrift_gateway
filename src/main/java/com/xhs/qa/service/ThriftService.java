package com.xhs.qa.service;

import com.xhs.qa.model.CmdOutput;
import com.xhs.qa.model.RunCmdRequest;
import com.xhs.qa.model.ThriftClient;
import com.xhs.qa.model.ThriftClientAddRequest;

import java.util.List;

/**
 * Created by sunyumei on 18/1/14.
 */
public interface ThriftService {
    void addOneClient(ThriftClientAddRequest thriftClientAddRequest);

    void addOneClientByGit(ThriftClient thriftClient);

    List<ThriftClient> listClientByName(String name);

    List<String> listClientNames();

    CmdOutput runCommand(RunCmdRequest request);

    Boolean deleteServiceByNameVersion(String name, String version);
}
