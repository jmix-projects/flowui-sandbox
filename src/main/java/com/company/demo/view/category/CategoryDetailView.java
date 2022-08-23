package com.company.demo.view.category;

import com.company.demo.entity.Category;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.UiController;
import io.jmix.flowui.view.UiDescriptor;

@Route(value = "categories/:id", layout = MainView.class)
@UiController("Category.detail")
@UiDescriptor("category-detail-view.xml")
@EditedEntityContainer("categoryDc")
public class CategoryDetailView extends StandardDetailView<Category> {
}