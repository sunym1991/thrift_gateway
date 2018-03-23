package com.xhs.qa.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xhs.qa.model.CmdOutput;
import com.xhs.qa.model.RunCmdRequest;
import com.xhs.qa.model.ThriftClient;
import com.xhs.qa.model.ThriftClientAddRequest;
import com.xhs.qa.service.HistorySaveService;
import com.xhs.qa.service.RuleDataService;
import com.xhs.qa.service.ServerAddressService;
import com.xhs.qa.service.ThriftService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
@Slf4j
public class ThriftServiceImpl implements ThriftService {

    private final ServerAddressService serverAddressService;
    private final RuleDataService ruleDataService;
    private final HistorySaveService historySaveService;
    @Value("${thrifteasy.generated-python-code-dir}")
    private String generatedPythonDir;
    @Value("${thrifteasy.thrift-file-dir}")
    private String thriftFileDir;
    @Value("${thrifteasy.tar-dir}")
    private String tarDir;
    @Value("${thrifteasy.build-data-dir")
    private String buildDataDir;
    @Value("${thrifteasy.service-method-run}")
    private String runServiceMethod;
    @Value("${thrifteasy.python-code-gen}")
    private String genPythonCode;
    @Value("${thrifteasy.python-code-gen-by-git}")
    private String genPythonCodeByGit;
    private Map<String, Map<String, ThriftClient>> thriftClients = new ConcurrentHashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ThriftServiceImpl(ServerAddressService serverAddressService, RuleDataService ruleDataService, HistorySaveService historySaveService) {
        this.serverAddressService = serverAddressService;
        this.ruleDataService = ruleDataService;
        this.historySaveService = historySaveService;
    }


    @PostConstruct
    private void init() throws IOException {
        // load existed services from disk
        File dir = new File(generatedPythonDir);
        File[] files = dir.listFiles((dir1, name) -> name.endsWith(".json"));
        assert files != null;
        for (File file : files) {
            ThriftClient thriftClient = loadFromFile(file);
            thriftClients.computeIfAbsent(thriftClient.getName(), k -> new ConcurrentHashMap<>())
                    .put(thriftClient.getVersion(), thriftClient);
        }
    }

    private ThriftClient loadFromFile(File file) throws IOException {
        return objectMapper.readValue(file, ThriftClient.class);
    }

    private void saveToFile(ThriftClient thriftClient, File file) {
        try {
            objectMapper.writeValue(file, thriftClient);
        } catch (IOException e) {
            throw new RuntimeException("Write thrift client to file failed", e);
        }
    }

    @Override
    public void addOneClient(ThriftClientAddRequest request) {
        if (!request.valid()) {
            throw new RuntimeException("Invalid add thrift client request");
        }
        Map<String, ThriftClient> clientsWithSameService = thriftClients.computeIfAbsent(
                request.getName(), k -> new ConcurrentHashMap<>());
        if (clientsWithSameService.containsKey(request.getVersion())) {
            throw new RuntimeException(String.format("Service=%s and version=%s already existed",
                    request.getName(), request.getVersion()));
        }

        String tarLocation = String.format("%s/%s-%s.tar", tarDir, request.getName(), request.getVersion());
        try {
            FileOutputStream fops = new FileOutputStream(tarLocation);
            fops.write(Base64.getDecoder().decode(request.getBase64Pack()));
            fops.close();
        } catch (IOException e) {
            throw new RuntimeException("Write thrifts files to disk failed");
        }

        String cmd = genPythonCode.replace("{client}", request.getName())
                .replace("{version}", request.getVersion());
        log.debug("run command={}", cmd);
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
            log.debug("process output={}", new CmdOutput(process));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to generated python source", e);
        }

