package com.xhs.qa.service.impl;

import com.xhs.qa.model.*;
import com.xhs.qa.repository.CmdHistoryRepository;
import com.xhs.qa.service.HistorySaveService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;
import java.util.List;
import java.util.TimeZone;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created on 18/2/6 15:52
 *
 * @author sunyumei
 */
@Service
@Slf4j
public class HistorySaveServiceImpl implements HistorySaveService {
    @Value("${logging.path}")
    private String logPath;

    @Autowired
    private CmdHistoryRepository cmdHistoryRepository;

    @Override
    public void historySave(RunCmdRequest request, CmdOutput cmdOutput) {
        //save request and response to mongodb!!!
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        Date date = new Date();
        log.debug(date.toString());
        CmdHistory cmdHistory = new CmdHistory();
        cmdHistory.setTimestamp(date);
        cmdHistory.setName(request.getName());
        cmdHistory.setVersion(request.getVersion());
        cmdHistory.setRequest(request);
        cmdHistory.setOutput(cmdOutput);
        cmdHistoryRepository.save(cmdHistory);
        //write request and response to history file!!!
        HistorySaveFile historySaveFile = new HistorySaveFile();
        Random randomNum = new Random();
        historySaveFile.setLogid(10000000 + randomNum.nextInt(99999999));
        historySaveFile.setVersion(request.getVersion());
        historySaveFile.setName(request.getName());
        historySaveFile.setRequest(request);
        historySaveFile.setOutput(cmdOutput);
        try {
            Writer output;
            output = new BufferedWriter(new FileWriter(logPath + "/history.log", true));
//            output = new BufferedWriter(new FileWriter("/Users/sunyumei/workspace/thrifteasy/src/main/java/com/xhs/qa/history.log", true));
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(historySaveFile);
            output.append(String.format("%s\n", json));
            output.append("====================================\n");
            output.close();
        } catch (IOException e) {
            throw new RuntimeException("write request and response to history file fail", e);
        }
    }

//    @Override
//    public CmdHistory query(ObjectId id) {
//        return cmdHistoryRepository.findOne(id);
//    }

    @Override
    public List<CmdHistory> queryByTime(String starttime, String endtime){
        Date startTime = new Date(Long.parseLong(starttime));
        Date endTime = new Date(Long.parseLong(endtime));
        return  cmdHistoryRepository.findByTimestampBetween(startTime, endTime);
    }


}

