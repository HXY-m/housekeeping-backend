package com.euler.housekeepingservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.euler.housekeepingservice.common.Result;
import com.euler.housekeepingservice.model.entity.Order;
import com.euler.housekeepingservice.model.entity.Service;
import com.euler.housekeepingservice.model.entity.User;
import com.euler.housekeepingservice.service.OrderService;
import com.euler.housekeepingservice.service.ServiceService;
import com.euler.housekeepingservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ServiceService serviceService;

    /**
     * 管理员：获取首页大盘所有统计数据
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        // 1. 顶部卡片数据统计 (利用 MyBatis-Plus 的 count() 极速查询)
        long customerCount = userService.count(new LambdaQueryWrapper<User>().eq(User::getRole, 2));
        long proCount = userService.count(new LambdaQueryWrapper<User>().eq(User::getRole, 3));
        long totalOrders = orderService.count();

        // 模拟计算流水：已完成的订单(状态40) * 假设平均单价 150元
        long completedOrders = orderService.count(new LambdaQueryWrapper<Order>().eq(Order::getOrderStatus, 40));
        long totalRevenue = completedOrders * 150;

        data.put("customerCount", customerCount);
        data.put("proCount", proCount);
        data.put("totalOrders", totalOrders);
        data.put("totalRevenue", totalRevenue);

        // 2. 折线图数据 (这里暂时写死模拟近7天，真实项目中会用复杂的 GROUP BY DATE(create_time) 语句)
        data.put("trendDates", List.of("周一", "周二", "周三", "周四", "周五", "周六", "周日"));
        data.put("trendData", List.of(2, 5, 8, 3, totalOrders, totalOrders + 2, totalOrders + 5)); // 把真实的订单量混进去做个动态效果

        // 3. 饼图数据：动态查询各个服务的真实订单分布
        List<Service> services = serviceService.list();
        List<Map<String, Object>> pieData = new ArrayList<>();

        for (Service s : services) {
            long count = orderService.count(new LambdaQueryWrapper<Order>().eq(Order::getServiceId, s.getId()));
            if (count > 0) { // 只展示有订单的服务
                Map<String, Object> map = new HashMap<>();
                map.put("name", s.getServiceName());
                map.put("value", count);
                pieData.add(map);
            }
        }
        // 如果系统刚建好还没有订单，塞个假数据防止饼图报错
        if (pieData.isEmpty()) {
            pieData.add(Map.of("name", "暂无数据", "value", 0));
        }

        data.put("pieData", pieData);

        return Result.success(data);
    }
}