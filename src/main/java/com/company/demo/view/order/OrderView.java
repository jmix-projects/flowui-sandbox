package com.company.demo.view.order;

import com.company.demo.entity.Order;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.screen.*;

@Route(value = "orders", layout = MainView.class)
@UiController("Order_.browse")
@UiDescriptor("order-view.xml")
@LookupComponent("ordersTable")
@DialogMode(width = "800px", height = "600px")
public class OrderView extends StandardLookup<Order> {
}
