package com.dlsu.controller;

import com.dlsu.model.*;
import com.dlsu.service.ApplicationService;
import com.dlsu.service.FeesService;
import com.dlsu.service.ReportService;
import com.dlsu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.management.Query;
import java.util.Date;

/**
 * Created by avggo on 7/30/2017.
 */

@Controller
public class ChiefController {

    @Autowired
    UserService userService;

    @Autowired
    ApplicationService applicationService;

    @Autowired
    FeesService feesService;

    @Autowired
    ReportService reportService;

    @RequestMapping(value = {"/chief-home"}, method = RequestMethod.GET)
    public ModelAndView adminHome(@RequestParam("curUserId") Integer curUserId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/chief-home");

        User user = userService.findUserByUserId(curUserId);

        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("applications", applicationService.getReadyForPaymentApplications());

        return modelAndView;
    }

    @RequestMapping(value = {"/enter-application-chief"}, method = RequestMethod.GET)
    public ModelAndView enterApplication(@RequestParam("curUserId") Integer curUserId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/enter-application");

        User user = userService.findUserByUserId(curUserId);

        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("success", "success");

        return modelAndView;
    }

    @RequestMapping(value = {"/enter-application-chief"}, method = RequestMethod.POST)
    public ModelAndView enterApplication(@RequestParam("curUserId") Integer curUserId, @RequestParam("referenceNumber") Integer referenceNumber) {
        ModelAndView modelAndView = new ModelAndView();


        User user = userService.findUserByUserId(curUserId);

        if(applicationService.findByReferenceNumber(referenceNumber) != null) {
            modelAndView.addObject("success", "success");
            modelAndView.addObject("currentApplication", applicationService.findByReferenceNumber(referenceNumber));
            modelAndView.setViewName("/chief/view-application");
        }
        else {
            modelAndView.addObject("succcess", "fail");
            modelAndView.setViewName("/chief/enter-application");
        }

        modelAndView.addObject("currentUser", user);

        return modelAndView;
    }

    @RequestMapping(value = {"/chief-home-processing"}, method = RequestMethod.GET)
    public ModelAndView adminHomeProcessing(@RequestParam("curUserId") Integer curUserId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/chief-home");

        User user = userService.findUserByUserId(curUserId);

        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("applications", applicationService.getProcessingApplications());

        return modelAndView;
    }

    @RequestMapping(value = {"/record-list"}, method = RequestMethod.GET)
    public ModelAndView recordList(@RequestParam("curUserId") Integer curUserId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/records-list");

        User user = userService.findUserByUserId(curUserId);

        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("applications", applicationService.getApprovedApplications());

        return modelAndView;
    }

    @RequestMapping(value = {"/report-employee-chief"}, method = RequestMethod.GET)
    public ModelAndView reportEmployee(@RequestParam("curUserId") Integer curUserId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/report-employee");

        User user = userService.findUserByUserId(curUserId);

        System.out.print(user.getUserId());

        Report report = new Report();
        report.setAdminId(curUserId);

        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("report", report);

        return modelAndView;
    }

    @RequestMapping(value = {"/report-employee-chief"}, method = RequestMethod.POST)
    public ModelAndView saveReportEmployee(@RequestParam("curUserId") Integer curUserId, Report report, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/report-employee");

        User user = userService.findUserByUserId(curUserId);

        modelAndView.addObject("report", new Report());
        modelAndView.addObject("currentUser", user);
        reportService.createNewReport(report);

        modelAndView.addObject("success", "success");

        return modelAndView;
    }

    @RequestMapping(value = {"/approve-application"}, method = RequestMethod.GET)
    public ModelAndView approveApplication(@RequestParam("curUserId") Integer curUserId, @RequestParam("appId") Integer appId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/approve-application");

        User user = userService.findUserByUserId(curUserId);
        BPApplication app = applicationService.findByIdNumber(appId);
        Fees fees = feesService.findByAppId(appId);

        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("app", app);
        modelAndView.addObject("fees", fees);
        modelAndView.addObject("bpldAssess", userService.findUserByUserId(app.getAssessBpld()));
        modelAndView.addObject("engAssess", userService.findUserByUserId(app.getAssessEng()));


        return modelAndView;
    }

