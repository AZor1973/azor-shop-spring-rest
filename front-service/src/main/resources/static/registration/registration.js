angular.module('market-front').controller('registrationController', function ($scope, $http, $location, $localStorage) {
    const contextPath = 'http://localhost:5555/auth/';

    $scope.isRegister = false;

    $scope.registerUser = function () {
        $http.post(contextPath + 'registration', $scope.userDto)
            .then(function (response) {
                $scope.username = $scope.userDto.username;
                document.getElementById('response').innerHTML = response.data.value;
                if (response.data.value === "Новый пользователь создан") {
                    $scope.userDto = null;
                    document.getElementById('password').value = null;
                    $scope.isRegister = true;
                }
            });
    };

    $scope.confirmRegistration = function () {
        $scope.stringResponseRequestDto.username = $scope.username;
        $http.post(contextPath + 'confirm_registration', $scope.stringResponseRequestDto)
            .then(function (response) {
                $scope.stringResponseRequestDto = null;

                if (response.data.token) {
                    $http.defaults.headers.common.Authorization = 'Bearer ' + response.data.token;
                    $localStorage.springWebUser = {username: $scope.username, token: response.data.token};

                    $http.get('http://localhost:5555/cart/api/v1/cart/' + $localStorage.springWebGuestCartId + '/merge')
                        .then(function successCallback(response) {
                        });

                    $location.path('/');
                } else {
                    document.getElementById('response').innerHTML = response.data.value;
                }
            });
    }

    $scope.checkPassword = function () {
        if (document.getElementById('password').value !== document.getElementById('confirm_password').value) {
            document.getElementById('message').innerHTML = 'Пароли не совпадают';
            return false;
        } else {
            document.getElementById('message').innerHTML = '';
            return true;
        }
    }

    $scope.cleanResponse = function () {
        document.getElementById('response').innerHTML = '';
    }
});