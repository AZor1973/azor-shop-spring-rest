angular.module('market-front').controller('adminController', function ($scope, $http, $location, $localStorage) {
    const contextCorePath = 'http://localhost:5555/core/';
    const contextAuthPath = 'http://localhost:5555/auth/';

    $scope.isProductFormAccessible = false;
    $scope.isUserListAccessible = false;
    $scope.userId = null;
    $scope.isCategoryFormAccessible = false;
    $scope.productDto = {title: '', price: 0, categories: []};
    $scope.profileDto = {id: 0, status: null, rolesDto: []};
    $scope.newRoles = new Set();
    $scope.newCategories = new Set();

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
                $scope.cleanResponse();
            }
        } else {
            alert('Недостаточно прав')
        }
    };

    $scope.showUserForm = function (currentId) {
        if ($scope.isUserHasAdminRole()) {
            if ($scope.userId === null) {
                $scope.cleanResponse();
                $scope.userId = currentId;
                $http.get(contextAuthPath + 'api/v1/roles')
                    .then(function successCallback(response) {
                        $scope.rolesSet = response.data;
                    });
            } else {
                $scope.updateProfileDto();
                $scope.userId = null;
            }
        } else {
            alert('Недостаточно прав')
        }
    };

    $scope.isUserFormAccessible = function (userId, currentId) {
        return userId === currentId;
    }

    $scope.createProductDto = function () {
        $scope.size = 0;
        $scope.productDto.categories = Array.from($scope.newCategories);
        $http({
            url: contextCorePath + 'api/v1/products',
            method: 'POST',
            data: $scope.productDto,
        })
            .then(function successCallback(response) {
                document.getElementById('newProductResponse').innerHTML = response.data.title + ' created';
                $scope.productDto = {title: '', price: 0, categories: []};
                $scope.errors = null;
                $scope.newCategories.clear();
            }, function errorCallback(response) {
                $scope.errors = response.data.list;
                $scope.productDto = {title: '', price: 0, categories: []};
                $scope.newCategories.clear();
            });
    };

    $scope.addCategory = function (category) {
        if ($scope.newCategories.has(category)) {
            $scope.newCategories.delete(category);
        } else {
            $scope.newCategories.add(category);
        }
    };

    $scope.goToStore = function () {
        $location.path('/store');
    };

    $scope.createCategoryDto = function () {
        $http({
            url: contextCorePath + 'api/v1/categories',
            method: 'POST',
            data: $scope.categoryDto,
        })
            .then(function successCallback(response) {
                document.getElementById('newCategoryResponse').innerHTML = response.data.title + ' created';
                $scope.categoryDto = null;
                $scope.errors = null;
            }, function errorCallback(response) {
                $scope.errors = response.data.list;
            });
    };

    $scope.updateProfileDto = function () {
        $scope.profileDto.id = $scope.userId;
        $scope.profileDto.rolesDto = Array.from($scope.newRoles);
        console.log($scope.profileDto);
        $http({
            url: contextAuthPath + 'api/v1/profiles',
            method: 'PUT',
            data: $scope.profileDto
        })
            .then(function successCallback(response) {
                document.getElementById('updateProfileResponse').innerHTML = 'Updated';
                $scope.profileDto = {id: 0, status: null, rolesDto: []};
                $scope.newRoles.clear();
                $scope.errors = null;
                $scope.userId = null;
                $scope.isUserListAccessible = false;
                $scope.showUserList();
            }, function errorCallback(response) {
                $scope.errors = response.data.list;
                console.log($scope.errors);
                $scope.newRoles.clear();
            });
    }


    $scope.addRole = function (role) {
        if ($scope.newRoles.has(role)) {
            $scope.newRoles.delete(role);
        } else {
            $scope.newRoles.add(role);
        }
    }

    $scope.cleanResponse = function () {
        document.getElementById('newProductResponse').innerHTML = '';
        document.getElementById('newCategoryResponse').innerHTML = '';
        document.getElementById('updateProfileResponse').innerHTML = '';
        $scope.errors = null;
    }
});