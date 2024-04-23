package com.nimbleways.springboilerplate.entities;

import com.nimbleways.springboilerplate.enums.ProductType;
import lombok.*;

import java.time.LocalDate;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "lead_time")
    private Integer leadTime;

    @Column(name = "available")
    private Integer available;

    @Column(name = "type")
    private ProductType type;

    @Column(name = "name")
    private String name;

    @Column(name = "selling_start_date")
    private LocalDate sellingStartDate;

    @Column(name = "selling_end_date")
    private LocalDate sellingEndDate;

    @Column(name = "max_to_sell")
    private Integer maxToSell;


    public LocalDate getPeriodEndDate() {
        return this.sellingEndDate;
    }

    public LocalDate getPeriodStartDate() {
        return this.sellingStartDate;
    }

    public LocalDate getSeasonEndDate() {
        return this.sellingEndDate;
    }

    public LocalDate getSeasonStartDate() {
        return this.sellingStartDate;
    }

    public LocalDate getExpiryDate() {
        return this.sellingEndDate;
    }
}
