package com.example.demo.fileupload.cosmos.dto;

public record Details(
		String createdAt,
		String createdBy,
		String lastAccessedAt,
		String lastAccessedBy,
		String[] accessGroups
) {
}
