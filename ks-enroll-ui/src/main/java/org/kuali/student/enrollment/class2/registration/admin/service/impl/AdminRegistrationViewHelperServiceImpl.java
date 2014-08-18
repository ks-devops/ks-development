package org.kuali.student.enrollment.class2.registration.admin.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.identity.principal.Principal;
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
import org.kuali.student.enrollment.class2.registration.admin.util.AdminRegClientCache;
import org.kuali.student.enrollment.class2.registration.admin.util.AdminRegConstants;
import org.kuali.student.enrollment.class2.registration.admin.util.AdminRegResourceLoader;
import org.kuali.student.enrollment.class2.registration.admin.util.AdminRegistrationUtil;
import org.kuali.student.enrollment.courseoffering.dto.ActivityOfferingInfo;
import org.kuali.student.enrollment.courseoffering.dto.CourseOfferingInfo;
import org.kuali.student.enrollment.courseoffering.dto.OfferingInstructorInfo;
import org.kuali.student.enrollment.courseoffering.dto.RegistrationGroupInfo;
import org.kuali.student.enrollment.courseofferingset.dto.SocInfo;
import org.kuali.student.enrollment.courseregistration.dto.ActivityRegistrationInfo;
import org.kuali.student.enrollment.courseregistration.dto.CourseRegistrationInfo;
import org.kuali.student.enrollment.courseregistration.dto.RegistrationRequestInfo;
import org.kuali.student.enrollment.registration.client.service.impl.util.CourseRegistrationAndScheduleOfClassesUtil;
import org.kuali.student.enrollment.registration.client.service.impl.util.RegistrationValidationResultsUtil;
import org.kuali.student.r2.common.dto.AttributeInfo;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.ValidationResultInfo;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.infc.ValidationResult;
import org.kuali.student.r2.common.util.TimeOfDayHelper;
import org.kuali.student.r2.common.util.constants.CourseRegistrationServiceConstants;
import org.kuali.student.r2.common.util.constants.LprServiceConstants;
import org.kuali.student.r2.common.util.constants.LuiServiceConstants;
import org.kuali.student.r2.core.acal.dto.TermInfo;
import org.kuali.student.r2.core.room.dto.BuildingInfo;
import org.kuali.student.r2.core.room.dto.RoomInfo;
import org.kuali.student.r2.core.scheduling.dto.ScheduleComponentInfo;
import org.kuali.student.r2.core.scheduling.dto.ScheduleInfo;
import org.kuali.student.r2.core.scheduling.dto.TimeSlotInfo;
import org.kuali.student.r2.core.scheduling.util.SchedulingServiceUtil;
import org.kuali.student.r2.lum.lrc.dto.ResultValueInfo;
import org.kuali.student.r2.lum.lrc.dto.ResultValuesGroupInfo;
import org.kuali.student.r2.lum.util.constants.LrcServiceConstants;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Blue Team (SA)
 * Date: 17 July 2014
 * <p/>
 * Implementation of the AdminRegistrationViewHelperService that contains helper methods that support the Admin Reg Controller.
 */
public class AdminRegistrationViewHelperServiceImpl extends KSViewHelperServiceImpl implements AdminRegistrationViewHelperService {

