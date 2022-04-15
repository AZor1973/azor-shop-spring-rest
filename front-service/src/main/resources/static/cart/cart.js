angular.module('market-front').controller('cartController', function ($scope, $http, $location, $localStorage) {
    const contextPath = 'http://localhost:5555/cart/';

    $scope.loadCart = function () {
        console.log("load cart")
        $http({
            url: contextPath + 'api/v1/cart/' + $localStorage.springWebGuestCartId,
            method: 'GET'
        }).then(function (response) {
            $scope.cart = response.data;
        });
    };

    $scope.disabledCheckOut = function () {
        alert("Для оформления заказа необходимо войти в учетную запись");
    }

    $scope.clearCart = function () {
        $http.get(contextPath + 'api/v1/cart/' + $localStorage.springWebGuestCartId + '/clear')
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
            console.log("log: " + response.data.value)
            $scope.loadCart();
            document.getElementById('response').innerHTML = response.data.value;
            $scope.orderDetails = null
        }, function errorCallback(response) {
            console.log("log: " + response.data.value)
            $scope.loadCart();
            document.getElementById('response').innerHTML = response.data.value;
        });
    };

    $scope.decrementQuantityOfCartItem = function (productId) {
        $http.get(contextPath + 'api/v1/cart/' + $localStorage.springWebGuestCartId + '/decrement/' + productId)
            .then(function (response) {
                $scope.loadCart();
            });
    }

    $scope.incrementQuantityOfCartItem = function (productId) {
        $http.get(contextPath + 'api/v1/cart/' + $localStorage.springWebGuestCartId + '/increment/' + productId)
            .then(function (response) {
                $scope.loadCart();
            });
    }

    $scope.deleteCartItemFromCart = function (productId) {
        $http.get(contextPath + 'api/v1/cart/' + $localStorage.springWebGuestCartId + '/remove/' + productId)
            .then(function (response) {
                $scope.loadCart();
            });
    }

    $scope.cleanResponse = function () {
        document.getElementById('response').innerHTML = '';
    }

    $scope.loadCart();
});