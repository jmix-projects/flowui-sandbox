package com.company.demo.component;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.Lumo;

//@Tag("theme-switcher")
//@JsModule("./src/theme-switcher/theme-switcher.js")
public class SimpleThemeSwitcher extends Component implements Focusable<SimpleThemeSwitcher>, HasStyle, HasTheme, HasSize {

    private String theme = Lumo.LIGHT;
    private Icon icon;

    public SimpleThemeSwitcher() {
        Icon icon = VaadinIcon.ADJUST.create();
        icon.getElement().getStyle().set("rotate", "180deg");
        setIcon(icon);
        updateTitle(getOpposite(theme));

        getElement().addEventListener("click", this::handleClick);
    }

    private String getOpposite(String variant) {
        return Lumo.LIGHT.equals(variant) ? Lumo.DARK : Lumo.LIGHT;
    }

    public void setIcon(Icon icon) {
        if (this.icon != null) {
            remove(this.icon);
        }

        this.icon = icon;

        add(icon);
    }

    private void handleClick(DomEvent event) {
        String oppositeTheme = getOpposite(theme);

        UI ui = getUI().orElse(UI.getCurrent());
        ui.getPage().executeJs("document.documentElement.setAttribute('theme', $0)", oppositeTheme);

        updateTitle(theme);

        theme = oppositeTheme;

        getEventBus().fireEvent(new ThemeSwitchEvent(this, true, theme));
    }

    private void updateTitle(String variant) {
        getElement().setAttribute("title", "Change to " + variant + " theme");
    }

    private void add(Component component) {
        getElement().appendChild(component.getElement());
    }

    private void remove(Component component) {
        getElement().removeChild(component.getElement());
    }

    public Registration addThemeSwitchListener(ComponentEventListener<ThemeSwitchEvent> listener) {
        return getEventBus().addListener(ThemeSwitchEvent.class, listener);
    }

    public static class ThemeSwitchEvent extends ComponentEvent<SimpleThemeSwitcher> {

        private final String selectedTheme;

        /**
         * ThemeSwitchEvent base constructor.
         *
         * @param source     the source component
         * @param fromClient <code>true</code> if the event originated from the client
         *                   side, <code>false</code> otherwise
         * @see ComponentEvent
         */
        public ThemeSwitchEvent(SimpleThemeSwitcher source, boolean fromClient, String selectedTheme) {
            super(source, fromClient);
            this.selectedTheme = selectedTheme;
        }

        public String getSelectedTheme() {
            return selectedTheme;
        }
    }

}
