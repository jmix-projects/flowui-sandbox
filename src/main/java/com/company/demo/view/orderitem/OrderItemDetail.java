package com.company.demo.view.orderitem;

import com.company.demo.entity.OrderItem;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "order-items/:id", layout = MainView.class)
@ViewController("OrderItem.detail")
@ViewDescriptor("order-item-detail-view.xml")
@EditedEntityContainer("orderItemDc")
public class OrderItemDetail extends StandardDetailView<OrderItem> {
}
