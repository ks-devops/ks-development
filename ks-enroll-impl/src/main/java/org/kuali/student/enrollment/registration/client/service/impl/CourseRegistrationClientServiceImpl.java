package org.kuali.student.enrollment.registration.client.service.impl;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.student.ap.academicplan.constants.AcademicPlanServiceConstants;
import org.kuali.student.ap.academicplan.dto.LearningPlanInfo;
import org.kuali.student.ap.academicplan.dto.PlanItemInfo;
import org.kuali.student.ap.academicplan.service.AcademicPlanService;
import org.kuali.student.common.collection.KSCollectionUtils;
import org.kuali.student.common.util.security.ContextUtils;
import org.kuali.student.enrollment.courseoffering.dto.CourseOfferingInfo;
import org.kuali.student.enrollment.courseregistration.dto.RegistrationRequestInfo;
import org.kuali.student.enrollment.courseregistration.dto.RegistrationRequestItemInfo;
import org.kuali.student.enrollment.lpr.dto.LprInfo;
import org.kuali.student.enrollment.lpr.dto.LprTransactionInfo;
import org.kuali.student.enrollment.lpr.dto.LprTransactionItemInfo;
import org.kuali.student.enrollment.lpr.service.LprService;
import org.kuali.student.enrollment.registration.client.service.CourseRegistrationClientService;
import org.kuali.student.enrollment.registration.client.service.ScheduleOfClassesService;
import org.kuali.student.enrollment.registration.client.service.ScheduleOfClassesServiceConstants;
import org.kuali.student.enrollment.registration.client.service.dto.ActivityOfferingScheduleComponentResult;
import org.kuali.student.enrollment.registration.client.service.dto.CourseSearchResult;
import org.kuali.student.enrollment.registration.client.service.dto.InstructorSearchResult;
import org.kuali.student.enrollment.registration.client.service.dto.LearningPlanItemResult;
import org.kuali.student.enrollment.registration.client.service.dto.PersonScheduleResult;
import org.kuali.student.enrollment.registration.client.service.dto.RegGroupSearchResult;
import org.kuali.student.enrollment.registration.client.service.dto.RegistrationResponseItemResult;
import org.kuali.student.enrollment.registration.client.service.dto.RegistrationResponseResult;
import org.kuali.student.enrollment.registration.client.service.dto.RegistrationValidationResult;
import org.kuali.student.enrollment.registration.client.service.dto.StudentScheduleActivityOfferingResult;
import org.kuali.student.enrollment.registration.client.service.dto.StudentScheduleCourseResult;
import org.kuali.student.enrollment.registration.client.service.dto.StudentScheduleTermResult;
import org.kuali.student.enrollment.registration.client.service.dto.TermSearchResult;
import org.kuali.student.enrollment.registration.client.service.impl.util.CourseRegistrationAndScheduleOfClassesUtil;
import org.kuali.student.enrollment.registration.client.service.impl.util.SearchResultHelper;
import org.kuali.student.enrollment.registration.search.service.impl.CourseRegistrationSearchServiceImpl;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.ValidationResultInfo;
import org.kuali.student.r2.common.exceptions.AlreadyExistsException;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.exceptions.ReadOnlyException;
import org.kuali.student.r2.common.util.constants.CourseOfferingServiceConstants;
import org.kuali.student.r2.common.util.constants.LprServiceConstants;
import org.kuali.student.r2.common.util.constants.LuServiceConstants;
import org.kuali.student.r2.common.util.constants.LuiServiceConstants;
import org.kuali.student.r2.core.search.dto.SearchRequestInfo;
import org.kuali.student.r2.core.search.dto.SearchResultInfo;
import org.kuali.student.r2.core.search.util.SearchRequestHelper;
import org.kuali.student.r2.lum.clu.service.CluService;
import org.kuali.student.r2.lum.lrc.dto.ResultValueInfo;
import org.kuali.student.r2.lum.lrc.dto.ResultValuesGroupInfo;
import org.kuali.student.r2.lum.util.constants.CluServiceConstants;
import org.kuali.student.r2.lum.util.constants.LrcServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseRegistrationClientServiceImpl implements CourseRegistrationClientService {

    public static final Logger LOGGER = LoggerFactory.getLogger(CourseRegistrationClientServiceImpl.class);

    private static final String COURSE_CODE_NOT_FOUND_MESSAGE_KEY="kuali.cr.learningplan.message.course.code.not.found";
    private static final String LEARNING_PLAN_NOT_CONFIGURED_MESSAGE_KEY ="kuali.cr.learningplan.message.learningplan.not.configured";

    private LprService lprService;
    private ScheduleOfClassesService scheduleOfClassesService;
    private AcademicPlanService academicPlanService; // this does not live in our module so do not put in our context. use GRL to access.
    private CluService cluService;    // this does not live in our module so do not put in our context. use GRL to access.

    protected static Comparator<LprTransactionItemInfo> LPR_TRANS_ITEM_CREATE_DATE = new Comparator<LprTransactionItemInfo>() {

        @Override
        public int compare(LprTransactionItemInfo o1, LprTransactionItemInfo o2) {
            int ret = 0;
            try {
                ret = o1.getMeta().getCreateTime().compareTo(o2.getMeta().getCreateTime());
            } catch (NullPointerException ex) {
                LOGGER.error("Error comparing reg request meta data", ex);
            }
            return ret;
        }
    };

    @Override
    public Response createAndSubmitAddCourseRegistrationRequest(String termCode, String courseCode, String regGroupCode, String regGroupId, String credits, String gradingOptionId, boolean okToWaitlist) {
        Response.ResponseBuilder response;

        try {
            response = Response.ok(registerForRegistrationGroupLocal(termCode, courseCode, regGroupCode, regGroupId, credits, gradingOptionId, okToWaitlist, ContextUtils.createDefaultContextInfo()));
        } catch (Exception e) {
            LOGGER.warn("Exception occurred", e);
            response = Response.serverError().entity(e.getMessage());
        }

        return response.build();
    }

    @Override
    public Response createAndSubmitDropCourseRegistrationRequest(String masterLprId) {
        Response.ResponseBuilder response;

        try {
            response = Response.ok(dropRegistrationGroupLocal(ContextUtils.createDefaultContextInfo(), masterLprId));
        } catch (Exception e) {
            LOGGER.warn("Exception occurred", e);
            response = Response.serverError().entity(e.getMessage());
        }

        return response.build();
    }

    @Override
    public Response clearScheduleRS(String termId, String termCode) {
        Response.ResponseBuilder response;


        try {
            termId = CourseRegistrationAndScheduleOfClassesUtil.getTermId(termId, termCode);
            PersonScheduleResult scheduleResult = getStudentScheduleAndWaitlistedCoursesByTerm(termId,termCode);

            List<StudentScheduleCourseResult> coursesToDrop = new ArrayList<>();
            if(scheduleResult != null && scheduleResult.getStudentScheduleTermResults() != null){

                for(StudentScheduleTermResult scheduleTermResult : scheduleResult.getStudentScheduleTermResults()){
                    if(scheduleTermResult.getRegisteredCourseOfferings() != null){
                        coursesToDrop.addAll(scheduleTermResult.getRegisteredCourseOfferings());
                    }
                    if(scheduleTermResult.getWaitlistCourseOfferings() != null){
                        coursesToDrop.addAll(scheduleTermResult.getWaitlistCourseOfferings());
                    }
                }
            }

            List<String> masterLprIds = new ArrayList<>(coursesToDrop.size());
            for(StudentScheduleCourseResult courseResult : coursesToDrop){
                masterLprIds.add(courseResult.getMasterLprId());
            }

            response = Response.ok(dropRegistrationGroupsLocal(ContextUtils.createDefaultContextInfo(), masterLprIds,termId));
        } catch (Exception e) {
            LOGGER.warn("Exception occurred", e);
            response = Response.serverError().entity(e.getMessage());
        }

        return response.build();
    }

    /**
     * SEARCH for STUDENT REGISTRATION INFO based on person and termCode *
     */
    @Override
    public PersonScheduleResult getStudentScheduleAndWaitlistedCoursesByTerm(String termId, String termCode) throws LoginException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {

        ContextInfo contextInfo = ContextUtils.createDefaultContextInfo();
        String userId = contextInfo.getPrincipalId();

        if (StringUtils.isEmpty(userId)) {
            userId = contextInfo.getPrincipalId();
        }

        if (StringUtils.isEmpty(userId)) {
            throw new LoginException("[CourseRegistrationClientServiceImpl::getStudentScheduleAndWaitlistedCoursesByTerm] User must be logged in to access this service");
        }

        if (StringUtils.isEmpty(termId) && StringUtils.isEmpty(termCode)) {
            termId = "";
        } else {
            termId = CourseRegistrationAndScheduleOfClassesUtil.getTermId(termId, termCode);
        }

        List<StudentScheduleTermResult> studentScheduleTermResults = getRegistrationScheduleByPersonAndTerm(userId, termId, contextInfo);

        PersonScheduleResult personScheduleResult = new PersonScheduleResult();
        personScheduleResult.setStudentScheduleTermResults(studentScheduleTermResults);
        personScheduleResult.setUserId(userId);

        return personScheduleResult;
    }

    @Override
    public Response createAndSubmitUpdateCourseRegistrationRequest(String courseCode, String regGroupId, String masterLprId, String termId, String credits, String gradingOptionId) {
        Response.ResponseBuilder response;

        try {
            response = Response.ok(updateScheduleItem(courseCode, regGroupId, null, masterLprId, termId, credits, gradingOptionId,
                    ContextUtils.createDefaultContextInfo()));
        } catch (Exception e) {
            LOGGER.warn("Exception occurred", e);
            response = Response.serverError().entity(e.getMessage());
        }

        return response.build();
    }

    @Override
    public Response createAndSubmitUpdateWaitlistRegistrationRequest(String courseCode, String regGroupId, String masterLprId, String termId, String credits, String gradingOptionId) {
        Response.ResponseBuilder response;

        try {
            response = Response.ok(updateWaitlistEntry(courseCode, regGroupId, null, masterLprId, termId, credits, gradingOptionId,
                    ContextUtils.createDefaultContextInfo()));
        } catch (Exception e) {
            LOGGER.warn("Exception occurred", e);
            response = Response.serverError().entity(e.getMessage());
        }

        return response.build();
    }

    @Override
    public Response getRegistrationStatus(@QueryParam("regReqId") String regReqId) {
        Response.ResponseBuilder response;

        try {
            response = Response.ok(getRegistrationStatusLocal(regReqId, ContextUtils.createDefaultContextInfo()));
        } catch (Exception e) {
            LOGGER.warn("Exception occurred", e);
            response = Response.serverError().entity(e.getMessage());
        }

        return response.build();
    }

    @Override
    public Response createAndSubmitDropWaitlistRegistrationRequest(@QueryParam("masterLprId") String masterLprId) {
        Response.ResponseBuilder response;

        try {
            response = Response.ok(dropFromWaitlistLocal(ContextUtils.createDefaultContextInfo(), masterLprId));
        } catch (Exception e) {
            LOGGER.warn("Exception occurred", e);
            response = Response.serverError().entity(e.getMessage());
        }

        return response.build();
    }

    private RegistrationRequestInfo dropFromWaitlistLocal(ContextInfo contextInfo, String masterLprId) throws DoesNotExistException, PermissionDeniedException, OperationFailedException, InvalidParameterException, ReadOnlyException, MissingParameterException, DataValidationErrorException, AlreadyExistsException, LoginException {

        String userId = contextInfo.getPrincipalId();

        LOGGER.debug("REGISTRATION: user[{}] masterLprId[{}]", userId, masterLprId);


        if (!StringUtils.isEmpty(userId)) {
            contextInfo.setPrincipalId(userId);
        } else if (StringUtils.isEmpty(contextInfo.getPrincipalId())) {
            throw new LoginException("[CourseRegistrationClientServiceImpl::registerForRegistrationGroupLocal] User must be logged in to access this service");
        }

        //Create the request object
        RegistrationRequestInfo regReqInfo = createRegistrationRequest(contextInfo.getPrincipalId(), null, null, masterLprId, null, null, LprServiceConstants.LPRTRANS_REGISTRATION_TYPE_KEY, LprServiceConstants.LPRTRANS_NEW_STATE_KEY, LprServiceConstants.REQ_ITEM_DROP_WAITLIST_TYPE_KEY, LprServiceConstants.LPRTRANS_ITEM_NEW_STATE_KEY, null, false);

        // persist the request object in the service
        RegistrationRequestInfo newRegReq = CourseRegistrationAndScheduleOfClassesUtil.getCourseRegistrationService().createRegistrationRequest(LprServiceConstants.LPRTRANS_REGISTRATION_TYPE_KEY, regReqInfo, contextInfo);

        // submit the request to the registration engine.
        return CourseRegistrationAndScheduleOfClassesUtil.getCourseRegistrationService().submitRegistrationRequest(newRegReq.getId(), contextInfo);
    }

    private RegistrationRequestInfo registerForRegistrationGroupLocal(String termCode, String courseCode, String regGroupCode, String regGroupId, String credits, String gradingOptionId, boolean okToWaitlist, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, DataValidationErrorException, DoesNotExistException, ReadOnlyException, AlreadyExistsException, LoginException {
        LOGGER.debug("REGISTRATION: user[{}] termCode[{}] courseCode[{}] regGroup[{}]",
                contextInfo.getPrincipalId(), termCode, courseCode, regGroupCode);

        // get the regGroup
        RegGroupSearchResult rg = CourseRegistrationAndScheduleOfClassesUtil.getRegGroup(null, termCode, courseCode, regGroupCode, regGroupId, contextInfo);

        // get the registration group, returns default (from Course Offering) credits (as creditId) and grading options (as a string of options)
        CourseOfferingInfo courseOfferingInfo = CourseRegistrationAndScheduleOfClassesUtil.getCourseOfferingIdCreditGrading(rg.getCourseOfferingId(), courseCode, rg.getTermId(), termCode);

        // verify passed credits (must be non-empty unless fixed) and grading option (can be null)
        credits = verifyRegistrationRequestCreditsGradingOption(courseOfferingInfo, credits, gradingOptionId, contextInfo);

        //Create the request object
        RegistrationRequestInfo regReqInfo = createRegistrationRequest(contextInfo.getPrincipalId(), rg.getTermId(), rg.getRegGroupId(), null, credits, gradingOptionId, LprServiceConstants.LPRTRANS_REGISTRATION_TYPE_KEY, LprServiceConstants.LPRTRANS_NEW_STATE_KEY, LprServiceConstants.REQ_ITEM_ADD_TYPE_KEY, LprServiceConstants.LPRTRANS_ITEM_NEW_STATE_KEY, courseCode, okToWaitlist);

        // persist the request object in the service
        RegistrationRequestInfo newRegReq = CourseRegistrationAndScheduleOfClassesUtil.getCourseRegistrationService().createRegistrationRequest(LprServiceConstants.LPRTRANS_REGISTRATION_TYPE_KEY, regReqInfo, contextInfo);

        // submit the request to the registration engine.
        return CourseRegistrationAndScheduleOfClassesUtil.getCourseRegistrationService().submitRegistrationRequest(newRegReq.getId(), contextInfo);
    }

    private RegistrationRequestInfo dropRegistrationGroupLocal(ContextInfo contextInfo, String masterLprId) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, DataValidationErrorException, DoesNotExistException, ReadOnlyException, AlreadyExistsException, LoginException {

        String userId = contextInfo.getPrincipalId();

        LOGGER.debug("REGISTRATION: user[{}] masterLprId[{}]", userId, masterLprId);


        if (!StringUtils.isEmpty(userId)) {
            contextInfo.setPrincipalId(userId);
        } else if (StringUtils.isEmpty(contextInfo.getPrincipalId())) {
            throw new LoginException("[CourseRegistrationClientServiceImpl::registerForRegistrationGroupLocal]User must be logged in to access this service");
        }

        LprInfo masterLpr = getLprService().getLpr(masterLprId, contextInfo);

        //Create the request object
        RegistrationRequestInfo regReqInfo = createRegistrationRequest(contextInfo.getPrincipalId(), masterLpr.getAtpId(), masterLpr.getLuiId(), masterLprId, null, null, LprServiceConstants.LPRTRANS_REGISTRATION_TYPE_KEY, LprServiceConstants.LPRTRANS_NEW_STATE_KEY, LprServiceConstants.REQ_ITEM_DROP_TYPE_KEY, LprServiceConstants.LPRTRANS_ITEM_DELETE_TYPE_KEY, null, false);

        // persist the request object in the service
        RegistrationRequestInfo newRegReq = CourseRegistrationAndScheduleOfClassesUtil.getCourseRegistrationService().createRegistrationRequest(LprServiceConstants.LPRTRANS_REGISTRATION_TYPE_KEY, regReqInfo, contextInfo);

        // submit the request to the registration engine.
        return CourseRegistrationAndScheduleOfClassesUtil.getCourseRegistrationService().submitRegistrationRequest(newRegReq.getId(), contextInfo);
    }


    private RegistrationRequestInfo dropRegistrationGroupsLocal(ContextInfo contextInfo, List<String> masterLprIds, String termId) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, DataValidationErrorException, DoesNotExistException, ReadOnlyException, AlreadyExistsException, LoginException {

        String userId = contextInfo.getPrincipalId();

        LOGGER.debug("REGISTRATION: user[{}] masterLprId[{}]", userId, masterLprIds);


        if (!StringUtils.isEmpty(userId)) {
            contextInfo.setPrincipalId(userId);
        } else if (StringUtils.isEmpty(contextInfo.getPrincipalId())) {
            throw new LoginException("[CourseRegistrationClientServiceImpl::registerForRegistrationGroupLocal]User must be logged in to access this service");
        }

        List<LprInfo> masterLprs = getLprService().getLprsByIds(masterLprIds, contextInfo);

        RegistrationRequestInfo regReqInfo = new RegistrationRequestInfo();
        regReqInfo.setRequestorId(userId);
        regReqInfo.setTermId(termId); // bad bc we have it from the load call above
        regReqInfo.setTypeKey(LprServiceConstants.LPRTRANS_REGISTRATION_TYPE_KEY);
        regReqInfo.setStateKey(LprServiceConstants.LPRTRANS_NEW_STATE_KEY);


        List<RegistrationRequestItemInfo> regReqItems = new ArrayList<>();

        for(LprInfo masterLpr : masterLprs){
            RegistrationRequestItemInfo registrationRequestItem = CourseRegistrationAndScheduleOfClassesUtil.createNewRegistrationRequestItem(userId, masterLpr.getLuiId(), masterLpr.getMasterLprId(), null, null, LprServiceConstants.REQ_ITEM_DROP_TYPE_KEY, LprServiceConstants.LPRTRANS_ITEM_DELETE_TYPE_KEY, null, false);
            regReqItems.add(registrationRequestItem);
        }

        regReqInfo.getRegistrationRequestItems().addAll(regReqItems);

        // persist the request object in the service
        RegistrationRequestInfo newRegReq = CourseRegistrationAndScheduleOfClassesUtil.getCourseRegistrationService().createRegistrationRequest(LprServiceConstants.LPRTRANS_REGISTRATION_TYPE_KEY, regReqInfo, contextInfo);

        // submit the request to the registration engine.
        return CourseRegistrationAndScheduleOfClassesUtil.getCourseRegistrationService().submitRegistrationRequest(newRegReq.getId(), contextInfo);
    }


    /**
     * This method retrieves registration schedule data for the person
     *
     * @param userId Person Id
     * @param termId Term Key
     * @return StudentScheduleCourseResults
     * @throws OperationFailedException
     * @throws InvalidParameterException
     */
    private List<StudentScheduleTermResult> getRegistrationScheduleByPersonAndTerm(String userId, String termId, ContextInfo contextInfo) throws LoginException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {
        SearchRequestInfo searchRequest = new SearchRequestInfo(CourseRegistrationSearchServiceImpl.REG_INFO_BY_PERSON_TERM_SEARCH_TYPE.getKey());
        searchRequest.addParam(CourseRegistrationSearchServiceImpl.SearchParameters.PERSON_ID, userId);
        searchRequest.addParam(CourseRegistrationSearchServiceImpl.SearchParameters.ATP_ID, termId);

        return searchRegistrationScheduleByPersonAndTerm(searchRequest, contextInfo);
    }

    /**
     * This method retrieves waitlist data for the person
     *
     * @param userId Person Id
     * @param termId Term Key
     * @return StudentScheduleCourseResults
     * @throws OperationFailedException
     * @throws InvalidParameterException
     */
    private List<StudentScheduleTermResult> getWaitlistByPersonAndTerm(String userId, String termId, ContextInfo contextInfo) throws LoginException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {
        SearchRequestInfo searchRequest = new SearchRequestInfo(CourseRegistrationSearchServiceImpl.REG_INFO_BY_PERSON_TERM_SEARCH_TYPE.getKey());

        searchRequest.addParam(CourseRegistrationSearchServiceImpl.SearchParameters.PERSON_ID, userId);
        searchRequest.addParam(CourseRegistrationSearchServiceImpl.SearchParameters.ATP_ID, termId);

        List<String> lprTypeParameters=new ArrayList<String>();
        lprTypeParameters.add(LprServiceConstants.WAITLIST_CO_LPR_TYPE_KEY);
        lprTypeParameters.add(LprServiceConstants.WAITLIST_RG_LPR_TYPE_KEY);
        lprTypeParameters.add(LprServiceConstants.WAITLIST_AO_LPR_TYPE_KEY);

        searchRequest.addParam(CourseRegistrationSearchServiceImpl.SearchParameters.LPR_TYPE, lprTypeParameters);

        return searchRegistrationScheduleByPersonAndTerm(searchRequest, contextInfo);
    }

    /**
     * This method calls the search service to retrieve registration schedule data for the person
     *
     * @param searchRequest Seach request info
     * @return StudentScheduleCourseResults
     * @throws OperationFailedException
     * @throws InvalidParameterException
     */
    private List<StudentScheduleTermResult> searchRegistrationScheduleByPersonAndTerm(SearchRequestInfo searchRequest, ContextInfo contextInfo) throws LoginException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {

        SearchRequestHelper requestHelper = new SearchRequestHelper(searchRequest);
        String userId = requestHelper.getParamAsString(CourseRegistrationSearchServiceImpl.SearchParameters.PERSON_ID);
        String termId = requestHelper.getParamAsString(CourseRegistrationSearchServiceImpl.SearchParameters.ATP_ID);

        List<StudentScheduleTermResult> studentScheduleTermResults = new ArrayList<StudentScheduleTermResult>();
        List<String> activityOfferingList = new ArrayList<String>();
        HashMap<String, StudentScheduleCourseResult> hmCourseOffering = new HashMap<String, StudentScheduleCourseResult>();
        HashMap<String, TermSearchResult> hmTermInfo = new HashMap<String, TermSearchResult>();
        HashMap<String, List<String>> hmTerm = new HashMap<String, List<String>>();

        SearchResultInfo searchResult;
        try {
            searchResult = CourseRegistrationAndScheduleOfClassesUtil.getSearchService().search(searchRequest, contextInfo);
        } catch (Exception e) {
            throw new OperationFailedException("Search of registration schedule for person " + userId + " and term " + termId + " failed: ", e);
        }

        for (SearchResultHelper.KeyValue row : SearchResultHelper.wrap(searchResult)) {
            String atpId = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.ATP_ID);
            String atpCode = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.ATP_CD);
            String atpName = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.ATP_NAME);
            String luiId = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.LUI_ID);
            String masterLprId = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.MASTER_LPR_ID);
            String personLuiType = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.PERSON_LUI_TYPE);
            String lprState = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.LPR_STATE);
            String credits = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.CREDITS);
            String gradingOptionId = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.GRADING_OPTION_ID);
            String crossList = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.CROSSLIST);
            String luiCode = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.LUI_CODE);
            String luiName = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.LUI_NAME);
            String luiDesc = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.LUI_DESC);
            String luiType = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.LUI_TYPE);
            String luiLongName = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.LUI_LONG_NAME);
            String isTBA = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.TBA_IND);
            String roomCode = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.ROOM_CODE);
            String buildingCode = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.BUILDING_CODE);
            String weekdays = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.WEEKDAYS);
            String startTimeMs = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.START_TIME_MS);
            String endTimeMs = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.END_TIME_MS);
            String resultValuesGroupKey = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.RES_VAL_GROUP_KEY);
            String aoName = (luiName != null && luiName.length() >= 3 ? luiName.substring(0, 1).toUpperCase() + luiName.substring(1).toLowerCase() : "");

            // running over the list of results returned. One CO can have multiple AOs
            if (hmCourseOffering.containsKey(masterLprId)) {
                StudentScheduleCourseResult studentScheduleCourseResult = hmCourseOffering.get(masterLprId);
                if (StringUtils.equals(personLuiType, LprServiceConstants.REGISTRANT_CO_LPR_TYPE_KEY) || StringUtils.equals(personLuiType, LprServiceConstants.WAITLIST_CO_LPR_TYPE_KEY)) {
                    if (!StringUtils.isEmpty(crossList) && !StringUtils.equals(crossList, luiCode)) {
                        studentScheduleCourseResult.setCourseCode(crossList);
                    } else {
                        studentScheduleCourseResult.setCourseCode(luiCode);
                    }
                    studentScheduleCourseResult.setDescription(luiDesc);
                    studentScheduleCourseResult.setCredits(credits);
                    studentScheduleCourseResult.setGradingOptionId(gradingOptionId);
                    studentScheduleCourseResult.setLongName(luiLongName);
                    studentScheduleCourseResult.setMasterLprId(masterLprId);
                    if (StringUtils.equals(personLuiType, LprServiceConstants.WAITLIST_CO_LPR_TYPE_KEY)) {
                        studentScheduleCourseResult.setWaitlisted(true);
                    } else {
                        studentScheduleCourseResult.setWaitlisted(false);
                    }
                    if (resultValuesGroupKey != null && resultValuesGroupKey.startsWith(LrcServiceConstants.RESULT_GROUP_KEY_KUALI_CREDITTYPE_CREDIT_BASE_OLD)) {
                        studentScheduleCourseResult.setCreditOptions(getCourseOfferingCreditOptionValues(resultValuesGroupKey, contextInfo));
                    } else {
                        if (!studentScheduleCourseResult.getGradingOptions().containsKey(resultValuesGroupKey)) {
                            String gradingOptionName = CourseRegistrationAndScheduleOfClassesUtil.translateGradingOptionKeyToName(resultValuesGroupKey);
                            if (!StringUtils.isEmpty(gradingOptionName)) {
                                studentScheduleCourseResult.getGradingOptions().put(resultValuesGroupKey, gradingOptionName);
                            }
                        }
                    }
                } else if (StringUtils.equals(personLuiType, LprServiceConstants.REGISTRANT_RG_LPR_TYPE_KEY) || StringUtils.equals(personLuiType, LprServiceConstants.WAITLIST_RG_LPR_TYPE_KEY)) {
                    studentScheduleCourseResult.setRegGroupCode(luiName);
                    studentScheduleCourseResult.setRegGroupId(luiId);
                } else if (StringUtils.equals(personLuiType, LprServiceConstants.REGISTRANT_AO_LPR_TYPE_KEY) || StringUtils.equals(personLuiType, LprServiceConstants.WAITLIST_AO_LPR_TYPE_KEY)) {
                    // Scheduling info
                    ActivityOfferingScheduleComponentResult scheduleComponent = CourseRegistrationAndScheduleOfClassesUtil.getActivityOfferingScheduleComponent(isTBA, roomCode, buildingCode,
                            weekdays, startTimeMs, endTimeMs);

                    // have to check if we already have the AO in our list, because we can have multiple schedules for the same AO
                    HashMap<String, StudentScheduleActivityOfferingResult> aoHm = new HashMap<String, StudentScheduleActivityOfferingResult>();
                    for (StudentScheduleActivityOfferingResult activityOfferingResult : studentScheduleCourseResult.getActivityOfferings()) {
                        aoHm.put(activityOfferingResult.getActivityOfferingId(), activityOfferingResult);
                    }
                    if (!aoHm.containsKey(luiId)) {
                        StudentScheduleActivityOfferingResult activityOffering = new StudentScheduleActivityOfferingResult();

                        // AO basic info
                        activityOffering.setActivityOfferingId(luiId);
                        activityOffering.setActivityOfferingTypeName(aoName);
                        activityOffering.setActivityOfferingType(luiType);  // to sort over priorities

                        // adding schedule to AO
                        List<ActivityOfferingScheduleComponentResult> scheduleComponents = new ArrayList<ActivityOfferingScheduleComponentResult>();
                        scheduleComponents.add(scheduleComponent);
                        activityOffering.setScheduleComponents(scheduleComponents);

                        // adding AO to result
                        if (studentScheduleCourseResult.getActivityOfferings().isEmpty()) {
                            List<StudentScheduleActivityOfferingResult> activityOfferings = new ArrayList<StudentScheduleActivityOfferingResult>();
                            activityOfferings.add(activityOffering);
                            studentScheduleCourseResult.setActivityOfferings(activityOfferings);
                        } else {
                            studentScheduleCourseResult.getActivityOfferings().add(activityOffering);
                        }
                    } else {
                        StudentScheduleActivityOfferingResult activityOffering = aoHm.get(luiId);
                        studentScheduleCourseResult.getActivityOfferings().remove(activityOffering);
                        activityOffering.getScheduleComponents().add(scheduleComponent);
                        studentScheduleCourseResult.getActivityOfferings().add(activityOffering);
                    }

                    // adding AOID to the list to search for instructors
                    if (!activityOfferingList.contains(luiId)) {
                        activityOfferingList.add(luiId);
                    }
                }
            } else {
                StudentScheduleCourseResult studentScheduleCourseResult = new StudentScheduleCourseResult();
                if (StringUtils.equals(personLuiType, LprServiceConstants.REGISTRANT_CO_LPR_TYPE_KEY) || StringUtils.equals(personLuiType, LprServiceConstants.WAITLIST_CO_LPR_TYPE_KEY)) {
                    if (!StringUtils.isEmpty(crossList) && !StringUtils.equals(crossList, luiCode)) {
                        studentScheduleCourseResult.setCourseCode(crossList);
                    } else {
                        studentScheduleCourseResult.setCourseCode(luiCode);
                    }
                    studentScheduleCourseResult.setDescription(luiDesc);
                    studentScheduleCourseResult.setCredits(credits);
                    studentScheduleCourseResult.setGradingOptionId(gradingOptionId);
                    studentScheduleCourseResult.setLongName(luiLongName);
                    studentScheduleCourseResult.setMasterLprId(masterLprId);
                    if (StringUtils.equals(personLuiType, LprServiceConstants.WAITLIST_CO_LPR_TYPE_KEY)) {
                        studentScheduleCourseResult.setWaitlisted(true);
                    } else {
                        studentScheduleCourseResult.setWaitlisted(false);
                    }
                    if (resultValuesGroupKey != null && resultValuesGroupKey.startsWith(LrcServiceConstants.RESULT_GROUP_KEY_KUALI_CREDITTYPE_CREDIT_BASE_OLD)) {
                        studentScheduleCourseResult.setCreditOptions(getCourseOfferingCreditOptionValues(resultValuesGroupKey, contextInfo));
                    } else {
                        studentScheduleCourseResult.setGradingOptions(new HashMap<String, String>());
                        String gradingOptionName = CourseRegistrationAndScheduleOfClassesUtil.translateGradingOptionKeyToName(resultValuesGroupKey);
                        if (!StringUtils.isEmpty(gradingOptionName)) {
                            studentScheduleCourseResult.getGradingOptions().put(resultValuesGroupKey, gradingOptionName);
                        }
                    }
                    hmCourseOffering.put(masterLprId, studentScheduleCourseResult);
                } else if (StringUtils.equals(personLuiType, LprServiceConstants.REGISTRANT_RG_LPR_TYPE_KEY) || StringUtils.equals(personLuiType, LprServiceConstants.WAITLIST_RG_LPR_TYPE_KEY)) {
                    studentScheduleCourseResult.setRegGroupCode(luiName);
                    hmCourseOffering.put(masterLprId, studentScheduleCourseResult);
                } else if (StringUtils.equals(personLuiType, LprServiceConstants.REGISTRANT_AO_LPR_TYPE_KEY) || StringUtils.equals(personLuiType, LprServiceConstants.WAITLIST_AO_LPR_TYPE_KEY)) {
                    List<StudentScheduleActivityOfferingResult> activityOfferings = new ArrayList<StudentScheduleActivityOfferingResult>();
                    StudentScheduleActivityOfferingResult activityOffering = new StudentScheduleActivityOfferingResult();
                    // AO basic info
                    activityOffering.setActivityOfferingId(luiId);
                    activityOffering.setActivityOfferingTypeName(aoName);
                    activityOffering.setActivityOfferingType(luiType);  // to sort over priorities

                    // Scheduling info
                    List<ActivityOfferingScheduleComponentResult> scheduleComponents = new ArrayList<ActivityOfferingScheduleComponentResult>();
                    ActivityOfferingScheduleComponentResult scheduleComponent = CourseRegistrationAndScheduleOfClassesUtil.getActivityOfferingScheduleComponent(isTBA, roomCode, buildingCode,
                            weekdays, startTimeMs, endTimeMs);
                    scheduleComponents.add(scheduleComponent);
                    activityOffering.setScheduleComponents(scheduleComponents);
                    // End scheduling info

                    activityOfferings.add(activityOffering);
                    studentScheduleCourseResult.setActivityOfferings(activityOfferings);
                    hmCourseOffering.put(masterLprId, studentScheduleCourseResult);

                    // adding AOID to the list to search for instructors
                    if (!activityOfferingList.contains(luiId)) {
                        activityOfferingList.add(luiId);
                    }
                }
            }

            // Adding all course offerings for the particular term
            if (!hmTerm.containsKey(termId)) {
                List<String> masterLprIds = new ArrayList<String>();
                masterLprIds.add(masterLprId);
                hmTerm.put(termId, masterLprIds);
                TermSearchResult termInfo = new TermSearchResult();
                termInfo.setTermId(atpId);
                termInfo.setTermCode(atpCode);
                termInfo.setTermName(atpName);
                hmTermInfo.put(termId, termInfo);
            } else {
                if (!hmTerm.get(termId).contains(masterLprId)) {
                    hmTerm.get(termId).add(masterLprId);
                }
            }
        }

        // getting instructor info for AOs
        Map<String, List<InstructorSearchResult>> hmAOInstructors = new HashMap<String, List<InstructorSearchResult>>();
        if (!activityOfferingList.isEmpty()) {
            hmAOInstructors = CourseRegistrationAndScheduleOfClassesUtil.searchForInstructorsByAoIds(activityOfferingList, contextInfo);
        }

        for (Map.Entry<String, List<String>> pair : hmTerm.entrySet()) {
            List<StudentScheduleCourseResult> studentScheduleRegisteredCourseResults = new ArrayList<StudentScheduleCourseResult>();
            List<StudentScheduleCourseResult> studentScheduleWaitlistCourseResults = new ArrayList<StudentScheduleCourseResult>();
            TermSearchResult termInfo = hmTermInfo.get(pair.getKey());
            List<String> masterLuiIds = pair.getValue();
            for (String masterLuiId : masterLuiIds) {
                StudentScheduleCourseResult studentScheduleCourseResult = hmCourseOffering.get(masterLuiId);
                if (studentScheduleCourseResult.getActivityOfferings().size() > 1) {
                    CourseRegistrationAndScheduleOfClassesUtil.sortActivityOfferingReslutList(studentScheduleCourseResult.getActivityOfferings(), contextInfo);
                }
                for (StudentScheduleActivityOfferingResult activityOffering : studentScheduleCourseResult.getActivityOfferings()) {
                    if (hmAOInstructors.containsKey(activityOffering.getActivityOfferingId())) {
                        activityOffering.setInstructors(hmAOInstructors.get(activityOffering.getActivityOfferingId()));
                    }
                }
                if (studentScheduleCourseResult.isWaitlisted()) {
                    studentScheduleWaitlistCourseResults.add(studentScheduleCourseResult);
                } else {
                    studentScheduleRegisteredCourseResults.add(studentScheduleCourseResult);
                }
            }
            StudentScheduleTermResult studentScheduleTermResult = new StudentScheduleTermResult();
            studentScheduleTermResult.setTerm(termInfo);
            studentScheduleTermResult.setRegisteredCourseOfferings(studentScheduleRegisteredCourseResults);
            studentScheduleTermResult.setWaitlistCourseOfferings(studentScheduleWaitlistCourseResults);

            studentScheduleTermResults.add(studentScheduleTermResult);
        }

        return studentScheduleTermResults;
    }


    private static int toMins(String s) {
        String[] hourMin = s.split(":");
        if (hourMin.length == 2) {
            int hour = Integer.parseInt(hourMin[0]);
            int mins = Integer.parseInt(hourMin[1]);
            int hoursInMins = hour * 60;
            return hoursInMins + mins;
        }
        return -1;
    }
    /**
     * This method call search service to retrieve registration schedule data for the person
     *
     * @param userId Person Id
     * @param termId Term Key
     * @return StudentScheduleCourseResults
     * @throws OperationFailedException
     * @throws InvalidParameterException
     */
    private List<StudentScheduleTermResult> getRegistrationAndWaitlistScheduleByPersonAndTerm(String userId, String termId, ContextInfo contextInfo) throws LoginException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {
        List<StudentScheduleTermResult> studentScheduleTermResults = new ArrayList<StudentScheduleTermResult>();

        SearchRequestInfo searchRequest = new SearchRequestInfo(CourseRegistrationSearchServiceImpl.REG_INFO_BY_PERSON_TERM_SEARCH_TYPE.getKey());
        searchRequest.addParam(CourseRegistrationSearchServiceImpl.SearchParameters.PERSON_ID, userId);
        searchRequest.addParam(CourseRegistrationSearchServiceImpl.SearchParameters.ATP_ID, termId);

        SearchResultInfo searchResult;
        try {
            searchResult = CourseRegistrationAndScheduleOfClassesUtil.getSearchService().search(searchRequest, contextInfo);
        } catch (Exception e) {
            throw new OperationFailedException("Search of registration schedule for person " + userId + " and term " + termId + " failed: ", e);
        }

        String lastTerm = "";
        String lastLprId = "";
        String lastAoType = "";
        for (SearchResultHelper.KeyValue row : SearchResultHelper.wrap(searchResult)) {
            String lprType = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.LPR_TYPE);
            String masterLprId = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.MASTER_LPR_ID);
            String atpId = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.ATP_ID);
            String atpCode = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.ATP_CD);
            String atpName = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.ATP_NAME);
            String courseCode = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.COURSE_CODE);
            String courseId = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.COURSE_ID);
            String rgCode = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.RG_CODE);
            String aoName = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.AO_NAME);
            aoName = (aoName != null && aoName.length() >= 3 ? aoName.substring(0, 3).toUpperCase() : "");
            String aoType = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.AO_TYPE);
            String courseName = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.LUI_LONG_NAME);
            String courseDesc = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.LUI_DESC);
            String isTBA = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.TBA_IND);
            String roomCode = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.ROOM_CODE);
            String buildingCode = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.BUILDING_CODE);
            String weekdays = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.WEEKDAYS);
            String startTimeMs = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.START_TIME_MS);
            String endTimeMs = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.END_TIME_MS);
            String resultValuesGroupKey = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.RES_VAL_GROUP_KEY);
            String credits = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.CREDITS);
            String gradingOptionId = row.get(CourseRegistrationSearchServiceImpl.SearchResultColumns.GRADING_OPTION_ID);

            if (!lastTerm.equals(atpId)) {

            }

        }
        return studentScheduleTermResults;
    }

    /**
     * This method creates a registration request for the add operation of a single registration group.
     *
     * @param principalId     principal id
     * @param termId          term id
     * @param regGroupId      Registration Group id
     * @param masterLprId     masterLprId
     * @param credits         credits
     * @param gradingOptionId gradingOptionId
     * @param typeKey         typeKey
     * @param stateKey        stateKey
     * @param reqItemTypeKey  reqItemTypeKey
     * @param reqItemStateKey reqItemStateKey
     * @param okToWaitlist    flag to indicate that the student wants to be added to the waitlist if that option is available
     * @return registration request
     */
    private RegistrationRequestInfo createRegistrationRequest(String principalId, String termId, String regGroupId, String masterLprId, String credits, String gradingOptionId, String typeKey, String stateKey, String reqItemTypeKey, String reqItemStateKey, String courseCode, boolean okToWaitlist) {
        RegistrationRequestInfo regReqInfo = new RegistrationRequestInfo();
        regReqInfo.setRequestorId(principalId);
        regReqInfo.setTermId(termId); // bad bc we have it from the load call above
        regReqInfo.setTypeKey(typeKey);
        regReqInfo.setStateKey(stateKey);

        RegistrationRequestItemInfo registrationRequestItem = CourseRegistrationAndScheduleOfClassesUtil.createNewRegistrationRequestItem(principalId, regGroupId, masterLprId, credits, gradingOptionId, reqItemTypeKey, reqItemStateKey, courseCode, okToWaitlist);

        regReqInfo.getRegistrationRequestItems().add(registrationRequestItem);

        return regReqInfo;
    }

    private String verifyRegistrationRequestCreditsGradingOption(CourseOfferingInfo courseOfferingInfo, String credits, String gradingOptionId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {
        int firstValue = 0;

        // checking grading option. If null - just keep it that way
        if (!StringUtils.isEmpty(gradingOptionId) && (courseOfferingInfo.getStudentRegistrationGradingOptions().isEmpty()
                || !courseOfferingInfo.getStudentRegistrationGradingOptions().contains(gradingOptionId)) && !ArrayUtils.contains(CourseOfferingServiceConstants.ALL_GRADING_OPTION_TYPE_KEYS, gradingOptionId)) {
            throw new InvalidParameterException("Grading option doesn't match");
        }

        //Lookup the selected credit option and set from persisted values
        if (!courseOfferingInfo.getCreditOptionId().isEmpty()) {
            //Lookup the resultValueGroup Information
            ResultValuesGroupInfo resultValuesGroupInfo = CourseRegistrationAndScheduleOfClassesUtil.getLrcService().getResultValuesGroup(courseOfferingInfo.getCreditOptionId(), contextInfo);
            String typeKey = resultValuesGroupInfo.getTypeKey();

            //Get the actual values
            List<ResultValueInfo> resultValueInfos = CourseRegistrationAndScheduleOfClassesUtil.getLrcService().getResultValuesByKeys(resultValuesGroupInfo.getResultValueKeys(), contextInfo);

            if (!resultValueInfos.isEmpty()) {
                if (typeKey.equals(LrcServiceConstants.RESULT_VALUES_GROUP_TYPE_KEY_FIXED)) {
                    credits = resultValueInfos.get(firstValue).getValue(); // fixed credits
                } else if (typeKey.equals(LrcServiceConstants.RESULT_VALUES_GROUP_TYPE_KEY_RANGE)) {  // range
                    if (credits.isEmpty() || (Float.valueOf(credits) < Float.valueOf(resultValuesGroupInfo.getResultValueRange().getMinValue()))
                            || (Float.valueOf(credits) > Float.valueOf(resultValuesGroupInfo.getResultValueRange().getMaxValue()))) {
                        throw new InvalidParameterException("Credits are incorrect");
                    }
                } else if (typeKey.equals(LrcServiceConstants.RESULT_VALUES_GROUP_TYPE_KEY_MULTIPLE)) {  // multiple
                    List<String> creditOptions = new ArrayList<String>();
                    for (ResultValueInfo resultValueInfo : resultValueInfos) {
                        creditOptions.add(resultValueInfo.getValue());
                    }
                    if (credits.isEmpty() || !creditOptions.contains(credits)) {
                        throw new InvalidParameterException("Credits are incorrect");
                    }
                }
            }
        }

        return credits;
    }

    private RegistrationRequestInfo updateScheduleItem(String courseCode, String regGroupId, String regGroupCode, String masterLprId, String termId, String credits, String gradingOptionId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, DoesNotExistException, OperationFailedException, PermissionDeniedException, DataValidationErrorException, ReadOnlyException, AlreadyExistsException {

        return updateRegistrationItem(courseCode, regGroupId, regGroupCode, masterLprId, termId, credits, gradingOptionId, contextInfo, LprServiceConstants.REQ_ITEM_UPDATE_TYPE_KEY);

    }

    private RegistrationRequestInfo updateWaitlistEntry(String courseCode, String regGroupId, String regGroupCode, String masterLprId, String termId, String credits, String gradingOptionId, ContextInfo contextInfo) throws DoesNotExistException, PermissionDeniedException, OperationFailedException, InvalidParameterException, ReadOnlyException, MissingParameterException, DataValidationErrorException, AlreadyExistsException {

        return updateRegistrationItem(courseCode,regGroupId, regGroupCode, masterLprId, termId, credits, gradingOptionId, contextInfo, LprServiceConstants.REQ_ITEM_UPDATE_WAITLIST_TYPE_KEY);

    }

    /*
     * Utility method to update any kind of schedule item (registered, waitlisted, etc)
     */
    private RegistrationRequestInfo updateRegistrationItem(String courseCode, String regGroupId, String regGroupCode, String masterLprId, String termId, String credits,
                                                      String gradingOptionId, ContextInfo contextInfo, String typeKey)
            throws DoesNotExistException, PermissionDeniedException, OperationFailedException,
            InvalidParameterException, ReadOnlyException, MissingParameterException, DataValidationErrorException,
            AlreadyExistsException {
        RegistrationRequestInfo registrationRequestInfo = new RegistrationRequestInfo();
        String userId = contextInfo.getPrincipalId();

        //Populate Fields for RegRequestInfo object
        registrationRequestInfo.setRequestorId(userId);
        registrationRequestInfo.setStateKey(LprServiceConstants.LPRTRANS_NEW_STATE_KEY);
        registrationRequestInfo.setTypeKey(LprServiceConstants.LPRTRANS_REGISTRATION_TYPE_KEY);

        //Create Reg Request Item
        RegistrationRequestItemInfo registrationRequestItem = CourseRegistrationAndScheduleOfClassesUtil.createNewRegistrationRequestItem(userId, regGroupId,
                masterLprId, credits, gradingOptionId, typeKey, LprServiceConstants.LPRTRANS_ITEM_NEW_STATE_KEY, courseCode, false);
        List<RegistrationRequestItemInfo> registrationRequestItemInfos = new ArrayList<RegistrationRequestItemInfo>();
        registrationRequestItemInfos.add(registrationRequestItem);
        registrationRequestInfo.setRegistrationRequestItems(registrationRequestItemInfos);
        registrationRequestInfo.setTermId(termId);

        //Create Registration Request
        RegistrationRequestInfo newRegReq = CourseRegistrationAndScheduleOfClassesUtil.getCourseRegistrationService().
                createRegistrationRequest(LprServiceConstants.LPRTRANS_REGISTRATION_TYPE_KEY, registrationRequestInfo, contextInfo);

        // submit the request to the registration engine.
        CourseRegistrationAndScheduleOfClassesUtil.getCourseRegistrationService().submitRegistrationRequest(newRegReq.getId(), contextInfo);

        return newRegReq;
    }

    private List<String> getCourseOfferingCreditOptionValues(String creditOptionId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {
        int firstValue = 0;
        List<String> creditOptions = new ArrayList<String>();

        //Lookup the selected credit option and set from persisted values
        if (!creditOptionId.isEmpty()) {
            //Lookup the resultValueGroup Information
            ResultValuesGroupInfo resultValuesGroupInfo = CourseRegistrationAndScheduleOfClassesUtil.getLrcService().getResultValuesGroup(creditOptionId, contextInfo);
            String typeKey = resultValuesGroupInfo.getTypeKey();

            //Get the actual values
            List<ResultValueInfo> resultValueInfos = CourseRegistrationAndScheduleOfClassesUtil.getLrcService().getResultValuesByKeys(resultValuesGroupInfo.getResultValueKeys(), contextInfo);

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
        }

        return creditOptions;
    }

    /**
     * All info about a registration request can be found in the LprTransaction. get the lpr transaction and transform
     * it into something useful.
     *
     * This method has been temporarily set as public because the TestCourseRegistrationEngine is relying on it...
     *
     * @param regReqId    registration request id
     * @param contextInfo context of the call
     * @throws PermissionDeniedException
     * @throws MissingParameterException
     * @throws InvalidParameterException
     * @throws OperationFailedException
     * @throws DoesNotExistException
     */
    public RegistrationResponseResult getRegistrationStatusLocal(String regReqId, ContextInfo contextInfo) throws PermissionDeniedException, MissingParameterException, InvalidParameterException, OperationFailedException, DoesNotExistException {

        RegistrationResponseResult result = new RegistrationResponseResult();

        LprTransactionInfo lprTransactionInfo = getLprService().getLprTransaction(regReqId, contextInfo);

        result.setRegistrationRequestId(lprTransactionInfo.getId());
        result.setState(lprTransactionInfo.getStateKey());
        result.getStatuses().add(lprTransactionInfo.getStateKey()); // use state for now until we come up with something better

        List<LprTransactionItemInfo> lprTransItems = lprTransactionInfo.getLprTransactionItems();

        // for some reason the users want this in reverse order.
        Collections.sort(lprTransItems, LPR_TRANS_ITEM_CREATE_DATE); // make sure we're sorting by date

        for (LprTransactionItemInfo lprTransactionItemInfo : lprTransItems) {
            RegistrationResponseItemResult resultItem = new RegistrationResponseItemResult();

            resultItem.setRegistrationRequestItemId(lprTransactionItemInfo.getId());
            resultItem.setRegistrationRequestId(lprTransactionInfo.getId());

            resultItem.setState(lprTransactionItemInfo.getStateKey());
            resultItem.setStatus(lprTransactionItemInfo.getStateKey()); // we should be using the result state, but that is currently a boolean and not useful
            resultItem.setNewLuiId(lprTransactionItemInfo.getNewLuiId());
            resultItem.setType(lprTransactionItemInfo.getTypeKey());

            for(ValidationResultInfo validationResult : lprTransactionItemInfo.getValidationResults()){
                resultItem.getMessages().add(validationResult.getMessage());

                //Translate some "pseudo" states for ease in the ui to indicate if waitlists are available or was added to waitlist, etc.
                if(LprServiceConstants.LPRTRANS_ITEM_FAILED_STATE_KEY.equals(lprTransactionItemInfo.getStateKey()) &&
                        validationResult.getMessage().contains(LprServiceConstants.LPRTRANS_ITEM_WAITLIST_AVAILABLE_MESSAGE_KEY)){
                    resultItem.setState(CourseRegistrationClientService.LPRTRANS_ITEM_WAITLIST_AVAILABLE_STATE_KEY);
                } else if(LprServiceConstants.LPRTRANS_ITEM_SUCCEEDED_STATE_KEY.equals(lprTransactionItemInfo.getStateKey()) &&
                        validationResult.getMessage().contains(LprServiceConstants.LPRTRANS_ITEM_WAITLIST_WAITLISTED_MESSAGE_KEY)){
                    resultItem.setState(CourseRegistrationClientService.LPRTRANS_ITEM_WAITLIST_STATE_KEY);
                }
            }

            resultItem.setResultingLprId(lprTransactionItemInfo.getResultingLprId());

            result.getResponseItemResults().add(resultItem);
        }

        return result;
    }

    @Override
    public Response getLearningPlan(String termId, String termCode) {
        Response.ResponseBuilder response;

        try {
            ContextInfo contextInfo = ContextUtils.createDefaultContextInfo();
            if(getAcademicPlanService() == null){
                return getResponse(Response.Status.NOT_FOUND, new RegistrationValidationResult(LEARNING_PLAN_NOT_CONFIGURED_MESSAGE_KEY)).build();
            }

           // get the learning plan for this student
            List<LearningPlanInfo> learningPlans = getAcademicPlanService().getLearningPlansForStudentByType(contextInfo.getPrincipalId(), AcademicPlanServiceConstants.LEARNING_PLAN_TYPE_PLAN, contextInfo);

            if(learningPlans == null || learningPlans.isEmpty()){
                return getResponse(Response.Status.NOT_FOUND, new RegistrationValidationResult(COURSE_CODE_NOT_FOUND_MESSAGE_KEY)).build();
            }

            // get the plan items
            List<PlanItemInfo> allPlanItems = new ArrayList<>();
            for(LearningPlanInfo lpi : learningPlans){
                List<PlanItemInfo> planItemsTemp = getAcademicPlanService().getPlanItemsInPlan(lpi.getId(), contextInfo);
                if(planItemsTemp != null && !planItemsTemp.isEmpty()){
                    allPlanItems.addAll(planItemsTemp);
                }
            }

            List<String> coursesToLookup = new ArrayList<>();
            List<String> rgsToLookup = new ArrayList<>();

            // get only the plans for this term
            termId = CourseRegistrationAndScheduleOfClassesUtil.getTermId(termId, termCode);
            List<PlanItemInfo> termPlanItems = new ArrayList<>();
            for(PlanItemInfo planItemInfo : allPlanItems){
                if(planItemInfo.getPlanTermIds().contains(termId)){
                    termPlanItems.add(planItemInfo);

                    // add additional lookup ids to their lists
                    if(LuServiceConstants.CREDIT_COURSE_LU_TYPE_KEY.equals(planItemInfo.getRefObjectType())) {
                        coursesToLookup.add(planItemInfo.getRefObjectId());
                    } else if(LuiServiceConstants.REGISTRATION_GROUP_TYPE_KEY.equals(planItemInfo.getRefObjectType())){
                        rgsToLookup.add(planItemInfo.getRefObjectId());
                    }
                }
            }

            if(termPlanItems == null || termPlanItems.isEmpty()){
                return getResponse(Response.Status.NOT_FOUND, new RegistrationValidationResult(COURSE_CODE_NOT_FOUND_MESSAGE_KEY)).build();
            }

            // clu verIndId -> courseSearchResult
            Map<String, CourseSearchResult> courseInfoMap = new HashMap<>();
            if(!coursesToLookup.isEmpty()){
                // Learning plan gives us the version independent id. We need to convert those into clu Ids
                Map<String, String> cluToVidMap = convertCluVersionIndIdToCluId(coursesToLookup, contextInfo);
                List<String> cluIds = new ArrayList<>(cluToVidMap.keySet());

                List<CourseSearchResult> courseInfoResults = searchForCourseInfo(cluIds,termId, contextInfo);
                if(courseInfoResults != null){
                    for(CourseSearchResult courseInfoResult : courseInfoResults){
                        courseInfoMap.put(cluToVidMap.get(courseInfoResult.getCluId()), courseInfoResult);
                    }
                }
            }

            Map<String, RegGroupSearchResult> regGroupMap = new HashMap<>();
            if(!rgsToLookup.isEmpty()){
                for(String rgId : rgsToLookup) {
                    RegGroupSearchResult regGroupSearchResult = getScheduleOfClassesService().getRegGroup(rgId, contextInfo);
                    if(regGroupSearchResult != null){
                        regGroupMap.put(regGroupSearchResult.getRegGroupId(), regGroupSearchResult);

                        // we'll need course info objects for both the reg groups and the course offerings
                        CourseSearchResult courseSearchResult = getScheduleOfClassesService().getCourseOfferingById(regGroupSearchResult.getCourseOfferingId(), contextInfo);
                        courseInfoMap.put(rgId, courseSearchResult);
                    }
                }
            }

            // Now we have all the plans... lets convert them into something the user wants to see
            List<LearningPlanItemResult> lpResults = new ArrayList<>(termPlanItems.size());
            for(PlanItemInfo planItemInfo : termPlanItems){
                LearningPlanItemResult lpiResult = new LearningPlanItemResult();
                lpiResult.setCategory(planItemInfo.getCategory().toString());
                lpiResult.setLearningPlanId(planItemInfo.getLearningPlanId());
                lpiResult.setPlanItemTermId(termId);
                lpiResult.setRefObjectId(planItemInfo.getRefObjectId());
                lpiResult.setRefObjectType(planItemInfo.getRefObjectType());
                lpiResult.setCluId(courseInfoMap.get(planItemInfo.getRefObjectId()).getCluId());  // both clus and reg groups have course results mapped to the ref object key
                // build the label for the item
                lpiResult.setItemLabel(getLearningPlanCourseLabel(planItemInfo.getRefObjectId(),planItemInfo.getRefObjectType(), courseInfoMap, regGroupMap ));
                lpResults.add(lpiResult);
            }

            response = Response.ok(lpResults);
        } catch (Exception e) {
            LOGGER.warn("Exception occurred", e);
            response = Response.serverError().entity(e.getMessage());
        }

        return response.build();
    }

    /**
     * create a map of cluId to VersionIndIds
     * @param versionIndependentIds
     * @param contextInfo
     * @return
     * @throws MissingParameterException
     * @throws InvalidParameterException
     * @throws OperationFailedException
     * @throws PermissionDeniedException
     */
    private Map<String,String> convertCluVersionIndIdToCluId(List<String> versionIndependentIds, ContextInfo contextInfo) throws MissingParameterException, InvalidParameterException, OperationFailedException, PermissionDeniedException {
        Map<String, String> cluToVidMap = new HashMap<>();

        for(String versionIndependentId : versionIndependentIds) {
            QueryByCriteria.Builder qbcBuilder = QueryByCriteria.Builder.create();
            qbcBuilder.setPredicates(PredicateFactory.equal("version.versionIndId", versionIndependentId));

            List<String> cluIds = getCluService().searchForCluIds(qbcBuilder.build(), contextInfo);

            cluToVidMap.put(KSCollectionUtils.getRequiredZeroElement(cluIds),versionIndependentId);
        }
        return cluToVidMap;
    }

    private String getLearningPlanCourseLabel(String refObjectid, String refObjectType, Map<String, CourseSearchResult> courseInfoMap, Map<String, RegGroupSearchResult> regGroupMap ) throws DoesNotExistException, MissingParameterException, InvalidParameterException, OperationFailedException, PermissionDeniedException {
        String label = new String();
        if(LuServiceConstants.CREDIT_COURSE_LU_TYPE_KEY.equals(refObjectType)) {
            label = courseInfoMap.get(refObjectid).getCourseCode();
        } else if(LuiServiceConstants.REGISTRATION_GROUP_TYPE_KEY.equals(refObjectType)){
            CourseSearchResult courseSearchResult = courseInfoMap.get(refObjectid);
            label = courseSearchResult.getCourseCode() + " (" + regGroupMap.get(refObjectid).getRegGroupName() + ")";
        }
        return label;
    }

    /**
     * return courseSearchResult objects based on the cluIds and term id passed in.
     * @param cluIds
     * @param termId
     * @param context
     * @return
     * @throws MissingParameterException
     * @throws InvalidParameterException
     * @throws OperationFailedException
     * @throws DoesNotExistException
     * @throws PermissionDeniedException
     */
    protected List<CourseSearchResult> searchForCourseInfo(List<String> cluIds, String termId, ContextInfo context) throws MissingParameterException, InvalidParameterException, OperationFailedException, DoesNotExistException, PermissionDeniedException {
        List<CourseSearchResult> courseInfoResults = new ArrayList<>();

        for(String cluId : cluIds) {
            List<CourseSearchResult> courseInfoResultsTemp =  getScheduleOfClassesService().searchForCourseOfferingsByTermIdAndCluId(termId, cluId, context);
            if(courseInfoResultsTemp != null && !courseInfoResultsTemp.isEmpty()) {
                courseInfoResults.addAll(courseInfoResultsTemp);
            }
        }


       return courseInfoResults;

    }

    /**
     * The academic plan service is NOT part of ENR so it should* not be injected. Instead, pull it from the GRL if
     * it exists.
     * @return
     */
    private AcademicPlanService getAcademicPlanService() {
        if (academicPlanService == null) {
            academicPlanService = GlobalResourceLoader.getService(new QName(AcademicPlanServiceConstants.NAMESPACE, AcademicPlanServiceConstants.SERVICE_NAME));
        }
        return academicPlanService;
    }

    public void setAcademicPlanService(AcademicPlanService academicPlanService) {
        this.academicPlanService = academicPlanService;
    }

    /**
     * The CLU service is NOT part of ENR so it should* not be injected. Instead, pull it from the GRL if
     * it exists.
     * @return
     */
    public CluService getCluService() {
        if (cluService == null) {
            cluService = GlobalResourceLoader.getService(new QName(CluServiceConstants.NAMESPACE, CluServiceConstants.SERVICE_NAME_LOCAL_PART));
        }
        return cluService;
    }

    public void setCluService(CluService cluService) {
        this.cluService = cluService;
    }

    protected LprService getLprService() {
        if (lprService == null) {
            lprService = GlobalResourceLoader.getService(new QName(LprServiceConstants.NAMESPACE, LprServiceConstants.SERVICE_NAME_LOCAL_PART));
        }
        return lprService;
    }

    public void setLprService(LprService lprService) {
        this.lprService = lprService;
    }

    protected ScheduleOfClassesService getScheduleOfClassesService() {
        if (scheduleOfClassesService == null) {
            scheduleOfClassesService = GlobalResourceLoader.getService(ScheduleOfClassesServiceConstants.QNAME);
        }

        return scheduleOfClassesService;
    }

    public void setScheduleOfClassesService(ScheduleOfClassesService scheduleOfClassesService) {
        this.scheduleOfClassesService = scheduleOfClassesService;
    }

    private static Response.ResponseBuilder getResponse(Response.Status status, Object entity) {
        //The request needs additional options (HTTP status 400 Bad Request)
        Response.ResponseBuilder response = Response.status(status).entity(entity);
        response.header("Access-Control-Allow-Header", "Content-Type");
        response.header("Access-Control-Allow-Methods", "POST, PUT, DELETE, GET, OPTIONS");
        response.header("Access-Control-Allow-Origin", "*");
        return response;
    }
}
