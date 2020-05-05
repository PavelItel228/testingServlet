package com.kpi.testing.controller.command.post;

import com.kpi.testing.controller.command.Command;
import com.kpi.testing.dto.DeclineReasonDTO;
import com.kpi.testing.entity.Report;
import com.kpi.testing.entity.User;
import com.kpi.testing.exceptions.UnknownReportError;
import com.kpi.testing.service.ReportService;
import com.kpi.testing.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class PostAcceptCommand implements Command {
    private final ReportService reportService;
    private  final UserService userService;

    public PostAcceptCommand(ReportService reportService, UserService userService) {
        this.reportService = reportService;
        this.userService = userService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String path = request.getRequestURI();
        User user;
        try {
            long userId = Long.parseLong(request.getSession().getAttribute("user").toString());
            user = userService.findById(userId);
        } catch (Exception ex) {
            response.sendRedirect(request.getContextPath()+"/app" + "/error");
            return;
        }
        Long reportId = Long.parseLong(path.replaceAll(".*/app/inspHome/accept/" , ""));
        if (user.getReportsInspected().stream().noneMatch(report -> report.getId().equals(reportId))){
            response.sendRedirect(request.getContextPath()+"/app" + "/error");
            return;
        }
        try {
            Report report = reportService.getById(reportId);
            try {
                reportService.acceptReport(report, user);
            } catch (SQLException exception) {
                exception.printStackTrace();
                throw new RuntimeException();
            }
            response.sendRedirect(request.getContextPath()+"/app" + "/inspHome");
        } catch (UnknownReportError error) {
            throw new RuntimeException();
        }
    }
}