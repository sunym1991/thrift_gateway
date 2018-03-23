package com.xhs.qa.service.impl;

import com.xhs.qa.service.RuleDataService;
import org.springframework.stereotype.Service;
import java.util.Date;

import java.util.Map;

/**
 * Created on 18/1/31 09:50
 *
 * @author sunyumei
 */
@Service
public class RuleDataServiceImpl implements RuleDataService{

    @Override
    public  String context(){
        //struct Context in rpc_common
        return String.format("",
                Integer.toString((int) new Date().getTime()));
    }

    @Override
    public  String image(){
        // strcut Image in image_common
        String name = "test_picture";
        String path = "/test/test";
        String extension = "extension";
        String width = "320";
        String height = "320";
        String link = "";
        return  String.format("",
                name, path, extension, width, height, link);
    }

    @Override
    public String price(){
        //struct Price in price_common
        return "";
    }

    @Override
    public String priceInt(){
        //struct PriceInt in price_common
        return "";
    }

    @Override
    public String video(){
        //struct Video in video_common
        return ";
    }

}
