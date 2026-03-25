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
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
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
        long paidOrders = orderService.count(new LambdaQueryWrapper<Order>().eq(Order::getPaymentStatus, 1));
        long afterSaleCount = afterSaleService.count();
        long afterSalePending = afterSaleService.count(new LambdaQueryWrapper<AfterSale>().eq(AfterSale::getStatus, 0));

        List<Order> completedOrderList = orderService.list(new LambdaQueryWrapper<Order>().eq(Order::getOrderStatus, 40));
        BigDecimal revenue = completedOrderList.stream()
                .map(Order::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalScore = completedOrderList.stream()
                .map(Order::getRatingScore)
                .filter(score -> score != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgScore = completedOrders == 0
                ? BigDecimal.ZERO
                : totalScore.divide(BigDecimal.valueOf(completedOrders), 2, RoundingMode.HALF_UP);

        data.put("customerCount", customerCount);
        data.put("proCount", proCount);
        data.put("totalOrders", totalOrders);
        data.put("completedOrders", completedOrders);
        data.put("paidOrders", paidOrders);
        data.put("totalRevenue", revenue);
        data.put("afterSaleCount", afterSaleCount);
        data.put("afterSalePending", afterSalePending);
        data.put("satisfaction", avgScore);
        data.put("paymentConversion", totalOrders == 0 ? 0 : Math.round((double) paidOrders * 10000 / totalOrders) / 100.0);

        data.put("trendDates", List.of("UNPAID", "PENDING", "ACCEPTED", "IN_SERVICE", "WAIT_CONFIRM", "COMPLETED"));
        data.put("trendData", List.of(
                orderService.count(new LambdaQueryWrapper<Order>().eq(Order::getPaymentStatus, 0)),
                orderService.count(new LambdaQueryWrapper<Order>().eq(Order::getOrderStatus, 10).eq(Order::getPaymentStatus, 1)),
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
        return Result.success(data);
    }

    @GetMapping("/export/orders")
    public void exportOrders(HttpServletResponse response) throws IOException {
        SecurityUtils.requireRole(1);
        List<Order> orders = orderService.list(new LambdaQueryWrapper<Order>().orderByDesc(Order::getCreateTime));
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" +
                URLEncoder.encode("orders-" + LocalDate.now() + ".csv", StandardCharsets.UTF_8));
        StringBuilder csv = new StringBuilder();
        csv.append("orderNo,customerId,professionalId,orderStatus,paymentStatus,paymentMethod,totalAmount,createTime,completeTime\n");
        for (Order order : orders) {
            csv.append(safe(order.getOrderNo())).append(',')
                    .append(safe(order.getCustomerId())).append(',')
                    .append(safe(order.getProfessionalId())).append(',')
                    .append(safe(order.getOrderStatus())).append(',')
                    .append(safe(order.getPaymentStatus())).append(',')
                    .append(safe(order.getPaymentMethod())).append(',')
                    .append(safe(order.getTotalAmount())).append(',')
                    .append(safe(order.getCreateTime())).append(',')
                    .append(safe(order.getCompleteTime())).append('\n');
        }
        response.getWriter().write('\ufeff' + csv.toString());
    }

    @GetMapping("/export/after-sale")
    public void exportAfterSale(HttpServletResponse response) throws IOException {
        SecurityUtils.requireRole(1);
        List<AfterSale> list = afterSaleService.list(new LambdaQueryWrapper<AfterSale>().orderByDesc(AfterSale::getCreateTime));
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" +
                URLEncoder.encode("after-sale-" + LocalDate.now() + ".csv", StandardCharsets.UTF_8));
        StringBuilder csv = new StringBuilder();
        csv.append("id,orderId,customerId,status,issueDescription,handleResult,createTime\n");
        for (AfterSale item : list) {
            csv.append(safe(item.getId())).append(',')
                    .append(safe(item.getOrderId())).append(',')
                    .append(safe(item.getCustomerId())).append(',')
                    .append(safe(item.getStatus())).append(',')
                    .append(escapeCsv(item.getFeedbackContent())).append(',')
                    .append(escapeCsv(item.getHandleRemark())).append(',')
                    .append(safe(item.getCreateTime())).append('\n');
        }
        response.getWriter().write('\ufeff' + csv.toString());
    }

    private String safe(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        return '"' + value.replace("\"", "\"\"") + '"';
    }
}
