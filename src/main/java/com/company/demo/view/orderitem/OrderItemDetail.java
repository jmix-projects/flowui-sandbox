package com.company.demo.view.orderitem;

import com.company.demo.entity.OrderItem;
import com.company.demo.entity.Product;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RoutePrefix;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.data.options.ContainerOptions;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.screen.*;

@Route(value = ":id", layout = MainView.class)
@RoutePrefix("order-items")
@UiController("OrderItem.edit")
@UiDescriptor("order-item-detail.xml")
@EditedEntityContainer("orderItemDc")
public class OrderItemDetail extends StandardEditor<OrderItem> {

    @ComponentId
    private EntityComboBox<Product> productField;

    @ComponentId
    private CollectionContainer<Product> productsDc;

    @Subscribe
    public void onInit(InitEvent event) {
        productField.setListOptions(new ContainerOptions<>(productsDc));
    }
}
