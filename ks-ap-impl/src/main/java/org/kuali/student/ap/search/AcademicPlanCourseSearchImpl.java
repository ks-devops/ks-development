/*
 * Copyright 2014 The Kuali Foundation Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.kuali.student.ap.search;

import org.apache.commons.lang.StringUtils;
import org.kuali.student.ap.framework.context.CourseSearchConstants;
import org.kuali.student.r2.common.class1.search.SearchServiceAbstractHardwiredImpl;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.util.RichTextHelper;
import org.kuali.student.r2.common.util.constants.LuiServiceConstants;
import org.kuali.student.r2.common.util.date.DateFormatters;
import org.kuali.student.r2.core.class1.type.dto.TypeInfo;
import org.kuali.student.r2.core.search.dto.SearchRequestInfo;
import org.kuali.student.r2.core.search.dto.SearchResultInfo;
import org.kuali.student.r2.core.search.dto.SearchResultRowInfo;
import org.kuali.student.r2.core.search.util.SearchRequestHelper;
import org.kuali.student.r2.lum.util.constants.CluServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class AcademicPlanCourseSearchImpl extends SearchServiceAbstractHardwiredImpl {
    private static final Logger LOG = LoggerFactory.getLogger(AcademicPlanCourseSearchImpl.class);



    // Search Types
    public static final TypeInfo KSAP_COURSE_SEARCH;
    public static final TypeInfo KSAP_COURSE_SEARCH_COURSEIDS_BY_TERM_SCHEDULED;
    public static final TypeInfo KSAP_COURSE_SEARCH_COURSEIDS_BY_TERM_OFFERED;



    public static final String DEFAULT_EFFECTIVE_DATE = "01/01/2012";

    static {
        TypeInfo info = new TypeInfo();
        info.setKey(CourseSearchConstants.KSAP_COURSE_SEARCH_KEY);
        info.setName("KSAP Course Search");
        info.setDescr(new RichTextHelper().fromPlain("Return search results from KSAP course search"));

        try {
            info.setEffectiveDate(DateFormatters.MONTH_DAY_YEAR_DATE_FORMATTER.parse("01/01/2012"));
        } catch ( IllegalArgumentException ex) {
            throw new RuntimeException("bad code");
        }
        KSAP_COURSE_SEARCH = info;

        info = new TypeInfo();
        info.setKey(CourseSearchConstants.KSAP_COURSE_SEARCH_COURSEIDS_BY_TERM_SCHEDULED_KEY);
        info.setName("Course Id Search By COs Scheduled In Term");
        info.setDescr(new RichTextHelper().fromPlain("Search for course ids based on if COs for course are offered in a term"));
        info.setEffectiveDate(DateFormatters.MONTH_DAY_YEAR_DATE_FORMATTER.parse(DEFAULT_EFFECTIVE_DATE));
        KSAP_COURSE_SEARCH_COURSEIDS_BY_TERM_SCHEDULED = info;

        info = new TypeInfo();
        info.setKey(CourseSearchConstants.KSAP_COURSE_SEARCH_COURSEIDS_BY_TERM_OFFERED_KEY);
        info.setName("Course Id Search By Terms Offered");
        info.setDescr(new RichTextHelper().fromPlain("Search for course ids based on if it is set to be offered in a type of term"));
        info.setEffectiveDate(DateFormatters.MONTH_DAY_YEAR_DATE_FORMATTER.parse(DEFAULT_EFFECTIVE_DATE));
        KSAP_COURSE_SEARCH_COURSEIDS_BY_TERM_OFFERED = info;


    }

    /**
     * Get the search type that the sub class implements.
     */
    @Override
    public TypeInfo getSearchType() {
        return KSAP_COURSE_SEARCH;
    }

    @Override
    public TypeInfo getSearchType(String searchTypeKey, ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        if (CourseSearchConstants.KSAP_COURSE_SEARCH_COURSEIDS_BY_TERM_SCHEDULED_KEY.equals(searchTypeKey)) {
            return KSAP_COURSE_SEARCH_COURSEIDS_BY_TERM_SCHEDULED;
        } else if (CourseSearchConstants.KSAP_COURSE_SEARCH_COURSEIDS_BY_TERM_OFFERED_KEY.equals(searchTypeKey)) {
            return KSAP_COURSE_SEARCH_COURSEIDS_BY_TERM_OFFERED;
        }
        throw new DoesNotExistException("No Search Type Found for key:"+searchTypeKey);
    }

    @Override
    public SearchResultInfo search(SearchRequestInfo searchRequestInfo, ContextInfo contextInfo) throws MissingParameterException, OperationFailedException, PermissionDeniedException {
        SearchResultInfo resultInfo;

        if (StringUtils.equals(searchRequestInfo.getSearchKey(), KSAP_COURSE_SEARCH_COURSEIDS_BY_TERM_SCHEDULED.getKey())) {
            resultInfo =  searchForCluIdsScheduledForTerms(searchRequestInfo, contextInfo);
        }else if (StringUtils.equals(searchRequestInfo.getSearchKey(), KSAP_COURSE_SEARCH_COURSEIDS_BY_TERM_OFFERED.getKey())) {
            resultInfo =  searchForCluIdsOfferedForTerms(searchRequestInfo, contextInfo);
        }else {
            throw new OperationFailedException("Unsupported search type: " + searchRequestInfo.getSearchKey());
        }

        return resultInfo;
    }

    protected SearchResultInfo searchForCluIdsScheduledForTerms(SearchRequestInfo searchRequestInfo, ContextInfo contextInfo)
            throws MissingParameterException, OperationFailedException {
        SearchRequestHelper requestHelper = new SearchRequestHelper(searchRequestInfo);

        String searchAtpId = requestHelper.getParamAsString(CourseSearchConstants.SearchParameters.ATP_ID);

        SearchResultInfo resultInfo = new SearchResultInfo();

        String queryStr = "SELECT" +
                "    DISTINCT" +
                "    lui.cluId";

        queryStr = queryStr +
                "    FROM" +
                "    LuiEntity lui ";

        queryStr = queryStr +
                "    WHERE" +
                "    lui.atpId = :atpId " +
                "    AND lui.luiState = '"+ LuiServiceConstants.LUI_CO_STATE_OFFERED_KEY+"'";

        TypedQuery<Object[]> query = getEntityManager().createQuery(queryStr, Object[].class);
        query.setParameter(CourseSearchConstants.SearchParameters.ATP_ID, searchAtpId);
        List<Object[]> results = query.getResultList();

        for(Object resultRow : results){
            int i = 0;
            SearchResultRowInfo row = new SearchResultRowInfo();
            row.addCell(CourseSearchConstants.SearchResultColumns.CLU_ID, (String)resultRow);
            resultInfo.getRows().add(row);
        }

        return resultInfo;
    }

    protected SearchResultInfo searchForCluIdsOfferedForTerms(SearchRequestInfo searchRequestInfo, ContextInfo contextInfo)
            throws MissingParameterException, OperationFailedException {
        SearchRequestHelper requestHelper = new SearchRequestHelper(searchRequestInfo);

        String searchAtpType = requestHelper.getParamAsString(CourseSearchConstants.SearchParameters.ATP_TYPE_KEY);

        SearchResultInfo resultInfo = new SearchResultInfo();

        String queryStr = "SELECT" +
                "    cluAtp.CLU_ID";

        queryStr = queryStr +
                "    FROM" +
                "    KSLU_CLU_ATP_TYPE_KEY cluAtp, " +
                "    KSLU_CLU clu ";

        queryStr = queryStr +
                "    WHERE" +
                "    cluAtp.ATP_TYPE_KEY = :atpTypeKey " +
                "    AND clu.ID = cluAtp.CLU_ID "+
                "    AND clu.ST = 'Active'" +
                "    AND clu.LUTYPE_ID = '"+ CluServiceConstants.CREDIT_COURSE_LU_TYPE_KEY+"'";

        Query query = getEntityManager().createNativeQuery(queryStr);
        query.setParameter(CourseSearchConstants.SearchParameters.ATP_TYPE_KEY, searchAtpType);
        List<Object> results = query.getResultList();

        for(Object resultRow : results){
            int i = 0;
            SearchResultRowInfo row = new SearchResultRowInfo();
            row.addCell(CourseSearchConstants.SearchResultColumns.CLU_ID, (String)resultRow);
            resultInfo.getRows().add(row);
        }

        return resultInfo;
    }
}
