package com.galega.order.adapters.in.rest.controller;

import com.galega.order.adapters.in.rest.dto.CreateOrderDTO;
import com.galega.order.adapters.in.rest.dto.OrderDTO;
import com.galega.order.adapters.in.rest.dto.OrderHistoryDTO;
import com.galega.order.adapters.in.rest.dto.UpdateOrderStatusDTO;
import com.galega.order.adapters.in.rest.mapper.OrderMapper;
import com.galega.order.adapters.out.queue.sqs.handler.SQSOutHandler;
import com.galega.order.domain.entity.Order;
import com.galega.order.domain.entity.OrderFilters;
import com.galega.order.domain.enums.OrderSortFieldsEnum;
import com.galega.order.domain.enums.OrderStatusEnum;
import com.galega.order.domain.enums.SortDirectionEnum;
import com.galega.order.domain.exception.EntityNotFoundException;
import com.galega.order.domain.exception.OrderAlreadyWithStatusException;
import com.galega.order.domain.service.OrderService;
import com.galega.order.domain.usecase.IOrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Order Controller")
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private SQSOutHandler sqsOutHandler;

    @Autowired
    private IOrderUseCase iOrderUseCase;

    @Operation(
            summary = "List all orders based on query filters",
            parameters = {
                    @Parameter(name = "status", schema = @Schema(implementation = OrderStatusEnum.class)),
                    @Parameter(name = "orderBy", schema = @Schema(implementation = OrderSortFieldsEnum.class)),
                    @Parameter(name = "orderDirection", schema = @Schema(implementation = SortDirectionEnum.class)),
            })
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrders(
            @Valid @RequestParam(required = false) String status,
            @Valid @RequestParam(required = false) String orderBy,
            @Valid @RequestParam(required = false) String orderDirection
    ) {
        OrderFilters filters = new OrderFilters();
        filters.setStatus(OrderStatusEnum.fromString(status));
        filters.setOrderBy(OrderSortFieldsEnum.fromString(orderBy));
        filters.setDirection(SortDirectionEnum.fromString(orderDirection == null ? "ASC" : orderDirection));
        List<Order> orders = (orderHasNoParameters(status, orderBy, orderDirection))
                ? iOrderUseCase.getDefaultListOrders()
                : iOrderUseCase.getAll(filters);

        List<OrderDTO> ordersDTO = orders.stream()
                .map(OrderDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ordersDTO);
    }

    private boolean orderHasNoParameters(String status, String orderBy, String direction) {
        return status == null && orderBy == null && direction == null;
    }

    @Operation(summary = "Create a new Order")
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderDTO request) throws EntityNotFoundException {
        Order order = OrderMapper.toDomain(request);
        Order createdOrder = iOrderUseCase.create(order);

        if (createdOrder == null) {
            return ResponseEntity.badRequest().body(null);
        }

        var orderDTO = new OrderDTO(createdOrder);
        sqsOutHandler.sendOrderMessage(orderDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderDTO);
    }

    @Operation(summary = "Get all details of an order")
    @GetMapping("/{id}")
    public OrderDTO getOrder(@PathVariable String id) throws EntityNotFoundException {
        var order = iOrderUseCase.get(UUID.fromString(id));
        return new OrderDTO(order);
    }

    @Operation(summary = "Update the order's status")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateOrderStatusDTO request
    ) throws OrderAlreadyWithStatusException, EntityNotFoundException {
        var orderId = UUID.fromString(id);
        var status = OrderStatusEnum.fromString(request.getStatus().toUpperCase());
        boolean updated = iOrderUseCase.updateStatus(orderId, status, true);

        if (updated) return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Get the order's history with all status changes")
    @GetMapping("/{id}/history")
    public List<OrderHistoryDTO> getOrderHistory(@PathVariable UUID id) throws EntityNotFoundException {
        return iOrderUseCase.getOrderHistory(id)
                .stream()
                .map(OrderHistoryDTO::new)
                .toList();
    }
}
