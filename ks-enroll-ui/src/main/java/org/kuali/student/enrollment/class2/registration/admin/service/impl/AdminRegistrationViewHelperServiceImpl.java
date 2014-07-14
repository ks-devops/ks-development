package org.kuali.student.enrollment.class2.registration.admin.service.impl;

import net.sf.ehcache.Element;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.student.common.collection.KSCollectionUtils;
import org.kuali.student.common.uif.service.impl.KSViewHelperServiceImpl;
import org.kuali.student.common.util.security.ContextUtils;
import org.kuali.student.enrollment.class2.courseoffering.util.CourseOfferingConstants;
import org.kuali.student.enrollment.class2.registration.admin.form.AdminRegistrationForm;
import org.kuali.student.enrollment.class2.registration.admin.form.RegistrationActivity;
import org.kuali.student.enrollment.class2.registration.admin.form.RegistrationCourse;
import org.kuali.student.enrollment.class2.registration.admin.service.CourseRegAdminViewHelperService;
import org.kuali.student.enrollment.class2.registration.admin.util.AdminRegConstants;
import org.kuali.student.enrollment.class2.registration.admin.util.AdminRegistrationUtil;
import org.kuali.student.enrollment.courseoffering.dto.ActivityOfferingInfo;
import org.kuali.student.enrollment.courseoffering.dto.CourseOfferingInfo;
import org.kuali.student.enrollment.courseoffering.infc.CourseOffering;
import org.kuali.student.enrollment.courseoffering.dto.OfferingInstructorInfo;
import org.kuali.student.enrollment.courseregistration.dto.ActivityRegistrationInfo;
import org.kuali.student.enrollment.courseregistration.dto.CourseRegistrationInfo;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.TimeOfDayInfo;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SW Genis on 2014/07/04.
 */
public class AdminRegistrationViewHelperServiceImpl extends KSViewHelperServiceImpl implements CourseRegAdminViewHelperService {
    
    private final static String CACHE_NAME = "AdminRegistrationCodeCache";
    
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

    @Override
    public TermInfo getTermByCode(String termCode) {

        try{
            QueryByCriteria.Builder qbcBuilder = QueryByCriteria.Builder.create();

            qbcBuilder.setPredicates(PredicateFactory.equal(CourseOfferingConstants.ATP_CODE, termCode));

            QueryByCriteria criteria = qbcBuilder.build();

            List<TermInfo> terms = AdminRegistrationUtil.getAcademicCalendarService().searchForTerms(criteria, createContextInfo());
            int firstTerm = 0;
            if (terms.size() > 1) {
                GlobalVariables.getMessageMap().putError("termCode", AdminRegConstants.ADMIN_REG_MSG_ERROR_MULTIPLE_TERMS);
                return null;
            }
            if (terms.isEmpty()) {
                GlobalVariables.getMessageMap().putError("termCode", AdminRegConstants.ADMIN_REG_MSG_ERROR_INVALID_TERM);
                return null;
            }
            return terms.get(firstTerm);
        }catch (Exception e){
            throw convertServiceExceptionsToUI(e);
        }
    }

    @Override
    public List<RegistrationCourse> getCourseRegStudentAndTerm(String studentId, String termCode) {

        List<RegistrationCourse> registeredCourses = new ArrayList<RegistrationCourse>();

        try {

            List<CourseRegistrationInfo> courseRegistrationInfos = AdminRegistrationUtil.getCourseRegistrationService().getCourseRegistrationsByStudentAndTerm(studentId, termCode, createContextInfo());

            for (CourseRegistrationInfo courseRegInfo : courseRegistrationInfos) {

                RegistrationCourse registeredCourse = new RegistrationCourse();
                CourseOfferingInfo coInfo = AdminRegistrationUtil.getCourseOfferingService().getCourseOffering(courseRegInfo.getCourseOfferingId(), createContextInfo());
                registeredCourse.setCode(coInfo.getCourseOfferingCode());
                registeredCourse.setTitle(coInfo.getCourseOfferingTitle());
                registeredCourse.setCredits(Integer.parseInt(coInfo.getCreditCnt()));
                registeredCourse.setRegDate(courseRegInfo.getEffectiveDate());
                registeredCourse.setSection(AdminRegistrationUtil.getCourseOfferingService().getRegistrationGroup(courseRegInfo.getRegistrationGroupId(), createContextInfo()).getRegistrationCode());

                List<ActivityRegistrationInfo> activityOfferings = AdminRegistrationUtil.getCourseRegistrationService().getActivityRegistrationsForCourseRegistration(courseRegInfo.getId(), createContextInfo());
                registeredCourse.setActivities(retrieveRegistrationActivities(activityOfferings));
                registeredCourses.add(registeredCourse);
            }

        } catch (Exception e) {
            throw convertServiceExceptionsToUI(e);
        }
        return registeredCourses;
    }

