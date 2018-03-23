package com.xhs.qa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CmdOutput {
    String stdout;
    String stderr;
    int exitValue;

    public CmdOutput(Process process) {
        try {
            exitValue = process.exitValue();
            stdout = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
            if (stdout.endsWith("\n")) {
                stdout = stdout.substring(0, stdout.length() - 1);
            }
            if (exitValue != 0) {
                stderr = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
