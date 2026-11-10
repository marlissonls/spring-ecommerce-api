//package br.com.springecommerce.otherdomains.user.domain;
//
//import br.com.springecommerce.otherdomains.iam.domain.user.User;
//import jakarta.persistence.*;
//import java.time.LocalDateTime;
//import java.util.Objects;
//import java.util.UUID;
//
//@Entity
//@Table(name = "addresses")
//public class Address {
//
//    @Id
//    // redundância: @Column(columnDefinition = "CHAR(36)")
//    private UUID id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    private String street;
//    private String number;
//    private String complex;
//    private String block;
//    private String building;
//
//    @Column(name = "apt_number")
//    private String aptNumber;
//
//    private String neighborhood;
//    private String city;
//    private String state;
//
//    @Column(name = "postal_code")
//    private String postalCode;
//    private String country;
//
//    @Column(name = "created_at", updatable = false)
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//
//    @Column(name = "deleted_at")
//    private LocalDateTime deletedAt;
//
//    @PrePersist
//    protected void onCreate() {
//        if (id == null) {
//            id = UUID.randomUUID();
//        }
//        createdAt = LocalDateTime.now();
//        updatedAt = createdAt;
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }
//
//    public Address() {}
//
//    public UUID getId() {
//        return id;
//    }
//
//    public void setId(UUID id) {
//        this.id = id;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//
//    public String getStreet() {
//        return street;
//    }
//
//    public void setStreet(String street) {
//        this.street = street;
//    }
//
//    public String getNumber() {
//        return number;
//    }
//
//    public void setNumber(String number) {
//        this.number = number;
//    }
//
//    public String getComplex() {
//        return complex;
//    }
//
//    public void setComplex(String complex) {
//        this.complex = complex;
//    }
//
//    public String getBlock() {
//        return block;
//    }
//
//    public void setBlock(String block) {
//        this.block = block;
//    }
//
//    public String getBuilding() {
//        return building;
//    }
//
//    public void setBuilding(String building) {
//        this.building = building;
//    }
//
//    public String getAptNumber() {
//        return aptNumber;
//    }
//
//    public void setAptNumber(String aptNumber) {
//        this.aptNumber = aptNumber;
//    }
//
//    public String getNeighborhood() {
//        return neighborhood;
//    }
//
//    public void setNeighborhood(String neighborhood) {
//        this.neighborhood = neighborhood;
//    }
//
//    public String getCity() {
//        return city;
//    }
//
//    public void setCity(String city) {
//        this.city = city;
//    }
//
//    public String getState() {
//        return state;
//    }
//
//    public void setState(String state) {
//        this.state = state;
//    }
//
//    public String getPostalCode() {
//        return postalCode;
//    }
//
//    public void setPostalCode(String postalCode) {
//        this.postalCode = postalCode;
//    }
//
//    public String getCountry() {
//        return country;
//    }
//
//    public void setCountry(String country) {
//        this.country = country;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public LocalDateTime getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(LocalDateTime updatedAt) {
//        this.updatedAt = updatedAt;
//    }
//
//    public LocalDateTime getDeletedAt() {
//        return deletedAt;
//    }
//
//    public void setDeletedAt(LocalDateTime deletedAt) {
//        this.deletedAt = deletedAt;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (o == null || getClass() != o.getClass()) return false;
//        Address address = (Address) o;
//        return Objects.equals(getId(), address.getId());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hashCode(getId());
//    }
//}
