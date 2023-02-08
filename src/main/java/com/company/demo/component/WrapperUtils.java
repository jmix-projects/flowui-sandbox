package com.company.demo.component;

import com.vaadin.flow.component.formlayout.FormLayout;
import io.jmix.core.annotation.Internal;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Internal
public class WrapperUtils {

    private WrapperUtils() {
    }

    public static List<FormLayout.ResponsiveStep> convertToFormLayoutResponsiveStep(
            List<SupportsResponsiveSteps.ResponsiveStep> responsiveSteps) {
        if (CollectionUtils.isEmpty(responsiveSteps)) {
            return Collections.emptyList();
        }

        return responsiveSteps.stream()
                .map(responsiveStep ->
                        new FormLayout.ResponsiveStep(responsiveStep.getMinWidth(),
                                responsiveStep.getColumns(),
                                convertToFormLayoutLabelsPosition(responsiveStep.getLabelsPosition())))
                .collect(Collectors.toUnmodifiableList());
    }

    @Nullable
    public static FormLayout.ResponsiveStep.LabelsPosition convertToFormLayoutLabelsPosition(
            @Nullable SupportsResponsiveSteps.ResponsiveStep.LabelsPosition labelsPosition) {
        if (labelsPosition == null) {
            return null;
        }

        switch (labelsPosition) {
            case TOP:
                return FormLayout.ResponsiveStep.LabelsPosition.TOP;
            case ASIDE:
                return FormLayout.ResponsiveStep.LabelsPosition.ASIDE;
            default:
                throw new IllegalArgumentException("Unknown labels position: " + labelsPosition);
        }
    }
}
