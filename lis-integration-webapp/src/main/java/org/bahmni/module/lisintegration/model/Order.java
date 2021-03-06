package org.bahmni.module.lisintegration.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "test_order")
public class Order extends BaseModel {

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_type_id", nullable = false)
    private OrderType orderType;

    @Column(name = "placer_order_uuid", unique = true, nullable = false)
    private String placerOrderUuid;

    @Column(name = "test_name", nullable = false)
    private String testName;

    @Column(name = "test_panel", nullable = false)
    private String testPanel;

    @Column(name = "test_uuid", unique = true, nullable = false)
    private String testUuid;

    @Column(name = "result")
    private String result;

    @Column(name = "creator")
    private String creator;

    @Column(name = "date_created")
    private Date dateCreated;

    @Column(name = "modifier")
    private String modifier;

    @Column(name = "date_modified")
    private Date dateModified;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "comment")
    private String comment;

    @Column(name = "sample")
    private String sample;

    @Column(name = "filler_order_uuid")
    private String fillerOrderUuid;

    public Order(final int id, final OrderType orderType, final String placerOrderUuid, final String testName,
            final String testPanel, final String testUuid, final String result, final String orderNumber,
            final String comment, final String fillerOrderUuid) {
        this.id = id;
        this.orderType = orderType;
        this.placerOrderUuid = placerOrderUuid;
        this.testName = testName;
        this.testPanel = testPanel;
        this.testUuid = testUuid;
        this.result = result;
        this.orderNumber = orderNumber;
        this.comment = comment;
        this.fillerOrderUuid = fillerOrderUuid;
    }

    public Order() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    /**
     * gets the placer or Bahmni uuid of the order
     *
     * @return placerOrderUuid returns the Bahmni uuid
     */
    public String getPlacerOrderUuid() {
        return placerOrderUuid;
    }

    /**
     * sets the placer or Bahmni uuid of the order
     *
     * @param placerOrderUuid represents the uuid of the placer
     */
    public void setPlacerOrderUuid(String placerOrderUuid) {
        this.placerOrderUuid = placerOrderUuid;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public void setTestPanelName(String testPanel) {
        this.testPanel = testPanel;
    }

    public String getTestUuid() {
        return testUuid;
    }

    public void setTestUuid(String testUuid) {
        this.testUuid = testUuid;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public String getFillerOrderUuid() {
        return fillerOrderUuid;
    }

    public void setFillerOrderUuid(String fillerOrderUuid) {
        this.fillerOrderUuid = fillerOrderUuid;
    }
}
