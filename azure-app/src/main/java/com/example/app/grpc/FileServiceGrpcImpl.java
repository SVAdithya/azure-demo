package com.example.app.grpc;

import com.example.app.service.FileServiceImpl;
import com.example.cosmos.repo.dto.FileMetadata;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
@AllArgsConstructor
public class FileServiceGrpcImpl extends FileServiceGrpc.FileServiceImplBase {

    private final FileServiceImpl fileService;

    @Override
    public void getFileMetadata(GetFileMetadataRequest request, StreamObserver<GetFileMetadataResponse> responseObserver) {
        List<String> metadataList = fileService.getFileMetadata(request.getFileId());
        // Assuming the first element is the file metadata string representation
        String metadataString = metadataList.isEmpty() ? "" : metadataList.get(0);

        GetFileMetadataResponse response = GetFileMetadataResponse.newBuilder()
                .setFileName(metadataString)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
