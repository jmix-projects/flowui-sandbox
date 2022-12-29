package com.company.demo.view.project;

import com.company.demo.entity.Project;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "projects", layout = MainView.class)
@ViewController("Project.list")
@ViewDescriptor("project-list-view.xml")
@LookupComponent("projectsTable")
@DialogMode(width = "50em", height = "37.5em")
public class ProjectListView extends StandardListView<Project> {
}