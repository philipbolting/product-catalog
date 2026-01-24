package de.philipbolting.product_catalog.product;

import de.philipbolting.product_catalog.brand.Brand;
import de.philipbolting.product_catalog.category.Category;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Objects;

@Entity(name = "Product")
@Table(name = "product")
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_product")
    @SequenceGenerator(name = "seq_product", allocationSize = 1)
    private long id;
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private String slug;
    private String name;
    private String description;
    @CreatedDate
    private Instant created;
    @LastModifiedDate
    private Instant lastModified;

    public Product() {
    }

    public Product(Brand brand, Category category, String slug, String name, String description) {
        this.brand = brand;
        this.category = category;
        this.slug = slug;
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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

    public Instant getCreated() {
        return created;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(slug, product.slug);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(slug);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", slug='" + slug + '\'' +
                '}';
    }
}
