/**
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
 *
 * Created by pauldanielrichardson on 6/17/14
 */
package org.kuali.student.enrollment.registration.client.service.impl.util;

import org.joda.time.DateTime;
import org.kuali.student.enrollment.registration.client.service.dto.UserDateResult;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.util.date.DateFormatters;
import org.kuali.student.r2.common.util.date.KSDateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is a utility for storing and retrieving static user dates in
 * memory.
 *
 * Expected date format is yyyy-MM-dd@HH:mm
 *
 * @author Kuali Student Team
 */
public class StaticUserDateUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger(StaticUserDateUtil.class);

    private static final Map<String, DateTime> STATIC_DATE_MAP = new ConcurrentHashMap<String, DateTime>();
    private static final Map<String, DateTime> PRE_LOADED_MAP = new ConcurrentHashMap<String, DateTime>();
    private static final KSDateTimeFormatter DATE_TIME_FORMATTER = DateFormatters.SIMPLE_DATE_TIME_FORMATTER;

    private StaticUserDateUtil() {

    }

    /**
     * Add/Update the userId / date to the map.
     * @param userId the user id we are adding (key)
     * @param date the date associated
     * @throws InvalidParameterException
     */
    public static void putDateInMap(String userId, String date) throws InvalidParameterException {
        if (userId == null) {
            throw new InvalidParameterException("Cannot put date in map, userId is null");
        }
        DateTime dateTime = parseString(date);
        STATIC_DATE_MAP.put(userId, dateTime);
    }

    /**
     * Get the static date for the specified user.
     * @param userId user id
     * @return the user and date specified
     * @throws InvalidParameterException
     */
    public static UserDateResult getDateFromMap(String userId) throws InvalidParameterException {
        if (userId == null) {
            throw new InvalidParameterException("Cannot get date from map, userId is null");
        }
        String date = null;
        DateTime dateTime = STATIC_DATE_MAP.get(userId);
        if (dateTime != null) {
            date = formatDate(dateTime);
        }
        return new UserDateResult(userId, date);
    }

    /**
     * Remove the specified user from the map.
     * @param userId user id
     * @throws InvalidParameterException
     */
    public static void removeDateFromMap(String userId) throws InvalidParameterException {
        if (userId == null) {
            throw new InvalidParameterException("Cannot remove date from map, userId is null");
        }
        STATIC_DATE_MAP.remove(userId);
    }

    /**
     * Get all user/dates on the map.
     * @return the full map of user/dates
     */
    public static List<UserDateResult> getDatesFromMap() {
        List<UserDateResult> userDateResults = new ArrayList<UserDateResult>();
        for (Map.Entry<String, DateTime> entry:STATIC_DATE_MAP.entrySet()) {
            userDateResults.add(new UserDateResult(entry.getKey(), formatDate(entry.getValue())));
        }
        return userDateResults;
    }

    /**
     * Helper method for clearing out the whole map.
     */
    public static void clearMap() {
        STATIC_DATE_MAP.clear();
    }

    /**
     * Helper method for loading the map
     */
    public static void loadMap(Map<String, String> regUserMap) {
        if (regUserMap != null) {
            for (Map.Entry<String, String> entry:regUserMap.entrySet()) {
                try {
                    String key=entry.getKey();
                    DateTime value=parseString(entry.getValue());
                    STATIC_DATE_MAP.put(key, value);
                    PRE_LOADED_MAP.put(key, value);
                } catch (InvalidParameterException ex) {
                    LOGGER.warn("Unable to parse date: {}", entry.getValue(), ex);
                }
            }
        }
    }

    /**
     * Resets the map to only have the pre-loaded values
     */
    public static void resetMap() {
        STATIC_DATE_MAP.clear();
        STATIC_DATE_MAP.putAll(PRE_LOADED_MAP);
    }

    private static DateTime parseString(String dateString) throws InvalidParameterException {
        DateTime dateTime;
        try {
            dateTime = new DateTime(DATE_TIME_FORMATTER.parse(dateString));
        } catch (IllegalArgumentException ex) {
            throw new InvalidParameterException(ex.getMessage(), ex);
        }
        return dateTime;
    }

    private static String formatDate(DateTime dateTime) {
        return DATE_TIME_FORMATTER.format(dateTime);
    }
}
