package com.company.demo.view.file;

import com.company.demo.entity.File;
import com.company.demo.view.main.MainView;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.FileRef;
import io.jmix.core.FileStorage;
import io.jmix.core.FileStorageLocator;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.Notifications.Type;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;

@Route(value = "files", layout = MainView.class)
@ViewController("File_.list")
@ViewDescriptor("file-list-view.xml")
@LookupComponent("filesTable")
@DialogMode(width = "50em", height = "37.5em")
public class FileListView extends StandardListView<File> {

    @ViewComponent
    private HorizontalLayout buttonsPanel;
    @ViewComponent
    private DataGrid<File> filesTable;

    @ViewComponent
    private CollectionContainer<File> filesDc;

    @Autowired
    private DataManager dataManager;
    @Autowired
    private Notifications notifications;
    @Autowired
    private FileStorageLocator fileStorageLocator;
//    @Autowired
//    protected Downloader downloader;

    private MemoryBuffer buffer;
    private Upload upload;

    @Subscribe
    public void onInit(InitEvent event) {
        buffer = new MemoryBuffer();
        upload = new Upload(buffer);
        upload.setDropAllowed(true);
        upload.addSucceededListener(this::onFileUploaded);
        buttonsPanel.addComponentAsFirst(upload);
    }

    private void onFileUploaded(SucceededEvent event) {
        String fileName = event.getFileName();
        InputStream inputStream = buffer.getInputStream();

        FileStorage fileStorage = fileStorageLocator.getDefault();
        FileRef fileRef = fileStorage.saveStream(fileName, inputStream);

        File file = dataManager.create(File.class);
        file.setFile(fileRef);

        File savedFile = dataManager.save(file);
        filesDc.getMutableItems().add(savedFile);

        upload.clearFileList();

        notifications.create("File Upload Succeeded", fileName)
                .withType(Type.SUCCESS)
                .withPosition(Position.BOTTOM_END)
                .show();
    }

    @Subscribe("filesTable.download")
    public void onFilesTableDownload(ActionPerformedEvent event) {
        File selected = filesTable.getSingleSelectedItem();
        if (selected != null) {
//            downloader.download(selected.getFile());
        }
    }
}