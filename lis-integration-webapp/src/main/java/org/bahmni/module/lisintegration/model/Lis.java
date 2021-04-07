package org.bahmni.module.lisintegration.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "lis")
public class Lis extends BaseModel {

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "ip", nullable = false)
    private String ip;

    @Column(name = "port")
    private Integer port;

    @Column(name = "timeout")
    private Integer timeout;

    public Lis(final Integer id, final String name, final String description, final String ip, final Integer port,
            final Integer timeout) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ip = ip;
        this.port = port;
        this.timeout = timeout;
    }

    public Lis() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "Lis{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", description='" + description + '\''
                + ", ip='" + ip + '\''
                + ", port=" + port
                + ", timeout=" + timeout
                + '}';
    }
}
