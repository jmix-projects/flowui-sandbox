package com.company.demo.view.product;

import com.company.demo.entity.Product;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.screen.LookupComponent;
import io.jmix.flowui.screen.StandardLookup;
import io.jmix.flowui.screen.UiController;
import io.jmix.flowui.screen.UiDescriptor;

@Route(value = "products", layout = MainView.class)
@UiController("Product.browse")
@UiDescriptor("product-view.xml")
@LookupComponent("productsTable")
public class ProductView extends StandardLookup<Product> {
}
