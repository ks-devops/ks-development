package org.kuali.student.enrollment.registration.client.service.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.kuali.student.enrollment.registration.client.service.dto.CourseSearchResult;
import org.kuali.student.enrollment.registration.client.service.dto.RegGroupSearchResult;
import org.kuali.student.enrollment.registration.search.elastic.ElasticEmbedded;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;

import java.util.ArrayList;
import java.util.List;

/**
 * There are some methods on the ScheduleOfClassesImpl that could / should be backed by a cache. In this case
 * elastic
 */
public class ScheduleOfClassesServiceElasticImpl extends ScheduleOfClassesServiceImpl {

    protected ElasticEmbedded elasticEmbedded;

    @Override
    public RegGroupSearchResult getRegGroup(String regGroupId) throws InvalidParameterException, MissingParameterException, PermissionDeniedException, OperationFailedException {
        RegGroupSearchResult regGroupSearchResult = null;

        GetResponse getResponse = elasticEmbedded.getClient().prepareGet(ElasticEmbedded.KS_ELASTIC_INDEX, ElasticEmbedded.REGISTRATION_GROUP_ELASTIC_TYPE, regGroupId).execute().actionGet();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (getResponse.isExists()) {
                regGroupSearchResult = objectMapper.readValue(getResponse.getSourceAsString(), RegGroupSearchResult.class);
            }
        } catch (Exception e) {
            throw new OperationFailedException("Error searching for course offering id. ", e);
        }
        return regGroupSearchResult;
    }

    @Override
    public List<RegGroupSearchResult> searchForRegGroupsByCourseAndName(String courseOfferingId, String regGroupName) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<RegGroupSearchResult> resultList = new ArrayList<>();

        try {

            //QueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("courseOfferingId", courseOfferingId)).should(QueryBuilders.termQuery("regGroupName", regGroupName));

            BoolFilterBuilder fb = FilterBuilders.boolFilter()
                    .must(FilterBuilders.termFilter("courseOfferingId", courseOfferingId));
            if(regGroupName != null && !regGroupName.isEmpty()) {
                fb.should(FilterBuilders.termFilter("regGroupName", regGroupName));
            }

            QueryBuilder query = QueryBuilders.filteredQuery(
                    QueryBuilders.matchAllQuery(),fb);

            SearchResponse sr = elasticEmbedded.getClient()
                    .prepareSearch(ElasticEmbedded.KS_ELASTIC_INDEX)
                    .setTypes(ElasticEmbedded.REGISTRATION_GROUP_ELASTIC_TYPE)
                    .setQuery(query)
                    .execute().actionGet();


            ObjectMapper objectMapper = new ObjectMapper();
            for (SearchHit hit : sr.getHits().getHits()) {
                RegGroupSearchResult regGroupSearchResult = objectMapper.readValue(hit.getSourceAsString(), RegGroupSearchResult.class);
                resultList.add(regGroupSearchResult);
            }

            if(resultList.isEmpty()) {
                resultList = null;
                if(LOGGER.isDebugEnabled()){
                    LOGGER.debug("No Result found for Elastic Search: " + query.toString());
                }
            }

        } catch (Exception e) {
            throw new OperationFailedException("Error searching for course offering id. ", e);
        }
        return resultList;
    }

    protected List<String> searchForCourseOfferingIdByCourseCodeAndTerm(String courseCode, String atpId) throws InvalidParameterException, MissingParameterException, PermissionDeniedException, OperationFailedException {
        List<String> resultList = new ArrayList<>();
        FilteredQueryBuilder query = null;

        try {
            //FilteredQueryBuilder query = QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), FilterBuilders.andFilter(FilterBuilders.termFilter("courseCode", courseCode), FilterBuilders.termsFilter("termId", atpId.toLowerCase().split("\\."))));

            query = QueryBuilders.filteredQuery(
                    QueryBuilders.matchAllQuery(),
                    FilterBuilders.andFilter(
                            FilterBuilders.termFilter("courseCode", courseCode.toLowerCase()),
                            FilterBuilders.termsFilter("termId", atpId.toLowerCase().split("\\."))
                    )
            );

            SearchResponse sr = elasticEmbedded.getClient()
                    .prepareSearch(ElasticEmbedded.KS_ELASTIC_INDEX)
                    .setTypes(ElasticEmbedded.COURSEOFFERING_ELASTIC_TYPE)
                    .setQuery(query)
                    .execute().actionGet();


            ObjectMapper objectMapper = new ObjectMapper();
            for (SearchHit hit : sr.getHits().getHits()) {
                CourseSearchResult courseSearchResult = objectMapper.readValue(hit.getSourceAsString(), CourseSearchResult.class);
                resultList.add(courseSearchResult.getCourseId());
            }

            if (resultList.isEmpty()) resultList = null;

        } catch (Exception e) {
            throw new OperationFailedException("Error searching for course code and term. " + query.toString(), e);
        }
        return resultList;
    }


    public void setElasticEmbedded(ElasticEmbedded elasticEmbedded) {
        this.elasticEmbedded = elasticEmbedded;
    }
}
