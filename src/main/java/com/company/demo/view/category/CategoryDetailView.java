package com.company.demo.view.category;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RoutePrefix;
import io.jmix.flowui.screen.EditedEntityContainer;
import io.jmix.flowui.screen.StandardEditor;
import io.jmix.flowui.screen.UiController;
import io.jmix.flowui.screen.UiDescriptor;
import com.company.demo.view.main.MainView;
import com.company.demo.entity.Category;

@Route(value = ":id", layout = MainView.class)
@RoutePrefix("categories")
@UiController("Category.edit")
@UiDescriptor("category-detail-view.xml")
@EditedEntityContainer("categoryDc")
public class CategoryDetailView extends StandardEditor<Category> {
}