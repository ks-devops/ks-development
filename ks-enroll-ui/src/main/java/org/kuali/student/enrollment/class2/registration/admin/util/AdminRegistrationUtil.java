package org.kuali.student.enrollment.class2.registration.admin.util;

import net.sf.ehcache.CacheManager;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.student.core.person.service.PersonService;
import org.kuali.student.core.person.service.PersonServiceNamespace;
import org.kuali.student.enrollment.class2.courseoffering.util.CourseOfferingResourceLoader;
import org.kuali.student.enrollment.courseoffering.service.CourseOfferingService;
import org.kuali.student.enrollment.courseregistration.service.CourseRegistrationService;
import org.kuali.student.enrollment.coursewaitlist.service.CourseWaitListService;
import org.kuali.student.r2.common.util.constants.CourseOfferingServiceConstants;
import org.kuali.student.r2.common.util.constants.CourseRegistrationServiceConstants;
import org.kuali.student.r2.common.util.constants.CourseWaitListServiceConstants;
import org.kuali.student.r2.core.acal.service.AcademicCalendarService;
import org.kuali.student.r2.core.constants.AcademicCalendarServiceConstants;
import org.kuali.student.r2.core.constants.RoomServiceConstants;
import org.kuali.student.r2.core.room.service.RoomService;
import org.kuali.student.r2.core.scheduling.constants.SchedulingServiceConstants;
import org.kuali.student.r2.core.scheduling.service.SchedulingService;

import javax.xml.namespace.QName;

/**
 * Created with IntelliJ IDEA.
 * User: Blue Team (SA)
 * Date: 17 July 2014
 * Utility Class for common auto generated reg group functions
 *
 * Class is used to make individual service calls for the Registration Application.
 */
public class AdminRegistrationUtil {

    private static PersonService personService;
    private static AcademicCalendarService academicCalendarService;
    private static IdentityService identityService;
    private static CourseRegistrationService courseRegService;
    private static CourseOfferingService courseOfferingService;
    private static CourseOfferingService courseOfferingServiceCached;
    private static SchedulingService schedulingService;
    private static RoomService roomService;
    private static CourseWaitListService courseWaitListService;

    public static PersonService getPersonService() {
        if (personService == null) {
            personService = (PersonService) GlobalResourceLoader.getService(new QName(PersonServiceNamespace.NAMESPACE, PersonServiceNamespace.SERVICE_NAME_LOCAL_PART));
        }
        return personService;
    }

    public static AcademicCalendarService getAcademicCalendarService() {
        if (academicCalendarService == null) {
            academicCalendarService = (AcademicCalendarService) GlobalResourceLoader.getService(new QName(AcademicCalendarServiceConstants.NAMESPACE, AcademicCalendarServiceConstants.SERVICE_NAME_LOCAL_PART));
        }
        return academicCalendarService;
    }

    public static IdentityService getIdentityService() {
        if (identityService == null) {
            identityService = KimApiServiceLocator.getIdentityService();
        }
        return identityService;
    }

    public static CourseRegistrationService getCourseRegistrationService() {
        if (courseRegService == null) {
            courseRegService = (CourseRegistrationService) GlobalResourceLoader.getService(new QName(CourseRegistrationServiceConstants.NAMESPACE, CourseRegistrationServiceConstants.SERVICE_NAME_LOCAL_PART));
        }
        return courseRegService;
    }

    public static CourseOfferingService getCourseOfferingService() {
        if (courseOfferingService == null) {
            courseOfferingService = (CourseOfferingService) GlobalResourceLoader.getService(new QName(CourseOfferingServiceConstants.NAMESPACE, CourseOfferingServiceConstants.SERVICE_NAME_LOCAL_PART));
        }
        return courseOfferingService;
    }

    public static CourseOfferingService getCourseOfferingServiceCached() {
        if (courseOfferingServiceCached == null) {
            courseOfferingServiceCached = (CourseOfferingService) GlobalResourceLoader.getService(new QName(CourseOfferingServiceConstants.NAMESPACE, CourseOfferingServiceConstants.CACHED_SERVICE_NAME_LOCAL_PART));
        }
        return courseOfferingServiceCached;
    }

    public static CourseWaitListService getCourseWaitlistService() {
        if (courseWaitListService == null) {
            courseWaitListService = (CourseWaitListService) GlobalResourceLoader.getService(new QName(CourseWaitListServiceConstants.NAMESPACE, CourseWaitListServiceConstants.SERVICE_NAME_LOCAL_PART));
        }
        return courseWaitListService;
    }

    public static SchedulingService getSchedulingService() {
        if (schedulingService == null) {
            schedulingService = (SchedulingService) GlobalResourceLoader.getService(new QName(SchedulingServiceConstants.NAMESPACE, SchedulingServiceConstants.SERVICE_NAME_LOCAL_PART));
        }
        return schedulingService;
    }

    public static RoomService getRoomService() {
        if (roomService == null) {
            roomService = (RoomService) GlobalResourceLoader.getService(new QName(RoomServiceConstants.NAMESPACE, RoomServiceConstants.SERVICE_NAME_LOCAL_PART));
        }
        return roomService;
    }
}
