package com.company.demo.view.category;

import com.company.demo.entity.Category;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.view.*;

@Route(value = "categories/:id", layout = MainView.class)
@ViewController("Category.detail")
@ViewDescriptor("category-detail-view.xml")
@EditedEntityContainer("categoryDc")
public class CategoryDetailView extends StandardDetailView<Category> {
}