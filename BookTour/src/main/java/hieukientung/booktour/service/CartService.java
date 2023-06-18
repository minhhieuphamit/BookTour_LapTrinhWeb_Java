package hieukientung.booktour.service;

import hieukientung.booktour.model.*;
import hieukientung.booktour.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@SessionScope
public class CartService {
    private List<CartItem> cartItems = new ArrayList<>();
    public List<CartItem> getCartItems() {
        return cartItems;
    }
    public void clearCart() {
        cartItems.clear();
    }

    public void addToCart(Tour tour) {

        CartItem findCart = cartItems.stream()
                .filter(item -> item.getId().equals(tour.getId()))
                .findFirst().orElse(null);
        if(findCart != null)
        {
            findCart.setQuantity(findCart.getQuantity()+1);
        }
        else
        {
            System.out.print("case item = null");
            findCart = new CartItem();
            findCart.setQuantity(1);

            findCart.setId(tour.getId());
            findCart.setName(tour.getTourName());
            findCart.setImage(tour.getImage());
            findCart.setPrice(tour.getPrice());

            cartItems.add(findCart);
        }
    }

    public void updateCartItem(Long productId, int quantity) {
        CartItem findCart = cartItems.stream()
                .filter(item -> item.getId().equals(productId))
                .findFirst().orElse(null);
        if(findCart != null)
        {
            findCart.setQuantity(quantity);
        }
    }

    public void removeFromCart(Long productId) {
        cartItems.removeIf(cartItem -> cartItem.getId().equals(productId));
    }


    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public void orderCart(User user) {
        // Create a new Order
        Orders order = new Orders();
        order.setOrderDate(new Date());
        order.setIsPaid(false);
        order.setUser(user);
        // Iterate over cart items and create OrderDetails
        List<OrdersDetail> orderDetails = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrdersDetail orderDetail = new OrdersDetail();

            orderDetail.setOrder(order);
            orderDetail.setPrice(cartItem.getPrice().longValue());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetails.add(orderDetail);
        }
        // Set order details in the order
        order.setOrderDetails(orderDetails);
        // Save the order to the database
        orderRepository.save(order);
        // Clear the cart
        clearCart();
    }

    public int getCartCount() {
        return cartItems.size();
    }
}
