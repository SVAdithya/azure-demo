package com.example.cosmos.repo.dto;

public record Details(
		String createdAt,
		String createdBy,
		String lastAccessedAt,
		String lastAccessedBy,
		String[] accessGroups
) {
}
