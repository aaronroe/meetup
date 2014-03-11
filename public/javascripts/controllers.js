var meetupApp = angular.module('meetupApp', []);

meetupApp.controller('landingPageCtrl', function() {
    var list = [
        'music.',
        'linguistics.',
        'politics.',
        'life.',
        'relationships.',
        'squirrels.',
        'romance.',
        'existentialism.',
        'O-Week.',
        'technology.',
        'ethics.',
        'religion.',
        'japanese film.',
        'coffee shops.'
    ];  // list of topics

    var txt = $('#topics');  // The container in which to render the list

    var options = {
      duration: 1,          // Time (ms) each blurb will remain on screen
      rearrangeDuration: 500, // Time (ms) a character takes to reach its position
      effect: 'fadeIn',        // Animation effect the characters use to appear
      centered: true           // Centers the text relative to its container
    }

    list = shuffle(list);

    txt.textualizer(list, options); // textualize it!

    txt.textualizer('start'); // start

    //+ Jonas Raoni Soares Silva
    //@ http://jsfromhell.com/array/shuffle [v1.0]
    function shuffle(o){ //v1.0
        for(var j, x, i = o.length; i; j = Math.floor(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x);
        return o;
    };
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
    $http.get('/api/locations.json').then(function(result) {
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

    // this is what will perform search over persons.
    var personSearcher;
    // declare the list for the persons.
    var personsList;
    // whether or not the persons typeahead should be open
    $scope.personsOpen = false;
    // helper function that closes all results and opens the desired one.
    $scope.openPersonsResults = function() {
        closeAllResults();
        $scope.personsOpen = true;
    };
    // helper function that closes the results for persons.
    $scope.closePersonsResults = function() {
        $scope.personsOpen = false;
    };
    // init the persons input.
    $scope.personInput = "";
    $scope.selectedPerson = null;
    // action to take if a persons option is selected.
    $scope.selectPersonOption = function(person) {
        $scope.personOpen = false;
        $scope.selectedPerson = person;
        $scope.personInput = $scope.selectedPerson.name + " (" + $scope.selectedPerson.college + ")";
    };
    // get the json with the persons.
    $http.get('/api/students.json').then(function(result) {
        // set the fuse searcher.
        var options = {
          keys: ['name']
        }

        personSearcher = new Fuse(result.data, options);

        // change the college value to make it more simple.
        for (var i = 0; i < result.data.length; i++) {
            var student = result.data[i];
            student.college = student.college.replace(" College", "");
        };

        // init the list of persons and the person search results.
        personList = result.data;
        $scope.personSearchResults = personList;
    });
    // watch the person input and change the search results accordingly.
    $scope.$watch('personInput', function(newValue, oldValue) {
        if (personSearcher !== undefined) {
            // check to see if the text is deviating from the person they selected
            if ($scope.selectedPerson !== null && newValue !== $scope.selectedPerson.name + " (" + $scope.selectedPerson.college + ")") {
                $scope.selectedPerson = null;
            }

            // check to see if new value is an empty string
            if (newValue === "") {
                $scope.personSearchResults = personList;
            } else {
                // get search results for what someone types in.
                $scope.personSearchResults = personSearcher.search(newValue);
            }
        }
    });

    /**
     * Function called before a submit that preps the selected user field.
     */
    $scope.prepareSubmission = function() {
        $scope.submittedName = $scope.selectedPerson.name;
        $scope.submittedEmail = $scope.selectedPerson.email;
    };

});