    @RequestMapping(value = {"/approve-application"}, method = RequestMethod.POST)
    public ModelAndView approveFee(@RequestParam("curUserId") Integer curUserId, @RequestParam("appId") Integer appId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/approve-application");

        User user = userService.findUserByUserId(curUserId);
        BPApplication app = applicationService.findByIdNumber(appId);
        Fees fees = feesService.findByAppId(appId);

        app.setStep(6);
        app.setStatus("approved");

        applicationService.createNewApplication(app);

        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("app", app);
        modelAndView.addObject("fees", fees);
        modelAndView.addObject("bpldAssess", userService.findUserByUserId(app.getAssessBpld()));
        modelAndView.addObject("engAssess", userService.findUserByUserId(app.getAssessEng()));

        return modelAndView;
    }

    @RequestMapping(value = {"/disapprove-application"}, method = RequestMethod.POST)
    public ModelAndView disapproveFee(@RequestParam("curUserId") Integer curUserId, @RequestParam("appId") Integer appId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/approve-application");

        User user = userService.findUserByUserId(curUserId);
        Fees fees = feesService.findByAppId(appId);
        BPApplication app = applicationService.findByIdNumber(appId);

        app.setStep(6);
        app.setStatus("disapproved");

        applicationService.createNewApplication(app);

        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("app", app);
        modelAndView.addObject("fees", fees);
        modelAndView.addObject("bpldAssess", userService.findUserByUserId(app.getAssessBpld()));
        modelAndView.addObject("engAssess", userService.findUserByUserId(app.getAssessEng()));

        return modelAndView;
    }

    @RequestMapping(value = {"/view-application-chief"}, method = RequestMethod.GET)
    public ModelAndView viewApplication(@RequestParam("curUserId") Integer curUserId, @RequestParam("appId") Integer appId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/view-application");

        User user = userService.findUserByUserId(curUserId);
        Fees fees = feesService.findByAppId(appId);

        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("fees", fees);
        if(fees != null)
            modelAndView.addObject("total", fees.getTotal());
        modelAndView.addObject("currentApplication", applicationService.findByIdNumber(appId));

        return modelAndView;
    }

    @RequestMapping(value = {"/encode-chief"}, method = RequestMethod.GET)
    public ModelAndView encodeApplication(@RequestParam("curUserId") Integer curUserId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/encode");

        User user = userService.findUserByUserId(curUserId);

        modelAndView.addObject("currentUser", user);

        BPApplication app = new BPApplication();
        app.setBpldId(curUserId);

        modelAndView.addObject("newBPApplication", app);

        return modelAndView;
    }

    @RequestMapping(value = {"/encode-chief"}, method = RequestMethod.POST)
    public ModelAndView encodeApplication(@RequestParam("curUserId") Integer curUserId, BPApplication newBPApplication, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/encode");

        User user = userService.findUserByUserId(curUserId);

        modelAndView.addObject("currentUser", user);

        newBPApplication.setEncodedDate(new Date());
        applicationService.createNewApplication(newBPApplication);

        BPApplication app = new BPApplication();
        app.setBpldId(curUserId);

        modelAndView.addObject("newBPApplication", app);
        modelAndView.addObject("success", "success");

        return modelAndView;
    }

    @RequestMapping(value = {"/choose-employee-report"}, method = RequestMethod.GET)
    public ModelAndView chooseEmployeeReport(@RequestParam("curUserId") Integer curUserId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/choose-employee-report");

        User user = userService.findUserByUserId(curUserId);
        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("reports", reportService.getAllReports());

        return modelAndView;
    }

    @RequestMapping(value = {"/view-employee-report"}, method = RequestMethod.GET)
    public ModelAndView viewEmployeeReport(@RequestParam("curUserId") Integer curUserId, @RequestParam("reportId") Integer reportId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/view-employee-report");

        User user = userService.findUserByUserId(curUserId);
        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("report", reportService.findById(reportId));

        return modelAndView;
    }

    @RequestMapping(value = {"/choose-generate-report"}, method = RequestMethod.GET)
    public ModelAndView chooseGenerateReport(@RequestParam("curUserId") Integer curUserId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/choose-generate-report");

        User user = userService.findUserByUserId(curUserId);
        modelAndView.addObject("currentUser", user);

        return modelAndView;
    }

    @RequestMapping(value = {"/reports-encoded-per-employee"}, method = RequestMethod.GET)
    public ModelAndView reportsEncodedPerEmployee(@RequestParam("curUserId") Integer curUserId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/reports-encoded-per-employee");

        User user = userService.findUserByUserId(curUserId);
        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("employees", userService.findEncoders());
        modelAndView.addObject("query", new QueryPerEmployee());

        return modelAndView;
    }

    @RequestMapping(value = {"/reports-encoded-per-employee"}, method = RequestMethod.POST)
    public ModelAndView reportsEncodedPerEmployee(@RequestParam("curUserId") Integer curUserId, QueryPerEmployee query) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/reports-encoded-per-employee");


