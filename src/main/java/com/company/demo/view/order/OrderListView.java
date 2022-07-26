package com.company.demo.view.order;

import com.company.demo.entity.Order;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "orders", layout = MainView.class)
@UiController("Order_.list")
@UiDescriptor("order-list-view.xml")
@LookupComponent("ordersTable")
@DialogMode(width = "50em", height = "37.5em")
public class OrderListView extends StandardListView<Order> {
}
