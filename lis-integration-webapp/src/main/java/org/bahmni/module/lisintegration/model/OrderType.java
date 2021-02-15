package org.bahmni.module.lisintegration.model;

import javax.persistence.*;


@Entity
@Table(name = "order_type")
public class OrderType extends BaseModel{

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name="lis_id")
    private Lis lis;

    public OrderType(int id, String name, Lis lis) {
        this.id = id;
        this.name = name;
        this.lis = lis;
    }

    public OrderType() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Lis getLis() {
        return lis;
    }

    public void setLis(Lis lis) {
        this.lis = lis;
    }
}
