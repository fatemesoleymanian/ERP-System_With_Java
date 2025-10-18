package com.example.minierp.application.dashboard;

import com.example.minierp.domain.customer.CustomerRepository;
import com.example.minierp.domain.product.ProductRepository;
import com.example.minierp.domain.sales.OrderStatus;
import com.example.minierp.domain.sales.SaleOrder;
import com.example.minierp.domain.sales.SaleOrderRepository;
import com.example.minierp.interfaces.rest.dashboard.KpiResponse;
import com.example.minierp.interfaces.rest.dashboard.LatestOrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SaleOrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public KpiResponse getDashboardKpis (){
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(today, LocalTime.MAX);

        Integer todayOrders = orderRepository.countByCreatedAtBetween(startOfDay, endOfDay);


        Integer totalInventory = productRepository.sumQuantity().orElse(0);

        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);
        List<OrderStatus> completedStatuses = List.of(OrderStatus.COMPLETED, OrderStatus.PAID);


        BigDecimal monthlyRevenue = orderRepository.sumTotalAmountByCreatedAtBetweenAndStatusIn(
                startOfMonth, endOfMonth, completedStatuses
        ).orElse(BigDecimal.ZERO);


        Integer totalCustomers = customerRepository.countByActiveTrue();

        List<SaleOrder> latestOrdersDomain = orderRepository.findTop5ByOrderByCreatedAtDesc();

        List<LatestOrderDto> latestOrders = latestOrdersDomain.stream()
                .map(LatestOrderDto::from)
                .toList();

        return new KpiResponse(
                todayOrders,
                totalInventory,
                monthlyRevenue,
                totalCustomers,
                latestOrders
        );
    }
    public record DailySales(String date, BigDecimal revenue) {}

    public List<DailySales> getLast7DaysSales() {
        // از دیروز شروع می‌کنیم و ۷ روز را محاسبه می‌کنیم
        LocalDate endDate = LocalDate.now().minusDays(1);
        List<DailySales> salesData = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = endDate.minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);

            // *نیاز به متد sumTotalAmountByCreatedAtBetweenAndStatusIn
            List<OrderStatus> completedStatuses = List.of(OrderStatus.COMPLETED, OrderStatus.PAID);
            BigDecimal dailyRevenue = orderRepository.sumTotalAmountByCreatedAtBetweenAndStatusIn(
                    start, end, completedStatuses
            ).orElse(BigDecimal.ZERO);

            salesData.add(new DailySales(date.toString(), dailyRevenue));
        }
        return salesData;
    }
}
