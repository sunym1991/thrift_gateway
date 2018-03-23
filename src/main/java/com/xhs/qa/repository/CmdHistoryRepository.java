package com.xhs.qa.repository;

import com.xhs.qa.model.CmdHistory;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.xml.crypto.Data;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Created on 18/2/7 13:24
 *
 * @author sunyumei
 */
public interface CmdHistoryRepository extends CrudRepository<CmdHistory, Date> {
    List<CmdHistory> findByTimestampBetween(Date startTime, Date endTime);
}
