package com.example.FoodApp.controller;

import com.example.FoodApp.dto.AllRestaurantsDTO;
import com.example.FoodApp.dto.MenuDTO;
import com.example.FoodApp.dto.RestaurantDTO;
import com.example.FoodApp.service.Service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    // Create restaurant with optional images
    @PostMapping(value = "/create-restaurant", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestaurantDTO> createRestaurant(
            @RequestPart("restaurant") RestaurantDTO restaurantDTO,
            @RequestPart(value = "restaurantImage", required = false) MultipartFile restaurantImage,
            @RequestPart(value = "menuImages", required = false) MultipartFile[] menuImages
    ) throws Exception {
        // Call service to handle saving restaurant + images
        RestaurantDTO savedRestaurant = restaurantService.createRestaurant(restaurantDTO, restaurantImage, menuImages);
        return ResponseEntity.ok(savedRestaurant);
    }

    @PostMapping("/create")
    public ResponseEntity<RestaurantDTO> create(@RequestBody RestaurantDTO restaurantDTO){
       RestaurantDTO created= restaurantService.createRestaurant(restaurantDTO);
       return ResponseEntity.ok(created);
    }

    // Other endpoints remain the same
    @GetMapping("/get/{restaurantId}")
    public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(restaurantId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RestaurantDTO>> getAll() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @GetMapping("/all-restaurants")
    public ResponseEntity<List<AllRestaurantsDTO>> allRestaurants() {
        return ResponseEntity.ok(restaurantService.allRestaurants());
    }

    @PutMapping("/update/{restaurantId}")
    public ResponseEntity<RestaurantDTO> updateRestaurant(@PathVariable Long restaurantId,
                                                          @RequestBody RestaurantDTO restaurantDTO) {
        return ResponseEntity.ok(restaurantService.updateRestaurant(restaurantId, restaurantDTO));
    }

    @DeleteMapping("/delete/{restaurantId}")
    public ResponseEntity<String> deleteRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantService.deleteRestaurantId(restaurantId));
    }

    @GetMapping("/all-menus/{restaurantId}")
    public ResponseEntity<List<MenuDTO>> getAllMenuId(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantService.getAllMenu(restaurantId));
    }
}
