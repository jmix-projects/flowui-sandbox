package com.company.demo.view.order;

import com.company.demo.entity.Order;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RoutePrefix;
import io.jmix.flowui.screen.EditedEntityContainer;
import io.jmix.flowui.screen.StandardEditor;
import io.jmix.flowui.screen.UiController;
import io.jmix.flowui.screen.UiDescriptor;

@Route(value = ":id", layout = MainView.class)
@RoutePrefix("orders")
@UiController("Order_.edit")
@UiDescriptor("order-detail.xml")
@EditedEntityContainer("orderDc")
public class OrderDetail extends StandardEditor<Order> {
}
