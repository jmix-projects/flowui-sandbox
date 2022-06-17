package com.company.demo.view.category;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.screen.LookupComponent;
import io.jmix.flowui.screen.StandardLookup;
import io.jmix.flowui.screen.UiController;
import io.jmix.flowui.screen.UiDescriptor;
import com.company.demo.view.main.MainView;
import com.company.demo.entity.Category;

@Route(value = "categories", layout = MainView.class)
@UiController("Category.browse")
@UiDescriptor("category-list-view.xml")
@LookupComponent("categoriesTable")
public class CategoryListView extends StandardLookup<Category> {
}