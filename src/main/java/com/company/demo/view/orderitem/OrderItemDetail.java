package com.company.demo.view.orderitem;

import com.company.demo.entity.OrderItem;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RoutePrefix;
import io.jmix.flowui.screen.EditedEntityContainer;
import io.jmix.flowui.screen.StandardEditor;
import io.jmix.flowui.screen.UiController;
import io.jmix.flowui.screen.UiDescriptor;

@Route(value = ":id", layout = MainView.class)
@RoutePrefix("order-items")
@UiController("OrderItem.edit")
@UiDescriptor("order-item-detail.xml")
@EditedEntityContainer("orderItemDc")
public class OrderItemDetail extends StandardEditor<OrderItem> {
}
