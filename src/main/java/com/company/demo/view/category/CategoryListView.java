package com.company.demo.view.category;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.screen.*;
import com.company.demo.view.main.MainView;
import com.company.demo.entity.Category;

@Route(value = "categories", layout = MainView.class)
@UiController("Category.browse")
@UiDescriptor("category-list-view.xml")
@LookupComponent("categoriesTable")
@DialogMode(width = "800px", height = "600px")
public class CategoryListView extends StandardLookup<Category> {
}