    @Override
    public PersonInfo getStudentById(String studentId) {

        if (StringUtils.isBlank(studentId)) {
            GlobalVariables.getMessageMap().putError(AdminRegConstants.PERSON_ID, AdminRegConstants.ADMIN_REG_MSG_ERROR_STUDENT_REQUIRED);
            return null;
        }

        try {
            PersonInfo personInfo = AdminRegResourceLoader.getPersonService().getPerson(studentId.toUpperCase(), createContextInfo());

            Boolean validStudent = false;
            List<PersonAffiliationInfo> personAffiliationInfos = AdminRegResourceLoader.getPersonService().getPersonAffiliationsByPerson(personInfo.getId(), createContextInfo());
            for (PersonAffiliationInfo personAffiliationInfo : personAffiliationInfos) {
                if (personAffiliationInfo.getTypeKey().equals(PersonServiceConstants.PERSON_AFFILIATION_TYPE_PREFIX + AdminRegConstants.STUDENT_AFFILIATION_TYPE_CODE.toLowerCase())) {
                    validStudent = true;
                }
            }
            if (!validStudent) {
                GlobalVariables.getMessageMap().putError(AdminRegConstants.PERSON_ID, AdminRegConstants.ADMIN_REG_MSG_ERROR_NO_STUDENT_AFFILIATION, studentId);
                return null;
            }
            return personInfo;
        } catch (DoesNotExistException dne) {
            GlobalVariables.getMessageMap().putError(AdminRegConstants.PERSON_ID, AdminRegConstants.ADMIN_REG_MSG_ERROR_INVALID_STUDENT, studentId);
        } catch (Exception e) {
            throw convertServiceExceptionsToUI(e);
        }

        return null;
    }

    @Override
    public TermInfo getTermByCode(String termCode) {

        if (StringUtils.isBlank(termCode)) {
            GlobalVariables.getMessageMap().putError(AdminRegConstants.TERM_CODE, AdminRegConstants.ADMIN_REG_MSG_ERROR_INVALID_TERM);
            return null;
        }

        try {
            TermInfo term = AdminRegClientCache.getTermByCode(termCode);
            if (term == null) {
                GlobalVariables.getMessageMap().putError(AdminRegConstants.TERM_CODE, AdminRegConstants.ADMIN_REG_MSG_ERROR_INVALID_TERM);
            } else {
                // check if SOC state is "published"
                ContextInfo contextInfo = ContextUtils.createDefaultContextInfo();

                SocInfo soc = AdminRegistrationUtil.getMainSocForTermId(term.getId(), contextInfo);
                if (soc != null) {
                    if (!soc.getStateKey().equals(AdminRegConstants.PUBLISHED_SOC_STATE_KEY)) {
                        GlobalVariables.getMessageMap().putError(AdminRegConstants.TERM_CODE, AdminRegConstants.ADMIN_REG_MSG_ERROR_TERM_SOC_NOT_PUBLISHED);
                        return null;
                    }
                } else {
                    GlobalVariables.getMessageMap().putError(AdminRegConstants.TERM_CODE, AdminRegConstants.ADMIN_REG_MSG_ERROR_TERM_SOC_NOT_EXISTS);
                    return null;
                }

            }
            return term;
        } catch (Exception e) {
            throw convertServiceExceptionsToUI(e);
        }
    }

    @Override
    public List<String> checkStudentEligibilityForTermLocal(String studentId, String termId) {
        ContextInfo contextInfo = ContextUtils.createDefaultContextInfo();
        List<String> reasons = new ArrayList<String>();

        try {

            List<ValidationResultInfo> validationResults = AdminRegResourceLoader.getCourseRegistrationService()
                    .checkStudentEligibilityForTerm(studentId, termId, contextInfo);

            // Filter out anything that isn't an error
            for (ValidationResultInfo vr : validationResults) {
                if (ValidationResult.ErrorLevel.ERROR.equals(vr.getLevel())) {

                    Map<String, Object> validationMap = RegistrationValidationResultsUtil.unmarshallResult(vr.getMessage());
                    if (validationMap.containsKey(AdminRegConstants.ADMIN_REG_VALIDATION_MSG)) {
                        reasons.add((String) validationMap.get(AdminRegConstants.ADMIN_REG_VALIDATION_MSG));
                    }
                }
            }

        } catch (Exception e) {
            throw convertServiceExceptionsToUI(e);
        }
        return reasons;
    }