    public List<RegistrationCourse> getCourseWaitListStudentAndTerm(String studentId, String termCode) {

        List<RegistrationCourse> waitListCourses = new ArrayList<RegistrationCourse>();

        try {
            List<CourseRegistrationInfo> courseWaitListInfos = AdminRegistrationUtil.getCourseWaitlistService().getCourseWaitListRegistrationsByStudentAndTerm(studentId, termCode, createContextInfo());

            for (CourseRegistrationInfo courseWaitListInfo : courseWaitListInfos) {
                RegistrationCourse waitListCourse = new RegistrationCourse();

                CourseOfferingInfo coInfo = AdminRegistrationUtil.getCourseOfferingService().getCourseOffering(courseWaitListInfo.getCourseOfferingId(), createContextInfo());
                waitListCourse.setCode(coInfo.getCourseOfferingCode());
                waitListCourse.setTitle(coInfo.getCourseOfferingTitle());
                waitListCourse.setCredits(Integer.parseInt(coInfo.getCreditCnt()));
                waitListCourse.setRegDate(courseWaitListInfo.getEffectiveDate());
                waitListCourse.setSection(AdminRegistrationUtil.getCourseOfferingService().getRegistrationGroup(courseWaitListInfo.getRegistrationGroupId(), createContextInfo()).getRegistrationCode());

                List<ActivityRegistrationInfo> activityOfferings = AdminRegistrationUtil.getCourseWaitlistService().getActivityWaitListRegistrationsForCourseRegistration(courseWaitListInfo.getId(), createContextInfo());
                waitListCourse.setActivities(retrieveRegistrationActivities(activityOfferings));
                waitListCourses.add(waitListCourse);
            }

        } catch (Exception e) {
            throw convertServiceExceptionsToUI(e);
        }
        return waitListCourses;
    }

