package org.kuali.student.enrollment.class2.registration.admin.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.student.common.collection.KSCollectionUtils;
import org.kuali.student.common.uif.service.impl.KSViewHelperServiceImpl;
import org.kuali.student.common.util.security.ContextUtils;
import org.kuali.student.core.person.dto.PersonAffiliationInfo;
import org.kuali.student.core.person.dto.PersonInfo;
import org.kuali.student.core.person.service.impl.PersonServiceConstants;
import org.kuali.student.enrollment.class2.courseoffering.util.CourseOfferingViewHelperUtil;
import org.kuali.student.enrollment.class2.registration.admin.form.AdminRegistrationForm;
import org.kuali.student.enrollment.class2.registration.admin.form.RegistrationActivity;
import org.kuali.student.enrollment.class2.registration.admin.form.RegistrationCourse;
import org.kuali.student.enrollment.class2.registration.admin.service.AdminRegistrationViewHelperService;
import org.kuali.student.enrollment.class2.registration.admin.util.AdminRegConstants;
import org.kuali.student.enrollment.class2.registration.admin.util.AdminRegistrationClientCache;
import org.kuali.student.enrollment.class2.registration.admin.util.AdminRegistrationUtil;
import org.kuali.student.enrollment.courseoffering.dto.ActivityOfferingInfo;
import org.kuali.student.enrollment.courseoffering.dto.CourseOfferingInfo;
import org.kuali.student.enrollment.courseoffering.dto.FormatOfferingInfo;
import org.kuali.student.enrollment.courseoffering.dto.OfferingInstructorInfo;
import org.kuali.student.enrollment.courseoffering.dto.RegistrationGroupInfo;
import org.kuali.student.enrollment.courseregistration.dto.ActivityRegistrationInfo;
import org.kuali.student.enrollment.courseregistration.dto.CourseRegistrationInfo;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.util.TimeOfDayHelper;
import org.kuali.student.r2.core.acal.dto.TermInfo;
import org.kuali.student.r2.core.room.dto.BuildingInfo;
import org.kuali.student.r2.core.room.dto.RoomInfo;
import org.kuali.student.r2.core.scheduling.dto.ScheduleComponentInfo;
import org.kuali.student.r2.core.scheduling.dto.ScheduleInfo;
import org.kuali.student.r2.core.scheduling.dto.TimeSlotInfo;
import org.kuali.student.r2.core.scheduling.util.SchedulingServiceUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Blue Team (SA)
 * Date: 17 July 2014
 *
 * Implementation of the AdminRegistrationViewHelperService that contains helper methods that support the Admin Reg Controller.
 */
public class AdminRegistrationViewHelperServiceImpl extends KSViewHelperServiceImpl implements AdminRegistrationViewHelperService {

    @Override
    public void populateStudentInfo(AdminRegistrationForm form) throws Exception {

        try {
            PersonInfo personInfo = AdminRegistrationUtil.getPersonService().getPerson(form.getPersonInfo().getId().toUpperCase(), createContextInfo());
            //KSENROLL-13558 :work around for incorrect Data
            form.getPrincipalIDs().addAll(AdminRegistrationUtil.getIdentityService().getPrincipalsByEntityId(personInfo.getId().toUpperCase()));

            List<PersonAffiliationInfo> personAffiliationInfos = AdminRegistrationUtil.getPersonService().getPersonAffiliationsByPerson(personInfo.getId(), createContextInfo());

            Boolean validStudent = false;
            for (PersonAffiliationInfo personAffiliationInfo : personAffiliationInfos) {
                if (personAffiliationInfo.getTypeKey().equals(PersonServiceConstants.PERSON_AFFILIATION_TYPE_PREFIX + AdminRegConstants.STUDENT_AFFILIATION_TYPE_CODE.toLowerCase())) {
                    validStudent = true;
                }
            }
            if (!validStudent) {
//                GlobalVariables.getMessageMap().putError(AdminRegConstants.STUDENT_INFO_SECTION_STUDENT_ID, AdminRegConstants.ADMIN_REG_MSG_ERROR_INVALID_STUDENT,form.getStudentId());
//                return;
            }
            form.setPersonInfo(personInfo);
        } catch (DoesNotExistException dne) {
            GlobalVariables.getMessageMap().putError(AdminRegConstants.PERSON_ID, AdminRegConstants.ADMIN_REG_MSG_ERROR_INVALID_STUDENT, form.getPersonInfo().getId());
        }
    }

