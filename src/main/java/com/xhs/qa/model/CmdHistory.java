package com.xhs.qa.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created on 18/2/7 13:05
 *
 * @author sunyumei
 */
@Data
@Document
@CompoundIndexes(value = @CompoundIndex(name = "name-version-idx", def = "{'name': 1, 'version': 1}"))
public class CmdHistory {
    @Id
    private ObjectId id;
    @Indexed(direction = IndexDirection.DESCENDING)
    private Date timestamp ;
    private String name;
    private String version;
    private RunCmdRequest request;
    private CmdOutput output;

}
