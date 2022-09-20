package com.company.demo.view.category;

import com.company.demo.entity.Category;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "categories", layout = MainView.class)
@ViewController("Category.list")
@ViewDescriptor("category-list-view.xml")
@LookupComponent("categoriesTable")
@DialogMode(width = "50em", height = "37.5em")
public class CategoryListView extends StandardListView<Category> {

}