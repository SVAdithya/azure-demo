package com.example.app.controller.v2;

import com.example.app.grpc.FileServiceGrpc;
import com.example.app.grpc.GetFileMetadataRequest;
import com.example.app.grpc.GetFileMetadataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/v2/files")
@Tag(name = "File API V2", description = "Endpoints for file management using gRPC")
public class FileUploadControllerV2 {

    @GrpcClient("fileService")
    private FileServiceGrpc.FileServiceBlockingStub fileServiceBlockingStub;

    @Operation(summary = "Get file metadata")
    @ApiResponse(responseCode = "200", description = "File metadata retrieved successfully")
    @GetMapping("/{id}/metadata")
    public ResponseEntity<Object> getFileMetadata(
            @Parameter(description = "ID of the file to retrieve metadata for") @PathVariable String id) {
        GetFileMetadataRequest request = GetFileMetadataRequest.newBuilder().setFileId(id).build();
        GetFileMetadataResponse response = fileServiceBlockingStub.getFileMetadata(request);
        return ResponseEntity.ok(response.getFileName());
    }
}
