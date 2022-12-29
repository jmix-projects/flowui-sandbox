import {html, PolymerElement} from '@polymer/polymer';
import {ElementMixin} from '@vaadin/component-base/src/element-mixin.js';
import {ActiveMixin} from "@vaadin/component-base/src/active-mixin";
import {FocusMixin} from "@vaadin/component-base/src/focus-mixin";
import {TabindexMixin} from "@vaadin/component-base/src/tabindex-mixin";
import {ThemableMixin} from '@vaadin/vaadin-themable-mixin/vaadin-themable-mixin.js';

export class ThemeSwitcher extends ActiveMixin(TabindexMixin(FocusMixin(ElementMixin(ThemableMixin(PolymerElement))))) {

    static get is() {
        return 'theme-switcher';
    }

    static get template() {
        return html`
            <style>
                :host {
                    display: inline-flex;
                    align-items: center;
                    justify-content: center;
                    box-sizing: border-box;
                    position: relative;
                    outline: none;

                    cursor: var(--lumo-clickable-cursor);

                    border-radius: var(--lumo-border-radius-m);
                    width: var(--lumo-size-l);
                    height: var(--lumo-size-l);
                    min-width: auto;
                }

                ::slotted(vaadin-icon[icon^='vaadin:']) {
                    padding: var(--lumo-space-xs);
                }

                /* For interaction states */
                :host::before,
                :host::after {
                    content: '';
                    /* We rely on the host always being relative */
                    position: absolute;
                    z-index: 1;
                    top: 0;
                    right: 0;
                    bottom: 0;
                    left: 0;
                    background-color: currentColor;
                    border-radius: inherit;
                    opacity: 0;
                    pointer-events: none;
                }

                /* Hover */

                @media (any-hover: hover) {
                    :host(:hover)::before {
                        opacity: 0.02;
                    }
                }

                /* Active */

                :host::after {
                    transition: opacity 1.4s, transform 0.1s;
                    filter: blur(8px);
                }

                :host([active])::before {
                    opacity: 0.05;
                    transition-duration: 0s;
                }

                :host([active])::after {
                    opacity: 0.1;
                    transition-duration: 0s, 0s;
                    transform: scale(0);
                }

                /* Keyboard focus */

                :host([focus-ring]) {
                    box-shadow: 0 0 0 2px var(--lumo-primary-color-50pct);
                }

            </style>

            <slot id="slot">
            </slot>
        `;
    }

    static get properties() {
        return {
            /**
             * Indicates whether the element can be focused and where it participates in sequential keyboard navigation.
             *
             * @override
             * @protected
             */
            tabindex: {
                value: 0
            }
        };
    }

    /** @protected */
    ready() {
        super.ready();

        // By default, if the user hasn't provided a custom role,
        // the role attribute is set to "button".
        if (!this.hasAttribute('role')) {
            this.setAttribute('role', 'button');
        }
    }

    /**
     * By default, `Space` is the only possible activation key for a focusable HTML element.
     * Nonetheless, the button is an exception as it can be also activated by pressing `Enter`.
     * See the "Keyboard Support" section in https://www.w3.org/TR/wai-aria-practices/examples/button/button.html.
     *
     * @protected
     * @override
     */
    get _activeKeys() {
        return ['Enter', ' '];
    }

    /**
     * Since the button component is designed on the base of the `[role=button]` attribute,
     * and doesn't have a native <button> inside, in order to be fully accessible from the keyboard,
     * it should manually fire the `click` event once an activation key is pressed,
     * as it follows from the WAI-ARIA specifications:
     * https://www.w3.org/TR/wai-aria-practices-1.1/#button
     *
     * According to the UI Events specifications,
     * the `click` event should be fired exactly on `keydown`:
     * https://www.w3.org/TR/uievents/#event-type-keydown
     *
     * @param {KeyboardEvent} event
     * @protected
     * @override
     */
    _onKeyDown(event) {
        super._onKeyDown(event);

        if (this._activeKeys.includes(event.key)) {
            event.preventDefault();

            // `DisabledMixin` overrides the standard `click()` method
            // so that it doesn't fire the `click` event when the element is disabled.
            this.click();
        }
    }
}

customElements.define(ThemeSwitcher.is, ThemeSwitcher);