        User user = userService.findUserByUserId(curUserId);
        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("employees", userService.findEncoders());
        modelAndView.addObject("apps", applicationService.getEncodedApplicationsByEmployee(query.getId(), query.getFromDate(), query.getToDate()));
        modelAndView.addObject("employee", userService.findUserByUserId(query.getId()));
        modelAndView.addObject("query", new QueryPerEmployee());

        return modelAndView;
    }

    @RequestMapping(value = {"/reports-assessed-per-employee"}, method = RequestMethod.GET)
    public ModelAndView reportsAssessedPerEmployee(@RequestParam("curUserId") Integer curUserId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/reports-assessed-per-employee");

        User user = userService.findUserByUserId(curUserId);
        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("employees", userService.findAssessors());
        modelAndView.addObject("query", new QueryPerEmployee());

        return modelAndView;
    }

    @RequestMapping(value = {"/reports-assessed-per-employee"}, method = RequestMethod.POST)
    public ModelAndView reportsAssessedPerEmployee(@RequestParam("curUserId") Integer curUserId, QueryPerEmployee query) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/reports-assessed-per-employee");


        User user = userService.findUserByUserId(curUserId);
        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("employees", userService.findAssessors());
        modelAndView.addObject("apps", applicationService.getAssessedApplicationsByEmployee(query.getId(), query.getFromDate(), query.getToDate()));
        modelAndView.addObject("employee", userService.findUserByUserId(query.getId()));
        modelAndView.addObject("query", new QueryPerEmployee());

        return modelAndView;
    }

    @RequestMapping(value = {"/reports-inspected-per-inspector"}, method = RequestMethod.GET)
    public ModelAndView reportsInspectedPerInspector(@RequestParam("curUserId") Integer curUserId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/reports-inspected-per-inspector");

        User user = userService.findUserByUserId(curUserId);
        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("employees", userService.findInspectors());
        modelAndView.addObject("query", new QueryPerEmployee());

        return modelAndView;
    }

    @RequestMapping(value = {"/reports-inspected-per-inspector"}, method = RequestMethod.POST)
    public ModelAndView reportsInspectedPerInspector(@RequestParam("curUserId") Integer curUserId, QueryPerEmployee query) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/reports-inspected-per-inspector");


        User user = userService.findUserByUserId(curUserId);
        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("employees", userService.findInspectors());
        modelAndView.addObject("apps", applicationService.getInspectedApplicationsByInspector(query.getId(), query.getFromDate(), query.getToDate()));
        modelAndView.addObject("employee", userService.findUserByUserId(query.getId()));
        modelAndView.addObject("query", new QueryPerEmployee());

        return modelAndView;
    }

    @RequestMapping(value = {"/reports-applications-processed"}, method = RequestMethod.GET)
    public ModelAndView reportsApplicationsProcessed(@RequestParam("curUserId") Integer curUserId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/reports-applications-processed");

        User user = userService.findUserByUserId(curUserId);
        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("query", new QueryPerEmployee());

        return modelAndView;
    }

    @RequestMapping(value = {"/reports-applications-processed"}, method = RequestMethod.POST)
    public ModelAndView reportsApplicationsProcessed(@RequestParam("curUserId") Integer curUserId, QueryPerEmployee query) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/reports-applications-processed");


        User user = userService.findUserByUserId(curUserId);
        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("fromDate", query.getFromDate());
        modelAndView.addObject("toDate", query.getToDate());
        modelAndView.addObject("apps", applicationService.getProcessedApplications(query.getFromDate(), query.getToDate()));
        modelAndView.addObject("query", new QueryPerEmployee());

        return modelAndView;
    }

    @RequestMapping(value = {"/reports-applications-received"}, method = RequestMethod.GET)
    public ModelAndView reportsApplicationsReceived(@RequestParam("curUserId") Integer curUserId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/reports-applications-received");

        User user = userService.findUserByUserId(curUserId);
        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("query", new QueryPerEmployee());

        return modelAndView;
    }

    @RequestMapping(value = {"/reports-applications-received"}, method = RequestMethod.POST)
    public ModelAndView reportsApplicationsReceived(@RequestParam("curUserId") Integer curUserId, QueryPerEmployee query) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/chief/reports-applications-received");

        User user = userService.findUserByUserId(curUserId);
        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("fromDate", query.getFromDate());
        modelAndView.addObject("toDate", query.getToDate());
        modelAndView.addObject("apps", applicationService.getReceivedApplications(query.getFromDate(), query.getToDate()));
        modelAndView.addObject("query", new QueryPerEmployee());

        return modelAndView;
    }
}