    @Override
    public List<RegistrationCourse> getCourseRegForStudentAndTerm(String studentId, String termCode) {

        List<RegistrationCourse> registeredCourses = new ArrayList<RegistrationCourse>();

        try {
            //Uses the StudentId and Term entered on the screen to retrieve existing CourseRegistrations for that specific student
            List<CourseRegistrationInfo> courseRegistrationInfos = AdminRegResourceLoader.getCourseRegistrationService().getCourseRegistrationsByStudentAndTerm(
                    studentId, termCode, createContextInfo());

            //TODO: KSENROLL-13558 :work around for incorrect Data
            List<Principal> principals = AdminRegResourceLoader.getIdentityService().getPrincipalsByEntityId(studentId.toUpperCase());
            for (Principal principal : principals) {
                courseRegistrationInfos.addAll(AdminRegResourceLoader.getCourseRegistrationService().getCourseRegistrationsByStudentAndTerm(principal.getPrincipalId(), termCode, createContextInfo()));

            }

            for (CourseRegistrationInfo courseRegInfo : courseRegistrationInfos) {
                RegistrationCourse registeredCourse = createRegistrationCourse(courseRegInfo);
                //retrieves ActivityRegistrations for the existing RegisteredCourse
                List<ActivityRegistrationInfo> activityRegistrations = AdminRegResourceLoader.getCourseRegistrationService().getActivityRegistrationsForCourseRegistration(
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
            List<CourseRegistrationInfo> courseWaitListInfos = AdminRegResourceLoader.getCourseWaitlistService().getCourseWaitListRegistrationsByStudentAndTerm(
                    studentId, termCode, createContextInfo());

            //TODO: KSENROLL-13558 :work around for incorrect Data
            List<Principal> principals = AdminRegResourceLoader.getIdentityService().getPrincipalsByEntityId(studentId.toUpperCase());
            for (Principal principal : principals) {
                courseWaitListInfos.addAll(AdminRegResourceLoader.getCourseWaitlistService().getCourseWaitListRegistrationsByStudentAndTerm(
                        principal.getPrincipalId(), termCode, createContextInfo()));
            }

            for (CourseRegistrationInfo courseWaitListInfo : courseWaitListInfos) {
                RegistrationCourse waitListCourse = createRegistrationCourse(courseWaitListInfo);

                //Getting the list of existing activities for waitlisted courses and adding it
                List<ActivityRegistrationInfo> activityRegistrations = AdminRegResourceLoader.getCourseWaitlistService().getActivityWaitListRegistrationsForCourseRegistration(
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
     * Creates Registration Course info based on CourseRegistrationInfo.
     *
     * @param courseRegistrationInfo
     * @return registrationCourse
     * @throws DoesNotExistException
     * @throws InvalidParameterException
     * @throws MissingParameterException
     * @throws OperationFailedException
     * @throws PermissionDeniedException
     * @throws ParseException
     */
    private RegistrationCourse createRegistrationCourse(CourseRegistrationInfo courseRegistrationInfo)
            throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ParseException {

        RegistrationCourse registrationCourse = new RegistrationCourse();
        registrationCourse.setCourseRegistrationId(courseRegistrationInfo.getId());
        registrationCourse.setCredits(courseRegistrationInfo.getCredits().toString());
        registrationCourse.setTransactionalDate(courseRegistrationInfo.getMeta().getCreateTime());
        registrationCourse.setEffectiveDate(courseRegistrationInfo.getEffectiveDate());
        registrationCourse.setGradingOptionId(courseRegistrationInfo.getGradingOptionId());

        CourseOfferingInfo coInfo = AdminRegResourceLoader.getCourseOfferingService().getCourseOffering(courseRegistrationInfo.getCourseOfferingId(), createContextInfo());
        registrationCourse.setCode(coInfo.getCourseOfferingCode());
        registrationCourse.setTitle(coInfo.getCourseOfferingTitle());
        registrationCourse.setGradingOptions(coInfo.getStudentRegistrationGradingOptions());

        RegistrationGroupInfo registrationGroup = AdminRegResourceLoader.getCourseOfferingService().getRegistrationGroup(
                courseRegistrationInfo.getRegistrationGroupId(), createContextInfo());
        registrationCourse.setSection(registrationGroup.getRegistrationCode());
        registrationCourse.setRegGroupId(registrationGroup.getId());

        return registrationCourse;
    }

    /**
     * Create Registration Activities based on the given list of activity registrations.
     *
     * @param activityRegistrations
     * @return List<RegistrationActivity> registrationActivities
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
            ActivityOfferingInfo aoInfo = AdminRegResourceLoader.getCourseOfferingService().getActivityOffering(activityRegInfo.getActivityOfferingId(), createContextInfo());
            registrationActivities.add(createRegistrationActivity(aoInfo));
        }
        return registrationActivities;
    }

    @Override
    public void validateForRegistration(AdminRegistrationForm form) {

        for (int i = 0; i < form.getPendingCourses().size(); i++) {
            RegistrationCourse course = form.getPendingCourses().get(i);

            try {
                CourseOfferingInfo courseOffering = AdminRegClientCache.getCourseOfferingByCodeAndTerm(form.getTerm().getId(), course.getCode());
                if (courseOffering == null) {
                    GlobalVariables.getMessageMap().putError(AdminRegConstants.PENDING_COURSES + "[" + i + "]." + AdminRegConstants.CODE,
                            AdminRegConstants.ADMIN_REG_MSG_ERROR_COURSE_CODE_TERM_INVALID);
                    continue;
                }

                course.setGradingOptionId(courseOffering.getGradingOptionId());
                course.setGradingOptions(courseOffering.getStudentRegistrationGradingOptions());
                course.setCreditOptions(this.getCourseOfferingCreditOptionValues(courseOffering.getCreditOptionId()));
                if (course.getCreditOptions().size() == 1) {
                    course.setCreditType(LrcServiceConstants.RESULT_VALUES_GROUP_TYPE_KEY_FIXED);
                    course.setCredits(KSCollectionUtils.getRequiredZeroElement(course.getCreditOptions()));
                } else {
                    course.setCreditType(LrcServiceConstants.RESULT_VALUES_GROUP_TYPE_KEY_MULTIPLE);
                    course.setCredits(null);
                }

                //Add error message when no registration group was found for given section.
                RegistrationGroupInfo regGroup = AdminRegClientCache.getRegistrationGroupForCourseOfferingIdAndSection(courseOffering.getId(), course.getSection());
                if (regGroup == null) {
                    GlobalVariables.getMessageMap().putError(AdminRegConstants.PENDING_COURSES + "[" + i + "]." + AdminRegConstants.SECTION,
                            AdminRegConstants.ADMIN_REG_MSG_ERROR_SECTION_CODE_INVALID);
                    continue;
                }
                course.setRegGroupId(regGroup.getId());
                if (regGroup.getStateKey().equals(LuiServiceConstants.REGISTRATION_GROUP_CANCELED_STATE_KEY)) {
                    GlobalVariables.getMessageMap().putErrorForSectionId(AdminRegConstants.PENDING_COURSES + "[" + i + "]." + AdminRegConstants.SECTION,
                            AdminRegConstants.ADMIN_REG_MSG_ERROR_REGISTRATION_GROUP_CANCELED, course.getCode(), course.getSection());
                }

            } catch (Exception e) {
                throw convertServiceExceptionsToUI(e);
            }
        }
    }

    @Override
    public void validateForSubmission(AdminRegistrationForm form) {

        for (RegistrationCourse regCourse : form.getCoursesInProcess()) {
            if (regCourse.getCredits() == null || regCourse.getCredits().isEmpty()) {
                String message = AdminRegistrationUtil.getMessageForKey(AdminRegConstants.ADMIN_REG_MSG_ERROR_CREDITS_REQUIRED, regCourse.getCode(), regCourse.getSection());
                form.getConfirmationIssues().add(message);
            }
            if (regCourse.getEffectiveDate() == null) {
                form.getConfirmationIssues().add(AdminRegistrationUtil.getMessageForKey(AdminRegConstants.ADMIN_REG_MSG_ERROR_EFFECTIVE_DATE_REQUIRED));
            }

        }
    }

    @Override
    public void validateCourseEdit(AdminRegistrationForm form) {

        for (RegistrationCourse editCourse : form.getCoursesInEdit()) {

            if (editCourse.getCredits() == null || editCourse.getCredits().isEmpty()) {
                form.getEditingIssues().add(AdminRegistrationUtil.getMessageForKey(AdminRegConstants.ADMIN_REG_MSG_ERROR_CREDITS_REQUIRED, editCourse.getCode(), editCourse.getSection()));
            }

            if (editCourse.getEffectiveDate() == null) {
                form.getEditingIssues().add(AdminRegistrationUtil.getMessageForKey(AdminRegConstants.ADMIN_REG_MSG_ERROR_EFFECTIVE_DATE_REQUIRED));
            }

            if (editCourse.getGradingOptionId() == null || editCourse.getGradingOptionId().isEmpty()) {
                form.getEditingIssues().add(AdminRegistrationUtil.getMessageForKey(AdminRegConstants.ADMIN_REG_MSG_ERROR_REG_OPTIONS_REQUIRED));
            }
        }
    }

    @Override
    public List<RegistrationActivity> getRegistrationActivitiesForRegistrationCourse(RegistrationCourse registrationCourse, String termCode) {

        List<RegistrationActivity> registrationActivities = new ArrayList<RegistrationActivity>();
        try {
            RegistrationGroupInfo registrationGroup = AdminRegResourceLoader.getCourseOfferingService().getRegistrationGroup(
                    registrationCourse.getRegGroupId(), ContextUtils.createDefaultContextInfo());
            List<ActivityOfferingInfo> activityOfferings = AdminRegResourceLoader.getCourseOfferingService().getActivityOfferingsByIds(
                    registrationGroup.getActivityOfferingIds(), createContextInfo());
            for (ActivityOfferingInfo activityOffering : activityOfferings) {
                registrationActivities.add(createRegistrationActivity(activityOffering));
            }
        } catch (Exception e) {
            throw convertServiceExceptionsToUI(e);
        }

        return registrationActivities;
    }

    @Override
    public List<String> getCourseOfferingCreditOptionValues(String creditOptionId) {

        int firstValue = 0;
        List<String> creditOptions = new java.util.ArrayList<String>();

        //Lookup the selected credit option and set from persisted values
        if (!creditOptionId.isEmpty()) {

            try {
                //Lookup the resultValueGroup Information
                ResultValuesGroupInfo resultValuesGroupInfo = CourseRegistrationAndScheduleOfClassesUtil.getLrcService().getResultValuesGroup(creditOptionId, createContextInfo());
                String typeKey = resultValuesGroupInfo.getTypeKey();

                //Get the actual values
                List<ResultValueInfo> resultValueInfos = CourseRegistrationAndScheduleOfClassesUtil.getLrcService().getResultValuesByKeys(
                        resultValuesGroupInfo.getResultValueKeys(), createContextInfo());

                if (!resultValueInfos.isEmpty()) {
                    if (typeKey.equals(LrcServiceConstants.RESULT_VALUES_GROUP_TYPE_KEY_FIXED)) {
                        creditOptions.add(resultValueInfos.get(firstValue).getValue()); // fixed credits
                    } else if (typeKey.equals(LrcServiceConstants.RESULT_VALUES_GROUP_TYPE_KEY_MULTIPLE) ||
                            typeKey.equals(LrcServiceConstants.RESULT_VALUES_GROUP_TYPE_KEY_RANGE)) {  // multiple or range
                        for (ResultValueInfo resultValueInfo : resultValueInfos) {
                            creditOptions.add(resultValueInfo.getValue());
                        }
                    }
                }
            } catch (Exception e) {
                throw convertServiceExceptionsToUI(e);
            }
        }

        return creditOptions;
    }

    /**
     * Create a single Registration Activity based on a activity offering.
     *
     * @param activityOffering
     * @return registrationActivity
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

        List<ScheduleInfo> scheduleInfos = AdminRegResourceLoader.getSchedulingService().getSchedulesByIds(activityOffering.getScheduleIds(), createContextInfo());

        StringBuilder timeSchedule = new StringBuilder();
        StringBuilder roomBuildInfo = new StringBuilder();

        for (ScheduleInfo scheduleInfo : scheduleInfos) {
            /**
             * Until we implement external scheduler, there is going to be only one Schedule component for every scheduleinfo
             * and the UI doesn't allow us to add multiple components to a schedule request.
             */
            ScheduleComponentInfo componentInfo = KSCollectionUtils.getOptionalZeroElement(scheduleInfo.getScheduleComponents());

            if (!componentInfo.getIsTBA()) {
                List<TimeSlotInfo> timeSlotInfos = AdminRegResourceLoader.getSchedulingService().getTimeSlotsByIds(componentInfo.getTimeSlotIds(), createContextInfo());
                // Assume only zero or one (should never be more than 1 until we support partial colo)
                TimeSlotInfo timeSlotInfo = KSCollectionUtils.getOptionalZeroElement(timeSlotInfos);

                regActivity.setDays(SchedulingServiceUtil.weekdaysList2WeekdaysString(timeSlotInfo.getWeekdays()));

                timeSchedule.append(TimeOfDayHelper.makeFormattedTimeForAOSchedules(timeSlotInfo.getStartTime()));
                timeSchedule.append(" - ");
                timeSchedule.append(TimeOfDayHelper.makeFormattedTimeForAOSchedules(timeSlotInfo.getEndTime()));
            } else {
                regActivity.setDays(StringUtils.EMPTY);
            }


            try {
                //Check if the room ID is null, if not get the buildingInfo from the room
                if (componentInfo.getRoomId() != null) {
                    RoomInfo room = AdminRegResourceLoader.getRoomService().getRoom(componentInfo.getRoomId(), createContextInfo());
                    //retrieve the buildingInfo from the Room.
                    BuildingInfo buildingInfo = AdminRegResourceLoader.getRoomService().getBuilding(room.getBuildingId(), createContextInfo());
                    roomBuildInfo.append(buildingInfo.getBuildingCode());
                    roomBuildInfo.append(" ");
                    roomBuildInfo.append(room.getRoomCode());
                }

            } catch (Exception e) {
                throw convertServiceExceptionsToUI(e);
            }
        }

        regActivity.setDateTime(timeSchedule.toString());
        regActivity.setRoom(roomBuildInfo.toString());
        return regActivity;
    }

    @Override
    public String submitCourses(String studentId, String termId, List<RegistrationCourse> registrationCourses, String typeKey) {

        //Create the request object
        RegistrationRequestInfo regRequest = AdminRegistrationUtil.createRegistrationRequest(studentId, termId);
        for (RegistrationCourse registrationCourse : registrationCourses) {
            regRequest.getRegistrationRequestItems().add(AdminRegistrationUtil.createRegistrationRequestItem(studentId, typeKey, registrationCourse));
        }

        String regRequestId = submitRegistrationRequest(regRequest);
        for (RegistrationCourse registrationCourse : registrationCourses) {
            registrationCourse.setCurrentRegRequestId(regRequestId);
        }
        return regRequestId;
    }

    @Override
    public String submitCourse(String studentId, String termId, RegistrationCourse registrationCourse, String typeKey) {
        String regRequestId = submitRegistrationRequest(AdminRegistrationUtil.buildRegistrationRequest(studentId, termId, registrationCourse, typeKey));
        registrationCourse.setCurrentRegRequestId(regRequestId);

        return regRequestId;
    }

    @Override
    public String resubmitCourse(String studentId, String termId, RegistrationCourse registrationCourse, String typeKey) {

        //Build the request object
        RegistrationRequestInfo regRequest = AdminRegistrationUtil.buildRegistrationRequest(studentId, termId, registrationCourse, typeKey);

        // Add dynamic attribute with override flag.
        AttributeInfo attributeInfo = new AttributeInfo(CourseRegistrationServiceConstants.ELIGIBILITY_OVERRIDE_TYPE_KEY_ATTR,
                String.valueOf(Boolean.TRUE));
        regRequest.getAttributes().add(attributeInfo);

        String regRequestId = submitRegistrationRequest(regRequest);
        registrationCourse.setCurrentRegRequestId(regRequestId);

        return regRequestId;
    }

    /**
     * Submits a registration request.
     *
     * @param regRequest
     * @return registration request id.
     */
    private String submitRegistrationRequest(RegistrationRequestInfo regRequest) {

        try {
            // persist the request object in the service
            String regRequestId = AdminRegResourceLoader.getCourseRegistrationService().createRegistrationRequest(
                    LprServiceConstants.LPRTRANS_REGISTRATION_TYPE_KEY, regRequest, createContextInfo()).getId();

            // submit the request to the registration engine.
            return CourseRegistrationAndScheduleOfClassesUtil.getCourseRegistrationService().submitRegistrationRequest(
                    regRequestId, createContextInfo()).getId();

        } catch (Exception e) {
            convertServiceExceptionsToUI(e);
        }

        return null;
    }

    @Override
    public RegistrationRequestInfo getRegistrationRequest(String regRequestId) {

        try {
            return AdminRegResourceLoader.getCourseRegistrationService().getRegistrationRequest(regRequestId, createContextInfo());
        } catch (Exception e) {
            convertServiceExceptionsToUI(e);
        }

        return null;
    }

    /**
     * This method is used for the course code suggest field on the input section on the client.
     *
     * @param termCode
     * @param courseCode
     * @return List<String> retrieveCourseCodes
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

        TermInfo term = getTermByCode(termCode);
        if (term == null) {
            return new ArrayList<String>(); // cannot do search on an invalid term code
        }

        return AdminRegClientCache.retrieveCourseCodes(term.getId(), courseCode.toUpperCase());
    }

    /**
     * This method is called on an ajax call from the client when a course code is entered in the input section.
     *
     * @param course
     * @param termId
     * @return String retrieveCourseTitle
     * @throws MissingParameterException
     * @throws InvalidParameterException
     * @throws OperationFailedException
     * @throws PermissionDeniedException
     */
    public String retrieveCourseTitle(RegistrationCourse course, String termId)
            throws MissingParameterException, InvalidParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {

        String courseCode = course.getCode();
        if (courseCode == null || courseCode.isEmpty()) {
            course.setTitle(StringUtils.EMPTY);
        } else {

            CourseOfferingInfo courseOffering = AdminRegClientCache.getCourseOfferingByCodeAndTerm(termId, courseCode);
            if (courseOffering != null) {
                course.setTitle(courseOffering.getCourseOfferingTitle());
            } else {
                course.setTitle(AdminRegistrationUtil.getMessageForKey(AdminRegConstants.ADMIN_REG_MSG_INFO_COURSE_TITLE_NOT_FOUND));
            }
        }

        return course.getTitle();
    }

    /**
     * This method is called on an ajax call from the client when a course code is entered in the input section.
     *
     * @param course
     * @param termId
     * @return String retrieveCourseCredits
     * @throws MissingParameterException
     * @throws InvalidParameterException
     * @throws OperationFailedException
     * @throws PermissionDeniedException
     */
    public String retrieveCourseCredits(RegistrationCourse course, String termId)
            throws MissingParameterException, InvalidParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {

        String courseCode = course.getCode();
        if (courseCode == null || courseCode.isEmpty()) {
            course.setCredits(StringUtils.EMPTY);
        } else {

            CourseOfferingInfo courseOffering = AdminRegClientCache.getCourseOfferingByCodeAndTerm(termId, courseCode);
            if (courseOffering != null) {
                course.setCredits(courseOffering.getCreditCnt());
            } else {
                course.setCredits(StringUtils.EMPTY);
            }
        }

        return course.getCredits();
    }

}