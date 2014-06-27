package org.kuali.student.ap.coursesearch.dataobject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chmaurer
 * Date: 6/9/14
 * Time: 10:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class ActivityFormatDetailsWrapper {
    private String formatName;
    private String formatType;
    private String termId;
    private String courseOfferingCode;
    private String formatOfferingId;
    private List<ActivityOfferingDetailsWrapper> activityOfferingDetailsWrappers;

    public ActivityFormatDetailsWrapper(String termId, String courseOfferingCode, String formatOfferingId, String activityFormatName, String activityTypeKey) {
        formatName = activityFormatName;
        formatType = activityTypeKey;
        this.termId = termId;
        this.courseOfferingCode = courseOfferingCode;
        this.formatOfferingId = formatOfferingId;
    }

    public String getFormatName() {
        return formatName;
    }

    public void setFormatName(String formatName) {
        this.formatName = formatName;
    }

    public String getFormatType() {
        return formatType;
    }

    public void setFormatType(String formatType) {
        this.formatType = formatType;
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getCourseOfferingCode() {
        return courseOfferingCode;
    }

    public void setCourseOfferingCode(String courseOfferingCode) {
        this.courseOfferingCode = courseOfferingCode;
    }

    public String getFormatOfferingId() {
        return formatOfferingId;
    }

    public void setFormatOfferingId(String formatOfferingId) {
        this.formatOfferingId = formatOfferingId;
    }

    public List<ActivityOfferingDetailsWrapper> getActivityOfferingDetailsWrappers() {
        return activityOfferingDetailsWrappers;
    }

    public void setActivityOfferingDetailsWrappers(List<ActivityOfferingDetailsWrapper> activityOfferingDetailsWrappers) {
        this.activityOfferingDetailsWrappers = activityOfferingDetailsWrappers;
    }
}