    /**
     * Method accpets RegistrationCourse and CourseRegistrationInfo
     * to retrieve the ActivitiesInfo
     * @param activityOfferings
     * @return
     * @throws InvalidParameterException
     * @throws MissingParameterException
     * @throws OperationFailedException
     * @throws PermissionDeniedException
     * @throws DoesNotExistException
     */
    private List<RegistrationActivity> retrieveRegistrationActivities(List<ActivityRegistrationInfo> activityOfferings) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {

        List<RegistrationActivity> registrationActivity = new ArrayList<RegistrationActivity>();

        for (ActivityRegistrationInfo activityRegInfo : activityOfferings) {
            //"Lec", "MWF 04:00pm - 05:30pm", "Steve Capriani", "PTX 2391"
            RegistrationActivity regActivity = new RegistrationActivity();

            ActivityOfferingInfo aoInfo = AdminRegistrationUtil.getCourseOfferingService().getActivityOffering(activityRegInfo.getActivityOfferingId(), createContextInfo());
            List<ScheduleInfo> scheduleInfos = AdminRegistrationUtil.getSchedulingService().getSchedulesByIds(aoInfo.getScheduleIds(), createContextInfo());

            StringBuilder dateTimeSchedule = new StringBuilder();
            StringBuilder roomBuildInfo = new StringBuilder();

            for (ScheduleInfo scheduleInfo : scheduleInfos) {
                /**
                 * Until we implement external scheduler, there is going to be only one Schedule component for every scheduleinfo
                 * and the UI doesn't allow us to add multiple components to a schedule request.
                 */
                for (ScheduleComponentInfo componentInfo : scheduleInfo.getScheduleComponents()) {

                    List<TimeSlotInfo> timeSlotInfos = AdminRegistrationUtil.getSchedulingService().getTimeSlotsByIds(componentInfo.getTimeSlotIds(), createContextInfo());
                    // Assume only zero or one (should never be more than 1 until we support partial colo)
                    TimeSlotInfo timeSlotInfo = KSCollectionUtils.getOptionalZeroElement(timeSlotInfos);

                    dateTimeSchedule.append(SchedulingServiceUtil.weekdaysList2WeekdaysString(timeSlotInfo.getWeekdays()));
                    dateTimeSchedule.append(" ");
                    //org.apache.commons.lang.StringUtils.substringBefore(timeSlotInfos.get(0).getStartTime(), " ")

                    dateTimeSchedule.append(TimeOfDayHelper.makeFormattedTimeForAOSchedules(timeSlotInfo.getStartTime()));
                    dateTimeSchedule.append(" - ");
                    dateTimeSchedule.append(TimeOfDayHelper.makeFormattedTimeForAOSchedules(timeSlotInfo.getEndTime()));

                    try {
                        RoomInfo room = AdminRegistrationUtil.getRoomService().getRoom(componentInfo.getRoomId(), createContextInfo());
                        //retrieve the buildingInfo from the Room.
                        BuildingInfo buildingInfo = AdminRegistrationUtil.getRoomService().getBuilding(room.getBuildingId(), createContextInfo());
                        roomBuildInfo.append(buildingInfo.getBuildingCode());
                        roomBuildInfo.append(" ");
                        roomBuildInfo.append(room.getRoomCode());

                    }catch (Exception e){
                        throw new RuntimeException("Could not retrieve Room RoomService for " + e);
                    }
                }
                regActivity.setType(aoInfo.getName());
                regActivity.setDateTime(dateTimeSchedule.toString());
                // Assume only zero or one (should never be more than 1 until we support partial colo)
                OfferingInstructorInfo offeringInstructorInfo = KSCollectionUtils.getOptionalZeroElement(aoInfo.getInstructors());
                regActivity.setInstructor(offeringInstructorInfo.getPersonName());
                regActivity.setRoom(roomBuildInfo.toString());
            }
            registrationActivity.add(regActivity);
        }
        return registrationActivity;
    }

    @Override
    public void populateStudentInfo(AdminRegistrationForm form) throws Exception {

        Person person = AdminRegistrationUtil.getPersonService().getPerson(form.getStudentId());
        if ((person != null)) {

            if (!person.hasAffiliationOfType(AdminRegConstants.STUDENT_AFFILIATION_TYPE_CODE)) {
//                GlobalVariables.getMessageMap().putError(AdminRegConstants.STUDENT_INFO_SECTION_STUDENT_ID, AdminRegConstants.ADMIN_REG_MSG_ERROR_INVALID_STUDENT,form.getStudentId());
//                return;
            }
            form.setStudentName(person.getFirstName() + " " + person.getLastName());
        } else {
            GlobalVariables.getMessageMap().putError(AdminRegConstants.STUDENT_INFO_SECTION_STUDENT_ID, AdminRegConstants.ADMIN_REG_MSG_ERROR_INVALID_STUDENT, form.getStudentId());
        }
    }

    public List<String> retrieveCourseCodes(String termCode, String courseCode) throws InvalidParameterException, MissingParameterException, PermissionDeniedException, OperationFailedException {

        if (courseCode == null || courseCode.isEmpty()) {
            return new ArrayList<String>();   // if nothing passed in, return empty list
        }

        courseCode = courseCode.toUpperCase(); // force toUpper
        return this.retrieveCourseCodesFromCache(termCode, courseCode);
    }

    public String retrieveCourseTitle(RegistrationCourse course, String termCode) throws MissingParameterException, InvalidParameterException, OperationFailedException, PermissionDeniedException {
        MultiKey cacheKey = new MultiKey(termCode, course.getCode());
        Element cachedResult = AdminRegistrationUtil.getCacheManager().getCache(CACHE_NAME).get(cacheKey);

        if(cachedResult!=null) {
            course.setTitle(((CourseOfferingInfo)cachedResult.getValue()).getCourseOfferingTitle());
        } else {
            course.setTitle(StringUtils.EMPTY);
        }
        return course.getTitle();
    }

