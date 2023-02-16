package com.store.storeapi.ws.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class PriceResponse {

	@JsonInclude(Include.NON_NULL)
	private Integer id;

	private BigDecimal price;
	private String description;
	private LocalDateTime validFrom;
	private LocalDateTime validUntil;

	@JsonInclude(Include.NON_NULL)
	private Integer createdBy;

	@JsonInclude(Include.NON_NULL)
	private Integer updatedBy;

	public PriceResponse() {
		super();
	}

	public PriceResponse(BigDecimal price, String description, LocalDateTime validFrom, LocalDateTime validUntil) {
		super();
		this.price = price;
		this.description = description;
		this.validFrom = validFrom;
		this.validUntil = validUntil;
	}

	public PriceResponse(Integer id, BigDecimal price, String description, LocalDateTime validFrom, LocalDateTime validUntil, Integer createdBy,
			Integer updatedBy) {
		super();
		this.id = id;
		this.price = price;
		this.description = description;
		this.validFrom = validFrom;
		this.validUntil = validUntil;
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(LocalDateTime validFrom) {
		this.validFrom = validFrom;
	}

	public LocalDateTime getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(LocalDateTime validUntil) {
		this.validUntil = validUntil;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

}