    @Override
    public TermInfo getTermByCode(String termCode) {

        try {
            TermInfo term = AdminRegistrationClientCache.getTermByCode(termCode);
            if (term == null) {
                GlobalVariables.getMessageMap().putError(AdminRegConstants.TERM_CODE, AdminRegConstants.ADMIN_REG_MSG_ERROR_INVALID_TERM);
                return null;
            }
            return term;
        } catch (Exception e) {
            throw convertServiceExceptionsToUI(e);
        }
    }

    @Override
    public List<RegistrationCourse> getCourseRegForStudentAndTerm(String studentId, String termCode) {

        List<RegistrationCourse> registeredCourses = new ArrayList<RegistrationCourse>();

        try {

            List<CourseRegistrationInfo> courseRegistrationInfos = AdminRegistrationUtil.getCourseRegistrationService().getCourseRegistrationsByStudentAndTerm(
                    studentId, termCode, createContextInfo());

            for (CourseRegistrationInfo courseRegInfo : courseRegistrationInfos) {
                RegistrationCourse registeredCourse = createRegistrationCourse(courseRegInfo);

                List<ActivityRegistrationInfo> activityRegistrations = AdminRegistrationUtil.getCourseRegistrationService().getActivityRegistrationsForCourseRegistration(
                        courseRegInfo.getId(), createContextInfo());
                registeredCourse.setActivities(createRegistrationActivitiesFromList(activityRegistrations));
                registeredCourses.add(registeredCourse);
            }

        } catch (Exception e) {
            throw convertServiceExceptionsToUI(e);
        }
        return registeredCourses;
    }

    @Override
    public List<RegistrationCourse> getCourseWaitListForStudentAndTerm(String studentId, String termCode) {

        List<RegistrationCourse> waitListCourses = new ArrayList<RegistrationCourse>();

        try {
            //Using the student Id and term info to retrieve the existing waitlisted courses for that student
            List<CourseRegistrationInfo> courseWaitListInfos = AdminRegistrationUtil.getCourseWaitlistService().getCourseWaitListRegistrationsByStudentAndTerm(
                    studentId, termCode, createContextInfo());

            for (CourseRegistrationInfo courseWaitListInfo : courseWaitListInfos) {
                RegistrationCourse waitListCourse = createRegistrationCourse(courseWaitListInfo);

                //Getting the list of existing activities for waitlisted courses and adding it
                List<ActivityRegistrationInfo> activityRegistrations = AdminRegistrationUtil.getCourseWaitlistService().getActivityWaitListRegistrationsForCourseRegistration(
                        courseWaitListInfo.getId(), createContextInfo());
                waitListCourse.setActivities(createRegistrationActivitiesFromList(activityRegistrations));
                waitListCourses.add(waitListCourse);
            }

        } catch (Exception e) {
            throw convertServiceExceptionsToUI(e);
        }
        return waitListCourses;
    }

    /**
     *
     * @param courseRegistrationListInfo
     * @return
     * @throws DoesNotExistException
     * @throws InvalidParameterException
     * @throws MissingParameterException
     * @throws OperationFailedException
     * @throws PermissionDeniedException
     * @throws ParseException
     */
    private RegistrationCourse createRegistrationCourse(CourseRegistrationInfo courseRegistrationListInfo)
            throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ParseException {

        CourseOfferingInfo coInfo = AdminRegistrationUtil.getCourseOfferingService().getCourseOffering(courseRegistrationListInfo.getCourseOfferingId(), createContextInfo());

        RegistrationCourse registrationCourse = new RegistrationCourse();
        registrationCourse.setCode(coInfo.getCourseOfferingCode());
        registrationCourse.setTitle(coInfo.getCourseOfferingTitle());
        registrationCourse.setCredits(Integer.parseInt(coInfo.getCreditCnt()));
        registrationCourse.setTransactionalDate(generateFormattedDate(courseRegistrationListInfo.getMeta().getCreateTime()));
        registrationCourse.setEffectiveDate(generateFormattedDate(courseRegistrationListInfo.getEffectiveDate()));

        registrationCourse.setSection(AdminRegistrationUtil.getCourseOfferingService().getRegistrationGroup(
                courseRegistrationListInfo.getRegistrationGroupId(), createContextInfo()).getRegistrationCode());
        return registrationCourse;
    }

