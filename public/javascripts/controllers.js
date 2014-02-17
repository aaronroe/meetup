var meetupApp = angular.module('meetupApp', []);

meetupApp.controller('landingPageCtrl', function() {
    var list = ['music.', 'psycho linguistics.', 'politics.', 'life.', 'relationships.'];  // list of blurbs

    var txt = $('#topics');  // The container in which to render the list

    var options = {
      duration: 200,          // Time (ms) each blurb will remain on screen
      rearrangeDuration: 100, // Time (ms) a character takes to reach its position
      effect: 'fadeIn',        // Animation effect the characters use to appear
      centered: true           // Centers the text relative to its container
    }

    txt.textualizer(list, options); // textualize it!

    txt.textualizer('start'); // start
});

meetupApp.controller('propositionCtrl', function($scope, $http) {

    var closeAllResults = function() {
        $scope.namesOpen = false;
        $scope.locationsOpen = false;
        $scope.topicsOpen = false;
    }

    // this is what will perform search over locations.
    var locationSearcher;
    // declare the list for the locations.
    var locationList;
    // whether or not the locations typeahead should be open
    $scope.locationsOpen = false;
    // helper function that closes all results and opens the desired one.
    $scope.openLocationsResults = function() {
        closeAllResults();
        $scope.locationsOpen = true;
    };
    // helper function that closes the results for locations.
    $scope.closeLocationsResults = function() {
        $scope.locationsOpen = false;
    };
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

    // this is what will perform search over topics.
    var topicSearcher;
    // declare the list for the topics.
    var topicList;
    // whether or not the topics typeahead should be open
    $scope.topicsOpen = false;
    // helper function that closes all results and opens the desired one.
    $scope.openTopicsResults = function() {
        closeAllResults();
        $scope.topicsOpen = true;
    };
    // helper function that closes the results for topics.
    $scope.closeTopicsResults = function() {
        $scope.topicsOpen = false;
    };
    // init the location input.
    $scope.topicInput = "";
    // action to take if a topic option is selected.
    $scope.selectTopicOption = function(location) {
        $scope.topicInput = location.name;
        $scope.topicsOpen = false;
    };
    // get the json with the topics.
    $http.get('api/topics.json').then(function(result) {
        // set the fuse searcher.
        var options = {
          keys: ['name']
        }

        topicSearcher = new Fuse(result.data, options);

        // init the list of topics and the topic search results.
        topicList = result.data;
        $scope.topicSearchResults = topicList;
    });
    // watch the topic input and change the search results accordingly.
    $scope.$watch('topicInput', function(newValue, oldValue) {
        if (topicSearcher !== undefined) {
            // check to see if new value is an empty string
            if (newValue === "") {
                $scope.topicSearchResults = topicList;
            } else {
                // get search results for what someone types in.
                $scope.topicSearchResults = topicSearcher.search(newValue);
            }
        }
    });

});