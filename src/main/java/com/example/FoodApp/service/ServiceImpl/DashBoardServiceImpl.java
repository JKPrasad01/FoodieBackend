package com.example.FoodApp.service.ServiceImpl;

import com.example.FoodApp.repository.OrderItemRepository;
import com.example.FoodApp.repository.OrderRepository;
import com.example.FoodApp.repository.RestaurantRepository;
import com.example.FoodApp.repository.UserRepository;
import com.example.FoodApp.service.Service.DashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class DashBoardServiceImpl implements DashBoardService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantRepository restaurantRepository;


    public Map<String,Long> fetchAllDetails(){
        long userCount =  userRepository.count();
        long ordersCount = orderRepository.count();
        long orderItemsCount = orderItemRepository.count();
        long restaurantCount = restaurantRepository.count();

        return Map.of("userCount",userCount,"ordersCount",ordersCount,"orderItemsCount",orderItemsCount,"restaurantCount",restaurantCount);
    }
}
