angular.module('market-front').controller('productFormController', function ($scope, $http, $location, $localStorage) {
    const contextPath = 'http://localhost:5555/core/';

    $scope.productDto = {title: '', price: 0, categories: []};
    $scope.productCategory = null;

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
        $scope.productDto.categories[0] = $scope.productCategory;
        console.log($scope.productDto);
        $http({
            url: contextPath + '/api/v1/products',
            method: 'PUT',
            data: $scope.productDto,
        })
            .then(function successCallback(response) {
                document.getElementById('updateProductResponse').innerHTML = response.data.title + ' updated';
                $scope.errors = null;
            }, function errorCallback(response) {
                $scope.errors = response.data.list;
            });
    };

    $scope.cleanResponse = function () {
        document.getElementById('updateProductResponse').innerHTML = '';
    }

    $scope.showProductForm();
});