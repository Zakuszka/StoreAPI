package com.store.storeapi.ws.response;

import java.math.BigDecimal;
import com.store.storeapi.pojo.Product;

public class ProductResponse {

	private int id;
	private String name;
	private String description;
	private String category;
	private BigDecimal price;
	private boolean available;
	private int createdBy;
	private Integer updatedBy;

	public ProductResponse() {
		super();
	}

	public ProductResponse(Product product) {
		super();
		this.id = product.getId();
		this.name = product.getName();
		this.description = product.getDescription();
		this.category = product.getCategory();
		this.price = product.getPrice();
		this.available = product.isAvailable();
		this.createdBy = product.getCreatedBy();
		this.updatedBy = product.getUpdatedBy();
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

}
