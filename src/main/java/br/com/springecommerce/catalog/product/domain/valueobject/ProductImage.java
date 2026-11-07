package br.com.springecommerce.catalog.product.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Id;

import java.util.Objects;

@Embeddable
public class ProductImage {

    @Column(name = "public_id", nullable = false)
    private String publicId;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "main", nullable = false)
    private Boolean main;

    protected ProductImage() {}

    public ProductImage(
            String publicId,
            String url,
            Integer position,
            Boolean main
    ) {
        this.publicId = publicId;
        this.url = url;
        this.position = position;
        this.main = main;
    }

    public String getPublicId() {
        return publicId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Boolean getMain() {
        return main;
    }

    public void setMain(Boolean main) {
        this.main = main;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductImage other)) return false;
        return Objects.equals(publicId, other.publicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publicId);
    }
}