    /**
     * The premise of this is rather simple. Return a distinct list of course code. At a minimum there needs to
     * be one character. It then does a char% search. so E% will return all ENGL or any E* codes.
     * <p/>
     * This implementation is a little special. It's both cached and recursive.
     * <p/>
     * Because this is a structured search and course codes don't update often we can cache this pretty heavily and make
     * some assumptions that allow us to make this very efficient.
     * <p/>
     * So a user wants to type and see the type ahead results very quickly. The server wants as few db calls as possible.
     * The "bad" way to do this is to search on Every character entered. If we cache the searches then we'll get much
     * better performance. But we can go one step further because ths is a structured search. The first letter E in
     * ENGL will return EVERY course that starts with an E. So when you search for EN... why would you call the DB if
     * you have already called a search for E. So this uses recursion to build the searches. So, in the average case
     * you will only have to call a db search Once for Every first letter of the course codes.
     *
     * @return List of distinct course codes or an empty list
     * @throws org.kuali.student.r2.common.exceptions.InvalidParameterException
     * @throws org.kuali.student.r2.common.exceptions.MissingParameterException
     * @throws org.kuali.student.r2.common.exceptions.PermissionDeniedException
     * @throws org.kuali.student.r2.common.exceptions.OperationFailedException
     */
    public List<String> retrieveCourseCodesFromCache(String termCode, String courseCode) throws InvalidParameterException, MissingParameterException, PermissionDeniedException, OperationFailedException {

        List<String> results = new ArrayList<String>();
        MultiKey cacheKey = new MultiKey("suggest", termCode, courseCode);
        Element cachedResult = AdminRegistrationUtil.getCacheManager().getCache(CACHE_NAME).get(cacheKey);

        // only one character. This is the base search.
        if (cachedResult == null) {
            if (courseCode.length() == 1) {
                List<String> result = searchCourseOfferingsByCodeAndTerm(termCode, courseCode);
                AdminRegistrationUtil.getCacheManager().getCache(CACHE_NAME).put(new Element(cacheKey, result));
                return result;
            }

            // This is where the recursion happens. If you entered CHEM and it didn't find anything it will
            // recurse and search for CHE -> CH -> C (C is the base). Each time building up the cache.
            // This for loop is the worst part of this method. I'd love to use some logic to remove the for loop.
            for (String searchedCode : retrieveCourseCodes(termCode, courseCode.substring(0, courseCode.length() - 1))) {
                // for every course code, see if it's part of the Match.
                if (searchedCode.startsWith(courseCode)) {
                    results.add(searchedCode);
                }
            }

            AdminRegistrationUtil.getCacheManager().getCache(CACHE_NAME).put(new Element(cacheKey, results));
        } else {
            return (List<String>) cachedResult.getValue();
        }

        return results;
    }

    /**
     * Does a search Query for course codes used for auto suggest
     *
     * @param courseCode the starting characters of a course code
     * @return a list of CourseCodeSuggestResults containing matching course codes
     */
    private List<String> searchCourseOfferingsByCodeAndTerm(String termCode, String courseCode) throws InvalidParameterException, MissingParameterException, PermissionDeniedException, OperationFailedException {

        ContextInfo context = ContextUtils.createDefaultContextInfo();
        TermInfo term = this.getTermByCode(termCode);

        QueryByCriteria.Builder qbcBuilder = QueryByCriteria.Builder.create();
        qbcBuilder.setPredicates(PredicateFactory.and(
                PredicateFactory.like("courseOfferingCode", courseCode + "*"),
                PredicateFactory.equalIgnoreCase("atpId", term.getId())));
        QueryByCriteria criteria = qbcBuilder.build();

        List<String> resultList = new ArrayList<String>();
        List<CourseOfferingInfo> results = AdminRegistrationUtil.getCourseOfferingService().searchForCourseOfferings(criteria, context);
        for(CourseOfferingInfo result : results) {
            MultiKey cacheKey = new MultiKey(termCode, result.getCourseOfferingCode());
            AdminRegistrationUtil.getCacheManager().getCache(CACHE_NAME).put(new Element(cacheKey, result));
            resultList.add(result.getCourseOfferingCode());
        }
        return resultList;
    }

}
