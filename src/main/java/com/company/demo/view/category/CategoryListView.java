package com.company.demo.view.category;

import com.company.demo.entity.Category;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "categories", layout = MainView.class)
@UiController("Category.list")
@UiDescriptor("category-list-view.xml")
@LookupComponent("categoriesTable")
@DialogMode(width = "50em", height = "37.5em")
public class CategoryListView extends StandardListView<Category> {

}