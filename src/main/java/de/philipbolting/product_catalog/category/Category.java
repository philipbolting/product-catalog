package de.philipbolting.product_catalog.category;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Objects;

@Entity(name = "Category")
@Table(name = "category")
@EntityListeners(AuditingEntityListener.class)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_category")
    @SequenceGenerator(name = "seq_category", allocationSize = 1)
    private long id;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;
    private int sortOrder;
    private String slug;
    private String name;
    private String description;
    @CreatedDate
    private Instant created;
    @LastModifiedDate
    private Instant lastModified;

    public Category() {
    }

    public Category(String slug, String name, String description) {
        this.sortOrder = 0;
        this.slug = slug;
        this.name = name;
        this.description = description;
    }

    public Category(int sortOrder, String slug, String name, String description) {
        this.sortOrder = sortOrder;
        this.slug = slug;
        this.name = name;
        this.description = description;
    }

    public Category(Category parent, int sortOrder, String slug, String name, String description) {
        this.parent = parent;
        this.sortOrder = sortOrder;
        this.slug = slug;
        this.name = name;
        this.description = description;
    }

    public Category(long id, Category parent, int sortOrder, String slug, String name, String description, Instant created, Instant lastModified) {
        this.id = id;
        this.parent = parent;
        this.sortOrder = sortOrder;
        this.slug = slug;
        this.name = name;
        this.description = description;
        this.created = created;
        this.lastModified = lastModified;
    }

    public static Category of(Category category) {
        return new Category(
                category.id,
                category.parent,
                category.sortOrder,
                category.slug,
                category.name,
                category.description,
                category.created,
                category.lastModified
        );
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parentCategory) {
        this.parent = parentCategory;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
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
        Category category = (Category) o;
        return Objects.equals(slug, category.slug);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(slug);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", slug='" + slug + '\'' +
                '}';
    }
}
