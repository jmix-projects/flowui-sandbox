package com.company.demo.view.file;

import com.company.demo.entity.File;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "files/:id", layout = MainView.class)
@ViewController("File_.detail")
@ViewDescriptor("file-detail-view.xml")
@EditedEntityContainer("fileDc")
public class FileDetailView extends StandardDetailView<File> {
}