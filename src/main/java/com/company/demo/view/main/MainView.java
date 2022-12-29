package com.company.demo.view.main;

import com.company.demo.component.ThemeSwitcher;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.app.main.StandardMainView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
@ViewController("main-view")
@ViewDescriptor("main-view.xml")
public class MainView extends StandardMainView {

    @ViewComponent
    private Header header;

    @Autowired
    private Notifications notifications;

    @Subscribe
    public void onInit(InitEvent event) {
        ThemeSwitcher themeSwitcher = new ThemeSwitcher();
        themeSwitcher.addThemeSwitchListener(this::onThemeChange);
//        themeSwitcher.setEnabled(false);
        themeSwitcher.getElement().getStyle().set("margin-inline-start", "auto");
        themeSwitcher.getElement().getStyle().set("margin-inline-end", "var(--lumo-space-m)");
        header.add(themeSwitcher);
    }

    private void onThemeChange(ThemeSwitcher.ThemeSwitchEvent event) {
        notifications.create("Selected theme: " + event.getSelectedTheme())
                .show();
    }
}
