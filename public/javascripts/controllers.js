var meetupApp = angular.module('meetupApp', []);

meetupApp.controller('propositionCtrl', function($scope, $http) {

    // this is what will perform search over locations.
    var locationSearcher;
    // declare the list for the locations.
    var locationList;
    // whether or not the locations typeahead should be open
    $scope.locationsOpen = false;

    // init the location input.
    $scope.locationInput = "";

    // action to take if a location option is selected.
    $scope.selectLocationOption = function(location) {
        $scope.locationInput = location.name;
        $scope.locationsOpen = false;
    };

    // get the json with the locations.
    $http.get('api/locations.json').then(function(result) {
        // set the fuse searcher.
        var options = {
          keys: ['name']
        }

        locationSearcher = new Fuse(result.data, options);

        // init the list of locations and the location search results.
        locationList = result.data;
        $scope.locationSearchResults = locationList;
    });

    // watch the location input and change the search results accordingly.
    $scope.$watch('locationInput', function(newValue, oldValue) {
        if (locationSearcher !== undefined) {
            // check to see if new value is an empty string
            if (newValue === "") {
                $scope.locationSearchResults = locationList;
            } else {
                // get search results for what someone types in.
                $scope.locationSearchResults = locationSearcher.search(newValue);
            }
        }
    });

});