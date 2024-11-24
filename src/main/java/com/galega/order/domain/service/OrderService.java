package com.galega.order.domain.service;

import com.galega.order.adapters.out.database.postgres.OrderRepository;
import com.galega.order.domain.entity.Order;
import com.galega.order.domain.entity.OrderFilters;
import com.galega.order.domain.entity.OrderHistory;
import com.galega.order.domain.enums.OrderStatusEnum;
import com.galega.order.domain.enums.PaymentStatusEnum;
import com.galega.order.domain.exception.EntityNotFoundException;
import com.galega.order.domain.exception.OrderAlreadyWithStatusException;
import com.galega.order.domain.repository.OrderRepositoryPort;
import com.galega.order.domain.usecase.IOrderUseCase;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.galega.order.domain.enums.OrderStatusEnum.*;
import static java.math.RoundingMode.HALF_EVEN;

@Service
public class OrderService implements IOrderUseCase{

	OrderRepositoryPort IOrderRepository;

	public OrderService(DataSource dataSource) {
		this.IOrderRepository = new OrderRepository(dataSource);
	}

	/**
	 * Gets ALL orders stored at the database
	 * @param filters: database filter queries
	 * @return: the filtered list of orders
	 */
	@Override
	public List<Order> getAll(OrderFilters filters)
	{
		var orders = IOrderRepository.getAll(filters);

		for(Order order : orders) {
			var waitTime = calculateWaitTime(order);
			order.setWaitingTimeInSeconds(waitTime);
		}

		return orders;
	}

	/**
	 * Gets the orders using default hierarchy:
	 * 1. READY_TO_DELIVERY > IN_PREPARATION > RECEIVED
	 * 2. Older orders first
	 * 3. Finished orders should NOT be present
	 * @return: the filtered list of orders
	 */
	@Override
	public List<Order> getDefaultListOrders(){
		var orders = IOrderRepository.getAll(null);
		// List with only READY_TO_DELIVERY, IN_PREPARATION and RECEIVED status
		List<Order> cleanList = orders.stream().filter(i -> i.getStatus().isDefaultListStatus()).toList();

		// Separating lists by status
		List<Order> ordersReady = filterListByStatus(cleanList, READY_TO_DELIVERY);
		List<Order> ordersInPreparation = filterListByStatus(cleanList, OrderStatusEnum.IN_PREPARATION);
		List<Order> ordersReceived = filterListByStatus(cleanList, OrderStatusEnum.RECEIVED);

		List<Order> ordersFinalList = new ArrayList<>();
		ordersFinalList.addAll(ordersReady);
		ordersFinalList.addAll(ordersInPreparation);
		ordersFinalList.addAll(ordersReceived);

		return ordersFinalList;
	}

	/**
	 * Filter a list of orders by the orders status
	 * @param orders the list of orders that will be filtered
	 * @param status the status to be filtered
	 * @return the list of orders filtered by the status
	 */
	private List<Order> filterListByStatus(List<Order> orders, OrderStatusEnum status){
		return orders.stream().filter(i -> i.getStatus().equals(status)).toList();
	}

	/**
	 * Calculate the time in seconds for each order in the list
	 * @param orders the list of orders that will have the time calculated
	 * @return the list of orders with time calculated in seconds
	 */
	private List<Order> calculateOrdersWaitTime(List<Order> orders){

		if(!orders.isEmpty()) {
			for (Order order : orders) {
				var waitTime = calculateWaitTime(order);
				order.setWaitingTimeInSeconds(waitTime);
			}
		}

		return orders;
	}

	/**
	 * Get all the data from an order, with products and history
	 * @param id: the oder ID
	 * @return: The order found in database
	 * @throws EntityNotFoundException
	 */
	@Override
	public Order get(UUID id) throws EntityNotFoundException{
		var order = IOrderRepository.getByIdWithProducts(id);

		if(order == null) throw new EntityNotFoundException("Order", id);

		var history = IOrderRepository.getOrderHistoryByOrderId(id);

		order.setWaitingTimeInSeconds(calculateWaitTime(order));
		order.setHistory(history);

		return order;
	}

