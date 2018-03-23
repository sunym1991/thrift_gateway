package com.xhs.qa.controller;

import com.xhs.qa.util.ApplicationConfig;
import com.xhs.qa.util.http.ResultModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.List;

/**
 * Created by zren on 2018/1/4.
 */
@RestController
@RequestMapping("/api/file")
public class FileController {
  /**
   * 单文件上传
   *
   * @param file
   * @param request
   * @return
   */
  @PostMapping(value = "/upload",produces = "application/json;charset=utf-8")
  @ResponseBody
  public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
    if (!file.isEmpty()) {
      String saveFileName = file.getOriginalFilename();
      File saveFile = new File(ApplicationConfig.getInstance().getProperty("thrifteasy.basedir","/data")+"/upload/"+ saveFileName);
      if (!saveFile.getParentFile().exists()) {
        saveFile.getParentFile().mkdirs();
      }
      try {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(saveFile));
        out.write(file.getBytes());
        out.flush();
        out.close();
        return ResultModel.succ(saveFile.getName() + " 上传成功").toString();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        return ResultModel.fail("上传失败", e.getMessage()).toString();
      } catch (IOException e) {
        e.printStackTrace();
        return ResultModel.fail("上传失败", e.getMessage()).toString();
      }
    } else {
      return ResultModel.fail("上传失败","文件为空").toString();
    }
  }

  /**
   * 多文件上传
   *
   * @param request
   * @return
   */
  @PostMapping(value = "/uploadFiles",produces = "application/json;charset=utf-8")
  @ResponseBody
  public String uploadFiles(HttpServletRequest request) throws IOException {
    File savePath = new File(request.getSession().getServletContext().getRealPath("/upload/"));
    if (!savePath.exists()) {
      savePath.mkdirs();
    }
    List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
    MultipartFile file = null;
    BufferedOutputStream stream = null;
    for (int i = 0; i < files.size(); ++i) {
      file = files.get(i);
      if (!file.isEmpty()) {
        try {
          byte[] bytes = file.getBytes();
          File saveFile = new File(savePath, file.getOriginalFilename());
          stream = new BufferedOutputStream(new FileOutputStream(saveFile));
          stream.write(bytes);
          stream.close();
        } catch (Exception e) {
          if (stream != null) {
            stream.close();
            stream = null;
          }
          return ResultModel.fail("第 " + i + " 个文件上传有错误" , e.getMessage()).toString();
        }
      } else {
        return ResultModel.fail("第 " + i + " 个文件上传有错误","第 " + i + " 个文件为空").toString();
      }
    }
    return ResultModel.succ("所有文件上传成功").toString();
  }
}