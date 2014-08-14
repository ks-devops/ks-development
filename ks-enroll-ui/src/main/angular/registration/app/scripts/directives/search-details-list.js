'use strict';

angular.module('regCartApp')

    /*
     * This directive is used to display a mobile view of search details. Details are shown
     * in a tabular format, which allows users to view the details that are relevant to
     * them. An "all" tab is also available for viewing all the results together.
     *
     * Event Handling
     * -- Emits: dynamic -- can be used to alert the parent that the row has been selected
     * -- Broadcasts: none
     * -- Receives: none
     *
     */
    .directive('searchDetailsList', ['$timeout', '$animate', 'DetailsFactory', function($timeout, $animate, DetailsFactory) {
        return {
            restrict: 'E',
            templateUrl: 'partials/searchDetailsList.html',
            scope: {
                searchDetails: '=',  // an array of search details to show
                selectable: '=',     // a boolean determining whether search details are selectable or not
                config: '@'          // the name of the configuration variable to dynamically inject (e.g. 'COURSE_DETAILS')
            },
            link:function(scope) {

                var detailsConfig = new DetailsFactory(scope.config);

                // reorganize the data into sections
                scope.sections=detailsConfig.getSections(scope.searchDetails);

                // turn off ng-repeat animations for better performance
                $animate.enabled(false, angular.element(document.querySelector('.kscr-Search-details-grid')));

                // initialize the tabs
                for (var i=0; i<scope.sections.length; i++) {
                    scope.sections[i].tabs = angular.copy(detailsConfig.tabs);
                    scope.sections[i].tab = angular.copy(detailsConfig.selectedTab);
                }

                // set the field options
                scope.fieldOptions = detailsConfig.fieldOptions;
                scope.icons = detailsConfig.icons;

                // this method selects a tab and sets the display flags accordingly
                scope.select = function(section, tab) {
                    section.tab = tab;
                    for (var i=0; i<section.tabs.length; i++) {
                        section.tabs[i].selected = section.tabs[i].id === tab || tab === 'all';
                    }
                };

                // if a row is selected, emit an event to the search details controller
                scope.selectRow = function(searchDetail) {
                    if (scope.selectable && !angular.isUndefined(detailsConfig.selectEvent)) {
                        scope.$emit(detailsConfig.selectEvent, searchDetail);
                    }
                };

                // Displays the details in batches for performance
                var stagger = 20;
                scope.limit = stagger;
                scope.$watch('limit', function() {
                    if (scope.limit < scope.searchDetails.length) {
                        $timeout(function() {
                            scope.limit += stagger;
                        }, 250);
                    }
                });
            }
        };
    }])

;