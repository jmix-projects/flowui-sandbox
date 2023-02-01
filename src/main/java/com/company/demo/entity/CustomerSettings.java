package com.company.demo.entity;

import io.jmix.appsettings.entity.AppSettingsEntity;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@JmixEntity
@Table(name = "CUSTOMER_SETTINGS")
@Entity
public class CustomerSettings extends AppSettingsEntity {
    @JmixGeneratedValue
    @Column(name = "UUID")
    private UUID uuid;

    @Column(name = "NOTIFICATION_TEXT")
    private String notificationText;

    @Column(name = "NOTIFICATION_TEXT_COPY")
    private String notificationTextCopy;

    @Column(name = "SALES_THRESHOLD", precision = 19, scale = 2)
    private BigDecimal salesThreshold;

    @Column(name = "SALES_THRESHOLD_COPY", precision = 19, scale = 2)
    private BigDecimal salesThresholdCopy;

    @Column(name = "DEFAULT_ORDER_STATUS")
    private String defaultOrderStatus;

    @Column(name = "DEFAULT_ORDER_STATUS_COPY")
    private String defaultOrderStatusCopy;

    @Column(name = "SETTING1")
    private String setting1;

    @Column(name = "SETTING1_COPY")
    private String setting1Copy;

    @Column(name = "SETTING2")
    private String setting2;

    @Column(name = "SETTING2_COPY")
    private String setting2Copy;

    @Column(name = "SETTING3")
    private String setting3;

    @Column(name = "SETTING3_COPY")
    private String setting3Copy;

    @Column(name = "SETTING4")
    private String setting4;

    @Column(name = "SETTING4_COPY")
    private String setting4Copy;

    @Column(name = "SETTING5")
    private String setting5;

    @Column(name = "SETTING5_COPY")
    private String setting5Copy;

    @Column(name = "SETTING6")
    private String setting6;

    @Column(name = "SETTING6_COPY")
    private String setting6Copy;

    @Column(name = "SETTING7")
    private String setting7;

    @Column(name = "SETTING7_COPY")
    private String setting7Copy;

    @Column(name = "SETTING8")
    private String setting8;

    @Column(name = "SETTING8_COPY")
    private String setting8Copy;

    public OrderStatus getDefaultOrderStatusCopy() {
        return defaultOrderStatusCopy == null ? null : OrderStatus.fromId(defaultOrderStatusCopy);
    }

    public void setDefaultOrderStatusCopy(OrderStatus defaultOrderStatusCopy) {
        this.defaultOrderStatusCopy = defaultOrderStatusCopy == null ? null : defaultOrderStatusCopy.getId();
    }

    public BigDecimal getSalesThresholdCopy() {
        return salesThresholdCopy;
    }

    public void setSalesThresholdCopy(BigDecimal salesThresholdCopy) {
        this.salesThresholdCopy = salesThresholdCopy;
    }

    public String getNotificationTextCopy() {
        return notificationTextCopy;
    }

    public void setNotificationTextCopy(String notificationTextCopy) {
        this.notificationTextCopy = notificationTextCopy;
    }

    public String getSetting1Copy() {
        return setting1Copy;
    }

    public void setSetting1Copy(String setting1Copy) {
        this.setting1Copy = setting1Copy;
    }

    public String getSetting2Copy() {
        return setting2Copy;
    }

    public void setSetting2Copy(String setting2Copy) {
        this.setting2Copy = setting2Copy;
    }

    public String getSetting3Copy() {
        return setting3Copy;
    }

    public void setSetting3Copy(String setting3Copy) {
        this.setting3Copy = setting3Copy;
    }

    public String getSetting4Copy() {
        return setting4Copy;
    }

    public void setSetting4Copy(String setting4Copy) {
        this.setting4Copy = setting4Copy;
    }

    public String getSetting5Copy() {
        return setting5Copy;
    }

    public void setSetting5Copy(String setting5Copy) {
        this.setting5Copy = setting5Copy;
    }

    public String getSetting7Copy() {
        return setting7Copy;
    }

    public void setSetting7Copy(String setting7Copy) {
        this.setting7Copy = setting7Copy;
    }

    public String getSetting8Copy() {
        return setting8Copy;
    }

    public void setSetting8Copy(String setting8Copy) {
        this.setting8Copy = setting8Copy;
    }

    public String getSetting8() {
        return setting8;
    }

    public void setSetting8(String setting8) {
        this.setting8 = setting8;
    }

    public String getSetting7() {
        return setting7;
    }

    public void setSetting7(String setting7) {
        this.setting7 = setting7;
    }

    public String getSetting6Copy() {
        return setting6Copy;
    }

    public void setSetting6Copy(String setting6Copy) {
        this.setting6Copy = setting6Copy;
    }

    public String getSetting6() {
        return setting6;
    }

    public void setSetting6(String setting6) {
        this.setting6 = setting6;
    }

    public String getSetting5() {
        return setting5;
    }

    public void setSetting5(String setting5) {
        this.setting5 = setting5;
    }

    public String getSetting4() {
        return setting4;
    }

    public void setSetting4(String setting4) {
        this.setting4 = setting4;
    }

    public String getSetting3() {
        return setting3;
    }

    public void setSetting3(String setting3) {
        this.setting3 = setting3;
    }

    public String getSetting2() {
        return setting2;
    }

    public void setSetting2(String setting2) {
        this.setting2 = setting2;
    }

    public String getSetting1() {
        return setting1;
    }

    public void setSetting1(String setting1) {
        this.setting1 = setting1;
    }

    public OrderStatus getDefaultOrderStatus() {
        return defaultOrderStatus == null ? null : OrderStatus.fromId(defaultOrderStatus);
    }

    public void setDefaultOrderStatus(OrderStatus defaultOrderStatus) {
        this.defaultOrderStatus = defaultOrderStatus == null ? null : defaultOrderStatus.getId();
    }

    public BigDecimal getSalesThreshold() {
        return salesThreshold;
    }

    public void setSalesThreshold(BigDecimal salesThreshold) {
        this.salesThreshold = salesThreshold;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}