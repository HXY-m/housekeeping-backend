package com.euler.housekeepingservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.euler.housekeepingservice.common.Result;
import com.euler.housekeepingservice.common.SecurityUtils;
import com.euler.housekeepingservice.model.entity.AfterSale;
import com.euler.housekeepingservice.model.entity.Order;
import com.euler.housekeepingservice.model.entity.Service;
import com.euler.housekeepingservice.model.entity.User;
import com.euler.housekeepingservice.service.AfterSaleService;
import com.euler.housekeepingservice.service.OrderService;
import com.euler.housekeepingservice.service.ServiceService;
import com.euler.housekeepingservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    private final UserService userService;
    private final OrderService orderService;
    private final ServiceService serviceService;
    private final AfterSaleService afterSaleService;

    public StatisticsController(UserService userService, OrderService orderService, ServiceService serviceService, AfterSaleService afterSaleService) {
        this.userService = userService;
        this.orderService = orderService;
        this.serviceService = serviceService;
        this.afterSaleService = afterSaleService;
    }

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardData() {
        SecurityUtils.requireRole(1);
        Map<String, Object> data = new HashMap<>();

        long customerCount = userService.count(new LambdaQueryWrapper<User>().eq(User::getRole, 2));
        long proCount = userService.count(new LambdaQueryWrapper<User>().eq(User::getRole, 3));
        long totalOrders = orderService.count();
        long completedOrders = orderService.count(new LambdaQueryWrapper<Order>().eq(Order::getOrderStatus, 40));
        long afterSaleCount = afterSaleService.count();

        BigDecimal revenue = orderService.list(new LambdaQueryWrapper<Order>().eq(Order::getOrderStatus, 40))
                .stream()
                .map(Order::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        data.put("customerCount", customerCount);
        data.put("proCount", proCount);
        data.put("totalOrders", totalOrders);
        data.put("totalRevenue", revenue);
        data.put("afterSaleCount", afterSaleCount);
        data.put("afterSalePending", afterSaleService.count(new LambdaQueryWrapper<AfterSale>().eq(AfterSale::getStatus, 0)));

        data.put("trendDates", List.of("PENDING", "ACCEPTED", "IN_SERVICE", "WAIT_CONFIRM", "COMPLETED"));
        data.put("trendData", List.of(
                orderService.count(new LambdaQueryWrapper<Order>().eq(Order::getOrderStatus, 10)),
                orderService.count(new LambdaQueryWrapper<Order>().eq(Order::getOrderStatus, 20)),
                orderService.count(new LambdaQueryWrapper<Order>().eq(Order::getOrderStatus, 25)),
                orderService.count(new LambdaQueryWrapper<Order>().eq(Order::getOrderStatus, 35)),
                completedOrders
        ));

        List<Service> services = serviceService.list();
        List<Map<String, Object>> pieData = new ArrayList<>();
        for (Service service : services) {
            long count = orderService.count(new LambdaQueryWrapper<Order>().eq(Order::getServiceId, service.getId()));
            if (count > 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", service.getServiceName());
                map.put("value", count);
                pieData.add(map);
            }
        }
        if (pieData.isEmpty()) {
            pieData.add(Map.of("name", "NO_DATA", "value", 0));
        }
        data.put("pieData", pieData);

        BigDecimal totalScore = orderService.list(new LambdaQueryWrapper<Order>().isNotNull(Order::getRatingScore))
                .stream()
                .map(Order::getRatingScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("satisfaction", totalScore);

        return Result.success(data);
    }
}
