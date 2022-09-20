package com.company.demo.view.product;

import com.company.demo.entity.Product;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "products", layout = MainView.class)
@ViewController("Product.list")
@ViewDescriptor("product-list-view.xml")
@LookupComponent("productsTable")
@DialogMode(width = "50em", height = "37.5em")
public class ProductListView extends StandardListView<Product> {
}