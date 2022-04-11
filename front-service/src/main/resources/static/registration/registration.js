angular.module('market-front').controller('registrationController', function ($scope, $http, $location) {
    const contextPath = 'http://localhost:5555/auth/';
    $scope.registerUser = function () {
        $http.post(contextPath + 'registration', $scope.userDto)
            .then(function (response) {
                $scope.userDto = null;
                document.getElementById('password').value = null;
                document.getElementById('response').innerHTML = response.data.value;
            });
    };

    $scope.checkPassword = function () {
        if (document.getElementById('password').value !== document.getElementById('confirm_password').value) {
            document.getElementById('message').innerHTML = 'Пароли не совпадают';
            return false;
        } else {
            document.getElementById('message').innerHTML = '';
            return true;
        }
    }
});