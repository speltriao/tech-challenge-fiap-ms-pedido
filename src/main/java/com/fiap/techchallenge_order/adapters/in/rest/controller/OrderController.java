package com.fiap.techchallenge_order.adapters.in.rest.controller;


import com.fiap.techchallenge_order.adapters.in.rest.dto.CreateOrderDTO;
import com.fiap.techchallenge_order.adapters.in.rest.dto.OrderDTO;
import com.fiap.techchallenge_order.adapters.in.rest.dto.OrderHistoryDTO;
import com.fiap.techchallenge_order.domain.entity.Order;
import com.fiap.techchallenge_order.domain.entity.OrderFilters;
import com.fiap.techchallenge_order.domain.enums.OrderSortFields;
import com.fiap.techchallenge_order.domain.enums.OrderStatus;
import com.fiap.techchallenge_order.domain.enums.SortDirection;
import com.fiap.techchallenge_order.domain.exception.EntityNotFoundException;

import com.fiap.techchallenge_order.adapters.in.rest.mapper.OrderMapper;
import com.fiap.techchallenge_order.domain.service.OrderService;
import com.fiap.techchallenge_order.domain.usecase.IOrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    IOrderUseCase iOrderUseCase;

    public OrderController(DataSource dataSource) {
        this.iOrderUseCase = new OrderService(dataSource);
    }

    @Operation(
            summary = "List all orders based on query filters",
            parameters = {
                    @Parameter(name = "status", schema = @Schema(implementation = OrderStatus.class)),
                    @Parameter(name = "orderBy", schema = @Schema(implementation = OrderSortFields.class)),
                    @Parameter(name = "orderDirection", schema = @Schema(implementation = SortDirection.class)),
            })
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrders(
            @Valid @RequestParam(required = false) String status,
            @Valid @RequestParam(required = false) String orderBy,
            @Valid @RequestParam(required = false) String orderDirection
    )
    {


        OrderFilters filters = new OrderFilters();
        filters.setStatus(OrderStatus.fromString(status));
        filters.setOrderBy(OrderSortFields.fromString(orderBy));
        filters.setDirection(SortDirection.fromString(orderDirection == null ? "ASC" : orderDirection));
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

    @Operation(summary = "Get all details of an order")
    @GetMapping("/{id}")
    public OrderDTO getOrder(@PathVariable String id) throws EntityNotFoundException
    {
        var order = iOrderUseCase.get(UUID.fromString(id));
        return new OrderDTO(order);
    }

    @Operation(summary = "Get the order's history with all status changes")
    @GetMapping("/{id}/history")
    public List<OrderHistoryDTO> getOrderHistory(@PathVariable UUID id) throws EntityNotFoundException
    {
        return iOrderUseCase.getOrderHistory(id)
                .stream()
                .map(OrderHistoryDTO::new)
                .toList();
    }
    /*TODO: Permitir acesso a essa rota apenas por SQS
    @Operation(summary = "Create a new Order")
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderDTO request) {
        Order order = OrderMapper.toDomain(request);
        Order createdOrder = iOrderUseCase.create(order);

        if(createdOrder == null)
            return ResponseEntity.badRequest().body(null);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new OrderDTO(createdOrder));
    }

    TODO: Permitir acesso a essa rota apenas por SQS
    @Operation(summary = "Update the oder's status")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateOrderStatusDTO request
    ) throws OrderAlreadyWithStatusException, EntityNotFoundException {
        var oderId = UUID.fromString(id);
        var status = OrderStatus.fromString(request.getStatus().toUpperCase());
        boolean updated = iOrderUseCase.updateStatus(oderId, status);

        if(updated) return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }
    */
}
