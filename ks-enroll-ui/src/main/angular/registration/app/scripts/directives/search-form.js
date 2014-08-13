'use strict';

angular.module('regCartApp')

    /**
     * Search form directive.
     *
     * @example <div search-form=></div>
     * @example <search-form></search-form>
     * @example <div class="search-form"></div>
     */
    .directive('searchForm', [function() {
        return {
            restrict: 'ECA',
            templateUrl: 'partials/searchForm.html',
            controller: ['$scope', '$state', function($scope, $state) {

                $scope.courseSearchCriteria = '';


                function getCriteriaFromState () {
                    var criteria = null;
                    if (angular.isDefined($state.params) && angular.isDefined($state.params.searchCriteria)) {
                        criteria = $state.params.searchCriteria;
                    }

                    return criteria;
                }

                function syncSearchCriteriaWithState() {
                    var stateCriteria = getCriteriaFromState();
                    if (stateCriteria !== null) {
                        // Only update the criteria if it exists in the $state and it doesn't match the current criteria
                        if (stateCriteria !== $scope.courseSearchCriteria) {
                            $scope.courseSearchCriteria = $state.params.searchCriteria;
                        }
                    } else {
                        // Criteria doesn't exist in $state, set local criteria to an empty string.
                        $scope.courseSearchCriteria = '';
                    }
                }

                syncSearchCriteriaWithState();

                // Listen for any state changes from ui-router.
                // This will reinsert the search criteria into the form on a refresh.
                $scope.$on('$stateChangeSuccess', function() {
                    syncSearchCriteriaWithState();
                });


                $scope.submit = function() {
                    if (getCriteriaFromState() !== $scope.courseSearchCriteria) {
                        // Push the criteria into the $state if it doesn't match - don't inherit so we don't keep any previous search values
                        $state.go('root.search.results', { searchCriteria: $scope.courseSearchCriteria }, { inherit: false });
                    }
                };

            }]
        };
    }]);