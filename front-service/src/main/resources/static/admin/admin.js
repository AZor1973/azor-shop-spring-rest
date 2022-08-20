angular.module('market-front').controller('adminController', function ($scope, $http, $location, $localStorage) {
    const contextCorePath = 'http://localhost:5555/core/';
    const contextAuthPath = 'http://localhost:5555/auth/';

    $scope.isProductFormAccessible = false;
    $scope.isUserListAccessible = false;
    $scope.isUserFormAccessible = false;
    $scope.isCategoryFormAccessible = false;
    $scope.productDto = {title: '', price: 0, categories: []};
    $scope.profileDto = null;
    $scope.productCategory = null;
    $scope.newRoles = new Set();

    $scope.showProductForm = function () {
        if ($scope.isUserHasAdminRole()) {
            if ($scope.isProductFormAccessible === false) {
                $scope.isProductFormAccessible = true;
                $http.get(contextCorePath + 'api/v1/categories')
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

    $scope.showCategoryForm = function () {
        if ($scope.isUserHasAdminRole()) {
            if ($scope.isCategoryFormAccessible === false) {
                $scope.isCategoryFormAccessible = true;
            } else {
                $scope.isCategoryFormAccessible = false;
            }
        } else {
            alert('Недостаточно прав')
        }
    };

    $scope.showUserList = function () {
        if ($scope.isUserHasAdminRole()) {
            if ($scope.isUserListAccessible === false) {

                $scope.isUserListAccessible = true;

                $http.get(contextAuthPath + 'api/v1/profiles')
                    .then(function successCallback(response) {
                        $scope.usersList = response.data;
                    });
            } else {
                $scope.isUserListAccessible = false;
            }
        } else {
            alert('Недостаточно прав')
        }
    };

    $scope.showUserForm = function () {
        if ($scope.isUserHasAdminRole()) {
            if ($scope.isUserFormAccessible === false) {
                $scope.isUserFormAccessible = true;
                $http.get(contextAuthPath + 'api/v1/roles')
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
    };

    $scope.createProductDto = function () {
        console.log($scope.productDto);
        $scope.productDto.categories[0] = $scope.productCategory;
        $http({
            url: contextCorePath + 'api/v1/products',
            method: 'POST',
            data: $scope.productDto,
        })
            .then(function successCallback(response) {
                document.getElementById('newProductResponse').innerHTML = response.data.title + ' created';
                $scope.productDto = null;
                $scope.errors = null;
            }, function errorCallback(response) {
                $scope.errors = response.data.list;
            });
    };

        $scope.goToStore = function () {
        $location.path('/store');
    };

    $scope.updateProfileDto = function () {
        if ($scope.newRoles.size === 0) {
            document.getElementById('message2').innerHTML = 'Должна быть выбрана хотя бы одна роль';
            document.getElementById('response2').innerHTML = '';
        }
        $scope.roles = Array.from($scope.newRoles).join(',');
        $http({
            url: contextAuthPath + 'api/v1/profiles',
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


    $scope.createCategoryDto = function () {
        $http({
            url: contextCorePath + 'api/v1/categories',
            method: 'POST',
            data: $scope.categoryDto,
        })
            .then(function successCallback(response) {
                if (response.data.value === "Новый продукт создан") {
                    document.getElementById('response3').innerHTML = response.data.value;
                    $scope.categoryDto = null;
                }
            }, function errorCallback(response) {
                document.getElementById('response3').innerHTML = response.data.value;
            });
    };

    $scope.addRole = function (role) {
        if ($scope.newRoles.has(role)) {
            $scope.newRoles.delete(role);
        } else {
            $scope.newRoles.add(role);
        }
    }

    $scope.cleanResponse = function () {
        document.getElementById('successResponse').innerHTML = '';
    }
});