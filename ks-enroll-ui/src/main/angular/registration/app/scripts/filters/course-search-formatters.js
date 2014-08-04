'use strict';

/**
 * Contains a variety of filters for use as data formatters in course search
 */
angular.module('regCartApp')
    .filter('creditRangeFormatter', function() {

        /**
         * Format an array of credit options into a range using the min & max values.
         *
         * If only 1 option exists in the array, it is returned (e.g. '1 cr').
         * Otherwise the value returned is a represented as a range (e.g. '1-3 cr').
         *
         * @param array of credit options
         * @return string
         */
        return function(creditOptions) {
            var range = '';

            if (creditOptions && angular.isArray(creditOptions)) {
                if (creditOptions.length === 1) {
                    range = parseFloat(creditOptions[0]);
                } else {
                    var min = parseFloat(Math.min.apply(null, creditOptions)),
                        max = parseFloat(Math.max.apply(null, creditOptions));

                    range = min + '-' + max;
                }

                range += ' cr';
            }

            return range;
        };

    })

    .filter('aoFormatter', ['DAY_CONSTANTS', function(DAY_CONSTANTS) {

        /**
         * Format an array of activity offerings for display using the search-list directive
         *
         * @param array of schedule components
         * @return string
         */
        return function(activityOfferings) {

            angular.forEach(activityOfferings, function(ao) {
                var scheduleComponents = ao.scheduleComponents;
                var instructors = ao.instructors;

                var days = '';                                          // days column
                var time = '';                                          // time column
                var location = '';                                      // location column
                var instructorList = '';                                // instructors column
                var seatsOpen = '';                                     // seats open column
                var additionalInfo;                                     // additional info column

                var indicator = false;                                  // determines if we show the row indicator on the left

                if (scheduleComponents && angular.isArray(scheduleComponents) && scheduleComponents.length > 0) {
                    for (var i = 0; i < scheduleComponents.length; i++) {
                        days += denullify(scheduleComponents[i].days);
                        time += denullify(scheduleComponents[i].displayTime);
                        location += denullify(scheduleComponents[i].buildingCode) + ' ' + denullify(scheduleComponents[i].roomCode);
                        if (i < (scheduleComponents.length - 1)) {
                            days += '<br />';
                            time += '<br />';
                            location += '<br />';
                        }
                    }
                    days = addSortField(days, getNumericDays(scheduleComponents[0].days));
                }

                if (instructors && angular.isArray(instructors) && instructors.length > 0) {
                    for (var j = 0; j < instructors.length; j++) {
                        instructorList += denullify(instructors[j].firstName) + ' ' + denullify(instructors[j].lastName);
                        if (j < (instructors.length - 1)) {
                            instructorList += '<br />';
                        }
                    }
                    instructorList = addSortField(instructorList, instructors[0].lastName + instructors[0].firstName);
                }

                seatsOpen += ao.seatsOpen + '/' + ao.seatsAvailable;
                if (ao.seatsOpen === 0) {
                    seatsOpen = '<span class="kscr-Search-results-no-seats">' + seatsOpen + '</span>';
                    indicator = true;
                }
                seatsOpen = addSortField(seatsOpen, zeroPad(ao.seatsOpen) + zeroPad(ao.seatsAvailable));

                var subterm = false,
                    requisites = false;

                if (ao.subterm !== null) {
                    subterm = true;
                }
                if (angular.isArray(ao.requisites) && ao.requisites.length > 0) {
                    requisites = true;
                }
                if (subterm || requisites) {
                    additionalInfo = '';
                }
                if (subterm) {
                    additionalInfo += '<span class="kscr-SearchDetails-icon--subterm" ng-click="$emit(\'showSubterm\', searchResult.subterm); $event.stopPropagation();"></span>';
                }
                if (requisites) {
                    if (!subterm) {
                        additionalInfo += '<span class="kscr-SearchDetails-icon">&nbsp;</span>';
                    }
                    additionalInfo += '<span class="kscr-SearchDetails-icon--requisites" ng-click="$emit(\'showRequisites\', searchResult.requisites); $event.stopPropagation();"></span>';
                }

                ao.formatted = {
                    days: days,
                    time: time,
                    instructor: instructorList,
                    location: location,
                    seatsOpen: seatsOpen,
                    additionalInfo: additionalInfo
                };
                ao.indicator = indicator;
            });

            return activityOfferings;
        };

        function denullify(value) {
            if (value === null) {
                return '';
            } else {
                return value;
            }
        }

        function zeroPad(value) {
            value = '' + value; // convert to string
            while (value.length < 3) {
                value = '0' + value;
            }
            return value;
        }

        function addSortField(field, sortField) {
            field = '<span class="kscr-Search-result-sort">'+sortField+"</span>"+field;
            return field;
        }

        function getNumericDays(days) {
            var dayNumArray=[];
            for (var i=0; i < DAY_CONSTANTS.dayArray.length; i++) {
                if (days.indexOf(DAY_CONSTANTS.dayArray[i]) > -1) {
                    dayNumArray.push(i);
                }
            }
            return dayNumArray.sort().toString();
        }

    }])
;