        ThriftClient client = new ThriftClient(request.getName(), request.getVersion(), request.getBranch());
        saveToFile(client, new File(String.format("%s/%s-%s.json", generatedPythonDir, client.getName(), client.getVersion())));
        clientsWithSameService.put(request.getVersion(),
                client);
    }

    @Override
    public void addOneClientByGit(ThriftClient request) {

        if (!request.valid()) {
            throw new RuntimeException("Invalid add thrift client request");
        }
        Map<String, ThriftClient> clientsWithSameService = thriftClients.computeIfAbsent(
                request.getName(), k -> new ConcurrentHashMap<>());
        if (clientsWithSameService.containsKey(request.getVersion())) {
            throw new RuntimeException(String.format("Service=%s and version=%s already existed",
                    request.getName(), request.getVersion()));
        }
        String cmd = genPythonCodeByGit.replace("{client}", request.getName())
                .replace("{version}", request.getVersion()).replace("{branch}", request.getBranch());
        log.debug("run command={}", cmd);
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
            log.debug("process output={}", new CmdOutput(process));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to generated python source by git", e);
        }

        ThriftClient client = new ThriftClient(request.getName(), request.getVersion(), request.getBranch());
        saveToFile(client, new File(String.format("%s/%s-%s.json", generatedPythonDir, client.getName(), client.getVersion())));
        clientsWithSameService.put(request.getVersion(),
                client);
    }


    @Override
    public Boolean deleteServiceByNameVersion(String name, String version) {
        Map<String, ThriftClient> clientsWithSameService = thriftClients.computeIfAbsent(
                name, k -> new ConcurrentHashMap<>());
        if (!clientsWithSameService.containsKey(version)) {
            throw new RuntimeException(String.format("Service=%s and version=%s already not existed",
                    name, version));
        }
        clientsWithSameService.remove(version);
        String tarLocation = String.format("%s/%s-%s.tar", tarDir, name, version);
        String jsonLocation = String.format("%s/%s-%s.json", generatedPythonDir, name, version);
        File compressFile = new File(tarLocation);
        File jsonFile = new File(jsonLocation);
        if (compressFile.exists()) {
            try {
                if (compressFile.delete()) {
                    log.debug("delete tar file success");
                }
            } catch (Exception e) {
                throw new RuntimeException("delete tar file failed", e);
            }
        } else {
            log.debug("tar file not existed");
        }
        if (jsonFile.exists()) {
            try {
                if (jsonFile.delete()) {
                    log.debug("delete json file success");
                }
            } catch (Exception e) {
                throw new RuntimeException("delete json file failed", e);
            }
        } else {
            log.debug("json file not existed");
        }
        String pythonPath = String.format("%s/%s/%s", generatedPythonDir, name, version);
        String srcPath = String.format("%s/%s/%s", thriftFileDir, name, version);
        try {
            FileUtils.deleteDirectory(new File(pythonPath));
            FileUtils.deleteDirectory(new File(srcPath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete python code dir or src dir", e);
        }
        return true;
    }

    @Override
    public List<ThriftClient> listClientByName(String name) {
        return new ArrayList<>(thriftClients.computeIfAbsent(name, k -> new ConcurrentHashMap<>()).values());
    }

    @Override
    public List<String> listClientNames() {
        return new ArrayList<>(thriftClients.keySet());
    }

    @Override
    public CmdOutput runCommand(RunCmdRequest request) {

        ///{client}/{version}/{serviceName}-remote -h {address} {function} {args}
        try {
            thriftClients.get(request.getName()).get(request.getVersion());
        } catch (Exception ex) {
            throw new RuntimeException(String.format("Name=%s and version=%s not exist", request.getName(), request.getVersion()));
        }

        String address = serverAddressService.query(request.getService(), request.getTags());
        if (address == null) {
            throw new RuntimeException("Cannot find address for " + request.getService());
        }
        String cmd = runServiceMethod.replace("{client}", request.getName())
                .replace("{version}", request.getVersion())
                .replace("{function}", request.getMethod())
                .replace("{address}", address)
                .replace("{serviceName}", request.getService());
        String[] commands = cmd.split(" ");
        //analysis args input
        Velocity.init();
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("Context", ruleDataService.context());
        velocityContext.put("Image", ruleDataService.image());
        velocityContext.put("Price", ruleDataService.price());
        velocityContext.put("PriceInt", ruleDataService.priceInt());
        velocityContext.put("Video", ruleDataService.video());
        List<String> args = new ArrayList<>();
        for (int i = 0; i < request.getArgs().size(); i++) {
            if (request.getArgs().get(i).indexOf('$') != -1) {
                StringWriter w = new StringWriter();
                Velocity.evaluate(velocityContext, w, "args", request.getArgs().get(i));
                args.add(w.toString());
            } else
                args.add(request.getArgs().get(i));
        }

        if (!args.isEmpty()) {
            commands = ArrayUtils.addAll(commands, args.toArray(new String[args.size()]));
        }
        log.debug("run command={}", (Object) commands);
//        try {
//            Process process = Runtime.getRuntime().exec(commands);
//            log.debug("process:{}", process);
//            process.waitFor();
//            return new CmdOutput(process);
//        } catch (IOException | InterruptedException e) {
//            log.debug("Run cmd={} failed", cmd, e);
//            throw new RuntimeException("Failed to run command=" + cmd, e);
//        }

        try {
            Process process = Runtime.getRuntime().exec(commands);
            log.debug("process:{}", process);
            process.waitFor();
            //save information to file and mongodb;
            CmdOutput cmdResponse = new CmdOutput(process);
            historySaveService.historySave(request, cmdResponse);

            return cmdResponse;
        } catch (IOException | InterruptedException e) {
            log.debug("Run cmd={} failed", cmd, e);
            throw new RuntimeException("Failed to run command=" + cmd, e);
        }


    }


}
