package com.xhs.qa.controller;

import com.xhs.qa.model.*;
import com.xhs.qa.service.HistorySaveService;
import com.xhs.qa.service.ThriftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by sunyumei on 2018/1/3.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/v1/thrift")
public class ThriftController {

    private ThriftService thriftService;
    private HistorySaveService historySaveService;

    @Autowired
    public ThriftController(ThriftService thriftService, HistorySaveService historySaveService) {
        this.thriftService = thriftService;
        this.historySaveService = historySaveService;
    }

    @RequestMapping(value = "/client", method = {RequestMethod.GET})
    public List<String> listServices() {
        return thriftService.listClientNames();
    }

    @RequestMapping(value = "/client/{name}", method = RequestMethod.GET)
    public List<ThriftClient> listClientVersions(@PathVariable String name) {
        return thriftService.listClientByName(name);
    }

    @RequestMapping(value = "/client", method = RequestMethod.POST)
    public void addClient(@RequestBody ThriftClientAddRequest request) {
        thriftService.addOneClient(request);
    }

    @RequestMapping(value = "/client/add", method = RequestMethod.POST)
    public void addClientByGit(@RequestBody ThriftClient request) {
        thriftService.addOneClientByGit(request);
    }

    @RequestMapping(value = "/client/{name}", method = RequestMethod.DELETE)
    public  Boolean deleteServiceByNameVersion(@PathVariable String name, @RequestBody ThriftClient client){
        return thriftService.deleteServiceByNameVersion(name, client.getVersion());
    }

    @RequestMapping(value = "/method", method = RequestMethod.POST)
    public CmdOutput runMethod(@RequestBody RunCmdRequest request) {
        return thriftService.runCommand(request);
    }

    @RequestMapping(value= "/history", method = RequestMethod.GET)
    public List<CmdHistory> queryHistoryByTime(@RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime){
        return historySaveService.queryByTime(startTime, endTime);
    }

}
