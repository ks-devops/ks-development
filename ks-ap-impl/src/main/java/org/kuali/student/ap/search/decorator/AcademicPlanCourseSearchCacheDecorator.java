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

package org.kuali.student.ap.search.decorator;

import net.sf.ehcache.CacheManager;
import org.kuali.student.ap.search.AcademicPlanCourseSearchImpl;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.core.class1.util.SearchCacheDecoratorUtil;
import org.kuali.student.r2.core.search.dto.SearchRequestInfo;
import org.kuali.student.r2.core.search.dto.SearchResultInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AcademicPlanCourseSearchCacheDecorator extends AcademicPlanCourseSearchImpl {
    private static final Logger LOG = LoggerFactory.getLogger(AcademicPlanCourseSearchCacheDecorator.class);

    private static final String ACADEMIC_PLAN_SEARCH_CACHE = "academicPlanSearchCache";
    private static final String ACADEMIC_PLAN_SEARCH_KEY_PREFIX = "ksapsearch";

    private CacheManager cacheManager;

    private AcademicPlanCourseSearchImpl nextDecorator;

    public AcademicPlanCourseSearchImpl getNextDecorator() throws OperationFailedException {
        if (null == nextDecorator) {
            throw new OperationFailedException("Misconfigured application: nextDecorator is null");
        }

        return nextDecorator;
    }

    public void setNextDecorator(AcademicPlanCourseSearchImpl nextDecorator) {
        this.nextDecorator = nextDecorator;
    }

    @Override
    public SearchResultInfo search(SearchRequestInfo searchRequestInfo, ContextInfo contextInfo) throws MissingParameterException, OperationFailedException, PermissionDeniedException {
        try {
            return SearchCacheDecoratorUtil.search(getCacheManager().getCache(ACADEMIC_PLAN_SEARCH_CACHE), ACADEMIC_PLAN_SEARCH_KEY_PREFIX, getNextDecorator(), searchRequestInfo, contextInfo);
        } catch (InvalidParameterException e) {
            LOG.warn("Unable to access cache", e);
            return getNextDecorator().search(searchRequestInfo,contextInfo);
        }
    }

    public CacheManager getCacheManager() {
        if (cacheManager == null) {
            cacheManager = CacheManager.getInstance();
        }
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}
