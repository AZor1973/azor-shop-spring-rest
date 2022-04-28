angular.module('market-front').controller('productFormController', function ($scope, $http, $location, $localStorage) {
    const contextPath = 'http://localhost:5555/core/';

    $scope.productDto = null;
    $scope.newCategories = new Set();

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
                    for (let i = 0; i < $scope.productDto.categories.length; i++) {
                        document.getElementById($scope.productDto.categories[i].title).click();
                        $scope.addCategory($scope.productDto.categories[i].title);
                    }
                });
        }
    };

    $scope.setProductDto = function () {
        if ($scope.newCategories.size === 0) {
            document.getElementById('message').innerHTML = 'Должна быть выбрана хотя бы одна категория';
            document.getElementById('response').innerHTML = '';
        }
        $scope.categories = Array.from($scope.newCategories).join(',');
        $http({
            url: contextPath + '/api/v1/products',
            method: 'PUT',
            data: $scope.productDto,
            headers: {'categories': $scope.categories}
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

    $scope.showProductForm();
});