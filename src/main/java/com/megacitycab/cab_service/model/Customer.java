package com.megacitycab.cab_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Address is required")
    private String address;
    @NotBlank(message = "NIC is required")
    @Pattern(regexp = "[0-9]{9}[vV]", message = "NIC must be 9 digits followed by 'v' or 'V'")
    private String nic;
    @OneToOne
    private User user;

    public Customer() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}