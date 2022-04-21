package cart_test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import ru.azor.api.core.CategoryDto;
import ru.azor.api.core.ProductDto;
import ru.azor.cart.integrations.CoreServiceIntegration;
import ru.azor.cart.models.Cart;
import ru.azor.cart.models.CartItem;
import ru.azor.cart.services.CartService;
import ru.azor.cart.services.CartStatisticService;

import java.math.BigDecimal;
import java.util.Set;

@SpringBootTest(classes = {CartService.class})
public class CartServiceTest {
    @Autowired
    private CartService cartService;
    @MockBean
    private CoreServiceIntegration coreServiceIntegration;
    @MockBean
    private RedisTemplate<String, Object> redisTemplate;
    @MockBean
    private CartStatisticService cartStatisticService;
    @MockBean
    private ValueOperations<String, Object> valueOperations;

    private static final String TEST_CART = "test_cart";
    private static final String USER_CART = "user_cart";
    private static final String MILK_NAME = "Milk";
    private static final String APPLE_NAME = "Apple";
    private static final String BREAD_NAME = "Bread";
    private static ProductDto milk;
    private static ProductDto apple;
    private static ProductDto bread;

    @BeforeAll
    public static void initProducts() {
        milk = new ProductDto();
        milk.setId(1L);
        milk.setTitle(MILK_NAME);
        milk.setPrice(BigDecimal.valueOf(100));
        milk.setCategories(Set.of(new CategoryDto(1L, "food")));

        apple = new ProductDto();
        apple.setId(2L);
        apple.setTitle(APPLE_NAME);
        apple.setPrice(BigDecimal.valueOf(120));
        apple.setCategories(Set.of(new CategoryDto(1L, "food")));

        bread = new ProductDto();
        bread.setId(3L);
        bread.setTitle(BREAD_NAME);
        bread.setPrice(BigDecimal.valueOf(30));
        bread.setCategories(Set.of(new CategoryDto(1L, "food")));
    }

    @BeforeEach
    public void initCart() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.doNothing().when(valueOperations).set(TEST_CART, Cart.class);
        Mockito.doReturn(new Cart()).when(valueOperations).get(TEST_CART);
        cartService.clearCart(TEST_CART);
    }

    @Test
    public void addToCartTest() {
        Mockito.doReturn(milk).when(coreServiceIntegration).findById(1L);
        Mockito.doReturn(apple).when(coreServiceIntegration).findById(2L);
        Mockito.doReturn(bread).when(coreServiceIntegration).findById(3L);
        Mockito.doNothing().when(cartStatisticService).addStatistic(milk);
        Assertions.assertEquals(MILK_NAME, coreServiceIntegration.findById(1L).getTitle());
        Assertions.assertEquals(APPLE_NAME, coreServiceIntegration.findById(2L).getTitle());
        Assertions.assertEquals(BREAD_NAME, coreServiceIntegration.findById(3L).getTitle());
        cartService.addToCart(TEST_CART, 1L);
        cartService.addToCart(TEST_CART, 2L);
        cartService.addToCart(TEST_CART, 3L);
        Assertions.assertEquals(3, cartService.getCurrentCart(TEST_CART).getItems().size());
        BigDecimal totalPrice = cartService.getCurrentCart(TEST_CART).getItems()
                .stream().map(CartItem::getPrice).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        Assertions.assertEquals(totalPrice, cartService.getCurrentCart(TEST_CART).getTotalPrice());
    }

    @Test
    public void decrementItemTest() {
        Mockito.doReturn(milk).when(coreServiceIntegration).findById(1L);
        cartService.addToCart(TEST_CART, 1L);
        cartService.addToCart(TEST_CART, 1L);
        Assertions.assertEquals(BigDecimal.valueOf(200), cartService.getCurrentCart(TEST_CART).getTotalPrice());
        cartService.decrementItem(TEST_CART, 1L);
        Assertions.assertEquals(BigDecimal.valueOf(100), cartService.getCurrentCart(TEST_CART).getTotalPrice());
        cartService.decrementItem(TEST_CART, 1L);
        Assertions.assertTrue(cartService.getCurrentCart(TEST_CART).getItems().isEmpty());
        Assertions.assertEquals(BigDecimal.ZERO, cartService.getCurrentCart(TEST_CART).getTotalPrice());
    }

    @Test
    public void clearCartTest() {
        Mockito.doReturn(milk).when(coreServiceIntegration).findById(1L);
        Mockito.doReturn(apple).when(coreServiceIntegration).findById(2L);
        Mockito.doReturn(bread).when(coreServiceIntegration).findById(3L);
        cartService.addToCart(TEST_CART, 1L);
        cartService.addToCart(TEST_CART, 2L);
        cartService.addToCart(TEST_CART, 3L);
        Assertions.assertEquals(3, cartService.getCurrentCart(TEST_CART).getItems().size());
        Assertions.assertEquals(BigDecimal.valueOf(250), cartService.getCurrentCart(TEST_CART).getTotalPrice());
        cartService.clearCart(TEST_CART);
        Assertions.assertTrue(cartService.getCurrentCart(TEST_CART).getItems().isEmpty());
        Assertions.assertEquals(BigDecimal.ZERO, cartService.getCurrentCart(TEST_CART).getTotalPrice());
    }

    @Test
    public void mergeTest() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.doNothing().when(valueOperations).set(USER_CART, Cart.class);
        Mockito.doReturn(new Cart()).when(valueOperations).get(USER_CART);
        cartService.clearCart(USER_CART);

        Mockito.doReturn(milk).when(coreServiceIntegration).findById(1L);
        Mockito.doReturn(apple).when(coreServiceIntegration).findById(2L);
        Mockito.doReturn(bread).when(coreServiceIntegration).findById(3L);
        cartService.addToCart(TEST_CART, 1L);
        cartService.addToCart(TEST_CART, 2L);
        Assertions.assertEquals(2, cartService.getCurrentCart(TEST_CART).getItems().size());
        Assertions.assertEquals(BigDecimal.valueOf(220), cartService.getCurrentCart(TEST_CART).getTotalPrice());
        cartService.addToCart(USER_CART, 3L);
        Assertions.assertEquals(1, cartService.getCurrentCart(USER_CART).getItems().size());
        Assertions.assertEquals(BigDecimal.valueOf(30), cartService.getCurrentCart(USER_CART).getTotalPrice());
        cartService.merge(USER_CART, TEST_CART);
        Assertions.assertEquals(3, cartService.getCurrentCart(USER_CART).getItems().size());
        Assertions.assertEquals(BigDecimal.valueOf(250), cartService.getCurrentCart(USER_CART).getTotalPrice());
    }

    @Test
    public void removeItemFromCartTest() {
        Mockito.doReturn(milk).when(coreServiceIntegration).findById(1L);
        Mockito.doReturn(apple).when(coreServiceIntegration).findById(2L);
        cartService.addToCart(TEST_CART, 1L);
        cartService.addToCart(TEST_CART, 1L);
        cartService.addToCart(TEST_CART, 2L);
        Assertions.assertEquals(2, cartService.getCurrentCart(TEST_CART).getItems().size());
        Assertions.assertEquals(BigDecimal.valueOf(320), cartService.getCurrentCart(TEST_CART).getTotalPrice());
        cartService.removeItemFromCart(TEST_CART, 1L);
        Assertions.assertEquals(1, cartService.getCurrentCart(TEST_CART).getItems().size());
        Assertions.assertEquals(BigDecimal.valueOf(120), cartService.getCurrentCart(TEST_CART).getTotalPrice());
    }
}
