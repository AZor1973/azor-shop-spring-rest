angular.module('market-front').controller('productFormController', function ($scope, $http, $location, $localStorage) {
    const contextPath = 'http://localhost:5555/core/';

    $scope.productDto = null;

    $scope.showProductForm = function () {
        $scope.productId = document.URL.substr(document.URL.lastIndexOf('/') + 1);
        if ($scope.isUserHasAdminRole()) {
            $http.get(contextPath + '/api/v1/categories')
                .then(function successCallback(response) {
                    $scope.categoriesSet = response.data;
                });
            $http.get(contextPath + '/api/v1/products/' + $scope.productId)
                .then(function successCallback(response) {
                    $scope.productDto = response.data;
                });
        }
    };

    $scope.setProductDto = function () {
        $http({
            url: contextPath + '/api/v1/products',
            method: 'PUT',
            data: $scope.productDto,
        })
            .then(function successCallback(response) {
                if (response.data.value === "Продукт изменён") {
                    document.getElementById('response').innerHTML = response.data.value;
                    $scope.productDto = null;
                }
            }, function errorCallback(response) {
                document.getElementById('response').innerHTML = response.data.value;
            });
    };

    $scope.cleanResponse = function () {
        document.getElementById('response').innerHTML = '';
    }

    $scope.showProductForm();
});