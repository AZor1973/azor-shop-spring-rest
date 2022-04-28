angular.module('market-front').controller('adminController', function ($scope, $http, $location, $localStorage) {
    const contextPath = 'http://localhost:5555/core/';

    $scope.isProductFormAccessible = false;
    $scope.productDto = null;
    $scope.newCategories = new Set();

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

    $scope.createProductDto = function () {
        if ($scope.newCategories.size === 0){
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
            .then(function successCallback (response) {
                if (response.data.value === "Новый продукт создан") {
                    document.getElementById('response').innerHTML = response.data.value;
                    $scope.productDto = null;
                }
            }, function errorCallback(response) {
                document.getElementById('response').innerHTML = response.data.value;
            });
    };

    $scope.addCategory = function (category) {
        if ($scope.newCategories.has(category)){
            $scope.newCategories.delete(category);
        }else {
            $scope.newCategories.add(category);
        }
    }

    $scope.isUserLoggedIn = function () {
        return !!$localStorage.springWebUser;
    };

    $scope.isUserHasAdminRole = function () {
        if (!$scope.isUserLoggedIn()) {
            return false;
        }
        $localStorage.springWebUser.listRoles.forEach($scope.listRoles.add, $scope.listRoles)
        return $scope.listRoles.has('ROLE_ADMIN');
    };

    $scope.cleanResponse = function () {
        document.getElementById('response').innerHTML = '';
    }
});