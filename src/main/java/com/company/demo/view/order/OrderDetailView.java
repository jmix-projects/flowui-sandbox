package com.company.demo.view.order;

import com.company.demo.entity.Order;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.UiController;
import io.jmix.flowui.view.UiDescriptor;

@Route(value = "orders/:id", layout = MainView.class)
@UiController("Order_.detail")
@UiDescriptor("order-detail-view.xml")
@EditedEntityContainer("orderDc")
public class OrderDetailView extends StandardDetailView<Order> {
}