    /**
     * Method was created to formatted and parse the date in
     * the correct way. If SimpleDateFormat is used it parses the
     * day in the month and gets an exception.
     *
     * @param date
     * @return
     */
    private Date generateFormattedDate(Date date) {
        StringBuilder formattedDate = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String formattedStringDate = sdf.format(date);
        formattedDate.append(formattedStringDate.substring(3, 5));
        formattedDate.append("/");
        formattedDate.append(formattedStringDate.substring(0, 2));
        formattedDate.append("/");
        formattedDate.append(formattedStringDate.substring(6, 10));

        try {
            return sdf.parse(formattedDate.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * Create Registration Activities based on the given list of activity registrations.
     *
     * @param activityRegistrations
     * @return
     * @throws InvalidParameterException
     * @throws MissingParameterException
     * @throws OperationFailedException
     * @throws PermissionDeniedException
     * @throws DoesNotExistException
     */
    private List<RegistrationActivity> createRegistrationActivitiesFromList(List<ActivityRegistrationInfo> activityRegistrations)
            throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {

        List<RegistrationActivity> registrationActivities = new ArrayList<RegistrationActivity>();
        for (ActivityRegistrationInfo activityRegInfo : activityRegistrations) {
            ActivityOfferingInfo aoInfo = AdminRegistrationUtil.getCourseOfferingService().getActivityOffering(activityRegInfo.getActivityOfferingId(), createContextInfo());
            registrationActivities.add(createRegistrationActivity(aoInfo));
        }
        return registrationActivities;
    }

    @Override
    public void validateForRegistration(AdminRegistrationForm form) {

        for (int i = 0; i < form.getPendingCourses().size(); i++) {
            RegistrationCourse course = form.getPendingCourses().get(i);

            List<RegistrationGroupInfo> regGroups = new ArrayList<RegistrationGroupInfo>();
            try {
                CourseOfferingInfo courseOffering = AdminRegistrationClientCache.getCourseOfferingByCodeAndTerm(form.getTermCode(), course.getCode());
                if (courseOffering == null) {
                    GlobalVariables.getMessageMap().putError(AdminRegConstants.PENDING_COURSES + "[" + i + "]." + AdminRegConstants.CODE,
                            AdminRegConstants.ADMIN_REG_MSG_ERROR_COURSE_CODE_TERM_INVALID);
                    continue;
                }

                //First get the format offerings for the course offering to get the registration groups linked to the FO.
                List<FormatOfferingInfo> formatOfferings = AdminRegistrationUtil.getCourseOfferingService().getFormatOfferingsByCourseOffering(
                        courseOffering.getId(), ContextUtils.createDefaultContextInfo());
                for (FormatOfferingInfo formatOffering : formatOfferings) {
                    regGroups.addAll(AdminRegistrationUtil.getCourseOfferingService().getRegistrationGroupsByFormatOffering(
                            formatOffering.getId(), ContextUtils.createDefaultContextInfo()));
                }
            } catch (Exception e) {
                throw convertServiceExceptionsToUI(e);
            }

            //Check if the input section matches a registration group.
            RegistrationGroupInfo registrationGroup = null;
            for (RegistrationGroupInfo regGroup : regGroups) {
                if (course.getSection().equals(regGroup.getName())) {
                    course.setActivityOfferingIds(regGroup.getActivityOfferingIds());
                    registrationGroup = regGroup;
                }
            }

            //Add error message when no registration group was found for given section.
            if (registrationGroup == null) {
                GlobalVariables.getMessageMap().putError(AdminRegConstants.PENDING_COURSES + "[" + i + "]" + AdminRegConstants.SECTION,
                        AdminRegConstants.ADMIN_REG_MSG_ERROR_SECTION_CODE_INVALID);
            }
        }
    }

    @Override
    public List<RegistrationActivity> getRegistrationActivitiesForRegistrationCourse(RegistrationCourse registrationCourse, String termCode) {

        List<RegistrationActivity> registrationActivities = new ArrayList<RegistrationActivity>();
        try {
            List<ActivityOfferingInfo> activityOfferings = AdminRegistrationUtil.getCourseOfferingService().getActivityOfferingsByIds(
                    registrationCourse.getActivityOfferingIds(), createContextInfo());
            for (ActivityOfferingInfo activityOffering : activityOfferings) {
                registrationActivities.add(createRegistrationActivity(activityOffering));
            }
        } catch (Exception e) {
            throw convertServiceExceptionsToUI(e);
        }

        return registrationActivities;
    }

    /**
     * Create a single Registration Activity based on a activity offering.
     *
     * @param activityOffering
     * @return
     * @throws DoesNotExistException
     * @throws InvalidParameterException
     * @throws MissingParameterException
     * @throws OperationFailedException
     * @throws PermissionDeniedException
     */
    private RegistrationActivity createRegistrationActivity(ActivityOfferingInfo activityOffering)
            throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {

        RegistrationActivity regActivity = new RegistrationActivity();

        // Assume only zero or one (should never be more than 1 until we support partial colo)
        OfferingInstructorInfo offeringInstructorInfo = CourseOfferingViewHelperUtil.findDisplayInstructor(activityOffering.getInstructors());
        regActivity.setInstructor(offeringInstructorInfo.getPersonName());
        regActivity.setType(activityOffering.getName());

        List<ScheduleInfo> scheduleInfos = AdminRegistrationUtil.getSchedulingService().getSchedulesByIds(activityOffering.getScheduleIds(), createContextInfo());

        StringBuilder dateTimeSchedule = new StringBuilder();
        StringBuilder roomBuildInfo = new StringBuilder();

        for (ScheduleInfo scheduleInfo : scheduleInfos) {
            /**
             * Until we implement external scheduler, there is going to be only one Schedule component for every scheduleinfo
             * and the UI doesn't allow us to add multiple components to a schedule request.
             */
            ScheduleComponentInfo componentInfo = KSCollectionUtils.getOptionalZeroElement(scheduleInfo.getScheduleComponents());

            List<TimeSlotInfo> timeSlotInfos = AdminRegistrationUtil.getSchedulingService().getTimeSlotsByIds(componentInfo.getTimeSlotIds(), createContextInfo());
            // Assume only zero or one (should never be more than 1 until we support partial colo)
            TimeSlotInfo timeSlotInfo = KSCollectionUtils.getOptionalZeroElement(timeSlotInfos);

            dateTimeSchedule.append(SchedulingServiceUtil.weekdaysList2WeekdaysString(timeSlotInfo.getWeekdays()));
            dateTimeSchedule.append(" ");

            dateTimeSchedule.append(TimeOfDayHelper.makeFormattedTimeForAOSchedules(timeSlotInfo.getStartTime()));
            dateTimeSchedule.append(" - ");
            dateTimeSchedule.append(TimeOfDayHelper.makeFormattedTimeForAOSchedules(timeSlotInfo.getEndTime()));
            regActivity.setDateTime(dateTimeSchedule.toString());

            try {
                RoomInfo room = AdminRegistrationUtil.getRoomService().getRoom(componentInfo.getRoomId(), createContextInfo());
                //retrieve the buildingInfo from the Room.
                BuildingInfo buildingInfo = AdminRegistrationUtil.getRoomService().getBuilding(room.getBuildingId(), createContextInfo());
                roomBuildInfo.append(buildingInfo.getBuildingCode());
                roomBuildInfo.append(" ");
                roomBuildInfo.append(room.getRoomCode());
                regActivity.setRoom(roomBuildInfo.toString());

            } catch (Exception e) {
                throw new RuntimeException("Could not retrieve Room RoomService for " + e);
            }

        }
        return regActivity;
    }

    @Override
    public void getRegistrationStatus() {

    }

    @Override
    public void submitRegistrationRequest() {

        // get the regGroup
        //RegGroupSearchResult rg = CourseRegistrationAndScheduleOfClassesUtil.getRegGroup(null, termCode, courseCode, regGroupCode, regGroupId, contextInfo);

        // get the registration group, returns default (from Course Offering) credits (as creditId) and grading options (as a string of options)
        //CourseOfferingInfo courseOfferingInfo = CourseRegistrationAndScheduleOfClassesUtil.getCourseOfferingIdCreditGrading(rg.getCourseOfferingId(), courseCode, rg.getTermId(), termCode);

        // verify passed credits (must be non-empty unless fixed) and grading option (can be null)
        //credits = verifyRegistrationRequestCreditsGradingOption(courseOfferingInfo, credits, gradingOptionId, contextInfo);

        //Create the request object
        //RegistrationRequestInfo regReqInfo = createRegistrationRequest(contextInfo.getPrincipalId(), rg.getTermId(), rg.getRegGroupId(), null, credits, gradingOptionId, LprServiceConstants.LPRTRANS_REGISTER_TYPE_KEY, LprServiceConstants.LPRTRANS_NEW_STATE_KEY, LprServiceConstants.REQ_ITEM_ADD_TYPE_KEY, LprServiceConstants.LPRTRANS_ITEM_NEW_STATE_KEY, okToWaitlist);

        // persist the request object in the service
        //RegistrationRequestInfo newRegReq = CourseRegistrationAndScheduleOfClassesUtil.getCourseRegistrationService().createRegistrationRequest(LprServiceConstants.LPRTRANS_REGISTER_TYPE_KEY, regReqInfo, contextInfo);

        // submit the request to the registration engine.
        //return CourseRegistrationAndScheduleOfClassesUtil.getCourseRegistrationService().submitRegistrationRequest(newRegReq.getId(), contextInfo);
    }

    /**
     * This method is used for the course code suggest field on the input section on the client.
     *
     * @param termCode
     * @param courseCode
     * @return
     * @throws InvalidParameterException
     * @throws MissingParameterException
     * @throws PermissionDeniedException
     * @throws OperationFailedException
     */
    public List<String> retrieveCourseCodes(String termCode, String courseCode)
            throws InvalidParameterException, MissingParameterException, PermissionDeniedException, OperationFailedException {

        if (courseCode == null || courseCode.isEmpty()) {
            return new ArrayList<String>();   // if nothing passed in, return empty list
        }

        courseCode = courseCode.toUpperCase(); // force toUpper
        return AdminRegistrationClientCache.retrieveCourseCodes(termCode, courseCode);
    }

    /**
     * This method is called on an ajax call from the client when a course code is entered in the input section.
     *
     * @param course
     * @param termCode
     * @return
     * @throws MissingParameterException
     * @throws InvalidParameterException
     * @throws OperationFailedException
     * @throws PermissionDeniedException
     */
    public String retrieveCourseTitle(RegistrationCourse course, String termCode)
            throws MissingParameterException, InvalidParameterException, OperationFailedException, PermissionDeniedException {

        String courseCode = course.getCode();
        if (courseCode == null || courseCode.isEmpty()) {
            course.setTitle(StringUtils.EMPTY);
        } else {

            CourseOfferingInfo courseOffering = AdminRegistrationClientCache.getCourseOfferingByCodeAndTerm(termCode, courseCode);
            if (courseOffering != null) {
                course.setTitle(courseOffering.getCourseOfferingTitle());
            } else {
                course.setTitle(StringUtils.EMPTY);
            }
        }

        return course.getTitle();
    }

}
