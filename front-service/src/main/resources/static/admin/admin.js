angular.module('market-front').controller('adminController', function ($scope, $http, $location, $localStorage) {
    const contextPath = 'http://localhost:5555/core/';

    $scope.isProductFormAccessible = false;
    $scope.isUserListAccessible = false;
    $scope.isUserFormAccessible = false;
    $scope.productDto = null;
    $scope.profileDto = null;
    $scope.newCategories = new Set();
    $scope.newRoles = new Set();

    $scope.showProductForm = function () {
        if ($scope.isUserHasAdminRole()) {
            if ($scope.isProductFormAccessible === false) {
                $scope.isProductFormAccessible = true;
                $http.get(contextPath + '/api/v1/categories')
                    .then(function successCallback(response) {
                        $scope.categoriesSet = response.data;
                    });
            } else {
                $scope.isProductFormAccessible = false;
            }
        } else {
            alert('Недостаточно прав')
        }
    };

    $scope.showUserList = function () {
        if ($scope.isUserHasAdminRole()) {
            if ($scope.isUserListAccessible === false) {

                $scope.isUserListAccessible = true;

                $http.get('http://localhost:5555/auth/api/v1/profile')
                    .then(function successCallback(response) {
                        $scope.usersList = response.data;
                    });
            } else {
                $scope.isUserListAccessible = false;
            }
        } else {
            alert('Недостаточно прав')
        }
    }

    $scope.showUserForm = function () {
        if ($scope.isUserHasAdminRole()) {
            if ($scope.isUserFormAccessible === false) {

                $scope.isUserFormAccessible = true;

                $http.get('http://localhost:5555/auth/api/v1/roles')
                    .then(function successCallback(response) {
                        $scope.rolesSet = response.data;
                    });

            } else {
                $scope.isUserFormAccessible = false;
                $scope.updateProfileDto();
            }
        } else {
            alert('Недостаточно прав')
        }
    }

    $scope.goToStore = function () {
        $location.path('/store');
    }

    $scope.createProfileDto = function () {
        console.log();
    }

    $scope.updateProfileDto = function () {
        if ($scope.newRoles.size === 0) {
            document.getElementById('message2').innerHTML = 'Должна быть выбрана хотя бы одна роль';
            document.getElementById('response2').innerHTML = '';
        }
        $scope.roles = Array.from($scope.newRoles).join(',');
        $http({
            url: 'http://localhost:5555/auth/api/v1/profile',
            method: 'PUT',
            data: $scope.profileDto,
            headers: {'roles': $scope.roles}
        })
            .then(function successCallback(response) {
                if (response.data === "Пользователь обновлён") {
                    document.getElementById('response2').innerHTML = response.data;
                    $scope.productDto = null;
                    $scope.newRoles.clear();
                }
            }, function errorCallback(response) {
                document.getElementById('response2').innerHTML = response.data;
            });
    }

    $scope.createProductDto = function () {
        if ($scope.newCategories.size === 0) {
            document.getElementById('message').innerHTML = 'Должна быть выбрана хотя бы одна категория';
            document.getElementById('response').innerHTML = '';
        }
        $scope.categories = Array.from($scope.newCategories).join(',');
        $http({
            url: contextPath + '/api/v1/products',
            method: 'POST',
            data: $scope.productDto,
            headers: {'categories': $scope.categories}
        })
            .then(function successCallback(response) {
                if (response.data.value === "Новый продукт создан") {
                    document.getElementById('response').innerHTML = response.data.value;
                    $scope.productDto = null;
                    $scope.newCategories.clear();
                }
            }, function errorCallback(response) {
                document.getElementById('response').innerHTML = response.data.value;
            });
    };

    $scope.addRole = function (role) {
        if ($scope.newRoles.has(role)) {
            $scope.newRoles.delete(role);
        } else {
            $scope.newRoles.add(role);
        }
    }

    $scope.addCategory = function (category) {
        if ($scope.newCategories.has(category)) {
            $scope.newCategories.delete(category);
        } else {
            $scope.newCategories.add(category);
        }
    }

    $scope.cleanResponse = function () {
        document.getElementById('response').innerHTML = '';
    }
});