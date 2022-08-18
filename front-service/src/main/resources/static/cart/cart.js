angular.module('market-front').controller('cartController', function ($scope, $http, $location, $localStorage) {
    const contextPath = 'http://localhost:5555/cart/';

    $scope.loadCart = function () {
        $http({
            url: contextPath + 'api/v1/carts/' + $localStorage.springWebGuestCartId,
            method: 'GET'
        }).then(function (response) {
            $scope.cart = response.data;
        });
    };

    $scope.disabledCheckOut = function () {
        alert("Для оформления заказа необходимо войти в учетную запись");
    }

    $scope.clearCart = function () {
        $http.get(contextPath + 'api/v1/carts/' + $localStorage.springWebGuestCartId + '/clear')
            .then(function (response) {
                $scope.loadCart();
            });
    }

    $scope.checkOut = function () {
        $http({
            url: 'http://localhost:5555/core/api/v1/orders',
            method: 'POST',
            data: $scope.orderDetails
        }).then(function successCallback(response) {
            $scope.loadCart();
            document.getElementById('successResponse').innerHTML = response.data.order_status;
            $scope.orderDetails = null
        }, function errorCallback(response) {
            $scope.loadCart();
            $scope.errors = response.data.list;
        });
    };

    $scope.decrementQuantityOfCartItem = function (productId) {
        $http.get(contextPath + 'api/v1/carts/' + $localStorage.springWebGuestCartId + '/decrement/' + productId)
            .then(function (response) {
                $scope.loadCart();
            });
    }

    $scope.incrementQuantityOfCartItem = function (productId) {
        $http.get(contextPath + 'api/v1/carts/' + $localStorage.springWebGuestCartId + '/increment/' + productId)
            .then(function (response) {
                $scope.loadCart();
            });
    }

    $scope.deleteCartItemFromCart = function (productId) {
        $http.get(contextPath + 'api/v1/carts/' + $localStorage.springWebGuestCartId + '/remove/' + productId)
            .then(function (response) {
                $scope.loadCart();
            });
    }

    $scope.cleanResponse = function () {
        document.getElementById('response').innerHTML = '';
    }

    $scope.loadCart();
});