	/**
	 * Create a new order with RECEIVED status
	 * @param order The basic order data to create and storage at database
	 * @return the created order object or null, in case of error
	 */
	@Override
	public Order create(Order order) throws IllegalArgumentException{

		// The order must have at least one product to be created
		if(order.getProducts() == null || order.getProducts().isEmpty())
			throw new IllegalArgumentException("Order must have at least one product");

		// Sum all products and multiply its quantities to generate the order amount
		BigDecimal orderAmount = order.getProducts()
				.stream()
				.map(item -> item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add)
				.setScale(2, HALF_EVEN);

		// Create final order object
		order.setId(UUID.randomUUID());
		order.setAmount(orderAmount);
		order.setStatus(CREATED);
		order.setCreatedAt(LocalDateTime.now());

		// Successfully created all data in database
		if(IOrderRepository.create(order) == 1)
			return order;

		else return null;
	}

	/**
	 * Gets all the order's history, with status changes registers
	 * @param id The Order's ID
	 * @return all order's history with status update
	 * @throws EntityNotFoundException
	 */
	@Override
	public List<OrderHistory> getOrderHistory(UUID id) throws EntityNotFoundException{
		var history = IOrderRepository.getOrderHistoryByOrderId(id);
		if(history == null) throw new EntityNotFoundException("Order", id);

		return history;
	}

	/**
	 * Updates the order's status
	 * @param id The order ID
	 * @param status The new order Status
	 * @return true if updated successfully, false otherwise
	 * @throws OrderAlreadyWithStatusException
	 */

	@Override
	public boolean updateStatus(UUID id, OrderStatusEnum status) throws OrderAlreadyWithStatusException, EntityNotFoundException{
		var order = IOrderRepository.getById(id);

		// Order Not Found in Database
		if(order == null) throw new EntityNotFoundException("Order", id);

		switch (status) {
			// Invalid Status from Payload
			case null: {
				throw new IllegalArgumentException("Status cannot be null and must be a valid status: CREATED, " +
						"RECEIVED, IN_PREPARATION, READY_TO_DELIVERY, CANCELED or FINISHED");
			}

			// Forbidden route usage
			case RECEIVED: {
				throw new IllegalArgumentException("To update to this status use payment route");
			}

			// RECEIVED -> IN_PREPARATION Validation
			case IN_PREPARATION: {
				if(order.getStatus() != RECEIVED)
					throw new IllegalArgumentException("Order must be in 'RECEIVED' status");
				break;
			}

			// IN_PREPARATION -> READY_TO_DELIVERY Validation
			case READY_TO_DELIVERY: {
				if(order.getStatus() != IN_PREPARATION)
					throw new IllegalArgumentException("Order must be in 'IN_PREPARATION' status");
				break;
			}

			// READY_TO_DELIVERY -> FINISHED Validation
			case FINISHED: {
				if(order.getStatus() != READY_TO_DELIVERY)
					throw new IllegalArgumentException("Order must be in 'READY_TO_DELIVERY' status");
				break;
			}

			// CANCELED not update status
			case CREATED: {
				if(order.getStatus() != CREATED)
					throw new IllegalArgumentException("Not possible to change the status of an order to 'CREATED'.");
				break;
			}

			// FINISHED not update status
			case CANCELED: {
				if(order.getStatus() == FINISHED)
					throw new IllegalArgumentException("Not possible to change the status of an order to 'FINISHED'.");
				break;
			}

			default: {
				break;
			}
		}

		// Order with the same status
		if(order.getStatus().equals(status))
			throw new OrderAlreadyWithStatusException(id, status);

		return IOrderRepository.updateStatus(order, status, order.getStatus()) == 2;
	}

	@Override
	public boolean processOrderPayment(UUID orderId, PaymentStatusEnum paymentStatusEnum) throws OrderAlreadyWithStatusException, EntityNotFoundException {
		var wasOrderPaid = paymentStatusEnum == PaymentStatusEnum.APPROVED;
		if (wasOrderPaid){
			updateStatus(orderId, RECEIVED);
			return true;
		}
		return false;
	}


	/**
	 * Calculate the total of time in seconds that the order was created until now
	 * @param order: The Oder that will have the time waiting calculated
	 * @return the total time of wait in seconds
	 */
	private long calculateWaitTime(Order order){
		OrderStatusEnum status = order.getStatus();

		// Invalid states to count waiting time
		if(status.equals(CREATED)
				|| status.equals(FINISHED)
				|| status.equals(OrderStatusEnum.CANCELED))
			return 0;

		// Error retrieving paid at date
		if(order.getPaidAt() == null)
			return 0;

		return Duration.between(order.getPaidAt(), LocalDateTime.now()).toSeconds();
	}
}