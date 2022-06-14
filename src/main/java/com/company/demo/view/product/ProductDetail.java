package com.company.demo.view.product;

import com.company.demo.entity.Product;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RoutePrefix;
import io.jmix.flowui.screen.EditedEntityContainer;
import io.jmix.flowui.screen.StandardEditor;
import io.jmix.flowui.screen.UiController;
import io.jmix.flowui.screen.UiDescriptor;

@Route(value = ":id", layout = MainView.class)
@RoutePrefix("products")
@UiController("Product.edit")
@UiDescriptor("product-detail.xml")
@EditedEntityContainer("productDc")
public class ProductDetail extends StandardEditor<Product> {
}
