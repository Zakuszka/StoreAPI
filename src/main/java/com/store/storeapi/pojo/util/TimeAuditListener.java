package com.store.storeapi.pojo.util;

import java.time.LocalDateTime;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class TimeAuditListener {

	@PrePersist
	public void handleCreatedAt(TimeAudit entity) {
		entity.setCreatedAt(LocalDateTime.now());
		entity.setUpdatedAt(LocalDateTime.now());
	}

	@PreUpdate
	public void handleUpdatedAt(TimeAudit entity) {
		entity.setUpdatedAt(LocalDateTime.now());
	}

}
