package com.company.demo.component;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * An interface used in describing the responsive layouting behavior of an implementing layout.
 */
public interface SupportsResponsiveSteps {

    /**
     * Get the list of {@link ResponsiveStep}s used to configure this layout.
     *
     * @return the list of {@link ResponsiveStep}s used to configure this layout
     * @see ResponsiveStep
     */
    List<ResponsiveStep> getResponsiveSteps();

    /**
     * Configure the responsive steps used in this layout.
     *
     * @param steps list of {@link ResponsiveStep}s to set
     * @see ResponsiveStep
     */
    void setResponsiveSteps(List<ResponsiveStep> steps);

    /**
     * Configure the responsive steps used in this layout.
     *
     * @param steps the {@link ResponsiveStep}s to set
     * @see ResponsiveStep
     */
    default void setResponsiveSteps(ResponsiveStep... steps) {
        setResponsiveSteps(Arrays.asList(steps));
    }

    class ResponsiveStep {

        private final String minWidth;
        private final int columns;
        private final LabelsPosition labelsPosition;

        /**
         * Constructs a ResponsiveStep with the given minimum width and number
         * of columns.
         *
         * @param minWidth the minimum width as a CSS string value after which this
         *                 responsive step is to be applied
         * @param columns  the number of columns the layout should have
         */
        public ResponsiveStep(String minWidth, int columns) {
            this(minWidth, columns, null);
        }

        /**
         * Constructs a ResponsiveStep with the given minimum width, number of
         * columns and label position.
         *
         * @param minWidth       the minimum width as a CSS string value after which this
         *                       responsive step is to be applied
         * @param columns        the number of columns the layout should have
         * @param labelsPosition the position where label components are to be displayed
         * @see ResponsiveStep.LabelsPosition
         */
        public ResponsiveStep(String minWidth, int columns,
                              @Nullable LabelsPosition labelsPosition) {
            this.minWidth = minWidth;
            this.columns = columns;
            this.labelsPosition = labelsPosition;
        }

        public String getMinWidth() {
            return minWidth;
        }

        public int getColumns() {
            return columns;
        }

        @Nullable
        public LabelsPosition getLabelsPosition() {
            return labelsPosition;
        }

        public enum LabelsPosition {

            /**
             * Labels are displayed on the left hand side of the wrapped
             * component.
             */
            ASIDE,

            /**
             * Labels are displayed atop the wrapped component.
             */
            TOP
        }
    }
}
