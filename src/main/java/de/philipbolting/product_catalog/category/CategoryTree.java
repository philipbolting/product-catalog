package de.philipbolting.product_catalog.category;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Immutable;

import java.util.Objects;

@Entity
@Immutable
public class CategoryTree {
    @Id
    private Long id;
    private Long parentId;
    private String name;
    private String slug;
    private String sortOrder;
    private int depth;

    public CategoryTree() {
    }

    public CategoryTree(Long id, Long parentId, String name, String slug, String sortOrder, int depth) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.slug = slug;
        this.sortOrder = sortOrder;
        this.depth = depth;
    }

    public Long getId() {
        return id;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CategoryTree that = (CategoryTree) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CategoryTree{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", sortOrder='" + sortOrder + '\'' +
                ", depth=" + depth +
                '}';
    }
}
