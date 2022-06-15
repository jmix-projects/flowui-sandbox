package com.company.demo.view.order;

import com.company.demo.entity.Order;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.screen.LookupComponent;
import io.jmix.flowui.screen.StandardLookup;
import io.jmix.flowui.screen.UiController;
import io.jmix.flowui.screen.UiDescriptor;

@Route(value = "orders", layout = MainView.class)
@UiController("Order_.browse")
@UiDescriptor("order-view.xml")
@LookupComponent("ordersTable")
public class OrderView extends StandardLookup<Order> {
}
