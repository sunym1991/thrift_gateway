package com.xhs.qa.service;

import com.xhs.qa.model.CmdHistory;
import com.xhs.qa.model.CmdOutput;
import com.xhs.qa.model.RunCmdRequest;
import com.xhs.qa.model.QueryHistoryByTime;
import org.bson.types.ObjectId;
import java.util.List;

/**
 * Created on 18/2/6 15:55
 *
 * @author sunyumei
 */
public interface  HistorySaveService {
//    void historySave(String request, String response);

    void historySave(RunCmdRequest request, CmdOutput cmdOutput);

//    CmdHistory query(ObjectId id);

    List<CmdHistory> queryByTime(String startTime, String endTime);
}
