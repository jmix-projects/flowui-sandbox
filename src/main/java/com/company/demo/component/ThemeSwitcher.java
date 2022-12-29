package com.company.demo.component;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.Lumo;

import javax.annotation.Nullable;

@Tag("theme-switcher")
@JsModule("./src/theme-switcher/theme-switcher.js")
public class ThemeSwitcher extends Component implements Focusable<ThemeSwitcher>, HasStyle, HasTheme, HasSize {

    private final Variants variants;
    private Icon icon;

    public ThemeSwitcher() {
        // TODO: gg, localized message
        this(new ThemeVariant(Lumo.LIGHT, null, "Change to " + Lumo.DARK + " theme"),
                new ThemeVariant(Lumo.DARK, null, "Change to " + Lumo.LIGHT + " theme"));
    }

    public ThemeSwitcher(ThemeVariant first, ThemeVariant second) {
        variants = new Variants(first, second);

        updateState(variants.getCurrent(), true);

        getElement().addEventListener("click", this::handleClick);
    }

    private void updateState(ThemeVariant variant, boolean useDefaultIcon) {
        if (variant.getIcon() != null) {
            setIcon(variant.getIcon());
        } else if (useDefaultIcon) {
            setIcon(getDefaultIcon());
        }

        updateTitle(variant.getTooltip());
    }

    public void setIcon(Icon icon) {
        if (this.icon != null) {
            remove(this.icon);
        }

        this.icon = icon;

        add(icon);
    }

    private void handleClick(DomEvent event) {
        variants.swap();
        ThemeVariant current = variants.getCurrent();

        UI ui = getUI().orElse(UI.getCurrent());
        ui.getPage().executeJs("document.documentElement.setAttribute('theme', $0)", current.getName());

        updateState(current, false);

        getEventBus().fireEvent(new ThemeSwitchEvent(this, true, current.name));
    }

    private void updateTitle(@Nullable String message) {
        if (Strings.isNullOrEmpty(message)) {
            getElement().removeAttribute("title");
            // TODO: gg, localized message
            getElement().setAttribute("aria-label", "Theme Switcher");
        } else {
            getElement().setProperty("title", message);
            getElement().setAttribute("aria-label", message);
        }
    }

    private void add(Component component) {
        getElement().appendChild(component.getElement());
    }

    private void remove(Component component) {
        getElement().removeChild(component.getElement());
    }

    private Icon getDefaultIcon() {
        Icon icon = VaadinIcon.ADJUST.create();
        icon.getElement().getStyle().set("rotate", "180deg");
        return icon;
    }

    public Registration addThemeSwitchListener(ComponentEventListener<ThemeSwitchEvent> listener) {
        return getEventBus().addListener(ThemeSwitchEvent.class, listener);
    }

    public static class ThemeSwitchEvent extends ComponentEvent<ThemeSwitcher> {

        private final String selectedTheme;

        /**
         * ThemeSwitchEvent base constructor.
         *
         * @param source     the source component
         * @param fromClient <code>true</code> if the event originated from the client
         *                   side, <code>false</code> otherwise
         * @see ComponentEvent
         */
        public ThemeSwitchEvent(ThemeSwitcher source, boolean fromClient, String selectedTheme) {
            super(source, fromClient);
            this.selectedTheme = selectedTheme;
        }

        public String getSelectedTheme() {
            return selectedTheme;
        }
    }

    public static class ThemeVariant {

        private final String name;
        private final Icon icon;
        private final String tooltip;

        public ThemeVariant(String name, @Nullable Icon icon, @Nullable String tooltip) {
            this.name = name;
            this.icon = icon;
            this.tooltip = tooltip;
        }

        public String getName() {
            return name;
        }

        @Nullable
        public Icon getIcon() {
            return icon;
        }

        @Nullable
        public String getTooltip() {
            return tooltip;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ThemeVariant that = (ThemeVariant) o;
            return Objects.equal(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
    }

    private static class Variants {

        private final ThemeVariant first;
        private final ThemeVariant second;

        private ThemeVariant current;

        public Variants(ThemeVariant first, ThemeVariant second) {
            this.first = first;
            this.second = second;

            this.current = first;
        }

        public void setCurrent(String name) {
            this.current = first.getName().equals(name) ? first : second;
        }

        public ThemeVariant getCurrent() {
            return current;
        }

        public ThemeVariant getOpposite() {
            return first.equals(current) ? second : first;
        }

        public void swap() {
            current = getOpposite();
        }
    }
}
