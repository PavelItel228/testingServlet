package com.kpi.testing.controller;

import com.kpi.testing.controller.command.*;
import com.kpi.testing.controller.command.get.*;
import com.kpi.testing.controller.command.get.UserHomeCommand;
import com.kpi.testing.controller.command.post.*;
import com.kpi.testing.service.InspectorService;
import com.kpi.testing.service.ReportOwnerService;
import com.kpi.testing.service.ReportService;
import com.kpi.testing.service.UserService;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Servlet extends HttpServlet {
    private final Map<String, Command> getCommands = new HashMap<>();
    private final Map<String, Command> postCommands = new HashMap<>();
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Servlet.class);

    @Override
    public void init() throws ServletException {
        super.init();
        System.setProperty("org.slf4j.simpleLogger.showDateTime","true");
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel","info");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd::HH-mm-ss-SSS");
        UserService userService = new UserService();
        ReportService reportService = new ReportService();
        ReportOwnerService reportOwnerService = new ReportOwnerService();
        InspectorService inspectorService = new InspectorService();
        getCommands.put("accounts/login", new LoginCommand());
        getCommands.put("index", new IndexCommand(userService));
        getCommands.put("error", new ErrorCommand());
        getCommands.put("accounts/registration", new RegistrationCommand());
        getCommands.put("accounts/logout", new LogoutCommand());
        getCommands.put("userHome", new UserHomeCommand(reportService, userService));
        getCommands.put("inspHome", new InspHomeCommand(reportService, userService));
        getCommands.put("userHome/add", new AddCommand());
        getCommands.put("userHome/update/[0-9]*", new UpdateCommand(reportService, userService));
        getCommands.put("inspHome/decline/[0-9]*", new DeclineCommand(reportService, userService));

        postCommands.put("accounts/login", new PostLoginCommand(userService));
        postCommands.put("accounts/registration", new PostRegistrationCommand(userService));
        postCommands.put("userHome/add", new PostAddCommand(reportService, userService));
        postCommands.put("userHome/update/[0-9]*", new PostUpdateCommand(reportService, userService));
        postCommands.put("userHome/change/[0-9]*", new PostChangeInspector(reportService,userService, reportOwnerService));
        postCommands.put("inspHome/decline/[0-9]*", new PostDeclineCommand(reportService,userService, inspectorService));
        postCommands.put("inspHome/accept/[0-9]*", new PostAcceptCommand(reportService,userService, inspectorService));

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response, postCommands);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response, getCommands);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response, Map<String, Command> commands) throws IOException,
            ServletException {
        String path = request.getRequestURI();
        path = path.replaceAll(".*/app/" , "");
        String match = commands.keySet().stream().filter(path::matches).findFirst().orElse("error");
        Command command = commands.getOrDefault(match , new ErrorCommand());
        logger.info("URI: " + path +" is redirected to: " + command.getClass().getSimpleName() + " command");
        command.execute(request, response);
    }
}
