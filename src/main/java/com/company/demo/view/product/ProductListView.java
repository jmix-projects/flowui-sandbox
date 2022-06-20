package com.company.demo.view.product;

import com.company.demo.entity.Product;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.screen.*;

@Route(value = "products", layout = MainView.class)
@UiController("Product.browse")
@UiDescriptor("product-list-view.xml")
@LookupComponent("productsTable")
@DialogMode(width = "800px", height = "600px")
public class ProductListView extends StandardLookup<Product> {
}