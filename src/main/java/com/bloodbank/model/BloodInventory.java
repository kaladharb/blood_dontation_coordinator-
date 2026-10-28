package com.bloodbank.model;

import jakarta.persistence.*;

@Entity
@Table(name = "blood_inventory", uniqueConstraints = @UniqueConstraint(columnNames = "bloodGroup"))
public class BloodInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 5)
    private String bloodGroup;
    @Column(nullable = false)
    private int units;

    public Long getId() { return id; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }
}
